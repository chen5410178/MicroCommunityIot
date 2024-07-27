package com.java110.dto.parking;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 停车位数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class ParkingSpaceDto extends PageDto implements Serializable {

    public static final String STATE_FREE = "F";
    public static final String STATE_HIRE = "H";
    public static final String STATE_SELL = "S";

    public static final String NUM_MOTHER = "母";

    public static final String TYPE_CD_COMMON = "1";
    public static final String TYPE_CD_SON_MOTHER = "2";
    private String parkingType;
    private String num;
    private String paId;

    private String prId;
    private String psId;
    private String[] psIds;
    private String remark;
    private String state;
    private String communityId;

    private String paNum;

    private String areaNum;
    private String psState;
    private String psStateName;
    private String parkCarNum;
    private String lockBattery;
    private String lockState;
    private String lockStateName;
    private String imagePhoto;
    private String machineName;

    private String machineStateName;
    private String updateTime;
    private String heartbeatTime;

    private String regionCode;

    private String monitorId;
    private String monitorName;
    private Date createTime;

    private String statusCd = "0";


    public String getParkingType() {
        return parkingType;
    }

    public void setParkingType(String parkingType) {
        this.parkingType = parkingType;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getPaId() {
        return paId;
    }

    public void setPaId(String paId) {
        this.paId = paId;
    }

    public String getPsId() {
        return psId;
    }

    public void setPsId(String psId) {
        this.psId = psId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getPaNum() {
        return paNum;
    }

    public void setPaNum(String paNum) {
        this.paNum = paNum;
    }

    public String getAreaNum() {
        return areaNum;
    }

    public void setAreaNum(String areaNum) {
        this.areaNum = areaNum;
    }

    public String[] getPsIds() {
        return psIds;
    }

    public void setPsIds(String[] psIds) {
        this.psIds = psIds;
    }

    public String getPsState() {
        return psState;
    }

    public void setPsState(String psState) {
        this.psState = psState;
    }

    public String getParkCarNum() {
        return parkCarNum;
    }

    public void setParkCarNum(String parkCarNum) {
        this.parkCarNum = parkCarNum;
    }

    public String getLockBattery() {
        return lockBattery;
    }

    public void setLockBattery(String lockBattery) {
        this.lockBattery = lockBattery;
    }

    public String getLockState() {
        return lockState;
    }

    public void setLockState(String lockState) {
        this.lockState = lockState;
    }

    public String getImagePhoto() {
        return imagePhoto;
    }

    public void setImagePhoto(String imagePhoto) {
        this.imagePhoto = imagePhoto;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getHeartbeatTime() {
        return heartbeatTime;
    }

    public void setHeartbeatTime(String heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
    }

    public String getMachineStateName() {
        return machineStateName;
    }

    public void setMachineStateName(String machineStateName) {
        this.machineStateName = machineStateName;
    }

    public String getPsStateName() {
        return psStateName;
    }

    public void setPsStateName(String psStateName) {
        this.psStateName = psStateName;
    }

    public String getLockStateName() {
        return lockStateName;
    }

    public void setLockStateName(String lockStateName) {
        this.lockStateName = lockStateName;
    }

    public String getPrId() {
        return prId;
    }

    public void setPrId(String prId) {
        this.prId = prId;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
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
