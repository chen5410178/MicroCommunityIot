package com.java110.openapi.bmo.adminImpl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.owner.OwnerRoomRelDto;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControlInout.AccessControlInoutDto;
import com.java110.dto.community.CommunityDto;
import com.java110.intf.accessControl.IAccessControlInoutV1InnerServiceSMO;
import com.java110.intf.community.ICommunityV1InnerServiceSMO;
import com.java110.intf.user.IOwnerRoomRelV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotAdminApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("listAdminAccessControlInoutBmoImpl")
public class ListAdminAccessControlInoutBmoImpl implements IIotAdminApiBmo {

    @Autowired
    private IAccessControlInoutV1InnerServiceSMO accessControlInoutV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerRoomRelV1InnerServiceSMO ownerRoomRelV1InnerServiceSMOImpl;

    @Autowired
    private ICommunityV1InnerServiceSMO communityV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "page", "未包含page");
        Assert.hasKeyAndValue(reqJson, "row", "未包含row");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        AccessControlInoutDto accessControlInoutDto = BeanConvertUtil.covertBean(reqJson, AccessControlInoutDto.class);
        String communityIdStr = reqJson.getString("communityIdStr");
        if(!StringUtil.isEmpty(communityIdStr)){
            accessControlInoutDto.setCommunityIds(communityIdStr.split(","));
        }
        //todo 如果包含房屋信息
        ifHasRoomId(reqJson, accessControlInoutDto);

        int count = accessControlInoutV1InnerServiceSMOImpl.queryAccessControlInoutsCount(accessControlInoutDto);

        List<AccessControlInoutDto> accessControlInoutDtos = null;

        if (count > 0) {
            accessControlInoutDtos = accessControlInoutV1InnerServiceSMOImpl.queryAccessControlInouts(accessControlInoutDto);
        } else {
            accessControlInoutDtos = new ArrayList<>();
        }

        refreshCommunityName(accessControlInoutDtos);

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, accessControlInoutDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }

    private void refreshCommunityName(List<AccessControlInoutDto> accessControlInoutDtos) {

        if(ListUtil.isNull(accessControlInoutDtos)){
            return;
        }

        List<String> communityIds = new ArrayList<>();
        for (AccessControlInoutDto accessControlInoutDto : accessControlInoutDtos) {
            communityIds.add(accessControlInoutDto.getCommunityId());
        }

        if(ListUtil.isNull(communityIds)){
            return ;
        }
        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityIds(communityIds.toArray(new String[communityIds.size()]));
        List<CommunityDto> communityDtos = communityV1InnerServiceSMOImpl.queryCommunitys(communityDto);
        if(ListUtil.isNull(communityDtos)){
            return;
        }
        for (AccessControlInoutDto accessControlInoutDto : accessControlInoutDtos) {
            for (CommunityDto tCommunityDto : communityDtos) {
                if (!accessControlInoutDto.getCommunityId().equals(tCommunityDto.getCommunityId())) {
                    continue;
                }
                accessControlInoutDto.setCommunityName(tCommunityDto.getName());
            }
        }
    }

    private void ifHasRoomId(JSONObject reqJson, AccessControlInoutDto accessControlInoutDto) {

        if (!reqJson.containsKey("roomId") || StringUtil.isEmpty(reqJson.getString("roomId"))) {
            return;
        }

        //todo 根据房屋ID查询房屋人员信息

        OwnerRoomRelDto ownerRoomRelDto = new OwnerRoomRelDto();
        ownerRoomRelDto.setRoomId(reqJson.getString("roomId"));
        ownerRoomRelDto.setCommunityId(reqJson.getString("communityId"));
        List<OwnerRoomRelDto> ownerRoomRelDtos = ownerRoomRelV1InnerServiceSMOImpl.queryOwnerRoomRels(ownerRoomRelDto);

        if (ListUtil.isNull(ownerRoomRelDtos)) {
            return;
        }

        List<String> tels = new ArrayList<>();
        for (OwnerRoomRelDto tOwnerRoomRelDto : ownerRoomRelDtos) {
            tels.add(tOwnerRoomRelDto.getLink());
        }

        accessControlInoutDto.setTels(tels.toArray(new String[tels.size()]));
    }
}
