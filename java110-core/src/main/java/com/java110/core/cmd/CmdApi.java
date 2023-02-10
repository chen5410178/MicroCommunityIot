package com.java110.core.cmd;

import com.java110.bean.dto.logSystemError.LogSystemErrorDto;
import com.java110.bean.po.logSystemError.LogSystemErrorPo;
import com.java110.core.constant.CommonConstant;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.smo.ICmdServiceSMO;
import com.java110.core.smo.ISaveSystemErrorSMO;
import com.java110.core.utils.ExceptionUtil;
import com.java110.core.utils.RequestUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 查询服务
 * add by wuxw on 2018/4/20.
 * modify by wuxw on 2019/4/20.
 *
 * @version 1.1
 */
@RestController
@RequestMapping(path = "/cmd")
public class CmdApi  {

    private final static Logger logger = LoggerFactory.getLogger(CmdApi.class);

    @Autowired
    private ICmdServiceSMO cmdServiceSMOImpl;

    @Autowired
    ISaveSystemErrorSMO saveSystemErrorSMOImpl;


    @RequestMapping(path = "/{service:.+}", method = RequestMethod.POST)
    public ResponseEntity<String> service(@PathVariable String service,
                                          @RequestBody String postInfo,
                                          HttpServletRequest request) {
        ResponseEntity<String> responseEntity = null;
        Map<String, String> headers = new HashMap<String, String>();
        try {

            this.getRequestInfo(request, headers);
            headers.put(CommonConstant.HTTP_SERVICE, service);
            headers.put(CommonConstant.HTTP_METHOD, CommonConstant.HTTP_METHOD_POST);
            logger.debug("api：{} 请求报文为：{},header信息为：{}", service, postInfo, headers);
            responseEntity = cmdServiceSMOImpl.cmd(postInfo, headers);
        } catch (Throwable e) {
            LogSystemErrorPo logSystemErrorPo = new LogSystemErrorPo();
            logSystemErrorPo.setErrId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_errId));
            logSystemErrorPo.setErrType(LogSystemErrorDto.ERR_TYPE_CMD);
            logSystemErrorPo.setMsg(ExceptionUtil.getStackTrace(e));
            saveSystemErrorSMOImpl.saveLog(logSystemErrorPo);
            logger.error("请求post 方法[" + service + "]失败：" + postInfo, e);
            responseEntity = new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.debug("cmd：{} 返回信息为：{}", service, responseEntity);
        return responseEntity;
    }

    /**
     * 资源请求 post方式
     *
     * @param resource 请求接口方式
     * @param postInfo post内容
     * @param request  请求对象 查询头信息 url等信息
     * @return http status 200 成功 其他失败
     */
    @RequestMapping(path = "/{resource}/{action}", method = RequestMethod.POST)
    public ResponseEntity<String> subServicePost(
            @PathVariable String resource,
            @PathVariable String action,
            @RequestBody String postInfo,
            HttpServletRequest request) {
        ResponseEntity<String> responseEntity = null;
        Map<String, String> headers = new HashMap<String, String>();
        try {
            this.getRequestInfo(request, headers);
            headers.put(CommonConstant.HTTP_SERVICE, "/" + resource + "/" + action);
            headers.put(CommonConstant.HTTP_RESOURCE, resource);
            headers.put(CommonConstant.HTTP_ACTION, action);
            headers.put(CommonConstant.HTTP_METHOD, CommonConstant.HTTP_METHOD_POST);
            logger.debug("api：{} 请求报文为：{},header信息为：{}", action, postInfo, headers);
            responseEntity = cmdServiceSMOImpl.cmd(postInfo, headers);
        } catch (Throwable e) {
            logger.error("请求post 方法[" + action + "]失败：" + postInfo, e);
            responseEntity = new ResponseEntity<String>("请求发生异常，" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.debug("cmd：{} 返回信息为：{}", action, responseEntity);
        return responseEntity;
    }

    /**
     * 获取请求信息
     *
     * @param request
     * @param headers
     * @throws RuntimeException
     */
    private void getRequestInfo(HttpServletRequest request, Map headers) throws Exception {
        try {
            RequestUtils.initHeadParam(request, headers);
            RequestUtils.initUrlParam(request, headers);
        } catch (Exception e) {
            logger.error("加载头信息失败", e);
            throw e;
        }
    }
}
