package com.java110.hal.http.yiteSmart.bmo;

import org.springframework.http.ResponseEntity;

public interface IManagerLoginBmo {

    ResponseEntity<String> login(String reqJson);
}
