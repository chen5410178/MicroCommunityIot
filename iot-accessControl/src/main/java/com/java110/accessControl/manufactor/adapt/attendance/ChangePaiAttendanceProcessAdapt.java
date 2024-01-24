package com.java110.accessControl.manufactor.adapt.attendance;

import com.alibaba.fastjson.JSONObject;
import com.java110.accessControl.manufactor.AbstractAttendanceManufactorAdapt;
import com.java110.bean.ResultVo;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.core.utils.ImageUtils;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.attendanceMachine.AttendanceMachineDto;
import com.java110.po.accessControlFace.AccessControlFacePo;
import com.java110.po.attendanceStaff.AttendanceStaffPo;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("changePaiAttendanceProcessAdapt")
public class ChangePaiAttendanceProcessAdapt extends AbstractAttendanceManufactorAdapt {

    private static Logger logger = LoggerFactory.getLogger(ChangePaiAttendanceProcessAdapt.class);
    //发布
    private static final String REQUEST_FACE = "changpai/topic/face/manage/request/SN";
    //接受
    private static final String RESPONSE_FACE = "changpai/topic/face/manage/response/";

    private static final String UPLOAD_RESPONSE_FACE = "changpai/topic/face/capture/request/";

    //心跳
    private static final String HEARTBEAT_FACE = "changpai/topic/face/heart/request/";

    public static final Map<String, String> resultInfo = new HashMap<>();


    private static final String SN = "SN";


    static {
        resultInfo.put("0", "成功，人员已添加或修改");
        resultInfo.put("2", "设备还未实现此功能（早于2020年8月21日的版本）");
        resultInfo.put("3", "给定的数据不正确（如果出现这个响应码，则会有desc对具体错误进行文字描述）");
        resultInfo.put("6", "资源不足。设备内存不够");
        resultInfo.put("16", "人脸重复（在开启了禁止重复人脸注册时可能出现）");
        resultInfo.put("20", "数据录入达到上限。设备人员已满");
        resultInfo.put("21", "记录已经存在。请求消息体中upload_mode为1，而设备已经存在对应id的用户");
        resultInfo.put("22", "记录不存在。请求消息体中upload_mode为2，而设备中不存在对应id的用户");
        resultInfo.put("25", "提取人脸失败。图像中无人脸");
        resultInfo.put("35", "图像解码失败。给定图像不为jpg或png");
        resultInfo.put("36", "图像太大。JPG文件不能大于10M");
        resultInfo.put("37", "归一化失败。只出现在给了错误的normal_image数据");
        resultInfo.put("38", "人脸尺寸太小");
        resultInfo.put("39", "人脸质量太差");
        resultInfo.put("40", "图像中有多个人脸");
        resultInfo.put("41", "图像中人脸不完整");
    }

    @Override
    public boolean initMachine(AttendanceMachineDto attendanceMachineDto) {
        MqttFactory.subscribe("changpai/topic/face/capture/request/" + attendanceMachineDto.getMachineCode().trim());
        MqttFactory.subscribe("changpai/topic/face/manage/response/" + attendanceMachineDto.getMachineCode().trim());
        MqttFactory.subscribe("changpai/topic/face/heart/request/" + attendanceMachineDto.getMachineCode().trim());
        return super.initMachine(attendanceMachineDto);
    }

