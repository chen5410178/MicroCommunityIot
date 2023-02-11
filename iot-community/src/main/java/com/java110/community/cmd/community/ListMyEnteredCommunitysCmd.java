package com.java110.community.cmd.community;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.roleCommunity.RoleCommunityDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.community.CommunityDto;
import com.java110.dto.store.StoreDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.community.ICommunityV1InnerServiceSMO;
import com.java110.intf.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@Java110Cmd(serviceCode = "community.listMyEnteredCommunitys")
public class ListMyEnteredCommunitysCmd extends Cmd {

    @Autowired
    private ICommunityV1InnerServiceSMO communityInnerServiceSMOImpl;

    @Autowired
    private IOrgStaffRelInnerServiceSMO orgStaffRelInnerServiceSMOImpl;

    @Autowired
    private IRoleCommunityV1InnerServiceSMO roleCommunityV1InnerServiceSMOImpl;

    @Autowired
    private IOrgInnerServiceSMO orgInnerServiceSMOImpl;

    @Autowired
    private IStoreV1InnerServiceSMO storeV1InnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {

        if (!reqJson.containsKey("storeId") || StringUtil.isEmpty(reqJson.getString("storeId"))) {
            reqJson.put("storeId", cmdDataFlowContext.getReqHeaders().get("store-id"));
        }

        if (!reqJson.containsKey("userId") || StringUtil.isEmpty(reqJson.getString("userId"))) {
            reqJson.put("userId", cmdDataFlowContext.getReqHeaders().get("user-id"));
        }

        Assert.hasKeyAndValue(reqJson, "storeId", "请求报文中未包含商户信息");
        Assert.hasKeyAndValue(reqJson, "userId", "请求报文中未包含用户信息");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {


        // 判断是不是管理员，管理员反馈 物业 的所有小区
        UserDto userDto = new UserDto();
        userDto.setUserId(reqJson.getString("userId"));
        userDto.setPage(1);
        userDto.setRow(1);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);

        Assert.listOnlyOne(userDtos, "用户不存在");

        //校验商户是否存在;
        StoreDto storeDto = new StoreDto();
        storeDto.setStoreId(reqJson.getString("storeId"));
        List<StoreDto> storeDtos = storeV1InnerServiceSMOImpl.queryStores(storeDto);

        Assert.listOnlyOne(storeDtos, "商户不存在");

        int count = 0;
        List<CommunityDto> communitys = null;
        if (UserDto.LEVEL_CD_ADMIN.equals(userDtos.get(0).getLevelCd())) {
            CommunityDto communityDto = BeanConvertUtil.covertBean(reqJson, CommunityDto.class);
            communityDto.setMemberId(reqJson.getString("storeId"));
            if (reqJson.containsKey("communityName")) {
                communityDto.setName(reqJson.getString("communityName"));
            }
            count = communityInnerServiceSMOImpl.queryCommunitysCount(communityDto);
            if (count > 0) {
                communitys = communityInnerServiceSMOImpl.queryCommunitys(communityDto);
            } else {
                communitys = new ArrayList<>();
            }
        } else {
            RoleCommunityDto orgCommunityDto = BeanConvertUtil.covertBean(reqJson, RoleCommunityDto.class);
            orgCommunityDto.setStaffId(userDtos.get(0).getUserId());
            count = roleCommunityV1InnerServiceSMOImpl.queryRoleCommunitysNameCount(orgCommunityDto);
            if (count > 0) {
                List<RoleCommunityDto> roleCommunityDtos = roleCommunityV1InnerServiceSMOImpl.queryRoleCommunitysName(orgCommunityDto);
                communitys = BeanConvertUtil.covertBeanList(roleCommunityDtos, CommunityDto.class);
                for (RoleCommunityDto tmpOrgCommunityDto : roleCommunityDtos) {
                    for (CommunityDto tmpApiCommunityDataVo : communitys) {
                        if (tmpOrgCommunityDto.getCommunityId().equals(tmpApiCommunityDataVo.getCommunityId())) {
                            tmpApiCommunityDataVo.setName(tmpOrgCommunityDto.getCommunityName());
                        }
                    }
                }
            } else {
                communitys = new ArrayList<>();
            }

        }
        //兼容 系统刚开始没有小区时
        if (communitys.size() < 1 && (StoreDto.STORE_TYPE_ADMIN.equals(storeDtos.get(0).getStoreTypeCd()) || StoreDto.STORE_TYPE_DEV.equals(storeDtos.get(0).getStoreTypeCd()))) {
            CommunityDto apiCommunityDataVo = new CommunityDto();
            apiCommunityDataVo.setCommunityId("-1");
            apiCommunityDataVo.setName("默认小区");
            apiCommunityDataVo.setTel("18909711234");
            communitys.add(apiCommunityDataVo);
        }
        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, communitys);
        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);
        context.setResponseEntity(responseEntity);
    }
}
