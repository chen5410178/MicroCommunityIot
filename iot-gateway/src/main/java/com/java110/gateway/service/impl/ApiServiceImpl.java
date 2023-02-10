package com.java110.gateway.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.utils.StringUtils;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.ValidateResult;
import com.java110.bean.dto.app.AppService;
import com.java110.core.client.RestTemplate;
import com.java110.core.constant.CommonConstant;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.RequestUtils;
import com.java110.core.utils.StringUtil;
import com.java110.gateway.service.IApiService;
import com.java110.gateway.service.IAppServiceCheck;
import jdk.nashorn.internal.ir.RuntimeNode;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class ApiServiceImpl implements IApiService {

    Logger logger = LoggerFactory.getLogger(ApiServiceImpl.class);

    public static final String CMD = "CMD";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IAppServiceCheck appServiceCheckImpl;

    @Override
    public ResponseEntity<String> post(String service, String postInfo, HttpServletRequest request) throws Exception {
        ResponseEntity<String> responseEntity = null;

        Map<String, String> headers = new HashMap<String, String>();
        this.getRequestInfo(request, headers);
        headers.put(CommonConstant.HTTP_SERVICE, service);
        headers.put(CommonConstant.HTTP_METHOD, CommonConstant.HTTP_METHOD_POST);
        logger.debug("api：{} 请求报文为：{},header信息为：{}", service, postInfo, headers);
        //privilegeSMOImpl.hasPrivilege(restTemplate, pd, "/app/" + service);
        responseEntity = doService(postInfo, headers, request);

        logger.debug("api：{} 返回信息为：{}", service, responseEntity);

        return responseEntity;
    }

    /**
     * Map<String, String> headers = new HashMap<String, String>();
     * this.getRequestInfo(request, headers);
     * headers.put(CommonConstant.HTTP_SERVICE, service);
     * headers.put(CommonConstant.HTTP_METHOD, CommonConstant.HTTP_METHOD_GET);
     * logger.debug("api：{} 请求报文为：{},header信息为：{}", "", headers);
     * IPageData pd = (IPageData) request.getAttribute(CommonConstant.CONTEXT_PAGE_DATA);
     * privilegeSMOImpl.hasPrivilege(restTemplate, pd, "/app/" + service);
     * responseEntity = apiSMOImpl.doApi(JSONObject.toJSONString(getParameterStringMap(request)), headers, request);
     *
     * @param service
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<String> get(String service, HttpServletRequest request) throws Exception {
        ResponseEntity<String> responseEntity = null;
        Map<String, String> headers = new HashMap<String, String>();
        this.getRequestInfo(request, headers);
        headers.put(CommonConstant.HTTP_SERVICE, service);
        headers.put(CommonConstant.HTTP_METHOD, CommonConstant.HTTP_METHOD_GET);
        String postInfo = JSONObject.toJSONString(RequestUtils.getParameterStringMap(request));
        logger.debug("api：{} 请求报文为：{},header信息为：{}", service, postInfo, headers);
        //privilegeSMOImpl.hasPrivilege(restTemplate, pd, "/app/" + service);
        responseEntity = doService(postInfo, headers, request);

        logger.debug("api：{} 返回信息为：{}", service, responseEntity);

        return responseEntity;
    }

    /**
     * 业主处理方法
     *
     * @param body    请求体
     * @param headers 请求头
     * @param request
     */
    private ResponseEntity<String> doService(String body, Map<String, String> headers, HttpServletRequest request) {
        ResponseEntity<String> responseEntity = null;
        //1.0 校验商户员工小区关系
        ValidateResult result = this.validateStoreStaffCommunityRelationship(body, headers);

        // header 中写入数据
        headers.put("store-id", result.getStoreId());
        headers.put("community-id", result.getCommunityId());
        headers.put("login-user-id", result.getLoginUserId());

        //2.0 接口权限校验处理 并且获取到APPService
        AppService appService = appServiceCheckImpl.checkAndGetAppService(body, headers);

        //3.0 业务处理
        if (CMD.equals(appService.getIsInstance())) {
            responseEntity = doCmdService(appService, body, headers);
        } else {
            throw new IllegalArgumentException("接口配置错误，接口时间目前只支持CMD模式");
        }

        return responseEntity;

    }

    private ResponseEntity<String> doCmdService(AppService appService, String body, Map<String, String> headers) {

        HttpHeaders tmpHeaders = new HttpHeaders();
        for (String key : headers.keySet()) {
            if ("user-name".equals(key)) {
                continue;
            }
            if ("userName".equals(key)) {
                continue;
            }
            tmpHeaders.add(key, headers.get(key));
        }
        HttpEntity<String> httpEntity = new HttpEntity<String>(body, tmpHeaders);
        String orgRequestUrl = headers.get("REQUEST_URL");

        String serviceCode = appService.getServiceCode();

        serviceCode = serviceCode.startsWith("/") ? serviceCode : ("/" + serviceCode);

        String requestUrl = appService.getUrl() + "/cmd" + serviceCode;
        //
        ResponseEntity responseEntity = null;
        if (!StringUtil.isNullOrNone(orgRequestUrl)) {
            String param = orgRequestUrl.contains("?") ? orgRequestUrl.substring(orgRequestUrl.indexOf("?") + 1, orgRequestUrl.length()) : "";
            requestUrl += ("?" + param);
        }
        try {
            responseEntity = restTemplate.exchange(requestUrl, HttpMethod.POST, httpEntity, String.class);

        } catch (HttpStatusCodeException e) { //这里spring 框架 在4XX 或 5XX 时抛出 HttpServerErrorException 异常，需要重新封装一下
            logger.error("请求下游服务【" + requestUrl + "】异常，参数为" + httpEntity + e.getResponseBodyAsString(), e);
            body = e.getResponseBodyAsString();

            if (StringUtil.isJsonObject(body)) {
                JSONObject bodyObj = JSONObject.parseObject(body);
                if (bodyObj.containsKey("message") && !StringUtil.isEmpty(bodyObj.getString("message"))) {
                    body = bodyObj.getString("message");
                }
            }
            responseEntity = new ResponseEntity<String>(body, e.getStatusCode());
        }

        logger.debug("API 服务调用下游服务请求：{}，返回为：{}", httpEntity, responseEntity);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            responseEntity = ResultVo.createResponseEntity(ResultVo.CODE_ERROR, String.valueOf(responseEntity.getBody()));
        }
        if (StringUtils.isEmpty(responseEntity.getBody() + "")) {
            responseEntity = ResultVo.createResponseEntity(ResultVo.CODE_ERROR, "处理失败");
        }
        return responseEntity;
    }


    private void getRequestInfo(HttpServletRequest request, Map headers) throws Exception {
        try {
            RequestUtils.initHeadParam(request, headers);
            RequestUtils.initUrlParam(request, headers);
            this.getUserInfo(request, headers);

        } catch (Exception e) {
            logger.error("加载头信息失败", e);
            throw e;
        }
    }


    private void getUserInfo(HttpServletRequest request, Map headers) throws Exception {
        Object claimsObj = request.getAttribute("claims");
        if (claimsObj == null) {
            return;
        }
        Map<String, String> claims = (Map<String, String>) claimsObj;

        for (String key : claims.keySet()) {

            if ("userId".equals(key)) {
                headers.put("user-id", claims.get(key));
            }
            headers.put(key, claims.get(key));
        }
    }


    protected ValidateResult validateStoreStaffCommunityRelationship(String body, Map<String, String> headers) {
        // 校验 员工和商户是否有关系
//        ResponseEntity responseEntity = getStoreInfo(pd, restTemplate);
//        if (responseEntity.getStatusCode() != HttpStatus.OK) {
//            return new ValidateResult("", "", "", pd.getUserId(), pd.getUserName());
//        }
//
//        JSONObject storeInfo = JSONObject.parseObject(responseEntity.getBody().toString());
//        if (!storeInfo.containsKey("storeId")) {
//            return new ValidateResult("", "", "", pd.getUserId(), pd.getUserName());
//        }
//
//        Assert.jsonObjectHaveKey(responseEntity.getBody().toString(), "storeId", "根据用户ID查询商户ID失败，未包含storeId节点");
//        Assert.jsonObjectHaveKey(responseEntity.getBody().toString(), "storeTypeCd", "根据用户ID查询商户类型失败，未包含storeTypeCd节点");
//
//        String storeId = JSONObject.parseObject(responseEntity.getBody().toString()).getString("storeId");
//        String storeTypeCd = JSONObject.parseObject(responseEntity.getBody().toString()).getString("storeTypeCd");
//
//        JSONObject paramIn = JSONObject.parseObject(pd.getReqData());
//
//        //开发者和运营不校验小区
//        if(StoreDto.STORE_TYPE_ADMIN.equals(storeTypeCd) || StoreDto.STORE_TYPE_DEV.equals(storeTypeCd)){
//            return new ValidateResult(storeId, storeTypeCd, "", pd.getUserId(), pd.getUserName());
//        }
//
//        String communityId = "";
//        if (paramIn != null && paramIn.containsKey("communityId")
//                && !StringUtil.isEmpty(paramIn.getString("communityId"))
//                && !"-1".equals(paramIn.getString("communityId"))) {
//            communityId = paramIn.getString("communityId");
//            checkStoreEnterCommunity(pd, storeId, storeTypeCd, communityId, restTemplate);
//        }
//        return new ValidateResult(storeId, storeTypeCd, communityId, pd.getUserId(), pd.getUserName());
        return null;
    }

}
