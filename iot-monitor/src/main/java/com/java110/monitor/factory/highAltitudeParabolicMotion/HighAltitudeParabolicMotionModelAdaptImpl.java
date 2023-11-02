package com.java110.monitor.factory.highAltitudeParabolicMotion;

import com.java110.dto.monitor.MonitorMachineDto;
import com.java110.monitor.factory.IMonitorModelAdapt;
import org.springframework.stereotype.Component;

@Component("highAltitudeParabolicMotionModelAdaptImpl")
public class HighAltitudeParabolicMotionModelAdaptImpl implements IMonitorModelAdapt {
    @Override
    public void queryMonitorMachineState(MonitorMachineDto monitorMachineDto) {
        monitorMachineDto.setIsOnlineState(MonitorMachineDto.STATE_ONLINE);
        monitorMachineDto.setIsOnlineStateName("在线");
    }
}
