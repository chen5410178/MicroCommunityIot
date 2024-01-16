package com.java110.charge.factory.hc;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.charge.factory.IChargeFactoryAdapt;
import com.java110.charge.factory.dingding.DingdingChargeUtils;
import com.java110.charge.factory.lvcc.LvCCChargeMachineFactoryAdapt;
import com.java110.charge.factory.lvcc.LvCCUtil;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.core.utils.*;
import com.java110.dto.chargeMachine.*;
import com.java110.dto.data.NettyReplyDataDto;
import com.java110.intf.charge.IChargeMachineOrderV1InnerServiceSMO;
import com.java110.intf.charge.IChargeMachinePortV1InnerServiceSMO;
import com.java110.intf.charge.IChargeMachineV1InnerServiceSMO;
import com.java110.intf.charge.INotifyChargeV1InnerServiceSMO;
import com.java110.intf.hal.INotifyNettyDataV1InnerServiceSMO;
import com.java110.po.chargeMachine.ChargeMachinePo;
import com.java110.po.chargeMachineOrder.ChargeMachineOrderPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 官方两轮充电桩协议
 */
@Service("hcChargeMachineFactory")
public class HcChargeMachineFactoryAdapt implements IChargeFactoryAdapt {

    private static Logger logger = LoggerFactory.getLogger(HcChargeMachineFactoryAdapt.class);


    // todo 请求topic
    public static final String TOPIC_REQ = "hc/charge/request/{sn}";

    //todo 返回 topic
    public static final String TOPIC_RES = "hc/charge/response";

    public static final String CMD_OPEN_CHARGE = "openCharge"; //todo 开启充电
    public static final String CMD_CLOSE_CHARGE = "closeCharge"; //todo 关闭充电

    public static final String CMD_STOP_CHARGE = "stopCharge"; //todo 结束充电

    public static final String CMD_HEARTBEAT = "heartbeat"; //todo 心跳
    public static final String CMD_REBOOT = "reboot"; //todo 重启

    public static final String CMD_UPLOAD_CHARGE_DATA = "uploadChargeData"; //todo 上报充电数据


    public static final String SN = "{sn}";

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


    @Override
    public ResultVo startCharge(ChargeMachineDto chargeMachineDto, ChargeMachinePortDto chargeMachinePortDto, String chargeType, double duration, String orderId) {

        String taskId = GenerateCodeFactory.getGeneratorId("11");
        JSONObject param = new JSONObject();
        param.put("cmd", CMD_OPEN_CHARGE);
        param.put("taskId", taskId);
        param.put("machineCode", chargeMachineDto.getMachineCode());
        param.put("port", chargeMachinePortDto.getPortCode());
        param.put("duration", duration * 60);
        param.put("orderId", orderId);

        MqttFactory.publish(TOPIC_REQ.replace(SN, chargeMachineDto.getMachineCode()), param.toJSONString());

        return new ResultVo(ResultVo.CODE_OK, "提交开启充电");
    }

