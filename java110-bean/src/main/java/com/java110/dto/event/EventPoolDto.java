package com.java110.dto.event;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class EventPoolDto extends PageDto implements Serializable {
    public static final String EVENT_STATE_WAITTING = "W";
    public static final String EVENT_STATE_DOING = "D";
    public static final String EVENT_STATE_NORMAL_COMPLETE = "NC";
    public static final String EVENT_STATE_TIMEOUT_COMPLETE = "TC";
    private String eventId;
    private String ruleId;
    private String eventType;
    private String eventMsg;
    private String objType;
    private String objectId;
    private String objectName;
    private String fromType;
    private String fromName;
    private String communityId;
    private String state;
    private String remark;
    private Date createTime;
    private String statusCd = "0";

    private String ruleName;
    private String eventTypeName;
    private String objTypeName;

    private List<EventPoolStaffDto> eventPoolStaffList;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventMsg() {
        return eventMsg;
    }

    public void setEventMsg(String eventMsg) {
        this.eventMsg = eventMsg;
    }

    public String getObjType() {
        return objType;
    }

    public void setObjType(String objType) {
        this.objType = objType;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getFromType() {
        return fromType;
    }

    public void setFromType(String fromType) {
        this.fromType = fromType;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getEventTypeName() {
        return eventTypeName;
    }

    public void setEventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }

    public String getObjTypeName() {
        return objTypeName;
    }

    public void setObjTypeName(String objTypeName) {
        this.objTypeName = objTypeName;
    }

    public List<EventPoolStaffDto> getEventPoolStaffList() {
        return eventPoolStaffList;
    }

    public void setEventPoolStaffList(List<EventPoolStaffDto> eventPoolStaffList) {
        this.eventPoolStaffList = eventPoolStaffList;
    }
}
