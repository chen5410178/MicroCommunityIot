package com.java110.hal.http.yiteSmart.bmo;

import org.springframework.http.ResponseEntity;

public interface IManagerRoomsBmo {

    ResponseEntity<String> rooms(String buildingUid, String uuid);
}
