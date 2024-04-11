package com.java110.dto.liftWarn;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 电梯告警数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class LiftWarnDto extends PageDto implements Serializable {

    private String lcId;
    private String currentPeopleNum;
    private String leavePeopleNum;
    private String haveBicycle;
    private String liftDoorState;
    private String warnType;
    private String liftOperateState;
    private String currentSpeed;
    private String alarmInPolarity;
    private String lwId;
    private String liftName;
    private String warnTime;
    private String enterPeopleNum;
    private String warnImg;
    private String liftMachineId;
    private String cameraCode;
    private String communityId;
    private String cameraName;


    private Date createTime;

    private String statusCd = "0";


    public String getLcId() {
        return lcId;
    }

    public void setLcId(String lcId) {
        this.lcId = lcId;
    }

    public String getCurrentPeopleNum() {
        return currentPeopleNum;
    }

    public void setCurrentPeopleNum(String currentPeopleNum) {
        this.currentPeopleNum = currentPeopleNum;
    }

    public String getLeavePeopleNum() {
        return leavePeopleNum;
    }

    public void setLeavePeopleNum(String leavePeopleNum) {
        this.leavePeopleNum = leavePeopleNum;
    }

    public String getHaveBicycle() {
        return haveBicycle;
    }

    public void setHaveBicycle(String haveBicycle) {
        this.haveBicycle = haveBicycle;
    }

    public String getLiftDoorState() {
        return liftDoorState;
    }

    public void setLiftDoorState(String liftDoorState) {
        this.liftDoorState = liftDoorState;
    }

    public String getWarnType() {
        return warnType;
    }

    public void setWarnType(String warnType) {
        this.warnType = warnType;
    }

    public String getLiftOperateState() {
        return liftOperateState;
    }

    public void setLiftOperateState(String liftOperateState) {
        this.liftOperateState = liftOperateState;
    }

    public String getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(String currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public String getAlarmInPolarity() {
        return alarmInPolarity;
    }

    public void setAlarmInPolarity(String alarmInPolarity) {
        this.alarmInPolarity = alarmInPolarity;
    }

    public String getLwId() {
        return lwId;
    }

    public void setLwId(String lwId) {
        this.lwId = lwId;
    }

    public String getLiftName() {
        return liftName;
    }

    public void setLiftName(String liftName) {
        this.liftName = liftName;
    }

    public String getWarnTime() {
        return warnTime;
    }

    public void setWarnTime(String warnTime) {
        this.warnTime = warnTime;
    }

    public String getEnterPeopleNum() {
        return enterPeopleNum;
    }

    public void setEnterPeopleNum(String enterPeopleNum) {
        this.enterPeopleNum = enterPeopleNum;
    }

    public String getWarnImg() {
        return warnImg;
    }

    public void setWarnImg(String warnImg) {
        this.warnImg = warnImg;
    }

    public String getLiftMachineId() {
        return liftMachineId;
    }

    public void setLiftMachineId(String liftMachineId) {
        this.liftMachineId = liftMachineId;
    }

    public String getCameraCode() {
        return cameraCode;
    }

    public void setCameraCode(String cameraCode) {
        this.cameraCode = cameraCode;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
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
}
