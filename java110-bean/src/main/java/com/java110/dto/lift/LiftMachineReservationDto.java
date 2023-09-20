package com.java110.dto.lift;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

public class LiftMachineReservationDto extends PageDto implements Serializable {
    private String lmrId;
    private String machineId;
    private String resWay;
    private String roomId;
    private String roomName;
    private String personId;
    private String personName;
    private String personWay;
    private String orgLayer;
    private String targetLayer;
    private Date createTime;
    private String statusCd = "0";

    private String resWayName;
    private String personWayName;

    public String getLmrId() {
        return lmrId;
    }

    public void setLmrId(String lmrId) {
        this.lmrId = lmrId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getResWay() {
        return resWay;
    }

    public void setResWay(String resWay) {
        this.resWay = resWay;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonWay() {
        return personWay;
    }

    public void setPersonWay(String personWay) {
        this.personWay = personWay;
    }

    public String getOrgLayer() {
        return orgLayer;
    }

    public void setOrgLayer(String orgLayer) {
        this.orgLayer = orgLayer;
    }

    public String getTargetLayer() {
        return targetLayer;
    }

    public void setTargetLayer(String targetLayer) {
        this.targetLayer = targetLayer;
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

    public String getResWayName() {
        return resWayName;
    }

    public void setResWayName(String resWayName) {
        this.resWayName = resWayName;
    }

    public String getPersonWayName() {
        return personWayName;
    }

    public void setPersonWayName(String personWayName) {
        this.personWayName = personWayName;
    }
}
