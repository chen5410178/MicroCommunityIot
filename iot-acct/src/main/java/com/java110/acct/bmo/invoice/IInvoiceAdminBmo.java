package com.java110.acct.bmo.invoice;

/**
 * 发票管理权限校验（PC 管理端专用）
 * 物联网手机端不接受入参手机号、以登录用户为准，不做本校验
 */
public interface IInvoiceAdminBmo {

    /**
     * 校验当前用户是否为该小区管理员（管理员：系统管理员 / 商户管理员 / 已授权该小区的员工）
     * 校验失败抛出 CmdException（业务错误码，不静默降级）
     *
     * @param storeId     当前登录商户ID（可为空，为空时仅按用户级别与小区授权校验）
     * @param userId      当前登录用户ID
     * @param communityId 小区ID
     */
    void validateCommunityAdmin(String storeId, String userId, String communityId);
}
