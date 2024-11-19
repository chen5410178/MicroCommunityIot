package com.java110.charge.factory.yuncarcharge;

import com.java110.bean.ResultVo;
import com.java110.charge.factory.IChargeFactoryAdapt;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.*;
import com.java110.dto.chargeMachine.*;
import com.java110.dto.chargePeakAlleyPrice.ChargePeakAlleyPriceDto;
import com.java110.dto.chargeRulePrice.ChargeRulePriceDto;
import com.java110.dto.data.NettyReplyDataDto;
import com.java110.intf.charge.*;
import com.java110.intf.hal.INotifyNettyDataV1InnerServiceSMO;
import com.java110.po.chargeMachine.ChargeMachinePo;
import com.java110.po.chargeMachineOrder.ChargeMachineOrderPo;
import com.java110.po.chargeMachinePort.ChargeMachinePortPo;
import com.java110.po.chargeProcessInfo.ChargeProcessInfoPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service("yunCarChargeMachineFactory")
public class YunCarChargeMachineFactoryAdapt implements IChargeFactoryAdapt {
    private static Logger logger = LoggerFactory.getLogger(YunCarChargeMachineFactoryAdapt.class);

    @Autowired
    private INotifyNettyDataV1InnerServiceSMO notifyNettyDataV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMachineV1InnerServiceSMO chargeMachineV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMachinePortV1InnerServiceSMO chargeMachinePortV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMachineOrderV1InnerServiceSMO chargeMachineOrderV1InnerServiceSMOImpl;

    @Autowired
    private IChargePeakAlleyPriceV1InnerServiceSMO chargePeakAlleyPriceV1InnerServiceSMOImpl;

    @Autowired
    private IChargeRulePriceV1InnerServiceSMO chargeRulePriceV1InnerServiceSMOImpl;

    @Autowired
    private IChargeProcessInfoV1InnerServiceSMO chargeProcessInfoV1InnerServiceSMOImpl;

    @Autowired
    private INotifyChargeV1InnerServiceSMO notifyChargeV1InnerServiceSMOImpl;

    public static final Map<String, String> resultMap = new HashMap<>();

    static {
        resultMap.put("00", "无");
        resultMap.put("01", "设备编号不匹配");
        resultMap.put("02", "枪已在充电");
        resultMap.put("03", "设备故障");
        resultMap.put("04", "设备离线");
        resultMap.put("05", "未插枪");
    }

