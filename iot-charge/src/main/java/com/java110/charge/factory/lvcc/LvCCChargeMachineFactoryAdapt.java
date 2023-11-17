package com.java110.charge.factory.lvcc;

import com.java110.bean.ResultVo;
import com.java110.charge.factory.IChargeFactoryAdapt;
import com.java110.core.utils.BytesUtil;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.chargeMachine.*;
import com.java110.dto.data.NettyReplyDataDto;
import com.java110.intf.charge.IChargeMachineOrderV1InnerServiceSMO;
import com.java110.intf.charge.IChargeMachinePortV1InnerServiceSMO;
import com.java110.intf.charge.IChargeMachineV1InnerServiceSMO;
import com.java110.intf.charge.INotifyChargeV1InnerServiceSMO;
import com.java110.intf.hal.INotifyNettyDataV1InnerServiceSMO;
import com.java110.po.chargeMachine.ChargeMachinePo;
import com.java110.po.chargeMachineOrder.ChargeMachineOrderPo;
import com.java110.po.chargeMachinePort.ChargeMachinePortPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

@Service("lvCCChargeMachineFactory")
public class LvCCChargeMachineFactoryAdapt implements IChargeFactoryAdapt {
    private static Logger logger = LoggerFactory.getLogger(LvCCChargeMachineFactoryAdapt.class);

    @Autowired
    private INotifyNettyDataV1InnerServiceSMO notifyNettyDataV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMachineV1InnerServiceSMO chargeMachineV1InnerServiceSMOImpl;

    @Autowired
    private INotifyChargeV1InnerServiceSMO notifyChargeV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMachinePortV1InnerServiceSMO chargeMachinePortV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMachineOrderV1InnerServiceSMO chargeMachineOrderV1InnerServiceSMOImpl;

    public static final Map<String, String> resultMap = new HashMap<>();

    static {
        /**
         * 1：计时结束
         * 2：充满结束
         * 3：手动结束
         * 4：功率过大
         * 5：空载结束
         * 6：中途异常拔
         * 掉插座
         */
        resultMap.put("1", "计时结束");
        resultMap.put("2", "充满结束");
        resultMap.put("3", "手动结束");
        resultMap.put("4", "功率过大");
        resultMap.put("5", "空载结束");
        resultMap.put("6", "中途异常拔");
    }

