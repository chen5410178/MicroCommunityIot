package com.java110.dto.monitorManufactor;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 监控厂家数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class MonitorManufactorDto extends PageDto implements Serializable {

    private String appSecure;
    private String appId;
    private String mmId;
    private String remark;
    private String communityId;
    private String mmUrl;
    private String mmName;
    private String mmBean;
    private String mmBeanName;


    private Date createTime;

    private String statusCd = "0";


    public String getAppSecure() {
        return appSecure;
    }

    public void setAppSecure(String appSecure) {
        this.appSecure = appSecure;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getMmId() {
        return mmId;
    }

    public void setMmId(String mmId) {
        this.mmId = mmId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getMmUrl() {
        return mmUrl;
    }

    public void setMmUrl(String mmUrl) {
        this.mmUrl = mmUrl;
    }

    public String getMmName() {
        return mmName;
    }

    public void setMmName(String mmName) {
        this.mmName = mmName;
    }

    public String getMmBean() {
        return mmBean;
    }

    public void setMmBean(String mmBean) {
        this.mmBean = mmBean;
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

    public String getMmBeanName() {
        return mmBeanName;
    }

    public void setMmBeanName(String mmBeanName) {
        this.mmBeanName = mmBeanName;
    }
}
