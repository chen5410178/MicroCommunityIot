package com.java110.openapi.bmo;

import com.alibaba.fastjson.JSONObject;

import com.java110.core.context.ICmdDataFlowContext;

public interface IIotCommonApiBmo {

    void validate(ICmdDataFlowContext context, JSONObject reqJson);


    void doCmd(ICmdDataFlowContext context, JSONObject reqJson);
}
