package com.java110.meter.factory.hc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.cache.UrlCache;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.meter.MeterMachineDetailDto;
import com.java110.dto.meter.MeterMachineDto;
import com.java110.dto.meter.MeterMachineSpecDto;
import com.java110.intf.meter.IMeterMachineChargeV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineDetailV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineSpecV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineV1InnerServiceSMO;
import com.java110.meter.factory.ISmartMeterFactoryAdapt;
import com.java110.meter.factory.tqdianbiao.TdDianBiaoUtil;
import com.java110.po.meter.MeterMachineDetailPo;
import com.java110.po.meter.MeterMachinePo;
import com.java110.po.meterMachineCharge.MeterMachineChargePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 电表预存
 */
@Service("hcPreFactoryAdaptImpl")
public class HcPreFactoryAdaptImpl implements ISmartMeterFactoryAdapt {
    private static Logger logger = LoggerFactory.getLogger(HcPreFactoryAdaptImpl.class);

    @Autowired
    private IMeterMachineSpecV1InnerServiceSMO meterMachineSpecV1InnerServiceSMOImpl;

    @Autowired
    private IMeterMachineDetailV1InnerServiceSMO meterMachineDetailV1InnerServiceSMOImpl;

    @Autowired
    private IMeterMachineV1InnerServiceSMO meterMachineV1InnerServiceSMOImpl;

    @Autowired
    private IMeterMachineChargeV1InnerServiceSMO meterMachineChargeV1InnerServiceSMOImpl;


    // todo 请求topic
    public static final String TOPIC_REQ = "hc/meter/request/{sn}";

    //todo 返回 topic
    public static final String TOPIC_RES = "hc/meter/response";

    public static final String SN = "{sn}";

    public static final String CMD_OPEN_METER = "openMeter"; //todo 开闸
    public static final String CMD_CLOSE_METER = "closeMeter"; //todo 关闸

    public static final String CMD_HEARTBEAT = "heartbeat"; //todo 心跳

    public static final String CMD_CHARGE_METER = "chargeMeter"; // todo 水电充值

    public static final String CMD_QUERY_METER_MONEY = "queryMeterMoney"; // todo 查询余额
    
    public static final String CI_ID = "102024011667160008";

    @Override
    public ResultVo requestRecharge(MeterMachineDto meterMachineDto, double degree, double money) {
        String taskId = GenerateCodeFactory.getGeneratorId("11");

        MeterMachineSpecDto meterMachineSpecDto = new MeterMachineSpecDto();
        meterMachineSpecDto.setMachineId(meterMachineDto.getMachineId());
        meterMachineSpecDto.setCommunityId(meterMachineDto.getCommunityId());
        meterMachineSpecDto.setSpecId(CI_ID);
        List<MeterMachineSpecDto> meterMachineSpecDtos = meterMachineSpecV1InnerServiceSMOImpl.queryMeterMachineSpecs(meterMachineSpecDto);
        if (ListUtil.isNull(meterMachineSpecDtos)) {
            return new ResultVo(ResultVo.CODE_ERROR, "未配置采集器ID");
        }

        JSONObject param = new JSONObject();
        param.put("cmd", CMD_CHARGE_METER);
        param.put("taskId", taskId);
        param.put("cId", meterMachineSpecDtos.get(0).getSpecValue());
        param.put("money", money);
        param.put("degree", degree);

        MqttFactory.publish(TOPIC_REQ.replace(SN, meterMachineDto.getAddress()), param.toJSONString());

        MeterMachineChargePo meterMachineChargePo = new MeterMachineChargePo();
        meterMachineChargePo.setChargeId(taskId);
        meterMachineChargePo.setMachineId(meterMachineDto.getMachineId());
        meterMachineChargePo.setCommunityId(meterMachineDto.getCommunityId());
        meterMachineChargePo.setChargeMoney(money + "");
        meterMachineChargePo.setChargeDegrees(degree + "");
        meterMachineChargePo.setState("W");
        meterMachineChargePo.setRemark("平台发起成功，等待拓强响应结果");
        int flag = meterMachineChargeV1InnerServiceSMOImpl.saveMeterMachineCharge(meterMachineChargePo);

        if (flag < 1) {
            throw new CmdException("保存数据失败");
        }

        return new ResultVo(ResultVo.CODE_OK, "请求已发送，等待电表反馈数据");
    }

