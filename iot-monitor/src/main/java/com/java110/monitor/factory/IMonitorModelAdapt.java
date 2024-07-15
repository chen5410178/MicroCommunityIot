package com.java110.monitor.factory;

import com.java110.dto.monitor.MonitorMachineDto;
import com.java110.dto.monitorManufactor.MonitorManufactorDto;

public interface IMonitorModelAdapt {
    /**
     * 查询设备在线状态
     *
     * @param monitorMachineDto
     */
    void queryMonitorMachineState(MonitorMachineDto monitorMachineDto);

    /**
     * 获取视频播放地址
     *
     * @param monitorMachineDto
     * @return
     */
    String getVideoUrl(MonitorMachineDto monitorMachineDto, MonitorManufactorDto monitorManufactorDto);
}