    @Override
    public ResultVo startCharge(ChargeMachineDto chargeMachineDto, ChargeMachinePortDto chargeMachinePortDto, String chargeType, double duration, String orderId) {
        int port = Integer.parseInt(chargeMachinePortDto.getPortCode());
        String portHex = String.format("%02x", port);
        String data = YunCarUtil.startCharge("0000", chargeMachineDto.getMachineCode(), orderId, portHex, "0000000000000000", "0000000000000000", duration + "");
        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(data)));
        return new ResultVo(ResultVo.CODE_OK, "启动信号已发送");
    }

    @Override
    public ResultVo stopCharge(ChargeMachineDto chargeMachineDto, ChargeMachinePortDto chargeMachinePortDto) {
        int port = Integer.parseInt(chargeMachinePortDto.getPortCode());
        String portHex = String.format("%02x", port);
        String data = YunCarUtil.stopCharge("0003", chargeMachineDto.getMachineCode(), portHex);
        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(data)));
        return new ResultVo(ResultVo.CODE_OK, "结束信号已发送");
    }

    @Override
    public ChargeMachinePortDto getChargePortState(ChargeMachineDto chargeMachineDto, ChargeMachinePortDto chargeMachinePortDto) {
        return chargeMachinePortDto;
    }

    @Override
    public List<NotifyChargePortDto> getChargeHeartBeatParam(NotifyChargeOrderDto notifyChargeOrderDto) {
        return null;
    }

    @Override
    public void queryChargeMachineState(ChargeMachineDto chargeMachineDto) {

        String heartbeatTime = chargeMachineDto.getHeartbeatTime();
        try {
            if (StringUtil.isEmpty(heartbeatTime)) {
                chargeMachineDto.setStateName("设备离线");
                chargeMachineDto.setState("OFFLINE");
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(DateUtil.getDateFromString(heartbeatTime, DateUtil.DATE_FORMATE_STRING_A));
                calendar.add(Calendar.MINUTE, 2);
                if (calendar.getTime().getTime() <= DateUtil.getCurrentDate().getTime()) {
                    chargeMachineDto.setStateName("设备离线");
                    chargeMachineDto.setState("OFFLINE");
                } else {
                    chargeMachineDto.setStateName("设备在线");
                    chargeMachineDto.setState("ONLINE");
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            chargeMachineDto.setStateName("设备离线");
            chargeMachineDto.setState("OFFLINE");

        }

    }

    @Override
    public void workHeartbeat(ChargeMachineDto chargeMachineDto, String bodyParam) {

    }

    @Override
    public ResultVo restartMachine(ChargeMachineDto chargeMachineDto) {
        String paramOut = YunCarUtil.restartMachine(chargeMachineDto.getMachineCode());
        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(paramOut)));
        return new ResultVo(ResultVo.CODE_OK, "充电桩重启成功");
    }

    @Override
    public void chargeResult(ChargeMachineDto chargeMachineDto, byte[] data) {
        //todo 解析指令
        String cmd = YunCarUtil.getCmd(data).toUpperCase();
        String serNo = YunCarUtil.getSerialNumber(data);
        logger.debug("设备 {} ，解析到 指令为 ：{}", chargeMachineDto.getMachineCode(), cmd);
        ChargeMachineDto tmpChargeMachineDto = new ChargeMachineDto();
        tmpChargeMachineDto.setMachineId(chargeMachineDto.getMachineId());
        tmpChargeMachineDto.setMachineCode(chargeMachineDto.getMachineCode());
        List<ChargeMachineDto> chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(tmpChargeMachineDto);
        if (ListUtil.isNull(chargeMachineDtos)) {
            throw new CmdException("设备不存在");
        }
        switch (cmd) {
            case YunCarUtil.CMD_REGISTER:
                machineRegisterRespon(chargeMachineDto);
                break;
            case YunCarUtil.CMD_HEARTBEAT:
                String gunNo = YunCarUtil.getGunNumber(data);
                String gunState = YunCarUtil.getGunState(data); //todo 枪状态异常需要实现下
                machineHeartbeatRespon(chargeMachineDto, serNo, gunNo);
                break;
            case YunCarUtil.CMD_BILLING_MODEL_VALIDATION:
                String billModeNo = YunCarUtil.getBillingmodelNo(data);
                machineBillingmodelRespon(chargeMachineDto, serNo, billModeNo);
                break;
            case YunCarUtil.CMD_BILLING_MODEL_REQUEST:
                machineBillingmodelRequestRespon(chargeMachineDto, serNo);
                break;
            case YunCarUtil.CMD_REAL_MONITORING_DATA://上传实时监测数据
                String tradeNo = YunCarUtil.getTradeNo(data);
                doRealMonitoringData(chargeMachineDto, serNo, tradeNo, data);
                break;
            case YunCarUtil.CMD_START_CHARGING_COMMAND_REPLY://远程启动充电命令回复
                startChargingCommandReply(chargeMachineDto, data);
                break;
            case YunCarUtil.CMD_CHARGING_HANDSHAKE://充电握手协议
                chargingHandshake(chargeMachineDto, data);
                break;
            case YunCarUtil.CMD_CHARGING_PROCESS_INFORMATION://充电过程BMS信息
                chargingHandshake(chargeMachineDto, data);
                break;
            case YunCarUtil.CMD_STOP_CHARGE_REPLY:
                stopChargeReply(chargeMachineDto, data);
                break;
            case YunCarUtil.CMD_STOP_CHARGE_UPLOAD: // todo 充电结束上报
                stopChargeBMS(chargeMachineDto, data);
                break;
            case YunCarUtil.CMD_TRANSACTION_RECORD_UPLOAD: // todo 结算订单上报
                settlementOrder(chargeMachineDto, serNo, data);
                break;
        }
    }

    /**
     * 充电结束后，订单结算
     * @param chargeMachineDto
     * @param serNo
     * @param data
     */
    //    68（起始标志）A2（数据长度）8001（序列号域）00（加密标志）3B（类型）    12
