package com.java110.hal.http.yiteSmart.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.hal.http.yiteSmart.bmo.ISyncFaceBmo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SyncFaceBmoImpl implements ISyncFaceBmo {
    @Override
    public ResponseEntity<String> sync(String machineCode) {
        long curTimeTimeStamp = new Date().getTime();
        curTimeTimeStamp = curTimeTimeStamp / 1000;
        ResponseEntity<String> responseEntity = null;
        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", curTimeTimeStamp);
        JSONObject data = new JSONObject();
        paramOut.put("data", data);
        responseEntity = new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.BAD_REQUEST);
        return responseEntity;
    }
}
