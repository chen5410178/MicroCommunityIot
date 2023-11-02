package com.java110.lock.factory;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;

public interface ILockCore {

    /**
     * 打开门锁
     * @param reqJson
     * @return
     */
    ResultVo unlock(JSONObject reqJson);
}
