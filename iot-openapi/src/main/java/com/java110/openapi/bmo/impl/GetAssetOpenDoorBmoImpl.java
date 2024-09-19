package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.Assert;
import com.java110.dto.accessControlLog.AccessControlLogDto;
import com.java110.intf.accessControl.IAccessControlLogV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("getAssetOpenDoorBmoImpl")
public class GetAssetOpenDoorBmoImpl implements IIotCommonApiBmo {

    @Autowired
    private IAccessControlLogV1InnerServiceSMO accessControlLogV1InnerServiceSMOImpl;


    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区ID");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {

        AccessControlLogDto accessControlLogDto = new AccessControlLogDto();
        accessControlLogDto.setCommunityId(reqJson.getString("communityId"));
        List<AccessControlLogDto> accessControlLogDtos =
                accessControlLogV1InnerServiceSMOImpl.getAssetOpenDoor(accessControlLogDto);

        context.setResponseEntity(ResultVo.createResponseEntity(accessControlLogDtos));
    }
}
