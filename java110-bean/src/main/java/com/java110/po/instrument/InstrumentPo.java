package com.java110.po.instrument;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

public class InstrumentPo implements Serializable {
    private String machineId;
    private String machineCode;
    private String machineName;
    private String communityId;
    private String typeId;
    private String charge;
    private String sign;
    private String upMin;
    private String checkSec;
    private String implBean;
    private String statusCd = "0";

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getUpMin() {
        return upMin;
    }

    public void setUpMin(String upMin) {
        this.upMin = upMin;
    }

    public String getCheckSec() {
        return checkSec;
    }

    public void setCheckSec(String checkSec) {
        this.checkSec = checkSec;
    }

    public String getImplBean() {
        return implBean;
    }

    public void setImplBean(String implBean) {
        this.implBean = implBean;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }
}
