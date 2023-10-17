package com.java110.po.monitor;

import java.io.Serializable;

public class MonitorModelPo implements Serializable {
    private String modelId;
    private String modelName;
    private String modelAdapt;
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

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }
}
