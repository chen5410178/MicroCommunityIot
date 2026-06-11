package com.java110.hal.http.yiteSmart.bmo;

import org.springframework.http.ResponseEntity;

public interface IManagerGateListBmo {

    ResponseEntity<String> gateList(String uuid);
}
