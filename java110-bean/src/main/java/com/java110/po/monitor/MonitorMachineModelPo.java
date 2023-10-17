package com.java110.po.monitor;

import java.io.Serializable;

public class MonitorMachineModelPo implements Serializable {
    private String mmmId;
    private String machineId;
    private String modelId;
    private String communityId;
    private String statusCd = "0";

    public String getMmmId() {
        return mmmId;
    }

    public void setMmmId(String mmmId) {
        this.mmmId = mmmId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
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
