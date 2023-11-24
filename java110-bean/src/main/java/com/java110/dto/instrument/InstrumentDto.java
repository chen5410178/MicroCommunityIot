package com.java110.dto.instrument;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class InstrumentDto extends PageDto implements Serializable {
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
    private Date createTime;
    private String statusCd = "0";

    private String factoryName;
    private String typeName;
    private List<InstrumentSensorDto> instrumentSensorList;
    private List<InstrumentParamDto> instrumentParamList;

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

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<InstrumentSensorDto> getInstrumentSensorList() {
        return instrumentSensorList;
    }

    public void setInstrumentSensorList(List<InstrumentSensorDto> instrumentSensorList) {
        this.instrumentSensorList = instrumentSensorList;
    }

    public List<InstrumentParamDto> getInstrumentParamList() {
        return instrumentParamList;
    }

    public void setInstrumentParamList(List<InstrumentParamDto> instrumentParamList) {
        this.instrumentParamList = instrumentParamList;
    }
}
