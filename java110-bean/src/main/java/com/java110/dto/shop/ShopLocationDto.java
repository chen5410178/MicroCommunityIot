package com.java110.dto.shop;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.List;

/**
 * 商铺位置DTO
 */
public class ShopLocationDto extends PageDto implements Serializable {

    private String locationId;
    private String locationName;
    private String locationNameLike;
    private String parentId;
    private int level;
    private String remark;
    private String communityId;
    private int seq;
    private String statusCd = "0";

    private List<ShopLocationDto> children;

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

    public String getLocationNameLike() {
        return locationNameLike;
    }

    public void setLocationNameLike(String locationNameLike) {
        this.locationNameLike = locationNameLike;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public List<ShopLocationDto> getChildren() {
        return children;
    }

    public void setChildren(List<ShopLocationDto> children) {
        this.children = children;
    }
}
