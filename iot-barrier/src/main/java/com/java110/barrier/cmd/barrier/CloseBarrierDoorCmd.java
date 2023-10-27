package com.java110.barrier.cmd.barrier;

import com.alibaba.fastjson.JSONObject;
import com.java110.barrier.engine.IBarrierEngine;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.CmdContextUtils;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.barrier.IBarrierV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

/**
 * 道闸关门
 */
@Java110Cmd(serviceCode = "barrier.closeBarrierDoor")
public class CloseBarrierDoorCmd extends Cmd {


    @Autowired
    private IBarrierEngine barrierEngineImpl;

    @Autowired
    private IBarrierV1InnerServiceSMO barrierV1InnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;
    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "machineCode", "未包含设备");
        Assert.hasKeyAndValue(reqJson, "state", "未包含状态");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        String userId = CmdContextUtils.getUserId(context);

        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        Assert.listOnlyOne(userDtos, "用户未登录");

        reqJson.put("staffId",userId);
        reqJson.put("staffName",userDtos.get(0).getName());

        BarrierDto barrierDto = new BarrierDto();
        barrierDto.setMachineCode(reqJson.getString("machineCode"));
        barrierDto.setCommunityId(reqJson.getString("communityId"));
        List<BarrierDto> barrierDtos = barrierV1InnerServiceSMOImpl.queryBarriers(barrierDto);

        Assert.listOnlyOne(barrierDtos, "摄像头不存在");

        ResultVo resultVo = barrierEngineImpl.closeDoor(barrierDtos.get(0),null, reqJson);
        context.setResponseEntity(ResultVo.createResponseEntity(resultVo));
    }
}
