package com.java110.lock.factory;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.dto.lock.LockMachineDto;

public interface ILockFactoryAdapt {
    /**
     * 开门
     * @param reqJson
     * @return
     */
    ResultVo unlock(JSONObject reqJson);

    /**
     * 查询设备状态
     * @param lockMachineDto
     */
    void queryLockMachineState(LockMachineDto lockMachineDto);
}
