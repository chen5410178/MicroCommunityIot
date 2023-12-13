package com.java110.dto.visit;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

public class VisitDto extends PageDto implements Serializable {
    private String visitId;
    private String name;
    private String visitGender;
    private String phoneNumber;
    private String communityId;
    private String roomName;
    private String roomId;
    private String ownerId;
    private String ownerName;
    private String typeId;
    private String visitCase;
    private Date visitTime;
    private Date departureTime;
    private String facePath;
    private String qrcode;
    private String state;
    private String msg;
    private Date createTime;
    private String statusCd = "0";

    private String visitTypeName;
    private String stateName;
    private String nameLike;
    private String phoneNumberLike;
    private String roomNameLike;
    private String ownerNameLike;

    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVisitGender() {
        return visitGender;
    }

    public void setVisitGender(String visitGender) {
        this.visitGender = visitGender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
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

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getVisitCase() {
        return visitCase;
    }

    public void setVisitCase(String visitCase) {
        this.visitCase = visitCase;
    }

    public Date getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(Date visitTime) {
        this.visitTime = visitTime;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public String getFacePath() {
        return facePath;
    }

    public void setFacePath(String facePath) {
        this.facePath = facePath;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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

    public String getVisitTypeName() {
        return visitTypeName;
    }

    public void setVisitTypeName(String visitTypeName) {
        this.visitTypeName = visitTypeName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getNameLike() {
        return nameLike;
    }

    public void setNameLike(String nameLike) {
        this.nameLike = nameLike;
    }

    public String getPhoneNumberLike() {
        return phoneNumberLike;
    }

    public void setPhoneNumberLike(String phoneNumberLike) {
        this.phoneNumberLike = phoneNumberLike;
    }

    public String getRoomNameLike() {
        return roomNameLike;
    }

    public void setRoomNameLike(String roomNameLike) {
        this.roomNameLike = roomNameLike;
    }

    public String getOwnerNameLike() {
        return ownerNameLike;
    }

    public void setOwnerNameLike(String ownerNameLike) {
        this.ownerNameLike = ownerNameLike;
    }
}
