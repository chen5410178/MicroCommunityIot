package com.java110.accessControl.manufactor.adapt.accessControl.yufan;

import com.alibaba.fastjson.JSONObject;
import com.java110.accessControl.manufactor.AbstractAccessControlManufactorAdapt;
import com.java110.accessControl.smo.impl.AccessControlLogV1InnerServiceSMOImpl;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControl.AccessControlOweFeeDto;
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
        MqttFactory.subscribe("device/uface/online");

        return true;
    }

    @Override
    public boolean addUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        String id = GenerateCodeFactory.getUUID();
        JSONObject param = new JSONObject();
        param.put("method", "addPerson");
        param.put("timestamp", System.currentTimeMillis());
        param.put("id", id);

        JSONObject data = new JSONObject();
        JSONObject person = new JSONObject();
        JSONObject faceJSON = new JSONObject();
        List<JSONObject> face = new ArrayList<>();

        person.put("personId", accessControlFacePo.getPersonId());
        person.put("name", accessControlFacePo.getName());
        String cardId = accessControlFacePo.getCardNumber();
        if (!StringUtil.isEmpty(cardId)) {
            person.put("cardNo", cardId);
        }
        data.put("person", person);

        String faceData = accessControlFacePo.getFaceBase64();
        if (!StringUtil.isEmpty(faceData)) {
            faceData = faceData.replaceAll("\n", "");
            faceJSON.put("image", faceData);
            face.add(faceJSON);
            data.put("face", face);
        }

        param.put("data", data);

        MqttFactory.publish(REQUEST_FACE.replace(DEVICE_SN, accessControlDto.getMachineCode()), param.toJSONString());

        saveAddFaceLog(id, accessControlDto.getMachineId(), REQUEST_FACE.replace(DEVICE_SN, accessControlDto.getMachineCode()) + "/addPerson",
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
        String id = GenerateCodeFactory.getUUID();

        JSONObject param = new JSONObject();
        param.put("method", "deletePerson");
        param.put("timestamp", System.currentTimeMillis());
        param.put("id",id);

        JSONObject data = new JSONObject();
        List<String> personId = new ArrayList<>();
        personId.add(accessControlFacePo.getPersonId());

        data.put("personId", personId);
        param.put("data", data);

        MqttFactory.publish(REQUEST_FACE.replace(DEVICE_SN, accessControlDto.getMachineCode()), param.toJSONString());

        saveDeleteFaceLog(id, accessControlDto.getMachineId(), REQUEST_FACE.replace(DEVICE_SN, accessControlDto.getMachineCode()) + "/deletePerson",
                accessControlDto.getCommunityId(),
                param.toJSONString(), accessControlFacePo.getPersonId(), accessControlFacePo.getName());

        return true;
    }

    @Override
    public boolean openDoor(AccessControlDto accessControlDto) {
        String id =  GenerateCodeFactory.getUUID();

        JSONObject param = new JSONObject();
        param.put("method", "openDoor");
        param.put("timestamp", System.currentTimeMillis());
        param.put("id",id);

        JSONObject data = new JSONObject();
//        JSONObject uiDisplayText = new JSONObject();
//        JSONObject broadcast = new JSONObject();
//        uiDisplayText.put("custom", "欢迎" + accessControlDto.getUserName());
//        uiDisplayText.put("duration", 3000);
//        broadcast.put("custom", "欢迎" + accessControlDto.getUserName());
//        data.put("uiDisplayText", uiDisplayText);
//        data.put("broadcast", broadcast);
        param.put("data", data);

        MqttFactory.publish(REQUEST_FACE.replace(DEVICE_SN, accessControlDto.getMachineCode()), param.toJSONString());

        saveOpenDoorLog(id, accessControlDto.getMachineId(), REQUEST_FACE.replace(DEVICE_SN, accessControlDto.getMachineCode()) + "/openDoor",
                accessControlDto.getCommunityId(),
                param.toJSONString(), accessControlDto.getUserId(), accessControlDto.getUserName());

        return true;
    }

    @Override
    public boolean restartMachine(AccessControlDto accessControlDto) {
        String id =  GenerateCodeFactory.getUUID();
        JSONObject param = new JSONObject();
        param.put("method", "reboot");
        param.put("timestamp", System.currentTimeMillis());
        param.put("id",id);

        MqttFactory.publish(REQUEST_FACE.replace(DEVICE_SN, accessControlDto.getMachineCode()), param.toJSONString());

        saveRebootAcLog(id, accessControlDto.getMachineId(), REQUEST_FACE.replace(DEVICE_SN, accessControlDto.getMachineCode()) + "/reboot",
                accessControlDto.getCommunityId(),
                param.toJSONString(), accessControlDto.getUserId(), accessControlDto.getUserName());

        return true;
    }

    @Override
    public String accessControlResult(String topic, String data) {

        if ("device/uface/online".equals(topic)) {
            machineOnline(data);
            return SUCCESS;
        }

        if (topic.contains("upload")) {
            doUploadResult(topic,data);
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

    private void machineOnline(String data) {

        JSONObject paramIn = JSONObject.parseObject(data);
        String method = paramIn.getString("method");
        if (!"online".equals(method)) {
            return;
        }


        String id = paramIn.getString("id");
        String deviceNo = paramIn.getJSONObject("data").getString("deviceNo");
        heartbeat(deviceNo, DateUtil.getCurrentDate());

        JSONObject paramOut = new JSONObject();
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime());
        paramOut.put("id", id);
        paramOut.put("code", 0);
        paramOut.put("message", "success");
        JSONObject dataObj = new JSONObject();
        dataObj.put("platformKey", "111111111111111111111111");//24位
        dataObj.put("platformName", "Y星球");
        dataObj.put("platformIV", "1111111111111111");
        paramOut.put("data", dataObj);

        MqttFactory.publish("device/uface/" + deviceNo + "/response", paramOut.toJSONString());


    }

    private void doUploadResult(String topic,String data) {
        JSONObject param = JSONObject.parseObject(data);
        String method = param.getString("method");
        if (!"pushIdentifyRecord".equals(method)) {
            return;

        }
        String id = param.getString("id");
        //device/uface/84E0F42F78D91901/upload
        String[]topics = topic.split("/");
        if(topics.length != 4){
            return;
        }
        String deviceNo = topics[2];

        JSONObject uploadData = param.getJSONObject("data");
        String personId = uploadData.getString("personId");
        String name = uploadData.getString("name");
        int result = uploadData.getIntValue("result");
        JSONObject spotContent = uploadData.getJSONObject("spotContent");
        if(spotContent == null){
            return;
        }

        String spotImage = spotContent.getString("spotImage");
        //todo:

        int similar = 100;
        if (result == 1) {
            similar = 0;
        }

        AccessControlOweFeeDto accessControlOweFeeDto = saveOpenDoorResult(deviceNo, spotImage, personId, name, OPEN_TYPE_FACE, similar);
        if (accessControlOweFeeDto.getCode() < 0) {
            return;
        }


        JSONObject paramOut = new JSONObject();
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime());
        paramOut.put("id", id);
        paramOut.put("code", 0);
        paramOut.put("message", "success");
        JSONObject dataObj = new JSONObject();
        paramOut.put("data", dataObj);

        MqttFactory.publish("device/uface/" + deviceNo + "/response", paramOut.toJSONString());

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

        String logAction = "";

        String id = param.getString("id");
        AccessControlLogDto accessControlLogDto = new AccessControlLogDto();
        accessControlLogDto.setLogId(id);
        accessControlLogDto.setState(AccessControlLogDto.STATE_REQ);
        List<AccessControlLogDto> accessControlLogDtos = accessControlLogV1InnerServiceSMOImpl.queryAccessControlLogs(accessControlLogDto);
        if(ListUtil.isNull(accessControlLogDtos)){
            return;
        }

        if("ADD_FACE".equals(accessControlLogDtos.get(0).getLogCmd())){
            JSONObject dataObj = param.getJSONObject("data");
            JSONObject face = dataObj.getJSONArray("face").getJSONObject(0);
            code = face.getInteger("code");
            message = face.getString("message");
        }
        if(AccessControlLogDto.CMD_DELETE_FACE.equals(accessControlLogDtos.get(0).getLogCmd())){
            //todo 这里失败的情况下再看下 成功的时候 好像没有必要判断
            /**
             * {"code":0,"data":{"person":[{"personId":"772024021975863681","result":true}]},"id":"efe78797d05a421a83dcee484726dda2","message":"操作成功","timestamp":1721296025759}
             */
        }


        cmdResult(code,message,accessControlLogDtos.get(0));

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
