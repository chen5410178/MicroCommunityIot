package com.java110.dto.parking;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 停车场数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class ParkingAreaDto extends PageDto implements Serializable {

    private String typeCd;
    private String num;
    private String paId;
    private String[] paIds;
    private String remark;
    private String communityId;
    private String extPaId;

    private String defaultArea;

    private String tempCarIn;
    private String fee;
    private String blueCarIn;
    private String yelowCarIn;

    private String tempAuth;


    private Date createTime;

    private String statusCd = "0";

    private String boxId;

    private String findQrcodeUrl;


    public String getTypeCd() {
        return typeCd;
    }

    public void setTypeCd(String typeCd) {
        this.typeCd = typeCd;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getPaId() {
        return paId;
    }

    public void setPaId(String paId) {
        this.paId = paId;
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

    public String getExtPaId() {
        return extPaId;
    }

    public void setExtPaId(String extPaId) {
        this.extPaId = extPaId;
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

    public String[] getPaIds() {
        return paIds;
    }

    public void setPaIds(String[] paIds) {
        this.paIds = paIds;
    }

    public String getDefaultArea() {
        return defaultArea;
    }

    public void setDefaultArea(String defaultArea) {
        this.defaultArea = defaultArea;
    }

    public String getTempCarIn() {
        return tempCarIn;
    }

    public void setTempCarIn(String tempCarIn) {
        this.tempCarIn = tempCarIn;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getBlueCarIn() {
        return blueCarIn;
    }

    public void setBlueCarIn(String blueCarIn) {
        this.blueCarIn = blueCarIn;
    }

    public String getYelowCarIn() {
        return yelowCarIn;
    }

    public void setYelowCarIn(String yelowCarIn) {
        this.yelowCarIn = yelowCarIn;
    }

    public String getTempAuth() {
        return tempAuth;
    }

    public void setTempAuth(String tempAuth) {
        this.tempAuth = tempAuth;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public String getFindQrcodeUrl() {
        return findQrcodeUrl;
    }

    public void setFindQrcodeUrl(String findQrcodeUrl) {
        this.findQrcodeUrl = findQrcodeUrl;
    }
}
