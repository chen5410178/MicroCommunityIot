package com.java110.barrier.engine;

import com.java110.bean.ResultVo;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.manualOpenDoorLog.ManualOpenDoorLogDto;
import com.java110.dto.parking.ParkingAreaTextDto;
import com.java110.po.manualOpenDoorLog.ManualOpenDoorLogPo;

/**
 * 道闸对接设备 适配器类
 * add by wuxw
 * <p>
 * 2020-05-11
 */
public interface ICarMachineProcess {





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

    void mqttMessageArrived(String taskId,String topic, String s);

    /**
     * 手动触发 识别
     * @param machineDto
     */
    void manualTrigger(BarrierDto machineDto);

    /**
     * 摄像头抓拍
     * @param manualOpenDoorLogPo
     */
    void triggerImage(BarrierDto machineDto, ManualOpenDoorLogPo manualOpenDoorLogPo);

    /**
     * 获取视频播放地址
     * @param barrierDto
     * @return
     */
    ResultVo getCloudFlvVideo(BarrierDto barrierDto);

    /**
     * 获取设备远程管理网页URL
     * @param barrierDto 设备信息
     * @return ResultVo，data中包含 url 字段
     */
    ResultVo getDeviceWebUrl(BarrierDto barrierDto);
}
