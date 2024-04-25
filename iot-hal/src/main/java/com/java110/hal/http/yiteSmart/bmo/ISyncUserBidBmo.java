package com.java110.hal.http.yiteSmart.bmo;

import org.springframework.http.ResponseEntity;

/**
 * 同步单元下所有房屋和人员信息
 */
public interface ISyncUserBidBmo {

    /**
     * 同步单元信息
     *
     * @param unitId
     * @return
     */
    public ResponseEntity<String> sync(String unitId);
}
