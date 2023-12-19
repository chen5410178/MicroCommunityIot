package com.java110.dto.meter;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

public class MeterQrcodeDto extends PageDto implements Serializable {
    private String mqId;
    private String qrcodeName;
    private String communityId;
    private String queryWay;
    private String createStaffId;
    private String createStaffName;
    private String state;
    private Date createTime;
    private String statusCd = "0";
    private String remark;

    private String qrcodeUrl;
    private String queryWayName;
    private String qrcodeNameLike;

    public String getMqId() {
        return mqId;
    }

    public void setMqId(String mqId) {
        this.mqId = mqId;
    }

    public String getQrcodeName() {
        return qrcodeName;
    }

    public void setQrcodeName(String qrcodeName) {
        this.qrcodeName = qrcodeName;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getQueryWay() {
        return queryWay;
    }

    public void setQueryWay(String queryWay) {
        this.queryWay = queryWay;
    }

    public String getCreateStaffId() {
        return createStaffId;
    }

    public void setCreateStaffId(String createStaffId) {
        this.createStaffId = createStaffId;
    }

    public String getCreateStaffName() {
        return createStaffName;
    }

    public void setCreateStaffName(String createStaffName) {
        this.createStaffName = createStaffName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getQrcodeUrl() {
        return qrcodeUrl;
    }

    public void setQrcodeUrl(String qrcodeUrl) {
        this.qrcodeUrl = qrcodeUrl;
    }

    public String getQueryWayName() {
        return queryWayName;
    }

    public void setQueryWayName(String queryWayName) {
        this.queryWayName = queryWayName;
    }

    public String getQrcodeNameLike() {
        return qrcodeNameLike;
    }

    public void setQrcodeNameLike(String qrcodeNameLike) {
        this.qrcodeNameLike = qrcodeNameLike;
    }
}
