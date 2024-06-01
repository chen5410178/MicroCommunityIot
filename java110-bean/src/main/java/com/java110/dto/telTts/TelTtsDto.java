package com.java110.dto.telTts;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 客服人员数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class TelTtsDto extends PageDto implements Serializable {

    private String ttsName;
    private String ttsPath;
    private String remark;
    private String ttsType;
    private String communityId;
    private String ttId;


    private Date createTime;

    private String statusCd = "0";


    public String getTtsName() {
        return ttsName;
    }

    public void setTtsName(String ttsName) {
        this.ttsName = ttsName;
    }

    public String getTtsPath() {
        return ttsPath;
    }

    public void setTtsPath(String ttsPath) {
        this.ttsPath = ttsPath;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTtsType() {
        return ttsType;
    }

    public void setTtsType(String ttsType) {
        this.ttsType = ttsType;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getTtId() {
        return ttId;
    }

    public void setTtId(String ttId) {
        this.ttId = ttId;
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
