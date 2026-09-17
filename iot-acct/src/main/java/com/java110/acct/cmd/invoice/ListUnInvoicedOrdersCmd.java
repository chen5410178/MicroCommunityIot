package com.java110.acct.cmd.invoice;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.acct.bmo.invoice.IInvoiceAdminBmo;
import com.java110.acct.bmo.invoice.IIotInvoiceSubmitBmo;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.CmdContextUtils;
import com.java110.dto.chargeMachine.ChargeMachineOrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 按手机号查询未开票充电订单（PC 管理端开票页）
 * 服务编码：invoice.listUnInvoicedOrders
 * 口径与手机端一致：该号下已完成（2002）、实付金额>0、且未开过有效开票单的充电订单
 */
@Java110Cmd(serviceCode = "invoice.listUnInvoicedOrders")
public class ListUnInvoicedOrdersCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ListUnInvoicedOrdersCmd.class);

    private static final String PHONE_REGEX = "^1\\d{10}$";

    @Autowired
    private IIotInvoiceSubmitBmo iotInvoiceSubmitBmoImpl;

    @Autowired
    private IInvoiceAdminBmo invoiceAdminBmoImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "请先选择小区");
        Assert.hasKeyAndValue(reqJson, "phone", "未包含开票手机号");
        if (!Pattern.matches(PHONE_REGEX, reqJson.getString("phone"))) {
            throw new CmdException("手机号格式错误");
        }
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String communityId = reqJson.getString("communityId");
        invoiceAdminBmoImpl.validateCommunityAdmin(CmdContextUtils.getStoreId(context),
                CmdContextUtils.getLoginUserId(context), communityId);

        List<ChargeMachineOrderDto> orders = iotInvoiceSubmitBmoImpl.queryUnInvoicedOrders(communityId,
                reqJson.getString("phone"));

        JSONArray orderArray = new JSONArray();
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (ChargeMachineOrderDto order : orders) {
            totalAmount = totalAmount.add(toBigDecimal(order.getAmount()));
            orderArray.add(toOrderJson(order));
        }

        JSONObject data = new JSONObject();
        data.put("orders", orderArray);
        data.put("totalAmount", totalAmount.setScale(2, RoundingMode.HALF_UP).toPlainString());

        context.setResponseEntity(ResultVo.createResponseEntity(1, orderArray.size(), data));
    }

    private JSONObject toOrderJson(ChargeMachineOrderDto order) {
        JSONObject result = new JSONObject();
        result.put("orderId", order.getOrderId());
        result.put("amount", order.getAmount());
        result.put("machineName", order.getMachineName());
        result.put("machineCode", order.getMachineCode());
        result.put("portName", order.getPortName());
        result.put("startTime", order.getStartTime());
        result.put("endTime", order.getEndTime());
        result.put("energy", order.getEnergy());
        result.put("chargeHours", order.getChargeHours());
        return result;
    }

    private BigDecimal toBigDecimal(String amount) {
        if (amount == null || amount.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(amount);
        } catch (Exception e) {
            logger.error("金额转换失败 amount: {}", amount);
            return BigDecimal.ZERO;
        }
    }
}
