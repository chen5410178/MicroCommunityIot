package com.java110.accessControl.manufactor.adapt.accessControl.hik;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.accessControl.manufactor.AbstractAccessControlManufactorAdapt;
import com.java110.bean.ResultVo;
import com.java110.core.cache.MappingCache;
import com.java110.core.client.RestTemplate;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.*;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControl.AccessControlOweFeeDto;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.accessControlLog.AccessControlLogDto;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.po.accessControlFace.AccessControlFacePo;
import com.java110.po.accessControlLog.AccessControlLogPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Calendar;
import java.util.List;

/**
 * 海康门禁 （网关版）
 * <p>
 * 门禁设备 部署海康网关设备后添加；
 */
@Service("hikGatewayAccessControlAdapt")
public class HikGatewayAccessControlAdapt extends AbstractAccessControlManufactorAdapt {

    //todo 查询门禁设备
    public static final String ADD_PERSON = "/ISAPI/AccessControl/UserInfo/Record?format=json&devIndex=";

    public static final String DELETE_PERSON = "/ISAPI/AccessControl/UserInfoDetail/Delete?format=json&devIndex=";



    public static final String ADD_CARD = "/ISAPI/AccessControl/CardInfo/Record?format=json&devIndex=";

    public static final String ADD_FACE = "/ISAPI/Intelligent/FDLib/FaceDataRecord?format=json&devIndex=";

    public static final String GET_PERSON = "/ISAPI/AccessControl/UserInfo/Search?format=json&devIndex=";

    public static final String UPDATE_CARD = "/ISAPI/AccessControl/CardInfo/Modify?format=json&devIndex=";

    public static final String DELETE_FACE = "/ISAPI/Intelligent/FDLib/FDSearch/Delete?format=json&devIndex=";


    public static final String OPEN_DOOR = "/ISAPI/AccessControl/RemoteControl/door/<ID>?format=json&devIndex=";


    public static final String QUERY_OPEN_DOOR_RESULT = "/ISAPI/AccessControl/AcsEvent?format=json&devIndex=";



    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;


    @Autowired
    private RestTemplate outRestTemplate;