    @Override
    public ResultVo requestRead(MeterMachineDto meterMachineDto) {


        String taskId = GenerateCodeFactory.getGeneratorId("11");
        MeterMachineSpecDto meterMachineSpecDto = new MeterMachineSpecDto();
        meterMachineSpecDto.setMachineId(meterMachineDto.getMachineId());
        meterMachineSpecDto.setCommunityId(meterMachineDto.getCommunityId());
        meterMachineSpecDto.setSpecId(CI_ID);
        List<MeterMachineSpecDto> meterMachineSpecDtos = meterMachineSpecV1InnerServiceSMOImpl.queryMeterMachineSpecs(meterMachineSpecDto);
        if (ListUtil.isNull(meterMachineSpecDtos)) {
            return new ResultVo(ResultVo.CODE_ERROR, "未配置采集器ID");
        }

        JSONObject param = new JSONObject();
        param.put("cmd", CMD_QUERY_METER_MONEY);
        param.put("taskId", taskId);
        param.put("cId", meterMachineSpecDtos.get(0).getSpecValue());

        MqttFactory.publish(TOPIC_REQ.replace(SN, meterMachineDto.getAddress()), param.toJSONString());

        List<MeterMachineDetailPo> meterMachineDetailPos = new ArrayList<>();
        MeterMachineDetailPo meterMachineDetailPo = new MeterMachineDetailPo();
        meterMachineDetailPo.setCommunityId(meterMachineDto.getCommunityId());
        meterMachineDetailPo.setDetailId(taskId);
        meterMachineDetailPo.setMachineId(meterMachineDto.getMachineId());
        meterMachineDetailPo.setDetailType(meterMachineDto.getMachineModel()); // 抄表
        meterMachineDetailPo.setCurDegrees(meterMachineDto.getCurDegrees());
        meterMachineDetailPo.setCurReadingTime(meterMachineDto.getCurReadingTime());
        meterMachineDetailPo.setPrestoreDegrees(meterMachineDto.getPrestoreDegrees());
        meterMachineDetailPo.setState(MeterMachineDetailDto.STATE_W);
        meterMachineDetailPos.add(meterMachineDetailPo);
        meterMachineDetailV1InnerServiceSMOImpl.saveMeterMachineDetails(meterMachineDetailPos);

        return new ResultVo(ResultVo.CODE_OK,"提交成功");
    }

    @Override
    public ResultVo requestReads(List<MeterMachineDto> meterMachineDtos) {
        return new ResultVo(ResultVo.CODE_OK,"提交成功");
    }

    @Override
    public ResultVo notifyReadData(String readData) {

        JSONObject paramIn = JSONObject.parseObject(readData);
        String cmd = paramIn.getString("cmd");
        String taskId = paramIn.getString("taskId");
        String machineCode = paramIn.getString("machineCode");

        Assert.hasLength(cmd, "未包含cmd");
        Assert.hasLength(taskId, "未包含taskId");
        Assert.hasLength(machineCode, "未包含machineCode");

        MeterMachineDto meterMachineDto = new MeterMachineDto();
        meterMachineDto.setAddress(machineCode);
        List<MeterMachineDto> meterMachineDtos = meterMachineV1InnerServiceSMOImpl.queryMeterMachines(meterMachineDto);

        Assert.listOnlyOne(meterMachineDtos, "未包含水电表");

        switch (cmd) {
            case CMD_HEARTBEAT: // todo 心跳
                heartbeatHc(cmd, taskId, machineCode, paramIn, meterMachineDtos.get(0));
                break;
            case CMD_OPEN_METER: //todo 开闸返回
                response(cmd, taskId, machineCode, paramIn);
                break;
            case CMD_CLOSE_METER: //todo 关闸反馈
                response(cmd, taskId, machineCode, paramIn);
                break;
            case CMD_CHARGE_METER: //todo 添加用户返回
                responseCharge(cmd, taskId, machineCode, paramIn);
                break;
            case CMD_QUERY_METER_MONEY: //todo 修改用户返回
                responseMoney(cmd, taskId, machineCode, paramIn,meterMachineDtos.get(0));
                break;
        }
        return new ResultVo(ResultVo.CODE_OK,"提交成功");
    }

