package com.java110.barrier.cmd.tempCarFee;

import com.alibaba.fastjson.JSONObject;
import com.java110.barrier.bmo.IGetTempCarFeeRules;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.fee.TempCarFeeRuleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;


@Java110Cmd(serviceCode = "tempCarFee.queryTempCarFeeRules")
public class QueryTempCarFeeRulesCmd extends Cmd {


    @Autowired
    private IGetTempCarFeeRules getTempCarFeeRulesImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        TempCarFeeRuleDto tempCarFeeRuleDto = BeanConvertUtil.covertBean(reqJson, TempCarFeeRuleDto.class);
        ResponseEntity<String> responseEntity = getTempCarFeeRulesImpl.queryRules(tempCarFeeRuleDto);
        context.setResponseEntity(responseEntity);
    }
}
