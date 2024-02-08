package com.java110.accessControl.manufactor.adapt.accessControl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.accessControl.manufactor.AbstractAccessControlManufactorAdapt;
import com.java110.accessControl.smo.impl.AccessControlLogV1InnerServiceSMOImpl;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControlLog.AccessControlLogDto;
import com.java110.po.accessControlFace.AccessControlFacePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("cjHttpAssessControlProcessAdapt")
public class CjHttpAssessControlProcessAdapt extends AbstractAccessControlManufactorAdapt {

    private static Logger logger = LoggerFactory.getLogger(CjHttpAssessControlProcessAdapt.class);

    //发布消息主题
    private static final String REQUEST_FACE = "device/uface/{deviceSN}/request";
    //回复消息主题
    private static final String RESPONSE_FACE = "device/uface/{deviceSN}/response";

    private static final String DEVICE_SN = "{deviceSN}";

    @Autowired
    private AccessControlLogV1InnerServiceSMOImpl accessControlLogV1InnerServiceSMOImpl;

    @Override
    public boolean initMachine(AccessControlDto machineDto) {
        MqttFactory.subscribe("device/uface/" + machineDto.getMachineCode() + "/response");
        MqttFactory.subscribe("device/uface/" + machineDto.getMachineCode() + "/event");
        return true;
    }

    @Override
    public boolean addUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        JSONObject param = new JSONObject();
        param.put("method", "addPerson");
        param.put("timestamp", System.currentTimeMillis());
        param.put("id", GenerateCodeFactory.getUUID());

        JSONObject data = new JSONObject();
        JSONObject person = new JSONObject();
        JSONObject faceJSON = new JSONObject();
        List<JSONObject> face = new ArrayList<>();

        person.put("personId", accessControlFacePo.getPersonId());
        person.put("name", accessControlFacePo.getName());
        data.put("person", person);

        String faceData = accessControlFacePo.getFaceBase64();
        if (!StringUtil.isEmpty(faceData)) {
            faceData = faceData.replaceAll("\n", "");
        }
        faceJSON.put("image", faceData);
        face.add(faceJSON);
        data.put("face", face);
        param.put("data", data);

        MqttFactory.publish(REQUEST_FACE.replace(DEVICE_SN, accessControlDto.getMachineCode()), param.toJSONString());

        saveAddFaceLog("", accessControlDto.getMachineId(), REQUEST_FACE.replace(DEVICE_SN, accessControlDto.getMachineCode()) + "/addPerson",
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
        param.put("method", "deletePerson");
        param.put("timestamp", System.currentTimeMillis());
        param.put("id", GenerateCodeFactory.getUUID());

        JSONObject data = new JSONObject();
        List<String> personId = new ArrayList<>();
        personId.add(accessControlFacePo.getPersonId());

        data.put("personId", personId);
        param.put("data", data);

        MqttFactory.publish(REQUEST_FACE.replace(DEVICE_SN, accessControlDto.getMachineCode()), param.toJSONString());

        saveDeleteFaceLog("", accessControlDto.getMachineId(), REQUEST_FACE.replace(DEVICE_SN, accessControlDto.getMachineCode()) + "/deletePerson",
                accessControlDto.getCommunityId(),
                param.toJSONString(), accessControlFacePo.getPersonId(), accessControlFacePo.getName());

        return true;
    }

    @Override
    public boolean openDoor(AccessControlDto accessControlDto) {
        JSONObject param = new JSONObject();
        param.put("method", "openDoor");
        param.put("timestamp", System.currentTimeMillis());
        param.put("id", GenerateCodeFactory.getUUID());

        JSONObject data = new JSONObject();
        JSONObject uiDisplayText = new JSONObject();
        JSONObject broadcast = new JSONObject();
        uiDisplayText.put("custom", "欢迎" + accessControlDto.getUserName());
        uiDisplayText.put("duration", 3000);
        broadcast.put("custom", "欢迎" + accessControlDto.getUserName());
        data.put("uiDisplayText", uiDisplayText);
        data.put("broadcast", broadcast);
        param.put("data", data);

        MqttFactory.publish(REQUEST_FACE.replace(DEVICE_SN, accessControlDto.getMachineCode()), param.toJSONString());

        saveOpenDoorLog("", accessControlDto.getMachineId(), REQUEST_FACE.replace(DEVICE_SN, accessControlDto.getMachineCode()) + "/openDoor",
                accessControlDto.getCommunityId(),
                param.toJSONString(), accessControlDto.getUserId(), accessControlDto.getUserName());

        return true;
    }

