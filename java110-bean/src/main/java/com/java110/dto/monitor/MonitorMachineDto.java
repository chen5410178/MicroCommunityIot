package com.java110.dto.monitor;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MonitorMachineDto extends PageDto implements Serializable {
    public static final String MONITOR_OPENING = "1001";
    public static final String MONITOR_CLOSING = "1002";

    public static final String STATE_ONLINE = "ONLINE";
    public static final String STATE_OFFLINE = "OFFLINE";

    public static final String PROTOCOL_RTSP = "RTSP";


    private String machineId;
    private String machineName;
    private String machineCode;
    private String locationName;
    private String maId;
    private String mmId;

    private String mmName;

    private String personId;


    private String communityId;
    private Date heartbeatTime;
    private String state;
    private Date createTime;
    private String statusCd = "0";

    private String maName;
    private List<MonitorMachineAttrsDto> monitorMachineAttrsList;
    private String isOnlineState;
    private String isOnlineStateName;

    private String photoUrl;


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

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getMaId() {
        return maId;
    }

    public void setMaId(String maId) {
        this.maId = maId;
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

    public String getMaName() {
        return maName;
    }

    public void setMaName(String maName) {
        this.maName = maName;
    }

    public List<MonitorMachineAttrsDto> getMonitorMachineAttrsList() {
        return monitorMachineAttrsList;
    }

    public void setMonitorMachineAttrsList(List<MonitorMachineAttrsDto> monitorMachineAttrsList) {
        this.monitorMachineAttrsList = monitorMachineAttrsList;
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

    public String getMmId() {
        return mmId;
    }

    public void setMmId(String mmId) {
        this.mmId = mmId;
    }

    public String getMmName() {
        return mmName;
    }

    public void setMmName(String mmName) {
        this.mmName = mmName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }
}
