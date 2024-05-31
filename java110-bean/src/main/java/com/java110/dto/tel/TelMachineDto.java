package com.java110.dto.tel;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 话机数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class TelMachineDto extends PageDto implements Serializable {

    private String heartbeatTime;
private String successPrompt;
private String implBean;
private String machineId;
private String machineCode;
private String machinePort;
private String failPrompt;
private String communityId;
private String machineName;
private String machineIp;


    private Date createTime;

    private String statusCd = "0";


    public String getHeartbeatTime() {
        return heartbeatTime;
    }
public void setHeartbeatTime(String heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
    }
public String getSuccessPrompt() {
        return successPrompt;
    }
public void setSuccessPrompt(String successPrompt) {
        this.successPrompt = successPrompt;
    }
public String getImplBean() {
        return implBean;
    }
public void setImplBean(String implBean) {
        this.implBean = implBean;
    }
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
public String getMachinePort() {
        return machinePort;
    }
public void setMachinePort(String machinePort) {
        this.machinePort = machinePort;
    }
public String getFailPrompt() {
        return failPrompt;
    }
public void setFailPrompt(String failPrompt) {
        this.failPrompt = failPrompt;
    }
public String getCommunityId() {
        return communityId;
    }
public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }
public String getMachineName() {
        return machineName;
    }
public void setMachineName(String machineName) {
        this.machineName = machineName;
    }
public String getMachineIp() {
        return machineIp;
    }
public void setMachineIp(String machineIp) {
        this.machineIp = machineIp;
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
