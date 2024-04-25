package com.java110.hal.http.yiteSmart.bmo;

import org.springframework.http.ResponseEntity;

public interface ISyncCardsBidBmo {

    /**
     * 同步单元信息
     *
     * @param unitId
     * @return
     */
    public ResponseEntity<String> sync(String unitId);
}
