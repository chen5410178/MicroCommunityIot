package com.java110.lock.factory;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;

public interface ILockFactoryAdapt {
    /**
     * 开门
     * @param reqJson
     * @return
     */
    ResultVo unlock(JSONObject reqJson);
}
