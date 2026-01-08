package com.java110.bean.dto.car;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName TempInnerCarDto
 * @Description 临时内部车数据层封装
 * @Author wuxw
 * @Date 2024/01/01 8:52
 * @Version 1.0
 * add by wuxw 2024/01/01
 **/
public class TempInnerCarDto extends PageDto implements Serializable {

    private String ticId;
    private String carNum;
    private String startTime;
    private String endTime;
    private String configId;
    private String feeName;
    private String communityId;
    private String paId;
    private String paNum;
    private String remark;
    private Date createTime;
    private String statusCd = "0";

    public String getTicId() {
        return ticId;
    }

    public void setTicId(String ticId) {
        this.ticId = ticId;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
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

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getFeeName() {
        return feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
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

    public String getPaNum() {
        return paNum;
    }

    public void setPaNum(String paNum) {
        this.paNum = paNum;
    }
}

