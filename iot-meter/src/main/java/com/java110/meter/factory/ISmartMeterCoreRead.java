package com.java110.meter.factory;

import com.java110.bean.ResultVo;
import com.java110.dto.chargeMachine.ChargeMachineDto;
import com.java110.dto.meter.MeterMachineDetailDto;
import com.java110.dto.meter.MeterMachineDto;

import java.util.List;

/**
 * 水电抄表逻辑
 */
public interface ISmartMeterCoreRead {

    /**
     * 抄表
     */
    void saveMeterAndCreateFee(MeterMachineDetailDto meterMachineDetailDto, String degree, String batchId);

    String generatorBatch(String communityId);

    /**
     * 查询 表读数
     * @param meterMachineDto
     * @return
     */
    double getMeterDegree(MeterMachineDto meterMachineDto);

    /**
     * 查询设备状态
     * @param meterMachineDtos
     */
    void queryMeterMachineState(List<MeterMachineDto> meterMachineDtos);

    /**
     * 表开关阀
     * @param meterMachineDto
     * @return
     */
    ResultVo switchControl(MeterMachineDto meterMachineDto, String controlType);
}
