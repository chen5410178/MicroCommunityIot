package com.java110.lamp.factory;

import com.java110.bean.ResultVo;
import com.java110.dto.lamp.LampMachineDto;
import com.java110.dto.lift.LiftMachineDto;
import com.java110.dto.lift.LiftMachineReservationDto;

public interface ILampCore {

    /**
     * 切换路灯状态
     * @param lampMachineDto
     * @return
     */
    ResultVo switchLampState(LampMachineDto lampMachineDto);
}
