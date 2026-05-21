package com.java110.hal.http.yiteSmart.bmo;

import org.springframework.http.ResponseEntity;

public interface ISyncOneUserBmo {

    ResponseEntity syncUser(String machineCode, String t);
}
