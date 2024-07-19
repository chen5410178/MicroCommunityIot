package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.Assert;
import com.java110.dto.monitor.MonitorMachineDto;
import com.java110.intf.monitor.IMonitorMachineV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("getPlayVideoUrlBmoImpl")
public class GetPlayVideoUrlBmoImpl implements IIotCommonApiBmo {

    @Autowired
    private IMonitorMachineV1InnerServiceSMO monitorMachineV1InnerServiceSMOImpl;
    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区ID");
        Assert.hasKeyAndValue(reqJson, "machineId", "未包含设备ID");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        MonitorMachineDto monitorMachineDto = new MonitorMachineDto();
        monitorMachineDto.setMachineId(reqJson.getString("machineId"));
        monitorMachineDto.setCommunityId(reqJson.getString("communityId"));
        String url = monitorMachineV1InnerServiceSMOImpl.getPlayVideoUrl(monitorMachineDto);


        context.setResponseEntity(ResultVo.createResponseEntity(url));
    }
}
