package com.java110.lamp.factory;

import com.java110.bean.ResultVo;
import com.java110.dto.lamp.LampMachineDto;


public interface ILampFactoryAdapt {

    /**
     * 获取路灯状态
     * @param lampMachineDto
     * @return
     */
    LampMachineDto getLampCurState(LampMachineDto lampMachineDto);

    /**
     * 开灯
     * @param lampMachineDto
     * @return
     */
    ResultVo startLamp(LampMachineDto lampMachineDto);

    /**
     * 关灯
     * @param lampMachineDto
     * @return
     */
    ResultVo stopLamp(LampMachineDto lampMachineDto);

    /**
     * 未知操作
     * @param lampMachineDto
     * @return
     */
    ResultVo unknownLampOperate(LampMachineDto lampMachineDto);

    /**
     * 查询设备在线状态
     * @param lampMachineDto
     */
    void queryLampMachineState(LampMachineDto lampMachineDto);
}
