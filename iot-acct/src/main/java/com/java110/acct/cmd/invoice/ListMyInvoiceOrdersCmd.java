package com.java110.acct.cmd.invoice;

import com.alibaba.fastjson.JSONArray;
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
import com.java110.dto.invoice.InvoiceOrderDto;
import com.java110.dto.invoice.InvoiceOrderItemDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.acct.IInvoiceOrderItemV1InnerServiceSMO;
import com.java110.intf.acct.IInvoiceOrderV1InnerServiceSMO;
import com.java110.intf.charge.IChargeMachineOrderV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 查询我的开票订单视图（未开票订单 + 已开票记录）
 * 服务编码：invoice.listMyInvoiceOrders
 * 未开票：当前小区已完成（state=2002）且实付金额>0 且未开过票（不在 state=DONE 的开票明细中）的充电订单
 */
@Java110Cmd(serviceCode = "invoice.listMyInvoiceOrders")
public class ListMyInvoiceOrdersCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ListMyInvoiceOrdersCmd.class);

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMachineOrderV1InnerServiceSMO chargeMachineOrderV1InnerServiceSMOImpl;

    @Autowired
    private IInvoiceOrderV1InnerServiceSMO invoiceOrderV1InnerServiceSMOImpl;

    @Autowired
    private IInvoiceOrderItemV1InnerServiceSMO invoiceOrderItemV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "请先选择小区");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String userId = CmdContextUtils.getUserId(context);

        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        if (ListUtil.isNull(userDtos)) {
            throw new CmdException("用户不存在");
        }
        String phone = userDtos.get(0).getTel();

        String communityId = reqJson.getString("communityId");
        String bizCategory = reqJson.getString("bizCategory");
        if (bizCategory == null || bizCategory.isEmpty()) {
            bizCategory = InvoiceOrderDto.BIZ_CATEGORY_CHARGE;
        }

        // 1. 我的开票记录（当前小区 + 业务类目）
        InvoiceOrderDto invoiceOrderDto = new InvoiceOrderDto();
        invoiceOrderDto.setPhone(phone);
        invoiceOrderDto.setCommunityId(communityId);
        invoiceOrderDto.setBizCategory(bizCategory);
        invoiceOrderDto.setStatusCd("0");
        invoiceOrderDto.setPage(-1);
        List<InvoiceOrderDto> invoiceOrders = invoiceOrderV1InnerServiceSMOImpl.queryInvoiceOrders(invoiceOrderDto);
        if (invoiceOrders == null) {
            invoiceOrders = new ArrayList<>();
        }

        // 2. 已开票（DONE）的订单ID集合
        Set<String> invoicedOrderIds = new HashSet<>();
        for (InvoiceOrderDto invoiceOrder : invoiceOrders) {
            if (InvoiceOrderDto.STATE_DONE.equals(invoiceOrder.getState())) {
                InvoiceOrderItemDto itemQuery = new InvoiceOrderItemDto();
                itemQuery.setInvoiceOrderId(invoiceOrder.getInvoiceOrderId());
                itemQuery.setStatusCd("0");
                itemQuery.setPage(-1);
                List<InvoiceOrderItemDto> items = invoiceOrderItemV1InnerServiceSMOImpl.queryInvoiceOrderItems(itemQuery);
                if (!ListUtil.isNull(items)) {
                    for (InvoiceOrderItemDto item : items) {
                        invoicedOrderIds.add(item.getOrderId());
                    }
                }
            }
        }

        // 3. 未开票充电订单（已完成）
        ChargeMachineOrderDto orderQuery = new ChargeMachineOrderDto();
        orderQuery.setPersonTel(phone);
        orderQuery.setCommunityId(communityId);
        orderQuery.setState(ChargeMachineOrderDto.STATE_FINISH);
        orderQuery.setStatusCd("0");
        orderQuery.setPage(-1);
        List<ChargeMachineOrderDto> finishedOrders = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrders(orderQuery);
        if (finishedOrders == null) {
            finishedOrders = new ArrayList<>();
        }

        JSONArray unInvoiceOrders = new JSONArray();
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (ChargeMachineOrderDto order : finishedOrders) {
            if (invoicedOrderIds.contains(order.getOrderId())) {
                continue;
            }
            BigDecimal amount = toBigDecimal(order.getAmount());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            totalAmount = totalAmount.add(amount);
            unInvoiceOrders.add(toUnInvoiceJson(order));
        }

        JSONArray invoiceOrderArray = new JSONArray();
        for (InvoiceOrderDto invoiceOrder : invoiceOrders) {
            invoiceOrderArray.add(toInvoiceJson(invoiceOrder));
        }

        JSONObject data = new JSONObject();
        data.put("unInvoiceOrders", unInvoiceOrders);
        data.put("invoiceOrders", invoiceOrderArray);
        data.put("totalAmount", totalAmount.setScale(2).toPlainString());

        ResultVo resultVo = new ResultVo(ResultVo.CODE_OK, ResultVo.MSG_OK, data);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(resultVo.toString(), HttpStatus.OK);
        context.setResponseEntity(responseEntity);
    }

    private JSONObject toUnInvoiceJson(ChargeMachineOrderDto order) {
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

    private JSONObject toInvoiceJson(InvoiceOrderDto invoiceOrder) {
        JSONObject result = new JSONObject();
        result.put("invoiceOrderId", invoiceOrder.getInvoiceOrderId());
        result.put("invoiceId", invoiceOrder.getInvoiceId());
        result.put("state", invoiceOrder.getState());
        result.put("invoiceNo", invoiceOrder.getInvoiceNo());
        result.put("invoiceCode", invoiceOrder.getInvoiceCode());
        result.put("pdfUrl", invoiceOrder.getPdfUrl());
        result.put("failReason", invoiceOrder.getFailReason());
        result.put("headingType", invoiceOrder.getHeadingType());
        result.put("buyerName", invoiceOrder.getBuyerName());
        result.put("amount", invoiceOrder.getAmount());
        result.put("createTime", invoiceOrder.getCreateTime() == null ? "" : invoiceOrder.getCreateTime().getTime());
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
