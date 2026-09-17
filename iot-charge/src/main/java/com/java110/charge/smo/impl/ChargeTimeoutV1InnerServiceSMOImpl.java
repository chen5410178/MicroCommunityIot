package com.java110.charge.smo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.charge.dao.IChargeMachineOrderV1ServiceDao;
import com.java110.charge.factory.IChargeCore;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.chargeMachine.ChargeMachineDto;
import com.java110.dto.chargeMachine.ChargeMachineOrderDto;
import com.java110.dto.chargeMachine.ChargeMachinePortDto;
import com.java110.dto.chargeMachine.NotifyChargeOrderDto;
import com.java110.intf.charge.IChargeMachineOrderV1InnerServiceSMO;
import com.java110.intf.charge.IChargeMachinePortV1InnerServiceSMO;
import com.java110.intf.charge.IChargeTimeoutV1InnerServiceSMO;
import com.java110.po.chargeMachinePort.ChargeMachinePortPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 充电订单超时自动结算与停止
 * <p>
 * 两轮：充电时长（充满自停 999 按 10 小时）+ 10 分钟缓冲后，任务内直接结算再下发停止；
 * 四轮：超过 24 小时，开启中(1000) 直接置空闲并置失败退预扣款，充电中(1001) 只下发停止指令。
 */
@RestController
public class ChargeTimeoutV1InnerServiceSMOImpl implements IChargeTimeoutV1InnerServiceSMO {

    private static final Logger logger = LoggerFactory.getLogger(ChargeTimeoutV1InnerServiceSMOImpl.class);

    private static final int DEFAULT_ROW = 100;

    //两轮待处理订单状态：1001 充电中、4004 结束中
    private static final String[] BIKE_STATES =
            {ChargeMachineOrderDto.STATE_DOING, ChargeMachineOrderDto.STATE_FINISHING};

    //四轮待处理订单状态：1000 开启中、1001 充电中
    private static final String[] CAR_STATES =
            {ChargeMachineOrderDto.STATE_STARTING, ChargeMachineOrderDto.STATE_DOING};

    @Autowired
    private IChargeMachineOrderV1ServiceDao chargeMachineOrderV1ServiceDaoImpl;

    @Autowired
    private IChargeCore chargeCoreImpl;

    @Autowired
    private IChargeMachineOrderV1InnerServiceSMO chargeMachineOrderV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMachinePortV1InnerServiceSMO chargeMachinePortV1InnerServiceSMOImpl;

    @Override
    public int timeoutStopCharge(@RequestBody JSONObject reqJson) {
        int bufferMinute = reqJson.getIntValue("bufferMinute") > 0
                ? reqJson.getIntValue("bufferMinute") : ChargeMachineOrderDto.TIMEOUT_BUFFER_MINUTE;
        int carTimeoutHour = reqJson.getIntValue("carTimeoutHour") > 0
                ? reqJson.getIntValue("carTimeoutHour") : ChargeMachineOrderDto.CAR_TIMEOUT_HOUR;
        int row = reqJson.getIntValue("row") > 0 ? reqJson.getIntValue("row") : DEFAULT_ROW;

        //两轮：当前时间 - 缓冲分钟
        String bufferTime = DateUtil.getAddMinStringA(new Date(), -bufferMinute);
        //四轮：当前时间 - 超时小时
        String carTimeoutTime = DateUtil.getAddHoursStringA(new Date(), -carTimeoutHour);

        Map<String, Object> bikeInfo = new HashMap<>();
        bikeInfo.put("states", BIKE_STATES);
        bikeInfo.put("bufferTime", bufferTime);
        bikeInfo.put("row", row);
        List<ChargeMachineOrderDto> bikeOrders = BeanConvertUtil.covertBeanList(
                chargeMachineOrderV1ServiceDaoImpl.queryTimeoutBikeOrders(bikeInfo), ChargeMachineOrderDto.class);

        Map<String, Object> carInfo = new HashMap<>();
        carInfo.put("states", CAR_STATES);
        carInfo.put("carTimeoutTime", carTimeoutTime);
        carInfo.put("row", row);
        List<ChargeMachineOrderDto> carOrders = BeanConvertUtil.covertBeanList(
                chargeMachineOrderV1ServiceDaoImpl.queryTimeoutCarOrders(carInfo), ChargeMachineOrderDto.class);

        int handled = 0;
        for (ChargeMachineOrderDto bikeOrder : bikeOrders) {
            if (timeoutBikeOrder(bikeOrder)) {
                handled++;
            }
        }
        for (ChargeMachineOrderDto carOrder : carOrders) {
            if (timeoutCarOrder(carOrder)) {
                handled++;
            }
        }

        if (handled > 0) {
            logger.debug("充电订单超时处理完成，本次处理订单数：{}", handled);
        }

        return handled;
    }

    /**
     * 两轮：任务内直接结算（不等设备响应），随后下发停止指令
     *
     * @param chargeMachineOrderDto 超时订单
     * @return true 已处理
     */
    private boolean timeoutBikeOrder(ChargeMachineOrderDto chargeMachineOrderDto) {
        if (queryWaitState(chargeMachineOrderDto, BIKE_STATES) == null) {
            return false;
        }

        try {
            NotifyChargeOrderDto notifyChargeOrderDto = new NotifyChargeOrderDto();
            notifyChargeOrderDto.setOrderId(chargeMachineOrderDto.getOrderId());
            notifyChargeOrderDto.setMachineCode(chargeMachineOrderDto.getMachineCode());
            notifyChargeOrderDto.setPortCode(chargeMachineOrderDto.getPortCode());
            notifyChargeOrderDto.setEnergy(StringUtil.isEmpty(chargeMachineOrderDto.getEnergy()) ? "0" : chargeMachineOrderDto.getEnergy());
            notifyChargeOrderDto.setReason(ChargeMachineOrderDto.TIMEOUT_REASON_BIKE);
            notifyChargeOrderDto.setBodyParam("");

            chargeCoreImpl.finishCharge(notifyChargeOrderDto);

            stopCharge(chargeMachineOrderDto);
            return true;
        } catch (Exception e) {
            logger.error("两轮充电订单超时处理失败, orderId={}", chargeMachineOrderDto.getOrderId(), e);
            return false;
        }
    }

