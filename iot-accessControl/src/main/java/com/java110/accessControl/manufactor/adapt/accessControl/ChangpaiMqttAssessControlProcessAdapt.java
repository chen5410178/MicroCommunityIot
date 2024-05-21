package com.java110.accessControl.manufactor.adapt.accessControl;

import com.alibaba.fastjson.JSONObject;
import com.java110.accessControl.manufactor.AbstractAccessControlManufactorAdapt;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControl.AccessControlOweFeeDto;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.po.accessControlFace.AccessControlFacePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 厂拍 门禁设备 Mqtt 方式
 */
@Service("changpaiMqttAssessControlProcessAdapt")
public class ChangpaiMqttAssessControlProcessAdapt extends AbstractAccessControlManufactorAdapt {

    private static Logger logger = LoggerFactory.getLogger(ChangpaiMqttAssessControlProcessAdapt.class);

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    public static final long START_TIME = new Date().getTime() - 1000 * 60 * 60;
    public static final long END_TIME = new Date().getTime() + 1000 * 60 * 60 * 24 * 365;


    //发布
    private static final String REQUEST_FACE = "changpaimenjin/topic/face/manage/request/SN";
    //接受
    private static final String RESPONSE_FACE = "changpaimenjin/topic/face/manage/response/";


    private static final String UPLOAD_RESPONSE_FACE = "changpaimenjin/topic/face/capture/response/";


    //心跳
    private static final String HEARTBEAT_FACE = "changpaimenjin/topic/face/heart/request/";

    public static final String CACHE_CHANGPAI = "_CACHE_CHANGPAIMENGJIN";

    public static final String SN = "SN";

    public static final String FACE_URL = "ACCESS_CONTROL_FACE_URL";

    public static final String FACE_RESULT = "face_result";

    //图片后缀
    public static final String IMAGE_SUFFIX = ".jpg";

    public static final Map<String, String> resultInfo = new HashMap<>();

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
    public boolean initMachine(AccessControlDto machineDto) {
        MqttFactory.subscribe("changpaimenjin/topic/face/capture/request/" + machineDto.getMachineCode());
        MqttFactory.subscribe("changpaimenjin/topic/face/manage/response/" + machineDto.getMachineCode());
        MqttFactory.subscribe("changpaimenjin/topic/face/heart/request/" + machineDto.getMachineCode());
        return true;
    }

    @Override
    public boolean addUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        JSONObject param = new JSONObject();
        param.put("cmd", "upload person");
        param.put("id", accessControlFacePo.getPersonId());
        param.put("name", accessControlFacePo.getName());
        param.put("role", 1);
        param.put("kind", 0);
        String cardId = accessControlFacePo.getCardNumber();
        if (!StringUtil.isEmpty(cardId) && StringUtil.isNumber(cardId.trim())) {
            param.put("wg_card_id", Long.parseLong(cardId.trim()));
        }
//        if(!StringUtil.isEmpty(userFaceDto.getStartTime())){
//            param.put("term_start", userFaceDto.getStartTime());
//        }
        String faceData = accessControlFacePo.getFaceBase64();
        if (!StringUtil.isEmpty(faceData)) {
            faceData = faceData.replaceAll("\r\n", "");
            faceData = faceData.replaceAll("\n", "");
        }
        param.put("reg_image", faceData);

        MqttFactory.publish(REQUEST_FACE.replace(SN, accessControlDto.getMachineCode()), param.toJSONString());

        saveAddFaceLog("", accessControlDto.getMachineId(), REQUEST_FACE.replace(SN, accessControlDto.getMachineCode()) + "/uploadperson",
                accessControlDto.getCommunityId(),
                param.toJSONString(), accessControlFacePo.getPersonId(), accessControlFacePo.getName());

