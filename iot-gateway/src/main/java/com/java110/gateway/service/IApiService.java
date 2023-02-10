package com.java110.gateway.service;

import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface IApiService {

    /**
     * post 请求
     * @param service
     * @param postInfo
     * @param request
     * @return
     */
    ResponseEntity<String> post(String service,
                                String postInfo,
                                HttpServletRequest request) throws Exception;

    /**
     * get 请求
     * @param service
     * @param request
     * @return
     */
    ResponseEntity<String> get(String service,
                               HttpServletRequest request) throws Exception;
}
