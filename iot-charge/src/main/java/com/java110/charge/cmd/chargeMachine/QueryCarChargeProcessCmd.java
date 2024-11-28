package com.java110.charge.cmd.chargeMachine;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;

import java.text.ParseException;

/**
 * 查询充电进度
 */
@Java110Cmd(serviceCode = "chargeMachine.queryCarChargeProcess")
public class QueryCarChargeProcessCmd extends Cmd {
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

        /**
         * todo 这里实现查询充电进度
         */
        JSONObject data = new JSONObject();
        data.put("process", 40); // todo 充电进度
        data.put("min", 10); // 剩余分钟数
        data.put("charged", 3.17); // 已充电量
        data.put("chargeMoney", 2.01); // todo 已充金额
        data.put("electric", 10); // 电流
        data.put("voltage", 220); // 电压
        data.put("temperature", 35); // 温度
        context.setResponseEntity(ResultVo.createResponseEntity(data));
    }
}
