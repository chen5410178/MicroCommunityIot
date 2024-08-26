package com.java110.hal.http.yiteSmart.bmo;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.ResponseEntity;

public interface IAdBmo {

    ResponseEntity<String> getAd(String machineCode, JSONObject reqJson);
}
