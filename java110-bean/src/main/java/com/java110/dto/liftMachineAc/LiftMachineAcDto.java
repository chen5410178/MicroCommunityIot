package com.java110.dto.liftMachineAc;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 电梯门禁数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class LiftMachineAcDto extends PageDto implements Serializable {

    private String acMachineId;
    private String machineId;
    private String acMachineCode;
    private String communityId;
    private String acType;
    private String acMachineName;
    private String lmaId;


    private Date createTime;

    private String statusCd = "0";


    public String getAcMachineId() {
        return acMachineId;
    }

    public void setAcMachineId(String acMachineId) {
        this.acMachineId = acMachineId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getAcMachineCode() {
        return acMachineCode;
    }

    public void setAcMachineCode(String acMachineCode) {
        this.acMachineCode = acMachineCode;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getAcType() {
        return acType;
    }

    public void setAcType(String acType) {
        this.acType = acType;
    }

    public String getAcMachineName() {
        return acMachineName;
    }

    public void setAcMachineName(String acMachineName) {
        this.acMachineName = acMachineName;
    }

    public String getLmaId() {
        return lmaId;
    }

    public void setLmaId(String lmaId) {
        this.lmaId = lmaId;
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
