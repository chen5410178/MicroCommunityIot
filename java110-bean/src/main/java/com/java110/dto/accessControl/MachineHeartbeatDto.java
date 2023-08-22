package com.java110.dto.accessControl;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * @ClassName MachineUploadFaceDto
 * @Description TODO 设备心跳对象
 * @Author wuxw
 * @Date 2020/5/27 8:31
 * @Version 1.0
 * add by wuxw 2020/5/27
 **/
public class MachineHeartbeatDto implements Serializable {


    private String machineCode;

    private String heartbeatTime;


    public MachineHeartbeatDto(String machineCode, String heartbeatTime) {
        this.machineCode = machineCode;
        this.heartbeatTime = heartbeatTime;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }


    public String getHeartbeatTime() {
        return heartbeatTime;
    }

    public void setHeartbeatTime(String heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
    }


    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
