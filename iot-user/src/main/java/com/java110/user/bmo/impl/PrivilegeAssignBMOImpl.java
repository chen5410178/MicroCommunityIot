package com.java110.user.bmo.impl;

import com.alibaba.fastjson.JSONArray;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.privilegeUser.PrivilegeUserDto;
import com.java110.dto.storeStaff.StoreStaffDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.user.IPrivilegeUserV1InnerServiceSMO;
import com.java110.intf.user.IStoreStaffV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.user.bmo.IPrivilegeAssignBMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service(value = "privilegeAssignBMOImpl")
public class PrivilegeAssignBMOImpl implements IPrivilegeAssignBMO {

    @Autowired
    private IStoreStaffV1InnerServiceSMO storeStaffV1InnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IPrivilegeUserV1InnerServiceSMO privilegeUserV1InnerServiceSMOImpl;

    @Override
    public boolean isAdmin(String storeId, String userId) {
        if (StringUtil.isNullOrNone(userId)) {
            return false;
        }
        StoreStaffDto staffDto = new StoreStaffDto();
        staffDto.setStoreId(storeId);
        staffDto.setStaffId(userId);
        staffDto.setAdminFlag(StoreStaffDto.ADMIN_FLAG_Y);
        if (!ListUtil.isNull(storeStaffV1InnerServiceSMOImpl.queryStoreStaffs(staffDto))) {
            return true;
        }

        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        if (ListUtil.isNull(userDtos)) {
            return false;
        }
        return UserDto.LEVEL_CD_ADMIN.equals(userDtos.get(0).getLevelCd());
    }

    @Override
    public Set<String> queryOwnPrivilegeIds(String userId, String domain) {
        Set<String> ownPIds = new HashSet<>();
        if (StringUtil.isNullOrNone(userId)) {
            return ownPIds;
        }
        PrivilegeUserDto privilegeUserDto = new PrivilegeUserDto();
        privilegeUserDto.setUserId(userId);
        privilegeUserDto.setDomain(domain);
        List<PrivilegeUserDto> ownDtos = privilegeUserV1InnerServiceSMOImpl.getUserPrivileges(privilegeUserDto);
        if (ListUtil.isNull(ownDtos)) {
            return ownPIds;
        }
        for (PrivilegeUserDto dto : ownDtos) {
            if (!StringUtil.isNullOrNone(dto.getpId())) {
                ownPIds.add(dto.getpId());
            }
        }
        return ownPIds;
    }

    @Override
    public void filterAssignable(JSONArray privileges, String storeId, String userId, String domain) {
        if (privileges == null || privileges.size() < 1) {
            return;
        }
        if (isAdmin(storeId, userId)) {
            return;
        }
        Set<String> ownPIds = queryOwnPrivilegeIds(userId, domain);
        for (int i = privileges.size() - 1; i >= 0; i--) {
            if (!ownPIds.contains(privileges.getJSONObject(i).getString("pId"))) {
                privileges.remove(i);
            }
        }
    }

    @Override
    public void validateAssignable(String storeId, String userId, String domain, List<String> pIds) {
        if (pIds == null || pIds.isEmpty()) {
            return;
        }
        if (isAdmin(storeId, userId)) {
            return;
        }
        Set<String> ownPIds = queryOwnPrivilegeIds(userId, domain);
        for (String pId : pIds) {
            if (!ownPIds.contains(pId)) {
                throw new CmdException("只能分配自己拥有的权限");
            }
        }
    }
}
