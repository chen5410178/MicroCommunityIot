package com.java110.po.visit;

import java.io.Serializable;

public class VisitTypePo implements Serializable {
    private String typeId;
    private String name;
    private String visitWay;
    private String auditWay;
    private String visitDay;
    private String remark;
    private String communityId;
    private String statusCd = "0";

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

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }
}
