package com.java110.dto.visit;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

public class VisitTypeDto extends PageDto implements Serializable {
    private String typeId;
    private String name;
    private String visitWay;
    private String auditWay;
    private String visitDay;
    private String remark;
    private String communityId;
    private Date createTime;
    private String statusCd = "0";

    private String nameLike;
    private String visitWayName;
    private String auditWayName;

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVisitWay() {
        return visitWay;
    }

    public void setVisitWay(String visitWay) {
        this.visitWay = visitWay;
    }

    public String getAuditWay() {
        return auditWay;
    }

    public void setAuditWay(String auditWay) {
        this.auditWay = auditWay;
    }

    public String getVisitDay() {
        return visitDay;
    }

    public void setVisitDay(String visitDay) {
        this.visitDay = visitDay;
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

    public String getNameLike() {
        return nameLike;
    }

    public void setNameLike(String nameLike) {
        this.nameLike = nameLike;
    }

    public String getVisitWayName() {
        return visitWayName;
    }

    public void setVisitWayName(String visitWayName) {
        this.visitWayName = visitWayName;
    }

    public String getAuditWayName() {
        return auditWayName;
    }

    public void setAuditWayName(String auditWayName) {
        this.auditWayName = auditWayName;
    }
}
