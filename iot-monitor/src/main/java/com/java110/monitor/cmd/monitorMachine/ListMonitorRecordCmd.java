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

/**
 * 查询监控录像
 */
@Java110Cmd(serviceCode = "monitorMachine.listMonitorRecord")
public class ListMonitorRecordCmd extends Cmd {

    @Autowired
    private IMonitorMachineV1InnerServiceSMO monitorMachineV1InnerServiceSMOImpl;

    @Autowired
    private IMonitorManufactorV1InnerServiceSMO monitorManufactorV1InnerServiceSMOImpl;


    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        super.validatePageInfo(reqJson);
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区ID");
        Assert.hasKeyAndValue(reqJson, "machineId", "未包含相机ID");

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {


        MonitorMachineDto monitorMachineDto = new MonitorMachineDto();
        monitorMachineDto.setMachineId(reqJson.getString("machineId"));
        monitorMachineDto.setCommunityId(reqJson.getString("communityId"));
        List<MonitorMachineDto> monitorMachineDtos = monitorMachineV1InnerServiceSMOImpl.queryMonitorMachines(monitorMachineDto);
        Assert.listOnlyOne(monitorMachineDtos, "监控相机不存在");


        MonitorManufactorDto monitorManufactorDto = new MonitorManufactorDto();
        monitorManufactorDto.setMmId(monitorMachineDtos.get(0).getMmId());
        List<MonitorManufactorDto> monitorManufactorDtos = monitorManufactorV1InnerServiceSMOImpl.queryMonitorManufactors(monitorManufactorDto);
        Assert.listOnlyOne(monitorManufactorDtos, "监控厂家不存在");


        IMonitorModelAdapt monitorModelAdapt = ApplicationContextFactory.getBean(monitorManufactorDtos.get(0).getMmBean(), IMonitorModelAdapt.class);

        if (monitorModelAdapt == null) {
            throw new CmdException("厂家协议未实现");
        }

        ResultVo resultVo = monitorModelAdapt.getVideoRecords(monitorMachineDtos.get(0), monitorManufactorDtos.get(0),reqJson);

        context.setResponseEntity(ResultVo.createResponseEntity(resultVo));
    }
}
