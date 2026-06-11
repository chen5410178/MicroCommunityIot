package com.java110.hal.http.yiteSmart.bmo;

import org.springframework.http.ResponseEntity;

public interface ICheckCardBmo {

    ResponseEntity<String> checkCard(String uuid, String machineId, String cardCode);
}
