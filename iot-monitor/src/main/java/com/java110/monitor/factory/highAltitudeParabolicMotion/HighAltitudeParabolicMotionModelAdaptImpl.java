package com.java110.monitor.factory.highAltitudeParabolicMotion;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.dto.monitor.MonitorMachineDto;
import com.java110.dto.monitorManufactor.MonitorManufactorDto;
import com.java110.monitor.factory.IMonitorModelAdapt;
import org.springframework.stereotype.Component;

@Component("highAltitudeParabolicMotionModelAdaptImpl")
public class HighAltitudeParabolicMotionModelAdaptImpl implements IMonitorModelAdapt {
    @Override
    public void queryMonitorMachineState(MonitorMachineDto monitorMachineDto) {
        monitorMachineDto.setIsOnlineState(MonitorMachineDto.STATE_ONLINE);
        monitorMachineDto.setIsOnlineStateName("在线");
    }

    @Override
    public String getVideoUrl(MonitorMachineDto monitorMachineDto, MonitorManufactorDto monitorManufactorDto) {
        return null;
    }

    @Override
    public ResultVo getVideoRecords(MonitorMachineDto monitorMachineDto, MonitorManufactorDto monitorManufactorDto, JSONObject reqJson) {
        return null;
    }

    @Override
    public String getRecordVideoUrl(MonitorMachineDto monitorMachineDto, MonitorManufactorDto monitorManufactorDto, JSONObject reqJson) {
        return null;
    }
}
