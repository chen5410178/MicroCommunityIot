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
import com.java110.dto.community.CommunityDto;
import com.java110.hal.http.AccessControlHal;
import com.java110.hal.http.yiteSmart.bmo.*;
import com.java110.intf.accessControl.IAccessControlFloorV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.community.ICommunityInnerServiceSMO;
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

    @Autowired
    private ICommunityInnerServiceSMO communityInnerServiceSMOImpl;

    @Autowired
    private ISyncUserBidBmo syncUserBidBmoImpl;

    @Autowired
    private ISyncCardsBidBmo syncCardsBidBmoImpl;

    @Autowired
    private ISyncFaceBmo syncFaceBmoImpl;

    @Autowired
    private ILoginBmo loginBmoImpl;

    @Autowired
    private ISyncOneUserBmo syncOneUserBmoImpl;

    @Autowired
    private ISyncOneUserFaceBmo syncOneUserFaceBmoImpl;

    @Autowired
    private ISyncOneUserCardBmo syncOneUserCardBmoImpl;

    @Autowired
    private IOpenDoorBmo openDoorBmoImpl;

    @Autowired
    private IAdBmo adBmoImpl;

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

        ResponseEntity<String> responseEntity = null;
        try {
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
            responseEntity = loginBmoImpl.login(uuId);

        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
        return responseEntity;
    }


    /**
     * 实例:
     * {
     * "code": 200,
     * "msg": "",
     * "timestamp": 1626961185,
     * "data": {
     * "gate_button_password": "1",
     * "gate_button_phone": "1",
     * "gate_button_room": "1",
     * "gate_button_room_input": "1",
     * "gate_call_manager": "1",
     * "gate_capture_picture": "1",
     * "gate_check_face_switch": "0",
     * "gate_open_face": "1",
     * "gate_qr_pwd_state": "1",
     * "gate_register_face": "1",
     * "health_qr_code_switch": "0",
     * "pop_ad_switch": "0",
     * "property_fee_switch": "0",
     * "stranger_captures": "1",
     * "alarm_switch": "1",
     * "gate_log_open": 1,
     * "gate_log_cycle": 3
     * }
     * }
     *
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
        data.put("gate_button_password", "0");
        data.put("gate_button_phone", "0");
        data.put("gate_button_room", "1");
        data.put("gate_button_room_input", "1");
        data.put("gate_call_manager", "0");
        data.put("gate_capture_picture", "1");
        data.put("gate_check_face_switch", "1");
        data.put("gate_open_face", "1");
        data.put("gate_qr_pwd_state", "1");
        data.put("gate_register_face", "0");
        data.put("health_qr_code_switch", "0");
        data.put("pop_ad_switch", "0");
        data.put("property_fee_switch", "0");
        data.put("stranger_captures", "0");
        data.put("alarm_switch", "0");
        data.put("gate_log_open", 1);
        data.put("gate_log_cycle", 3);
        paramOut.put("data", data);
        logger.debug("登陆返回报文：{}", paramOut.toString());
        return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
    }


    /**
     * 同步单元下的所有人员信息
     *
     * @param request
     * @return
     */
    @RequestMapping(path = "/v3_1/sync/users/bid/{unitId}", method = RequestMethod.GET)
    public ResponseEntity<String> syncUsersBid(@PathVariable String unitId,
                                               HttpServletRequest request) {

        Map<String, String> headers = new HashMap<>();
        RequestUtils.initHeadParam(request, headers);
        RequestUtils.initUrlParam(request, headers);
        logger.debug("进入设备登陆接口,/v3_1/device/login,{}", headers);


        JSONObject reqJson = JSONObject.parseObject(headers.get("header"));

        String uuId = reqJson.getString("uuid");

        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = syncUserBidBmoImpl.sync(unitId, uuId);
        } catch (Exception e) {
            e.printStackTrace();
            long curTimeTimeStamp = new Date().getTime();
            curTimeTimeStamp = curTimeTimeStamp / 1000;
            JSONObject paramOut = new JSONObject();
            paramOut.put("code", 200);
            paramOut.put("msg", "");
            paramOut.put("timestamp", curTimeTimeStamp);
            JSONObject data = new JSONObject();
            paramOut.put("data", data);
            responseEntity = new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        return responseEntity;

    }

    /**
     * 实例:
     *
     * @param request
     * @return
     */
    @RequestMapping(path = "/v3_1/sync/cards/bid/{unitId}", method = RequestMethod.GET)
    public ResponseEntity<String> syncCardsBid(@PathVariable String unitId,
                                               HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        RequestUtils.initHeadParam(request, headers);
        RequestUtils.initUrlParam(request, headers);
        logger.debug("进入设备登陆接口,/v3_1/device/login,{}", headers);


        JSONObject reqJson = JSONObject.parseObject(headers.get("header"));

        String uuId = reqJson.getString("uuid");

        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = syncCardsBidBmoImpl.sync(unitId, uuId);
        } catch (Exception e) {
            e.printStackTrace();
            long curTimeTimeStamp = new Date().getTime();
            curTimeTimeStamp = curTimeTimeStamp / 1000;
            JSONObject paramOut = new JSONObject();
            paramOut.put("code", 200);
            paramOut.put("msg", "");
            paramOut.put("timestamp", curTimeTimeStamp);
            JSONObject data = new JSONObject();
            paramOut.put("data", data);
            responseEntity = new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }

    /**
     * 实例:
     *
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
     *
     * @param request
     * @return
     */
    @RequestMapping(path = "/device/v2/face/sync", method = RequestMethod.GET)
    public ResponseEntity<String> faceSync(
            HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        RequestUtils.initHeadParam(request, headers);
        RequestUtils.initUrlParam(request, headers);
        logger.debug("进入人脸获取接口,/device/v2/face/sync,{}", headers);
        JSONObject reqJson = JSONObject.parseObject(headers.get("header"));

        String uuId = reqJson.getString("uuid");
        int page = Integer.parseInt(request.getParameter("page"));
        int row =  Integer.parseInt(request.getParameter("size"));

        String t = request.getParameter("t");
        long curTimeTimeStamp = new Date().getTime();
        curTimeTimeStamp = curTimeTimeStamp / 1000;
        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", curTimeTimeStamp);
        JSONObject data = new JSONObject();
        paramOut.put("data", data);
        ResponseEntity<String> responseEntity = null;
        responseEntity = new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
        if ("0".equals(t)) {
            try {
                responseEntity = syncFaceBmoImpl.sync(uuId,page,row);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            try {
                responseEntity = syncOneUserFaceBmoImpl.syncUserFace(uuId);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        logger.debug("/device/v2/face/sync,返回内容：{}", responseEntity);
        return responseEntity;
    }

    /**
     * 实例:
     *
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
     *
     * @param request
     * @return
     */
    @RequestMapping(path = "/v3_1/sync/users/add/{t}", method = RequestMethod.GET)
    public ResponseEntity<String> syncUserAdd(
            @PathVariable String t,
            HttpServletRequest request) {
        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime());
        JSONObject data = new JSONObject();
        paramOut.put("data", data);
        logger.debug("/v3_1/sync/users/bid/：{}", paramOut.toString());
        if ("0".equals(t)) {
            return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
        }

        Map<String, String> headers = new HashMap<>();
        RequestUtils.initHeadParam(request, headers);
        RequestUtils.initUrlParam(request, headers);
        logger.debug("进入人脸获取接口,/device/v2/face/sync,{}", headers);
        JSONObject reqJson = JSONObject.parseObject(headers.get("header"));

        String uuId = reqJson.getString("uuid");


        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = syncOneUserBmoImpl.syncUser(uuId);
        } catch (Exception e) {
            e.printStackTrace();
            responseEntity = new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);

        }

        return responseEntity;
    }

    @RequestMapping(path = "/v3_1/sync/cards/add/{t}", method = RequestMethod.GET)
    public ResponseEntity<String> syncCardsAdd(
            @PathVariable String t,
            HttpServletRequest request) {
        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime());
        JSONObject data = new JSONObject();
        paramOut.put("data", data);
        logger.debug("/v3_1/sync/users/bid/：{}", paramOut.toString());
        if ("0".equals(t)) {
            return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
        }

        Map<String, String> headers = new HashMap<>();
        RequestUtils.initHeadParam(request, headers);
        RequestUtils.initUrlParam(request, headers);
        logger.debug("进入人脸获取接口,/v3_1/sync/cards/add/,{}", headers);
        JSONObject reqJson = JSONObject.parseObject(headers.get("header"));

        String uuId = reqJson.getString("uuid");


        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = syncOneUserCardBmoImpl.syncUserCard(uuId);
        } catch (Exception e) {
            e.printStackTrace();
            responseEntity = new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);

        }

        return responseEntity;
    }


    /**
     * 实例:
     *
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
        timestrap = timestrap / 1000;

        long curTimeTimeStamp = new Date().getTime();
        curTimeTimeStamp = curTimeTimeStamp / 1000;

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
                "    \"timestamp\": " + curTimeTimeStamp + "\n" +
                "}";
        JSONObject result = JSONObject.parseObject(body);

        logger.debug("用户返回报文：{}", result.toString());
        return new ResponseEntity<>(result.toJSONString(), HttpStatus.OK);
    }


    @RequestMapping(path = "/device/v1/face/users", method = RequestMethod.GET)
    public ResponseEntity<String> user(HttpServletRequest request) {


        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime());
        JSONObject data = new JSONObject();
        paramOut.put("data", data);
        logger.debug("/device/v1/face/users：{}", paramOut.toString());
        return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
    }


    @RequestMapping(path = "/device/v1/face/qiniu/token")
    public ResponseEntity<String> qiniuToken(HttpServletRequest request) {

        Map<String, String> headers = new HashMap<>();
        RequestUtils.initHeadParam(request, headers);
        RequestUtils.initUrlParam(request, headers);
        logger.debug("qiniuToken,/device/v1/face/qiniu/token/,{}", headers);
        JSONObject reqJson = JSONObject.parseObject(headers.get("header"));

        String uuId = reqJson.getString("uuid");

        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime());

        ResponseEntity<String> responseEntity = new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(path = "/device/v1/face/qiniu/token/{photo}")
    public ResponseEntity<String> qiniuToken(HttpServletRequest request, @PathVariable String photo) {

        Map<String, String> headers = new HashMap<>();
        RequestUtils.initHeadParam(request, headers);
        RequestUtils.initUrlParam(request, headers);
        logger.debug("qiniuToken,/device/v1/face/qiniu/token/,{},photo={}", headers, photo);
        JSONObject reqJson = JSONObject.parseObject(headers.get("header"));

        String uuId = reqJson.getString("uuid");


        openDoorBmoImpl.createPhoto(photo, request.getParameter("image"));


        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime());

        ResponseEntity<String> responseEntity = new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(path = "/device/v1/stranger/captures")
    public ResponseEntity<String> captures(HttpServletRequest request) {

        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime());

        ResponseEntity<String> responseEntity = new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
        return responseEntity;
    }


    @RequestMapping(path = "/v3_1/log/open_door", method = RequestMethod.POST)
    public ResponseEntity<String> openDoor(HttpServletRequest request) {

        Map<String, String> headers = new HashMap<>();
        RequestUtils.initHeadParam(request, headers);
        RequestUtils.initUrlParam(request, headers);
        logger.debug("开门记录接口,/v3_1/log/open_door,{}", headers);
        JSONObject reqJson = JSONObject.parseObject(headers.get("header"));

        String uuId = reqJson.getString("uuid");

        JSONObject reqParam = new JSONObject();

        reqParam.put("data", request.getParameter("data"));
        reqParam.put("type", request.getParameter("type"));
        reqParam.put("photo", request.getParameter("photo"));

        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = openDoorBmoImpl.openDoor(uuId, reqParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseEntity;
    }

    /**
     * /device/v2/ad?orientation=1&t=1724685762&sign=0c4379f7188be1f879c82a2dd8788126&baidu=0&uuid=zy20240529003
     * &app_id=10862002&local=9 HTTP/1.0" 500 199 "-" "okhttp-okgo/jeasonlzy" "60.222.137.151"
     *
     * @param request
     * @return
     */
    @RequestMapping(path = "/device/v2/ad")
    public ResponseEntity<String> ad(HttpServletRequest request) {


        Map<String, String> headers = new HashMap<>();
        RequestUtils.initHeadParam(request, headers);
        RequestUtils.initUrlParam(request, headers);
        logger.debug("查询广告门禁,/device/v2/ad,{}", headers);
        JSONObject reqJson = JSONObject.parseObject(headers.get("header"));

        String uuId = reqJson.getString("uuid");

        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = adBmoImpl.getAd(uuId, reqJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("查询广告门禁返回报文,{}", responseEntity);

        return responseEntity;
    }

}
