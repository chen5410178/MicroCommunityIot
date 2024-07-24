package com.java110.barrier.cmd.barrier;

import com.alibaba.fastjson.JSONObject;
import com.java110.barrier.factory.CarMachineProcessFactory;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.CmdContextUtils;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.monitor.MonitorMachineDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.barrier.IBarrierV1InnerServiceSMO;
import com.java110.intf.monitor.IMonitorMachineV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

@Java110Cmd(serviceCode = "barrier.getCloudFlvVideo")
public class GetCloudFlvVideoCmd extends Cmd {

    @Autowired
    private IBarrierV1InnerServiceSMO barrierV1InnerServiceSMOImpl;


    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IMonitorMachineV1InnerServiceSMO monitorMachineV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "machineId", "未包含设备ID");
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区信息");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String userId = CmdContextUtils.getUserId(context);

        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        Assert.listOnlyOne(userDtos, "用户未登录");

        reqJson.put("staffId", userId);
        reqJson.put("staffName", userDtos.get(0).getName());

        BarrierDto barrierDto = new BarrierDto();
        barrierDto.setMachineId(reqJson.getString("machineId"));
        barrierDto.setCommunityId(reqJson.getString("communityId"));
        List<BarrierDto> barrierDtos = barrierV1InnerServiceSMOImpl.queryBarriers(barrierDto);

        Assert.listOnlyOne(barrierDtos, "摄像头不存在");
        //todo 这里不调用臻识云
       // ResultVo resultVo = CarMachineProcessFactory.getCarImpl(barrierDtos.get(0).getHmId()).getCloudFlvVideo(barrierDtos.get(0));

        MonitorMachineDto monitorMachineDto = new MonitorMachineDto();
        monitorMachineDto.setMachineId(barrierDtos.get(0).getMonitorId());
        monitorMachineDto.setCommunityId(reqJson.getString("communityId"));
        String url = monitorMachineV1InnerServiceSMOImpl.getPlayVideoUrl(monitorMachineDto);

        JSONObject data = new JSONObject();
        data.put("url",url);


        context.setResponseEntity(ResultVo.createResponseEntity(data));


    }
}
