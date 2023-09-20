package com.java110.lift.factory;

import com.java110.bean.ResultVo;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.Assert;
import com.java110.dto.lift.LiftMachineDto;
import com.java110.dto.lift.LiftMachineFactoryDto;
import com.java110.dto.lift.LiftMachineReservationDto;
import com.java110.intf.lift.ILiftMachineFactoryV1InnerServiceSMO;
import com.java110.intf.lift.ILiftMachineV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LiftCoreImpl implements ILiftCore {

    @Autowired
    private ILiftMachineFactoryV1InnerServiceSMO liftMachineFactoryV1InnerServiceSMOImpl;

    @Autowired
    private ILiftMachineV1InnerServiceSMO liftMachineV1InnerServiceSMOImpl;

    @Override
    public ResultVo switchLiftState(LiftMachineDto liftMachineDto) {
        LiftMachineFactoryDto liftMachineFactoryDto = new LiftMachineFactoryDto();
        liftMachineFactoryDto.setFactoryId(liftMachineDto.getFactoryId());
        List<LiftMachineFactoryDto> liftMachineFactoryDtos = liftMachineFactoryV1InnerServiceSMOImpl.queryLiftMachineFactorys(liftMachineFactoryDto);
        Assert.listOnlyOne(liftMachineFactoryDtos, "电梯厂家不存在");

        ILiftFactoryAdapt liftFactoryAdaptImpl = null;
        try {
            liftFactoryAdaptImpl = ApplicationContextFactory.getBean(liftMachineFactoryDtos.get(0).getBeanImpl(), ILiftFactoryAdapt.class);
        } finally {
            if (liftFactoryAdaptImpl == null) {
                throw new CmdException("厂家接口未实现");
            }
        }

        LiftMachineDto machineDto = liftFactoryAdaptImpl.getLiftCurState(liftMachineDto);
        //todo: 判断电梯当前状态是否可以切换到目标状态

        ResultVo resultVo;
        switch (liftMachineDto.getLogAction()) {
            case "running":
                resultVo = liftFactoryAdaptImpl.startLift(liftMachineDto);
                break;
            case "stopping":
                resultVo = liftFactoryAdaptImpl.stopLift(liftMachineDto);
                break;
            case "unknown":
                resultVo = liftFactoryAdaptImpl.unknownLiftOperate(liftMachineDto);
                break;
            default:
                resultVo = new ResultVo(ResultVo.CODE_ERROR, "未知错误");
        }

        return resultVo;
    }

    @Override
    public ResultVo reservationLiftMachine(LiftMachineReservationDto liftMachineReservationDto) {
        LiftMachineDto liftMachineDto = new LiftMachineDto();
        liftMachineDto.setMachineId(liftMachineReservationDto.getMachineId());
        List<LiftMachineDto> liftMachineDtos = liftMachineV1InnerServiceSMOImpl.queryLiftMachines(liftMachineDto);

        LiftMachineFactoryDto liftMachineFactoryDto = new LiftMachineFactoryDto();
        liftMachineFactoryDto.setFactoryId(liftMachineDtos.get(0).getFactoryId());
        List<LiftMachineFactoryDto> liftMachineFactoryDtos = liftMachineFactoryV1InnerServiceSMOImpl.queryLiftMachineFactorys(liftMachineFactoryDto);
        Assert.listOnlyOne(liftMachineFactoryDtos, "电梯厂家不存在");

        ILiftFactoryAdapt liftFactoryAdaptImpl = null;
        try {
            liftFactoryAdaptImpl = ApplicationContextFactory.getBean(liftMachineFactoryDtos.get(0).getBeanImpl(), ILiftFactoryAdapt.class);
        } finally {
            if (liftFactoryAdaptImpl == null) {
                throw new CmdException("厂家接口未实现");
            }
        }

        return liftFactoryAdaptImpl.reservationLiftMachine(liftMachineReservationDto);
    }
}
