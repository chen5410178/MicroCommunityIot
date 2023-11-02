package com.java110.dto.lift;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

public class LiftMachineDto extends PageDto implements Serializable {

    public static final String LIFT_RUNNING_STATE = "1001";
    public static final String LIFT_STOPPING_STATE = "2002";

    public static final String STATE_ONLINE = "ONLINE";
    public static final String STATE_OFFLINE = "OFFLINE";

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
    private Date heartbeatTime;
    private String state;
    private String curLayer;
    private Date createTime;
    private String statusCd = "0";

    private String factoryName;
    private String communityName;
    private String stateName;
    private String userName;
    private String userId;
    private String logAction;
    private String isOnlineState;
    private String isOnlineStateName;

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

    public Date getHeartbeatTime() {
        return heartbeatTime;
    }

    public void setHeartbeatTime(Date heartbeatTime) {
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

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLogAction() {
        return logAction;
    }

    public void setLogAction(String logAction) {
        this.logAction = logAction;
    }

    public String getIsOnlineState() {
        return isOnlineState;
    }

    public void setIsOnlineState(String isOnlineState) {
        this.isOnlineState = isOnlineState;
    }

    public String getIsOnlineStateName() {
        return isOnlineStateName;
    }

    public void setIsOnlineStateName(String isOnlineStateName) {
        this.isOnlineStateName = isOnlineStateName;
    }
}
