package com.java110.accessControl.manufactor.adapt.attendance;

import com.alibaba.fastjson.JSONObject;
import com.java110.accessControl.manufactor.AbstractAttendanceManufactorAdapt;
import com.java110.bean.ResultVo;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ImageUtils;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.accessControlLog.AccessControlLogDto;
import com.java110.dto.attendanceMachine.AttendanceMachineDto;
import com.java110.dto.attendanceMachineLog.AttendanceMachineLogDto;
import com.java110.dto.attendanceStaff.AttendanceStaffDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.accessControl.IAttendanceMachineLogV1InnerServiceSMO;
import com.java110.intf.accessControl.IAttendanceMachineV1InnerServiceSMO;
import com.java110.intf.accessControl.IAttendanceStaffV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.attendanceMachineLog.AttendanceMachineLogPo;
import com.java110.po.attendanceStaff.AttendanceStaffPo;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 官方考勤协议
 */
@Service("hcAttendanceProcessAdapt")
public class HcAttendanceProcessAdapt extends AbstractAttendanceManufactorAdapt {

    private static Logger logger = LoggerFactory.getLogger(HcAttendanceProcessAdapt.class);
    public static final String TOPIC_REQ = "hc/attendance/request/{sn}";

    //todo 返回 topic
    public static final String TOPIC_RES = "hc/attendance/response";

    public static final String SN = "{sn}";

    public static final String CMD_HEARTBEAT = "heartbeat"; //todo 心跳指令
    public static final String CMD_REBOOT = "reboot"; //todo 心跳指令
    public static final String CMD_OPEN_DOOR = "openDoor"; //todo 心跳指令

    public static final String CMD_ADD_USER = "addUser";// todo 添加用户；
    public static final String CMD_UPDATE_USER = "updateUser";// todo 修改用户；
    public static final String CMD_DELETE_USER = "deleteUser";// todo 删除用户；

    public static final String CMD_FACE_RESULT = "faceResult"; // todo 开门记录

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IAttendanceMachineV1InnerServiceSMO attendanceMachineV1InnerServiceSMOImpl;

    @Autowired
    private IAttendanceMachineLogV1InnerServiceSMO attendanceMachineLogV1InnerServiceSMOImpl;

    @Autowired
    private IAttendanceStaffV1InnerServiceSMO attendanceStaffV1InnerServiceSMOImpl;

    @Override
    public boolean initMachine(AttendanceMachineDto attendanceMachineDto) {
        MqttFactory.subscribe("hc/attendance/response");
        return super.initMachine(attendanceMachineDto);
    }

    @Override
    public boolean addUser(AttendanceMachineDto attendanceMachineDto, AttendanceStaffPo attendanceStaffPo) {
        String taskId = GenerateCodeFactory.getGeneratorId("11");
        JSONObject param = new JSONObject();
        param.put("cmd", CMD_ADD_USER);
        param.put("taskId", taskId);
        param.put("userId", attendanceStaffPo.getStaffId());
        param.put("userName", attendanceStaffPo.getStaffName());
        param.put("faceData", attendanceStaffPo.getFacePath());

        UserDto userDto = new UserDto();
        userDto.setUserId(attendanceStaffPo.getStaffId());
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        if (!ListUtil.isNull(userDtos)) {
            param.put("tel", userDtos.get(0).getTel());
        }

        MqttFactory.publish(TOPIC_REQ.replace(SN, attendanceMachineDto.getMachineCode()), param.toJSONString());
        saveLog(taskId, attendanceMachineDto.getMachineId(), TOPIC_REQ.replace(SN, attendanceMachineDto.getMachineCode()) + "/" + CMD_ADD_USER,
                attendanceMachineDto.getCommunityId(), param.toJSONString(), attendanceStaffPo.getStaffId(), attendanceStaffPo.getStaffName());
        return true;
    }