    @Override
    public boolean restartMachine(AccessControlDto accessControlDto) {
        JSONObject param = new JSONObject();
        param.put("method", "reboot");
        param.put("timestamp", System.currentTimeMillis());
        param.put("id", GenerateCodeFactory.getUUID());

        MqttFactory.publish(REQUEST_FACE.replace(DEVICE_SN, accessControlDto.getMachineCode()), param.toJSONString());

        saveRebootAcLog("", accessControlDto.getMachineId(), REQUEST_FACE.replace(DEVICE_SN, accessControlDto.getMachineCode()) + "/reboot",
                accessControlDto.getCommunityId(),
                param.toJSONString(), accessControlDto.getUserId(), accessControlDto.getUserName());

        return true;
    }

    @Override
    public String accessControlResult(String topic, String data) {
        if (topic.contains("upload")) {
            doUploadResult(data);
            return SUCCESS;
        }

        if (topic.contains("event")) {
            doEventResult(data);
            return SUCCESS;
        }

        if (topic.contains("response")) {
            doResponseResult(topic, data);
        }
        return SUCCESS;
    }

    private void doUploadResult(String data) {
        JSONObject param = JSONObject.parseObject(data);
        String method = param.getString("method");
        if ("pushIdentifyRecord".equals(method)) {
            JSONObject uploadData = param.getJSONObject("data");
            String spotImage = uploadData.getJSONObject("spotContent").getString("spotImage");
            //todo:
        }

    }

    private void doEventResult(String data) {
        JSONObject param = JSONObject.parseObject(data);
        Integer event = param.getInteger("event");
        JSONObject eventData = param.getJSONObject("data");
        Integer type = eventData.getInteger("type");

        if (event == 2 && type == 1) {
            String qrcode = eventData.getJSONObject("content").getString("qrcode");
            //todo:
        }

    }

    private void doResponseResult(String topic, String data) {
        JSONObject param = JSONObject.parseObject(data);
        Integer code = param.getInteger("code");
        String message = param.getString("message");
        if (code != 0 || !"success".equals(message)) {
            return;
        }

        String logAction = "";

        JSONObject responseData = param.getJSONObject("data");
        if (responseData != null && responseData.getJSONObject("uiDisplayText") != null) {
            logAction = topic.replace("response", "request") + "/openDoor";
            openDoorResult(logAction, param.getString("id"), param.getInteger("timestamp"));
        }
        if (responseData == null) {
            logAction = topic.replace("response", "request") + "/reboot";
        }
        if (responseData != null && responseData.get("person") != null && responseData.get("person") instanceof JSONObject) {
            logAction = topic.replace("response", "request") + "/addPerson";
        }
        if (responseData != null && responseData.get("person") != null && responseData.get("person") instanceof JSONArray) {
            logAction = topic.replace("response", "request") + "/deletePerson";
        }
        AccessControlLogDto accessControlLogDto = new AccessControlLogDto();
        accessControlLogDto.setLogAction(logAction);
        accessControlLogDto.setState(AccessControlLogDto.STATE_REQ);
        List<AccessControlLogDto> accessControlLogDtos = accessControlLogV1InnerServiceSMOImpl.queryAccessControlLogs(accessControlLogDto);
        for (AccessControlLogDto controlLogDto : accessControlLogDtos) {
            String reqParam = controlLogDto.getReqParam();
            JSONObject paramData = JSONObject.parseObject(reqParam);
            Integer reqTimestamp = paramData.getInteger("timestamp");
            String reqId = paramData.getString("id");
            if (param.getString("id").equals(reqId) && param.getInteger("timestamp") >= reqTimestamp) {
                cmdResult(code, data, logAction, logAction.split("/")[2], controlLogDto.getUserId());
                break;
            }
        }
    }

    private void openDoorResult(String logAction, String id, Integer timestamp) {
        AccessControlLogDto accessControlLogDto = new AccessControlLogDto();
        accessControlLogDto.setLogAction(logAction);
        accessControlLogDto.setState(AccessControlLogDto.STATE_REQ);
        List<AccessControlLogDto> accessControlLogDtos = accessControlLogV1InnerServiceSMOImpl.queryAccessControlLogs(accessControlLogDto);
        for (AccessControlLogDto controlLogDto : accessControlLogDtos) {
            String reqParam = controlLogDto.getReqParam();
            JSONObject param = JSONObject.parseObject(reqParam);
            Integer reqTimestamp = param.getInteger("timestamp");
            String reqId = param.getString("id");
            if (id.equals(reqId) && timestamp >= reqTimestamp) {
                saveOpenDoorResult(logAction.split("/")[2], null, controlLogDto.getUserId(), controlLogDto.getUserName(), OPEN_TYPE_CARD, 51);
                break;
            }
        }
    }
}
