package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.CmdContextUtils;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.hardwareManufacturer.HardwareManufacturerDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.system.IHardwareManufacturerV1InnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("openAccessControlDoorBmoImpl")
public class OpenAccessControlDoorBmoImpl implements IIotCommonApiBmo {

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Autowired
    private IHardwareManufacturerV1InnerServiceSMO hardwareManufacturerV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "machineCode", "未包含设备信息");
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区信息");
        Assert.hasKeyAndValue(reqJson, "memberId", "未包含员工账号");

    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {

        String memberId = reqJson.getString("memberId");

        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setMemberId(memberId);
        List<OwnerDto> ownerDtos = ownerV1InnerServiceSMOImpl.queryOwners(ownerDto);

        Assert.listOnlyOne(ownerDtos, "用户不存在");


        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineCode(reqJson.getString("machineCode"));
        accessControlDto.setCommunityId(reqJson.getString("communityId"));
        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);

        Assert.listOnlyOne(accessControlDtos, "门禁不存在");

        accessControlDtos.get(0).setUserId(memberId);
        accessControlDtos.get(0).setUserName(ownerDtos.get(0).getName());
        accessControlDtos.get(0).setTel(ownerDtos.get(0).getLink());


        int openFlag = accessControlV1InnerServiceSMOImpl.openDoor(accessControlDtos.get(0));


        if (openFlag > 0) {
            context.setResponseEntity(ResultVo.success());
            return;
        }

        context.setResponseEntity(ResultVo.error("开门失败"));
    }
}
