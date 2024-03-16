package com.java110.meter.factory;

import com.java110.bean.ResultVo;
import com.java110.dto.meter.MeterMachineDto;

import java.util.List;

/**
 * 智能电表 适配器
 */
public interface ISmartMeterFactoryAdapt {

    /**
     * 电表充值
     *
     * @param meterMachineDto
     * @param degree
     */
    ResultVo requestRecharge(MeterMachineDto meterMachineDto, double degree, double money);

    /**
     * 电表读数
     *
     * @param meterMachineDto
     * @return
     */
    ResultVo requestRead(MeterMachineDto meterMachineDto);

    /**
     * 多电表读数
     *
     * @param meterMachineDtos
     * @return
     */
    ResultVo requestReads(List<MeterMachineDto> meterMachineDtos);

    /**
     * 电表通知读取数据
     *
     * @param readData
     * @return
     */
    ResultVo notifyReadData(String readData);

    /**
     * 查询设备状态
     *
     * @param meterMachineDto
     */
    void queryMeterMachineState(MeterMachineDto meterMachineDto);

    /**
     * 表开关阀
     *
     * @param meterMachineDto
     */
    ResultVo switchControl(MeterMachineDto meterMachineDto, String controlType);

    /**
     * 表数据清零
     *
     * @param meterMachineDto
     */
    ResultVo cleanControl(MeterMachineDto meterMachineDto);
}
