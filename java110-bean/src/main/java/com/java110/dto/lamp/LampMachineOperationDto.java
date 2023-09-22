package com.java110.dto.lamp;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

public class LampMachineOperationDto extends PageDto implements Serializable {
    private String lmoId;
    private String machineId;
    private String personId;
    private String personName;
    private String type;
    private Date operationTime;
    private Date createTime;
    private String statusCd = "0";

    private String typeName;
    private String machineName;

    public String getLmoId() {
        return lmoId;
    }

    public void setLmoId(String lmoId) {
        this.lmoId = lmoId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(Date operationTime) {
        this.operationTime = operationTime;
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

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }
}
