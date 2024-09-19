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

    public static final String STATE_REQ = "10001";//请求成功 10001
    public static final String STATE_RES = "10002";//返回成功10002
    public static final String STATE_FAIL = "10003";//操作失败 10003

    public static final String CMD_ADD_FACE = "ADD_FACE"; // todo 添加人脸

    public static final String CMD_UPDATE_FACE = "UPDATE_FACE"; // todo 修改人脸

    public static final String CMD_DELETE_FACE = "DELETE_FACE"; // todo 删除人脸

    public static final String CMD_REBOOT = "REBOOT"; // todo 重启

    public static final String CMD_OPEN_DOOR = "OPEN_DOOR"; // todo 开门
    private String resParam;
    private String machineId;
    private String logId;
    private String reqParam;
    private String state;
    private String[] states;
    private String communityId;
    private String userName;
    private String logAction;
    private String userId;

    private String[] userIds;


    private Date createTime;

    private String statusCd = "0";

    private String machineName;

    private String machineCode;

    private String stateName;

    private String queryStartTime;

    private String queryEndTime;

    private String logCmd;
    private String[] logCmds;

    private String logCmdName;

    private String cTime;

    private String count;



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

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String[] getUserIds() {
        return userIds;
    }

    public void setUserIds(String[] userIds) {
        this.userIds = userIds;
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

    public String getLogCmd() {
        return logCmd;
    }

    public void setLogCmd(String logCmd) {
        this.logCmd = logCmd;
    }

    public String getLogCmdName() {
        return logCmdName;
    }

    public void setLogCmdName(String logCmdName) {
        this.logCmdName = logCmdName;
    }

    public String[] getStates() {
        return states;
    }

    public void setStates(String[] states) {
        this.states = states;
    }

    public String[] getLogCmds() {
        return logCmds;
    }

    public void setLogCmds(String[] logCmds) {
        this.logCmds = logCmds;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getcTime() {
        return cTime;
    }

    public void setcTime(String cTime) {
        this.cTime = cTime;
    }
}
