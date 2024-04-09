package com.java110.barrier.engine.adapt.huaxiamqtt;

import com.java110.barrier.engine.ICarMachineProcess;
import com.java110.barrier.engine.adapt.BaseMachineAdapt;
import com.java110.bean.ResultVo;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.parking.ParkingAreaTextDto;
import com.java110.po.manualOpenDoorLog.ManualOpenDoorLogPo;

public class HuaxiaMqttCarMachineAdapt extends BaseMachineAdapt implements ICarMachineProcess {

    @Override
    public void restartMachine(BarrierDto machineDto) {

    }

    @Override
    public void openDoor(BarrierDto machineDto, ParkingAreaTextDto parkingAreaTextDto) {

    }

    @Override
    public void closeDoor(BarrierDto machineDto, ParkingAreaTextDto parkingAreaTextDto) {

    }

    @Override
    public void sendKeepAlive(BarrierDto machineDto) {

    }

    @Override
    public void mqttMessageArrived(String taskId, String topic, String s) {

    }

    @Override
    public void manualTrigger(BarrierDto machineDto) {

    }

    @Override
    public void triggerImage(BarrierDto machineDto, ManualOpenDoorLogPo manualOpenDoorLogPo) {

    }

    @Override
    public ResultVo getCloudFlvVideo(BarrierDto barrierDto) {
        return null;
    }
}
