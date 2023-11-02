package com.java110.po.lock;

import java.io.Serializable;

public class LockInoutPo implements Serializable {
    private String inoutId;
    private String machineId;
    private String machineCode;
    private String userId;
    private String userName;
    private String openTypeCd;
    private String communityId;
    private String tel;
    private String statusCd = "0";
    private String state;

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
}
