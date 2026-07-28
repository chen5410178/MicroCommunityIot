package com.java110.dto.carInoutEvasion;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;

public class CarInoutEvasionConfigDto extends PageDto implements Serializable {

    private String configId;
    private String communityId;
    private String evasionOpen;
    private String queryDays;
    private String queryMinutes;

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getEvasionOpen() {
        return evasionOpen;
    }

    public void setEvasionOpen(String evasionOpen) {
        this.evasionOpen = evasionOpen;
    }

    public String getQueryDays() {
        return queryDays;
    }

    public void setQueryDays(String queryDays) {
        this.queryDays = queryDays;
    }

    public String getQueryMinutes() {
        return queryMinutes;
    }

    public void setQueryMinutes(String queryMinutes) {
        this.queryMinutes = queryMinutes;
    }
}
