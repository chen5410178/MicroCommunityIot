package com.java110.lock.factory;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.dto.lock.LockMachineDto;
import com.java110.po.lock.LockPersonPo;

import java.util.List;

public interface ILockCore {

    /**
     * 打开门锁
     * @param reqJson
     * @return
     */
    ResultVo unlock(JSONObject reqJson);

    /**
     * 查询设备在线状态
     * @param lockMachineDtos
     */
    void queryLockMachineState(List<LockMachineDto> lockMachineDtos);

    /**
     * 将自定义密码推向厂家云端
     * @param lockPersonPo
     */
    void addPasswordToCloud(LockPersonPo lockPersonPo);
}
