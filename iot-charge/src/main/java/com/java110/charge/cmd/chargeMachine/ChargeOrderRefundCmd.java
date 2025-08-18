package com.java110.charge.cmd.chargeMachine;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.charge.factory.IChargeCore;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.intf.charge.IChargeMachineOrderV1InnerServiceSMO;
import com.java110.intf.charge.IChargeMachinePortV1InnerServiceSMO;
import com.java110.intf.charge.IChargeMachineV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;

/**
 *
 * <p>
 * add by wuxw 2023-03-09
 */

@Java110Cmd(serviceCode = "chargeMachine.orderRefundMoney")
public class ChargeOrderRefundCmd extends Cmd {

    @Autowired
    private IChargeMachineV1InnerServiceSMO chargeMachineV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMachinePortV1InnerServiceSMO chargeMachinePortV1InnerServiceSMOImpl;

    @Autowired
    private IChargeCore chargeCoreImpl;

    @Autowired
    private IChargeMachineOrderV1InnerServiceSMO chargeMachineOrderV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区信息");
        Assert.hasKeyAndValue(reqJson, "orderId", "未包含订单ID");
        Assert.hasKeyAndValue(reqJson, "returnMoney", "未包含退款金额");
        Assert.hasKeyAndValue(reqJson, "remark", "未包含备注");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String orderId = reqJson.getString("orderId");
        String returnMoney = reqJson.getString("returnMoney");
        String remark = reqJson.getString("remark");
        ResultVo resultVo = chargeCoreImpl.orderRefund(orderId,returnMoney,remark);
        if (resultVo.getCode() != ResultVo.CODE_OK) {
            context.setResponseEntity(ResultVo.createResponseEntity(resultVo));
            return;
        }
        context.setResponseEntity(ResultVo.createResponseEntity(resultVo));
    }
}
