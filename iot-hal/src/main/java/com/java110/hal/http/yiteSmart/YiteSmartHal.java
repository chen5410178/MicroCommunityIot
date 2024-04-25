package com.java110.hal.http.yiteSmart;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.RequestUtils;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.hal.http.AccessControlHal;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class YiteSmartHal {

    Logger logger = LoggerFactory.getLogger(YiteSmartHal.class);

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

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

        JSONObject result = JSONObject.parseObject("{\n" +
                "\t\"code\": 200,\n" +
                "\t\"msg\": \"\",\n" +
                "\t\"timestamp\": " + DateUtil.getCurrentDate().getTime() + ",\n" +
                "\t\"data\": {\n" +
                "\t\t\"socket\": {\n" +
                "\t\t\t\"ip\": \"120.28.34.218\",\n" +
                "\t\t\t\"port\": \"9700\",\n" +
                "\t\t\t\"token\": \"abcdef\",\n" +
                "\t\t\t\"interval\": \"90\"\n" +
                "\t\t},\n" +
                "\t\t\"mqtt\": {\n" +
                "\t\t\t\"client_id\": \"\",\n" +
                "\t\t\t\"mqtt_pwd\": \"XXX@122223$\",\n" +
                "\t\t\t\"ip\": \"120.28.147.218\",\n" +
                "\t\t\t\"port\": \"1443\",\n" +
                "\t\t\t\"username\": \"xiaozhi\",\n" +
                "\t\t\t\"subscribe_topic\": [\n" +
                "\t\t\t\t\"zghl_door/8410789684082c1607d0\", //设备ID\n" +
                "\t\t\t\t\"zghl_door/1fe02032aeab84eb021cdbfabcf26966\"  //项目小区ID\n" +
                "\t\t\t],\n" +
                "\t\t\t\"publish_topic\": \"zghl_door/server_php\",\n" +
                "\t\t\t\"subscribe_id\": \"SUBSCRIBE:36:8410789684082c1607d0\", //设备ID\n" +
                "\t\t\t\"publish_id\": \"PUBLISH:36:8410789684082c1607d0\" //设备ID\n" +
                "\t\t},\n" +
                "\t\t\"sip\": {\n" +
                "\t\t\t\"ip\": \"39.108.64.137\",\n" +
                "\t\t\t\"port\": \"6050\",\n" +
                "\t\t\t\"username\": 101011,\n" +
                "\t\t\t\"sip\": \"Z$Xiaowo\"\n" +
                "\t\t},\n" +
                "\t\t\"secret_key\": \"023917234\",\n" +
                "\t\t\"ad_config\": {\n" +
                "\t\t\t\"ad_url\": \"https://adapi.***.com\",\n" +
                "\t\t\t\"app_id\": \"10862002\",\n" +
                "\t\t\t\"app_secret\": \"54b318acea40\"\n" +
                "\t\t},\n" +
                "\t\t\"b_id\": [//b_id与房屋，用户，卡等数据关联\n" +
                "\t\"287$&2栋3单元\",//楼栋编号$&楼栋名称(建议按实际名称来)\n" +
                "\"288$&2栋4单元\",\n" +
                "\t\t\t  \"-1$&物业中心\"//单元门口机只有1条记录，围墙机就多个\n" +
                "\t\t],\n" +
                "\t\t\"type\": \"1\",\n" +
                "\t\t\"address\": \"花园测试\",\n" +
                "\t\t\"system_pwd\": \"00888\",\n" +
                "\t\t\"setting_pwd\": \"006666\",\n" +
                "\t\t\"app_latest\": \"v2.0\",\n" +
                "\t\t\"app_url\": \"http://www.baidu.com\",\n" +
                "\t\t\"reboot_at\": \"1,2,3,4,5,6,7|04:30\",\n" +
                "\t\t\"talk_timeout\": \"55\",\n" +
                "\t\t\"sync\": \"86400\",\n" +
                "\t\t\"face\": \"1\",\n" +
                "\t\t\"manage\": \"1\",\n" +
                "\t\t\"baidu_face_type\": \"v2\",\n" +
                "\t\t\"baidu_face_url\": \"https://aip.baidubce.com/rest/2.0/face/v2/identify\",\n" +
                "\t\t\"ad\": {\n" +
                "\t\t\t\"local\": 9,\n" +
                "\t\t\t\"baidu\": 0\n" +
                "\t\t},\n" +
                "\t\t\"token\": \"eyJ0eXBlIjoiSldUIiwiYWxnIjoiU0hBMjU2In0=.eyJpc3MiOiJkZXZlbG9wZXJAemhpZ3VvaHVsaWFuLmNvbSIsImlhdCI6MTYyNjcwMDQ1NiwiZXhwIjoxNjI3MzA1MjU2LCJ1aWQiOiIyMjAxOGFiMzIxODY3ODQ1NmYzNDZhZjBiM2Y2YWNhMyIsImlkIjoiZ2F0ZSIsInBhcmFtcyI6eyJnYXRlX3VpZCI6IjIyMDE4YWIzMjE4Njc4NDU2ZjM0NmFmMGIzZjZhY2EzIiwiZ2F0ZV9ndWlkIjoiODQxMDc4OTY4NDA4MmMxNjA3ZDAiLCJhcHBfaWQiOiIyMDIwMTAyMTE3NDAzMiIsInByb2plY3RfdWlkIjoiMWZlMDIwMzJhZWFiODRlYjAyMWNkYmZhYmNmMjY5NjYifX0=.65585f3313292b7d24fc8f9ba42cfe7f59cbc2e0c8ee47b9ca95ee4715bfe114.20201021174032\"\n" +
                "\t}\n" +
                "}");

        JSONObject mqtt = result.getJSONObject("data").getJSONObject("mqtt");
        mqtt.put("client_id", uuId);
        mqtt.put("mqtt_pwd", "wuxw2015");
        mqtt.put("ip", "49.232.150.183");
        mqtt.put("port", "1883");
        mqtt.put("username", "wuxw");
        mqtt.put("publish_topic", "zghl_door/server_php");
        mqtt.put("subscribe_id", "SUBSCRIBE:" + accessControlDtos.get(0).getCommunityId() + ":" + uuId);
        mqtt.put("publish_id", "PUBLISH:" + accessControlDtos.get(0).getCommunityId() + ":" + uuId);
        JSONArray subTopics = new JSONArray();
        subTopics.add("zghl_door/" + uuId);
        subTopics.add("zghl_door/" + accessControlDtos.get(0).getCommunityId());
        mqtt.put("subscribe_topic", subTopics);

        logger.debug("登陆返回报文：{}", request.toString());

        return new ResponseEntity<>(result.toJSONString(), HttpStatus.OK);
    }


    @RequestMapping(path = "/device/v1/face/users", method = RequestMethod.GET)
    public ResponseEntity<String> user(HttpServletRequest request){

        Map<String, String> headers = new HashMap<>();
        RequestUtils.initHeadParam(request, headers);
        RequestUtils.initUrlParam(request, headers);

        String t = request.getParameter("t");

        logger.debug("进入设备同步人脸,/device/v1/face/users,t={},{}",t, headers.toString());
        long timestrap = DateUtil.getCurrentDate().getTime();

        String body = "{\n" +
                "    \"code\": 200,\n" +
                "    \"msg\": \"success\",\n" +
                "    \"data\": {\n" +
                "        \"rooms\": [\n" +
                "            {\n" +
                "                \"b_id\": 288,\n" +
                "                \"b_name\": [\n" +
                "                    \"2栋4单元\"\n" +
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
                "                                \"expired\": "+timestrap+"\n" +
                "                            },\n" +
                "                            {\n" +
                "                                \"id\": 1004352,\n" +
                "                                \"uid\": \"7247871cfb24f48bf1877d8f6795cc23\",\n" +
                "                                \"phone\": \"17728043791\",\n" +
                "                                \"expired\": "+timestrap+"\n" +
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
                "    \"timestamp\": 1626702918\n" +
                "}";
        JSONObject result = JSONObject.parseObject(body);

        return new ResponseEntity<>(result.toJSONString(),HttpStatus.OK);
    }
}
