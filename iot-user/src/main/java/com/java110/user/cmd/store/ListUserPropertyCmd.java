package com.java110.user.cmd.store;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.CmdContextUtils;
import com.java110.core.utils.ListUtil;
import com.java110.dto.appUser.AppUserDto;
import com.java110.dto.communityMember.CommunityMemberDto;
import com.java110.dto.store.StoreDto;
import com.java110.intf.community.ICommunityMemberV1InnerServiceSMO;
import com.java110.intf.user.IAppUserV1InnerServiceSMO;
import com.java110.intf.user.IStoreV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

@Java110Cmd(serviceCode = "store.listUserProperty")
public class ListUserPropertyCmd extends Cmd {

    @Autowired
    private IAppUserV1InnerServiceSMO appUserV1InnerServiceSMOImpl;

    @Autowired
    private IStoreV1InnerServiceSMO storeV1InnerServiceSMOImpl;

    @Autowired
    private ICommunityMemberV1InnerServiceSMO communityMemberV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson,"communityId","未包含小区信息");
        String userId = CmdContextUtils.getUserId(context);


        AppUserDto appUserDto = new AppUserDto();
        appUserDto.setUserId(userId);
        appUserDto.setCommunityId(reqJson.getString("communityId"));
        List<AppUserDto> appUserDtos = appUserV1InnerServiceSMOImpl.queryAppUsers(appUserDto);

        if (ListUtil.isNull(appUserDtos)) {
            throw new CmdException("业主未认证");
        }


    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        CommunityMemberDto communityMemberDto = new CommunityMemberDto();
        communityMemberDto.setCommunityId(reqJson.getString("communityId"));
        List<CommunityMemberDto> communityMemberDtos = communityMemberV1InnerServiceSMOImpl.queryCommunityMembers(communityMemberDto);

        if(ListUtil.isNull(communityMemberDtos)){
            throw new CmdException("小区不存在");
        }

        StoreDto storeDto = new StoreDto();
        storeDto.setStoreId(communityMemberDtos.get(0).getMemberId());

        List<StoreDto> storeDtos = storeV1InnerServiceSMOImpl.queryStores(storeDto);

        context.setResponseEntity(ResultVo.createResponseEntity(storeDtos));

    }
}
