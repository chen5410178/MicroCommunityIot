package com.java110.charge.factory.sbcharge;

import com.java110.bean.ResultVo;
import com.java110.charge.factory.IChargeFactoryAdapt;
import com.java110.charge.factory.lvcc.LvCCUtil;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.BytesUtil;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.chargeMachine.*;
import com.java110.dto.data.NettyReplyDataDto;
import com.java110.intf.charge.*;
import com.java110.intf.hal.INotifyNettyDataV1InnerServiceSMO;
import com.java110.po.chargeMachine.ChargeMachinePo;
import com.java110.po.chargeMachineOrder.ChargeMachineOrderPo;
import com.java110.po.chargeMachinePort.ChargeMachinePortPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

@Service("songbingChargeMachineFactory")
public class SongbingChargeMachineFactoryAdapt implements IChargeFactoryAdapt {
    private static Logger logger = LoggerFactory.getLogger(SongbingChargeMachineFactoryAdapt.class);

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

    @Autowired
    private IChargeRuleFeeV1InnerServiceSMO chargeRuleFeeV1InnerServiceSMOImpl;

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
        if (chargeType.equals("hours")) {
            chargeType = "1";
        } else {
            chargeType = "0";
        }
        int maxEnergy = getMaxEnery(chargeMachineDto);
        // 插座
        int port = Integer.parseInt(chargeMachinePortDto.getPortCode()) - 1;
        String portHex = String.format("%X", port);
        // 充电时间 分钟
        String chargeTime = String.format("%04X", (int) Math.round(duration * 60));
        if (!chargeType.equals("1")) {//智能模式与免费模式
            chargeTime = String.format("%04X",600);
        }
        chargeType = String.format("%02x", Integer.parseInt(chargeType));
        // todo 最小功率
        String minW = String.format("%04x", 20);
        //todo 最大功率
        String maxW = String.format("%04x", maxEnergy);
        //todo 空载等待时间
        String freeWaitTime = String.format("%04x", 60);
        //todo 充满等待时间
        String fullWaitTime = String.format("%04x", 1800);
        //todo 充电订单,必须为20位,orderId 为18为
        String orderNum = BytesUtil.bytesToHex(("HC" + orderId).getBytes());
        //todo 计费方式
        String feeWay = String.format("%02x", 1);
        //todo 是否充满断电
        String isFullClose = String.format("%02x", 1);

        //  String data = portHex + minW + maxW + freeWaitTime + fullWaitTime + chargeTime + orderNum + feeWay + openWay + isFullClose;

        //  String paramOut = SongbingChargeUtil.requestMachineData(SongbingChargeUtil.CMD_START_CHARGE, chargeMachineDto.getMachineCode(), data);

