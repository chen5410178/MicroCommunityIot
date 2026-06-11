package com.java110.hal.http.yiteSmart.bmo;

import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface IManagerSignVerifyBmo {

    ResponseEntity<String> verify(HttpServletRequest request);
}
