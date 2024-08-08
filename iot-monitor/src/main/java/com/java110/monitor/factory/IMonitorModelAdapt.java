package com.java110.monitor.factory;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
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

    /**
     * 查询监控录像
     * @param monitorMachineDto
     * @param monitorManufactorDto
     * @return
     */
    ResultVo getVideoRecords(MonitorMachineDto monitorMachineDto, MonitorManufactorDto monitorManufactorDto, JSONObject reqJson);

    /**
     * 查询录像播放地址
     * @param monitorMachineDto
     * @param monitorManufactorDto
     * @param reqJson
     * @return
     */
    String getRecordVideoUrl(MonitorMachineDto monitorMachineDto, MonitorManufactorDto monitorManufactorDto, JSONObject reqJson);
}
