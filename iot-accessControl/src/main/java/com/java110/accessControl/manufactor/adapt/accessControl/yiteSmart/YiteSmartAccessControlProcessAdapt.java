package com.java110.accessControl.manufactor.adapt.accessControl.yiteSmart;

import com.alibaba.fastjson.JSONObject;
import com.java110.accessControl.manufactor.AbstractAccessControlManufactorAdapt;
import com.java110.accessControl.manufactor.adapt.accessControl.ChangpaiMqttAssessControlProcessAdapt;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.po.accessControlAdRel.AccessControlAdRelPo;
import com.java110.po.accessControlFace.AccessControlFacePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 一特智慧
 */
@Service("yiteSmartAccessControlProcessAdapt")
public class YiteSmartAccessControlProcessAdapt extends AbstractAccessControlManufactorAdapt {

    private static Logger logger = LoggerFactory.getLogger(YiteSmartAccessControlProcessAdapt.class);

    @Autowired
    private ISyncUser syncUserImpl;

    @Autowired
    private ISyncUserFace syncUserFaceImpl;

    @Autowired
    private ISyncUserCard syncUserCardImpl;

    @Override
    public boolean initMachine(AccessControlDto machineDto) {
        MqttFactory.subscribe("zghl_door/server_php");
        return true;
    }

    /**
     * {
     * "t": "8410789684082c1607d0",
     * "c": "syncUsers",
     * "m": {
     * "p": "all",
     * "t": "11291445e513488f1f0381ef6de241a2"
     * },
     * "f": "server_php"
     * }
     *
     * @param accessControlDto    门禁信息
     * @param accessControlFacePo 人员信息
     * @return
     */
    @Override
    public boolean addUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {

        // todo 保存用户信息

        JSONObject param = syncUserImpl.getAddUser(accessControlDto, accessControlFacePo);

        saveAddFaceLog("", accessControlDto.getMachineId(), "/v3_1/sync/users/add/",
                accessControlDto.getCommunityId(),
                param.toJSONString(), accessControlFacePo.getPersonId(), accessControlFacePo.getName());


        JSONObject paramIn = new JSONObject();
        paramIn.put("t", accessControlDto.getMachineCode());
        paramIn.put("c", "syncUsers");
        paramIn.put("f", "server_php");

        JSONObject m = new JSONObject();
        m.put("p", "all");
        m.put("t", GenerateCodeFactory.getUUID());
        paramIn.put("m", m);

        MqttFactory.publish("zghl_door/" + accessControlDto.getMachineCode(), paramIn.toJSONString());

        //todo 人员人脸信息
        param = syncUserFaceImpl.getUserFace(accessControlDto, accessControlFacePo);

        saveAddFaceLog("", accessControlDto.getMachineId(), "/device/v2/face/sync",
                accessControlDto.getCommunityId(),
                param.toJSONString(), accessControlFacePo.getPersonId(), accessControlFacePo.getName());

        paramIn = new JSONObject();
        paramIn.put("t", accessControlDto.getMachineCode());
        paramIn.put("c", "syncFace");
        paramIn.put("f", "server_php");

        m = new JSONObject();
        m.put("p", "all");
        m.put("t", GenerateCodeFactory.getUUID());
        paramIn.put("m", m);

        MqttFactory.publish("zghl_door/" + accessControlDto.getMachineCode(), paramIn.toJSONString());

        if (StringUtil.isEmpty(accessControlFacePo.getCardNumber())) {
            return true;
        }


        //todo 人员卡信息

        param = syncUserCardImpl.getUserCard(accessControlDto, accessControlFacePo);

        saveAddFaceLog("", accessControlDto.getMachineId(), "/v3_1/sync/cards/add/sync",
                accessControlDto.getCommunityId(),
                param.toJSONString(), accessControlFacePo.getPersonId(), accessControlFacePo.getName());

        paramIn = new JSONObject();
        paramIn.put("t", accessControlDto.getMachineCode());
        paramIn.put("c", "syncCards");
        paramIn.put("f", "server_php");

        m = new JSONObject();
        m.put("p", "all");
        m.put("t", GenerateCodeFactory.getUUID());
        paramIn.put("m", m);

        MqttFactory.publish("zghl_door/" + accessControlDto.getMachineCode(), paramIn.toJSONString());


        return true;
    }

    @Override
    public boolean updateUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        //return addUser(accessControlDto, accessControlFacePo);
        //todo 人员人脸信息
        JSONObject param = syncUserFaceImpl.getUserFace(accessControlDto, accessControlFacePo);

        saveAddFaceLog("", accessControlDto.getMachineId(), "/device/v2/face/sync",
                accessControlDto.getCommunityId(),
                param.toJSONString(), accessControlFacePo.getPersonId(), accessControlFacePo.getName());

        JSONObject paramIn = new JSONObject();
        paramIn.put("t", accessControlDto.getMachineCode());
        paramIn.put("c", "syncFace");
        paramIn.put("f", "server_php");

        JSONObject m = new JSONObject();
        m.put("p", "all");
        m.put("t", GenerateCodeFactory.getUUID());
        paramIn.put("m", m);

        MqttFactory.publish("zghl_door/" + accessControlDto.getMachineCode(), paramIn.toJSONString());

        if (StringUtil.isEmpty(accessControlFacePo.getCardNumber())) {
            return true;
        }

