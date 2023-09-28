package com.java110.barrier.engine;

import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.manualOpenDoorLog.ManualOpenDoorLogDto;
import com.java110.dto.parking.ParkingAreaTextDto;

/**
 * 道闸对接设备 适配器类
 * add by wuxw
 * <p>
 * 2020-05-11
 */
public interface ICarMachineProcess {

    /**
     * 初始化方法
     */
    void initCar();

    /**
     * 初始化设备
     */
    void initCar(BarrierDto machineDto);


    /**
     * 设备同步的字节信息
     *
     * @param machineDto 设备信息
     * @param bytes      字节信息
     */
    void readByte(BarrierDto machineDto, byte[] bytes) throws Exception;


    /**
     * 重启设备
     *
     * @param machineDto 硬件信息
     */
    void restartMachine(BarrierDto machineDto);

    /**
     * 道闸开门
     *
     * @param machineDto 硬件信息
     */
    void openDoor(BarrierDto machineDto, ParkingAreaTextDto parkingAreaTextDto);
    /**
     * 道闸开门
     *
     * @param machineDto 硬件信息
     */
    void closeDoor(BarrierDto machineDto, ParkingAreaTextDto parkingAreaTextDto);

    void sendKeepAlive(BarrierDto machineDto);


    void mqttMessageArrived(String taskId,String topic, String s);

    /**
     * 手动触发 识别
     * @param machineDto
     */
    void manualTrigger(BarrierDto machineDto);

    /**
     * 摄像头抓拍
     * @param manualOpenDoorLogDto
     */
    void triggerImage(BarrierDto machineDto, ManualOpenDoorLogDto manualOpenDoorLogDto);
}
