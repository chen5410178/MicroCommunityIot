package com.java110.hal.http.yiteSmart;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.RequestUtils;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControlFloor.AccessControlFloorDto;
import com.java110.hal.http.AccessControlHal;
import com.java110.intf.accessControl.IAccessControlFloorV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
public class YiteSmartHal {

    Logger logger = LoggerFactory.getLogger(YiteSmartHal.class);

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlFloorV1InnerServiceSMO accessControlFloorV1InnerServiceSMOImpl;

    /**
     * 设备核验
     *
     * @param request
     * @return
     */
    @RequestMapping(path = "/v3_1/device/test", method = RequestMethod.GET)
    public ResponseEntity<String> test(HttpServletRequest request) {


        Map<String, String> headers = new HashMap<>();
        RequestUtils.initHeadParam(request, headers);
        RequestUtils.initUrlParam(request, headers);

        logger.debug("进入设备校验接口,/v3_1/device/test,{}", headers.toString());

        JSONObject reqJson = JSONObject.parseObject(headers.get("header"));

        String uuId = reqJson.getString("uuid");

        try {
            if (StringUtil.isEmpty(uuId)) {
                throw new IllegalArgumentException("未包含设备ID");
            }

            AccessControlDto accessControlDto = new AccessControlDto();
            accessControlDto.setMachineCode(uuId);
            List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);

            if (ListUtil.isNull(accessControlDtos)) {
                throw new IllegalArgumentException("设备不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }

        JSONObject result = new JSONObject();
        result.put("code", 200);
        result.put("msg", "ok");
        result.put("timestamp", DateUtil.getCurrentDate().getTime());
        return new ResponseEntity<>(result.toJSONString(), HttpStatus.OK);
    }

    /**
     * 设备核验
     *
     * @param request
     * @return
     */
    @RequestMapping(path = "/v3_1/device/login", method = RequestMethod.POST)
    public ResponseEntity<String> login(@RequestBody String postInfo,
                                        HttpServletRequest request) {

        Map<String, String> headers = new HashMap<>();
        RequestUtils.initHeadParam(request, headers);
        RequestUtils.initUrlParam(request, headers);
        logger.debug("进入设备登陆接口,/v3_1/device/login,{}", headers);

        logger.debug("设备来登录，{}", postInfo);

        JSONObject reqJson = JSONObject.parseObject(headers.get("header"));

        String uuId = reqJson.getString("uuid");

        if (StringUtil.isEmpty(uuId)) {
            throw new IllegalArgumentException("未包含设备ID");
        }

        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineCode(uuId);
        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);

        if (ListUtil.isNull(accessControlDtos)) {
            throw new IllegalArgumentException("设备不存在");
        }

        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime());
        JSONObject data = new JSONObject();
        paramOut.put("data", data);

        JSONObject socket = new JSONObject();
        socket.put("ip", "120.28.34.218");
        socket.put("port", "9700");
        socket.put("token", "abcdef");
        socket.put("interval", "90");
        data.put("socket", socket);

        JSONObject mqtt = new JSONObject();
        mqtt.put("client_id", uuId);
        mqtt.put("mqtt_pwd", "wuxw2015");
        mqtt.put("ip", "49.232.150.183");
        mqtt.put("port", "2883");
        mqtt.put("username", "wuxw");
        mqtt.put("publish_topic", "zghl_door/server_php");
        mqtt.put("subscribe_id", "SUBSCRIBE:" + accessControlDtos.get(0).getCommunityId() + ":" + uuId);
        mqtt.put("publish_id", "PUBLISH:" + accessControlDtos.get(0).getCommunityId() + ":" + uuId);
        JSONArray subTopics = new JSONArray();
        subTopics.add("zghl_door/" + uuId);
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
        data.put("type",1);
        data.put("address","花园测试");
        data.put("system_pwd","00888");
        data.put("setting_pwd","006666");
        data.put("app_latest","app_latest");
        data.put("app_url","http://www.baidu.com");
        data.put("reboot_at","1,2,3,4,5,6,7|04:30");
        data.put("talk_timeout","55");
        data.put("sync","86400");
        data.put("face","1");
        data.put("manage","1");
        data.put("baidu_face_type","v2");
        data.put("baidu_face_url","https://aip.baidubce.com/rest/2.0/face/v2/identify");


        /**
         *          "\t\t\"ad\": {\n" +
         *                 "\t\t\t\"local\": 9,\n" +
         *                 "\t\t\t\"baidu\": 0\n" +
         *                 "\t\t},\n" +
         */
        JSONObject ad = new JSONObject();
        ad.put("local",9);
        ad.put("baidu",0);
        data.put("ad",ad);

        /**
         *                 "\t\t\"token\": \"eyJ0eXBlIjoiSldUIiwiYWxnIjoiU0hBMjU2In0=.eyJpc3MiOiJkZXZlbG9wZXJAemhpZ3VvaHVsaWFuLmNvbSIsImlhdCI6MTYyNjcwMDQ1NiwiZXhwIjoxNjI3MzA1MjU2LCJ1aWQiOiIyMjAxOGFiMzIxODY3ODQ1NmYzNDZhZjBiM2Y2YWNhMyIsImlkIjoiZ2F0ZSIsInBhcmFtcyI6eyJnYXRlX3VpZCI6IjIyMDE4YWIzMjE4Njc4NDU2ZjM0NmFmMGIzZjZhY2EzIiwiZ2F0ZV9ndWlkIjoiODQxMDc4OTY4NDA4MmMxNjA3ZDAiLCJhcHBfaWQiOiIyMDIwMTAyMTE3NDAzMiIsInByb2plY3RfdWlkIjoiMWZlMDIwMzJhZWFiODRlYjAyMWNkYmZhYmNmMjY5NjYifX0=.65585f3313292b7d24fc8f9ba42cfe7f59cbc2e0c8ee47b9ca95ee4715bfe114.20201021174032\"\n" +
         */

        data.put("token","eyJ0eXBlIjoiSldUIiwiYWxnIjoiU0hBMjU2In0=.eyJpc3MiOiJkZXZlbG9wZXJAemhpZ3VvaHVsaWFuLmNvbSIsImlhdCI6MTYyNjcwMDQ1NiwiZXhwIjoxNjI3MzA1MjU2LCJ1aWQiOiIyMjAxOGFiMzIxODY3ODQ1NmYzNDZhZjBiM2Y2YWNhMyIsImlkIjoiZ2F0ZSIsInBhcmFtcyI6eyJnYXRlX3VpZCI6IjIyMDE4YWIzMjE4Njc4NDU2ZjM0NmFmMGIzZjZhY2EzIiwiZ2F0ZV9ndWlkIjoiODQxMDc4OTY4NDA4MmMxNjA3ZDAiLCJhcHBfaWQiOiIyMDIwMTAyMTE3NDAzMiIsInByb2plY3RfdWlkIjoiMWZlMDIwMzJhZWFiODRlYjAyMWNkYmZhYmNmMjY5NjYifX0=.65585f3313292b7d24fc8f9ba42cfe7f59cbc2e0c8ee47b9ca95ee4715bfe114.20201021174032");


        logger.debug("登陆返回报文：{}", paramOut.toString());

        return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
    }


