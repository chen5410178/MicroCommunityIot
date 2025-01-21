package com.java110.charge.cmd.chargeMachine;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ListUtil;
import com.java110.dto.chargeProcessAnalysis.ChargeProcessAnalysisDto;
import com.java110.intf.charge.IChargeProcessAnalysisV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

/**
 * 查询充电进度
 */
@Java110Cmd(serviceCode = "chargeMachine.queryCarChargeProcess")
public class QueryCarChargeProcessCmd extends Cmd {

    @Autowired
    private IChargeProcessAnalysisV1InnerServiceSMO chargeProcessAnalysisV1InnerServiceSMOImpl;


    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "查询小区ID");
        Assert.hasKeyAndValue(reqJson, "orderId", "查询订单ID");

    }

    /**
     * process: 0,
     * min: 60,
     * charged: 3.17,
     * chargeMoney: 2.01,
     * energy: 70,
     * electric: 10,
     * voltage: 220,
     * temperature: 35,
     *
     * @param event   事件对象
     * @param context 数据上文对象
     * @param reqJson 请求报文
     * @throws CmdException
     * @throws ParseException
     */
    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        ChargeProcessAnalysisDto chargeProcessAnalysisDto = new ChargeProcessAnalysisDto();
        chargeProcessAnalysisDto.setOrderId(reqJson.getString("orderId"));
        List<ChargeProcessAnalysisDto> chargeProcessAnalysiss = chargeProcessAnalysisV1InnerServiceSMOImpl.queryChargeProcessAnalysiss(chargeProcessAnalysisDto);
        if(ListUtil.isNull(chargeProcessAnalysiss)){
            throw new CmdException("未查询到上报数据");
        }
        JSONObject data = new JSONObject();
        String[] parts = chargeProcessAnalysiss.get(0).getSoc().split("\\.");
        data.put("process", parts[0]); //充电进度（整数部分）
        data.put("min", chargeProcessAnalysiss.get(0).getRemainingTime()); // 剩余分钟数
        data.put("charged", chargeProcessAnalysiss.get(0).getChargingDegree()); // 已充电量
        data.put("chargeMoney", chargeProcessAnalysiss.get(0).getRechargeAmount()); //已充金额
        data.put("electric", chargeProcessAnalysiss.get(0).getOutputCurrent()); // 电流
        data.put("voltage", chargeProcessAnalysiss.get(0).getOutputVoltage()); // 电压
        data.put("temperature", chargeProcessAnalysiss.get(0).getGunLineTem()); // 枪线温度
        context.setResponseEntity(ResultVo.createResponseEntity(data));
    }
}
