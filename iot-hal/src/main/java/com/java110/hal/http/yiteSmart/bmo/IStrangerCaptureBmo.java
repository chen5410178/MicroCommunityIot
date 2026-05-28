package com.java110.hal.http.yiteSmart.bmo;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.ResponseEntity;

public interface IStrangerCaptureBmo {

    ResponseEntity<String> capture(String machineCode, JSONObject reqJson);
}