    @Override
    public ResultVo stopCharge(ChargeMachineDto chargeMachineDto, ChargeMachinePortDto chargeMachinePortDto) {
        String taskId = GenerateCodeFactory.getGeneratorId("11");
        JSONObject param = new JSONObject();
        param.put("cmd", CMD_CLOSE_CHARGE);
        param.put("taskId", taskId);
        param.put("machineCode", chargeMachineDto.getMachineCode());
        param.put("port", chargeMachinePortDto.getPortCode());

        MqttFactory.publish(TOPIC_REQ.replace(SN, chargeMachineDto.getMachineCode()), param.toJSONString());

        ChargeMachineOrderDto chargeMachineOrderDto = new ChargeMachineOrderDto();
        chargeMachineOrderDto.setPortId(chargeMachinePortDto.getPortId());
        chargeMachineOrderDto.setCommunityId(chargeMachinePortDto.getCommunityId());
        chargeMachineOrderDto.setMachineId(chargeMachinePortDto.getMachineId());
        List<ChargeMachineOrderDto> chargeMachineOrderDtos = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrders(chargeMachineOrderDto);

        if (ListUtil.isNull(chargeMachineOrderDtos)) {
            return new ResultVo(ResultVo.CODE_OK, "停止成功");
        }
        //todo 设备不上传结束的指令上来 所以需要 自己手工结束下
        NotifyChargeOrderDto notifyChargeOrderDto = new NotifyChargeOrderDto();
        notifyChargeOrderDto.setOrderId(chargeMachineOrderDtos.get(0).getOrderId());
        notifyChargeOrderDto.setMachineCode(chargeMachineDto.getMachineCode());
        notifyChargeOrderDto.setPortCode(chargeMachinePortDto.getPortCode());
        notifyChargeOrderDto.setBodyParam("");
        notifyChargeOrderDto.setReason("停止成功");
        notifyChargeOrderDto.setEnergy(chargeMachineOrderDtos.get(0).getEnergy());

        notifyChargeV1InnerServiceSMOImpl.finishCharge(notifyChargeOrderDto);

        return new ResultVo(ResultVo.CODE_OK, "提交结束充电");
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
    public void chargeResult(ChargeMachineDto cMachineDto, byte[] data) {
        //todo 解析指令
        JSONObject paramIn = null;
        try {
            paramIn = JSONObject.parseObject(new String(data, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String cmd = paramIn.getString("cmd");
        String taskId = paramIn.getString("taskId");
        String machineCode = paramIn.getString("machineCode");

        Assert.hasLength(cmd, "未包含cmd");
        Assert.hasLength(taskId, "未包含taskId");
        Assert.hasLength(machineCode, "未包含machineCode");


        ChargeMachineDto chargeMachineDto = new ChargeMachineDto();
        chargeMachineDto.setMachineCode(machineCode);

        List<ChargeMachineDto> chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(chargeMachineDto);

        Assert.listOnlyOne(chargeMachineDtos, "未包含充电设备");

        logger.debug("设备 {} ，解析到 指令为 ：{}", chargeMachineDto.getMachineCode(), cmd);

        switch (cmd) {
            case CMD_HEARTBEAT: // todo 心跳
                machineHeartbeat(cmd, taskId, machineCode, chargeMachineDtos.get(0));
                break;
            case CMD_OPEN_CHARGE: // todo 开始充电返回
                break;
            case CMD_STOP_CHARGE: // todo 结束充电
                machineChargeEnd(cmd, taskId, machineCode, chargeMachineDtos.get(0), paramIn);
                break;
            case CMD_UPLOAD_CHARGE_DATA:// todo 充电过程中上报 功率
                machineUploadChargeData(cmd, taskId, machineCode, chargeMachineDtos.get(0), paramIn);
                break;
//            case LvCCUtil.CMD_CHANGE_PORT_STATE:// todo 充电过程中上报 功率
//                machineChangePortState(chargeMachineDto, data);
//                break;


        }
    }

    /**
     * 充电数据上报
     *
     * @param chargeMachineDto
     * @param data
     */
    private void machineUploadChargeData(String cmd, String taskId, String machineCode, ChargeMachineDto chargeMachineDto, JSONObject data) {


        String energy = data.getString("energy");
        String port = data.getString("port");


        ChargeMachinePortDto chargeMachinePortDto = new ChargeMachinePortDto();
        chargeMachinePortDto.setMachineId(chargeMachineDto.getMachineId());
        chargeMachinePortDto.setPortCode(port + "");
        List<ChargeMachinePortDto> chargeMachinePortDtos = chargeMachinePortV1InnerServiceSMOImpl.queryChargeMachinePorts(chargeMachinePortDto);
        if (ListUtil.isNull(chargeMachinePortDtos)) {
            return;
        }

        ChargeMachineOrderDto chargeMachineOrderDto = new ChargeMachineOrderDto();
        chargeMachineOrderDto.setMachineId(chargeMachineDto.getMachineId());
        chargeMachineOrderDto.setPortId(chargeMachinePortDtos.get(0).getPortId());
        chargeMachineOrderDto.setState(ChargeMachineOrderDto.STATE_DOING);
        List<ChargeMachineOrderDto> chargeMachineOrderDtos = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrders(chargeMachineOrderDto);
        if (ListUtil.isNull(chargeMachineOrderDtos)) {
            return;
        }

        ChargeMachineOrderPo chargeMachineOrderPo = new ChargeMachineOrderPo();
        chargeMachineOrderPo.setOrderId(chargeMachineOrderDtos.get(0).getOrderId());
        chargeMachineOrderPo.setEnergy(energy + "");
        chargeMachineOrderPo.setCommunityId(chargeMachineOrderDtos.get(0).getCommunityId());
        chargeMachineOrderV1InnerServiceSMOImpl.updateChargeMachineOrder(chargeMachineOrderPo);


        JSONObject paramOut = new JSONObject();
        paramOut.put("cmd", cmd);
        paramOut.put("taskId", taskId);
        paramOut.put("code", 0);
        paramOut.put("msg", "处理成功");
        paramOut.put("machineCode", machineCode);
        MqttFactory.publish(TOPIC_REQ.replace(SN, machineCode), paramOut.toJSONString());

    }

    private void machineChargeEnd(String cmd, String taskId, String machineCode, ChargeMachineDto chargeMachineDto, JSONObject data) {


        // todo 结束原因
        String reason = data.getString("reason");
        String port = data.getString("port");
        String orderNum = data.getString("orderId");
        String energy = data.getString("energy");


        String remark = StringUtil.isEmpty(reason) ? "未知原因" : reason;

        // JSONObject param = JSONObject.parseObject(postInfo);
        NotifyChargeOrderDto notifyChargeOrderDto = new NotifyChargeOrderDto();
        notifyChargeOrderDto.setOrderId(orderNum);
        notifyChargeOrderDto.setMachineCode(chargeMachineDto.getMachineCode());
        notifyChargeOrderDto.setPortCode(port + "");
        notifyChargeOrderDto.setBodyParam("");
        notifyChargeOrderDto.setReason(remark);
        notifyChargeOrderDto.setEnergy(energy + "");


        notifyChargeV1InnerServiceSMOImpl.finishCharge(notifyChargeOrderDto);


        JSONObject paramOut = new JSONObject();
        paramOut.put("cmd", cmd);
        paramOut.put("taskId", taskId);
        paramOut.put("code", 0);
        paramOut.put("msg", "处理成功");
        paramOut.put("machineCode", machineCode);
        MqttFactory.publish(TOPIC_REQ.replace(SN, machineCode), paramOut.toJSONString());
    }

    private void machineHeartbeat(String cmd, String taskId, String machineCode, ChargeMachineDto chargeMachineDto) {

        ChargeMachinePo chargeMachinePo = new ChargeMachinePo();
        chargeMachinePo.setHeartbeatTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        chargeMachinePo.setMachineId(chargeMachineDto.getMachineId());
        chargeMachinePo.setMachineCode(chargeMachineDto.getMachineCode());
        chargeMachineV1InnerServiceSMOImpl.updateChargeMachine(chargeMachinePo);

        JSONObject paramOut = new JSONObject();
        paramOut.put("cmd", cmd);
        paramOut.put("taskId", taskId);
        paramOut.put("code", 0);
        paramOut.put("msg", "处理成功");
        paramOut.put("machineCode", machineCode);
        MqttFactory.publish(TOPIC_REQ.replace(SN, machineCode), paramOut.toJSONString());
    }

    @Override
    public ResultVo restartMachine(ChargeMachineDto chargeMachineDto) {
        String taskId = GenerateCodeFactory.getGeneratorId("11");

        JSONObject paramOut = new JSONObject();
        paramOut.put("cmd", CMD_REBOOT);
        paramOut.put("taskId", taskId);
        MqttFactory.publish(TOPIC_REQ.replace(SN, chargeMachineDto.getMachineCode()), paramOut.toJSONString());
        return new ResultVo(ResultVo.CODE_OK, "申请重启");
    }
}
