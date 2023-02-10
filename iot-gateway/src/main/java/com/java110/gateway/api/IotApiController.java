package com.java110.gateway.api;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
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


@RestController
@RequestMapping(path = "/iot/api")
public class IotApiController {
    Logger logger = LoggerFactory.getLogger(IotApiController.class);

    @Autowired
    private IApiService apiServiceImpl;


    /**
     * post 入口类
     * @param service
     * @param postInfo
     * @param request
     * @return
     */
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

    /**
     * get 入口类
     * @param service
     * @param request
     * @return
     */
    @RequestMapping(path = "/{service:.+}", method = RequestMethod.GET)
    public ResponseEntity<String> serviceGet(@PathVariable String service,
                                             HttpServletRequest request) {
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = apiServiceImpl.get(service,request);
        } catch (Throwable e) {
            logger.error("请求get 方法[" + service + "]失败：", e);
            responseEntity = ResultVo.error("请求发生异常，" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.debug("api：{} 返回信息为：{}", service, responseEntity);

        return responseEntity;
    }


}
