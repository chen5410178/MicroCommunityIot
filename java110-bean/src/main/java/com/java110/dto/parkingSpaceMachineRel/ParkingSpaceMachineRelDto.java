package com.java110.dto.parkingSpaceMachineRel;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 车位摄像头数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class ParkingSpaceMachineRelDto extends PageDto implements Serializable {

    public static final String PS_STATE_FREE = "FREE";//车位状态，FREE 空闲 OCCUPY 占用
    public static final String PS_STATE_OCCUPY = "OCCUPY";//车位状态，FREE 空闲 OCCUPY 占用

    public static final String LOCK_STATE_ON = "ON"; // 开
    public static final String LOCK_STATE_OFF = "OFF"; // 关

    private String lockBattery;
    private String relId;
    private String psState;
    private String machineId;
    private String[] machineIds;
    private String psName;
    private String carNum;
    private String psId;
    private String updateTime;
    private String communityId;
    private String lockState;
    private String imagePhoto;

    private String areaNum;

    private String psNum;


    private Date createTime;

    private String statusCd = "0";


    public String getLockBattery() {
        return lockBattery;
    }

    public void setLockBattery(String lockBattery) {
        this.lockBattery = lockBattery;
    }

    public String getRelId() {
        return relId;
    }

    public void setRelId(String relId) {
        this.relId = relId;
    }

    public String getPsState() {
        return psState;
    }

    public void setPsState(String psState) {
        this.psState = psState;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getPsName() {
        return psName;
    }

    public void setPsName(String psName) {
        this.psName = psName;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    public String getPsId() {
        return psId;
    }

    public void setPsId(String psId) {
        this.psId = psId;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
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

    public String[] getMachineIds() {
        return machineIds;
    }

    public void setMachineIds(String[] machineIds) {
        this.machineIds = machineIds;
    }

    public String getAreaNum() {
        return areaNum;
    }

    public void setAreaNum(String areaNum) {
        this.areaNum = areaNum;
    }

    public String getPsNum() {
        return psNum;
    }

    public void setPsNum(String psNum) {
        this.psNum = psNum;
    }
}