    @Override
    public boolean updateUser(AttendanceMachineDto attendanceMachineDto, AttendanceStaffPo attendanceStaffPo) {
        String taskId = GenerateCodeFactory.getGeneratorId("11");
        JSONObject param = new JSONObject();
        param.put("cmd", CMD_UPDATE_USER);
        param.put("taskId", taskId);
        param.put("userId", attendanceStaffPo.getStaffId());
        param.put("userName", attendanceStaffPo.getStaffName());
        param.put("faceData", attendanceStaffPo.getFacePath());

        UserDto userDto = new UserDto();
        userDto.setUserId(attendanceStaffPo.getStaffId());
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        if (!ListUtil.isNull(userDtos)) {
            param.put("tel", userDtos.get(0).getTel());
        }

        MqttFactory.publish(TOPIC_REQ.replace(SN, attendanceMachineDto.getMachineCode()), param.toJSONString());
        saveLog(taskId, attendanceMachineDto.getMachineId(), TOPIC_REQ.replace(SN, attendanceMachineDto.getMachineCode()) + "/" + CMD_UPDATE_USER,
                attendanceMachineDto.getCommunityId(), param.toJSONString(), attendanceStaffPo.getStaffId(), attendanceStaffPo.getStaffName());
        return true;
    }

    @Override
    public boolean deleteUser(AttendanceMachineDto attendanceMachineDto, AttendanceStaffPo attendanceStaffPo) {
        String taskId = GenerateCodeFactory.getGeneratorId("11");
        JSONObject param = new JSONObject();
        param.put("cmd", CMD_DELETE_USER);
        param.put("taskId", taskId);
        param.put("userId", attendanceStaffPo.getStaffId());

        MqttFactory.publish(TOPIC_REQ.replace(SN, attendanceMachineDto.getMachineCode()), param.toJSONString());
        saveLog(taskId, attendanceMachineDto.getMachineId(), TOPIC_REQ.replace(SN, attendanceMachineDto.getMachineCode()) + "/" + CMD_DELETE_USER,
                attendanceMachineDto.getCommunityId(), param.toJSONString(), attendanceStaffPo.getStaffId(), attendanceStaffPo.getStaffName());


        return true;
    }

    @Override
    public boolean restartMachine(AttendanceMachineDto attendanceMachineDto) {
        String taskId = GenerateCodeFactory.getGeneratorId("11");
        JSONObject param = new JSONObject();
        param.put("cmd", CMD_REBOOT);
        param.put("taskId", taskId);
        MqttFactory.publish(TOPIC_REQ.replace(SN, attendanceMachineDto.getMachineCode()), param.toJSONString());
        saveLog(taskId, attendanceMachineDto.getMachineId(), TOPIC_REQ.replace(SN, attendanceMachineDto.getMachineCode()) + "/" + CMD_REBOOT,
                attendanceMachineDto.getCommunityId(), param.toJSONString(), "-1", "无");
        return true;
    }

    @Override
    public String attendanceResult(String topic, String data) {

        JSONObject paramIn = JSONObject.parseObject(data);
        String cmd = paramIn.getString("cmd");
        String taskId = paramIn.getString("taskId");
        String machineCode = paramIn.getString("machineCode");

        Assert.hasLength(cmd, "未包含cmd");
        Assert.hasLength(taskId, "未包含taskId");
        Assert.hasLength(machineCode, "未包含machineCode");

        switch (cmd) {
            case CMD_HEARTBEAT: // todo 心跳
                heartbeat(machineCode);
                break;
            case CMD_REBOOT: //todo 重启返回
                response(cmd, taskId, machineCode, paramIn);
                break;
            case CMD_ADD_USER: //todo 添加用户返回
                response(cmd, taskId, machineCode, paramIn);
                break;
            case CMD_UPDATE_USER: //todo 修改用户返回
                response(cmd, taskId, machineCode, paramIn);
                break;
            case CMD_DELETE_USER: //todo 删除用户返回
                response(cmd, taskId, machineCode, paramIn);
                break;
            case CMD_FACE_RESULT: //todo 开门记录
                clockIn(cmd, taskId, machineCode, paramIn);
                break;
        }
        return SUCCESS;
    }

