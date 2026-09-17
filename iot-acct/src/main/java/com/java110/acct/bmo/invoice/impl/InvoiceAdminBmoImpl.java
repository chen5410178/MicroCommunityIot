package com.java110.acct.bmo.invoice.impl;

import com.java110.acct.bmo.invoice.IInvoiceAdminBmo;
import com.java110.bean.dto.roleCommunity.RoleCommunityDto;
import com.java110.bean.ResultVo;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.storeStaff.StoreStaffDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.user.IRoleCommunityV1InnerServiceSMO;
import com.java110.intf.user.IStoreStaffV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 发票管理权限校验实现
 * 判定口径与小区列表查询保持一致：系统管理员（u_user.level_cd=00）或商户管理员（s_store_staff.admin_flag=Y）
 * 或已通过角色授权该小区的员工（u_role_community + p_privilege_user）
 */
@Service
public class InvoiceAdminBmoImpl implements IInvoiceAdminBmo {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceAdminBmoImpl.class);

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IStoreStaffV1InnerServiceSMO storeStaffV1InnerServiceSMOImpl;

    @Autowired
    private IRoleCommunityV1InnerServiceSMO roleCommunityV1InnerServiceSMOImpl;

    @Override
    public void validateCommunityAdmin(String storeId, String userId, String communityId) {
        if (StringUtil.isNullOrNone(userId)) {
            throw new CmdException(ResultVo.CODE_BUSINESS_VERIFICATION, "未获取到登录用户信息");
        }
        if (StringUtil.isNullOrNone(communityId)) {
            throw new CmdException(ResultVo.CODE_BUSINESS_VERIFICATION, "请先选择小区");
        }
        if (isSystemAdmin(userId) || isStoreAdmin(storeId, userId) || isCommunityStaff(userId, communityId)) {
            return;
        }
        logger.warn("发票管理越权调用 userId: {}, communityId: {}", userId, communityId);
        throw new CmdException(ResultVo.CODE_BUSINESS_VERIFICATION, "无该小区发票管理权限，请联系小区管理员");
    }

    /**
     * 系统管理员（u_user.level_cd=00）
     */
    private boolean isSystemAdmin(String userId) {
        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        if (ListUtil.isNull(userDtos)) {
            return false;
        }
        return UserDto.LEVEL_CD_ADMIN.equals(userDtos.get(0).getLevelCd());
    }

    /**
     * 商户管理员（s_store_staff.admin_flag=Y）
     */
    private boolean isStoreAdmin(String storeId, String userId) {
        if (StringUtil.isNullOrNone(storeId)) {
            return false;
        }
        StoreStaffDto staffDto = new StoreStaffDto();
        staffDto.setStoreId(storeId);
        staffDto.setStaffId(userId);
        staffDto.setAdminFlag(StoreStaffDto.ADMIN_FLAG_Y);
        return !ListUtil.isNull(storeStaffV1InnerServiceSMOImpl.queryStoreStaffs(staffDto));
    }

    /**
     * 已授权该小区的员工（u_role_community 关联 p_privilege_user）
     */
    private boolean isCommunityStaff(String userId, String communityId) {
        RoleCommunityDto roleCommunityDto = new RoleCommunityDto();
        roleCommunityDto.setStaffId(userId);
        roleCommunityDto.setCommunityId(communityId);
        roleCommunityDto.setStatusCd("0");
        roleCommunityDto.setPage(1);
        roleCommunityDto.setRow(1);
        return roleCommunityV1InnerServiceSMOImpl.queryRoleCommunitysCount(roleCommunityDto) > 0;
    }
}
