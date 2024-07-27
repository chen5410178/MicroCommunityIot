package com.java110.po.lift;

import java.io.Serializable;

public class LiftMachinePo implements Serializable {
    private String machineId;
    private String machineName;
    private String machineCode;
    private String brand;
    private String locationName;
    private String registrationUnit;
    private String useUnit;
    private String maintenanceUnit;
    private String rescueLink;
    private String factoryId;
    private String communityId;
    private String heartbeatTime;
    private String state;
    private String curLayer;
    private String statusCd = "0";


    private String monitorId;
    private String monitorName;

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getRegistrationUnit() {
        return registrationUnit;
    }

    public void setRegistrationUnit(String registrationUnit) {
        this.registrationUnit = registrationUnit;
    }

    public String getUseUnit() {
        return useUnit;
    }

    public void setUseUnit(String useUnit) {
        this.useUnit = useUnit;
    }

    public String getMaintenanceUnit() {
        return maintenanceUnit;
    }

    public void setMaintenanceUnit(String maintenanceUnit) {
        this.maintenanceUnit = maintenanceUnit;
    }

    public String getRescueLink() {
        return rescueLink;
    }

    public void setRescueLink(String rescueLink) {
        this.rescueLink = rescueLink;
    }

    public String getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(String factoryId) {
        this.factoryId = factoryId;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getHeartbeatTime() {
        return heartbeatTime;
    }

    public void setHeartbeatTime(String heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCurLayer() {
        return curLayer;
    }

    public void setCurLayer(String curLayer) {
        this.curLayer = curLayer;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public String getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(String monitorId) {
        this.monitorId = monitorId;
    }

    public String getMonitorName() {
        return monitorName;
    }

    public void setMonitorName(String monitorName) {
        this.monitorName = monitorName;
    }
}