    @Override
    public boolean addUser(AttendanceMachineDto attendanceMachineDto, AttendanceStaffPo attendanceStaffPo) {
        JSONObject param = new JSONObject();
        param.put("cmd", "upload person");
        param.put("id", attendanceStaffPo.getStaffId());
        param.put("name", attendanceStaffPo.getStaffName());
        param.put("role", 1);
        param.put("kind", 0);

        String faceData = ImageUtils.getBase64ByImgUrl(attendanceStaffPo.getFacePath());
        if (!StringUtil.isEmpty(faceData)) {
            faceData = faceData.replaceAll("\n", "");
        }
        param.put("reg_image", faceData);

        MqttFactory.publish(REQUEST_FACE.replace(SN, attendanceMachineDto.getMachineCode()), param.toJSONString());

        saveLog("", attendanceMachineDto.getMachineId(), REQUEST_FACE.replace(SN, attendanceMachineDto.getMachineCode()) + "/uploadperson",
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
        JSONObject param = new JSONObject();
        param.put("cmd", "delete person(s)");
        param.put("flag", -1);
        param.put("id", attendanceStaffPo.getStaffId());

        MqttFactory.publish(REQUEST_FACE.replace(SN, attendanceMachineDto.getMachineCode()), param.toJSONString());
        saveLog("", attendanceMachineDto.getMachineId(), REQUEST_FACE.replace(SN, attendanceMachineDto.getMachineCode()) + "/deleteperson",
                attendanceMachineDto.getCommunityId(), param.toJSONString(),
                attendanceStaffPo.getStaffId(), attendanceStaffPo.getStaffName());

        return true;
    }

    @Override
    public boolean restartMachine(AttendanceMachineDto attendanceMachineDto) {
        JSONObject param = JSONObject.parseObject("{\n" +
                "  \"cmd\": \"reboot\"\n" +
                "}");
        MqttFactory.publish(REQUEST_FACE.replace(SN, attendanceMachineDto.getMachineCode()), param.toJSONString());
        saveLog(GenerateCodeFactory.getGeneratorId("11"), attendanceMachineDto.getMachineId(), REQUEST_FACE.replace(SN, attendanceMachineDto.getMachineCode()) + "/reboot",
                attendanceMachineDto.getCommunityId(), param.toJSONString(), "-1", "无");
        return true;
    }

    @Override
    public String attendanceResult(String topic, String data) {
        JSONObject param = JSONObject.parseObject(data);

        String cmd = param.getString("cmd");

        if (topic.startsWith(HEARTBEAT_FACE)) {
            heartbeat(param.getString("device_sn"));
            return SUCCESS;
        }
        if ("face".equals(cmd)) {
            clockIn(param);
            return SUCCESS;
        }

        if (topic.startsWith(RESPONSE_FACE)) {
            doCmdResult(JSONObject.parseObject(data));
        }
        return SUCCESS;
    }


    /**
     * 打卡记录
     *
     * @param paramJson
     */
    private void clockIn(JSONObject paramJson) {
        logger.debug("考勤结果,{}", paramJson.toJSONString());
        if(!paramJson.containsKey("match")){
            return ;
        }
        JSONObject dataObj = paramJson.getJSONObject("match");

        if (!dataObj.containsKey("person_id") || StringUtil.isEmpty(dataObj.getString("person_id"))) {
            return;
        }

        String machineCode = paramJson.getString("device_sn");

        try {
            String staffId = dataObj.getString("person_id");
            ResultVo resultDto = propertyCheckIn(staffId,dataObj.getString("image"),machineCode);
            logger.debug("考勤结果,{}", JSONObject.toJSONString(resultDto));
            JSONObject result = new JSONObject();
            result.put("reply", "ACK");
            result.put("cmd", "face");
            result.put("code", 0);
            result.put("sequence_no", paramJson.getString("sequence_no"));
            result.put("cap_time", paramJson.getString("cap_time"));
            JSONObject tts = new JSONObject();
            tts.put("text",  resultDto.getMsg());
            result.put("tts", tts);
            MqttFactory.publish(RESPONSE_FACE + paramJson.getString("device_sn"), result.toJSONString());

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
                    "   \"text\":\"" + dataObj.getString("person_name") + "\"\n" +
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
            MqttFactory.publish(REQUEST_FACE.replace(SN, paramJson.getString("device_sn")), viewText);

        } catch (Exception e) {
            logger.error("考勤失败", e);
        }
    }


    private void doCmdResult(JSONObject resultCmd) {

        String personId = resultCmd.getString("id");
        String cmd = resultCmd.getString("cmd");
        String machineCode = resultCmd.getString("device_sn");
        if (StringUtil.isEmpty(cmd)) {
            return;
        }

        String logAction = "";

        if (cmd.startsWith("delete")) {
            logAction = REQUEST_FACE.replace(SN, machineCode) + "/deleteperson";
        } else if (cmd.startsWith("upload")) {
            logAction = REQUEST_FACE.replace(SN, machineCode) + "/uploadperson";
        } else if (cmd.startsWith("gpio")) {
            //{"cmd":"gpio control","code":0,"device_sn":"0123E0-9EED8D-E9D0EE","reply":"ACK"}
            logAction = REQUEST_FACE.replace(SN, machineCode) + "/openDoor";
        } else if (cmd.startsWith("reboot")) {
            logAction = REQUEST_FACE.replace(SN, machineCode) + "/reboot";
        }
        System.out.println("logAction=" + logAction + ",cmd=" + cmd + ",machineCode=" + machineCode);
        if (StringUtil.isEmpty(logAction)) {
            return;
        }
        int code = -1;
        if (!resultCmd.containsKey("code")) {
            code = -1;
        } else {
            code = resultCmd.getIntValue("code");
        }
        String msg = resultCmd.getString("reply") + "说明：" + resultInfo.get(code + "") + "," + resultCmd.getString("desc");
        cmdResult(code, msg, logAction, machineCode, personId);

    }
}