    @Override
    public boolean addUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {

        //todo 添加人员

//        {
//            "UserInfo" : [{
//            "employeeNo":"",
//                    /*必选项，string，工号（人员 ID）；长度根据能力集协议获取*/
//                    "name":"",
//                    /*可选项，string，人员姓名；长度根据能力集协议获取*/
//                    "Valid" :{
//                /*必选项，有效期；最早时间"1970-01-01T00:00:00"，最晚时间"2037-12-31T23:59:59"*/
//                "beginTime":"",
//                        /*必选项，有效期开始时间，例如，当地时间为"2017-08-01T17:30:08"；UTC 时间为"2017-08-01T17:30:08+08:00"*/
//                        "endTime":""
//                /*必选项，有效期开始时间，例如，当地时间为"2017-08-01T17:30:08"；UTC 时间为"2017-08-01T17:30:08+08:00"*/
//            }
//        }]
//        }
        String url = accessControlDto.getMachineIp() + ADD_PERSON + accessControlDto.getMachineCode();
        JSONObject reqParam = JSONObject.parseObject("{\n" +
                " \"UserInfo\" : [{\n" +
                " \"employeeNo\": \"" + accessControlFacePo.getPersonId() + "\", \n" +
                " \"name\": \"" + accessControlFacePo.getName() + "\", \n" +
                " \"Valid\" : {\n" +
                " \"beginTime\": \"" + accessControlFacePo.getStartTime().replace(" ", "T") + "\",\n" +
                " \"endTime\": \"" + accessControlFacePo.getEndTime().replace(" ", "T") + "\"\n" +
                " }\n" +
                " }]\n" +
                "}");
        String paramOutString = HttpClient.doPost(url, reqParam.toJSONString(), " ", "POST");

        JSONObject retParam = JSONObject.parseObject(paramOutString);
        if (retParam.containsKey("statusCode") && retParam.getIntValue("statusCode") != 1) {
            throw new IllegalArgumentException(retParam.getString("statusString"));
        }

        //todo 添加卡

        /**
         * {
         *  "CardInfo" : {
         *  "employeeNo": "",
         * /*必选项，string，工号（人员 ID）
         * "cardNo":"1234567890"
         * /*必选项，string，卡号
         *  }
         *}
         */
        url = accessControlDto.getMachineIp() + ADD_CARD + accessControlDto.getMachineCode();
        String cardId = accessControlFacePo.getCardNumber();
        if (StringUtil.isEmpty(cardId)) {
            cardId = "123456789";
        }
        reqParam = JSONObject.parseObject("{\n" +
                " \"CardInfo\" : {\n" +
                " \"employeeNo\": \"" + accessControlFacePo.getPersonId() + "\", \n" +
                " \"cardNo\": \"" + cardId + "\" \n" +
                " }\n" +
                "}");
        HttpClient.doPost(url, reqParam.toJSONString(), " ", "POST");

        //todo 添加人脸
        url = accessControlDto.getMachineIp() + ADD_FACE + accessControlDto.getMachineCode();
        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
        /**
         * {
         *  "FaceInfo": {
         *  "employeeNo": "",
         * /*必选项，string，人员 ID
         *"faceLibType":""
         * 必选项，string，名单库类型：infraredFD（红外名单库）、blackFD（非准入名单库）、ƐƚĂƟcF（静态名单库）
         *}
         *
         }
         */
        reqParam = JSONObject.parseObject("{\n" +
                " \"FaceInfo\": {\n" +
                " \"employeeNo\": \"" + accessControlFacePo.getPersonId() + "\"\n" +
                " }\n" +
                "}");
        postParameters.add("FaceDataRecord", reqParam);
        postParameters.add("FaceImage", ImageUtils.imageToBinary(accessControlFacePo.getFacePath()));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity(postParameters, httpHeaders);

        String logId = GenerateCodeFactory.getGeneratorId("11");
        saveAddFaceLog(logId, accessControlDto.getMachineId(), "uploadperson",
                accessControlDto.getCommunityId(),
                postParameters.toString(), accessControlFacePo.getPersonId(), accessControlFacePo.getName());

        ResponseEntity<String> responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

        retParam = JSONObject.parseObject(responseEntity.getBody());

        if (retParam.containsKey("statusCode") && retParam.getIntValue("statusCode") != 1) {
            cmdResult(-1, retParam.getString("statusString"), "uploadperson", accessControlDto.getMachineCode(), accessControlFacePo.getPersonId());
            return false;
        }

        cmdResult(0, "成功", "uploadperson", accessControlDto.getMachineCode(), accessControlFacePo.getPersonId());

        return true;
    }

    @Override
    public boolean updateUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        //todo 查询员工 如果不存在调用添加人脸
        /**
         * {
         *  "UserInfoSearchCond": {
         *  "searchID": "",
         * /*必选项，string，查询 ID，用于确认前后两次的搜索请求者是否相同；如果相同，搜索记录将被存储在设备中
         * 以加快下一次搜索
         * "serachResultPosition": 0,
         * /*必选项，integer32，搜索结果在结果列表的结束位置；如果在一次查询中，不能获取结果列表中的所有记录,
         * 可以标记结束位置,并在下次查询时直接获取到指定位置后的记录
         *"maxResults":30,
         * /*必选项，integer32，可获取的最大记录数；如 maxResults 的值大于设备能力集返回的范围，则设备按照能力
         * 集最大值返回，不进行报错
         *"EmployeeNoList" : [{
         * /*可选项，人员 ID 列表；当该节点不存在或者未配置是，表示将查询所有人员
         *"employeeNo":""
         * /*可选项，string，工号（人员 ID）
         *}]
         *}
         *
         }
         */
        String url = accessControlDto.getMachineIp() + GET_PERSON + accessControlDto.getMachineCode();

        JSONObject reqParam = JSONObject.parseObject(" {\n" +
                "  \"UserInfoSearchCond\": {\n" +
                "  \"searchID\": \""+GenerateCodeFactory.getUUID()+"\",\n" +
                " \"serachResultPosition\": 0,\n" +
                "\"maxResults\":30,\n" +
                "\"EmployeeNoList\" : [{\n" +
                "\"employeeNo\":\""+accessControlFacePo.getPersonId()+"\"\n" +
                "}]\n" +
                "}");
        String paramOutString = HttpClient.doPost(url, reqParam.toJSONString(), " ", "POST");

