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
package com.java110.po.chargeProcessAnalysis;

import java.io.Serializable;
import java.util.Date;
/**
 * 类表述： Po 数据模型实体对象 基本保持与数据库模型一直 用于 增加修改删除 等时的数据载体
 * add by 吴学文 at 2024-11-29 14:48:58 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
public class ChargeProcessAnalysisPo implements Serializable {

    private String gunNum;
private String orderId;
private String accumulatedChargingTime;
private String outputVoltage;
private String soc;
private String cpaId;
private String statusCd = "0";
private String remark;
private String chargingDegree;
private String insertedGun;
private String outputCurrent;
private String remainingTime;
private String gunLineTem;
private String backInPlace;
private String batterypackMaxTem;
private String machineId;
private String pileNum;
private String calculatedChargingDegree;
private String hardwareFailure;
private String rechargeAmount;
private String gunCode;
private String communityId;
private String status;
public String getGunNum() {
        return gunNum;
    }
public void setGunNum(String gunNum) {
        this.gunNum = gunNum;
    }
public String getOrderId() {
        return orderId;
    }
public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
public String getAccumulatedChargingTime() {
        return accumulatedChargingTime;
    }
public void setAccumulatedChargingTime(String accumulatedChargingTime) {
        this.accumulatedChargingTime = accumulatedChargingTime;
    }
public String getOutputVoltage() {
        return outputVoltage;
    }
public void setOutputVoltage(String outputVoltage) {
        this.outputVoltage = outputVoltage;
    }
public String getSoc() {
        return soc;
    }
public void setSoc(String soc) {
        this.soc = soc;
    }
public String getCpaId() {
        return cpaId;
    }
public void setCpaId(String cpaId) {
        this.cpaId = cpaId;
    }
public String getStatusCd() {
        return statusCd;
    }
public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }
public String getRemark() {
        return remark;
    }
public void setRemark(String remark) {
        this.remark = remark;
    }
public String getChargingDegree() {
        return chargingDegree;
    }
public void setChargingDegree(String chargingDegree) {
        this.chargingDegree = chargingDegree;
    }
public String getInsertedGun() {
        return insertedGun;
    }
public void setInsertedGun(String insertedGun) {
        this.insertedGun = insertedGun;
    }
public String getOutputCurrent() {
        return outputCurrent;
    }
public void setOutputCurrent(String outputCurrent) {
        this.outputCurrent = outputCurrent;
    }
public String getRemainingTime() {
        return remainingTime;
    }
public void setRemainingTime(String remainingTime) {
        this.remainingTime = remainingTime;
    }
public String getGunLineTem() {
        return gunLineTem;
    }
public void setGunLineTem(String gunLineTem) {
        this.gunLineTem = gunLineTem;
    }
public String getBackInPlace() {
        return backInPlace;
    }
public void setBackInPlace(String backInPlace) {
        this.backInPlace = backInPlace;
    }
public String getBatterypackMaxTem() {
        return batterypackMaxTem;
    }
public void setBatterypackMaxTem(String batterypackMaxTem) {
        this.batterypackMaxTem = batterypackMaxTem;
    }
public String getMachineId() {
        return machineId;
    }
public void setMachineId(String machineId) {
        this.machineId = machineId;
    }
public String getPileNum() {
        return pileNum;
    }
public void setPileNum(String pileNum) {
        this.pileNum = pileNum;
    }
public String getCalculatedChargingDegree() {
        return calculatedChargingDegree;
    }
public void setCalculatedChargingDegree(String calculatedChargingDegree) {
        this.calculatedChargingDegree = calculatedChargingDegree;
    }
public String getHardwareFailure() {
        return hardwareFailure;
    }
public void setHardwareFailure(String hardwareFailure) {
        this.hardwareFailure = hardwareFailure;
    }
public String getRechargeAmount() {
        return rechargeAmount;
    }
public void setRechargeAmount(String rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }
public String getGunCode() {
        return gunCode;
    }
public void setGunCode(String gunCode) {
        this.gunCode = gunCode;
    }
public String getCommunityId() {
        return communityId;
    }
public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }
public String getStatus() {
        return status;
    }
public void setStatus(String status) {
        this.status = status;
    }



}
