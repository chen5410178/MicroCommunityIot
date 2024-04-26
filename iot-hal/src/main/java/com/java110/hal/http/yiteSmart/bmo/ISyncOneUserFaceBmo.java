package com.java110.hal.http.yiteSmart.bmo;

import org.springframework.http.ResponseEntity;

public interface ISyncOneUserFaceBmo {

    ResponseEntity syncUserFace(String machineCode);
}
