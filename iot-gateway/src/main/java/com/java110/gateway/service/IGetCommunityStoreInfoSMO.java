package com.java110.gateway.service;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.dto.communityMember.CommunityMemberDto;
import com.java110.dto.privilegeUser.PrivilegeUserDto;
import com.java110.dto.storeStaff.StoreStaffDto;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public interface IGetCommunityStoreInfoSMO {

    StoreStaffDto getStoreInfo(String userId);

    List<CommunityMemberDto> getStoreEnterCommunitys(String storeId, String storeTypeCd);

    /**
     * 查询用户权限
     * @param storeId
     * @param storeTypeCd
     * @return
     */
    List<PrivilegeUserDto> getUserPrivileges(String storeId, String storeTypeCd);


    List<Map> checkUserHasResourceListener(JSONObject paramIn, String cacheKey);
}
