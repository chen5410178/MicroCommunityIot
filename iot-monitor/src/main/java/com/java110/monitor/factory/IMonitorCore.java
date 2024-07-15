package com.java110.monitor.factory;

import com.java110.dto.monitor.MonitorMachineDto;

import java.util.List;

public interface IMonitorCore {
    /**
     * 查询设备在线状态
     * @param monitorMachineDtos
     */
    void queryMonitorMachineState(List<MonitorMachineDto> monitorMachineDtos);


}
