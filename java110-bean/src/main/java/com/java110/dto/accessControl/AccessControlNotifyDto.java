package com.java110.dto.accessControl;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;

/**
 * @ClassName FloorDto
 * @Description 车位摄像头数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class AccessControlNotifyDto extends PageDto implements Serializable {

    public AccessControlNotifyDto() {
    }

    public AccessControlNotifyDto(String machineCode, String reqBody) {
        this.machineCode = machineCode;
        this.reqBody = reqBody;
    }

    private String machineCode;
    private String reqBody;

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getReqBody() {
        return reqBody;
    }

    public void setReqBody(String reqBody) {
        this.reqBody = reqBody;
    }
}
