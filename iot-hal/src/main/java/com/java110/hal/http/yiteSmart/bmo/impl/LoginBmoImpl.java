package com.java110.hal.http.yiteSmart.bmo.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.core.cache.CommonCache;
import com.java110.core.cache.MappingCache;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControlFloor.AccessControlFloorDto;
import com.java110.dto.community.CommunityDto;
import com.java110.hal.http.yiteSmart.YiteSmartHal;
import com.java110.hal.http.yiteSmart.bmo.ILoginBmo;
import com.java110.intf.accessControl.IAccessControlFloorV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.community.ICommunityInnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginBmoImpl implements ILoginBmo {

    Logger logger = LoggerFactory.getLogger(LoginBmoImpl.class);


    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlFloorV1InnerServiceSMO accessControlFloorV1InnerServiceSMOImpl;

    @Autowired
    private ICommunityInnerServiceSMO communityInnerServiceSMOImpl;

    @Override
    public ResponseEntity<String> login(String machineCode) {

        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineCode(machineCode);
        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);

        if (ListUtil.isNull(accessControlDtos)) {
            throw new IllegalArgumentException("设备不存在");
        }

        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(accessControlDtos.get(0).getCommunityId());

        List<CommunityDto> communityDtos = communityInnerServiceSMOImpl.queryCommunitys(communityDto);
        if (ListUtil.isNull(communityDtos)) {
            throw new IllegalArgumentException("小区不存在");
        }

        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime() / 1000);
        JSONObject data = new JSONObject();
        paramOut.put("data", data);

        JSONObject socket = new JSONObject();
        socket.put("ip", "120.28.34.218");
        socket.put("port", "9700");
        socket.put("token", "abcdef");
        socket.put("interval", "90");
        data.put("socket", socket);


        String ip= MappingCache.getValue("YI_TE","YI_IP");
        String password= MappingCache.getValue("YI_TE","YI_PASSWORD");
        String username= MappingCache.getValue("YI_TE","YI_USERNAME");
        String port= MappingCache.getValue("YI_TE","YI_PORT");

        JSONObject mqtt = new JSONObject();
        mqtt.put("client_id", machineCode);
        mqtt.put("mqtt_pwd", password);
        mqtt.put("ip", ip);
        mqtt.put("port", port);
        mqtt.put("username", username);
        mqtt.put("publish_topic", "zghl_door/server_php");
        mqtt.put("subscribe_id", "SUBSCRIBE:" + accessControlDtos.get(0).getCommunityId() + ":" + machineCode);
        mqtt.put("publish_id", "PUBLISH:" + accessControlDtos.get(0).getCommunityId() + ":" + machineCode);
        JSONArray subTopics = new JSONArray();
        subTopics.add("zghl_door/" + machineCode);
        subTopics.add("zghl_door/" + accessControlDtos.get(0).getCommunityId());
        mqtt.put("subscribe_topic", subTopics);

        data.put("mqtt", mqtt);

        /**
         *                 "\t\t\"sip\": {\n" +
         *                 "\t\t\t\"ip\": \"39.108.64.137\",\n" +
         *                 "\t\t\t\"port\": \"6050\",\n" +
         *                 "\t\t\t\"username\": 101011,\n" +
         *                 "\t\t\t\"sip\": \"Z$Xiaowo\"\n" +
         *                 "\t\t},\n" +
         */
        JSONObject sip = new JSONObject();
        sip.put("ip", "39.108.64.137");
        sip.put("port", 6050);
        sip.put("username", 101011);
        sip.put("sip", "Z$Xiaowo");
        data.put("sip", sip);
        data.put("secret_key", "023917234");

        /**
         *                 "\t\t\"ad_config\": {\n" +
         *                 "\t\t\t\"ad_url\": \"https://adapi.***.com\",\n" +
         *                 "\t\t\t\"app_id\": \"10862002\",\n" +
         *                 "\t\t\t\"app_secret\": \"54b318acea40\"\n" +
         *                 "\t\t},\n" +
         */

        JSONObject adConfig = new JSONObject();
        adConfig.put("ad_url", "https://adapi.***.com");
        adConfig.put("app_id", "10862002");
        adConfig.put("app_secret", "54b318acea40");
        data.put("ad_config", adConfig);

        /**
         *            "\t\t\"b_id\": [//b_id与房屋，用户，卡等数据关联\n" +
         *                 "\t\"287$&2栋3单元\",//楼栋编号$&楼栋名称(建议按实际名称来)\n" +
         *                 "\"288$&2栋4单元\",\n" +
         *                 "\t\t\t  \"-1$&物业中心\"//单元门口机只有1条记录，围墙机就多个\n" +
         *                 "\t\t],\n" +
         */

        JSONArray b_id = new JSONArray();

        AccessControlFloorDto accessControlFloorDto = new AccessControlFloorDto();
        accessControlFloorDto.setMachineId(accessControlDtos.get(0).getMachineId());
        List<AccessControlFloorDto> accessControlFloorDtos
                = accessControlFloorV1InnerServiceSMOImpl.queryAccessControlFloors(accessControlFloorDto);

        if (ListUtil.isNull(accessControlFloorDtos)) {
            throw new IllegalArgumentException("设备未绑定楼栋");
        }

        for (AccessControlFloorDto tmpAccessControlFloorDto : accessControlFloorDtos) {
            b_id.add(tmpAccessControlFloorDto.getUnitId() + "$&" + tmpAccessControlFloorDto.getFloorNum() + "栋" + tmpAccessControlFloorDto.getUnitNum() + "单元");
        }

        b_id.add("-1$&物业中心");


        data.put("b_id", b_id);

        /**
         *  "\t\t\"type\": \"1\",\n" +
         *                 "\t\t\"address\": \"花园测试\",\n" +
         *                 "\t\t\"system_pwd\": \"00888\",\n" +
         *                 "\t\t\"setting_pwd\": \"006666\",\n" +
         *                 "\t\t\"app_latest\": \"v2.0\",\n" +
         *                 "\t\t\"app_url\": \"http://www.baidu.com\",\n" +
         *                 "\t\t\"reboot_at\": \"1,2,3,4,5,6,7|04:30\",\n" +
         *                 "\t\t\"talk_timeout\": \"55\",\n" +
         *                 "\t\t\"sync\": \"86400\",\n" +
         *                 "\t\t\"face\": \"1\",\n" +
         *                 "\t\t\"manage\": \"1\",\n" +
         *                 "\t\t\"baidu_face_type\": \"v2\",\n" +
         *                 "\t\t\"baidu_face_url\": \"https://aip.baidubce.com/rest/2.0/face/v2/identify\",\n" +
         */
        data.put("type", accessControlDtos.get(0).getMachineMac());
        data.put("address", communityDtos.get(0).getName() + "-" + accessControlDtos.get(0).getMachineName());
        data.put("system_pwd", "00888");
        data.put("setting_pwd", "006666");
        data.put("app_latest", "app_latest");
        data.put("app_url", "http://www.baidu.com");
        data.put("reboot_at", "1,2,3,4,5,6,7|04:30");
        data.put("talk_timeout", "55");
        data.put("sync", "86400");
        data.put("face", "1");
        data.put("manage", "1");
        data.put("baidu_face_type", "v2");
        data.put("baidu_face_url", "https://aip.baidubce.com/rest/2.0/face/v2/identify");


        /**
         *          "\t\t\"ad\": {\n" +
         *                 "\t\t\t\"local\": 9,\n" +
         *                 "\t\t\t\"baidu\": 0\n" +
         *                 "\t\t},\n" +
         */
        JSONObject ad = new JSONObject();
        ad.put("local", 9);
        ad.put("baidu", 0);
        data.put("ad", ad);

        /**
         *                 "\t\t\"token\": \"eyJ0eXBlIjoiSldUIiwiYWxnIjoiU0hBMjU2In0=.eyJpc3MiOiJkZXZlbG9wZXJAemhpZ3VvaHVsaWFuLmNvbSIsImlhdCI6MTYyNjcwMDQ1NiwiZXhwIjoxNjI3MzA1MjU2LCJ1aWQiOiIyMjAxOGFiMzIxODY3ODQ1NmYzNDZhZjBiM2Y2YWNhMyIsImlkIjoiZ2F0ZSIsInBhcmFtcyI6eyJnYXRlX3VpZCI6IjIyMDE4YWIzMjE4Njc4NDU2ZjM0NmFmMGIzZjZhY2EzIiwiZ2F0ZV9ndWlkIjoiODQxMDc4OTY4NDA4MmMxNjA3ZDAiLCJhcHBfaWQiOiIyMDIwMTAyMTE3NDAzMiIsInByb2plY3RfdWlkIjoiMWZlMDIwMzJhZWFiODRlYjAyMWNkYmZhYmNmMjY5NjYifX0=.65585f3313292b7d24fc8f9ba42cfe7f59cbc2e0c8ee47b9ca95ee4715bfe114.20201021174032\"\n" +
         */

        data.put("token", "eyJ0eXBlIjoiSldUIiwiYWxnIjoiU0hBMjU2In0=.eyJpc3MiOiJkZXZlbG9wZXJAemhpZ3VvaHVsaWFuLmNvbSIsImlhdCI6MTYyNjcwMDQ1NiwiZXhwIjoxNjI3MzA1MjU2LCJ1aWQiOiIyMjAxOGFiMzIxODY3ODQ1NmYzNDZhZjBiM2Y2YWNhMyIsImlkIjoiZ2F0ZSIsInBhcmFtcyI6eyJnYXRlX3VpZCI6IjIyMDE4YWIzMjE4Njc4NDU2ZjM0NmFmMGIzZjZhY2EzIiwiZ2F0ZV9ndWlkIjoiODQxMDc4OTY4NDA4MmMxNjA3ZDAiLCJhcHBfaWQiOiIyMDIwMTAyMTE3NDAzMiIsInByb2plY3RfdWlkIjoiMWZlMDIwMzJhZWFiODRlYjAyMWNkYmZhYmNmMjY5NjYifX0=.65585f3313292b7d24fc8f9ba42cfe7f59cbc2e0c8ee47b9ca95ee4715bfe114.20201021174032");


        logger.debug("登陆返回报文：{}", paramOut.toString());

        return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
    }
}
