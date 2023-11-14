package com.java110.dto.lock;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

public class LockInoutDto extends PageDto implements Serializable {
    public static final String UNLOCK_SUCCESS = "C";
    public static final String UNLOCK_FAIL = "F";
    public static final String OPEN_TYPE_PW = "1001";
    public static final String OPEN_TYPE_BT = "2002";
    public static final String OPEN_TYPE_REMOTE = "3003";

    private String inoutId;
    private String machineId;
    private String machineCode;
    private String userId;
    private String userName;
    private String openTypeCd;
    private String communityId;
    private String tel;
    private Date createTime;
    private String statusCd = "0";
    private String state;

    private String machineName;

    private String roomId;

    public String getInoutId() {
        return inoutId;
    }

    public void setInoutId(String inoutId) {
        this.inoutId = inoutId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
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

    public String getOpenTypeCd() {
        return openTypeCd;
    }

    public void setOpenTypeCd(String openTypeCd) {
        this.openTypeCd = openTypeCd;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
