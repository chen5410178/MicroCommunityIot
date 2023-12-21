package com.java110.meter.cmd.meter;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DateUtil;
import com.java110.intf.meter.IMeterMachineDetailV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 水电统计报表
 */
@Java110Cmd(serviceCode = "meter.queryMeterMoneyStatistics")
public class QueryMeterMoneyStatisticsCmd extends Cmd {

    @Autowired
    private IMeterMachineDetailV1InnerServiceSMO meterMachineDetailV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        String startTime = DateUtil.getAddDayStringB(DateUtil.getCurrentDate(),-7);

        reqJson.put("startTime",startTime);

        List<Map> datas = meterMachineDetailV1InnerServiceSMOImpl.queryMeterMoneyStatistics(reqJson);
        context.setResponseEntity(ResultVo.createResponseEntity(datas));
    }
}
