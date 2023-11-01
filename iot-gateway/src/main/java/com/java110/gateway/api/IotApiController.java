package com.java110.gateway.api;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.cache.MappingCache;
import com.java110.core.client.OssUploadTemplate;
import com.java110.core.constant.MappingConstant;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.Assert;
import com.java110.gateway.service.IApiService;
import com.java110.gateway.smo.IAssetImportSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统统一入口类
 */
@RestController
@RequestMapping(path = "/iot/api")
public class IotApiController {
    Logger logger = LoggerFactory.getLogger(IotApiController.class);
    private static final String ROOT_PATH = "hc/iot/";
    @Autowired
    private IApiService apiServiceImpl;

    @Autowired
    private OssUploadTemplate ossUploadTemplate;

    @Autowired
    private IAssetImportSMO assetImportSMOImpl;


    /**
     * post 入口类
     *
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
     *
     * @param service
     * @param request
     * @return
     */
    @RequestMapping(path = "/{service:.+}", method = RequestMethod.GET)
    public ResponseEntity<String> serviceGet(@PathVariable String service,
                                             HttpServletRequest request) {
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = apiServiceImpl.get(service, request);
        } catch (Throwable e) {
            logger.error("请求get 方法[" + service + "]失败：", e);
            responseEntity = ResultVo.error("请求发生异常，" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.debug("api：{} 返回信息为：{}", service, responseEntity);

        return responseEntity;
    }

    /**
     * get 入口类
     *
     * @param uploadFile 上传文件
     * @param request
     * @return
     */
    @RequestMapping(path = "/upload/uploadFile", method = RequestMethod.POST)
    public ResponseEntity<String> uploadFile(@RequestParam("uploadFile") MultipartFile uploadFile,
                                             //@RequestBody String info,
                                             HttpServletRequest request) {
        ResponseEntity<String> responseEntity = null;

        String fileName = ossUploadTemplate.upload(uploadFile, ROOT_PATH);

        JSONObject outParam = new JSONObject();
        outParam.put("fileId", fileName);
        String imgUrl = MappingCache.getValue(MappingConstant.FILE_DOMAIN, "IMG_PATH");
        outParam.put("url", imgUrl + fileName);

        return ResultVo.createResponseEntity(outParam);
    }


    /**
     * get 入口类
     *
     * @param uploadFile 上传文件
     * @param request
     * @return
     */
    @RequestMapping(path = "/assetImport", method = RequestMethod.POST)
    public ResponseEntity<String> assetImport(@RequestParam("uploadFile") MultipartFile uploadFile,
                                              @RequestBody String postInfo,
                                              HttpServletRequest request) {
        ResponseEntity<String> responseEntity = null;


        responseEntity = assetImportSMOImpl.importData(postInfo, uploadFile, request);


        return responseEntity;
    }

}
