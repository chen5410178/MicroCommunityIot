package com.java110.gateway.service;

import com.java110.bean.dto.app.AppService;

import java.util.Map;

/**
 * 接口权限校验
 */
public interface IAppServiceCheck {

     AppService checkAndGetAppService(String reqJson, Map<String,String> headers);
}
