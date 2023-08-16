package com.java110.dto.accessControlLog;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 门禁日志数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class AccessControlLogDto extends PageDto implements Serializable {

    private String resParam;
private String machineId;
private String logId;
private String reqParam;
private String state;
private String communityId;
private String userName;
private String logAction;
private String userId;


    private Date createTime;

    private String statusCd = "0";


    public String getResParam() {
        return resParam;
    }
public void setResParam(String resParam) {
        this.resParam = resParam;
    }
public String getMachineId() {
        return machineId;
    }
public void setMachineId(String machineId) {
        this.machineId = machineId;
    }
public String getLogId() {
        return logId;
    }
public void setLogId(String logId) {
        this.logId = logId;
    }
public String getReqParam() {
        return reqParam;
    }
public void setReqParam(String reqParam) {
        this.reqParam = reqParam;
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
public String getUserName() {
        return userName;
    }
public void setUserName(String userName) {
        this.userName = userName;
    }
public String getLogAction() {
        return logAction;
    }
public void setLogAction(String logAction) {
        this.logAction = logAction;
    }
public String getUserId() {
        return userId;
    }
public void setUserId(String userId) {
        this.userId = userId;
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
}
