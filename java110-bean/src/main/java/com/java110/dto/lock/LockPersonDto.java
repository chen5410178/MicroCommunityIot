package com.java110.dto.lock;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

public class LockPersonDto extends PageDto implements Serializable {
    public static final String OPEN_MODEL_PW = "1001";
    public static final String OPEN_MODEL_BT = "2002";

    public static final String STATE_AVAILABLE = "1001";
    public static final String STATE_FREEZE = "2002";

    private String lpId;
    private String machineId;
    private String personId;
    private String name;
    private String startTime;
    private String endTime;
    private String openModel;
    private String state;
    private String communityId;
    private String cardNumber;
    private Date createTime;
    private String statusCd = "0";

    private String machineName;
    private String machineCode;
    private String queryTime;
    private String tel;

    public String getLpId() {
        return lpId;
    }

    public void setLpId(String lpId) {
        this.lpId = lpId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getOpenModel() {
        return openModel;
    }

    public void setOpenModel(String openModel) {
        this.openModel = openModel;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
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

    public String getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(String queryTime) {
        this.queryTime = queryTime;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
