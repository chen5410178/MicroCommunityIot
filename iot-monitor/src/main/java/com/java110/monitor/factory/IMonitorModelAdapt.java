package com.java110.monitor.factory;

import com.java110.dto.monitor.MonitorMachineDto;

public interface IMonitorModelAdapt {
    /**
     * 查询设备在线状态
     * @param monitorMachineDto
     */
    void queryMonitorMachineState(MonitorMachineDto monitorMachineDto);
}
