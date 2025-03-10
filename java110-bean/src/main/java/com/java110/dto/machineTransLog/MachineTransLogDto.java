package com.java110.dto.machineTransLog;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 硬件交互日志数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class MachineTransLogDto extends PageDto implements Serializable {

    private String logType;
private String resParam;
private String logCmd;
private String machineCode;
private String busiKey;
private String logId;
private String reqParam;
private String communityId;


    private Date createTime;

    private String statusCd = "0";


    public String getLogType() {
        return logType;
    }
public void setLogType(String logType) {
        this.logType = logType;
    }
public String getResParam() {
        return resParam;
    }
public void setResParam(String resParam) {
        this.resParam = resParam;
    }
public String getLogCmd() {
        return logCmd;
    }
public void setLogCmd(String logCmd) {
        this.logCmd = logCmd;
    }
public String getMachineCode() {
        return machineCode;
    }
public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }
public String getBusiKey() {
        return busiKey;
    }
public void setBusiKey(String busiKey) {
        this.busiKey = busiKey;
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
public String getCommunityId() {
        return communityId;
    }
public void setCommunityId(String communityId) {
        this.communityId = communityId;
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
