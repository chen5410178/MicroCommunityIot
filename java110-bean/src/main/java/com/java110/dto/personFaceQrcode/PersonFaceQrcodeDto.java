package com.java110.dto.personFaceQrcode;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 人脸二维码数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class PersonFaceQrcodeDto extends PageDto implements Serializable {

    public static final String OFF = "OFF";
    private String remark;
    private String pfqId;
    private String smsValidate;
    private String telValidate;
    private String createStaffName;
    private String createStaffId;
    private String audit;
    private String inviteCode;
    private String invite;
    private String state;
    private String qrcodeName;
    private String communityId;


    private Date createTime;

    private String statusCd = "0";


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPfqId() {
        return pfqId;
    }

    public void setPfqId(String pfqId) {
        this.pfqId = pfqId;
    }

    public String getSmsValidate() {
        return smsValidate;
    }

    public void setSmsValidate(String smsValidate) {
        this.smsValidate = smsValidate;
    }

    public String getTelValidate() {
        return telValidate;
    }

    public void setTelValidate(String telValidate) {
        this.telValidate = telValidate;
    }

    public String getCreateStaffName() {
        return createStaffName;
    }

    public void setCreateStaffName(String createStaffName) {
        this.createStaffName = createStaffName;
    }

    public String getCreateStaffId() {
        return createStaffId;
    }

    public void setCreateStaffId(String createStaffId) {
        this.createStaffId = createStaffId;
    }

    public String getAudit() {
        return audit;
    }

    public void setAudit(String audit) {
        this.audit = audit;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getInvite() {
        return invite;
    }

    public void setInvite(String invite) {
        this.invite = invite;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getQrcodeName() {
        return qrcodeName;
    }

    public void setQrcodeName(String qrcodeName) {
        this.qrcodeName = qrcodeName;
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
