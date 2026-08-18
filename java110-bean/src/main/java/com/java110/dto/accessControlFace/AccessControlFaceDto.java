package com.java110.dto.accessControlFace;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 门禁人员数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class AccessControlFaceDto extends PageDto implements Serializable {

    public static final String COME_TYPE_FLOOR = "FLOOR"; //来自类型 FLOOR 楼栋授权 OWNER 人员授权 STAFF 员工授权
    public static final String COME_TYPE_OWNER = "OWNER"; //来自类型 FLOOR 楼栋授权 OWNER 人员授权 STAFF 员工授权
    public static final String COME_TYPE_STAFF = "STAFF"; //来自类型 FLOOR 楼栋授权 OWNER 人员授权 STAFF 员工授权
    public static final String COME_TYPE_SHOP = "SHOP"; //来自类型 SHOP 商铺授权

    public static final String PERSON_TYPE_OWNER = "OWNER"; //OWNER 业主 STAFF
    public static final String PERSON_TYPE_STAFF = "STAFF"; //OWNER 业主 STAFF

    public static final String STATE_WAIT = "W";//待下发
    public static final String STATE_FAIL = "F";//下发失败
    public static final String STATE_COMPLETE = "C";//待下发


    private String comeId;
    private String[] comeIds;
    private String comeType;

    private String comeTypeName;
    private String mfId;
    private String idNumber;
    private String message;
    private String machineId;
    private String name;
    private String personId;
    private String startTime;
    private String endTime;
    private String facePath;
    private String state;
    private String stateName;
    private String personType;
    private String communityId;
    private String cardNumber;


    private Date createTime;

    private String statusCd = "0";

    private String roomName;

    private String personCount;

    private String machineName;

    private String machineCode;

    private String tel;

    private String queryStartTime;

    private String queryEndTime;

    private String ownerLink;

    private String staffLink;


    public String getComeId() {
        return comeId;
    }

    public void setComeId(String comeId) {
        this.comeId = comeId;
    }

    public String getComeType() {
        return comeType;
    }

    public void setComeType(String comeType) {
        this.comeType = comeType;
    }

    public String getMfId() {
        return mfId;
    }

    public void setMfId(String mfId) {
        this.mfId = mfId;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
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

    public String getFacePath() {
        return facePath;
    }

    public void setFacePath(String facePath) {
        this.facePath = facePath;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
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

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String[] getComeIds() {
        return comeIds;
    }

    public void setComeIds(String[] comeIds) {
        this.comeIds = comeIds;
    }

    public String getPersonCount() {
        return personCount;
    }

    public void setPersonCount(String personCount) {
        this.personCount = personCount;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getComeTypeName() {
        return comeTypeName;
    }

    public void setComeTypeName(String comeTypeName) {
        this.comeTypeName = comeTypeName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getQueryStartTime() {
        return queryStartTime;
    }

    public void setQueryStartTime(String queryStartTime) {
        this.queryStartTime = queryStartTime;
    }

    public String getQueryEndTime() {
        return queryEndTime;
    }

    public void setQueryEndTime(String queryEndTime) {
        this.queryEndTime = queryEndTime;
    }

    public String getOwnerLink() {
        return ownerLink;
    }

    public void setOwnerLink(String ownerLink) {
        this.ownerLink = ownerLink;
    }

    public String getStaffLink() {
        return staffLink;
    }

    public void setStaffLink(String staffLink) {
        this.staffLink = staffLink;
    }
}
