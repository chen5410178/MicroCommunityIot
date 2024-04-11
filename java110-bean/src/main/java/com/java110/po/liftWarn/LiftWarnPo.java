/*
 * Copyright 2017-2020 吴学文 and java110 team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.java110.po.liftWarn;

import java.io.Serializable;
import java.util.Date;
/**
 * 类表述： Po 数据模型实体对象 基本保持与数据库模型一直 用于 增加修改删除 等时的数据载体
 * add by 吴学文 at 2024-04-11 13:55:36 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
public class LiftWarnPo implements Serializable {

    private String lcId;
private String currentPeopleNum;
private String leavePeopleNum;
private String haveBicycle;
private String liftDoorState;
private String warnType;
private String liftOperateState;
private String statusCd = "0";
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
public String getStatusCd() {
        return statusCd;
    }
public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
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



}
