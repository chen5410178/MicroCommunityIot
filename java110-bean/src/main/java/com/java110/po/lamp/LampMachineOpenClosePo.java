package com.java110.po.lamp;

import java.io.Serializable;
import java.util.List;

public class LampMachineOpenClosePo implements Serializable {
    private String lmoId;
    private String machineId;
    private String type;
    private String hours;
    private String min;
    private String statusCd = "0";

    private List<String> machineIdList;

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
}
