package com.java110.monitor.factory;

import com.java110.core.exception.CmdException;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.Assert;
import com.java110.dto.monitor.MonitorMachineDto;
import com.java110.dto.monitor.MonitorMachineModelDto;
import com.java110.dto.monitor.MonitorModelDto;
import com.java110.intf.monitor.IMonitorMachineModelV1InnerServiceSMO;
import com.java110.intf.monitor.IMonitorModelV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MonitorCoreImpl implements IMonitorCore{
    @Autowired
    private IMonitorMachineModelV1InnerServiceSMO monitorMachineModelV1InnerServiceSMOImpl;

    @Autowired
    private IMonitorModelV1InnerServiceSMO monitorModelV1InnerServiceSMOImpl;

    @Override
    public void queryMonitorMachineState(List<MonitorMachineDto> monitorMachineDtos) {
        for (MonitorMachineDto monitorMachineDto : monitorMachineDtos) {
            try {
                MonitorMachineModelDto monitorMachineModelDto = new MonitorMachineModelDto();
                monitorMachineModelDto.setMachineId(monitorMachineDto.getMachineId());
                List<MonitorMachineModelDto> monitorMachineModelDtos = monitorMachineModelV1InnerServiceSMOImpl.queryMonitorMachineModels(monitorMachineModelDto);
                if (monitorMachineModelDtos == null || monitorMachineModelDtos.size() < 1) {
                    throw new CmdException("监控设备未绑定监控模型");
                }
                for (MonitorMachineModelDto monitorMachineModel : monitorMachineModelDtos) {
                    MonitorModelDto monitorModelDto = new MonitorModelDto();
                    monitorModelDto.setModelId(monitorMachineModel.getModelId());
                    List<MonitorModelDto> monitorModelDtos = monitorModelV1InnerServiceSMOImpl.queryMonitorModels(monitorModelDto);
                    Assert.listOnlyOne(monitorModelDtos, "监控模型不存在");

                    IMonitorModelAdapt monitorModelAdapt = ApplicationContextFactory.getBean(monitorModelDtos.get(0).getModelAdapt(), IMonitorModelAdapt.class);
                    if (monitorModelAdapt == null) {
                        throw new CmdException("厂家接口未实现");
                    }
                    monitorModelAdapt.queryMonitorMachineState(monitorMachineDto);
                }
            } catch (Exception e) {
                e.printStackTrace();
                monitorMachineDto.setIsOnlineState(MonitorMachineDto.STATE_OFFLINE);
                monitorMachineDto.setIsOnlineStateName("离线");
            }
        }
    }
}
