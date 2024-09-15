package com.java110.accessControl.manufactor.adapt.attendance;

import com.alibaba.fastjson.JSONObject;
import com.java110.accessControl.manufactor.AbstractAttendanceManufactorAdapt;
import com.java110.bean.ResultVo;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ImageUtils;
import com.java110.core.utils.StringUtil;
import com.java110.dto.attendanceMachine.AttendanceMachineDto;
import com.java110.intf.accessControl.IAttendanceCheckinV1InnerServiceSMO;
import com.java110.intf.accessControl.IAttendanceMachineLogV1InnerServiceSMO;
import com.java110.po.attendanceStaff.AttendanceStaffPo;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("hikAttendanceProcessAdapt")
public class HikAttendanceProcessAdapt extends AbstractAttendanceManufactorAdapt {

    private static Logger logger = LoggerFactory.getLogger(HikAttendanceProcessAdapt.class);

    @Autowired
    private IAttendanceCheckinV1InnerServiceSMO attendanceCheckinV1InnerServiceSMOImpl;

    @Autowired
    private IAttendanceMachineLogV1InnerServiceSMO attendanceMachineLogV1InnerServiceSMOImpl;

    public static final String ACTION = "HIK_ATTENDANCE";
    public static final String TOPIC_REQ = "mgateway/request";

    public static final String TOPIC_RES = "mgateway/response/hikattendance";

    public static final String CMD_HEARTBEAT = "heartbeat"; //todo 心跳指令
    public static final String CMD_REBOOT = "reboot"; //todo 心跳指令
    public static final String CMD_OPEN_DOOR = "openDoor"; //todo 心跳指令

    public static final String CMD_ADD_USER = "addUser";// todo 添加用户；
    public static final String CMD_UPDATE_USER = "updateUser";// todo 修改用户；
    public static final String CMD_DELETE_USER = "deleteUser";// todo 删除用户；

    public static final String CMD_FACE_RESULT = "faceResult"; // todo 开门记录

    @Override
    public boolean addUser(AttendanceMachineDto attendanceMachineDto, AttendanceStaffPo attendanceStaffPo) {
        String taskId = GenerateCodeFactory.getGeneratorId("11");
        JSONObject param = new JSONObject();
        param.put("cmd", CMD_ADD_USER);
        param.put("action", ACTION);
        param.put("sn", attendanceMachineDto.getMachineCode());

        param.put("taskId", taskId);
        param.put("userId", attendanceStaffPo.getStaffId());
        param.put("userName", attendanceStaffPo.getStaffName());
        param.put("pass", "");
        String faceData = ImageUtils.getBase64ByImgUrl(attendanceStaffPo.getFacePath());
        if (!StringUtil.isEmpty(faceData)) {
            faceData = faceData.replaceAll("\n", "");
        }
        param.put("faceData", faceData);


        MqttFactory.publish(TOPIC_REQ, param.toJSONString());
        saveLog(taskId, attendanceMachineDto.getMachineId(), TOPIC_REQ,
                attendanceMachineDto.getCommunityId(),
                param.toJSONString(), attendanceStaffPo.getStaffId(), attendanceStaffPo.getStaffName());
        return true;
    }

    @Override
    public boolean updateUser(AttendanceMachineDto attendanceMachineDto, AttendanceStaffPo attendanceStaffPo) {
        deleteUser(attendanceMachineDto, attendanceStaffPo);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return addUser(attendanceMachineDto, attendanceStaffPo);
    }

    @Override
    public boolean deleteUser(AttendanceMachineDto attendanceMachineDto, AttendanceStaffPo attendanceStaffPo) {
        String taskId = GenerateCodeFactory.getGeneratorId("11");
        JSONObject param = new JSONObject();
        param.put("cmd", CMD_DELETE_USER);
        param.put("action", ACTION);
        param.put("sn", attendanceMachineDto.getMachineCode());
        param.put("pass", "");
        param.put("taskId", taskId);
        param.put("userId", attendanceStaffPo.getStaffId());

        MqttFactory.publish(TOPIC_REQ, param.toJSONString());
        saveLog(taskId, attendanceMachineDto.getMachineId(), TOPIC_REQ,
                attendanceMachineDto.getCommunityId(), param.toJSONString(),
                attendanceStaffPo.getStaffId(), attendanceStaffPo.getStaffName());
        return true;
    }

    @Override
    public boolean restartMachine(AttendanceMachineDto attendanceMachineDto) {
        String taskId = GenerateCodeFactory.getGeneratorId("11");
        JSONObject param = new JSONObject();
        param.put("cmd", CMD_REBOOT);
        param.put("action", ACTION);
        param.put("sn", attendanceMachineDto.getMachineCode());
        param.put("pass", "");
        param.put("taskId", taskId);
        MqttFactory.publish(TOPIC_REQ, param.toJSONString());
        saveLog(taskId, attendanceMachineDto.getMachineId(), TOPIC_REQ,
                attendanceMachineDto.getCommunityId(), param.toJSONString(),
                "-1", "无");
        return true;
    }

    @Override
    public String attendanceResult(String topic, String param) {

        JSONObject paramIn = JSONObject.parseObject(param);
        String cmd = paramIn.getString("cmd");
        String taskId = paramIn.getString("taskId");
        String machineCode = paramIn.getString("sn");

        Assert.hasLength(cmd, "未包含cmd");
        Assert.hasLength(taskId, "未包含taskId");
        Assert.hasLength(machineCode, "未包含sn");

        switch (cmd) {
            case CMD_HEARTBEAT: // todo 心跳
                heartbeat(machineCode);
                break;
            case CMD_REBOOT: //todo 重启返回
                doCmdResult(cmd, taskId, machineCode, paramIn);
                break;
            case CMD_ADD_USER: //todo 添加用户返回
                doCmdResult(cmd, taskId, machineCode, paramIn);
                break;
            case CMD_UPDATE_USER: //todo 修改用户返回
                doCmdResult(cmd, taskId, machineCode, paramIn);
                break;
            case CMD_DELETE_USER: //todo 删除用户返回
                doCmdResult(cmd, taskId, machineCode, paramIn);
                break;
            case CMD_FACE_RESULT: //todo 开门记录
                clockIn(cmd, taskId, machineCode, paramIn);
                break;
        }
        return null;
    }

    private void doCmdResult(String cmd, String taskId, String machineCode, JSONObject paramIn) {
        cmdResult(paramIn.getIntValue("code"), paramIn.getString("msg"), taskId, machineCode);
    }

    /**
     * 打卡记录
     *
     * @param paramJson
     */
    private void clockIn(String cmd, String taskId, String machineCode, JSONObject paramJson) {
        logger.debug("考勤结果,{}", paramJson.toJSONString());
        String staffId = paramJson.getString("userId");
        String staffName = paramJson.getString("userName");
        JSONObject dataObj = paramJson.getJSONObject("match");

        if (StringUtil.isEmpty(dataObj.getString("staffId"))) {
            return;
        }
        String images = paramJson.getString("faceData");

        try {
            ResultVo resultDto = propertyCheckIn(staffId,images,machineCode);
            logger.debug("考勤结果,{}", JSONObject.toJSONString(resultDto));
            JSONObject paramOut = new JSONObject();
            paramOut.put("cmd", cmd);
            paramOut.put("taskId", taskId);
            paramOut.put("code", 0);
            paramOut.put("msg", "处理成功");
            paramOut.put("machineCode", machineCode);
            MqttFactory.publish(TOPIC_REQ, paramOut.toJSONString());

        } catch (Exception e) {
            logger.error("考勤失败", e);
        }
    }
}
