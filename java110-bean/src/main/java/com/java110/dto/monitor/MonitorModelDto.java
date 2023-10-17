package com.java110.dto.monitor;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

public class MonitorModelDto extends PageDto implements Serializable {
    private String modelId;
    private String modelName;
    private String modelAdapt;
    private Date createTime;
    private String statusCd = "0";

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelAdapt() {
        return modelAdapt;
    }

    public void setModelAdapt(String modelAdapt) {
        this.modelAdapt = modelAdapt;
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
