package com.java110.monitor.cmd.monitorMachine;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.Assert;
import com.java110.dto.monitor.MonitorMachineDto;
import com.java110.dto.monitorManufactor.MonitorManufactorDto;
import com.java110.intf.monitor.IMonitorMachineV1InnerServiceSMO;
import com.java110.intf.monitor.IMonitorManufactorV1InnerServiceSMO;
import com.java110.monitor.factory.IMonitorModelAdapt;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

@Java110Cmd(serviceCode = "monitorMachine.getPlayVideoUrl")
public class GetPlayVideoUrlCmd extends Cmd {

    @Autowired
    private IMonitorMachineV1InnerServiceSMO monitorMachineV1InnerServiceSMOImpl;



    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区ID");
        Assert.hasKeyAndValue(reqJson, "machineId", "未包含设备ID");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {


        MonitorMachineDto monitorMachineDto = new MonitorMachineDto();
        monitorMachineDto.setMachineId(reqJson.getString("machineId"));
        monitorMachineDto.setCommunityId(reqJson.getString("communityId"));
        String url = monitorMachineV1InnerServiceSMOImpl.getPlayVideoUrl(monitorMachineDto);


        context.setResponseEntity(ResultVo.createResponseEntity(url));


    }
}
