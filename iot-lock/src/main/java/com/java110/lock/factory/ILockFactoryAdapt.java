package com.java110.lock.factory;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.dto.lock.LockMachineDto;
import com.java110.dto.lock.LockMachineParamDto;
import com.java110.po.lock.LockPersonPo;

import java.util.List;

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

    /**
     * 将自定义密码推向厂家云端
     * @param lockPersonPo
     * @param lockMachineParamDtos
     */
    void addPasswordToCloud(LockPersonPo lockPersonPo, List<LockMachineParamDto> lockMachineParamDtos);
}
