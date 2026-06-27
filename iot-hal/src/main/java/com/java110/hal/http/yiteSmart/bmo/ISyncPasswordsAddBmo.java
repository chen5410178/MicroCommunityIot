package com.java110.hal.http.yiteSmart.bmo;

import org.springframework.http.ResponseEntity;

public interface ISyncPasswordsAddBmo {

    ResponseEntity syncPasswordsAdd(String machineCode, String t);
}
