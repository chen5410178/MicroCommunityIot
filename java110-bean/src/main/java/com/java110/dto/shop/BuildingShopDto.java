package com.java110.dto.shop;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;

/**
 * 商铺DTO
 */
public class BuildingShopDto extends PageDto implements Serializable {

    public static final String STATE_RENT = "2006";
    public static final String STATE_SELL = "2007";
    public static final String STATE_FREE = "2008";

    private String shopId;
    private String shopNum;
    private String locationId;
    private String locationName;
    private String ownerId;
    private String ownerName;
    private String builtUpArea;
    private String shopArea;
    private String remark;
    private String state;
    private String communityId;
    private String link;
    private String shopRent;
    private String startTime;
    private String endTime;
    private String statusCd = "0";
    private String createTime;
    private String rentTimeStart;
    private String rentTimeEnd;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopNum() {
        return shopNum;
    }

    public void setShopNum(String shopNum) {
        this.shopNum = shopNum;
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

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getBuiltUpArea() {
        return builtUpArea;
    }

    public void setBuiltUpArea(String builtUpArea) {
        this.builtUpArea = builtUpArea;
    }

    public String getShopArea() {
        return shopArea;
    }

    public void setShopArea(String shopArea) {
        this.shopArea = shopArea;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getShopRent() {
        return shopRent;
    }

    public void setShopRent(String shopRent) {
        this.shopRent = shopRent;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getRentTimeStart() {
        return rentTimeStart;
    }

    public void setRentTimeStart(String rentTimeStart) {
        this.rentTimeStart = rentTimeStart;
    }

    public String getRentTimeEnd() {
        return rentTimeEnd;
    }

    public void setRentTimeEnd(String rentTimeEnd) {
        this.rentTimeEnd = rentTimeEnd;
    }
}
