package com.java110.gateway.api;

import com.java110.bean.ResultVo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提示 某些用户访问后端 时，提醒访问前端项目
 */
@RestController
public class WelComeController {

    @RequestMapping(path = "/")
    public ResponseEntity<String> index() {
        return ResultVo.createResponseEntity("亲爱的用户您好，系统运行正常，当前访问路径为后端项目，不包含任何页面，请您访问物联网前端项目");
    }
}
