package com.java110.dto.parkingSpaceMachine;

import com.java110.bean.dto.PageDto;
import com.java110.dto.parkingSpaceMachineRel.ParkingSpaceMachineRelDto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName FloorDto
 * @Description 车位摄像头数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class ParkingSpaceMachineNotifyDto extends PageDto implements Serializable {

    public ParkingSpaceMachineNotifyDto() {
    }

    public ParkingSpaceMachineNotifyDto(String machineCode, String reqBody) {
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