    private void responseMoney(String cmd, String taskId, String machineCode, JSONObject paramIn,MeterMachineDto meterMachineDto) {

        MeterMachineDetailDto meterMachineDetailDto = new MeterMachineDetailDto();
        meterMachineDetailDto.setDetailId(taskId);
        meterMachineDetailDto.setState(MeterMachineDetailDto.STATE_W);
        List<MeterMachineDetailDto> meterMachineDetailDtos = meterMachineDetailV1InnerServiceSMOImpl.queryMeterMachineDetails(meterMachineDetailDto);
        if (ListUtil.isNull(meterMachineDetailDtos)) {
            throw new IllegalArgumentException("未查到查询明细");
        }
        MeterMachineDetailPo meterMachineDetailPo = new MeterMachineDetailPo();
        meterMachineDetailPo.setDetailId(meterMachineDetailDto.getDetailId());
        meterMachineDetailPo.setCurDegrees(paramIn.getString("money"));
        meterMachineDetailPo.setState(MeterMachineDetailDto.STATE_C);
        meterMachineDetailPo.setPrestoreDegrees(paramIn.getString("money"));
        meterMachineDetailPo.setCurReadingTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        meterMachineDetailV1InnerServiceSMOImpl.updateMeterMachineDetail(meterMachineDetailPo);

        MeterMachinePo meterMachinePo = new MeterMachinePo();
        meterMachinePo.setMachineId(meterMachineDto.getMachineId());
        meterMachinePo.setCurDegrees(paramIn.getString("money"));
        meterMachinePo.setPrestoreDegrees(paramIn.getString("money"));
        meterMachinePo.setCurReadingTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        meterMachineV1InnerServiceSMOImpl.updateMeterMachine(meterMachinePo);
    }

    private void responseCharge(String cmd, String taskId, String machineCode, JSONObject paramIn) {

        MeterMachineChargePo meterMachineChargePo = new MeterMachineChargePo();
        meterMachineChargePo.setChargeId(taskId);
        if(paramIn.getIntValue("code") == 0) {
            meterMachineChargePo.setState(MeterMachineDetailDto.STATE_C);
        }else{
            meterMachineChargePo.setState(MeterMachineDetailDto.STATE_F);
        }
        int flag = meterMachineChargeV1InnerServiceSMOImpl.updateMeterMachineCharge(meterMachineChargePo);

        if (flag < 1) {
            throw new CmdException("保存数据失败");
        }
    }

    private void response(String cmd, String taskId, String machineCode, JSONObject paramIn) {

        MeterMachineDetailPo meterMachineDetailPo = new MeterMachineDetailPo();
        meterMachineDetailPo.setDetailId(taskId);
        meterMachineDetailPo.setRemark("拉合闸"+paramIn.getString("msg"));
        if(paramIn.getIntValue("code") == 0) {
            meterMachineDetailPo.setState(MeterMachineDetailDto.STATE_C);
        }else{
            meterMachineDetailPo.setState(MeterMachineDetailDto.STATE_F);
        }
        meterMachineDetailV1InnerServiceSMOImpl.updateMeterMachineDetail(meterMachineDetailPo);
    }