    private void response(String cmd, String taskId, String machineCode, JSONObject paramIn) {
        AttendanceMachineDto attendanceMachineDto = new AttendanceMachineDto();
        attendanceMachineDto.setMachineCode(machineCode);
        List<AttendanceMachineDto> attendanceMachineDtos = attendanceMachineV1InnerServiceSMOImpl.queryAttendanceMachines(attendanceMachineDto);

        if (ListUtil.isNull(attendanceMachineDtos)) {
            return;
        }
        //todo 更新下发日志
        AttendanceMachineLogDto attendanceMachineLogDto = new AttendanceMachineLogDto();
        attendanceMachineLogDto.setLogId(taskId);
        attendanceMachineLogDto.setState(AccessControlLogDto.STATE_REQ);
        List<AttendanceMachineLogDto> attendanceMachineLogDtos = attendanceMachineLogV1InnerServiceSMOImpl.queryAttendanceMachineLogs(attendanceMachineLogDto);
        if (ListUtil.isNull(attendanceMachineLogDtos)) {
            return;
        }

        String state = AccessControlLogDto.STATE_RES;
        if (paramIn.getIntValue("code") != ResultVo.CODE_OK) {
            state = AccessControlLogDto.STATE_FAIL;
        }


        AttendanceMachineLogPo attendanceMachineLogPo = new AttendanceMachineLogPo();
        attendanceMachineLogPo.setLogId(attendanceMachineLogDtos.get(0).getLogId());
        attendanceMachineLogPo.setState(state);
        attendanceMachineLogPo.setResParam(paramIn.getString("msg"));
        attendanceMachineLogV1InnerServiceSMOImpl.updateAttendanceMachineLog(attendanceMachineLogPo);

        //todo 更新人员日志

        AttendanceStaffDto attendanceStaffDto = new AttendanceStaffDto();
        attendanceStaffDto.setStaffId(attendanceMachineLogDtos.get(0).getStaffId());
        attendanceStaffDto.setMachineId(attendanceMachineDtos.get(0).getMachineId());
        attendanceStaffDto.setState(AccessControlFaceDto.STATE_WAIT);
        List<AttendanceStaffDto> attendanceStaffDtos = attendanceStaffV1InnerServiceSMOImpl.queryAttendanceStaffs(attendanceStaffDto);
        if (ListUtil.isNull(attendanceStaffDtos)) {
            return;
        }

        state = AttendanceStaffDto.STATE_COMPLETE;
        if (paramIn.getIntValue("code") != ResultVo.CODE_OK) {
            state = AttendanceStaffDto.STATE_FAIL;
        }


        AttendanceStaffPo attendanceStaffPo = new AttendanceStaffPo();
        attendanceStaffPo.setMsId(attendanceStaffDtos.get(0).getMsId());
        attendanceStaffPo.setState(state);
        attendanceStaffPo.setMessage(paramIn.getString("msg"));
        attendanceStaffV1InnerServiceSMOImpl.updateAttendanceStaff(attendanceStaffPo);
    }


    /**
     * 打卡记录
     *
     * @param paramIn
     */
    private void clockIn(String cmd, String taskId, String machineCode, JSONObject paramIn) {
        logger.debug("考勤结果,{}", paramIn.toJSONString());
        try {
            String staffId = paramIn.getString("userId");
            ResultVo resultDto = propertyCheckIn(staffId, paramIn.getString("faceData"), machineCode);
            logger.debug("考勤结果,{}", JSONObject.toJSONString(resultDto));
            JSONObject result = new JSONObject();
            result.put("reply", "ACK");
            result.put("cmd", "face");
            result.put("code", 0);
            JSONObject tts = new JSONObject();
            tts.put("text", resultDto.getMsg());
            result.put("tts", tts);
            MqttFactory.publish(TOPIC_REQ.replace(SN, machineCode), result.toJSONString());

            String viewText = "{\n" +
                    " \"cmd\": \"text display\",\n" +
                    " \"coding_type\":\"utf8\",\n" +
                    " \"text_list\":[\n" +
                    "  {\n" +
                    "   \"position\":{\n" +
                    "    \"x\":40,\n" +
                    "    \"y\":800\n" +
                    "   },\n" +
                    "   \"alive_time\": 3000,\n" +
                    "   \"font_size\": 50,\n" +
                    "   \"font_spacing\": 1,\n" +
                    "   \"font_color\": \"0xff00ff00\",\n" +
                    "   \"text\":\"" + paramIn.getString("userName") + "\"\n" +
                    "  },\n" +
                    "  {\n" +
                    "   \"position\":{\n" +
                    "    \"x\":10,\n" +
                    "    \"y\":850\n" +
                    "   },\n" +
                    "   \"alive_time\": 3000,\n" +
                    "   \"font_size\": 50,\n" +
                    "   \"font_spacing\": 1,\n" +
                    "   \"font_color\": \"0xff00ff00\",\n" +
                    "   \"text\":\"" + resultDto.getMsg() + "\"\n" +
                    "  }\n" +
                    " ]\n" +
                    "}";
            MqttFactory.publish(TOPIC_REQ.replace(SN, machineCode), viewText);

        } catch (Exception e) {
            logger.error("考勤失败", e);
        }
    }
}