    /**
     * 四轮：开启中(1000) 端口直接置空闲 + 置失败退预扣款；充电中(1001) 只下发停止指令
     *
     * @param chargeMachineOrderDto 超时订单
     * @return true 已处理
     */
    private boolean timeoutCarOrder(ChargeMachineOrderDto chargeMachineOrderDto) {
        String state = queryWaitState(chargeMachineOrderDto, CAR_STATES);
        if (state == null) {
            return false;
        }

        try {
            if (ChargeMachineOrderDto.STATE_STARTING.equals(state)) {
                //开启中：端口直接置空闲
                ChargeMachinePortPo chargeMachinePortPo = new ChargeMachinePortPo();
                chargeMachinePortPo.setPortId(chargeMachineOrderDto.getPortId());
                chargeMachinePortPo.setState(ChargeMachinePortDto.STATE_FREE);
                chargeMachinePortV1InnerServiceSMOImpl.updateChargeMachinePort(chargeMachinePortPo);

                //订单置失败并退回预扣款，不做结算
                NotifyChargeOrderDto notifyChargeOrderDto = new NotifyChargeOrderDto();
                notifyChargeOrderDto.setOrderId(chargeMachineOrderDto.getOrderId());
                notifyChargeOrderDto.setMachineCode(chargeMachineOrderDto.getMachineCode());
                notifyChargeOrderDto.setPortCode(chargeMachineOrderDto.getPortCode());
                notifyChargeOrderDto.setReason(ChargeMachineOrderDto.TIMEOUT_REASON_CAR_STARTING);

                chargeCoreImpl.startChargeFail(notifyChargeOrderDto);
            }

            //充电中(1001) 只做这一步，不结算、不改端口状态
            stopCharge(chargeMachineOrderDto);
            return true;
        } catch (Exception e) {
            logger.error("四轮充电订单超时处理失败, orderId={}", chargeMachineOrderDto.getOrderId(), e);
            return false;
        }
    }

    /**
     * 校验订单是否还需要处理：充电桩/插槽编码完整，且复核后的订单状态仍是待处理状态
     *
     * @param chargeMachineOrderDto 超时订单
     * @param states                待处理订单状态
     * @return 复核后的订单状态，无需处理时返回 null
     */
    private String queryWaitState(ChargeMachineOrderDto chargeMachineOrderDto, String[] states) {
        if (StringUtil.isEmpty(chargeMachineOrderDto.getMachineCode())
                || StringUtil.isEmpty(chargeMachineOrderDto.getPortCode())) {
            logger.warn("充电桩编码或插槽编码为空，跳过超时处理, orderId={}", chargeMachineOrderDto.getOrderId());
            return null;
        }

        //复核订单状态，避免与设备上报并发导致重复结算
        ChargeMachineOrderDto chargeMachineOrderDtoQuery = new ChargeMachineOrderDto();
        chargeMachineOrderDtoQuery.setOrderId(chargeMachineOrderDto.getOrderId());
        List<ChargeMachineOrderDto> chargeMachineOrderDtos
                = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrders(chargeMachineOrderDtoQuery);
        if (ListUtil.isNull(chargeMachineOrderDtos) || !containState(states, chargeMachineOrderDtos.get(0).getState())) {
            logger.debug("订单状态已变化，跳过超时处理, orderId={}", chargeMachineOrderDto.getOrderId());
            return null;
        }

        return chargeMachineOrderDtos.get(0).getState();
    }

    /**
     * 下发停止指令，失败只记录日志（结算/置失败已完成，设备后到的报文有幂等校验）
     *
     * @param chargeMachineOrderDto 超时订单
     */
    private void stopCharge(ChargeMachineOrderDto chargeMachineOrderDto) {
        ChargeMachineDto chargeMachineDto = new ChargeMachineDto();
        chargeMachineDto.setMachineId(chargeMachineOrderDto.getMachineId());
        chargeMachineDto.setMachineCode(chargeMachineOrderDto.getMachineCode());
        chargeMachineDto.setImplBean(chargeMachineOrderDto.getImplBean());
        chargeMachineDto.setRuleId(chargeMachineOrderDto.getRuleId());
        chargeMachineDto.setCommunityId(chargeMachineOrderDto.getCommunityId());

        ChargeMachinePortDto chargeMachinePortDto = new ChargeMachinePortDto();
        chargeMachinePortDto.setPortId(chargeMachineOrderDto.getPortId());
        chargeMachinePortDto.setPortCode(chargeMachineOrderDto.getPortCode());
        chargeMachinePortDto.setState(chargeMachineOrderDto.getPortState());
        chargeMachinePortDto.setMachineId(chargeMachineOrderDto.getMachineId());
        chargeMachinePortDto.setCommunityId(chargeMachineOrderDto.getCommunityId());

        try {
            chargeCoreImpl.stopCharge(chargeMachineDto, chargeMachinePortDto);
        } catch (Exception e) {
            logger.error("下发停止指令失败, orderId={}", chargeMachineOrderDto.getOrderId(), e);
        }
    }

    private boolean containState(String[] states, String state) {
        for (String tmpState : states) {
            if (tmpState.equals(state)) {
                return true;
            }
        }
        return false;
    }
}
