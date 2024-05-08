package com.java110.accessControl.manufactor.adapt.accessControl.yld;

import com.alibaba.fastjson.JSONObject;
import com.java110.accessControl.manufactor.AbstractAccessControlManufactorAdapt;
import com.java110.core.constant.ResponseConstant;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControl.AccessControlOweFeeDto;
import com.java110.dto.accessControlLog.AccessControlLogDto;
import com.java110.dto.barrier.BarrierDto;
import com.java110.intf.accessControl.IAccessControlLogV1InnerServiceSMO;
import com.java110.po.accessControlFace.AccessControlFacePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 伊兰度 门禁设备 Mqtt 方式
 */
@Service("yldMqttAssessControlProcessAdapt")
public class YldMqttAssessControlProcessAdapt extends AbstractAccessControlManufactorAdapt {

    private static Logger logger = LoggerFactory.getLogger(YldMqttAssessControlProcessAdapt.class);
    //public static Function fun=new Function();


    public static final long START_TIME = new Date().getTime() - 1000 * 60 * 60;
    public static final long END_TIME = new Date().getTime() + 1000 * 60 * 60 * 24 * 365;

    public static final String OPEN_TYPE_FACE = "1000"; // 人脸开门

    //平台名称
    public static final String MANUFACTURER = "YLD_";

    public static final String VERSION = "0.2";

    public static final String CMD_ADD_FACE = "create_face"; // 创建人脸

    public static final String CMD_OPEN_DOOR = "gpio control"; // 开门

    public static final String CMD_REBOOT = "reboot_cam";// 重启设备

    public static final String CMD_UPDATE_FACE = "update_face"; //修改人脸

    public static final String CMD_DELETE_FACE = "delete_face"; //删除人脸

    public static final String CMD_UI_TITLE = "set_ui_title";// 设置名称
    public static final String CMD_FACE_SEARCH = "face_search";// 搜素设备

    //单设备处理
    public static final String TOPIC_FACE_SN_REQUEST = "face.{sn}.request";

    //多设备处理
    public static final String TOPIC_FACE_REQUEST = "face.request";

    //接收设备处理
    public static final String TOPIC_FACE_SN_RESPONSE = "face.{sn}.response";

    //识别结果上报
    public static final String TOPIC_FACE_RESPONSE = "face/response";

    //硬件上线上报
    public static final String TOPIC_ONLINE_RESPONSE = "online/response";

    public static final String TOPIC_HEARTBEAT = "heartbeat/response";

    public static final String SN = "{sn}";


    public static final String FACE_RESULT = "face_result";

    @Autowired
    private IAccessControlLogV1InnerServiceSMO accessControlLogV1InnerServiceSMOImpl;


    // @Override
    public void initAssessControlProcess() {
        logger.debug("初始化是配置器");
        MqttFactory.subscribe("online.response");

    }

