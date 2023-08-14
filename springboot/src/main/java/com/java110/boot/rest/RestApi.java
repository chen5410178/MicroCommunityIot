package com.java110.boot.rest;

import com.java110.core.factory.LoggerFactory;
import com.java110.doc.annotation.Java110ApiDoc;
import com.java110.doc.annotation.Java110RequestMappingDoc;
import com.java110.doc.annotation.Java110RequestMappingsDoc;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.*;

/**
 * rest api
 * Created by wuxw on 2018/10/16.
 */
@RestController
@RequestMapping(path = "/api")
@Api(value = "对外统一提供服务接口服务")
@Java110ApiDoc(
        title = "HC物联网管理系统api接口文档",
        description = "HC物联网管理系统api接口文档",
        company = "Java110工作室",
        version = "v1.6"
)

@Java110RequestMappingsDoc(
        mappingsDocs = {
                @Java110RequestMappingDoc(name = "门禁中心", resource = "accessControlDoc", url = "http://127.0.0.1:9999", seq = 1, startWay = "boot"),
                @Java110RequestMappingDoc(name = "考勤中心", resource = "attendanceDoc", url = "http://127.0.0.1:9999", seq = 2, startWay = "boot"),
                @Java110RequestMappingDoc(name = "道闸中心", resource = "barrierDoc", url = "http://127.0.0.1:9999", seq = 3, startWay = "boot"),
                @Java110RequestMappingDoc(name = "车辆中心", resource = "carDoc", url = "http://127.0.0.1:9999", seq = 4, startWay = "boot"),
                @Java110RequestMappingDoc(name = "资产中心", resource = "communityDoc", url = "http://127.0.0.1:9999", seq = 5, startWay = "boot"),
                @Java110RequestMappingDoc(name = "开发中心", resource = "devDoc", url = "http://127.0.0.1:9999", seq = 6, startWay = "boot"),
                @Java110RequestMappingDoc(name = "任务中心", resource = "jobDoc", url = "http://127.0.0.1:9999", seq = 7, startWay = "boot"),
                @Java110RequestMappingDoc(name = "监控中心", resource = "monitorDoc", url = "http://127.0.0.1:9999", seq = 8, startWay = "boot"),
                @Java110RequestMappingDoc(name = "平台中心", resource = "systemDoc", url = "http://127.0.0.1:9999", seq = 9, startWay = "boot"),
                @Java110RequestMappingDoc(name = "用户中心", resource = "userDoc", url = "http://127.0.0.1:9999", seq = 10, startWay = "boot"),
        }
)
public class RestApi {

    private static Logger logger = LoggerFactory.getLogger(RestApi.class);
    private static final String VERSION = "version";
    private static final String VERSION_2 = "1.6";


    /**
     * 健康检查 服务
     *
     * @return
     */
    @RequestMapping(path = "/version", method = RequestMethod.GET)
    @ApiOperation(value = "版本", notes = "test: 返回 2XX 表示服务正常")
    public String version() {
        return VERSION_2;
    }

}
