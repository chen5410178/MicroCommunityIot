package com.java110.monitor.factory.illegalParking;

import com.java110.dto.monitor.MonitorMachineDto;
import com.java110.dto.monitorManufactor.MonitorManufactorDto;
import com.java110.monitor.factory.IMonitorModelAdapt;
import org.springframework.stereotype.Component;

@Component("illegalParkingModelAdaptImpl")
public class IllegalParkingModelAdaptImpl implements IMonitorModelAdapt {
    @Override
    public void queryMonitorMachineState(MonitorMachineDto monitorMachineDto) {
        monitorMachineDto.setIsOnlineState(MonitorMachineDto.STATE_ONLINE);
        monitorMachineDto.setIsOnlineStateName("在线");
    }

    @Override
    public String getVideoUrl(MonitorMachineDto monitorMachineDto, MonitorManufactorDto monitorManufactorDto) {
        return null;
    }
}
