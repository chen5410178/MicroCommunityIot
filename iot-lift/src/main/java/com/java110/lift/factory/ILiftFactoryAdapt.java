package com.java110.lift.factory;

import com.java110.bean.ResultVo;
import com.java110.dto.lift.LiftMachineDto;
import com.java110.dto.lift.LiftMachineReservationDto;

public interface ILiftFactoryAdapt {

    /**
     * 获取电梯状态
     * @param liftMachineDto
     * @return
     */
    LiftMachineDto getLiftCurState(LiftMachineDto liftMachineDto);

    /**
     * 启动电梯
     * @param liftMachineDto
     * @return
     */
    ResultVo startLift(LiftMachineDto liftMachineDto);

    /**
     * 停止电梯
     * @param liftMachineDto
     * @return
     */
    ResultVo stopLift(LiftMachineDto liftMachineDto);

    /**
     * 未知操作
     * @param liftMachineDto
     * @return
     */
    ResultVo unknownLiftOperate(LiftMachineDto liftMachineDto);

    /**
     * 预约电梯
     * @param liftMachineReservationDto
     * @return
     */
    ResultVo reservationLiftMachine(LiftMachineReservationDto liftMachineReservationDto);
}
