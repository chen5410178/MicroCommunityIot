package com.java110.dto.chargeMachine;

import java.io.Serializable;

public class NotifyChargeOrderDto implements Serializable {

    private String orderId;

    private String machineCode;

    private String portCode;

    private String urlParam;

    private String bodyParam;

    private String reason;

    private String energyFlag = "2"; // 1 是电量 2 是功率 默认是功率

    private String energy;

    private String amount;

    private String feeAmount;

    private String serviceAmount;


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getPortCode() {
        return portCode;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public String getUrlParam() {
        return urlParam;
    }

    public void setUrlParam(String urlParam) {
        this.urlParam = urlParam;
    }

    public String getBodyParam() {
        return bodyParam;
    }

    public void setBodyParam(String bodyParam) {
        this.bodyParam = bodyParam;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getEnergy() {
        return energy;
    }

    public void setEnergy(String energy) {
        this.energy = energy;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getEnergyFlag() {
        return energyFlag;
    }

    public void setEnergyFlag(String energyFlag) {
        this.energyFlag = energyFlag;
    }

    public void setFeeAmount(String feeAmount) {
        this.feeAmount = feeAmount;
    }

    public void setServiceAmount(String serviceAmount) {
        this.serviceAmount = serviceAmount;
    }

    public String getFeeAmount() {
        return feeAmount;
    }

    public String getServiceAmount() {
        return serviceAmount;
    }
}