//    55031412782305012018061910262392（交易流水号）55031412782305（桩编码）01（枪号：1        32+14+2
//    枪）98B70E11100314（开始时间：2020-03-16 17:14:47）98B70E11100314（结束时间：2020-03-16   14+14
//            17:14:47）D0FB0100（尖单价:1.30000）00000000（尖电量：0）00000000（计损尖电量：0）  8+8+8
//            00000000（尖金额：0）D0FB0100（峰单价: 1.30000）00000000（峰电量：0）00000000（计损  8+8+8+8
//    峰电量：0）00000000（峰金额：0）D0FB0100（平单价: 1.30000）00000000（平电量：0）00000000      8+8+8+8
//            （计损平电量：0）00000000（平金额：0）D0FB0100（谷单价: 1.30000）00000000（谷电量：     8+8+8
//            0）00000000（计损谷电量：0）00000000（谷金额：0）0000000000（电表总起值：0）0000000000   8+8+10+10
//            （电表总止值：0）00000000（总电量：0）00000000（计损总电量：0）00000000（消费金额：       8+8+8
//            0）0000000000000000000000000000000000（VIN码）02（交易标识：app启动）98B70E11100314
//（交易时间：2020-03-16 17:14:47）00（停止原因：无）00000000D14B0A54（物理卡号：D14B0A54）
//            388C（帧校验域）
    private void settlementOrder(ChargeMachineDto chargeMachineDto, String serNo, byte[] data) {
        recordedData(chargeMachineDto, data);
        String tradeNo = YunCarUtil.getTradeNo(data);
        String amount = YunCarUtil.priceHexToPrice(YunCarUtil.getAmount(data), 4);
        String energy = YunCarUtil.priceHexToPrice(YunCarUtil.getTotalEnergy(data), 4);
        String endTime = YunCarUtil.getChargeEndTime(data);
        String gunNo = YunCarUtil.getGunNumber(data);

        ChargeMachineOrderDto chargeMachineOrderDto = new ChargeMachineOrderDto();
        chargeMachineOrderDto.setOrderId(tradeNo);
        chargeMachineOrderDto.setCommunityId(chargeMachineDto.getCommunityId());
        chargeMachineOrderDto.setMachineId(chargeMachineDto.getMachineId());
        List<ChargeMachineOrderDto> chargeMachineOrderDtos = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrders(chargeMachineOrderDto);
        if (ListUtil.isNull(chargeMachineOrderDtos)) {
            return;
        }
        NotifyChargeOrderDto notifyChargeOrderDto = new NotifyChargeOrderDto();
        notifyChargeOrderDto.setOrderId(tradeNo);
        notifyChargeOrderDto.setMachineCode(chargeMachineDto.getMachineCode());
        notifyChargeOrderDto.setPortCode(Integer.parseInt(gunNo, 16) + "");
        notifyChargeOrderDto.setBodyParam("");
        notifyChargeOrderDto.setReason("结束充电");
        notifyChargeOrderDto.setAmount(amount);
        notifyChargeOrderDto.setEnergy(energy);
        notifyChargeV1InnerServiceSMOImpl.finishCharge(notifyChargeOrderDto);
        //todo 回复订单计算情况
        String paramOut = YunCarUtil.confirmTransactionRespon(serNo, tradeNo);
        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(paramOut)));
    }


    private void stopChargeBMS(ChargeMachineDto chargeMachineDto, byte[] data) {
        recordedData(chargeMachineDto, data);
    }

    /**
     * 远程停机命令回复
     *
     * @param chargeMachineDto
     * @param data
     */

