package com.java110.hal.http.yiteSmart.bmo.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.core.cache.CommonCache;
import com.java110.core.cache.MappingCache;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.community.CommunityDto;
import com.java110.hal.http.yiteSmart.bmo.IManagerLoginBmo;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.hal.http.yiteSmart.util.Gcj02ToBd09Util;
import com.java110.intf.community.ICommunityInnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ManagerLoginBmoImpl implements IManagerLoginBmo {

    Logger logger = LoggerFactory.getLogger(ManagerLoginBmoImpl.class);

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Autowired
    private ICommunityInnerServiceSMO communityInnerServiceSMOImpl;

    @Override
    public ResponseEntity<String> login(String uuid) {

//        String uuid = reqJson.getString("uuid");

        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineCode(uuid);
        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);

        if (ListUtil.isNull(accessControlDtos)) {
            throw new IllegalArgumentException("管理机设备不存在");
        }

        AccessControlDto machine = accessControlDtos.get(0);

        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(machine.getCommunityId());
        List<CommunityDto> communityDtos = communityInnerServiceSMOImpl.queryCommunitys(communityDto);
        if (ListUtil.isNull(communityDtos)) {
            throw new IllegalArgumentException("小区不存在");
        }

        CommunityDto community = communityDtos.get(0);

        String token = GenerateCodeFactory.getUUID();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 365);
        long expire = calendar.getTime().getTime() / 1000;

        CommonCache.setValue("YITESMART_MANAGER_TOKEN_" + uuid, token);

        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime() / 1000);

        JSONObject data = new JSONObject();
        data.put("expire", expire);
        paramOut.put("data", data);

        JSONObject gateData = new JSONObject();
        gateData.put("gate_id", machine.getMachineId());
        gateData.put("gate_name", machine.getMachineName());
        gateData.put("gate_alias", "");
        gateData.put("gate_setting_pwd", "00666666");
        gateData.put("gate_system_pwd", "00888888");
        gateData.put("gate_type", "3");
        gateData.put("gate_app_ver", "24.01.22");
        gateData.put("gate_system_ver", "rk32888.1.0");
        gateData.put("gate_guid", uuid);
        gateData.put("project_uid", community.getCommunityId());
        gateData.put("project_name", community.getName());
        gateData.put("stranger_capture_switch", 1);
        double[] bd = Gcj02ToBd09Util.convert(
                community.getLng(), community.getLat());
        gateData.put("project_latitude", String.valueOf(bd[1]));
        gateData.put("project_longitude", String.valueOf(bd[0]));

        String ip = MappingCache.getValue("YI_TE", "YI_IP");
        String password = MappingCache.getValue("YI_TE", "YI_PASSWORD");
        String username = MappingCache.getValue("YI_TE", "YI_USERNAME");
        String port = MappingCache.getValue("YI_TE", "YI_PORT");

        JSONObject mqtt = new JSONObject();
        mqtt.put("ip", ip);
        mqtt.put("port", port);
        mqtt.put("username", username);
        mqtt.put("mqtt_pwd", password);
        mqtt.put("publish_topic", "zghl_door/server_php");
        mqtt.put("subscribe_id", "SUBSCRIBE:" + community.getCommunityId() + ":" + uuid);
        mqtt.put("publish_id", "PUBLISH:" + community.getCommunityId() + ":" + uuid);
        JSONArray subTopics = new JSONArray();
        subTopics.add("zghl_door/" + uuid);
        subTopics.add("zghl_door/" + community.getCommunityId());
        mqtt.put("subscribe_topic", subTopics);
        gateData.put("mqtt", mqtt);


        String sipip = MappingCache.getValue("YI_TE", "SIP_IP");
        String sipport = MappingCache.getValue("YI_TE", "SIP_PORT");

        JSONObject sip = new JSONObject();
        sip.put("ip", sipip != null ? sipip : "39.108.64.137");
        sip.put("port", sipport != null ? sipport : "6050");
        String callingNum = accessControlDtos.get(0).getCallingNum();
        if (callingNum != null && !callingNum.isEmpty()) {
            sip.put("username", Integer.parseInt(callingNum));
        } else {
            sip.put("username", 10011);
        }
        sip.put("sip_pwd", callingNum != null ? callingNum : "Z$Xiaowo");
        gateData.put("sip", sip);

        data.put("data", gateData);
        data.put("token", token);

        logger.debug("管理机登录返回报文：{}", paramOut.toString());
        return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
    }
}
