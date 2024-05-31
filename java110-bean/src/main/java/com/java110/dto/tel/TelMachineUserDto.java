package com.java110.dto.tel;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 客服人员数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class TelMachineUserDto extends PageDto implements Serializable {

    private String masterFlag;
private String tmuId;
private String machineId;
private String staffTel;
private String staffName;
private String communityId;
private String staffId;


    private Date createTime;

    private String statusCd = "0";


    public String getMasterFlag() {
        return masterFlag;
    }
public void setMasterFlag(String masterFlag) {
        this.masterFlag = masterFlag;
    }
public String getTmuId() {
        return tmuId;
    }
public void setTmuId(String tmuId) {
        this.tmuId = tmuId;
    }
public String getMachineId() {
        return machineId;
    }
public void setMachineId(String machineId) {
        this.machineId = machineId;
    }
public String getStaffTel() {
        return staffTel;
    }
public void setStaffTel(String staffTel) {
        this.staffTel = staffTel;
    }
public String getStaffName() {
        return staffName;
    }
public void setStaffName(String staffName) {
        this.staffName = staffName;
    }
public String getCommunityId() {
        return communityId;
    }
public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }
public String getStaffId() {
        return staffId;
    }
public void setStaffId(String staffId) {
        this.staffId = staffId;
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
