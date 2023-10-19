package com.java110.dto.monitor;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MonitorMachineDto extends PageDto implements Serializable {
    public static final String MONITOR_OPENING = "1001";
    public static final String MONITOR_CLOSING = "1002";

    private String machineId;
    private String machineName;
    private String machineCode;
    private String locationName;
    private String maId;
    private String protocol;
    private String communityId;
    private Date heartbeatTime;
    private String state;
    private Date createTime;
    private String statusCd = "0";

    private String maName;
    private List<MonitorMachineAttrsDto> monitorMachineAttrsList;

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

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
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
}
