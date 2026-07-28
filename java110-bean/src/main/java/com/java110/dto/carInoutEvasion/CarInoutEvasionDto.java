package com.java110.dto.carInoutEvasion;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;

public class CarInoutEvasionDto extends PageDto implements Serializable {

    public static final String STATE_W = "W";
    public static final String STATE_D = "D";

    private String evasionId;
    private String carNum;
    private String communityId;
    private String paId;
    private String paNum;
    private String boxId;
    private String inoutId;
    private String ciId;
    private String inTime;
    private String evasionTime;
    private String evasionTimeStart;
    private String evasionTimeEnd;
    private String amount;
    private String state;
    private String remark;
    private String photoJpg;

    public String getEvasionId() {
        return evasionId;
    }

    public void setEvasionId(String evasionId) {
        this.evasionId = evasionId;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
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

    public String getPaNum() {
        return paNum;
    }

    public void setPaNum(String paNum) {
        this.paNum = paNum;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public String getInoutId() {
        return inoutId;
    }

    public void setInoutId(String inoutId) {
        this.inoutId = inoutId;
    }

    public String getCiId() {
        return ciId;
    }

    public void setCiId(String ciId) {
        this.ciId = ciId;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getEvasionTime() {
        return evasionTime;
    }

    public void setEvasionTime(String evasionTime) {
        this.evasionTime = evasionTime;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEvasionTimeStart() {
        return evasionTimeStart;
    }

    public void setEvasionTimeStart(String evasionTimeStart) {
        this.evasionTimeStart = evasionTimeStart;
    }

    public String getEvasionTimeEnd() {
        return evasionTimeEnd;
    }

    public void setEvasionTimeEnd(String evasionTimeEnd) {
        this.evasionTimeEnd = evasionTimeEnd;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPhotoJpg() {
        return photoJpg;
    }

    public void setPhotoJpg(String photoJpg) {
        this.photoJpg = photoJpg;
    }
}
