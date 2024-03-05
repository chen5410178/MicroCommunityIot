package com.java110.dto.parkingSpaceMachine;

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
public class ParkingSpaceMachineDto extends PageDto implements Serializable {

    private String machineMac;
    private String implBean;
    private String machineId;
    private String machineCode;
    private String communityId;
    private String machineName;
    private String machineIp;


    private Date createTime;

    private String statusCd = "0";


    public String getMachineMac() {
        return machineMac;
    }

    public void setMachineMac(String machineMac) {
        this.machineMac = machineMac;
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
