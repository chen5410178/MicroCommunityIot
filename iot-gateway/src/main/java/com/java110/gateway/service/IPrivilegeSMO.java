package com.java110.gateway.service;

import org.springframework.http.ResponseEntity;

/**
 * 权限业务处理类
 * Created by Administrator on 2019/4/1.
 */
public interface IPrivilegeSMO {

    /**
     * 保存用户默认权限
     * @param resource 权限信息
     * @return
     */
    public void hasPrivilege( String resource,String userId);





}
