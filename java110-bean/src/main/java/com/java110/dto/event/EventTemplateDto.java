package com.java110.dto.event;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

public class EventTemplateDto extends PageDto implements Serializable {
    private String templateId;
    private String templateName;
    private String eventWay;
    private String communityId;
    private String remark;
    private Date createTime;
    private String statusCd = "0";

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getEventWay() {
        return eventWay;
    }

    public void setEventWay(String eventWay) {
        this.eventWay = eventWay;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
