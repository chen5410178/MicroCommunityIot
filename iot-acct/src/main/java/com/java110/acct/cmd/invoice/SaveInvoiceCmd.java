package com.java110.acct.cmd.invoice;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
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
import com.java110.po.invoice.InvoiceOrderItemPo;
import com.java110.po.invoice.InvoiceOrderPo;
import com.java110.acct.mall.ISendMall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 提交开票（核心）：校验 → 建单 → 同步商城抬头 → 商城开票 → 回写发票信息
 * 服务编码：invoice.saveInvoice
 * 幂等：商城按 remark（CHARGE:orderId1,orderId2）判重
 */
@Java110Cmd(serviceCode = "invoice.saveInvoice")
public class SaveInvoiceCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(SaveInvoiceCmd.class);

    private static final String MALL_GET_HEADING = "queryInvoiceHeadingBmoImpl";
    private static final String MALL_SAVE_HEADING = "saveInvoiceHeadingBmoImpl";
    private static final String MALL_PROPERTY_INVOICE = "propertyInvoiceBmoImpl";

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMachineOrderV1InnerServiceSMO chargeMachineOrderV1InnerServiceSMOImpl;

    @Autowired
    private IInvoiceOrderV1InnerServiceSMO invoiceOrderV1InnerServiceSMOImpl;

    @Autowired
    private IInvoiceOrderItemV1InnerServiceSMO invoiceOrderItemV1InnerServiceSMOImpl;

    @Autowired
    private ISendMall sendMallImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "请先选择小区");
        JSONArray orderIds = reqJson.getJSONArray("orderIds");
        if (orderIds == null || orderIds.isEmpty()) {
            throw new CmdException("请先勾选需要开票的订单");
        }
        Assert.hasKeyAndValue(reqJson, "headingType", "未包含抬头类型");
        Assert.hasKeyAndValue(reqJson, "headingName", "未包含抬头名称");
        if ("公司".equals(reqJson.getString("headingType"))) {
            Assert.hasKeyAndValue(reqJson, "taxNo", "企业抬头必须填写税号");
        }
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String userId = CmdContextUtils.getLoginUserId(context);

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
        String headingType = reqJson.getString("headingType");
        String headingName = reqJson.getString("headingName");
        String taxNo = reqJson.getString("taxNo");
        String email = reqJson.getString("email");
        String invoiceType = reqJson.getString("invoiceType");
        if (invoiceType == null || invoiceType.isEmpty()) {
            invoiceType = "普票";
        }
        String receivePhone = reqJson.getString("phone");
        if (receivePhone == null || receivePhone.isEmpty()) {
            receivePhone = phone;
        }

        // 1. 校验订单（存在、归属、已完成、金额>0、小区一致、未开票）
        JSONArray orderIds = reqJson.getJSONArray("orderIds");
        List<String> orderIdList = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (int i = 0; i < orderIds.size(); i++) {
            String orderId = orderIds.getString(i);
            orderIdList.add(orderId);
            ChargeMachineOrderDto order = getChargeMachineOrder(orderId);
            if (order == null) {
                throw new CmdException("订单" + orderId + "不存在");
            }
            if (!phone.equals(order.getPersonTel())) {
                throw new CmdException("订单" + orderId + "不属于当前用户");
            }
            if (!communityId.equals(order.getCommunityId())) {
                throw new CmdException("订单" + orderId + "与当前小区不一致");
            }
            if (!ChargeMachineOrderDto.STATE_FINISH.equals(order.getState())) {
                throw new CmdException("订单" + orderId + "未完成，不能开票");
            }
            BigDecimal amount = toBigDecimal(order.getAmount());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new CmdException("订单" + orderId + "无实付金额，不参与开票");
            }
            if (isOrderInvoiced(orderId)) {
                throw new CmdException("订单" + orderId + "已开票，请勿重复提交");
            }
            totalAmount = totalAmount.add(amount);
        }

        // 2. 建单（invoice_order + invoice_order_item）
        String invoiceOrderId = GenerateCodeFactory.getGeneratorId("CINV");
        String remark = "CHARGE:" + String.join(",", orderIdList);

        InvoiceOrderPo invoiceOrderPo = new InvoiceOrderPo();
        invoiceOrderPo.setInvoiceOrderId(invoiceOrderId);
        invoiceOrderPo.setUserId(userId);
        invoiceOrderPo.setPhone(phone);
        invoiceOrderPo.setCommunityId(communityId);
        invoiceOrderPo.setHeadingType(headingType);
        invoiceOrderPo.setBuyerName(headingName);
        invoiceOrderPo.setBuyerTaxNo(taxNo);
        invoiceOrderPo.setEmail(email);
        invoiceOrderPo.setInvoiceType(invoiceType);
        invoiceOrderPo.setBizCategory(bizCategory);
        invoiceOrderPo.setAmount(totalAmount.setScale(2, RoundingMode.HALF_UP).toPlainString());
        invoiceOrderPo.setState(InvoiceOrderDto.STATE_WAIT);
        invoiceOrderPo.setRemark(remark);
        int saveFlag = invoiceOrderV1InnerServiceSMOImpl.saveInvoiceOrder(invoiceOrderPo);
        if (saveFlag < 1) {
            throw new CmdException("保存开票单失败");
        }

        int seq = 1;
        for (String orderId : orderIdList) {
            ChargeMachineOrderDto order = getChargeMachineOrder(orderId);
            InvoiceOrderItemPo itemPo = new InvoiceOrderItemPo();
            itemPo.setItemId(GenerateCodeFactory.getGeneratorId("CINI"));
            itemPo.setInvoiceOrderId(invoiceOrderId);
            itemPo.setOrderId(orderId);
            itemPo.setCommunityId(communityId);
            itemPo.setBizCategory(bizCategory);
            itemPo.setAmount(toBigDecimal(order.getAmount()).setScale(2, RoundingMode.HALF_UP).toPlainString());
            itemPo.setSeq(seq + "");
            if (invoiceOrderItemV1InnerServiceSMOImpl.saveInvoiceOrderItem(itemPo) < 1) {
                throw new CmdException("保存开票明细失败");
            }
            seq++;
        }

        // 3. 同步抬头到商城（存在更新、不存在新增）
        JSONObject headingParam = new JSONObject();
        headingParam.put("mallApiCode", MALL_SAVE_HEADING);
        headingParam.put("headingType", headingType);
        headingParam.put("headingName", headingName);
        headingParam.put("taxNo", taxNo == null ? "" : taxNo);
        headingParam.put("phone", receivePhone);
        headingParam.put("userPhone", phone);   // 用户电话 = 登录用户手机号（抬头归属，不可修改）
        headingParam.put("email", email == null ? "" : email);
        headingParam.put("isDefault", "1");
        ResultVo headingResult = sendMallImpl.post("/mall/api/common.openCommonApi", headingParam);
        if (headingResult.getCode() != ResultVo.CODE_OK) {
            throw new CmdException(headingResult.getMsg());
        }
        if (headingResult.getData() == null) {
            throw new CmdException("商城系统未部署");
        }
        JSONObject headingData = JSONObject.parseObject(JSONObject.toJSONString(headingResult.getData()));
        String headingId = headingData.getString("headingId");
        if (headingId == null || headingId.isEmpty()) {
            throw new CmdException("保存发票抬头失败");
        }
        InvoiceOrderPo updateHeadingPo = new InvoiceOrderPo();
        updateHeadingPo.setInvoiceOrderId(invoiceOrderId);
        updateHeadingPo.setHeadingId(headingId);
        invoiceOrderV1InnerServiceSMOImpl.updateInvoiceOrder(updateHeadingPo);

        // 4. 商城开票（免审核直接开票）
        JSONObject invoiceParam = new JSONObject();
        invoiceParam.put("mallApiCode", MALL_PROPERTY_INVOICE);
        invoiceParam.put("orgId", communityId);
        invoiceParam.put("orgType", "C");
        invoiceParam.put("buyerName", headingName);
        invoiceParam.put("buyerTaxNo", taxNo == null ? "" : taxNo);
        invoiceParam.put("invoiceType", invoiceType);
        invoiceParam.put("remark", remark);
        JSONArray items = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("bizCategory", bizCategory);
        item.put("amount", totalAmount.setScale(2, RoundingMode.HALF_UP).toPlainString());
        items.add(item);
        invoiceParam.put("items", items);
        ResultVo invoiceResult = sendMallImpl.post("/mall/api/common.openCommonApi", invoiceParam);
        if (invoiceResult.getCode() != ResultVo.CODE_OK) {
            updateInvoiceOrderFail(invoiceOrderId, invoiceResult.getMsg());
            throw new CmdException(invoiceResult.getMsg());
        }
        if (invoiceResult.getData() == null) {
            updateInvoiceOrderFail(invoiceOrderId, "商城开票未生效");
            throw new CmdException("商城开票未生效");
        }
        JSONObject invoiceData = JSONObject.parseObject(JSONObject.toJSONString(invoiceResult.getData()));

        // 5. 回写发票信息（DONE/FAIL/WAIT）
        InvoiceOrderPo updatePo = new InvoiceOrderPo();
        updatePo.setInvoiceOrderId(invoiceOrderId);
        updatePo.setInvoiceId(invoiceData.getString("invoiceId"));
        updatePo.setState(invoiceData.getString("state"));
        updatePo.setInvoiceNo(invoiceData.getString("invoiceNo"));
        updatePo.setInvoiceCode(invoiceData.getString("invoiceCode"));
        updatePo.setPdfUrl(invoiceData.getString("pdfUrl"));
        updatePo.setFailReason(invoiceData.getString("failReason"));
        invoiceOrderV1InnerServiceSMOImpl.updateInvoiceOrder(updatePo);

        JSONObject result = new JSONObject();
        result.put("invoiceOrderId", invoiceOrderId);
        result.put("invoiceId", invoiceData.getString("invoiceId"));
        result.put("state", invoiceData.getString("state"));
        result.put("invoiceNo", invoiceData.getString("invoiceNo"));
        result.put("invoiceCode", invoiceData.getString("invoiceCode"));
        result.put("pdfUrl", invoiceData.getString("pdfUrl"));
        result.put("failReason", invoiceData.getString("failReason"));

        ResponseEntity<String> responseEntity = new ResponseEntity<>(
                new ResultVo(ResultVo.CODE_OK, ResultVo.MSG_OK, result).toString(), HttpStatus.OK);
        context.setResponseEntity(responseEntity);
    }

    private ChargeMachineOrderDto getChargeMachineOrder(String orderId) {
        ChargeMachineOrderDto orderQuery = new ChargeMachineOrderDto();
        orderQuery.setOrderId(orderId);
        orderQuery.setStatusCd("0");
        orderQuery.setPage(-1);
        List<ChargeMachineOrderDto> orders = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrders(orderQuery);
        if (ListUtil.isNull(orders)) {
            return null;
        }
        return orders.get(0);
    }

    /**
     * 订单是否已开票（存在 state=DONE 的开票单明细）
     */
    private boolean isOrderInvoiced(String orderId) {
        InvoiceOrderItemDto itemQuery = new InvoiceOrderItemDto();
        itemQuery.setOrderId(orderId);
        itemQuery.setStatusCd("0");
        itemQuery.setPage(-1);
        List<InvoiceOrderItemDto> items = invoiceOrderItemV1InnerServiceSMOImpl.queryInvoiceOrderItems(itemQuery);
        if (ListUtil.isNull(items)) {
            return false;
        }
        Set<String> invoiceOrderIds = new HashSet<>();
        for (InvoiceOrderItemDto item : items) {
            invoiceOrderIds.add(item.getInvoiceOrderId());
        }
        InvoiceOrderDto invoiceOrderQuery = new InvoiceOrderDto();
        invoiceOrderQuery.setInvoiceOrderIds(invoiceOrderIds.toArray(new String[0]));
        invoiceOrderQuery.setState(InvoiceOrderDto.STATE_DONE);
        invoiceOrderQuery.setStatusCd("0");
        invoiceOrderQuery.setPage(-1);
        List<InvoiceOrderDto> invoiceOrders = invoiceOrderV1InnerServiceSMOImpl.queryInvoiceOrders(invoiceOrderQuery);
        return !ListUtil.isNull(invoiceOrders);
    }

    private void updateInvoiceOrderFail(String invoiceOrderId, String failReason) {
        InvoiceOrderPo updatePo = new InvoiceOrderPo();
        updatePo.setInvoiceOrderId(invoiceOrderId);
        updatePo.setState(InvoiceOrderDto.STATE_FAIL);
        updatePo.setFailReason(failReason);
        invoiceOrderV1InnerServiceSMOImpl.updateInvoiceOrder(updatePo);
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
