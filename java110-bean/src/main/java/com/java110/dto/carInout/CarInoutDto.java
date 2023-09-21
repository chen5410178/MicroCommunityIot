package com.java110.dto.carInout;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 出入场明细数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class CarInoutDto extends PageDto implements Serializable {


    //状态，100300 进场状态 100400 支付完成 100500 离场状态 100600 支付超时重新支付
    public static final String STATE_IN = "100300";
    public static final String STATE_PAY = "100400";
    public static final String STATE_OUT = "100500";
    public static final String STATE_REPAY = "100600";

    public static final String STATE_IN_FAIL = "100301";
    public static final String CAR_TYPE_MONTH = "1001";
    public static final String CAR_TYPE_TEMP = "1003";

    private String paNum;
    private String machineCode;
    private String carInout;
    private String photoJpg;
    private String carNum;
    private String remark;
    private String ciId;
    private String inoutId;
    private String machineId;
    private String carType;
    private String paId;
    private String[] paIds;
    private String state;
    private String[] states;
    private String communityId;
    private String openTime;
    private String carTypeName;


    private Date createTime;

    private String statusCd = "0";

    private String payTime;


    public String getPaNum() {
        return paNum;
    }

    public void setPaNum(String paNum) {
        this.paNum = paNum;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getCarInout() {
        return carInout;
    }

    public void setCarInout(String carInout) {
        this.carInout = carInout;
    }

    public String getPhotoJpg() {
        return photoJpg;
    }

    public void setPhotoJpg(String photoJpg) {
        this.photoJpg = photoJpg;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCiId() {
        return ciId;
    }

    public void setCiId(String ciId) {
        this.ciId = ciId;
    }

    public String getInoutId() {
        return inoutId;
    }

    public void setInoutId(String inoutId) {
        this.inoutId = inoutId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getPaId() {
        return paId;
    }

    public void setPaId(String paId) {
        this.paId = paId;
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

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCarTypeName() {
        return carTypeName;
    }

    public void setCarTypeName(String carTypeName) {
        this.carTypeName = carTypeName;
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

    public String[] getStates() {
        return states;
    }

    public void setStates(String[] states) {
        this.states = states;
    }

    public String[] getPaIds() {
        return paIds;
    }

    public void setPaIds(String[] paIds) {
        this.paIds = paIds;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }
}