    @Override
    public ResultVo startCharge(ChargeMachineDto chargeMachineDto, ChargeMachinePortDto chargeMachinePortDto, String chargeType, double duration, String orderId) {

        //todo 插座
        int port = Integer.parseInt(chargeMachinePortDto.getPortCode()) - 1;
        String portHex = String.format("%02x", port);

        // todo 最小功率
        String minW = String.format("%04x", 20);

        //todo 最大功率
        String maxW = String.format("%04x", 800);

        //todo 空载等待时间
        String freeWaitTime = String.format("%04x", 60);

        //todo 充满等待时间
        String fullWaitTime = String.format("%04x", 1800);

        //todo 充电时间
        String chargeTime = String.format("%04x", duration * 60);

        //todo 充电订单,必须为20位,orderId 为18为
        String orderNum = BytesUtil.bytesToHex(("HC" + orderId).getBytes());

        //todo 计费方式
        String feeWay = String.format("%02x", 1);
        //todo 开电方式
        String openWay = String.format("%02x", 1);

        //todo 是否充满断电
        String isFullClose = String.format("%02x", 1);

        String data = portHex + minW + maxW + freeWaitTime + fullWaitTime + chargeTime + orderNum + feeWay + openWay + isFullClose;

        String paramOut = LvCCUtil.requestMachineData(LvCCUtil.CMD_START_CHARGE, chargeMachineDto.getMachineCode(), data);

        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(paramOut)));

        return new ResultVo(ResultVo.CODE_OK, "启动成功");
    }

    @Override
    public ResultVo stopCharge(ChargeMachineDto chargeMachineDto, ChargeMachinePortDto chargeMachinePortDto) {
        //todo 插座
        int port = Integer.parseInt(chargeMachinePortDto.getPortCode()) - 1;
        String portHex = String.format("%02x", port);

        String data = portHex;

        String paramOut = LvCCUtil.requestMachineData(LvCCUtil.CMD_START_CHARGE, chargeMachineDto.getMachineCode(), data);

        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(paramOut)));

        return new ResultVo(ResultVo.CODE_OK, "启动成功");
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


        String paramOut = LvCCUtil.requestMachineData(LvCCUtil.CMD_RESTART, chargeMachineDto.getMachineCode(), "");

        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(paramOut)));

        return new ResultVo(ResultVo.CODE_OK, "重启");
    }

    @Override
    public void chargeResult(ChargeMachineDto chargeMachineDto, byte[] data) {

        //todo 解析指令
        String cmd = LvCCUtil.getCmd(data);

        logger.debug("设备 {} ，解析到 指令为 ：{}", chargeMachineDto.getMachineCode(), cmd);

        switch (cmd) {
            case LvCCUtil.CMD_REGISTER: //todo 注册
                machineRegister(chargeMachineDto, data, LvCCUtil.getCmdContext(data));
                break;
            case LvCCUtil.CMD_HEARTBEAT: // todo 结束
                machineHeartbeat(chargeMachineDto, data);
                break;
            case LvCCUtil.CMD_CHARGE_END:// todo 充电结束
                machineChargeEnd(chargeMachineDto, data);
                break;
            case LvCCUtil.CMD_UPLOAD_CHARGE_DATA:// todo 充电过程中上报 功率
                machineUploadChargeData(chargeMachineDto, data);
                break;
            case LvCCUtil.CMD_CHANGE_PORT_STATE:// todo 充电过程中上报 功率
                machineChangePortState(chargeMachineDto, data);
                break;


        }


    }

    /**
     * 插座状态改变上报
     * @param chargeMachineDto
     * @param data
     */
    private void machineChangePortState(ChargeMachineDto chargeMachineDto, byte[] data) {
        String dataHex = LvCCUtil.getCmdContext(data);

        // todo 插座
        int port = Integer.parseInt(dataHex.substring(0, 2), 16) + 1;

        // todo 状态
        int state = Integer.parseInt(dataHex.substring(2, 4), 16) ;

        ChargeMachinePortDto chargeMachinePortDto = new ChargeMachinePortDto();
        chargeMachinePortDto.setMachineId(chargeMachineDto.getMachineId());
        chargeMachinePortDto.setPortCode((port + 1) + "");
        List<ChargeMachinePortDto> chargeMachinePortDtos = chargeMachinePortV1InnerServiceSMOImpl.queryChargeMachinePorts(chargeMachinePortDto);
        if (!ListUtil.isNull(chargeMachinePortDtos)) {
            ChargeMachinePortPo chargeMachinePortPo = new ChargeMachinePortPo();
            chargeMachinePortPo.setPortId(chargeMachinePortDtos.get(0).getPortId());
            chargeMachinePortPo.setState(ChargeMachinePortDto.STATE_FREE);
            if(state == 1){
                chargeMachinePortPo.setState(ChargeMachinePortDto.STATE_WORKING);
            }
            if(state == 2){
                chargeMachinePortPo.setState(ChargeMachinePortDto.STATE_BREAKDOWN);
            }
            chargeMachinePortV1InnerServiceSMOImpl.updateChargeMachinePort(chargeMachinePortPo);
        }

        String paramOut = String.format("%02x", 1);

        paramOut = LvCCUtil.computeResultDate(data, chargeMachineDto.getMachineCode(), paramOut);

        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(paramOut)));
    }

    /**
     * 充电数据上报
     * @param chargeMachineDto
     * @param data
     */
    private void machineUploadChargeData(ChargeMachineDto chargeMachineDto, byte[] data) {

        String param = LvCCUtil.getCmdContext(data);

        //todo 设备当前信号值 丢弃
        param = param.substring(2);


        List<String> portWs = new ArrayList<>();
        String w = "";
        for (int port = 0; port < param.length(); port++) {
            w += param.charAt(port);
            if (w.length() == 4) {
                portWs.add(w);
                w = "";
            }
        }

        int energy = 0;
        for (int port = 0; port < portWs.size(); port++) {

            energy = Integer.parseInt(portWs.get(port), 16);
            if (energy <= 0) {
                continue;
            }

            ChargeMachinePortDto chargeMachinePortDto = new ChargeMachinePortDto();
            chargeMachinePortDto.setMachineId(chargeMachineDto.getMachineId());
            chargeMachinePortDto.setPortCode((port + 1) + "");
            List<ChargeMachinePortDto> chargeMachinePortDtos = chargeMachinePortV1InnerServiceSMOImpl.queryChargeMachinePorts(chargeMachinePortDto);
            if (ListUtil.isNull(chargeMachinePortDtos)) {
                continue;
            }

            ChargeMachineOrderDto chargeMachineOrderDto = new ChargeMachineOrderDto();
            chargeMachineOrderDto.setMachineId(chargeMachineDto.getMachineId());
            chargeMachineOrderDto.setPortId(chargeMachinePortDtos.get(0).getPortId());
            chargeMachineOrderDto.setState(ChargeMachineOrderDto.STATE_DOING);
            List<ChargeMachineOrderDto> chargeMachineOrderDtos = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrders(chargeMachineOrderDto);
            if (ListUtil.isNull(chargeMachineOrderDtos)) {
                continue;
            }

            ChargeMachineOrderPo chargeMachineOrderPo = new ChargeMachineOrderPo();
            chargeMachineOrderPo.setOrderId(chargeMachineOrderDtos.get(0).getOrderId());
            chargeMachineOrderPo.setEnergy(energy + "");
            chargeMachineOrderPo.setCommunityId(chargeMachineOrderDtos.get(0).getCommunityId());
            chargeMachineOrderV1InnerServiceSMOImpl.updateChargeMachineOrder(chargeMachineOrderPo);
        }

        String paramOut = String.format("%02x", 1);

        paramOut = LvCCUtil.computeResultDate(data, chargeMachineDto.getMachineCode(), paramOut);

        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(paramOut)));

    }

    /**
     * 充电结束
     *
     * @param chargeMachineDto
     * @param data
     */
    private void machineChargeEnd(ChargeMachineDto chargeMachineDto, byte[] data) {

        String dataHex = LvCCUtil.getCmdContext(data);

        // todo 结束原因
        int reason = Integer.parseInt(dataHex.substring(0, 2), 16);

        // todo 插座
        int port = Integer.parseInt(dataHex.substring(2, 4), 16) + 1;

        //todo 订单号
        String orderHex = dataHex.substring(4, 44);

        String orderNum = null;
        try {
            orderNum = new String(BytesUtil.hexStringToByteArray(orderHex), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        //todo 结束时功率
        int w = Integer.parseInt(dataHex.substring(48, 100), 16);


        String remark = resultMap.containsKey(reason + "") ? resultMap.get(reason + "") : "未知原因";

        // JSONObject param = JSONObject.parseObject(postInfo);
        NotifyChargeOrderDto notifyChargeOrderDto = new NotifyChargeOrderDto();
        notifyChargeOrderDto.setOrderId(orderNum.substring(2));
        notifyChargeOrderDto.setMachineCode(chargeMachineDto.getMachineCode());
        notifyChargeOrderDto.setPortCode(port + "");
        notifyChargeOrderDto.setBodyParam("");
        notifyChargeOrderDto.setReason(remark);
        notifyChargeOrderDto.setEnergy(w + "");


        notifyChargeV1InnerServiceSMOImpl.finishCharge(notifyChargeOrderDto);

        //
        String paramOut = String.format("%02x", 1);

        paramOut = LvCCUtil.computeResultDate(data, chargeMachineDto.getMachineCode(), paramOut);

        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(paramOut)));

    }

    /**
     * 心跳
     *
     * @param chargeMachineDto
     * @param data
     */
    private void machineHeartbeat(ChargeMachineDto chargeMachineDto, byte[] data) {

        ChargeMachinePo chargeMachinePo = new ChargeMachinePo();
        chargeMachinePo.setHeartbeatTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        chargeMachinePo.setMachineId(chargeMachineDto.getMachineId());
        chargeMachinePo.setMachineCode(chargeMachineDto.getMachineCode());
        chargeMachineV1InnerServiceSMOImpl.updateChargeMachine(chargeMachinePo);

        long curUnit = new Date().getTime() / 1000;

        String paramOut = String.format("%x", curUnit);

        paramOut = LvCCUtil.computeResultDate(data, chargeMachineDto.getMachineCode(), paramOut);

        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(paramOut)));
    }

    /**
     * 设备注册
     *
     * @param chargeMachineDto
     * @param data
     * @param cmdContext
     */
    private void machineRegister(ChargeMachineDto chargeMachineDto, byte[] data, String cmdContext) {

        long curUnit = new Date().getTime() / 1000;

        String paramOut = String.format("%x", curUnit);

        paramOut = LvCCUtil.computeResultDate(data, chargeMachineDto.getMachineCode(), paramOut);

        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(paramOut)));
    }


}
