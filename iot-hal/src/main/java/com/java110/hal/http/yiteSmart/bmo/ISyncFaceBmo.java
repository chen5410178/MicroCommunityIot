package com.java110.hal.http.yiteSmart.bmo;

import org.springframework.http.ResponseEntity;

public interface ISyncFaceBmo {

    /**
     * 同步人脸
     *
     * @param machineCode
     * @return
     */
    public ResponseEntity<String> sync(String machineCode,int page,int row);
}
