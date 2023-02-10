package com.java110.gateway.api;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.factory.LoggerFactory;
import com.java110.gateway.service.IApiService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import com.java110.vo.ResultVo;

@RestController
@RequestMapping(path = "/iot/api")
public class IotApiController {
    Logger logger = LoggerFactory.getLogger(IotApiController.class);

    @Autowired
    private IApiService apiServiceImpl;


    @RequestMapping(path = "/{service:.+}", method = RequestMethod.POST)
    public ResponseEntity<String> servicePost(@PathVariable String service,
                                              @RequestBody String postInfo,
                                              HttpServletRequest request) {
        ResponseEntity<String> paramOut = null;
        try {
            paramOut = apiServiceImpl.post(service, postInfo, request);
        } catch (Exception e) {
            logger.error("网关异常", e);
            paramOut = ResultVo.error("请求发生异常，" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return paramOut;
    }

    @RequestMapping(path = "/{service:.+}", method = RequestMethod.GET)
    @ApiOperation(value = "资源get请求", notes = "test: 返回 2XX 表示服务正常")
    @ApiImplicitParam(paramType = "query", name = "service", value = "用户编号", required = true, dataType = "String")
    @Java110Lang
    public ResponseEntity<String> serviceGet(@PathVariable String service,
                                             HttpServletRequest request) {
        ResponseEntity<String> responseEntity = null;
        try {
            Map<String, String> headers = new HashMap<String, String>();
            this.getRequestInfo(request, headers);
            headers.put(CommonConstant.HTTP_SERVICE, service);
            headers.put(CommonConstant.HTTP_METHOD, CommonConstant.HTTP_METHOD_GET);
            logger.debug("api：{} 请求报文为：{},header信息为：{}", "", headers);
            IPageData pd = (IPageData) request.getAttribute(CommonConstant.CONTEXT_PAGE_DATA);
            privilegeSMOImpl.hasPrivilege(restTemplate, pd, "/app/" + service);
            responseEntity = apiSMOImpl.doApi(JSONObject.toJSONString(getParameterStringMap(request)), headers, request);

        } catch (Throwable e) {
            logger.error("请求get 方法[" + service + "]失败：", e);
            responseEntity = ResultVo.error("请求发生异常，" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.debug("api：{} 返回信息为：{}", service, responseEntity);

        return responseEntity;
    }


}
