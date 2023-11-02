package com.java110.lift.factory;

import com.java110.bean.ResultVo;
import com.java110.dto.lift.LiftMachineDto;
import com.java110.dto.lift.LiftMachineReservationDto;

import java.util.List;

public interface ILiftCore {

    /**
     * 切换电梯状态
     * @param liftMachineDto
     * @return
     */
    ResultVo switchLiftState(LiftMachineDto liftMachineDto);

    /**
     * 预约电梯
     * @param liftMachineReservationDto
     * @return
     */
    ResultVo reservationLiftMachine(LiftMachineReservationDto liftMachineReservationDto);

    /**
     * 查询设备在线状态
     * @param liftMachineDtos
     */
    void queryLiftMachineState(List<LiftMachineDto> liftMachineDtos);
}
