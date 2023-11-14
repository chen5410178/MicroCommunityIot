package com.java110.lock.factory;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.Assert;
import com.java110.dto.lock.LockMachineDto;
import com.java110.dto.lock.LockMachineFactoryDto;
import com.java110.dto.lock.LockMachineParamDto;
import com.java110.intf.lock.ILockMachineFactoryV1InnerServiceSMO;
import com.java110.intf.lock.ILockMachineParamV1InnerServiceSMO;
import com.java110.intf.lock.ILockMachineV1InnerServiceSMO;
import com.java110.po.lock.LockMachinePo;
import com.java110.po.lock.LockPersonPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LockCoreImpl implements ILockCore{

    @Autowired
    private ILockMachineFactoryV1InnerServiceSMO lockMachineFactoryV1InnerServiceSMOImpl;

    @Autowired
    private ILockMachineV1InnerServiceSMO lockMachineV1InnerServiceSMOImpl;

    @Autowired
    private ILockMachineParamV1InnerServiceSMO lockMachineParamV1InnerServiceSMOImpl;

    @Override
    public void unlock(LockMachinePo lockMachinePo) {
        LockMachineDto lockMachineDto = new LockMachineDto();
        lockMachineDto.setMachineId(lockMachinePo.getMachineId());
        List<LockMachineDto> lockMachineDtos = lockMachineV1InnerServiceSMOImpl.queryLockMachines(lockMachineDto);
        Assert.listOnlyOne(lockMachineDtos, "门锁设备不存在");

        LockMachineFactoryDto lockMachineFactoryDto = new LockMachineFactoryDto();
        lockMachineFactoryDto.setFactoryId(lockMachineDtos.get(0).getImplBean());
        List<LockMachineFactoryDto> lockMachineFactoryDtos = lockMachineFactoryV1InnerServiceSMOImpl.queryLockMachineFactorys(lockMachineFactoryDto);
        Assert.listOnlyOne(lockMachineFactoryDtos, "门锁厂家不存在");

        ILockFactoryAdapt lockFactoryAdapt = null;
        try{
            lockFactoryAdapt = ApplicationContextFactory.getBean(lockMachineFactoryDtos.get(0).getBeanImpl(), ILockFactoryAdapt.class);
        } finally {
            if (lockFactoryAdapt == null) {
                throw new CmdException("厂家接口未实现");
            }
        }

        LockMachineParamDto lockMachineParamDto = new LockMachineParamDto();
        lockMachineParamDto.setMachineId(lockMachinePo.getMachineId());
        List<LockMachineParamDto> lockMachineParamDtos = lockMachineParamV1InnerServiceSMOImpl.queryLockMachineParams(lockMachineParamDto);
        if (lockMachineParamDtos.size() != lockMachineFactoryDtos.get(0).getLockMachineFactorySpecList().size() || lockMachineParamDtos.size() < 1) {
            throw new CmdException("未设置门锁参数");
        }

        lockFactoryAdapt.unlock(lockMachinePo, lockMachineDtos.get(0).getLockMachineParamDtoList());
    }

    @Override
    public void queryMachineStateAndElectricQuantity(List<LockMachineDto> lockMachineDtoList) {
        for (LockMachineDto lockMachineDto : lockMachineDtoList) {
            List<LockMachineDto> lockMachineDtos = lockMachineV1InnerServiceSMOImpl.queryLockMachines(lockMachineDto);
            Assert.listOnlyOne(lockMachineDtos, "门锁设备不存在");

            LockMachineFactoryDto lockMachineFactoryDto = new LockMachineFactoryDto();
            lockMachineFactoryDto.setFactoryId(lockMachineDtos.get(0).getImplBean());
            List<LockMachineFactoryDto> lockMachineFactoryDtos = lockMachineFactoryV1InnerServiceSMOImpl.queryLockMachineFactorys(lockMachineFactoryDto);
            Assert.listOnlyOne(lockMachineFactoryDtos, "门锁厂家不存在");

            ILockFactoryAdapt lockFactoryAdapt = null;
            try{
                lockFactoryAdapt = ApplicationContextFactory.getBean(lockMachineFactoryDtos.get(0).getBeanImpl(), ILockFactoryAdapt.class);
            } finally {
                if (lockFactoryAdapt == null) {
                    throw new CmdException("厂家接口未实现");
                }
            }

            LockMachineParamDto lockMachineParamDto = new LockMachineParamDto();
            lockMachineParamDto.setMachineId(lockMachineDto.getMachineId());
            List<LockMachineParamDto> lockMachineParamDtos = lockMachineParamV1InnerServiceSMOImpl.queryLockMachineParams(lockMachineParamDto);
            if (lockMachineParamDtos.size() != lockMachineFactoryDtos.get(0).getLockMachineFactorySpecList().size() || lockMachineParamDtos.size() < 1) {
                throw new CmdException("未设置门锁参数");
            }
            try {
                lockFactoryAdapt.queryLockMachineState(lockMachineDto, lockMachineDtos.get(0).getLockMachineParamDtoList());
            } catch (Exception e) {
                e.printStackTrace();
                lockMachineDto.setState(LockMachineDto.STATE_OFFLINE);
                lockMachineDto.setStateName("离线");
            }
            lockMachineDto.setCharge(lockFactoryAdapt.queryElectricQuantity(lockMachineDto, lockMachineDtos.get(0).getLockMachineParamDtoList()).toString());
        }
    }

    @Override
    public String addPasswordToCloud(LockPersonPo lockPersonPo) {
        LockMachineDto lockMachineDto = new LockMachineDto();
        lockMachineDto.setMachineId(lockPersonPo.getMachineId());
        List<LockMachineDto> lockMachineDtos = lockMachineV1InnerServiceSMOImpl.queryLockMachines(lockMachineDto);
        Assert.listOnlyOne(lockMachineDtos, "门锁设备不存在");

        LockMachineFactoryDto lockMachineFactoryDto = new LockMachineFactoryDto();
        lockMachineFactoryDto.setFactoryId(lockMachineDtos.get(0).getImplBean());
        List<LockMachineFactoryDto> lockMachineFactoryDtos = lockMachineFactoryV1InnerServiceSMOImpl.queryLockMachineFactorys(lockMachineFactoryDto);
        Assert.listOnlyOne(lockMachineFactoryDtos, "门锁厂家不存在");

        ILockFactoryAdapt lockFactoryAdapt = null;
        try{
            lockFactoryAdapt = ApplicationContextFactory.getBean(lockMachineFactoryDtos.get(0).getBeanImpl(), ILockFactoryAdapt.class);
        } finally {
            if (lockFactoryAdapt == null) {
                throw new CmdException("厂家接口未实现");
            }
        }

        LockMachineParamDto lockMachineParamDto = new LockMachineParamDto();
        lockMachineParamDto.setMachineId(lockPersonPo.getMachineId());
        List<LockMachineParamDto> lockMachineParamDtos = lockMachineParamV1InnerServiceSMOImpl.queryLockMachineParams(lockMachineParamDto);
        if (lockMachineParamDtos.size() != lockMachineFactoryDtos.get(0).getLockMachineFactorySpecList().size() || lockMachineParamDtos.size() < 1) {
            throw new CmdException("未设置门锁参数");
        }

        return lockFactoryAdapt.addPasswordToCloud(lockPersonPo, lockMachineDtos.get(0).getLockMachineParamDtoList());
    }

    @Override
    public void updatePasswordToCloud(LockPersonPo lockPersonPo) {
        LockMachineDto lockMachineDto = new LockMachineDto();
        lockMachineDto.setMachineId(lockPersonPo.getMachineId());
        List<LockMachineDto> lockMachineDtos = lockMachineV1InnerServiceSMOImpl.queryLockMachines(lockMachineDto);
        Assert.listOnlyOne(lockMachineDtos, "门锁设备不存在");

        LockMachineFactoryDto lockMachineFactoryDto = new LockMachineFactoryDto();
        lockMachineFactoryDto.setFactoryId(lockMachineDtos.get(0).getImplBean());
        List<LockMachineFactoryDto> lockMachineFactoryDtos = lockMachineFactoryV1InnerServiceSMOImpl.queryLockMachineFactorys(lockMachineFactoryDto);
        Assert.listOnlyOne(lockMachineFactoryDtos, "门锁厂家不存在");

        ILockFactoryAdapt lockFactoryAdapt = null;
        try{
            lockFactoryAdapt = ApplicationContextFactory.getBean(lockMachineFactoryDtos.get(0).getBeanImpl(), ILockFactoryAdapt.class);
        } finally {
            if (lockFactoryAdapt == null) {
                throw new CmdException("厂家接口未实现");
            }
        }

        LockMachineParamDto lockMachineParamDto = new LockMachineParamDto();
        lockMachineParamDto.setMachineId(lockPersonPo.getMachineId());
        List<LockMachineParamDto> lockMachineParamDtos = lockMachineParamV1InnerServiceSMOImpl.queryLockMachineParams(lockMachineParamDto);
        if (lockMachineParamDtos.size() != lockMachineFactoryDtos.get(0).getLockMachineFactorySpecList().size() || lockMachineParamDtos.size() < 1) {
            throw new CmdException("未设置门锁参数");
        }

        lockFactoryAdapt.updatePasswordToCloud(lockPersonPo, lockMachineDtos.get(0).getLockMachineParamDtoList());
    }
}
