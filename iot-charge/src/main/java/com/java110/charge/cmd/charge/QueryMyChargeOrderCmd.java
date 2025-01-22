package com.java110.charge.cmd.charge;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.CmdContextUtils;
import com.java110.core.utils.ListUtil;
import com.java110.dto.chargeMachine.ChargeMachineOrderDto;
import com.java110.intf.charge.IChargeMachineOrderV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

@Java110Cmd(serviceCode = "charge.queryMyChargeOrder")
public class QueryMyChargeOrderCmd extends Cmd {

    @Autowired
    private IChargeMachineOrderV1InnerServiceSMO chargeMachineOrderV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "portId", "未包含枪编号");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String userId = CmdContextUtils.getUserId(context);

        ChargeMachineOrderDto chargeMachineOrderDto = new ChargeMachineOrderDto();
        chargeMachineOrderDto.setPersonId(userId);
        chargeMachineOrderDto.setPortId(reqJson.getString("portId"));
        chargeMachineOrderDto.setState(ChargeMachineOrderDto.STATE_DOING);
        List<ChargeMachineOrderDto> chargeMachineOrderDtos = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrders(chargeMachineOrderDto);
        if (ListUtil.isNull(chargeMachineOrderDtos)) {
            throw new CmdException("不存在充电订单");
        }
        context.setResponseEntity(ResultVo.createResponseEntity(chargeMachineOrderDtos.get(0)));
    }
}