        JSONObject retParam = JSONObject.parseObject(paramOutString);
        if (retParam.containsKey("statusCode") && retParam.getIntValue("statusCode") != 1) {
            return addUser(accessControlDto,accessControlFacePo);
        }

        url = accessControlDto.getMachineIp() + UPDATE_CARD + accessControlDto.getMachineCode();
        String cardId = accessControlFacePo.getCardNumber();
        if (StringUtil.isEmpty(cardId)) {
            cardId = "123456789";
        }
        reqParam = JSONObject.parseObject("{\n" +
                " \"CardInfo\" : {\n" +
                " \"employeeNo\": \"" + accessControlFacePo.getPersonId() + "\", \n" +
                " \"cardNo\": \"" + cardId + "\" \n" +
                " }\n" +
                "}");
        HttpClient.doPost(url, reqParam.toJSONString(), " ", "PUT");

        url = accessControlDto.getMachineIp() + DELETE_FACE + accessControlDto.getMachineCode();
//        {
//            "FaceInfoDelCond": {
//            "EmployeeNoList" : [{
//                /*必选项，人员 ID 列表*/
//                "employeeNo": ""
//                /*必选项，string, 工号 ID（人员 ID）*/
//            }]
//        }
//        }
        reqParam = JSONObject.parseObject("{\n" +
                " \"FaceInfoDelCond\": {\n" +
                " \"EmployeeNoList\" : [{\n" +
                " \"employeeNo\": \""+accessControlFacePo.getPersonId()+"\" \n" +
                " }]\n" +
                " }\n" +
                "}");
        HttpClient.doPost(url, reqParam.toJSONString(), " ", "PUT");



//todo 添加人脸
        url = accessControlDto.getMachineIp() + ADD_FACE + accessControlDto.getMachineCode();
        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
        /**
         * {
         *  "FaceInfo": {
         *  "employeeNo": "",
         * /*必选项，string，人员 ID
         *"faceLibType":""
         * 必选项，string，名单库类型：infraredFD（红外名单库）、blackFD（非准入名单库）、ƐƚĂƟcF（静态名单库）
         *}
         *
         }
         */
        reqParam = JSONObject.parseObject("{\n" +
                " \"FaceInfo\": {\n" +
                " \"employeeNo\": \"" + accessControlFacePo.getPersonId() + "\"\n" +
                " }\n" +
                "}");
        postParameters.add("FaceDataRecord", reqParam);
        postParameters.add("FaceImage", ImageUtils.imageToBinary(accessControlFacePo.getFacePath()));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity(postParameters, httpHeaders);

        String logId = GenerateCodeFactory.getGeneratorId("11");
        saveUpdateFaceLog(logId, accessControlDto.getMachineId(), "uploadperson",
                accessControlDto.getCommunityId(),
                postParameters.toString(), accessControlFacePo.getPersonId(), accessControlFacePo.getName());

        ResponseEntity<String> responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

        retParam = JSONObject.parseObject(responseEntity.getBody());

        if (retParam.containsKey("statusCode") && retParam.getIntValue("statusCode") != 1) {
            cmdResult(-1, retParam.getString("statusString"), "uploadperson", accessControlDto.getMachineCode(), accessControlFacePo.getPersonId());
            return false;
        }

        cmdResult(0, "成功", "uploadperson", accessControlDto.getMachineCode(), accessControlFacePo.getPersonId());

        return true;
    }

    @Override
    public boolean deleteUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        String url = accessControlDto.getMachineIp() + GET_PERSON + accessControlDto.getMachineCode();

        JSONObject reqParam = JSONObject.parseObject(" {\n" +
                "  \"UserInfoSearchCond\": {\n" +
                "  \"searchID\": \""+GenerateCodeFactory.getUUID()+"\",\n" +
                " \"serachResultPosition\": 0,\n" +
                "\"maxResults\":30,\n" +
                "\"EmployeeNoList\" : [{\n" +
                "\"employeeNo\":\""+accessControlFacePo.getPersonId()+"\"\n" +
                "}]\n" +
                "}");
        String paramOutString = HttpClient.doPost(url, reqParam.toJSONString(), " ", "POST");

        JSONObject retParam = JSONObject.parseObject(paramOutString);
        if (retParam.containsKey("statusCode") && retParam.getIntValue("statusCode") != 1) {
            return true;
        }
        url = accessControlDto.getMachineIp() + DELETE_PERSON + accessControlDto.getMachineCode();

