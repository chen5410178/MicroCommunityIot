package com.java110.dto.meterMachineCharge;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 水电充值数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class MeterMachineChargeDto extends PageDto implements Serializable {
    public static final String STATE_WAITING = "W";
    public static final String STATE_COMPLETE = "C";

    private String machineId;
    private String chargeId;
    private String chargeDegrees;
    private String remark;
    private String chargeMoney;
    private String state;
    private String communityId;

    private String communityName;

    private String address;
    private String roomName;
    private String machineName;
    private String typeName;

    private Date createTime;

    private String statusCd = "0";
    private String machineNameLike;
    private String addressLike;
    private String roomNameLike;

    private String queryStartTime;

    private String queryEndTime;


    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getChargeId() {
        return chargeId;
    }

    public void setChargeId(String chargeId) {
        this.chargeId = chargeId;
    }

    public String getChargeDegrees() {
        return chargeDegrees;
    }

    public void setChargeDegrees(String chargeDegrees) {
        this.chargeDegrees = chargeDegrees;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getChargeMoney() {
        return chargeMoney;
    }

    public void setChargeMoney(String chargeMoney) {
        this.chargeMoney = chargeMoney;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getMachineNameLike() {
        return machineNameLike;
    }

    public void setMachineNameLike(String machineNameLike) {
        this.machineNameLike = machineNameLike;
    }

    public String getAddressLike() {
        return addressLike;
    }

    public void setAddressLike(String addressLike) {
        this.addressLike = addressLike;
    }

    public String getRoomNameLike() {
        return roomNameLike;
    }

    public void setRoomNameLike(String roomNameLike) {
        this.roomNameLike = roomNameLike;
    }

    public String getQueryStartTime() {
        return queryStartTime;
    }

    public void setQueryStartTime(String queryStartTime) {
        this.queryStartTime = queryStartTime;
    }

    public String getQueryEndTime() {
        return queryEndTime;
    }

    public void setQueryEndTime(String queryEndTime) {
        this.queryEndTime = queryEndTime;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }
}
