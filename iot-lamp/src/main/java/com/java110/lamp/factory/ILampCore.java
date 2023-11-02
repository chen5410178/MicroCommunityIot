package com.java110.lamp.factory;

import com.java110.bean.ResultVo;
import com.java110.dto.lamp.LampMachineDto;

import java.util.List;

public interface ILampCore {

    /**
     * 切换路灯状态
     * @param lampMachineDto
     * @return
     */
    ResultVo switchLampState(LampMachineDto lampMachineDto);

    /**
     * 查询设备在线状态
     * @param lampMachineDtos
     */
    void queryLampMachineState(List<LampMachineDto> lampMachineDtos);
}
