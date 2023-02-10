package com.java110.gateway.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.basePrivilege.BasePrivilegeDto;
import com.java110.core.configuration.Java110RedisConfig;
import com.java110.core.utils.Assert;
import com.java110.dto.communityMember.CommunityMemberDto;
import com.java110.dto.privilegeUser.PrivilegeUserDto;
import com.java110.dto.storeStaff.StoreStaffDto;
import com.java110.gateway.service.IGetCommunityStoreInfoSMO;
import com.java110.intf.community.ICommunityMemberV1InnerServiceSMO;
import com.java110.intf.user.IMenuCatalogV1InnerServiceSMO;
import com.java110.intf.user.IMenuV1InnerServiceSMO;
import com.java110.intf.user.IPrivilegeUserV1InnerServiceSMO;
import com.java110.intf.user.IStoreStaffV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GetCommunityStoreInfoSMOImpl implements IGetCommunityStoreInfoSMO {

    private static final String BASE_PRIVILEGE = "JAVA110_BASE_PRIVILEGE";

    @Autowired
    private IStoreStaffV1InnerServiceSMO storeStaffV1InnerServiceSMOImpl;

    @Autowired
    private ICommunityMemberV1InnerServiceSMO communityMemberV1InnerServiceSMOImpl;

    @Autowired
    private IPrivilegeUserV1InnerServiceSMO privilegeUserV1InnerServiceSMOImpl;

    @Autowired
    private IMenuV1InnerServiceSMO menuV1InnerServiceSMOImpl;

    @Override
    @Cacheable(value = "getStoreInfo" + Java110RedisConfig.GET_STORE_INFO_EXPIRE_TIME_KEY, key = "#userId")
    public StoreStaffDto getStoreInfo(String userId) {

        Assert.hasLength(userId, "用户未登录请先登录");
        StoreStaffDto storeStaffDto = new StoreStaffDto();
        storeStaffDto.setStaffId(userId);
        List<StoreStaffDto> storeStaffDtos = storeStaffV1InnerServiceSMOImpl.queryStoreStaffs(storeStaffDto);

        if (storeStaffDtos == null || storeStaffDtos.size() < 1) {
            throw new IllegalArgumentException("员工未包含商户信息");
        }

        return storeStaffDtos.get(0);
    }

    @Override
    @Cacheable(value = "getStoreEnterCommunitys" + Java110RedisConfig.GET_STORE_ENTER_COMMUNITYS_EXPIRE_TIME_KEY, key = "#storeId")
    public List<CommunityMemberDto> getStoreEnterCommunitys( String storeId, String storeTypeCd) {
        ResponseEntity<String> responseEntity = null;
//        responseEntity = CallApiServiceFactory.callCenterService(restTemplate, pd, "",
//                "query.myCommunity.byMember?memberId=" + storeId + "&memberTypeCd="
//                        + MappingCache.getValue(MappingConstant.DOMAIN_STORE_TYPE_2_COMMUNITY_MEMBER_TYPE, storeTypeCd), HttpMethod.GET);

        CommunityMemberDto communityMemberDto = new CommunityMemberDto();
        communityMemberDto.setMemberId(storeId);
        communityMemberDto.setMemberTypeCd(storeTypeCd);
        List<CommunityMemberDto> communityMemberDtos = communityMemberV1InnerServiceSMOImpl.queryCommunityMembers(communityMemberDto);
        if(communityMemberDtos == null || communityMemberDtos.size()< 1){
            throw new IllegalArgumentException("商户未包含小区");
        }
        return communityMemberDtos;
    }

    @Override
    @Cacheable(value = "getUserPrivileges" + Java110RedisConfig.DEFAULT_EXPIRE_TIME_KEY, key = "#staffId")
    public List<PrivilegeUserDto> getUserPrivileges( String staffId, String storeTypeCd) {

        PrivilegeUserDto privilegeUserDto = new PrivilegeUserDto();
        privilegeUserDto.setUserId(staffId);
        privilegeUserDto.setDomain(storeTypeCd);
        List<PrivilegeUserDto> privilegeUserDtos = privilegeUserV1InnerServiceSMOImpl.getUserPrivileges(privilegeUserDto);
        return privilegeUserDtos;


    }

    @Cacheable(value = "checkUserHasResourceListener" + Java110RedisConfig.DEFAULT_EXPIRE_TIME_KEY, key = "#cacheKey")
    public List<Map> checkUserHasResourceListener( JSONObject paramIn, String cacheKey) {
        ResponseEntity<String> responseEntity = null;
        BasePrivilegeDto basePrivilegeDto = new BasePrivilegeDto();
        basePrivilegeDto.setResource(paramIn.getString("resource"));
        basePrivilegeDto.setUserId(paramIn.getString("userId"));
        List<Map> privileges = menuV1InnerServiceSMOImpl.checkUserHasResource(basePrivilegeDto);

        return privileges;

    }
}
