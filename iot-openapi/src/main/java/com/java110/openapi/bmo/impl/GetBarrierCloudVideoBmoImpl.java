package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ListUtil;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.monitor.MonitorMachineDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.barrier.IBarrierV1InnerServiceSMO;
import com.java110.intf.monitor.IMonitorMachineV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 获取道闸云端视频
 */
@Service("getBarrierCloudVideoBmoImpl")
public class GetBarrierCloudVideoBmoImpl implements IIotCommonApiBmo {

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IBarrierV1InnerServiceSMO barrierV1InnerServiceSMOImpl;

    @Autowired
    private IMonitorMachineV1InnerServiceSMO monitorMachineV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "machineId", "未包含设备");
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区");


    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        String tel = reqJson.getString("propertyUserTel");

        UserDto userDto = new UserDto();
        userDto.setTel(tel);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);

        if(ListUtil.isNull(userDtos)){
            throw new CmdException("用户未登录");
        }

        BarrierDto barrierDto = new BarrierDto();
        barrierDto.setMachineId(reqJson.getString("machineId"));
        barrierDto.setCommunityId(reqJson.getString("communityId"));
        List<BarrierDto> barrierDtos = barrierV1InnerServiceSMOImpl.queryBarriers(barrierDto);

        Assert.listOnlyOne(barrierDtos, "摄像头不存在");

//        ResultVo resultVo = barrierV1InnerServiceSMOImpl.getCloudFlvVideo(barrierDtos.get(0));
//        context.setResponseEntity(ResultVo.createResponseEntity(resultVo));

        MonitorMachineDto monitorMachineDto = new MonitorMachineDto();
        monitorMachineDto.setMachineId(barrierDtos.get(0).getMonitorId());
        monitorMachineDto.setCommunityId(reqJson.getString("communityId"));
        String url = monitorMachineV1InnerServiceSMOImpl.getPlayVideoUrl(monitorMachineDto);

        JSONObject data = new JSONObject();
        data.put("url",url);
        context.setResponseEntity(ResultVo.createResponseEntity(data));

    }
}
