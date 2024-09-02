package com.java110.accessControl.manufactor.adapt.accessControl;

import com.alibaba.fastjson.JSONObject;
import com.java110.accessControl.manufactor.AbstractAccessControlManufactorAdapt;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControl.AccessControlOweFeeDto;
import com.java110.dto.visit.VisitDto;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.accessControl.IVisitV1InnerServiceSMO;
import com.java110.po.accessControlFace.AccessControlFacePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 微光 门禁设备 Mqtt 方式
 */
@Service("weiguangMqttAssessControlProcessAdapt")
public class WeiGuangMqttAssessControlProcessAdapt extends AbstractAccessControlManufactorAdapt {

    private static Logger logger = LoggerFactory.getLogger(WeiGuangMqttAssessControlProcessAdapt.class);

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Autowired
    private IVisitV1InnerServiceSMO visitV1InnerServiceSMOImpl;

    public static final long START_TIME = new Date().getTime() - 1000 * 60 * 60;
    public static final long END_TIME = new Date().getTime() + 1000 * 60 * 60 * 24 * 365;


    //在线验证
    private static final String ONLINECHECK = "20180820/onlinecheck";
    private static final String REQUEST_FACE = "20180820/onlinecheck";
    //在线回复
    private static final String ONLINECHECK_REPLY = "20180820/SN/onlinecheck_reply";
    //控制开门
    private static final String CONTROLDEVICE = "20180820/SN/controldevice";

    //心跳
    private static final String HEARTBEAT_FACE = "changpaimenjin/topic/face/heart/request/";



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
        MqttFactory.subscribe("20180820/"+machineDto.getMachineCode()+"/controldevice");
        MqttFactory.subscribe("20180820/"+machineDto.getMachineCode()+"/onlinecheck_reply");
        MqttFactory.subscribe("20180820/onlinecheck");
        return true;
    }

    @Override
    public boolean addUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        return true;
    }

    @Override
    public boolean updateUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        return true;
    }

    @Override
    public boolean deleteUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
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
        //TODO 如果有心跳
        if (topic.startsWith(HEARTBEAT_FACE)) {
            heartbeat(param.getString("device_sn"));
            return SUCCESS;
        }

        if (ONLINECHECK.equals(topic)) {//在线验证二维码
            doVerifyResult(JSONObject.parseObject(data));
        }
        return SUCCESS;
    }

    /**
     {
     "SerialNo" : 2, //消息序列号
     "DeviceAddr" : "xxxxxxxx", //设备地址
     "IdentityType" : 101, /身份类型
     "IdentityCode" : "xxxxxxxx", //身份标识
     "OriginalCode" : "xxxxxxxx", //原始数据
     "PassTime" : 1596277728 //通行时间，时间戳（秒）
     }
     *
     * @param data
     */
    private void doVerifyResult(JSONObject data) {
        String personId = data.getString("IdentityCode");//身份标识
        String SerialNo = data.getString("SerialNo");//
        String originalCode = data.getString("OriginalCode");//二维码识别后的原始数据
        String cmd = data.getString("cmd");
        String machineCode = data.getString("DeviceAddr");
        VisitDto visitDto = new VisitDto();
        visitDto.setQrcode(originalCode);
        visitDto.setState(VisitDto.STATE_C);
        int count = visitV1InnerServiceSMOImpl.queryVisitsCount(visitDto);
        int code = -1;
        String msg = "";
        //能不能开都给个结果
        List<VisitDto> visitDtos = null;
        if (count > 0) {
            visitDtos = visitV1InnerServiceSMOImpl.queryVisits(visitDto);
            Date currentTime = new Date();
            if (currentTime.after(visitDtos.get(0).getVisitTime()) && currentTime.before(visitDtos.get(0).getDepartureTime())) {
                msg="访客“"+ visitDtos.get(0).getName() + "”使用"+originalCode+"开门";
                code = 0;
                String openMsg = "{\n" +
                        " \"SerialNo\":" + SerialNo + ",\n" +
                        " \"DeviceAddr\":\"" + machineCode + "\",\n" +
                        " \"PassResult\":1,\n" +
                        " \"Message\":\"" + "开门成功" + "\"\n" +
                        "}";
                MqttFactory.publish(ONLINECHECK_REPLY.replace(SN,machineCode), openMsg);
                saveOpenDoorResult(machineCode, "", visitDtos.get(0).getVisitId(), visitDtos.get(0).getName(), OPEN_TYPE_QRCODE, 100);
            } else {
                msg="访客“"+ visitDtos.get(0).getName() + "”使用"+originalCode+"开门，但是不在来访时间范围内，开门失败。";
            }
        } else {//不能开
            msg="陌生人使用"+originalCode+"开门，开门失败。";
        }
        cmdResult(code, msg, ONLINECHECK, machineCode, personId);



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
     后台或应用到设备
     消息 topic: 20180820/{#DeviceAddr}/controldevice
     消息内容：
     {
     "SerialNo":2, //消息序列号
     "DeviceAddr":"xxxxxxxx", //设备地址
     "HintType":1//控制类型，1-开门
     }
     *
     * @param machineDto 硬件信息
     */
    @Override
    public boolean openDoor(AccessControlDto machineDto) {
        String seriaNo = GenerateCodeFactory.getGeneratorId("18");
        String msg = "{\n" +
                " \"SerialNo\":\"" + seriaNo + "\",\n" +
                " \"DeviceAddr\":\"" + machineDto.getMachineCode() + "\",\n" +
                " \"HintType\":1\n" +
                "}";
        MqttFactory.publish(CONTROLDEVICE.replace(SN, machineDto.getMachineCode()), msg);
        saveOpenDoorLog(seriaNo, machineDto.getMachineId(), CONTROLDEVICE.replace(SN, machineDto.getMachineCode()),
                machineDto.getCommunityId(), msg, machineDto.getUserId(), machineDto.getUserName());
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
        MqttFactory.publish(paramJson.getString("device_sn"), result.toJSONString());

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
        MqttFactory.publish(ONLINECHECK_REPLY + paramJson.getString("device_sn"), result.toJSONString());

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
