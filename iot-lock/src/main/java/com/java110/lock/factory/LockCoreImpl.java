package com.java110.lock.factory;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.Assert;
import com.java110.dto.lock.LockMachineDto;
import com.java110.dto.lock.LockMachineFactoryDto;
import com.java110.intf.lock.ILockMachineFactoryV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LockCoreImpl implements ILockCore{

    @Autowired
    private ILockMachineFactoryV1InnerServiceSMO lockMachineFactoryV1InnerServiceSMOImpl;

    @Override
    public ResultVo unlock(JSONObject reqJson) {
        LockMachineFactoryDto lockMachineFactoryDto = new LockMachineFactoryDto();
        lockMachineFactoryDto.setFactoryId(reqJson.getString("implBean"));
        List<LockMachineFactoryDto> lockMachineFactoryDtos = lockMachineFactoryV1InnerServiceSMOImpl.queryLockMachineFactorys(lockMachineFactoryDto);
        Assert.listOnlyOne(lockMachineFactoryDtos, "门锁厂家不存在");

        ILockFactoryAdapt lockFactoryAdaptImpl = null;
        try {
            lockFactoryAdaptImpl = ApplicationContextFactory.getBean(lockMachineFactoryDtos.get(0).getBeanImpl(), ILockFactoryAdapt.class);
        } finally {
            if (lockFactoryAdaptImpl == null) {
                throw new CmdException("厂家接口未实现");
            }
        }

        return lockFactoryAdaptImpl.unlock(reqJson);
    }

    @Override
    public void queryLockMachineState(List<LockMachineDto> lockMachineDtos) {
        for (LockMachineDto lockMachineDto : lockMachineDtos) {
            try {
                LockMachineFactoryDto lockMachineFactoryDto = new LockMachineFactoryDto();
                lockMachineFactoryDto.setFactoryId(lockMachineDto.getImplBean());
                List<LockMachineFactoryDto> lockMachineFactoryDtos = lockMachineFactoryV1InnerServiceSMOImpl.queryLockMachineFactorys(lockMachineFactoryDto);

                Assert.listOnlyOne(lockMachineFactoryDtos, "门锁厂家不存在");

                ILockFactoryAdapt lockFactoryAdapt = ApplicationContextFactory.getBean(lockMachineFactoryDtos.get(0).getBeanImpl(), ILockFactoryAdapt.class);
                if (lockFactoryAdapt == null) {
                    throw new CmdException("厂家接口未实现");
                }
                lockFactoryAdapt.queryLockMachineState(lockMachineDto);
            } catch (Exception e) {
                e.printStackTrace();
                lockMachineDto.setState(LockMachineDto.STATE_OFFLINE);
                lockMachineDto.setStateName("离线");
            }
        }
    }
}
