package com.java110.accessControl.cmd.accessControl;

import com.alibaba.fastjson.JSONObject;
import com.java110.accessControl.manufactor.IAccessControlManufactor;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.CmdContextUtils;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.appUser.AppUserDto;
import com.java110.dto.hardwareManufacturer.HardwareManufacturerDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.accessControl.IAccessControlFaceV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.system.IHardwareManufacturerV1InnerServiceSMO;
import com.java110.intf.user.IAppUserV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

@Java110Cmd(serviceCode = "accessControl.ownerOpenAccessControlDoor")
public class OwnerOpenAccessControlDoorCmd extends Cmd {

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlFaceV1InnerServiceSMO accessControlFaceV1InnerServiceSMOImpl;

    @Autowired
    private IHardwareManufacturerV1InnerServiceSMO hardwareManufacturerV1InnerServiceSMOImpl;

    @Autowired
    private IAppUserV1InnerServiceSMO appUserV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "machineId", "未包含设备信息");
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区信息");

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        String userId = CmdContextUtils.getUserId(context);

        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);

        Assert.listOnlyOne(userDtos, "用户未登录");


        AppUserDto appUserDto = new AppUserDto();
        appUserDto.setUserId(userId);
        List<AppUserDto> appUserDtos = appUserV1InnerServiceSMOImpl.queryAppUsers(appUserDto);
        Assert.listOnlyOne(appUserDtos, "房屋未认证");


        AccessControlFaceDto accessControlFaceDto = new AccessControlFaceDto();
        accessControlFaceDto.setPersonId(appUserDtos.get(0).getMemberId());
        accessControlFaceDto.setMachineId(reqJson.getString("machineId"));
        List<AccessControlFaceDto> accessControlFaceDtos = accessControlFaceV1InnerServiceSMOImpl.queryAccessControlFaces(accessControlFaceDto);
        Assert.listOnlyOne(accessControlFaceDtos, "没有权限开门");


        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineId(reqJson.getString("machineId"));
        accessControlDto.setCommunityId(reqJson.getString("communityId"));
        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);

        Assert.listOnlyOne(accessControlDtos, "门禁不存在");
        HardwareManufacturerDto hardwareManufacturerDto = new HardwareManufacturerDto();
        hardwareManufacturerDto.setHmId(accessControlDtos.get(0).getImplBean());
        List<HardwareManufacturerDto> hardwareManufacturerDtos = hardwareManufacturerV1InnerServiceSMOImpl.queryHardwareManufacturers(hardwareManufacturerDto);

        Assert.listOnlyOne(hardwareManufacturerDtos, "协议不存在");

        accessControlDtos.get(0).setUserId(userId);
        accessControlDtos.get(0).setUserName(userDtos.get(0).getName());

        IAccessControlManufactor accessControlManufactor = ApplicationContextFactory.getBean(hardwareManufacturerDtos.get(0).getProtocolImpl(), IAccessControlManufactor.class);
        boolean openFlag = accessControlManufactor.openDoor(accessControlDtos.get(0));

        if (openFlag) {
            context.setResponseEntity(ResultVo.success());
            return;
        }

        context.setResponseEntity(ResultVo.error("开门失败"));


    }
}
