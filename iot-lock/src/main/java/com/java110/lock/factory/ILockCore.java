package com.java110.lock.factory;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.dto.lock.LockMachineDto;
import com.java110.po.lock.LockMachinePo;
import com.java110.po.lock.LockPersonPo;

import java.util.List;

public interface ILockCore {

    /**
     * 打开门锁
     * @param lockMachinePo
     * @return
     */
    void unlock(LockMachinePo lockMachinePo);

    /**
     * 查询门锁在线状态和门锁电量
     * @param lockMachineDtos
     */
    void queryMachineStateAndElectricQuantity(List<LockMachineDto> lockMachineDtos);

    /**
     * 将自定义密码推向厂家云端
     * @param lockPersonPo
     * @return
     */
    String addPasswordToCloud(LockPersonPo lockPersonPo);

    /**
     * 修改锁密码
     * @param lockPersonPo
     */
    void updatePasswordToCloud(LockPersonPo lockPersonPo);
}
