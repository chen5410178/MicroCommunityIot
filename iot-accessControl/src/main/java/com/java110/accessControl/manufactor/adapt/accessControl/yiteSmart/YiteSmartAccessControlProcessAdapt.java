package com.java110.accessControl.manufactor.adapt.accessControl.yiteSmart;

import com.alibaba.fastjson.JSONObject;
import com.java110.accessControl.manufactor.AbstractAccessControlManufactorAdapt;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.po.accessControlFace.AccessControlFacePo;
import org.springframework.stereotype.Service;

/**
 * 一特智慧
 */
@Service("yiteSmartAccessControlProcessAdapt")
public class YiteSmartAccessControlProcessAdapt extends AbstractAccessControlManufactorAdapt {

    @Override
    public boolean initMachine(AccessControlDto machineDto) {
        MqttFactory.subscribe("zghl_door/server_php");
        return true;
    }

    @Override
    public boolean addUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        return false;
    }

    @Override
    public boolean updateUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        return false;
    }

    @Override
    public boolean deleteUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        return false;
    }

    /**
     * {
     * 	"f": "18988888888",//手机发, server_php 服务端发、
     * 	"t": "8410789684082c1607d0",
     * 	"c": "openDoor",
     * 	"m": {
     * 		"t": "441da6eefef101225257c2b23281eccc"
     * 	    }
     * }
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
        return false;
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
        paramIn.put("c", "restartApp");
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
        return null;
    }
}
