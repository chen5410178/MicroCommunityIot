package com.java110.hal.http.yiteSmart.bmo;

import org.springframework.http.ResponseEntity;

public interface IManagerUsersBmo {

    ResponseEntity<String> users(String buildingUid, String roomUid, String uuid);
}