    @Override
    public boolean addUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        String cmdId = GenerateCodeFactory.getUUID();
        JSONObject param = new JSONObject();
        param.put("client_id", accessControlDto.getMachineCode());
        param.put("cmd_id", cmdId);
        param.put("version", VERSION);
        param.put("cmd", CMD_ADD_FACE);
        param.put("per_id", accessControlFacePo.getPersonId());
        param.put("face_id", accessControlFacePo.getPersonId());
        param.put("per_name", accessControlFacePo.getName());
        param.put("idcardNum", accessControlFacePo.getIdNumber());
        param.put("img_url", accessControlFacePo.getFacePath());
        param.put("idcardper", accessControlFacePo.getIdNumber());
        param.put("s_time", START_TIME);
        param.put("e_time", END_TIME);
        param.put("per_type", 0);
        param.put("usr_type", 0);

        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, accessControlDto.getMachineCode()), param.toJSONString());

        saveAddFaceLog(cmdId, accessControlDto.getMachineId(), TOPIC_FACE_SN_REQUEST.replace(SN, accessControlDto.getMachineCode()) + "/uploadperson",
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
        String cmdId = GenerateCodeFactory.getUUID();

        JSONObject param = new JSONObject();
        param.put("client_id", accessControlDto.getMachineCode());
        param.put("cmd_id", cmdId);
        param.put("version", VERSION);
        param.put("cmd", CMD_DELETE_FACE);
        param.put("type", 0);
        param.put("per_id", accessControlFacePo.getPersonId());
        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, accessControlDto.getMachineCode()), param.toJSONString());
        saveDeleteFaceLog(cmdId, accessControlDto.getMachineId(), TOPIC_FACE_SN_REQUEST.replace(SN, accessControlDto.getMachineCode()) + "/deleteperson",
                accessControlDto.getCommunityId(), param.toJSONString(),
                accessControlFacePo.getPersonId(), accessControlFacePo.getName());

        return true;
    }


    @Override
    public String accessControlResult(String topic, String data) {

        JSONObject param = JSONObject.parseObject(data);

        switch (topic) {
            case TOPIC_FACE_RESPONSE:
                openDoorResult(data);
                break;
            case TOPIC_ONLINE_RESPONSE: //硬件上线
                machineOnline(data);
                break;
            case TOPIC_HEARTBEAT: //心跳
                heartbeat(param.getString("sn"));
                break;
            default:
                machineCmdResult(data);
                break;
        }

        return SUCCESS;

    }

    private void machineCmdResult(String data) {
        JSONObject resultCmd = JSONObject.parseObject(data);
        if (!resultCmd.containsKey("cmd")) {
            return;
        }
        String cmd = resultCmd.getString("cmd");

        switch (cmd) {
            case CMD_ADD_FACE:
                doCmdResultCloud(resultCmd);
                break;
            case CMD_UPDATE_FACE:
                doCmdResultCloud(resultCmd);
                break;
            case CMD_DELETE_FACE:
                doCmdResultCloud(resultCmd);
                break;
            case CMD_OPEN_DOOR:
                doCmdResultCloud(resultCmd);
                break;
            case CMD_REBOOT:
                doCmdResultCloud(resultCmd);
                break;
            default:
                break;
        }
    }

    private void doCmdResultCloud(JSONObject resultCmd) {
        String logId = resultCmd.getString("cmd_id");

        //todo 更新下发日志
        AccessControlLogDto accessControlLogDto = new AccessControlLogDto();
        accessControlLogDto.setLogId(logId);
        accessControlLogDto.setState(AccessControlLogDto.STATE_REQ);
        List<AccessControlLogDto> accessControlLogDtos = accessControlLogV1InnerServiceSMOImpl.queryAccessControlLogs(accessControlLogDto);
        if (ListUtil.isNull(accessControlLogDtos)) {
            return;
        }

        int code = -1;
        if (!resultCmd.containsKey("code")) {
            code = -1;
        } else {
            code = resultCmd.getIntValue("code");
        }
        String msg = resultCmd.getString("reply");
        cmdResult(code, msg, accessControlLogDtos.get(0).getLogAction(), accessControlLogDtos.get(0).getMachineCode(), accessControlLogDtos.get(0).getUserId());
    }

    @Override
    public boolean restartMachine(AccessControlDto machineDto) {
        String cmdId = GenerateCodeFactory.getGeneratorId("11");
        JSONObject param = new JSONObject();
        param.put("client_id", machineDto.getMachineCode());
        param.put("cmd_id", cmdId);
        param.put("version", VERSION);
        param.put("cmd", CMD_REBOOT);
        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());
        saveRebootAcLog(cmdId, machineDto.getMachineId(), TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()) + "/reboot",
                machineDto.getCommunityId(), param.toJSONString(), machineDto.getUserId(), machineDto.getUserName());
        return true;

    }

    @Override
    public boolean openDoor(AccessControlDto machineDto) {
        String cmdId = GenerateCodeFactory.getGeneratorId("11");
        JSONObject param = new JSONObject();
        param.put("client_id", machineDto.getMachineCode());
        param.put("cmd_id", cmdId);
        param.put("version", VERSION);
        param.put("cmd", CMD_OPEN_DOOR);
        param.put("ctrl_type", "on");
        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());
        saveOpenDoorLog(cmdId, machineDto.getMachineId(), TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()) + "/openDoor",
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

        if (paramJson.containsKey("type") && !FACE_RESULT.equals(paramJson.getString("type"))) {
            return;
        }

        JSONObject body = paramJson.getJSONObject("body");

        String machineCode = body.getString("sn");

        String userId = body.containsKey("per_id") ? body.getString("per_id") : "-1";
        String userName = body.containsKey("name") ? body.getString("name") : "未知人员";

        if (StringUtil.isEmpty(userId)) {
            userId = "-1";
        }

        String images = body.getString("img_data");


        AccessControlOweFeeDto accessControlOweFeeDto = saveOpenDoorResult(machineCode, images, userId, userName,
                OPEN_TYPE_FACE, body.containsKey("matched") ? body.getIntValue("matched") : 0);


        if (accessControlOweFeeDto.getCode() < 0) {
            return;
        }

    }

    /**
     * 设备上线
     *
     * @param data {
     *             "cmd": "mqtt_online",
     *             "sn": "fffffff",
     *             "result": "mqtt is online"
     *             }
     */
    protected void machineOnline(String data) {
        JSONObject param = JSONObject.parseObject(data);

        String machineCode = param.getString("sn");

        MqttFactory.subscribe(TOPIC_FACE_SN_RESPONSE.replace(SN, machineCode));
        //注册设备上线 topic
        MqttFactory.subscribe("heartbeat/response");
        //推送人脸识别结果
        MqttFactory.subscribe("face/response");

        heartbeat(machineCode, DateUtil.getCurrentDate());
    }
}
