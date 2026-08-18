package com.java110.po.accessControlShop;

import java.io.Serializable;

/**
 * 商铺授权门禁持久化对象
 */
public class AccessControlShopPo implements Serializable {

    private String acsId;
    private String machineId;
    private String communityId;
    private String locationId;
    private String locationName;
    private String statusCd = "0";

    public String getAcsId() {
        return acsId;
    }

    public void setAcsId(String acsId) {
        this.acsId = acsId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }
}