        String paramOut = SongbingChargeUtil.startCharge(chargeMachineDto.getMachineCode(), portHex, chargeType, chargeTime);
        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(paramOut)));
        return new ResultVo(ResultVo.CODE_OK, "请求充电成功，等待设备成功相应。");
    }

    /**
     * 计算最大功率
     *
     * @param chargeMachineDto
     * @return
     */
    private int getMaxEnery(ChargeMachineDto chargeMachineDto) {

        int maxW = 800;
        if (StringUtil.isEmpty(chargeMachineDto.getRuleId())) {
            return maxW;
        }

        ChargeRuleFeeDto chargeRuleFeeDto = new ChargeRuleFeeDto();
        chargeRuleFeeDto.setRuleId(chargeMachineDto.getRuleId());
        List<ChargeRuleFeeDto> chargeRuleFeeDtos = chargeRuleFeeV1InnerServiceSMOImpl.queryChargeRuleFees(chargeRuleFeeDto);
        if (ListUtil.isNull(chargeRuleFeeDtos)) {
            return maxW;
        }

        maxW = (int) Double.parseDouble(chargeRuleFeeDtos.get(0).getMaxEnergyPrice());
        int tmpMaxW = 0;
        for (ChargeRuleFeeDto tmpChargeRuleFeeDto : chargeRuleFeeDtos) {
            tmpMaxW = (int) Double.parseDouble(tmpChargeRuleFeeDto.getMaxEnergyPrice());
            if (tmpMaxW > maxW) {
                maxW = tmpMaxW;
            }
        }
        //todo 极端情况
        if (tmpMaxW <= 20) {
            maxW = 800;
        }

        return maxW;
    }

    @Override
    public ResultVo stopCharge(ChargeMachineDto chargeMachineDto, ChargeMachinePortDto chargeMachinePortDto) {
        //插座
        int port = Integer.parseInt(chargeMachinePortDto.getPortCode()) - 1;
        String portHex = String.format("%X", port);

        String paramOut = SongbingChargeUtil.stopCharge(chargeMachineDto.getMachineCode(), portHex);

        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(paramOut)));

        return new ResultVo(ResultVo.CODE_OK, "停止充电请求成功，等待设备响应。");
    }

    /**
     * 远程启动充电命令回复
     *
     * @param chargeMachineDto
     * @param decodedMap
     */
    private void startChargeResponse(ChargeMachineDto chargeMachineDto, Map<String, String> decodedMap) {
        //  插座
        int port = Integer.parseInt(decodedMap.get("key6").substring(6, 7), 16) + 1;
        //  状态
        int state = Integer.parseInt(decodedMap.get("key5").substring(6, 9), 16);
        ChargeMachinePortDto chargeMachinePortDto = new ChargeMachinePortDto();
        chargeMachinePortDto.setMachineId(chargeMachineDto.getMachineId());
        chargeMachinePortDto.setPortCode(port + "");
        List<ChargeMachinePortDto> chargeMachinePortDtos = chargeMachinePortV1InnerServiceSMOImpl.queryChargeMachinePorts(chargeMachinePortDto);
        if (!ListUtil.isNull(chargeMachinePortDtos)) {
            ChargeMachinePortPo chargeMachinePortPo = new ChargeMachinePortPo();
            chargeMachinePortPo.setPortId(chargeMachinePortDtos.get(0).getPortId());
            if (state == 0) {//0:无错误，非0:错误
                chargeMachinePortPo.setState(ChargeMachinePortDto.STATE_WORKING);
            } else {
                chargeMachinePortPo.setState(ChargeMachinePortDto.STATE_FREE);
            }
            chargeMachinePortV1InnerServiceSMOImpl.updateChargeMachinePort(chargeMachinePortPo);
        }
    }

    /**
     * 远程结束动充电命令回复
     *
     * @param chargeMachineDto
     * @param decodedMap
     */
    private void stopChargeResponse(ChargeMachineDto chargeMachineDto, Map<String, String> decodedMap) {
        //插座
        String portHex = decodedMap.get("key6").substring(1, 2);
        int port = Integer.parseInt(portHex, 16) + 1;

        ChargeMachinePortDto chargeMachinePortDto = new ChargeMachinePortDto();
        chargeMachinePortDto.setMachineId(chargeMachineDto.getMachineId());
        chargeMachinePortDto.setPortCode(port + "");
        List<ChargeMachinePortDto> chargeMachinePortDtos = chargeMachinePortV1InnerServiceSMOImpl.queryChargeMachinePorts(chargeMachinePortDto);
        if (ListUtil.isNull(chargeMachinePortDtos)) {
            new ResultVo(ResultVo.CODE_OK, port + "充电端口异常。");
        }
        ChargeMachineOrderDto chargeMachineOrderDto = new ChargeMachineOrderDto();
        chargeMachineOrderDto.setPortId(chargeMachinePortDtos.get(0).getPortId());
        chargeMachineOrderDto.setCommunityId(chargeMachinePortDto.getCommunityId());
        chargeMachineOrderDto.setMachineId(chargeMachinePortDto.getMachineId());
        List<ChargeMachineOrderDto> chargeMachineOrderDtos = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrders(chargeMachineOrderDto);
        if (ListUtil.isNull(chargeMachineOrderDtos)) {
            new ResultVo(ResultVo.CODE_OK, "停止成功");
        }
        //todo 设备不上传结束的指令上来 所以需要 自己手工结束下
        NotifyChargeOrderDto notifyChargeOrderDto = new NotifyChargeOrderDto();
        notifyChargeOrderDto.setOrderId(chargeMachineOrderDtos.get(0).getOrderId());
        notifyChargeOrderDto.setMachineCode(chargeMachineDto.getMachineCode());
        notifyChargeOrderDto.setPortCode(chargeMachinePortDto.getPortCode());
        notifyChargeOrderDto.setBodyParam("");
        notifyChargeOrderDto.setReason("停止成功");
        Date startTime = DateUtil.getDateFromStringA(chargeMachineOrderDtos.get(0).getStartTime());//开始时间
        Date endTime = new Date();//本次结束时间
        double differenceInMillis = (endTime.getTime() - startTime.getTime()) / ( 60 * 1000.00);
        double lastPower = Double.parseDouble(chargeMachineOrderDtos.get(0).getEnergy())/differenceInMillis;
        notifyChargeOrderDto.setEnergy(lastPower+ "");
        notifyChargeV1InnerServiceSMOImpl.finishCharge(notifyChargeOrderDto);
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
        String paramOut = SongbingChargeUtil.restartMachine(chargeMachineDto.getMachineCode());
        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(paramOut)));
        return new ResultVo(ResultVo.CODE_OK, "重启");
    }

    @Override
    public void chargeResult(ChargeMachineDto chargeMachineDto, byte[] data) {
        //todo 解析指令
        Map<String, String> decodedMap = SongbingChargeUtil.getDataItems(data);
        if (decodedMap.size() < 5) {
            throw new CmdException("收到充电设备消息：" + chargeMachineDto.getMachineCode() + "指令异常");
        }
        String machineCode = decodedMap.get("key2").substring(3);
        String cmd = decodedMap.get("key4").substring(3);
        logger.debug("设备 {} ，解析到 指令为 ：{}", chargeMachineDto.getMachineCode(), cmd);
        if (!machineCode.contains(chargeMachineDto.getMachineCode())) {
            throw new CmdException("收到充电设备消息：" + chargeMachineDto.getMachineCode() + "--" + "设备编码与系统编码不包含" + machineCode);
        }
        //todo 查询设备是否在线
        ChargeMachineDto tmpChargeMachineDto = new ChargeMachineDto();
        tmpChargeMachineDto.setMachineId(chargeMachineDto.getMachineId());
        tmpChargeMachineDto.setMachineCode(chargeMachineDto.getMachineCode());
        List<ChargeMachineDto> chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(tmpChargeMachineDto);

        if (ListUtil.isNull(chargeMachineDtos)) {
            throw new CmdException("设备不存在");
        }
        switch (cmd) {
            case SongbingChargeUtil.CMD_REGISTER: // 注册
                machineRegisterResponse(chargeMachineDto);
                break;
            case SongbingChargeUtil.CMD_HEARTBEAT_AND_CHANGE_PORT: // 07 心跳与插座状态改变
                String type = decodedMap.get("key5").substring(0, 2) + decodedMap.get("key6").substring(0, 2);
                if ("2728".equals(type)) {//心跳及功率上报
                    machineHeartbeat(chargeMachineDto);
                    String switchFlag = decodedMap.get("key5").substring(3);
                    if (switchFlag.contains("01")) {//有打开的开关
                        String powerFlag = decodedMap.get("key6").substring(3);
                        machineUploadChargeData(chargeMachineDto, switchFlag, powerFlag);
                    }
                } else {
                    machineChangePortState(chargeMachineDto, decodedMap);
                    //结束订单
                }
                break;
            case SongbingChargeUtil.CMD_SERVER_CONTROL: // 主动远程启动充电命令回复
                if ("0000".equals(decodedMap.get("key5").substring(3))) {//正确开始无误反馈
                    String com = decodedMap.get("key6").substring(3);
                    if ("01".equals(com)) {//远程开启
                        startChargeResponse(chargeMachineDto, decodedMap);
                    } else if ("00".equals(com)) {//远程结束
                        stopChargeResponse(chargeMachineDto, decodedMap);
                    }
                }
                break;
            case SongbingChargeUtil.CMD_CHARGE_END://充电结束
                machineChargeEnd(chargeMachineDto, data);
                break;
        }
    }

    /**
     * 充电数据上报
     *
     * @param chargeMachineDto
     * @param switchFlag
     * @param powerFlag
     */
    private void machineUploadChargeData(ChargeMachineDto chargeMachineDto, String switchFlag, String powerFlag) {
        String[] ports = splitIntoPairs(switchFlag, 2);
        String[] portPowers = splitIntoPairs(powerFlag, 4);

        int power = 0;
        for (int port = 0; port < ports.length; port++) {
            if ("00".equals(portPowers[port])) {//开关未开放弃
                continue;
            }
            power = Integer.parseInt(portPowers[port], 16);
            if (power <= 0) {//功率为零放弃
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
            double lastPower = Double.parseDouble(chargeMachineOrderDtos.get(0).getEnergy());
            double sum = power + lastPower;
            ChargeMachineOrderPo chargeMachineOrderPo = new ChargeMachineOrderPo();
            chargeMachineOrderPo.setOrderId(chargeMachineOrderDtos.get(0).getOrderId());
            chargeMachineOrderPo.setEnergy(sum + "");
            chargeMachineOrderPo.setCommunityId(chargeMachineOrderDtos.get(0).getCommunityId());
            chargeMachineOrderV1InnerServiceSMOImpl.updateChargeMachineOrder(chargeMachineOrderPo);
        }
        String paramOut = SongbingChargeUtil.heartBeatResponse(chargeMachineDto.getMachineCode());
        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(paramOut)));
    }


    /**
     * 插座状态改变上报与回复
     *
     * @param chargeMachineDto
     * @param decodedMap
     */
    private void machineChangePortState(ChargeMachineDto chargeMachineDto, Map<String, String> decodedMap) {
        //  插座
        int port = Integer.parseInt(decodedMap.get("key5").substring(1, 2), 16) + 1;
        //  状态
        int state = Integer.parseInt(decodedMap.get("key5").substring(3), 16);
        ChargeMachinePortDto chargeMachinePortDto = new ChargeMachinePortDto();
        chargeMachinePortDto.setMachineId(chargeMachineDto.getMachineId());
        chargeMachinePortDto.setPortCode(port + "");
        List<ChargeMachinePortDto> chargeMachinePortDtos = chargeMachinePortV1InnerServiceSMOImpl.queryChargeMachinePorts(chargeMachinePortDto);
        if (!ListUtil.isNull(chargeMachinePortDtos)) {
            ChargeMachinePortPo chargeMachinePortPo = new ChargeMachinePortPo();
            chargeMachinePortPo.setPortId(chargeMachinePortDtos.get(0).getPortId());
            if (state == 0) {
                chargeMachinePortPo.setState(ChargeMachinePortDto.STATE_FREE);
            } else {
                chargeMachinePortPo.setState(ChargeMachinePortDto.STATE_WORKING);
            }
            chargeMachinePortV1InnerServiceSMOImpl.updateChargeMachinePort(chargeMachinePortPo);
        }

        ChargeMachineOrderDto chargeMachineOrderDto = new ChargeMachineOrderDto();
        chargeMachineOrderDto.setPortId(chargeMachinePortDto.getPortId());
        chargeMachineOrderDto.setCommunityId(chargeMachinePortDto.getCommunityId());
        chargeMachineOrderDto.setMachineId(chargeMachinePortDto.getMachineId());
        chargeMachineOrderDto.setState(ChargeMachineOrderDto.STATE_DOING);
        List<ChargeMachineOrderDto> chargeMachineOrderDtos = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrders(chargeMachineOrderDto);
        if (!ListUtil.isNull(chargeMachineOrderDtos)) {//有进行的
            NotifyChargeOrderDto notifyChargeOrderDto = new NotifyChargeOrderDto();
            notifyChargeOrderDto.setOrderId(chargeMachineOrderDtos.get(0).getOrderId());
            notifyChargeOrderDto.setMachineCode(chargeMachineDto.getMachineCode());
            notifyChargeOrderDto.setPortCode(chargeMachinePortDto.getPortCode());
            notifyChargeOrderDto.setBodyParam("");
            notifyChargeOrderDto.setReason("停止成功");

            Date startTime = DateUtil.getDateFromStringA(chargeMachineOrderDtos.get(0).getStartTime());//开始时间
            Date endTime = new Date();//本次结束时间
            double differenceInMillis = (endTime.getTime() - startTime.getTime()) / (60 * 1000.00);
            double lastPower = Double.parseDouble(chargeMachineOrderDtos.get(0).getEnergy()) / differenceInMillis;
            notifyChargeOrderDto.setEnergy(lastPower+ "");

            notifyChargeV1InnerServiceSMOImpl.finishCharge(notifyChargeOrderDto);
        }

        String paramOut = SongbingChargeUtil.stateChangeResponse(chargeMachineDto.getMachineCode());
        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(paramOut)));
    }


    /**
     * 充电结束
     * 例如 ： 7e5d7d7f00360001110f38363135353130353730303536393601ca00100500484331313230323331313230353832313030303500000000655b08ca0c
     *
     * @param chargeMachineDto
     * @param data
     */
    private void machineChargeEnd(ChargeMachineDto chargeMachineDto, byte[] data) {

        String dataHex = "";

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
        //todo 充电电量
        int e = Integer.parseInt(dataHex.substring(44, 48), 16);
        //todo 结束时功率
        //int w = Integer.parseInt(dataHex.substring(48, 52), 16);

        ChargeMachineOrderDto chargeMachineOrderDto = new ChargeMachineOrderDto();
        chargeMachineOrderDto.setOrderId(orderNum.substring(2));
        List<ChargeMachineOrderDto> chargeMachineOrderDtos = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrders(chargeMachineOrderDto);
        if (ListUtil.isNull(chargeMachineOrderDtos)) {
            throw new CmdException("订单不存在");
        }

        Date nowTime = DateUtil.getCurrentDate();
        double tHours = (nowTime.getTime() - DateUtil.getDateFromStringA(chargeMachineOrderDtos.get(0).getStartTime()).getTime()) / (60 * 60 * 1000 * 1.00);
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        String diffInHoursStr = decimalFormat.format(tHours);
        double lastPower = Double.parseDouble(chargeMachineOrderDtos.get(0).getEnergy())*Double.parseDouble(diffInHoursStr);

        String remark = resultMap.containsKey(reason + "") ? resultMap.get(reason + "") : "未知原因";

        // JSONObject param = JSONObject.parseObject(postInfo);
        NotifyChargeOrderDto notifyChargeOrderDto = new NotifyChargeOrderDto();
        notifyChargeOrderDto.setOrderId(orderNum.substring(2));
        notifyChargeOrderDto.setMachineCode(chargeMachineDto.getMachineCode());
        notifyChargeOrderDto.setPortCode(port + "");
        notifyChargeOrderDto.setBodyParam("");
        notifyChargeOrderDto.setReason(remark);
        notifyChargeOrderDto.setEnergy(lastPower + "");


        notifyChargeV1InnerServiceSMOImpl.finishCharge(notifyChargeOrderDto);

        //
        String paramOut = String.format("%02x", 1);

        paramOut = "SongbingChargeUtil.computeResultDate(data, chargeMachineDto.getMachineCode(), paramOut)";

        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(paramOut)));

    }

    /**
     * 心跳
     *
     * @param chargeMachineDto
     */
    private void machineHeartbeat(ChargeMachineDto chargeMachineDto) {
        ChargeMachinePo chargeMachinePo = new ChargeMachinePo();
        chargeMachinePo.setHeartbeatTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        chargeMachinePo.setMachineId(chargeMachineDto.getMachineId());
        chargeMachinePo.setMachineCode(chargeMachineDto.getMachineCode());
        chargeMachineV1InnerServiceSMOImpl.updateChargeMachine(chargeMachinePo);
        String paramOut = SongbingChargeUtil.heartBeatResponse(chargeMachineDto.getMachineCode());
        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(paramOut)));
    }

    /**
     * 登录认证应答
     *
     * @param chargeMachineDto
     */
    private void machineRegisterResponse(ChargeMachineDto chargeMachineDto) {
        String paramOut = SongbingChargeUtil.registerResponseDate(chargeMachineDto.getMachineCode());
        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(paramOut)));
    }

    private static String[] splitIntoPairs(String input, int len) {
        if (input == null || input.isEmpty()) {
            return new String[0];
        }
        if (input.length() % len != 0) {
            return new String[0];
        }

        String[] pairs = new String[input.length() / len];
        for (int i = 0, j = 0; i < input.length(); i += len, j++) {
            pairs[j] = input.substring(i, i + len);
        }

        for (int i = 0; i < pairs.length; i++) {
            System.out.print(pairs[i] + " ");
        }
        System.out.println();
        return pairs;
    }
}
