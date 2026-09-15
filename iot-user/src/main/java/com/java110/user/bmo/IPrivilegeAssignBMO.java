package com.java110.user.bmo;

import com.alibaba.fastjson.JSONArray;

import java.util.List;
import java.util.Set;

/**
 * 权限可分配范围
 */
public interface IPrivilegeAssignBMO {

    /**
     * 是否管理员：商户管理员(s_store_staff.admin_flag='Y') 或 系统管理员(u_user.level_cd='00')
     */
    boolean isAdmin(String storeId, String userId);

    /**
     * 操作人拥有的权限ID集合（组权限 ∪ 直授权限，按 domain 过滤）
     */
    Set<String> queryOwnPrivilegeIds(String userId, String domain);

    /**
     * 非管理员时就地剔除权限树中操作人不拥有的节点（管理员不处理）
     */
    void filterAssignable(JSONArray privileges, String storeId, String userId, String domain);

    /**
     * 校验待分配权限是否都在操作人权限范围内，越权抛 CmdException
     */
    void validateAssignable(String storeId, String userId, String domain, List<String> pIds);
}
