package com.java110.user.cmd.org;

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
import com.java110.intf.community.ICommunityInnerServiceSMO;
import com.java110.intf.user.IRoleCommunityV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@Java110Cmd(serviceCode = "org.listOrgNoCommunitys")
public class ListOrgNoCommunitysCmd extends Cmd {

    @Autowired
    private IRoleCommunityV1InnerServiceSMO roleCommunityV1InnerServiceSMO;

    @Autowired
    private ICommunityInnerServiceSMO communityInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        super.validatePageInfo(reqJson);
        if(!reqJson.containsKey("storeId") || StringUtil.isEmpty(reqJson.getString("storeId"))) {
            String storeId = context.getReqHeaders().get("store-id");
            reqJson.put("storeId",storeId);
        }
        Assert.hasKeyAndValue(reqJson, "storeId", "必填，请填写商户ID");

        Assert.hasKeyAndValue(reqJson, "roleId", "必填，请填写角色");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        RoleCommunityDto roleCommunityDto = BeanConvertUtil.covertBean(reqJson, RoleCommunityDto.class);

        List<RoleCommunityDto> orgCommunityDtos = roleCommunityV1InnerServiceSMO.queryRoleCommunitys(roleCommunityDto);
        List<String> communityIds = new ArrayList<>();
        for(RoleCommunityDto tmpOrgCommunityDto : orgCommunityDtos){
            communityIds.add(tmpOrgCommunityDto.getCommunityId());
        }
        CommunityDto communityDto = BeanConvertUtil.covertBean(reqJson, CommunityDto.class);
        if(communityIds.size()>0) {
            communityDto.setNotInCommunityId(communityIds.toArray(new String[communityIds.size()]));
        }
        communityDto.setAuditStatusCd("1100");
        communityDto.setMemberId(reqJson.getString("storeId"));
        int count = communityInnerServiceSMOImpl.queryCommunitysCount(communityDto);

        List<RoleCommunityDto> communitys = null;

        if (count > 0) {
            communitys = BeanConvertUtil.covertBeanList(communityInnerServiceSMOImpl.queryCommunitys(communityDto), RoleCommunityDto.class);
        } else {
            communitys = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, communitys);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }
}