//        {
//            "UserInfoDetail" : {
//            "mode": "",
///必选项，string，删除模式："all"（全部删除）、"byEmployeeNo"（按工号或人员 ID 删除）*/
//            "EmployeeNoList" : [{
//                /*可选项，工号（人员 ID）列表*/
//                "employeeNo": ""
//                /*可选项，string， 工号（人员 ID）；当 mode 的值为"byEmployeeNo"时，该节点才有效*/
//            }]
//        }
//        }



        reqParam = JSONObject.parseObject("{\n" +
                 " \"UserInfoDetail\" : {\n" +
                 " \"mode\": \"all\", \n" +
                 " \"EmployeeNoList\" : [{\n" +
                 " \"employeeNo\": \""+accessControlFacePo.getPersonId()+"\" \n" +
                 " }]\n" +
                 " }\n" +
                 "}");
        saveDeleteFaceLog("", accessControlDto.getMachineId(), "/deleteperson",
                accessControlDto.getCommunityId(), reqParam.toJSONString(),
                accessControlFacePo.getPersonId(), accessControlFacePo.getName());
         paramOutString = HttpClient.doPost(url, reqParam.toJSONString(), " ", "PUT");

         retParam = JSONObject.parseObject(paramOutString);
        if (retParam.containsKey("statusCode") && retParam.getIntValue("statusCode") != 1) {
            cmdResult(-1, retParam.getString("statusString"), "uploadperson", accessControlDto.getMachineCode(), accessControlFacePo.getPersonId());
            return false;
        }
        cmdResult(0, "成功", "uploadperson", accessControlDto.getMachineCode(), accessControlFacePo.getPersonId());


        return true;
    }

    @Override
    public boolean openDoor(AccessControlDto accessControlDto) {

        String url = accessControlDto.getMachineIp() + OPEN_DOOR + accessControlDto.getMachineCode();

        /**
         * {
         *  "RemoteControlDoor": {
         *  "cmd":""
         * /*string，命令："open"（开），"close"（关），"alwaysOpen"（常开），"alwaysClose"（常关/禁用），"visitorCallLadder"
         * （访客呼梯），"householdCallLadder"（住户呼梯）
         *}
         *
}
         */
        JSONObject reqParam = JSONObject.parseObject(" {\n" +
                " \"RemoteControlDoor\": {\n" +
                " \"cmd\":\"open\" \n" +
                " }\n" +
                "}");
        String paramOutString = HttpClient.doPost(url, reqParam.toJSONString(), " ", "PUT");

        JSONObject retParam = JSONObject.parseObject(paramOutString);
        if (retParam.containsKey("statusCode") && retParam.getIntValue("statusCode") != 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean restartMachine(AccessControlDto accessControlDto) {


        return false;
    }

    @Override
    public String accessControlResult(String topic, String param) {

        JSONObject reqJson = JSONObject.parseObject(param);

        JSONObject eventNotificationAlert = reqJson.getJSONObject("EventNotificationAlert");

        if ("heartbeat".equals(eventNotificationAlert.getString("eventType"))) {
            heartbeat(topic);

            // todo 心跳时 触发 一次查询
            queryOpenDoorResult(topic);
            return SUCCESS;
        }
        return SUCCESS;
    }

    private void queryOpenDoorResult(String machineCode) {

        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineCode(machineCode);

       List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);

       if(ListUtil.isNull(accessControlDtos)){
           return;
       }
        accessControlDto = accessControlDtos.get(0);


        String url = accessControlDto.getMachineIp() + QUERY_OPEN_DOOR_RESULT + accessControlDto.getMachineCode();

//        {
//            "AcsEventCond": {
//            "searchID": "",
///*必选项，string，查询 ID，用于确认前后两次的搜索请求者是否相同；如果相同，搜索记录将被存储在设备中
//以加快下一次搜索*/
//                    ΗƐĞĂrcŚRĞƐƵůƚWŽƐŝƟŽnΗ͗ ,
///*必选项，integer32，搜索结果在结果列表的结束位置；如果在一次查询中，不能获取结果列表中的所有记录,
//可以标记结束位置,并在下次查询时直接获取到指定位置后的记录*/
//                    "maxResults": ,
///*必选项，integer32，可获取的最大记录数，依赖于设备能力。若 maxResults 值大于设备能力集返回的范围，
//则设备按照能力集最大值返回，不进行报错*/
//            "major": ,
///*可选项，int，事件主类型，参考事件上传宏定义，此处传递的是 10 进制数据，不能传递 16 进制，如传递
//1024 代表 0x400。不填默认为 0，表示全部事件/
// "minor": ,
///*可选项，int，事件次类型，参考事件上传宏定义，此处传递的是 10 进制数据，不能传递 16 进制，如传递
//1024 代表 0x400。不填默认为 0，表示全部事件/
// "startTime": "2016-12-12T17:30:08+08:00",
///*可选项，string，开始时间（UTC 时间）*/
//            "endTime": "2017-12-12T17:30:08+08:00",
//                    /*可选项，string，结束时间（UTC 时间）*/
//                    "cardNo": "",
//                    /*可选项，string，卡号*/
//                    "name": "",
//                    /*可选项，string，持卡人姓名*/
//                    "picEnable": ,
//            /*可选项，boolean，是否带图片：false（不带图片）、true（带图片）*/
//            "employeeNoString": ""
//            /*可选项，string，工号（人员 ID）*/
//        }
//        }
        JSONObject reqParam = JSONObject.parseObject(" {\n" +
                " \"AcsEventCond\": {\n"+
                " }\n" +
                "}");

        JSONObject acsEventCond = reqParam.getJSONObject("AcsEventCond");
        acsEventCond.put("searchID",GenerateCodeFactory.getUUID());
        acsEventCond.put("searchResultPosition",0);
        acsEventCond.put("maxResults",100);
        acsEventCond.put("startTime", DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A).replace(" ","T"));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE,1);
        acsEventCond.put("endTime",DateUtil.getFormatTimeStringA(calendar.getTime()).replace(" ","T"));
        String paramOutString = HttpClient.doPost(url, reqParam.toJSONString(), " ", "PUT");

        JSONObject retParam = JSONObject.parseObject(paramOutString);

        if (retParam.containsKey("statusCode") && retParam.getIntValue("statusCode") != 1) {
            return ;
        }

        JSONArray matchList = retParam.getJSONObject("AcsEvent").getJSONArray("MatchList");

        if(ListUtil.isNull(matchList)){
            return;
        }

        for(int matIndex = 0; matIndex < matchList.size();matIndex++){
            try{
                openDoorResult(matchList.getJSONObject(matIndex),accessControlDto);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private void openDoorResult(JSONObject data,AccessControlDto accessControlDto) {

        if (!data.containsKey("employeeNoString") || StringUtil.isEmpty(data.getString("employeeNoString"))) {
            return;
        }
        String staffId = data.getString("employeeNoString");
        String staffName = data.getString("person_name");
        String machineCode = accessControlDto.getMachineCode();
        if (StringUtil.isEmpty(staffId)) {
            staffId = "-1";
            staffName = "未知";
        }

        String currentVerifyMode = data.getString("currentVerifyMode");

        String openType = OPEN_TYPE_FACE;
        if("card".equals(openType)){
            openType = OPEN_TYPE_CARD;
        }

        String images = "";
        if (data.containsKey("pictureURL")) {
            images = data.getString("pictureURL");
            images = ImageUtils.getBase64ByImgUrl(images);
        }

        int similar = 100;
        if (data.containsKey("match_result")) {
            similar = data.getIntValue("match_result");
        }
        saveOpenDoorResult(machineCode, images, staffId, staffName, openType, similar);

    }


}