//    68（起始标志）0E（数据长度）0003（序列号域）00（加密标志）35（类型）32010200000001
//            （桩编码）01（枪号：1枪）01（停止结果：成功）00（失败原因：00）907E（帧校验域）
    private void stopChargeReply(ChargeMachineDto chargeMachineDto, byte[] data) {
        recordedData(chargeMachineDto, data);
    }


    //TODO 充电握手数据上报
    private void chargingHandshake(ChargeMachineDto chargeMachineDto, byte[] data) {
        recordedData(chargeMachineDto, data);
    }

    //记录上报数据
    private void recordedData(ChargeMachineDto chargeMachineDto, byte[] data) {
        String cmd = YunCarUtil.getCmd(data);

        ChargeProcessInfoPo chargeProcessInfoPo = new ChargeProcessInfoPo();
        chargeProcessInfoPo.setCpiId(GenerateCodeFactory.getGeneratorId("68"));
        chargeProcessInfoPo.setCommunityId(chargeMachineDto.getCommunityId());
        chargeProcessInfoPo.setContent(BytesUtil.bytesToHex(data));
        chargeProcessInfoPo.setType("11");
        chargeProcessInfoPo.setFrameType(cmd);

        chargeProcessInfoV1InnerServiceSMOImpl.saveChargeProcessInfo(chargeProcessInfoPo);
    }


    /**
     * 远程启动充电命令回复
     *
     * @param chargeMachineDto
     * @param data
     */
