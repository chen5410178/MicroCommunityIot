package com.java110.lamp.factory;

import com.java110.bean.ResultVo;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.Assert;
import com.java110.dto.lamp.LampMachineDto;
import com.java110.dto.lamp.LampMachineFactoryDto;
import com.java110.intf.lamp.ILampMachineFactoryV1InnerServiceSMO;
import com.java110.intf.lamp.ILampMachineV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LampCoreImpl implements ILampCore {

    @Autowired
    private ILampMachineFactoryV1InnerServiceSMO lampMachineFactoryV1InnerServiceSMOImpl;

    @Autowired
    private ILampMachineV1InnerServiceSMO lampMachineV1InnerServiceSMOImpl;

    @Override
    public ResultVo switchLampState(LampMachineDto lampMachineDto) {
        LampMachineFactoryDto lampMachineFactoryDto = new LampMachineFactoryDto();
        lampMachineFactoryDto.setFactoryId(lampMachineDto.getFactoryId());
        List<LampMachineFactoryDto> lampMachineFactoryDtos = lampMachineFactoryV1InnerServiceSMOImpl.queryLampMachineFactorys(lampMachineFactoryDto);
        Assert.listOnlyOne(lampMachineFactoryDtos, "路灯厂家不存在");

        ILampFactoryAdapt lampFactoryAdaptImpl = null;
        try {
            lampFactoryAdaptImpl = ApplicationContextFactory.getBean(lampMachineFactoryDtos.get(0).getBeanImpl(), ILampFactoryAdapt.class);
        } finally {
            if (lampFactoryAdaptImpl == null) {
                throw new CmdException("厂家接口未实现");
            }
        }

        LampMachineDto machineDto = lampFactoryAdaptImpl.getLampCurState(lampMachineDto);
        //todo: 判断路灯当前状态是否可以切换到目标状态

        ResultVo resultVo;
        switch (lampMachineDto.getLogAction()) {
            case "open":
                resultVo = lampFactoryAdaptImpl.startLamp(lampMachineDto);
                break;
            case "close":
                resultVo = lampFactoryAdaptImpl.stopLamp(lampMachineDto);
                break;
            case "unknown":
                resultVo = lampFactoryAdaptImpl.unknownLampOperate(lampMachineDto);
                break;
            default:
                resultVo = new ResultVo(ResultVo.CODE_ERROR, "未知错误");
        }

        return resultVo;
    }

    @Override
    public void queryLampMachineState(List<LampMachineDto> lampMachineDtos) {
        for (LampMachineDto lampMachineDto : lampMachineDtos) {
            try {
                LampMachineFactoryDto lampMachineFactoryDto = new LampMachineFactoryDto();
                lampMachineFactoryDto.setFactoryId(lampMachineDto.getFactoryId());
                List<LampMachineFactoryDto> lampMachineFactoryDtos = lampMachineFactoryV1InnerServiceSMOImpl.queryLampMachineFactorys(lampMachineFactoryDto);

                Assert.listOnlyOne(lampMachineFactoryDtos, "路灯厂家不存在");

                ILampFactoryAdapt lampFactoryAdapt = ApplicationContextFactory.getBean(lampMachineFactoryDtos.get(0).getBeanImpl(), ILampFactoryAdapt.class);
                if (lampFactoryAdapt == null) {
                    throw new CmdException("厂家接口未实现");
                }
                lampFactoryAdapt.queryLampMachineState(lampMachineDto);
            } catch (Exception e) {
                e.printStackTrace();
                lampMachineDto.setIsOnlineState(LampMachineDto.STATE_OFFLINE);
                lampMachineDto.setIsOnlineStateName("离线");
            }
        }
    }
}
