package com.java110.hal.http.yiteSmart;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.core.cache.CommonCache;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.factory.MqttFactory;
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
    private ISyncPasswordsAddBmo syncPasswordsAddBmoImpl;

    @Autowired
    private IOpenDoorBmo openDoorBmoImpl;

    @Autowired
    private IAdBmo adBmoImpl;

    @Autowired
    private IManagerLoginBmo managerLoginBmoImpl;

    @Autowired
    private IManagerGateListBmo managerGateListBmoImpl;

    @Autowired
    private IManagerViewDataBmo managerViewDataBmoImpl;

    @Autowired
    private IManagerBuildingsBmo managerBuildingsBmoImpl;

    @Autowired
    private IManagerRoomsBmo managerRoomsBmoImpl;

    @Autowired
    private IManagerUsersBmo managerUsersBmoImpl;

    @Autowired
    private IStrangerCaptureBmo strangerCaptureBmoImpl;

    @Autowired
    private ICheckCardBmo checkCardBmoImpl;

    @Autowired
    private IManagerSignVerifyBmo managerSignVerifyBmoImpl;

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
        result.put("timestamp", DateUtil.getCurrentDate().getTime() / 1000);
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

    @RequestMapping(path = "/device/v1/gate/adminSwitch", method = RequestMethod.GET)
    public ResponseEntity<String> adminSwitch(HttpServletRequest request) {
//        Map<String, String> headers = new HashMap<>();
//        RequestUtils.initHeadParam(request, headers);
//        RequestUtils.initUrlParam(request, headers);
//        JSONObject reqJson = JSONObject.parseObject(headers.get("header"));
//        String uuId = reqJson.getString("uuid");
        long curTimeTimeStamp = new Date().getTime();
        curTimeTimeStamp = curTimeTimeStamp / 1000;
        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", curTimeTimeStamp);
        JSONObject data = new JSONObject();
        data.put("isOpen",1);
        paramOut.put("data", data);
        logger.debug("登陆返回报文：{}", paramOut.toString());
        return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
    }

    @RequestMapping(path = "/property/v1/usergate/open")
    public ResponseEntity<String> usergateopen(HttpServletRequest request) {

        Map<String, String> headers = new HashMap<>();
        RequestUtils.initHeadParam(request, headers);
        RequestUtils.initUrlParam(request, headers);
        logger.debug("进入usergateopen,/property/v1/usergate/open/,{}", headers);
//        String uuId = getDeviceUuid(request, headers);

        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime());

        ResponseEntity<String> responseEntity = new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(path = "/v3_1/qiniu/token/")
    public ResponseEntity<String> token(HttpServletRequest request) {

        Map<String, String> headers = new HashMap<>();
        RequestUtils.initHeadParam(request, headers);
        RequestUtils.initUrlParam(request, headers);
        logger.debug("进入token,/v3_1/qiniu/token/,{}", headers);
//        String uuId = getDeviceUuid(request, headers);

        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime());

        ResponseEntity<String> responseEntity = new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(path = "/v3_1/qiniu/token/{filename}")
    public ResponseEntity<String> tokenWithFilename(HttpServletRequest request, @PathVariable String filename) {

        Map<String, String> headers = new HashMap<>();
        RequestUtils.initHeadParam(request, headers);
        RequestUtils.initUrlParam(request, headers);
        logger.debug("进入token,/v3_1/qiniu/token/{},filename={}", headers, filename);
//        String uuId = getDeviceUuid(request, headers);

        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime());

        ResponseEntity<String> responseEntity = new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
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
        Map<String, String> headers = new HashMap<>();
        RequestUtils.initHeadParam(request, headers);
        RequestUtils.initUrlParam(request, headers);
        JSONObject reqJson = JSONObject.parseObject(headers.get("header"));

        String uuId = reqJson.getString("uuid");

        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineCode(uuId);
        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);
        String getSwitchs = accessControlDtos.get(0).getRemark();
        if(getSwitchs.split("-", 17).length != 17){
            getSwitchs = "0-0-1-1-0-1-0-1-1-0-0-0-0-0-0-1-3";
        }
        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime() / 1000);
        JSONObject data = new JSONObject();
        data.put("gate_button_password", getSwitchs.split("-", 17)[0]); //门禁机密码开门开关
        data.put("gate_button_phone", getSwitchs.split("-", 17)[1]); //手机号呼叫开门开关
        data.put("gate_button_room", getSwitchs.split("-", 17)[2]); //房间号呼叫开门开关
        data.put("gate_button_room_input", getSwitchs.split("-", 17)[3]); //输入房间号呼叫开门开关
        data.put("gate_call_manager", getSwitchs.split("-", 17)[4]); //门禁呼叫管理处开关
        data.put("gate_capture_picture", getSwitchs.split("-", 17)[5]); //开门抓拍图片是否上传开关
        data.put("gate_check_face_switch", getSwitchs.split("-", 17)[6]); //活体检测开关
        data.put("gate_open_face", getSwitchs.split("-", 17)[7]); //门禁机人脸识别开关
        data.put("gate_qr_pwd_state", getSwitchs.split("-", 17)[8]); //扫码开门开关
        data.put("gate_register_face", getSwitchs.split("-", 17)[9]); //门禁机注册人脸开关
        data.put("health_qr_code_switch", getSwitchs.split("-", 17)[10]);
        data.put("pop_ad_switch", getSwitchs.split("-", 17)[11]); //弹屏广告开关
        data.put("property_fee_switch", getSwitchs.split("-", 17)[12]);
        data.put("stranger_captures", getSwitchs.split("-", 17)[13]); //陌生人抓拍开关
        data.put("alarm_switch", getSwitchs.split("-", 17)[14]); //防拆报警开关
        data.put("gate_log_open", getSwitchs.split("-", 17)[15]); //日志开关
        data.put("gate_log_cycle", getSwitchs.split("-", 17)[16]); //日志保存周期
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
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime() / 1000);
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
        long curTimeTimeStamp = new Date().getTime() / 1000;
        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", curTimeTimeStamp);
        JSONObject data = new JSONObject();
        paramOut.put("data", data);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
        if ("0".equals(t)) {
            try {
                responseEntity = syncFaceBmoImpl.sync(uuId,page,row);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                responseEntity = syncOneUserFaceBmoImpl.syncUserFace(uuId,page,row,t);
            } catch (Exception e) {
                e.printStackTrace();
                paramOut.put("timestamp", Long.parseLong(t));
                responseEntity = new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
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
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime() / 1000);
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
        paramOut.put("timestamp", "0".equals(t) ? DateUtil.getCurrentDate().getTime() / 1000 : Long.parseLong(t));
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
            responseEntity = syncOneUserBmoImpl.syncUser(uuId, t);
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
        paramOut.put("timestamp", "0".equals(t) ? DateUtil.getCurrentDate().getTime() / 1000 : Long.parseLong(t));
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
            responseEntity = syncOneUserCardBmoImpl.syncUserCard(uuId, t);
        } catch (Exception e) {
            e.printStackTrace();
            paramOut.put("timestamp", Long.parseLong(t));
            responseEntity = new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);

        }

        return responseEntity;
    }

    /**
     * 在线验证卡
     *
     * @param request
     * @return
     */
    @RequestMapping(path = "/v3_1/check/card", method = RequestMethod.POST)
    public ResponseEntity<String> checkCard(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        RequestUtils.initHeadParam(request, headers);
        RequestUtils.initUrlParam(request, headers);
        logger.debug("进入在线验证卡接口,/v3_1/check/card,{}", headers);

        JSONObject reqJson = JSONObject.parseObject(headers.get("header"));
        String uuId = reqJson.getString("uuid");
        String cardCode = request.getParameter("card_code");

        ResponseEntity<String> responseEntity;
        try {
            if (StringUtil.isEmpty(uuId)) {
                throw new IllegalArgumentException("未包含设备ID");
            }

            AccessControlDto accessControlDto = new AccessControlDto();
            accessControlDto.setMachineCode(uuId);
            List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);

            if (ListUtil.isNull(accessControlDtos)) {
                long timestamp = DateUtil.getCurrentDate().getTime() / 1000;
                JSONObject result = new JSONObject();
                result.put("code", 204);
                result.put("msg", "设备不存在");
                result.put("timestamp", timestamp);
                return new ResponseEntity<>(result.toJSONString(), HttpStatus.OK);
            }

            if (StringUtil.isEmpty(cardCode)) {
                long timestamp = DateUtil.getCurrentDate().getTime() / 1000;
                JSONObject result = new JSONObject();
                result.put("code", 204);
                result.put("msg", "未包含卡号");
                result.put("timestamp", timestamp);
                return new ResponseEntity<>(result.toJSONString(), HttpStatus.OK);
            }

            String machineId = accessControlDtos.get(0).getMachineId();
            responseEntity = checkCardBmoImpl.checkCard(uuId, machineId, cardCode);
        } catch (Exception e) {
            logger.error("在线验证卡异常", e);
            long timestamp = DateUtil.getCurrentDate().getTime() / 1000;
            JSONObject result = new JSONObject();
            result.put("code", 204);
            result.put("msg", "[" + cardCode + "] 卡号查询失败，请检查您的卡是否录入到物业系统，门卡是否能正常识别，设备是否支持门卡类型");
            result.put("timestamp", timestamp);
            responseEntity = new ResponseEntity<>(result.toJSONString(), HttpStatus.OK);
        }
        return responseEntity;
    }

    /**
     * 实例:
     *
     * @param request
     * @return
     */
    @RequestMapping(path = "/v3_1/sync/passwords/add/{t}", method = RequestMethod.GET)
    public ResponseEntity<String> syncPasswordsAdd(
            @PathVariable String t,
            HttpServletRequest request) {
        logger.debug("/v3_1/sync/passwords/add/, t={}", t);

        Map<String, String> headers = new HashMap<>();
        RequestUtils.initHeadParam(request, headers);
        RequestUtils.initUrlParam(request, headers);

        String machineCode = null;
        if (headers.containsKey("header")) {
            JSONObject headerJson = JSONObject.parseObject(headers.get("header"));
            if (headerJson != null) {
                machineCode = headerJson.getString("uuid");
            }
        }
        if (StringUtil.isEmpty(machineCode)) {
            machineCode = headers.get("uuid");
        }
        if (StringUtil.isEmpty(machineCode)) {
            JSONObject error = new JSONObject();
            error.put("code", 202);
            error.put("msg", "未包含设备ID(uuid)");
            error.put("timestamp", System.currentTimeMillis() / 1000);
            error.put("data", new JSONObject());
            return new ResponseEntity<>(error.toJSONString(), HttpStatus.OK);
        }

        return syncPasswordsAddBmoImpl.syncPasswordsAdd(machineCode, t);
    }


    @RequestMapping(path = "/device/v1/face/users", method = RequestMethod.GET)
    public ResponseEntity<String> user(HttpServletRequest request) {


        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime() / 1000);
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
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime() / 1000);

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
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime() / 1000);

        ResponseEntity<String> responseEntity = new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(path = "/device/v1/stranger/captures", method = RequestMethod.POST)
    public ResponseEntity<String> captures(HttpServletRequest request) {

        Map<String, String> headers = new HashMap<>();
        RequestUtils.initHeadParam(request, headers);
        RequestUtils.initUrlParam(request, headers);
        logger.debug("陌生人抓拍接口,/device/v1/stranger/captures,{}", headers);
        JSONObject reqJson = JSONObject.parseObject(headers.get("header"));

        String uuId = reqJson.getString("uuid");

        JSONObject reqParam = new JSONObject();
        reqParam.put("capture_type", request.getParameter("capture_type"));
        reqParam.put("capture_photo", request.getParameter("capture_photo"));
        reqParam.put("capture_time", request.getParameter("capture_time"));
        reqParam.put("ext", request.getParameter("ext"));
        reqParam.put("why", request.getParameter("why"));

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = strangerCaptureBmoImpl.capture(uuId, reqParam);
            String image = request.getParameter("image");
            if (!StringUtil.isEmpty(image)) {
                openDoorBmoImpl.createPhoto(request.getParameter("capture_photo"), image);
            }
        } catch (Exception e) {
            logger.error("陌生人抓拍异常", e);
            JSONObject paramOut = new JSONObject();
            paramOut.put("code", 500);
            paramOut.put("msg", "内部错误");
            paramOut.put("timestamp", DateUtil.getCurrentDate().getTime() / 1000);
            responseEntity = new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
        }
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

    /**
     * 管理机登录
     */
    @RequestMapping(path = "/property/v1/gate/login", method = RequestMethod.POST)

    public ResponseEntity<String> managerLogin(@RequestBody String postInfo, HttpServletRequest request) {
        ResponseEntity<String> responseEntity = null;
        try {
            Map<String, String> headers = new HashMap<>();
            RequestUtils.initHeadParam(request, headers);
            RequestUtils.initUrlParam(request, headers);

            JSONObject reqJson = JSONObject.parseObject(postInfo);

            JSONObject paramObj = reqJson.getJSONObject("param");
            String uuid = null;
            String appVer = null;
            String systemVer = null;

            if (paramObj != null) {
                uuid = paramObj.getString("uuid");
            } else {
                uuid = reqJson.getString("uuid");
            }

            if (StringUtil.isEmpty(uuid)) {
                JSONObject headerJson = JSONObject.parseObject(headers.get("header"));
                if (headerJson != null) {
                    uuid = headerJson.getString("uuid");
                }
            }

            if (StringUtil.isEmpty(uuid)) {
                throw new IllegalArgumentException("未包含设备ID");
            }
            if (paramObj != null) {
                CommonCache.setValue("YITESMART_MANAGER_PARAM_" + uuid, paramObj.toJSONString(), 10 * 24 * 60 * 60);
            }
            responseEntity = managerLoginBmoImpl.login(uuid);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
        return responseEntity;
    }

    /**
     * 管理机设备列表
     */
    @RequestMapping(path = "/property/v1/gate/list", method = RequestMethod.GET)
    public ResponseEntity<String> managerGateList(HttpServletRequest request) {
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = managerSignVerifyBmoImpl.verify(request);
            if (responseEntity != null) {
                return responseEntity;
            }
            String uuId = request.getParameter("uuid");
            responseEntity = managerGateListBmoImpl.gateList(uuId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
        return responseEntity;
    }

    /**
     * 管理机首页数据
     */
    @RequestMapping(path = "/property/v1/gate/viewdata", method = RequestMethod.GET)
    public ResponseEntity<String> managerViewData(HttpServletRequest request) {
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = managerSignVerifyBmoImpl.verify(request);
            if (responseEntity != null) {
                return responseEntity;
            }
            String uuId = request.getParameter("uuid");
            responseEntity = managerViewDataBmoImpl.viewData(uuId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
        return responseEntity;
    }

    /**
     * 管理机楼栋列表
     */
    @RequestMapping(path = "/property/v1/buildings", method = RequestMethod.GET)
    public ResponseEntity<String> managerBuildings(HttpServletRequest request) {
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = managerSignVerifyBmoImpl.verify(request);
            if (responseEntity != null) {
                return responseEntity;
            }
            String uuId = request.getParameter("uuid");
            responseEntity = managerBuildingsBmoImpl.buildings(uuId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
        return responseEntity;
    }

    /**
     * 管理机房间列表
     */
    @RequestMapping(path = "/property/v1/rooms", method = RequestMethod.GET)
    public ResponseEntity<String> managerRooms(HttpServletRequest request) {
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = managerSignVerifyBmoImpl.verify(request);
            if (responseEntity != null) {
                return responseEntity;
            }
            String buildingUid = request.getParameter("building_uid");
            String uuId = request.getParameter("uuid");
            responseEntity = managerRoomsBmoImpl.rooms(buildingUid, uuId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
        return responseEntity;
    }

    /**
     * 管理机用户列表
     */
    @RequestMapping(path = "/property/v1/users", method = RequestMethod.GET)
    public ResponseEntity<String> managerUsers(HttpServletRequest request) {
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = managerSignVerifyBmoImpl.verify(request);
            if (responseEntity != null) {
                return responseEntity;
            }
            String buildingUid = request.getParameter("building_uid");
            String roomUid = request.getParameter("room_uid");
            String uuId = request.getParameter("uuid");
            logger.debug("管理机用户列表请求：building_uid={}, room_uid={}, uuid={}", buildingUid, roomUid, uuId);
            responseEntity = managerUsersBmoImpl.users(buildingUid, roomUid, uuId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
        return responseEntity;
    }

    /**
     * 管理机CCTV呼叫门禁机
     * GET /property/v1/cctv/call
     * 参数：expire, gate_guid, model, sig, sign, ts, uuid
     */
    @RequestMapping(path = "/property/v1/cctv/call", method = RequestMethod.GET)
    public ResponseEntity<String> cctvCall(HttpServletRequest request) {
//        System.out.println("========== CCTV呼叫请求开始 ==========");
//        System.out.println("请求URL: " + request.getRequestURL());
//        System.out.println("QueryString: " + request.getQueryString());
        Map<String, String> headers = new HashMap<>();
        RequestUtils.initHeadParam(request, headers);
        RequestUtils.initUrlParam(request, headers);
        System.out.println("进入管理机登录接口,/property/v1/gate/login");
        System.out.println("headers: " + headers);
        System.out.println("请求方法: " + request.getMethod());
        System.out.println("请求URL: " + request.getRequestURL());
        System.out.println("请求参数: " + request.getQueryString());
        System.out.println("Content-Type: " + request.getContentType());
        System.out.println("Content-Length: " + request.getContentLength());
        System.out.println("CharacterEncoding: " + request.getCharacterEncoding());

        Map<String, String> params = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = request.getParameter(paramName);
            params.put(paramName, paramValue);
            System.out.println("URL参数 " + paramName + ": " + paramValue);
        }

        String uuid = params.get("uuid");
        String gateGuid = params.get("gate_guid");
        String sign = params.get("sign");

        System.out.println("解析参数: uuid=" + uuid + ", gate_guid=" + gateGuid + ", sign=" + sign);

        if (StringUtil.isEmpty(uuid)) {
//            return buildErrorResponse(202, "未包含设备ID(uuid)", System.currentTimeMillis() / 1000);
        }
        if (StringUtil.isEmpty(gateGuid)) {
//            return buildErrorResponse(202, "未包含门禁机编号(gate_guid)", System.currentTimeMillis() / 1000);
        }

        try {
            // 1. 查询管理机设备信息（根据uuid）
            AccessControlDto manageQuery = new AccessControlDto();
            manageQuery.setMachineCode(uuid);
            List<AccessControlDto> manageDevices = accessControlV1InnerServiceSMOImpl.queryAccessControls(manageQuery);
            if (ListUtil.isNull(manageDevices)) {
//                return buildErrorResponse(202, "管理机设备不存在", System.currentTimeMillis() / 1000);
            }
            AccessControlDto manageDevice = manageDevices.get(0);
            System.out.println("管理机信息: machineCode=" + manageDevice.getMachineCode() + ", callingNum=" + manageDevice.getCallingNum());

            // 2. 查询目标门禁机设备信息（根据gate_guid）
            AccessControlDto doorQuery = new AccessControlDto();
            doorQuery.setMachineCode(gateGuid);
            List<AccessControlDto> doorDevices = accessControlV1InnerServiceSMOImpl.queryAccessControls(doorQuery);
            if (ListUtil.isNull(doorDevices)) {
//                return buildErrorResponse(202, "门禁机设备不存在", System.currentTimeMillis() / 1000);
            }
            AccessControlDto doorDevice = doorDevices.get(0);
            System.out.println("门禁机信息: machineCode=" + doorDevice.getMachineCode() + ", machineName=" + doorDevice.getMachineName());

            // 3. 发送MQTT消息到门禁机，通知管理机呼叫
            JSONObject mqttMsg = new JSONObject();
            mqttMsg.put("f", "server_php");
            mqttMsg.put("t", gateGuid);
            mqttMsg.put("c", "cctv");
            JSONObject m = new JSONObject();
            m.put("p", "物管机");
            m.put("s", Integer.parseInt(manageDevice.getCallingNum()));
            m.put("t", GenerateCodeFactory.getUUID());

//            mqttMsg.put("f", "zghl_door/server_php");
//            mqttMsg.put("t", "jwja250424696");
//            mqttMsg.put("c", "callUp");
//            JSONObject m = new JSONObject();
//            m.put("g", "jwja250424696");
//            m.put("u", "");
//            m.put("n", "管理机");
//            m.put("c", "小区");
//            m.put("t", 1777126486);
//            m.put("s", 10022);
//            m.put("p", 10022);
            mqttMsg.put("m", m);

            System.out.println("发送MQTT消息: topic=zghl_door/" + gateGuid + ", msg=" + mqttMsg.toJSONString());
            MqttFactory.publish("zghl_door/" + gateGuid, mqttMsg.toJSONString());

            // 4. 返回成功响应
            long curTimeTimeStamp = new Date().getTime();
            curTimeTimeStamp = curTimeTimeStamp / 1000;
            JSONObject paramOut = new JSONObject();
            paramOut.put("code", 200);
            paramOut.put("msg", "");
            paramOut.put("timestamp", curTimeTimeStamp);

            // 构造 data 数组（列表）
            JSONArray dataArray = new JSONArray();

            // 第一个对象
            JSONObject data1 = new JSONObject();
            data1.put("phone", "20250826123456789456");
            data1.put("timeout", "55");
            data1.put("sip", 21920);
            data1.put("call_id", "call20250904132703U2y72331007635854R420020");
            dataArray.add(data1);

            // 第二个对象
            JSONObject data2 = new JSONObject();
            data2.put("phone", "ys01212202853610");
            data2.put("timeout", "55");
            data2.put("sip", 10011);
            data2.put("call_id", "call20250904132703U2y72331007635854R420020");
            dataArray.add(data2);

            // 放入返回结果
            paramOut.put("data", dataArray);

            System.out.println("cctv返回报文"+ paramOut.toString());
            return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);

        } catch (Exception e) {
            System.out.println("处理CCTV呼叫请求异常: " + e.getMessage());
            e.printStackTrace();
            return null ;
        }
    }
    @RequestMapping(path = "/device/v1/call/callup", method = RequestMethod.POST)
    public ResponseEntity<String> callCallup(HttpServletRequest request) {
        //        Map<String, String> headers = new HashMap<>();
        //        RequestUtils.initHeadParam(request, headers);
        //        RequestUtils.initUrlParam(request, headers);
        //        JSONObject reqJson = JSONObject.parseObject(headers.get("header"));
        //        String uuId = reqJson.getString("uuid");
        long curTimeTimeStamp = new Date().getTime();
        curTimeTimeStamp = curTimeTimeStamp / 1000;
        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", curTimeTimeStamp);

        // 构造 data 数组（列表）
        JSONArray dataArray = new JSONArray();

        // 第一个对象
        JSONObject data1 = new JSONObject();
        data1.put("phone", "20250826123456789456");
        data1.put("timeout", "55");
        data1.put("sip", 10011);
        data1.put("call_id", "call20250904132703U2y72331007635854R420020");
        dataArray.add(data1);

//        // 第二个对象
//        JSONObject data2 = new JSONObject();
//        data2.put("phone", "ys01212202853610");
//        data2.put("timeout", "55");
//        data2.put("sip", 21920);
//        data2.put("call_id", "call20250904132703U2y72331007635854R420020");
//        dataArray.add(data2);

        // 放入返回结果
        paramOut.put("data", dataArray);

        System.out.println("callup返回报文：{}"+ paramOut.toString());
        return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
    }
}
