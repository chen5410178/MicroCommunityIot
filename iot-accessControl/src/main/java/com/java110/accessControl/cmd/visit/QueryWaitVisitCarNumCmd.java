package com.java110.accessControl.cmd.visit;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cache.CommonCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.StringUtil;

import java.text.ParseException;

@Java110Cmd(serviceCode = "visit.queryWaitVisitCarNum")
public class QueryWaitVisitCarNumCmd extends Cmd {
    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "machineId", "未包含进场摄像头");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        String carInoutTemp = CommonCache.getValue(reqJson.getString("machineId") + "_inCarEngine");
        if (StringUtil.isEmpty(carInoutTemp)) {
            context.setResponseEntity(ResultVo.createResponseEntity(""));
            return;
        }

        JSONObject carInoutTempJson = JSONObject.parseObject(carInoutTemp);

        String carNum = carInoutTempJson.getString("carNum");
        context.setResponseEntity(ResultVo.createResponseEntity(carNum));


    }
}
