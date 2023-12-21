package com.java110.charge.cmd.charge;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DateUtil;
import com.java110.intf.charge.IChargeMachineOrderV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 查询充电订单数
 */
@Java110Cmd(serviceCode = "charge.queryOrderMoneyStatistics")
public class QueryOrderMoneyStatisticsCmd extends Cmd {

    @Autowired
    private IChargeMachineOrderV1InnerServiceSMO chargeMachineOrderV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String startTime = DateUtil.getAddDayStringB(DateUtil.getCurrentDate(),-7);

        reqJson.put("startTime",startTime);

        List<Map> datas = chargeMachineOrderV1InnerServiceSMOImpl.queryOrderMoneyStatistics(reqJson);
        context.setResponseEntity(ResultVo.createResponseEntity(datas));
    }
}
