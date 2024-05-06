package com.java110.hal.http.yiteSmart.bmo;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.ResponseEntity;

public interface IOpenDoorBmo {

    /**
     * 开门记录
     * @param machineCode
     * @param reqJson
     * @return
     */
    ResponseEntity<String> openDoor(String machineCode, JSONObject reqJson);
}
