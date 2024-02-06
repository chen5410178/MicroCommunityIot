package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.Assert;
import com.java110.core.utils.CmdContextUtils;
import com.java110.dto.user.UserDto;
import com.java110.intf.barrier.IBarrierV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 道闸开门
 */
@Service("openBarrierDoorBmoImpl")
public class OpenBarrierDoorBmoImpl implements IIotCommonApiBmo {

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IBarrierV1InnerServiceSMO barrierV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "machineCode", "未包含设备");
        Assert.hasKeyAndValue(reqJson, "state", "未包含状态");
        Assert.hasKeyAndValue(reqJson, "propertyUserTel", "未包含员工账号");


    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        String tel = reqJson.getString("propertyUserTel");

        UserDto userDto = new UserDto();
        userDto.setTel(tel);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        Assert.listOnlyOne(userDtos, "用户未登录");

        reqJson.put("staffId", userDtos.get(0).getUserId());
        reqJson.put("staffName", userDtos.get(0).getName());

        ResultVo resultVo = barrierV1InnerServiceSMOImpl.openDoor(reqJson);
        context.setResponseEntity(ResultVo.createResponseEntity(resultVo));
    }
}
