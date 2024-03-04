package com.java110.dto.accessControl;

import com.alibaba.fastjson.JSONArray;

public class AccessControlOweFeeDto {

    public AccessControlOweFeeDto() {
    }

    public AccessControlOweFeeDto(int code, String msg, String normalMsg, String oweMsg, JSONArray data) {
        this.code = code;
        this.msg = msg;
        this.normalMsg = normalMsg;
        this.oweMsg = oweMsg;
        this.data = data;
    }

    private int code;

    private String msg;

    private String normalMsg;

    private String oweMsg;


    private JSONArray data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getNormalMsg() {
        return normalMsg;
    }

    public void setNormalMsg(String normalMsg) {
        this.normalMsg = normalMsg;
    }

    public String getOweMsg() {
        return oweMsg;
    }

    public void setOweMsg(String oweMsg) {
        this.oweMsg = oweMsg;
    }

    public JSONArray getData() {
        return data;
    }

    public void setData(JSONArray data) {
        this.data = data;
    }
}
