package com.java110.dto.chargeProcessAnalysis;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 充电过程数据分析数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class ChargeProcessAnalysisDto extends PageDto implements Serializable {

    private String gunNum;
    private String orderId;
    private String accumulatedChargingTime;
    private String outputVoltage;
    private String soc;
    private String cpaId;
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


    private Date createTime;

    private String statusCd = "0";


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
