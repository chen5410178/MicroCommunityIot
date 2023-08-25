package com.java110.meter.factory;

import com.java110.dto.meter.MeterMachineDetailDto;
import com.java110.dto.meter.MeterMachineDto;

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
}