    /**
     * 电表心跳
     *
     * @param cmd
     * @param taskId
     * @param machineCode
     * @param paramIn
     * @param meterMachineDto
     */
    private void heartbeatHc(String cmd, String taskId, String machineCode, JSONObject paramIn, MeterMachineDto meterMachineDto) {

        MeterMachinePo meterMachinePo = new MeterMachinePo();
        meterMachinePo.setMachineId(meterMachineDto.getMachineId());
        meterMachinePo.setHeartbeatTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        meterMachineV1InnerServiceSMOImpl.updateMeterMachine(meterMachinePo);

        JSONObject paramOut = new JSONObject();
        paramOut.put("cmd", cmd);
        paramOut.put("taskId", taskId);
        paramOut.put("code", 0);
        paramOut.put("msg", "处理成功");
        paramOut.put("machineCode", machineCode);
        MqttFactory.publish(TOPIC_REQ.replace(SN, machineCode), paramOut.toJSONString());

    }

    @Override
    public void queryMeterMachineState(MeterMachineDto meterMachineDto) {

    }

    @Override
    public ResultVo switchControl(MeterMachineDto meterMachineDto, String controlType) {
        List<Map<String, Object>> req = new ArrayList<>();
        MeterMachineSpecDto meterMachineSpecDto = new MeterMachineSpecDto();
        meterMachineSpecDto.setMachineId(meterMachineDto.getMachineId());
        meterMachineSpecDto.setCommunityId(meterMachineDto.getCommunityId());
        meterMachineSpecDto.setSpecId(CI_ID);
        List<MeterMachineSpecDto> meterMachineSpecDtos = meterMachineSpecV1InnerServiceSMOImpl.queryMeterMachineSpecs(meterMachineSpecDto);
        if (ListUtil.isNull(meterMachineSpecDtos)) {
            return new ResultVo(ResultVo.CODE_ERROR, "未配置采集器ID");
        }
        String taskId = GenerateCodeFactory.getGeneratorId("11");

        if ("ON".equals(controlType)) {
            JSONObject param = new JSONObject();
            param.put("cmd", CMD_OPEN_METER);
            param.put("taskId", taskId);
            param.put("cId", meterMachineSpecDtos.get(0).getSpecValue());
            MqttFactory.publish(TOPIC_REQ.replace(SN, meterMachineDto.getAddress()), param.toJSONString());

        } else if ("OFF".equals(controlType)) {
            JSONObject param = new JSONObject();
            param.put("cmd", CMD_CLOSE_METER);
            param.put("taskId", taskId);
            param.put("cId", meterMachineSpecDtos.get(0).getSpecValue());
            MqttFactory.publish(TOPIC_REQ.replace(SN, meterMachineDto.getAddress()), param.toJSONString());
        }

        MeterMachineDetailPo meterMachineDetailPo = new MeterMachineDetailPo();
        meterMachineDetailPo.setDetailId(taskId);
        meterMachineDetailPo.setMachineId(meterMachineDto.getMachineId());
        meterMachineDetailPo.setDetailType(controlType);
        meterMachineDetailPo.setPrestoreDegrees(meterMachineDto.getPrestoreDegrees());
        meterMachineDetailPo.setCurDegrees(meterMachineDto.getCurDegrees());
        meterMachineDetailPo.setCurReadingTime(meterMachineDto.getCurReadingTime());
        meterMachineDetailPo.setCommunityId(meterMachineDto.getCommunityId());
        meterMachineDetailPo.setRemark("拉合闸");
        meterMachineDetailPo.setState(MeterMachineDetailDto.STATE_W);
        meterMachineDetailV1InnerServiceSMOImpl.saveMeterMachineDetail(meterMachineDetailPo);

        return new ResultVo(ResultVo.CODE_OK, "请求已发送，等待电表反馈");
    }

    @Override
    public ResultVo cleanControl(MeterMachineDto meterMachineDto) {
        return null;
    }
}
