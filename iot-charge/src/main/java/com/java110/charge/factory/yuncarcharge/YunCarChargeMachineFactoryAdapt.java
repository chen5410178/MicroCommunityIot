package com.java110.charge.factory.yuncarcharge;

import com.java110.bean.ResultVo;
import com.java110.charge.factory.IChargeFactoryAdapt;
import com.java110.core.cache.UrlCache;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.*;
import com.java110.dto.chargeMachine.*;
import com.java110.dto.chargePeakAlleyPrice.ChargePeakAlleyPriceDto;
import com.java110.dto.chargeProcessAnalysis.ChargeProcessAnalysisDto;
import com.java110.dto.chargeRulePrice.ChargeRulePriceDto;
import com.java110.dto.data.NettyReplyDataDto;
import com.java110.intf.charge.*;
import com.java110.intf.hal.INotifyNettyDataV1InnerServiceSMO;
import com.java110.po.chargeMachine.ChargeMachinePo;
import com.java110.po.chargeMachineOrder.ChargeMachineOrderPo;
import com.java110.po.chargeMachinePort.ChargeMachinePortPo;
import com.java110.po.chargeProcessAnalysis.ChargeProcessAnalysisPo;
import com.java110.po.chargeProcessInfo.ChargeProcessInfoPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
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
    private IChargeProcessAnalysisV1InnerServiceSMO chargeProcessAnalysisV1InnerServiceSMOImpl;

    @Autowired
    private INotifyChargeV1InnerServiceSMO notifyChargeV1InnerServiceSMOImpl;

    public static final Map<String, String> resultMap = new HashMap<>();

    static {
        resultMap.put("40", "结束充电，APP 远程停止");
        resultMap.put("41", "结束充电，SOC 达到 100%");
        resultMap.put("42", "结束充电，充电电量满足设定条件");
        resultMap.put("43", "结束充电，充电金额满足设定条件");
        resultMap.put("44", "结束充电，充电时间满足设定条件");
        resultMap.put("4A", "充电启动失败，充电桩控制系统故障(需要重启或自动恢复)");
        resultMap.put("4B", "充电启动失败，控制导引断开");
        resultMap.put("4C", "充电启动失败，断路器跳位");
        resultMap.put("4D", "充电启动失败，电表通信中断");
        resultMap.put("4E", "充电启动失败，余额不足");
        resultMap.put("4F", "充电启动失败，充电模块故障");
        resultMap.put("50", "充电启动失败，急停开入");
        resultMap.put("51", "充电启动失败，防雷器异常");
        resultMap.put("52", "充电启动失败，BMS 未就绪");
        resultMap.put("53", "充电启动失败，温度异常");
        resultMap.put("54", "充电启动失败，电池反接故障");
        resultMap.put("55", "充电启动失败，电子锁异常");
        resultMap.put("56", "充电启动失败，合闸失败");
        resultMap.put("57", "充电启动失败，绝缘异常");
        resultMap.put("59", "充电启动失败，接收 BMS 握手报文 BHM 超时");
        resultMap.put("5A", "充电启动失败，接收 BMS 和车辆的辨识报文超时 BRM");
        resultMap.put("5B", "充电启动失败，接收电池充电参数报文超时 BCP");
        resultMap.put("5C", "充电启动失败，接收 BMS 完成充电准备报文超时 BRO AA");
        resultMap.put("5D", "充电启动失败，接收电池充电总状态报文超时 BCS");
        resultMap.put("5E", "充电启动失败，接收电池充电要求报文超时 BCL");
        resultMap.put("5F", "充电启动失败，接收电池状态信息报文超时 BSM");
        resultMap.put("60", "充电启动失败，GB2015 电池在 BHM 阶段有电压不允许充电");
        resultMap.put("61", "充电启动失败，GB2015 辨识阶段在 BRO_AA 时候电池实际电压与 BCP 报文电池电压差距大于 5%");
        resultMap.put("62", "充电启动失败，B2015 充电机在预充电阶段从 BRO_AA 变成 BRO_00 状态");
        resultMap.put("63", "充电启动失败，接收主机配置报文超时");
        resultMap.put("64", "充电启动失败，充电机未准备就绪,我们没有回 CRO AA，对应老国标");
        resultMap.put("6A", "充电异常中止，系统闭锁");
        resultMap.put("6B", "充电异常中止，导引断开");
        resultMap.put("6C", "充电异常中止，断路器跳位");
        resultMap.put("6D", "充电异常中止，电表通信中断");
        resultMap.put("6E", "充电异常中止，余额不足");
        resultMap.put("6F", "充电异常中止，交流保护动作");
        resultMap.put("70", "充电异常中止，直流保护动作");
        resultMap.put("71", "充电异常中止，充电模块故障");
        resultMap.put("72", "充电异常中止，急停开入");
        resultMap.put("73", "充电异常中止，防雷器异常");
        resultMap.put("74", "充电异常中止，温度异常");
        resultMap.put("75", "充电异常中止，输出异常");
        resultMap.put("76", "充电异常中止，充电无流");
        resultMap.put("77", "充电异常中止，电子锁异常");
        resultMap.put("79", "充电异常中止，总充电电压异常");
        resultMap.put("7A", "充电异常中止，总充电电流异常");
        resultMap.put("7B", "充电异常中止，单体充电电压异常");
        resultMap.put("7C", "充电异常中止，电池组过温");
        resultMap.put("7D", "充电异常中止，最高单体充电电压异常");
        resultMap.put("7E", "充电异常中止，最高电池组过温");
        resultMap.put("7F", "充电异常中止，BMV 单体充电电压异常");
        resultMap.put("80", "充电异常中止，BMT 电池组过温");
        resultMap.put("81", "充电异常中止，电池状态异常停止充电");
        resultMap.put("82", "充电异常中止，车辆发报文禁止充电");
        resultMap.put("83", "充电异常中止，充电桩断电");
        resultMap.put("84", "充电异常中止，接收电池充电总状态报文超时");
        resultMap.put("85", "充电异常中止，接收电池充电要求报文超时");
        resultMap.put("86", "充电异常中止，接收电池状态信息报文超时");
        resultMap.put("87", "充电异常中止，接收 BMS 中止充电报文超时");
        resultMap.put("88", "充电异常中止，接收 BMS 充电统计报文超时");
        resultMap.put("89", "充电异常中止，接收对侧 CCS 报文超时");
        resultMap.put("90", "未知原因停止");

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

        // 检查工单是否为开启中 如果是开启中 说明工单异常了手动结束

        ChargeMachineOrderDto chargeMachineOrderDto = new ChargeMachineOrderDto();
        chargeMachineOrderDto.setMachineId(chargeMachineDto.getMachineId());
        chargeMachineOrderDto.setPortId(chargeMachinePortDto.getPortId());
        chargeMachineOrderDto.setState(ChargeMachineOrderDto.STATE_STARTING);
        List<ChargeMachineOrderDto> chargeMachineOrderDtos = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrders(chargeMachineOrderDto);
        if (ListUtil.isNull(chargeMachineOrderDtos)) {
            return new ResultVo(ResultVo.CODE_OK, "结束信号已发送");
        }

        NotifyChargeOrderDto notifyChargeOrderDto = new NotifyChargeOrderDto();
        notifyChargeOrderDto.setOrderId(chargeMachineOrderDtos.get(0).getOrderId());
        notifyChargeOrderDto.setMachineCode(chargeMachineDto.getMachineCode());
        notifyChargeOrderDto.setPortCode(chargeMachinePortDto.getPortCode());
        notifyChargeOrderDto.setBodyParam("");
        String reasonStr = "结束充电-> 开启设备没有反映，后台手工结束";

        notifyChargeOrderDto.setReason(reasonStr);
        notifyChargeOrderDto.setAmount("0");
        notifyChargeOrderDto.setEnergy("0");
        notifyChargeOrderDto.setFeeAmount("0");
        notifyChargeOrderDto.setServiceAmount("0");
        notifyChargeV1InnerServiceSMOImpl.finishCharge(notifyChargeOrderDto);

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
        chargeMachineDto = chargeMachineDtos.get(0);
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
     *
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

        int prexPos = 0; // 起始标志
        int prexLen = 12;
        int transId = prexPos + prexLen; // 交易流水号
        int transLen = 32;
        int machineCodePos = transId + transLen;
        int machineCodeLen = 14;
        int portCodePos = machineCodePos + machineCodeLen; // 桩编号
        int portCodeLen = 2;
        int startTimePos = portCodePos + portCodeLen;
        int startTimeLen = 14;
        int endTimePos = startTimePos + startTimeLen;
        int endTimeLen = 14;
        int jianPricePos = endTimePos + endTimeLen; //尖单价
        int jianPriceLen = 8;
        int jianEnergyPos = jianPricePos + jianPriceLen;
        int jianEnergyLen = 8;
        int jisunjianEnergyPos = jianEnergyPos + jianEnergyLen;
        int jisunjianEnergyLen = 8;
        int jianAmountPos = jisunjianEnergyPos + jisunjianEnergyLen; // 尖金额
        int jianAmountLen = 8;
        int fenPricePos = jianAmountPos + jianAmountLen; //峰单价
        int fenPriceLen = 8;
        int fenEnergyPos = fenPricePos + fenPriceLen;
        int fenEnergyLen = 8;
        int jisunfenEnergyPos = fenEnergyPos + fenEnergyLen;
        int jisunfenEnergyLen = 8;
        int fenAmountPos = jisunfenEnergyPos + jisunfenEnergyLen; // 尖金额
        int fenAmountLen = 8;
        int pingPricePos = fenAmountPos + fenAmountLen; //峰单价
        int pingPriceLen = 8;
        int pingEnergyPos = pingPricePos + pingPriceLen;
        int pingEnergyLen = 8;
        int jisunpingEnergyPos = pingEnergyPos + pingEnergyLen;
        int jisunpingEnergyLen = 8;
        int pingAmountPos = jisunpingEnergyPos + jisunpingEnergyLen; // 平金额
        int pingAmountLen = 8;
        int guPricePos = pingAmountPos + pingAmountLen; //峰单价
        int guPriceLen = 8;
        int guEnergyPos = guPricePos + guPriceLen;
        int guEnergyLen = 8;
        int jisunguEnergyPos = guEnergyPos + guEnergyLen;
        int jisunguEnergyLen = 8;
        int guAmountPos = jisunguEnergyPos + jisunguEnergyLen; // 平金额
        int guAmountLen = 8;
        int biaoStartValuePos = guAmountPos + guAmountLen;
        int biaoStartValueLen = 10;
        int biaoEndValuePos = biaoStartValuePos + biaoStartValueLen;
        int biaoEndValueLen = 10;
        int energyPos = biaoEndValuePos + biaoEndValueLen;
        int energyLen = 8;
        int sunEnergyPos = energyPos + energyLen;
        int sunEnergyLen = 8;
        int amountPos = sunEnergyPos + sunEnergyLen;
        int amountLen = 8;
        int carOnlyIdPos = amountPos + amountLen; // 电动汽车唯一标识
        int carOnlyIdLen = 34;
        int tranFlagPos = carOnlyIdPos + carOnlyIdLen;
        int tranFlagLen = 2;
        int transTimePos = tranFlagPos + tranFlagLen;
        int transTimeLen = 14;
        int stopReasonPos = transTimePos + transTimeLen;
        int stopReasonLen = 2;


        recordedData(chargeMachineDto, data);
        String portCode = YunCarUtil.getTargetString(data, portCodePos, portCodeLen);
        String tradeNo = YunCarUtil.getTradeNo(data);
        String amount = YunCarUtil.getTargetBin(data, amountPos, amountLen, 4);
        String energy = YunCarUtil.getTargetBin(data, energyPos, energyLen, 4);
        String reason = YunCarUtil.getTargetString(data, stopReasonPos, stopReasonLen);

        ChargeMachineOrderDto chargeMachineOrderDto = new ChargeMachineOrderDto();
        chargeMachineOrderDto.setOrderId(tradeNo);
        chargeMachineOrderDto.setCommunityId(chargeMachineDto.getCommunityId());
        chargeMachineOrderDto.setMachineId(chargeMachineDto.getMachineId());
        List<ChargeMachineOrderDto> chargeMachineOrderDtos = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrders(chargeMachineOrderDto);
        if (ListUtil.isNull(chargeMachineOrderDtos)) {
            return;
        }

        ChargePeakAlleyPriceDto chargePeakAlleyPriceDto = new ChargePeakAlleyPriceDto();
        chargePeakAlleyPriceDto.setRuleId(chargeMachineDto.getRuleId());
        List<ChargePeakAlleyPriceDto> chargePeakAlleyPriceDtos = chargePeakAlleyPriceV1InnerServiceSMOImpl.queryChargePeakAlleyPrices(chargePeakAlleyPriceDto);
        if (ListUtil.isNull(chargePeakAlleyPriceDtos)) {
            return;
        }
//        00	尖费率
//        01	峰费率
//        02	平费率
//        03	谷费率
        String jianServicePrice = "0";
        String fenServicePrice = "0";
        String pingServicePrice = "0";
        String guServicePrice = "0";
        for (ChargePeakAlleyPriceDto tmpChargePeakAlleyPriceDto : chargePeakAlleyPriceDtos) {
            if ("00".equals(tmpChargePeakAlleyPriceDto.getType())) {
                jianServicePrice = tmpChargePeakAlleyPriceDto.getServicePrice();
            }
            if ("01".equals(tmpChargePeakAlleyPriceDto.getType())) {
                fenServicePrice = tmpChargePeakAlleyPriceDto.getServicePrice();
            }
            if ("02".equals(tmpChargePeakAlleyPriceDto.getType())) {
                pingServicePrice = tmpChargePeakAlleyPriceDto.getServicePrice();
            }
            if ("03".equals(tmpChargePeakAlleyPriceDto.getType())) {
                guServicePrice = tmpChargePeakAlleyPriceDto.getServicePrice();
            }
        }
        // 尖电量
        String jianEnergy = YunCarUtil.getTargetBin(data, jianEnergyPos, jianEnergyLen, 4);
        // 峰电量
        String fenEnergy = YunCarUtil.getTargetBin(data, fenEnergyPos, fenEnergyLen, 4);
        // 平电量
        String pingEnergy = YunCarUtil.getTargetBin(data, pingEnergyPos, pingEnergyLen, 4);
        // 谷电量
        String guEnergy = YunCarUtil.getTargetBin(data, guEnergyPos, guEnergyLen, 4);
        BigDecimal jianDec = new BigDecimal(jianEnergy).multiply(new BigDecimal(jianServicePrice)).setScale(4, BigDecimal.ROUND_HALF_UP);
        BigDecimal fenDec = new BigDecimal(fenEnergy).multiply(new BigDecimal(fenServicePrice)).setScale(4, BigDecimal.ROUND_HALF_UP);
        BigDecimal pingDec = new BigDecimal(pingEnergy).multiply(new BigDecimal(pingServicePrice)).setScale(4, BigDecimal.ROUND_HALF_UP);
        BigDecimal guDec = new BigDecimal(guEnergy).multiply(new BigDecimal(guServicePrice)).setScale(4, BigDecimal.ROUND_HALF_UP);
        BigDecimal serviceFeeDec = jianDec.add(fenDec).add(pingDec).add(guDec);
        BigDecimal feeDec = new BigDecimal(energy).subtract(serviceFeeDec);

        NotifyChargeOrderDto notifyChargeOrderDto = new NotifyChargeOrderDto();
        notifyChargeOrderDto.setOrderId(tradeNo);
        notifyChargeOrderDto.setMachineCode(chargeMachineDto.getMachineCode());
        notifyChargeOrderDto.setPortCode(Integer.parseInt(portCode, 16) + "");
        notifyChargeOrderDto.setBodyParam("");
        String reasonStr = resultMap.get(reason);
        if (StringUtil.isEmpty(reasonStr)) {
            reasonStr = "结束充电->" + reason;
        }
        notifyChargeOrderDto.setReason(reasonStr);
        notifyChargeOrderDto.setAmount(amount);
        notifyChargeOrderDto.setEnergy(energy);
        notifyChargeOrderDto.setFeeAmount(feeDec.doubleValue() + "");
        notifyChargeOrderDto.setServiceAmount(serviceFeeDec.doubleValue() + "");
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
        chargeProcessInfoPo.setMachineId(chargeMachineDto.getMachineId());
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
        //recordedData(chargeMachineDto, data);
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
        // 684000020013000000000000000000000000000000006620241101000102020200000000003200000000000000000032000000000000000000000000000000000000c3c6

        int prexPos = 0; // 起始标志
        int prexLen = 2;
        int dataLenPos = prexLen; //数据长度
        int dataLen = 2;
        int seqPos = dataLenPos + 2; //序列号域
        int seqLen = 4;
        int secureFlagPos = seqPos + seqLen;//加密标志
        int secureFlagLen = 2;
        int cmdPos = secureFlagPos + 2;
        int cmdLen = 2;
        int transPos = cmdPos + cmdLen;
        int transLen = 32;
        int machineCodePos = transPos + transLen;
        int machineCodeLen = 14;
        int portCodePos = machineCodePos + machineCodeLen;
        int portCodeLen = 2;
        int statePos = portCodePos + portCodeLen;
        int stateLen = 2;
        int backInPlacePos = statePos + stateLen;
        int backInPlaceLen = 2;
        int insertedGunPos = backInPlacePos + backInPlaceLen;
        int insertedGunLen = 2;
        int outputVoltagePos = insertedGunPos + insertedGunLen;
        int outputVoltageLen = 4;
        int outputCurrentPos = outputVoltagePos + outputVoltageLen;
        int outputCurrentLen = 4;
        int gunLineTemPos = outputCurrentPos + outputCurrentLen;
        int gunLineTemLen = 2;
        int gunCodePos = gunLineTemPos + gunLineTemLen;
        int gunCodeLen = 16;
        int socPos = gunCodePos + gunCodeLen;
        int socLen = 2;
        int batterypackMaxTemPos = socPos + socLen;
        int batterypackMaxTemLen = 2;
        int accumulatedChargingTimePos = batterypackMaxTemPos + batterypackMaxTemLen;
        int accumulatedChargingTimeLen = 4;
        int remainingTimePos = accumulatedChargingTimePos + accumulatedChargingTimeLen;
        int remainingTimeLen = 4;
        int chargingDegreePos = remainingTimePos + remainingTimeLen;
        int chargingDegreeLen = 8;
        int calculatedChargingDegreePos = chargingDegreePos + chargingDegreeLen;
        int calculatedChargingDegreeLen = 8;
        int rechargeAmountPos = calculatedChargingDegreePos + calculatedChargingDegreeLen;
        int rechargeAmountLen = 8;
        int hardwareFailurePos = rechargeAmountPos + rechargeAmountLen;
        int hardwareFailureLen = 4;

        String machineCode = YunCarUtil.getTargetString(data, machineCodePos, machineCodeLen);
        String portCode = YunCarUtil.getTargetString(data, portCodePos, portCodeLen);

        if (StringUtil.isEmpty(machineCode) || StringUtil.isEmpty(portCode)) {
            throw new CmdException("设备编号或枪号不存在");
        }
        ChargeMachineDto tmpChargeMachineDto = new ChargeMachineDto();
        tmpChargeMachineDto.setMachineCode(machineCode);
        List<ChargeMachineDto> chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(tmpChargeMachineDto);

        if (ListUtil.isNull(chargeMachineDtos)) {
            throw new CmdException("充电桩不存在");
        }

        // todo 插槽
        ChargeMachinePortDto chargeMachinePortDto = new ChargeMachinePortDto();
        chargeMachinePortDto.setMachineId(chargeMachineDtos.get(0).getMachineId());
        chargeMachinePortDto.setPortCode(Integer.parseInt(portCode) + "");
        List<ChargeMachinePortDto> chargeMachinePortDtos = chargeMachinePortV1InnerServiceSMOImpl.queryChargeMachinePorts(chargeMachinePortDto);
        if (ListUtil.isNull(chargeMachinePortDtos)) {
            throw new CmdException("插座不存在");
        }

        ChargeMachineOrderDto chargeMachineOrderDto = new ChargeMachineOrderDto();
        chargeMachineOrderDto.setMachineId(chargeMachineDtos.get(0).getMachineId());
        chargeMachineOrderDto.setPortId(chargeMachinePortDtos.get(0).getPortId());
        chargeMachineOrderDto.setState(ChargeMachineOrderDto.STATE_DOING);
        List<ChargeMachineOrderDto> chargeMachineOrderDtos = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrders(chargeMachineOrderDto);

        if (ListUtil.isNull(chargeMachineOrderDtos)) {
            throw new IllegalArgumentException("订单不存在");
        }

        ChargeProcessAnalysisPo chargeProcessAnalysisPo = new ChargeProcessAnalysisPo();
        chargeProcessAnalysisPo.setGunNum(portCode); // 枪编号
        chargeProcessAnalysisPo.setOrderId(chargeMachineOrderDtos.get(0).getOrderId()); // 订单ID
        chargeProcessAnalysisPo.setAccumulatedChargingTime(YunCarUtil.getTargetBin(data, accumulatedChargingTimePos, accumulatedChargingTimeLen)); //累计充电时间单位：min；待机置零
        chargeProcessAnalysisPo.setOutputVoltage(YunCarUtil.getTargetBin(data, outputVoltagePos, outputVoltageLen, 1)); // 输出电压
        chargeProcessAnalysisPo.setSoc(YunCarUtil.getTargetBin(data, socPos, socLen));// SOC
        chargeProcessAnalysisPo.setRemark(""); // 备注
        chargeProcessAnalysisPo.setChargingDegree(YunCarUtil.getTargetBin(data, chargingDegreePos, chargingDegreeLen, 4)); //  充电度数 精确到小数点后四位；待机置零
        chargeProcessAnalysisPo.setInsertedGun(YunCarUtil.getTargetBin(data, insertedGunPos, insertedGunLen)); // 是否插枪
        chargeProcessAnalysisPo.setOutputCurrent(YunCarUtil.getTargetBin(data, outputCurrentPos, outputCurrentLen, 1));//输出电流
        chargeProcessAnalysisPo.setRemainingTime(YunCarUtil.getTargetBin(data, remainingTimePos, remainingTimeLen));//剩余时间单位：min；待机置零、交流桩置零
        String gunLineTem = YunCarUtil.getTargetBin(data, gunLineTemPos, gunLineTemLen);
        try{
           int gunLineTemInt = Integer.parseInt(gunLineTem)-50;
            gunLineTem = gunLineTemInt+"";
        }catch (Exception e){
            e.printStackTrace();
        }
        chargeProcessAnalysisPo.setGunLineTem(gunLineTem); //枪线温度
        chargeProcessAnalysisPo.setBackInPlace(YunCarUtil.getTargetBin(data, backInPlacePos, backInPlaceLen)); // 枪是否归位 0x00 否 0x01 是 0x02未知
        chargeProcessAnalysisPo.setBatterypackMaxTem(YunCarUtil.getTargetBin(data, batterypackMaxTemPos, batterypackMaxTemLen)); //电池组最高温度
        chargeProcessAnalysisPo.setMachineId(chargeMachineDto.getMachineId());
        chargeProcessAnalysisPo.setPileNum(machineCode); // 桩编号
        chargeProcessAnalysisPo.setCalculatedChargingDegree(YunCarUtil.getTargetBin(data, calculatedChargingDegreePos, calculatedChargingDegreeLen, 4)); //计损充电度数
        chargeProcessAnalysisPo.setHardwareFailure(YunCarUtil.getTargetBin(data, hardwareFailurePos, hardwareFailureLen));//硬件故障
        chargeProcessAnalysisPo.setRechargeAmount(YunCarUtil.getTargetBin(data, rechargeAmountPos, rechargeAmountLen, 4));//已充金额
        chargeProcessAnalysisPo.setGunCode(YunCarUtil.getTargetBin(data, gunCodePos, gunCodeLen));//枪线编码
        chargeProcessAnalysisPo.setCommunityId(chargeMachineDto.getCommunityId());

        ChargeMachineOrderPo chargeMachineOrderPo = new ChargeMachineOrderPo();
        chargeMachineOrderPo.setOrderId(chargeProcessAnalysisPo.getOrderId());
        chargeMachineOrderPo.setEnergy(chargeProcessAnalysisPo.getChargingDegree());
        chargeMachineOrderPo.setAmount(chargeProcessAnalysisPo.getRechargeAmount());
        chargeMachineOrderV1InnerServiceSMOImpl.updateChargeMachineOrder(chargeMachineOrderPo);


        ChargeProcessAnalysisDto chargeProcessAnalysisDto = new ChargeProcessAnalysisDto();
        chargeProcessAnalysisDto.setOrderId(chargeMachineOrderDtos.get(0).getOrderId());
        List<ChargeProcessAnalysisDto> chargeProcessAnalysisDtos =
                chargeProcessAnalysisV1InnerServiceSMOImpl.queryChargeProcessAnalysiss(chargeProcessAnalysisDto);

        if (ListUtil.isNull(chargeProcessAnalysisDtos)) {
            chargeProcessAnalysisPo.setCpaId(GenerateCodeFactory.getGeneratorId("11"));
            chargeProcessAnalysisV1InnerServiceSMOImpl.saveChargeProcessAnalysis(chargeProcessAnalysisPo);
            return;
        }
        chargeProcessAnalysisPo.setCpaId(chargeProcessAnalysisDtos.get(0).getCpaId());
        chargeProcessAnalysisV1InnerServiceSMOImpl.updateChargeProcessAnalysis(chargeProcessAnalysisPo);
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

        //时间校准
        paramOut = YunCarUtil.calibrationTime(chargeMachineDto.getMachineCode());
        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(paramOut)));
        //二维码下发
        paramOut = YunCarUtil.sendQrCode(chargeMachineDto.getMachineCode(), UrlCache.getOwnerUrl() + "/app/charge/ykc?No=");
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
