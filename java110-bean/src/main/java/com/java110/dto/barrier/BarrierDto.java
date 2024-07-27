package com.java110.dto.barrier;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 道闸摄像头数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class BarrierDto extends PageDto implements Serializable {

    public static final String MACHINE_DIRECTION_ENTER = "3306"; // 进场
    public static final String MACHINE_DIRECTION_OUT = "3307"; // 出场

    private String heartbeatTime;
    private String implBean;
    private String machineId;
    private String machineCode;
    private String communityId;
    private String machineName;
    private String machineIp;
    private String direction;
    private String boxId;
    private String[] boxIds;
    private String boxName;

    private String implBeanName;
    private String stateName;


    private Date createTime;

    private String statusCd = "0";

    private String photoJpg;

    private String photoFragment;

    private String hmId;

    private String paId;

    private String monitorId;
    private String monitorName;


    public String getHeartbeatTime() {
        return heartbeatTime;
    }

    public void setHeartbeatTime(String heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
    }

    public String getImplBean() {
        return implBean;
    }

    public void setImplBean(String implBean) {
        this.implBean = implBean;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getMachineIp() {
        return machineIp;
    }

    public void setMachineIp(String machineIp) {
        this.machineIp = machineIp;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
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

    public String getBoxName() {
        return boxName;
    }

    public void setBoxName(String boxName) {
        this.boxName = boxName;
    }

    public String getImplBeanName() {
        return implBeanName;
    }

    public void setImplBeanName(String implBeanName) {
        this.implBeanName = implBeanName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getPhotoJpg() {
        return photoJpg;
    }

    public void setPhotoJpg(String photoJpg) {
        this.photoJpg = photoJpg;
    }

    public String getHmId() {
        return hmId;
    }

    public void setHmId(String hmId) {
        this.hmId = hmId;
    }

    public String getPaId() {
        return paId;
    }

    public void setPaId(String paId) {
        this.paId = paId;
    }

    public String[] getBoxIds() {
        return boxIds;
    }

    public void setBoxIds(String[] boxIds) {
        this.boxIds = boxIds;
    }

    public String getPhotoFragment() {
        return photoFragment;
    }

    public void setPhotoFragment(String photoFragment) {
        this.photoFragment = photoFragment;
    }

    public String getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(String monitorId) {
        this.monitorId = monitorId;
    }

    public String getMonitorName() {
        return monitorName;
    }

    public void setMonitorName(String monitorName) {
        this.monitorName = monitorName;
    }
}
