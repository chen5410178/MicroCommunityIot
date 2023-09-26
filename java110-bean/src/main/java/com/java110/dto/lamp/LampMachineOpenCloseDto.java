package com.java110.dto.lamp;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class LampMachineOpenCloseDto extends PageDto implements Serializable {
    private String lmoId;
    private String machineId;
    private String type;
    private String hours;
    private String min;
    private Date createTime;
    private String statusCd = "0";

    private List<String> machineIdList;
    private String typeName;
    private String machineCode;
    private String machineName;

    public String getLmoId() {
        return lmoId;
    }

    public void setLmoId(String lmoId) {
        this.lmoId = lmoId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public List<String> getMachineIdList() {
        return machineIdList;
    }

    public void setMachineIdList(List<String> machineIdList) {
        this.machineIdList = machineIdList;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }
}
