package com.java110.lock.factory;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.dto.lock.LockMachineDto;
import com.java110.dto.lock.LockMachineParamDto;
import com.java110.po.lock.LockMachinePo;
import com.java110.po.lock.LockPersonPo;

import java.util.List;

public interface ILockFactoryAdapt {
    /**
     * 开门
     *
     * @param lockMachinePo
     * @param lockMachineParamDtos
     * @return
     */
    void unlock(LockMachinePo lockMachinePo, List<LockMachineParamDto> lockMachineParamDtos);

    /**
     * 查询门锁在线状态
     *
     * @param lockMachineDto
     */
    void queryLockMachineState(LockMachineDto lockMachineDto, List<LockMachineParamDto> lockMachineParamDtos);

    /**
     * 将自定义密码推向厂家云端
     *
     * @param lockPersonPo
     * @param lockMachineParamDtos
     * @return
     */
    String addPasswordToCloud(LockPersonPo lockPersonPo, List<LockMachineParamDto> lockMachineParamDtos);

    /**
     * 修改锁密码
     *
     * @param lockPersonPo
     * @param lockMachineParamDtos
     */
    void updatePasswordToCloud(LockPersonPo lockPersonPo, List<LockMachineParamDto> lockMachineParamDtos);

    /**
     * 查询门锁电量
     *
     * @param lockMachineDto
     * @param lockMachineParamDtos
     */
    Integer queryElectricQuantity(LockMachineDto lockMachineDto, List<LockMachineParamDto> lockMachineParamDtos);

    /**
     * mqtt 上报数据
     *
     * @param topic 主题
     * @param data  数据
     */
    void lockResult(String topic, String data);
}
