package com.java110.dto.meter;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 智能水电表明细数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class MeterMachineReadOrRechargeDetailDto extends PageDto implements Serializable {

    private String detailType;
    private String curDegrees;
    private String machineId;
    private String prestoreDegrees;
    private String detailId;
    private String remark;
    private String communityId;
    private String curReadingTime;

    private String state;
    private Date createTime;

    private String statusCd = "0";
    private String machineName;
    private String address;
    private String roomName;
    private String roomId;
    private String typeName;
    private String typeId;
    private String machineNameLike;
    private String addressLike;
    private String roomNameLike;

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
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

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getDetailType() {
        return detailType;
    }

    public void setDetailType(String detailType) {
        this.detailType = detailType;
    }

    public String getCurDegrees() {
        return curDegrees;
    }

    public void setCurDegrees(String curDegrees) {
        this.curDegrees = curDegrees;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getPrestoreDegrees() {
        return prestoreDegrees;
    }

    public void setPrestoreDegrees(String prestoreDegrees) {
        this.prestoreDegrees = prestoreDegrees;
    }

    public String getDetailId() {
        return detailId;
    }

    public void setDetailId(String detailId) {
        this.detailId = detailId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getCurReadingTime() {
        return curReadingTime;
    }

    public void setCurReadingTime(String curReadingTime) {
        this.curReadingTime = curReadingTime;
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
}