    /**
     * 实例:
     * {
     * 	"code": 200,
     * 	"msg": "",
     * 	"timestamp": 1626961185,
     * 	"data": {
     * 		"gate_button_password": "1",
     * 		"gate_button_phone": "1",
     * 		"gate_button_room": "1",
     * 		"gate_button_room_input": "1",
     * 		"gate_call_manager": "1",
     * 		"gate_capture_picture": "1",
     * 		"gate_check_face_switch": "0",
     * 		"gate_open_face": "1",
     * 		"gate_qr_pwd_state": "1",
     * 		"gate_register_face": "1",
     * 		"health_qr_code_switch": "0",
     * 		"pop_ad_switch": "0",
     * 		"property_fee_switch": "0",
     * 		"stranger_captures": "1",
     * 		"alarm_switch": "1",
     * 		"gate_log_open": 1,
     * 		"gate_log_cycle": 3
     * 	    }
     * }
     * @param request
     * @return
     */
    @RequestMapping(path = "/device/v1/gate/getSwitch", method = RequestMethod.GET)
    public ResponseEntity<String> getSwitch(HttpServletRequest request) {
        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime());
        JSONObject data = new JSONObject();
        data.put("gate_button_password","1");
        data.put("gate_button_phone","1");
        data.put("gate_button_room","1");
        data.put("gate_button_room_input","1");
        data.put("gate_call_manager","1");
        data.put("gate_capture_picture","1");
        data.put("gate_check_face_switch","0");
        data.put("gate_open_face","1");
        data.put("gate_qr_pwd_state","1");
        data.put("gate_register_face","1");
        data.put("health_qr_code_switch","0");
        data.put("pop_ad_switch","0");
        data.put("property_fee_switch","0");
        data.put("stranger_captures","1");
        data.put("alarm_switch","1");
        data.put("gate_log_open",1);
        data.put("gate_log_cycle",3);
        paramOut.put("data", data);
        logger.debug("登陆返回报文：{}", paramOut.toString());
        return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
    }


    /**
     * 实例:
     * @param request
     * @return
     */
    @RequestMapping(path = "/v3_1/sync/users/bid/{unitId}", method = RequestMethod.GET)
    public ResponseEntity<String> syncUsersBid(@PathVariable String unitId,
                                            HttpServletRequest request) {
        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime());
        JSONObject data = new JSONObject();
        paramOut.put("data", data);
        logger.debug("/v3_1/sync/users/bid/：{}", paramOut.toString());
        return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
    }

    /**
     * 实例:
     * @param request
     * @return
     */
    @RequestMapping(path = "/v3_1/sync/cards/bid/{unitId}", method = RequestMethod.GET)
    public ResponseEntity<String> syncCardsBid(@PathVariable String unitId,
                                               HttpServletRequest request) {
        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime());
        JSONObject data = new JSONObject();
        paramOut.put("data", data);
        logger.debug("/v3_1/sync/users/bid/：{}", paramOut.toString());
        return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
    }

    /**
     * 实例:
     * @param request
     * @return
     */
    @RequestMapping(path = "/v3_1/sync/password/add/{unitId}", method = RequestMethod.GET)
    public ResponseEntity<String> password(@PathVariable String unitId,
                                               HttpServletRequest request) {
        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime());
        JSONObject data = new JSONObject();
        paramOut.put("data", data);
        logger.debug("/v3_1/sync/passwords/add/：{}", paramOut.toString());
        return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
    }


    /**
     * 实例:
     * @param request
     * @return
     */
    @RequestMapping(path = "/device/v2/face/sync", method = RequestMethod.GET)
    public ResponseEntity<String> faceSync(
                                            HttpServletRequest request) {
        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime());
        JSONObject data = new JSONObject();
        paramOut.put("data", data);
        logger.debug("/v3_1/sync/users/bid/：{}", paramOut.toString());
        return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
    }

    /**
     * 实例:
     * @param request
     * @return
     */
    @RequestMapping(path = "/device/v3_1/sync/employee", method = RequestMethod.GET)
    public ResponseEntity<String> syncEmployee(
            HttpServletRequest request) {
        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime());
        JSONObject data = new JSONObject();
        paramOut.put("data", data);
        logger.debug("/v3_1/sync/users/bid/：{}", paramOut.toString());
        return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
    }

    /**
     * 实例:
     * @param request
     * @return
     */
    @RequestMapping(path = "/v3_1/sync/users/add/{userId}", method = RequestMethod.GET)
    public ResponseEntity<String> syncUserAdd(
            @PathVariable String userId,
            HttpServletRequest request) {
        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime());
        JSONObject data = new JSONObject();
        paramOut.put("data", data);
        logger.debug("/v3_1/sync/user/add/：{}", paramOut.toString());
        return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
    }

    /**
     * 实例:
     * @param request
     * @return
     */
    @RequestMapping(path = "/v3_1/sync/cards/add/{userId}", method = RequestMethod.GET)
    public ResponseEntity<String> syncCardsAdd(
            @PathVariable String userId,
            HttpServletRequest request) {
        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime());
        JSONObject data = new JSONObject();
        paramOut.put("data", data);
        logger.debug("/v3_1/sync/user/add/：{}", paramOut.toString());
        return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
    }

    /**
     * 实例:
     * @param request
     * @return
     */
    @RequestMapping(path = "/v3_1/sync/passwords/add/{userId}", method = RequestMethod.GET)
    public ResponseEntity<String> syncPasswordsAdd(
            @PathVariable String userId,
            HttpServletRequest request) {
        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime());
        JSONObject data = new JSONObject();
        paramOut.put("data", data);
        logger.debug("/v3_1/sync/user/add/：{}", paramOut.toString());

       // logger.debug("进入设备同步人脸,/device/v1/face/users,t={},{}", t, headers.toString());
        long timestrap = DateUtil.getDateFromStringB("2050-01-01").getTime();
        timestrap = timestrap/1000;

        long curTimeTimeStamp = new Date().getTime();
        curTimeTimeStamp = curTimeTimeStamp/1000;

        String body = "{\n" +
                "    \"code\": 200,\n" +
                "    \"msg\": \"success\",\n" +
                "    \"data\": {\n" +
                "        \"rooms\": [\n" +
                "            {\n" +
                "                \"b_id\": \"742023120517690335\",\n" +
                "                \"b_name\": [\n" +
                "                    \"1栋1单元\"\n" +
                "                ],\n" +
                "                \"add\": [\n" +
                "                    {\n" +
                "                        \"id\": 392188,\n" +
                "                        \"uid\": \"20ce9b7368ad918515650baee75420fd\",\n" +
                "                        \"name\": \"1003\",\n" +
                "                        \"num\": \"0101-1003\",\n" +
                "                        \"user\": [\n" +
                "                            {\n" +
                "                                \"id\": 416290,\n" +
                "                                \"uid\": \"6003e234c6f988a6fc7ab5275f766d1e\",\n" +
                "                                \"phone\": \"17782426083\",\n" +
                "                                \"expired\": " + timestrap + "\n" +
                "                            },\n" +
                "                            {\n" +
                "                                \"id\": 1004352,\n" +
                "                                \"uid\": \"7247871cfb24f48bf1877d8f6795cc23\",\n" +
                "                                \"phone\": \"17728043791\",\n" +
                "                                \"expired\": " + timestrap + "\n" +
                "                            }\n" +
                "                        ]\n" +
                "                    }],\n" +
                "                \"del\": {\n" +
                "                    \"del_room\": [],\n" +
                "                    \"del_user\": []\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"timestamp\": "+curTimeTimeStamp+"\n" +
                "}";
        JSONObject result = JSONObject.parseObject(body);

        logger.debug("用户返回报文：{}", result.toString());
        return new ResponseEntity<>(result.toJSONString(), HttpStatus.OK);
    }




    @RequestMapping(path = "/device/v1/face/users", method = RequestMethod.GET)
    public ResponseEntity<String> user(HttpServletRequest request) {

        Map<String, String> headers = new HashMap<>();
        RequestUtils.initHeadParam(request, headers);
        RequestUtils.initUrlParam(request, headers);

        String t = request.getParameter("t");

        logger.debug("进入设备同步人脸,/device/v1/face/users,t={},{}", t, headers.toString());
        long timestrap = DateUtil.getDateFromStringB("2050-01-01").getTime();
        timestrap = timestrap/1000;

        long curTimeTimeStamp = new Date().getTime();
        curTimeTimeStamp = curTimeTimeStamp/1000;

        String body = "{\n" +
                "    \"code\": 200,\n" +
                "    \"msg\": \"success\",\n" +
                "    \"data\": {\n" +
                "        \"rooms\": [\n" +
                "            {\n" +
                "                \"b_id\": \"742023120517690335\",\n" +
                "                \"b_name\": [\n" +
                "                    \"1栋1单元\"\n" +
                "                ],\n" +
                "                \"add\": [\n" +
                "                    {\n" +
                "                        \"id\": 392188,\n" +
                "                        \"uid\": \"20ce9b7368ad918515650baee75420fd\",\n" +
                "                        \"name\": \"1003\",\n" +
                "                        \"num\": \"0101-1003\",\n" +
                "                        \"user\": [\n" +
                "                            {\n" +
                "                                \"id\": 416290,\n" +
                "                                \"uid\": \"6003e234c6f988a6fc7ab5275f766d1e\",\n" +
                "                                \"phone\": \"17782426083\",\n" +
                "                                \"expired\": " + timestrap + "\n" +
                "                            },\n" +
                "                            {\n" +
                "                                \"id\": 1004352,\n" +
                "                                \"uid\": \"7247871cfb24f48bf1877d8f6795cc23\",\n" +
                "                                \"phone\": \"17728043791\",\n" +
                "                                \"expired\": " + timestrap + "\n" +
                "                            }\n" +
                "                        ]\n" +
                "                    }],\n" +
                "                \"del\": {\n" +
                "                    \"del_room\": [],\n" +
                "                    \"del_user\": []\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"timestamp\": "+curTimeTimeStamp+"\n" +
                "}";
        JSONObject result = JSONObject.parseObject(body);

        logger.debug("用户返回报文：{}", result.toString());


        return new ResponseEntity<>(result.toJSONString(), HttpStatus.OK);
    }
}
