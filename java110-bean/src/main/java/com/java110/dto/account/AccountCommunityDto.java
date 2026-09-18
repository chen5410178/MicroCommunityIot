package com.java110.dto.account;

/**
 * 账户 + 小区名称展示数据层封装（业主端查询多小区账户余额）
 */
public class AccountCommunityDto extends AccountDto {

    /**
     * 小区ID（与账户 partId 一致）
     */
    private String communityId;

    /**
     * 小区名称
     */
    private String communityName;

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }
}