        //todo 人员卡信息

        param = syncUserCardImpl.getUserCard(accessControlDto, accessControlFacePo);

        saveAddFaceLog("", accessControlDto.getMachineId(), "/v3_1/sync/cards/add/sync",
                accessControlDto.getCommunityId(),
                param.toJSONString(), accessControlFacePo.getPersonId(), accessControlFacePo.getName());

        paramIn = new JSONObject();
        paramIn.put("t", accessControlDto.getMachineCode());
        paramIn.put("c", "syncCards");
        paramIn.put("f", "server_php");

        m = new JSONObject();
        m.put("p", "all");
        m.put("t", GenerateCodeFactory.getUUID());
        paramIn.put("m", m);

        MqttFactory.publish("zghl_door/" + accessControlDto.getMachineCode(), paramIn.toJSONString());


        return true;
    }

    @Override
    public boolean deleteUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        // todo 保存用户信息

        try {
            JSONObject param = syncUserImpl.getDeleteUser(accessControlDto, accessControlFacePo);

            saveAddFaceLog("", accessControlDto.getMachineId(), "/v3_1/sync/users/add/",
                    accessControlDto.getCommunityId(),
                    param.toJSONString(), accessControlFacePo.getPersonId(), accessControlFacePo.getName());

            JSONObject paramIn = new JSONObject();
            paramIn.put("t", accessControlDto.getMachineCode());
            paramIn.put("c", "syncUsers");
            paramIn.put("f", "server_php");

            JSONObject m = new JSONObject();
            m.put("p", "all");
            m.put("t", GenerateCodeFactory.getUUID());
            paramIn.put("m", m);

            MqttFactory.publish("zghl_door/" + accessControlDto.getMachineCode(), paramIn.toJSONString());

            //todo 人员卡信息

            param = syncUserCardImpl.getUserDeleteCard(accessControlDto, accessControlFacePo);

            saveAddFaceLog("", accessControlDto.getMachineId(), "/v3_1/sync/cards/add/sync",
                    accessControlDto.getCommunityId(),
                    param.toJSONString(), accessControlFacePo.getPersonId(), accessControlFacePo.getName());

            paramIn = new JSONObject();
            paramIn.put("t", accessControlDto.getMachineCode());
            paramIn.put("c", "syncCards");
            paramIn.put("f", "server_php");

            m = new JSONObject();
            m.put("p", "all");
            m.put("t", GenerateCodeFactory.getUUID());
            paramIn.put("m", m);

            MqttFactory.publish("zghl_door/" + accessControlDto.getMachineCode(), paramIn.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * {
     * "f": "18988888888",//手机发, server_php 服务端发、
     * "t": "8410789684082c1607d0",
     * "c": "openDoor",
     * "m": {
     * "t": "441da6eefef101225257c2b23281eccc"
     * }
     * }
     *
     * @param accessControlDto 门禁信息
     * @return
     */
    @Override
    public boolean openDoor(AccessControlDto accessControlDto) {
        JSONObject paramIn = new JSONObject();
        paramIn.put("t", accessControlDto.getMachineCode());
        paramIn.put("c", "openDoor");
        paramIn.put("f", "server_php");

        JSONObject m = new JSONObject();
        m.put("t", GenerateCodeFactory.getUUID());
        paramIn.put("m", m);

        MqttFactory.publish("zghl_door/" + accessControlDto.getMachineCode(), paramIn.toJSONString());
        return true;
    }

    /**
     * {
     * "t": "8410789684082c1607d0",
     * "c": "restartApp",
     * "m": {
     * "g": "8410789684082c1607d0",
     * "t": "d5c19f2bcc796091ff87c75463a11471"
     * },
     * "f": "server_php"
     * }
     *
     * @param accessControlDto 门禁信息
     * @return
     */
    @Override
    public boolean restartMachine(AccessControlDto accessControlDto) {

        JSONObject paramIn = new JSONObject();
        paramIn.put("t", accessControlDto.getMachineCode());
        paramIn.put("c", "restartGate");
        paramIn.put("f", "server_php");

        JSONObject m = new JSONObject();
        m.put("g", accessControlDto.getMachineCode());
        m.put("t", GenerateCodeFactory.getUUID());
        paramIn.put("m", m);

        MqttFactory.publish("zghl_door/" + accessControlDto.getMachineCode(), paramIn.toJSONString());

        return true;
    }

    @Override
    public String accessControlResult(String topic, String param) {

        logger.debug("topic={},data={}", topic, param);
        JSONObject paramObj = JSONObject.parseObject(param);
        String c = paramObj.getString("c");

        switch (c) {
            case "heartbeat":
                heartbeat(paramObj.getString("f"));
                break;
        }

        return null;
    }


    @Override
    public boolean addAd(AccessControlDto accessControlDto, AccessControlAdRelPo accessControlAdRelPo){
        JSONObject paramIn = new JSONObject();
        paramIn.put("t", accessControlDto.getMachineCode());
        paramIn.put("c", "syncAd");
        paramIn.put("f", "server_php");

        JSONObject m = new JSONObject();
        m.put("t", GenerateCodeFactory.getUUID());
        paramIn.put("m", m);

        MqttFactory.publish("zghl_door/" + accessControlDto.getMachineCode(), paramIn.toJSONString());
        return true;
    }
}
