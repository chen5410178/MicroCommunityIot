package com.java110.gateway.service.impl;

import com.java110.core.constant.CommonConstant;
import com.java110.core.factory.LoggerFactory;
import com.java110.gateway.service.IApiService;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class ApiServiceImpl implements IApiService {

    Logger logger = LoggerFactory.getLogger(ApiServiceImpl.class);

    @Override
    public ResponseEntity<String> post(String service, String postInfo, HttpServletRequest request) throws Exception {
        ResponseEntity<String> responseEntity = null;

        Map<String, String> headers = new HashMap<String, String>();
        this.getRequestInfo(request, headers);
        headers.put(CommonConstant.HTTP_SERVICE, service);
        headers.put(CommonConstant.HTTP_METHOD, CommonConstant.HTTP_METHOD_POST);
        logger.debug("api：{} 请求报文为：{},header信息为：{}", service, postInfo, headers);
        IPageData pd = (IPageData) request.getAttribute(CommonConstant.CONTEXT_PAGE_DATA);
        privilegeSMOImpl.hasPrivilege(restTemplate, pd, "/app/" + service);
        responseEntity = apiSMOImpl.doApi(postInfo, headers, request);

        logger.debug("api：{} 返回信息为：{}", service, responseEntity);

        return responseEntity;
    }

    @Override
    public ResponseEntity<String> get(String service, HttpServletRequest request) {
        return null;
    }


    private void getRequestInfo(HttpServletRequest request, Map headers) throws Exception {
        try {
            super.initHeadParam(request, headers);
            super.initUrlParam(request, headers);
            this.getUserInfo(request, headers);

        } catch (Exception e) {
            logger.error("加载头信息失败", e);
            throw e;
        }
    }
}