//    68（起始标志）1E（数据长度）0002（序列号域）00（加密标志）33（类型）
//            32010200000000111511161555350260（交易流水号）32010200000001（桩编码）01（枪号：1
//    枪）01（启动结果：成功）00（失败原因：无）0FE2（帧校验域）
    private void startChargingCommandReply(ChargeMachineDto chargeMachineDto, byte[] data) {
        String serNo = YunCarUtil.getSerialNumber(data);
        String tradeNo = YunCarUtil.getTradeNo(data);
        String gunNo = YunCarUtil.getGunNumber(data);
        String state = YunCarUtil.getStartChargeState(data);
        String stateReason = YunCarUtil.getStartChargeStateReason(data);
        ChargeMachineOrderDto chargeMachineOrderDto = new ChargeMachineOrderDto();
        chargeMachineOrderDto.setOrderId(tradeNo);
        chargeMachineOrderDto.setCommunityId(chargeMachineDto.getCommunityId());
        chargeMachineOrderDto.setMachineId(chargeMachineDto.getMachineId());
        List<ChargeMachineOrderDto> chargeMachineOrderDtos = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrders(chargeMachineOrderDto);
        if (ListUtil.isNull(chargeMachineOrderDtos)) {
            throw new CmdException("该汽车充电订单不存在");
        }
        //0x00失败 0x01成功
        if ("00".equals(state)) {//失败了，关闭订单，提示消息
            NotifyChargeOrderDto notifyChargeOrderDto = new NotifyChargeOrderDto();
            notifyChargeOrderDto.setOrderId(tradeNo);
            notifyChargeOrderDto.setMachineCode(chargeMachineDto.getMachineCode());
            notifyChargeOrderDto.setPortCode(Integer.parseInt(gunNo, 16) + "");
            notifyChargeOrderDto.setBodyParam("");
            notifyChargeOrderDto.setReason(resultMap.get(stateReason));
            notifyChargeOrderDto.setAmount("0");
            notifyChargeOrderDto.setEnergy("0");
            notifyChargeV1InnerServiceSMOImpl.startChargeFail(notifyChargeOrderDto);
            //throw new CmdException(resultMap.get(stateReason));
            return;
        }
        if (!"01".equals(state)) {
            return;
        }
        //todo 开启充电成功
        NotifyChargeOrderDto notifyChargeOrderDto = new NotifyChargeOrderDto();
        notifyChargeOrderDto.setOrderId(tradeNo);
        notifyChargeOrderDto.setMachineCode(chargeMachineDto.getMachineCode());
        notifyChargeOrderDto.setPortCode(Integer.parseInt(gunNo, 16) + "");
        notifyChargeOrderDto.setBodyParam("");
        notifyChargeOrderDto.setReason(resultMap.get(stateReason));
        notifyChargeOrderDto.setAmount("0");
        notifyChargeOrderDto.setEnergy("0");
        notifyChargeV1InnerServiceSMOImpl.startChargeSuccess(notifyChargeOrderDto);
        recordedData(chargeMachineDto, data);
    }

    /**
     * 计费模型请求
     *
     * @param chargeMachineDto
     * @param serNo
     */
    private void doRealMonitoringData(ChargeMachineDto chargeMachineDto, String serNo, String tradeNo, byte[] data) {
        // todo 上传实时监测数据
        recordedData(chargeMachineDto, data);
    }

    /**
     * 计费模型请求相应
     *
     * @param chargeMachineDto
     * @param serNo
     */

    private void machineBillingmodelRequestRespon(ChargeMachineDto chargeMachineDto, String serNo) {
        ChargeMachinePo chargeMachinePo = new ChargeMachinePo();
        chargeMachinePo.setHeartbeatTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        chargeMachinePo.setMachineId(chargeMachineDto.getMachineId());
        chargeMachinePo.setMachineCode(chargeMachineDto.getMachineCode());
        chargeMachineV1InnerServiceSMOImpl.updateChargeMachine(chargeMachinePo);

        ChargeRulePriceDto chargeRulePriceDto = new ChargeRulePriceDto();
        chargeRulePriceDto.setRuleId(chargeMachineDto.getRuleId());
        List<ChargeRulePriceDto> chargeRulePriceDtos = chargeRulePriceV1InnerServiceSMOImpl.queryChargeRulePrices(chargeRulePriceDto);
        ChargePeakAlleyPriceDto chargePeakAlleyPriceDto = new ChargePeakAlleyPriceDto();
        chargePeakAlleyPriceDto.setRuleId(chargeMachineDto.getRuleId());
        List<ChargePeakAlleyPriceDto> chargePeakAlleyPriceDtos = chargePeakAlleyPriceV1InnerServiceSMOImpl.queryChargePeakAlleyPrices(chargePeakAlleyPriceDto);

        String paramOut = YunCarUtil.machineBillingmodelRequestRespon(serNo, chargeMachineDto.getMachineCode(), chargeRulePriceDtos, chargePeakAlleyPriceDtos);
        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(paramOut)));
    }

    /**
     * 计费模型校验应答
     *
     * @param chargeMachineDto
     * @param serNo
     */
    private void machineBillingmodelRespon(ChargeMachineDto chargeMachineDto, String serNo, String billModeNo) {
        ChargeMachinePo chargeMachinePo = new ChargeMachinePo();
        chargeMachinePo.setHeartbeatTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        chargeMachinePo.setMachineId(chargeMachineDto.getMachineId());
        chargeMachinePo.setMachineCode(chargeMachineDto.getMachineCode());
        chargeMachineV1InnerServiceSMOImpl.updateChargeMachine(chargeMachinePo);
        String paramOut = YunCarUtil.machineBillingmodelRespon(serNo, chargeMachineDto.getMachineCode(), billModeNo);
        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(paramOut)));
    }


    /**
     * 心跳
     *
     * @param chargeMachineDto
     * @param serNo
     */
    private void machineHeartbeatRespon(ChargeMachineDto chargeMachineDto, String serNo, String gunNo) {
        ChargeMachinePo chargeMachinePo = new ChargeMachinePo();
        chargeMachinePo.setHeartbeatTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        chargeMachinePo.setMachineId(chargeMachineDto.getMachineId());
        chargeMachinePo.setMachineCode(chargeMachineDto.getMachineCode());
        chargeMachineV1InnerServiceSMOImpl.updateChargeMachine(chargeMachinePo);
        String paramOut = YunCarUtil.machineHeartbeatRespon(serNo, chargeMachineDto.getMachineCode(), gunNo);
        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(paramOut)));
    }

    /**
     * 设备注册响应
     *
     * @param chargeMachineDto
     */
    private void machineRegisterRespon(ChargeMachineDto chargeMachineDto) {
        String paramOut = YunCarUtil.machineRegisterRespon(chargeMachineDto.getMachineCode());
        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(paramOut)));
    }


}
