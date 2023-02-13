package com.java110.dto.communityMember;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 小区成员数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class CommunityMemberDto extends PageDto implements Serializable {


    public static final String AUDIT_STATUS_NORMAL = "1100"; // 审核通过

    public static final String MEMBER_TYPE_PROPERTY = "800900000003";

    private String memberTypeCd;
    private String memberName;
    private String startTime;
    private String endTime;
    private String communityId;
    private String communityMemberId;
    private String memberId;


    private Date createTime;

    private String statusCd = "0";


    public String getMemberTypeCd() {
        return memberTypeCd;
    }

    public void setMemberTypeCd(String memberTypeCd) {
        this.memberTypeCd = memberTypeCd;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
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

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getCommunityMemberId() {
        return communityMemberId;
    }

    public void setCommunityMemberId(String communityMemberId) {
        this.communityMemberId = communityMemberId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
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
