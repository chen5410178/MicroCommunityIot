package com.java110.dto.event;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

public class EventTemplateParamKeyDto extends PageDto implements Serializable {
    private String keyId;
    private String eventWay;
    private String specName;
    private String specCd;
    private String remark;
    private Date createTime;
    private String statusCd = "0";

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getEventWay() {
        return eventWay;
    }

    public void setEventWay(String eventWay) {
        this.eventWay = eventWay;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    public String getSpecCd() {
        return specCd;
    }

    public void setSpecCd(String specCd) {
        this.specCd = specCd;
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