        return true;
    }

    @Override
    public boolean updateUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        deleteUser(accessControlDto, accessControlFacePo);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return addUser(accessControlDto, accessControlFacePo);
    }

    @Override
    public boolean deleteUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        JSONObject param = new JSONObject();
        param.put("cmd", "delete person(s)");
        param.put("flag", -1);
        param.put("id", accessControlFacePo.getPersonId());

        MqttFactory.publish(REQUEST_FACE.replace(SN, accessControlDto.getMachineCode()), param.toJSONString());
        saveDeleteFaceLog("", accessControlDto.getMachineId(), REQUEST_FACE.replace(SN, accessControlDto.getMachineCode()) + "/deleteperson",
                accessControlDto.getCommunityId(), param.toJSONString(),
                accessControlFacePo.getPersonId(), accessControlFacePo.getName());

        return true;
    }


    /**
     * 设备回调
     *
     * @param topic 回话
     * @param data  上报参数
     * @return
     */
    @Override
    public String accessControlResult(String topic, String data) {

        JSONObject param = JSONObject.parseObject(data);

        String cmd = param.getString("cmd");

        if (topic.startsWith(HEARTBEAT_FACE)) {
            heartbeat(param.getString("device_sn"));
            return SUCCESS;
        }
        if ("face".equals(cmd)) {
            openDoorResult(data);
            return SUCCESS;
        }

        if (topic.startsWith(RESPONSE_FACE)) {
            doCmdResult(JSONObject.parseObject(data));
        }
        return SUCCESS;
    }

    /**
     * # 状态码，可能的取值：
     * #    0：成功，人员已添加或修改
     * #    2：设备还未实现此功能（早于2020年8月21日的版本）
     * #    3：给定的数据不正确（如果出现这个响应码，则会有desc对具体错误进行文字描述）
     * #    6：资源不足。设备内存不够
     * #	   16：人脸重复（在开启了禁止重复人脸注册时可能出现）
     * #    20：数据录入达到上限。设备人员已满
     * #    21：记录已经存在。请求消息体中upload_mode为1，而设备已经存在对应id的用户
     * #    22：记录不存在。请求消息体中upload_mode为2，而设备中不存在对应id的用户
     * #    25：提取人脸失败。图像中无人脸
     * #    35：图像解码失败。给定图像不为jpg或png
     * #    36：图像太大。JPG文件不能大于10M
     * #    37：归一化失败。只出现在给了错误的normal_image数据
     * #    38：人脸尺寸太小
     * #    39：人脸质量太差
     * #    40：图像中有多个人脸
     * #    41：图像中人脸不完整
     *
     * @param resultCmd
     */
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


    @Override
    public boolean restartMachine(AccessControlDto machineDto) {
        JSONObject param = JSONObject.parseObject("{\n" +
                "  \"cmd\": \"reboot\"\n" +
                "}");
        MqttFactory.publish(REQUEST_FACE.replace(SN, machineDto.getMachineCode()), param.toJSONString());
        saveRebootAcLog(GenerateCodeFactory.getGeneratorId("11"), machineDto.getMachineId(), REQUEST_FACE.replace(SN, machineDto.getMachineCode()) + "/reboot",
                machineDto.getCommunityId(), param.toJSONString(), machineDto.getUserId(), machineDto.getUserName());
        return true;
    }

    /**
     * {
     * # 命令
     * "cmd": "gpio control",
     * <p>
     * # 端口号
     * # 远程开门固定传1
     * "port": 1,
     * <p>
     * # 控制类型，on: 闭合 off: 断开
     * # 目前只支持闭合，由相机自动断开
     * "ctrl_type": "on",
     * <p>
     * # 控制模式，test: 测试模式 force: 强制模式 scene: 场景模式
     * # 测试模式：用于测试，不产生开闸信息记录；
     * # 强制方式：输出信号将强制输出到设备；
     * # 场景模式：是否输出信号到设备取决于设备是否使能；
     * "ctrl_mode": "force",
     * }
     *
     * @param machineDto 硬件信息
     */
    @Override
    public boolean openDoor(AccessControlDto machineDto) {
        JSONObject param = JSONObject.parseObject("{\n" +
                "       \"cmd\": \"gpio control\",\n" +
                "       \"port\": 1,\n" +
                "       \"ctrl_type\": \"on\",\n" +
                "       \"ctrl_mode\": \"force\",\n" +
                "     }");
        MqttFactory.publish(REQUEST_FACE.replace(SN, machineDto.getMachineCode()), param.toJSONString());
        saveOpenDoorLog(GenerateCodeFactory.getGeneratorId("11"), machineDto.getMachineId(), REQUEST_FACE.replace(SN, machineDto.getMachineCode()) + "/openDoor",
                machineDto.getCommunityId(), param.toJSONString(), machineDto.getUserId(), machineDto.getUserName());
        return true;
    }


    /**
     * {
     * "body" : {
     * "code" : 101,
     * "face_imgdata" : "/9j/4AAQSkZJRghDk+tAH/2Q==",
     * "face_imgsize" : 27178,
     * "hat" : 1,
     * "matched" : 91,
     * "model_imgdata" : "/9j/4AAQSkZJRgABAQAAKDKUT/2Q==",
     * "model_imgsize" : 6001,
     * "name" : "吴学文",
     * "per_id" : "772020051963050001",
     * "pic_name" : "9f15b229-0422fd6b_1590397611_91_101.jpg",
     * "role" : 0,
     * "sec" : 1590397611,
     * "sequence" : 2085,
     * "sn" : "9f15b229-0422fd6b",
     * "usec" : 778083
     * },
     * "type" : "face_result"
     * }
     *
     * @param data 这个为设备人脸推送协议，请参考设备协议文档
     * @return
     */

    public void openDoorResult(String data) {
        JSONObject paramJson = JSONObject.parseObject(data);
        logger.debug("门禁内容,{}", paramJson.toJSONString());

        JSONObject result = new JSONObject();
        result.put("reply", "ACK");
        result.put("cmd", "face");
        result.put("code", 0);
        result.put("sequence_no", paramJson.getIntValue("sequence_no"));
        result.put("cap_time", paramJson.getString("cap_time"));
        MqttFactory.publish(UPLOAD_RESPONSE_FACE + paramJson.getString("device_sn"), result.toJSONString());

        if (!paramJson.containsKey("match")) {
            return;
        }
        JSONObject dataObj = paramJson.getJSONObject("match");
        if (!dataObj.containsKey("person_id") || StringUtil.isEmpty(dataObj.getString("person_id"))) {
            return;
        }
        String staffId = dataObj.getString("person_id");
        String staffName = dataObj.getString("person_name");
        String machineCode = paramJson.getString("device_sn");
        if (StringUtil.isEmpty(staffId)) {
            staffId = "-1";
            staffName = "未知";
        }
        String images = "";
        if (paramJson.containsKey("closeup_pic")) {
            JSONObject closeup_pic = paramJson.getJSONObject("closeup_pic");
            images = closeup_pic.getString("data");
        }
        if (StringUtil.isEmpty(images)) {
            images = dataObj.getString("image");
        }

        int similar = 0;
        if (paramJson.containsKey("match_result")) {
            similar = paramJson.getIntValue("match_result");
        }
        AccessControlOweFeeDto accessControlOweFeeDto = saveOpenDoorResult(machineCode, images, staffId, staffName, OPEN_TYPE_FACE, similar);

        if (accessControlOweFeeDto.getCode() < 0) {
            return;
        }

        JSONObject tts = new JSONObject();
        tts.put("text",  accessControlOweFeeDto.getMsg());
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
                "   \"text\":\"" + accessControlOweFeeDto.getMsg() + "\"\n" +
                "  }\n" +
                " ]\n" +
                "}";
        MqttFactory.publish(REQUEST_FACE.replace(SN, paramJson.getString("device_sn")), viewText);

    }


}
