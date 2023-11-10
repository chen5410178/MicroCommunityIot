package com.java110.dto.lock;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

public class LockLogDto extends PageDto implements Serializable {
    public static final String REQUEST_SUCCESS = "10001";
    public static final String RETURN_SUCCESS = "10002";
    public static final String OPERATE_FAILED = "10003";

    public static final String LOG_ACTION_GET_ACCESS_TOKEN = "1001";
    public static final String LOG_ACTION_REFRESH_ACCESS_TOKEN = "1002";
    public static final String LOG_ACTION_ADD_PWD_TO_CLOUD = "1003";

    private String logId;
    private String machineId;
    private String communityId;
    private String logAction;
    private String userId;
    private String userName;
    private String reqParam;
    private String resParam;
    private String state;
    private Date createTime;

    private String machineName;
    private String machineCode;
    private String roomId;

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getLogAction() {
        return logAction;
    }

    public void setLogAction(String logAction) {
        this.logAction = logAction;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getReqParam() {
        return reqParam;
    }

    public void setReqParam(String reqParam) {
        this.reqParam = reqParam;
    }

    public String getResParam() {
        return resParam;
    }

    public void setResParam(String resParam) {
        this.resParam = resParam;
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

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
