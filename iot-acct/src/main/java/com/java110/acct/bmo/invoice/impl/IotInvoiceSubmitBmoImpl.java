package com.java110.acct.bmo.invoice.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.acct.bmo.invoice.IIotInvoiceSubmitBmo;
import com.java110.acct.mall.ISendMall;
import com.java110.bean.ResultVo;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.chargeMachine.ChargeMachineOrderDto;
import com.java110.dto.invoice.InvoiceOrderDto;
import com.java110.dto.invoice.InvoiceOrderItemDto;
import com.java110.intf.acct.IInvoiceOrderItemV1InnerServiceSMO;
import com.java110.intf.acct.IInvoiceOrderV1InnerServiceSMO;
import com.java110.intf.charge.IChargeMachineOrderV1InnerServiceSMO;
import com.java110.po.invoice.InvoiceOrderItemPo;
import com.java110.po.invoice.InvoiceOrderPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 物联网提交开票编排实现（PC 管理端与手机端共用，逻辑与物业一致）
 * 生成物联网开票单号(=商城 ext_apply_id) → 校验订单 → 调商城 iotInvoiceBmoImpl（抬头随报文 upsert）
 * → 商城返回开票单号才落 invoice_order/invoice_order_item；商城未建单零落库并返回 state=F + failReason
 */
@Service
public class IotInvoiceSubmitBmoImpl implements IIotInvoiceSubmitBmo {

    private static final Logger logger = LoggerFactory.getLogger(IotInvoiceSubmitBmoImpl.class);

    private static final String MALL_API_CODE_INVOICE = "iotInvoiceBmoImpl";
    private static final String MALL_URL = "/mall/api/common.openCommonApi";
    private static final String ORG_TYPE_COMMUNITY = "C";
    private static final String INVOICE_TYPE_NORMAL = "普票";
    private static final String ID_PREFIX = "CINV";
    private static final String ITEM_ID_PREFIX = "CINI";
    private static final String ORDER_ID_PREFIX = "CHARGE:";
    private static final String STATE_SUBMIT_FAIL = "F";
    private static final String FAIL_REASON_MALL_CLOSED = "商城系统未开启或未部署，无法开票";
    private static final String FAIL_REASON_CALL_MALL = "调用商城开票失败";
    private static final String FAIL_REASON_NO_INVOICE_ID = "商城未生成开票单";
    private static final String FAIL_REASON_NO_CONTENT = "商城未返回开票结果";
    private static final String FAIL_REASON_SQL_NOISE = "商城开票调用失败(详见服务日志)";
    private static final int REMARK_MAX_LEN = 200;
    private static final String[] SQL_NOISE_KEYWORDS = {"### Error updating database", "### SQL:", "nested exception"};

    @Autowired
    private IChargeMachineOrderV1InnerServiceSMO chargeMachineOrderV1InnerServiceSMOImpl;

    @Autowired
    private IInvoiceOrderV1InnerServiceSMO invoiceOrderV1InnerServiceSMOImpl;

    @Autowired
    private IInvoiceOrderItemV1InnerServiceSMO invoiceOrderItemV1InnerServiceSMOImpl;

    @Autowired
    private ISendMall sendMallImpl;

    @Override
    public JSONObject submitInvoice(JSONObject paramIn) throws CmdException {
        String communityId = paramIn.getString("communityId");
        String userId = paramIn.getString("userId");
        String phone = paramIn.getString("phone");
        String bizCategory = paramIn.getString("bizCategory");
        if (StringUtil.isNullOrNone(bizCategory)) {
            bizCategory = InvoiceOrderDto.BIZ_CATEGORY_CHARGE;
        }
        String invoiceType = paramIn.getString("invoiceType");
        if (StringUtil.isNullOrNone(invoiceType)) {
            invoiceType = INVOICE_TYPE_NORMAL;
        }

        // ① 校验订单（存在、归属手机号、小区一致、已完成、金额>0、未开票）并汇总金额
        List<String> orderIds = parseOrderIds(paramIn);
        if (orderIds.isEmpty()) {
            throw new CmdException("请先勾选需要开票的订单");
        }
        Set<String> invoicedOrderIds = queryInvoicedOrderIds(communityId, phone);
        List<ChargeMachineOrderDto> orders = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (String orderId : orderIds) {
            ChargeMachineOrderDto order = recheckOrder(orderId, communityId, phone, invoicedOrderIds);
            orders.add(order);
            totalAmount = totalAmount.add(toBigDecimal(order.getAmount()));
        }
        String amount = totalAmount.setScale(2, RoundingMode.HALF_UP).toPlainString();

        // ② 开票单号同时作为商城幂等键(ext_apply_id)
        String invoiceOrderId = GenerateCodeFactory.getGeneratorId(ID_PREFIX);

        // ③ 先调商城：商城不可用或未建单时零落库，失败原因原样返回
        JSONObject mallResult = callMall(buildMallParam(invoiceOrderId, paramIn, communityId, phone,
                bizCategory, invoiceType, amount, orderIds));
        if (mallResult.getIntValue("code") != ResultVo.CODE_OK) {
            return submitFailResult(invoiceOrderId, safeFailReason(mallResult.getString("msg"), FAIL_REASON_CALL_MALL));
        }
        JSONObject invoiceData = mallResult.getJSONObject("data");
        if (invoiceData == null) {
            return submitFailResult(invoiceOrderId, FAIL_REASON_MALL_CLOSED);
        }
        if (StringUtil.isNullOrNone(invoiceData.getString("invoiceId"))) {
            return submitFailResult(invoiceOrderId,
                    safeFailReason(invoiceData.getString("failReason"), FAIL_REASON_NO_INVOICE_ID));
        }

        // ④ 商城已建单才落物联网开票记录（发票PDF只存商城）
        saveInvoiceOrder(invoiceOrderId, userId, phone, communityId, paramIn, bizCategory, invoiceType, amount,
                orders);
        updateMallState(invoiceOrderId, invoiceData);

        return invoiceResult(invoiceOrderId, invoiceData);
    }

    @Override
    public JSONObject reSubmitInvoice(String communityId, String invoiceOrderId) throws CmdException {
        InvoiceOrderDto invoiceOrder = getInvoiceOrder(communityId, invoiceOrderId);
        if (invoiceOrder == null) {
            throw new CmdException("开票单不存在");
        }
        if (InvoiceOrderDto.STATE_DONE.equals(invoiceOrder.getState())
                || InvoiceOrderDto.STATE_RED.equals(invoiceOrder.getState())) {
            throw new CmdException("该开票单已开票或已红冲，无需重新提交");
        }

        JSONObject paramIn = new JSONObject();
        paramIn.put("headingType", invoiceOrder.getHeadingType());
        paramIn.put("headingName", invoiceOrder.getBuyerName());
        paramIn.put("taxNo", invoiceOrder.getBuyerTaxNo());
        paramIn.put("buyerAddress", invoiceOrder.getBuyerAddress());
        paramIn.put("buyerBankNo", invoiceOrder.getBuyerBankNo());
        paramIn.put("email", invoiceOrder.getEmail());
        paramIn.put("remark", invoiceOrder.getRemark());

        JSONObject mallResult = callMall(buildMallParam(invoiceOrderId, paramIn, communityId, invoiceOrder.getPhone(),
                invoiceOrder.getBizCategory(), invoiceOrder.getInvoiceType(), invoiceOrder.getAmount(),
                new ArrayList<>()));
        if (mallResult.getIntValue("code") != ResultVo.CODE_OK) {
            return submitFailResult(invoiceOrderId, safeFailReason(mallResult.getString("msg"), FAIL_REASON_CALL_MALL));
        }
        JSONObject invoiceData = mallResult.getJSONObject("data");
        if (invoiceData == null) {
            return submitFailResult(invoiceOrderId, FAIL_REASON_MALL_CLOSED);
        }
        if (StringUtil.isNullOrNone(invoiceData.getString("invoiceId"))) {
            return submitFailResult(invoiceOrderId,
                    safeFailReason(invoiceData.getString("failReason"), FAIL_REASON_NO_CONTENT));
        }

        // 商城按 ext_apply_id 幂等重开，回写最新发票号/PDF/状态
        updateMallState(invoiceOrderId, invoiceData);
        return invoiceResult(invoiceOrderId, invoiceData);
    }

    @Override
    public List<ChargeMachineOrderDto> queryUnInvoicedOrders(String communityId, String phone) throws CmdException {
        Set<String> invoicedOrderIds = queryInvoicedOrderIds(communityId, phone);

        ChargeMachineOrderDto orderQuery = new ChargeMachineOrderDto();
        orderQuery.setPersonTel(phone);
        orderQuery.setCommunityId(communityId);
        orderQuery.setState(ChargeMachineOrderDto.STATE_FINISH);
        orderQuery.setStatusCd("0");
        orderQuery.setPage(-1);
        List<ChargeMachineOrderDto> orders = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrders(orderQuery);
        if (ListUtil.isNull(orders)) {
            return new ArrayList<>();
        }
        List<ChargeMachineOrderDto> unInvoicedOrders = new ArrayList<>();
        for (ChargeMachineOrderDto order : orders) {
            if (invoicedOrderIds.contains(order.getOrderId())) {
                continue;
            }
            if (toBigDecimal(order.getAmount()).compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            unInvoicedOrders.add(order);
        }
        return unInvoicedOrders;
    }

    /**
     * 单笔订单开票校验（存在、归属手机号、小区一致、已完成、金额>0、未开票）
     */
    private ChargeMachineOrderDto recheckOrder(String orderId, String communityId, String phone,
                                               Set<String> invoicedOrderIds) {
        ChargeMachineOrderDto order = getChargeMachineOrder(orderId);
        if (order == null) {
            throw new CmdException("订单" + orderId + "不存在");
        }
        if (!phone.equals(order.getPersonTel())) {
            throw new CmdException("订单" + orderId + "不属于该手机号，无法开票");
        }
        if (!communityId.equals(order.getCommunityId())) {
            throw new CmdException("订单" + orderId + "与当前小区不一致");
        }
        if (!ChargeMachineOrderDto.STATE_FINISH.equals(order.getState())) {
            throw new CmdException("订单" + orderId + "未完成，不能开票");
        }
        if (toBigDecimal(order.getAmount()).compareTo(BigDecimal.ZERO) <= 0) {
            throw new CmdException("订单" + orderId + "无实付金额，不参与开票");
        }
        if (invoicedOrderIds.contains(orderId)) {
            throw new CmdException("订单" + orderId + "已开票，请勿重复提交");
        }
        return order;
    }

    /**
     * 组装商城开票报文：抬头随报文 upsert（buyerTel 即抬头归属手机号），明细按业务类目汇总
     */
    private JSONObject buildMallParam(String invoiceOrderId, JSONObject paramIn, String communityId, String phone,
                                      String bizCategory, String invoiceType, String amount, List<String> orderIds) {
        JSONObject mallParam = new JSONObject();
        mallParam.put("mallApiCode", MALL_API_CODE_INVOICE);
        mallParam.put("orgId", communityId);
        mallParam.put("orgType", ORG_TYPE_COMMUNITY);
        mallParam.put("applyId", invoiceOrderId);
        mallParam.put("headingType", paramIn.getString("headingType"));
        mallParam.put("buyerName", paramIn.getString("headingName"));
        mallParam.put("buyerTaxNo", nvl(paramIn.getString("taxNo")));
        mallParam.put("buyerTel", phone);
        mallParam.put("buyerAddress", nvl(paramIn.getString("buyerAddress")));
        mallParam.put("buyerBankNo", nvl(paramIn.getString("buyerBankNo")));
        mallParam.put("email", nvl(paramIn.getString("email")));
        mallParam.put("invoiceType", invoiceType);
        mallParam.put("amount", amount);
        mallParam.put("bizCategory", bizCategory);
        mallParam.put("remark", buildMallRemark(paramIn.getString("remark"), orderIds));

        JSONArray items = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("bizCategory", bizCategory);
        item.put("amount", amount);
        items.add(item);
        mallParam.put("items", items);
        return mallParam;
    }

    /**
     * 商城开票单备注：业务前缀 + 充电订单号（便于商城侧追溯），有用户备注时追加
     */
    private String buildMallRemark(String remark, List<String> orderIds) {
        String text = orderIds.isEmpty() ? "" : ORDER_ID_PREFIX + String.join(",", orderIds);
        if (!StringUtil.isNullOrNone(remark)) {
            text = StringUtil.isNullOrNone(text) ? remark : text + " " + remark;
        }
        return safeRemark(text);
    }

    /**
     * 调用商城开票：MALL_SWITCH 未开启时返回 code=0 + data 为空
     */
    private JSONObject callMall(JSONObject mallParam) {
        ResultVo resultVo;
        try {
            resultVo = sendMallImpl.post(MALL_URL, mallParam);
        } catch (Exception e) {
            logger.error("调用商城开票异常 applyId: {}", mallParam.getString("applyId"), e);
            return errorResult(safeFailReason(e.getMessage(), FAIL_REASON_CALL_MALL));
        }
        if (resultVo == null) {
            return errorResult(FAIL_REASON_NO_CONTENT);
        }
        JSONObject result = new JSONObject();
        result.put("code", resultVo.getCode());
        result.put("msg", resultVo.getMsg());
        if (resultVo.getData() != null) {
            result.put("data", JSONObject.parseObject(JSONObject.toJSONString(resultVo.getData())));
        }
        return result;
    }

    /**
     * 落本地开票记录：invoice_order + invoice_order_item（商城已建单后调用）
     */
    private void saveInvoiceOrder(String invoiceOrderId, String userId, String phone, String communityId,
                                  JSONObject paramIn, String bizCategory, String invoiceType, String amount,
                                  List<ChargeMachineOrderDto> orders) {
        InvoiceOrderPo invoiceOrderPo = new InvoiceOrderPo();
        invoiceOrderPo.setInvoiceOrderId(invoiceOrderId);
        invoiceOrderPo.setUserId(userId);
        invoiceOrderPo.setPhone(phone);
        invoiceOrderPo.setCommunityId(communityId);
        invoiceOrderPo.setHeadingType(paramIn.getString("headingType"));
        invoiceOrderPo.setBuyerName(paramIn.getString("headingName"));
        invoiceOrderPo.setBuyerTaxNo(paramIn.getString("taxNo"));
        invoiceOrderPo.setBuyerAddress(paramIn.getString("buyerAddress"));
        invoiceOrderPo.setBuyerBankNo(paramIn.getString("buyerBankNo"));
        invoiceOrderPo.setEmail(paramIn.getString("email"));
        invoiceOrderPo.setInvoiceType(invoiceType);
        invoiceOrderPo.setBizCategory(bizCategory);
        invoiceOrderPo.setAmount(amount);
        invoiceOrderPo.setState(InvoiceOrderDto.STATE_WAIT);
        invoiceOrderPo.setRemark(safeRemark(nvl(paramIn.getString("remark"))));
        int saveFlag = invoiceOrderV1InnerServiceSMOImpl.saveInvoiceOrder(invoiceOrderPo);
        if (saveFlag < 1) {
            throw new CmdException("保存开票单失败");
        }

        int seq = 1;
        for (ChargeMachineOrderDto order : orders) {
            InvoiceOrderItemPo itemPo = new InvoiceOrderItemPo();
            itemPo.setItemId(GenerateCodeFactory.getGeneratorId(ITEM_ID_PREFIX));
            itemPo.setInvoiceOrderId(invoiceOrderId);
            itemPo.setOrderId(order.getOrderId());
            itemPo.setCommunityId(communityId);
            itemPo.setBizCategory(bizCategory);
            itemPo.setAmount(toBigDecimal(order.getAmount()).setScale(2, RoundingMode.HALF_UP).toPlainString());
            itemPo.setSeq(seq + "");
            if (invoiceOrderItemV1InnerServiceSMOImpl.saveInvoiceOrderItem(itemPo) < 1) {
                throw new CmdException("保存开票明细失败");
            }
            seq++;
        }
    }

    /**
     * 按商城返回结果回写本地开票记录状态与发票信息
     */
    private void updateMallState(String invoiceOrderId, JSONObject invoiceData) {
        InvoiceOrderPo updatePo = new InvoiceOrderPo();
        updatePo.setInvoiceOrderId(invoiceOrderId);
        updatePo.setInvoiceId(invoiceData.getString("invoiceId"));
        updatePo.setState(invoiceData.getString("state"));
        updatePo.setInvoiceNo(invoiceData.getString("invoiceNo"));
        updatePo.setInvoiceCode(invoiceData.getString("invoiceCode"));
        updatePo.setPdfUrl(invoiceData.getString("pdfUrl"));
        if (InvoiceOrderDto.STATE_FAIL.equals(invoiceData.getString("state"))) {
            updatePo.setFailReason(safeFailReason(invoiceData.getString("failReason"), null));
        }
        invoiceOrderV1InnerServiceSMOImpl.updateInvoiceOrder(updatePo);
    }

    /**
     * 本地有效开票明细订单集合（开票单未删除且状态非 FAIL；FAIL 单可重新提交故不占用订单）
     */
    private Set<String> queryInvoicedOrderIds(String communityId, String phone) {
        InvoiceOrderDto invoiceOrderQuery = new InvoiceOrderDto();
        invoiceOrderQuery.setCommunityId(communityId);
        if (!StringUtil.isNullOrNone(phone)) {
            invoiceOrderQuery.setPhone(phone);
        }
        invoiceOrderQuery.setStatusCd("0");
        invoiceOrderQuery.setPage(-1);
        List<InvoiceOrderDto> invoiceOrders = invoiceOrderV1InnerServiceSMOImpl.queryInvoiceOrders(invoiceOrderQuery);
        if (ListUtil.isNull(invoiceOrders)) {
            return new HashSet<>();
        }
        List<String> invoiceOrderIds = new ArrayList<>();
        for (InvoiceOrderDto invoiceOrder : invoiceOrders) {
            if (InvoiceOrderDto.STATE_FAIL.equals(invoiceOrder.getState())) {
                continue;
            }
            invoiceOrderIds.add(invoiceOrder.getInvoiceOrderId());
        }
        if (invoiceOrderIds.isEmpty()) {
            return new HashSet<>();
        }

        InvoiceOrderItemDto itemQuery = new InvoiceOrderItemDto();
        itemQuery.setInvoiceOrderIds(invoiceOrderIds.toArray(new String[0]));
        itemQuery.setStatusCd("0");
        itemQuery.setPage(-1);
        List<InvoiceOrderItemDto> items = invoiceOrderItemV1InnerServiceSMOImpl.queryInvoiceOrderItems(itemQuery);
        Set<String> orderIds = new HashSet<>();
        if (ListUtil.isNull(items)) {
            return orderIds;
        }
        for (InvoiceOrderItemDto item : items) {
            if (!StringUtil.isNullOrNone(item.getOrderId())) {
                orderIds.add(item.getOrderId());
            }
        }
        return orderIds;
    }

    private InvoiceOrderDto getInvoiceOrder(String communityId, String invoiceOrderId) {
        InvoiceOrderDto invoiceOrderQuery = new InvoiceOrderDto();
        invoiceOrderQuery.setInvoiceOrderId(invoiceOrderId);
        invoiceOrderQuery.setCommunityId(communityId);
        invoiceOrderQuery.setStatusCd("0");
        invoiceOrderQuery.setPage(-1);
        List<InvoiceOrderDto> invoiceOrders = invoiceOrderV1InnerServiceSMOImpl.queryInvoiceOrders(invoiceOrderQuery);
        if (ListUtil.isNull(invoiceOrders)) {
            return null;
        }
        return invoiceOrders.get(0);
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
     * 勾选订单号去重（保持勾选顺序）
     */
    private List<String> parseOrderIds(JSONObject paramIn) {
        JSONArray orderIdArray = paramIn.getJSONArray("orderIds");
        if (orderIdArray == null || orderIdArray.isEmpty()) {
            return new ArrayList<>();
        }
        Set<String> orderIdSet = new LinkedHashSet<>();
        for (int i = 0; i < orderIdArray.size(); i++) {
            String orderId = orderIdArray.getString(i);
            if (StringUtil.isNullOrNone(orderId)) {
                continue;
            }
            orderIdSet.add(orderId);
        }
        return new ArrayList<>(orderIdSet);
    }

    /**
     * 商城未建单：零落库，返回 state=F + failReason 由前端提示
     */
    private JSONObject submitFailResult(String invoiceOrderId, String failReason) {
        logger.warn("商城未建单, 物联网侧零落库 applyId: {}, failReason: {}", invoiceOrderId, failReason);
        JSONObject result = new JSONObject();
        result.put("invoiceOrderId", invoiceOrderId);
        result.put("state", STATE_SUBMIT_FAIL);
        result.put("failReason", failReason);
        return result;
    }

    private JSONObject invoiceResult(String invoiceOrderId, JSONObject invoiceData) {
        JSONObject result = new JSONObject();
        result.put("invoiceOrderId", invoiceOrderId);
        result.put("invoiceId", invoiceData.getString("invoiceId"));
        result.put("state", invoiceData.getString("state"));
        result.put("invoiceNo", invoiceData.getString("invoiceNo"));
        result.put("invoiceCode", invoiceData.getString("invoiceCode"));
        result.put("pdfUrl", invoiceData.getString("pdfUrl"));
        result.put("failReason", InvoiceOrderDto.STATE_FAIL.equals(invoiceData.getString("state"))
                ? invoiceData.getString("failReason") : null);
        return result;
    }

    private JSONObject errorResult(String msg) {
        JSONObject result = new JSONObject();
        result.put("code", ResultVo.CODE_ERROR);
        result.put("msg", msg);
        return result;
    }

    private BigDecimal toBigDecimal(String amount) {
        if (StringUtil.isNullOrNone(amount)) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(amount);
        } catch (Exception e) {
            logger.error("金额转换失败 amount: {}", amount);
            return BigDecimal.ZERO;
        }
    }

    private String nvl(String text) {
        return text == null ? "" : text;
    }

    /**
     * 所有列宽受限字段统一：压缩连续空白 + 截断至列宽以内
     */
    private String safeRemark(String text) {
        if (StringUtil.isNullOrNone(text)) {
            return text;
        }
        String summary = text.replaceAll("\\s+", " ").trim();
        return summary.length() > REMARK_MAX_LEN ? summary.substring(0, REMARK_MAX_LEN) : summary;
    }

    /**
     * 失败原因摘要化：原文入日志，含 MyBatis/SQL 堆栈时仅保留首个可读片段，再按列宽截断
     */
    private String safeFailReason(String reason, String fallback) {
        if (StringUtil.isNullOrNone(reason)) {
            return fallback;
        }
        logger.warn("商城开票失败原因(原文): {}", reason);
        String summary = reason;
        if (containsSqlNoise(reason)) {
            summary = firstReadableFragment(reason);
        }
        return safeRemark(summary);
    }

    private boolean containsSqlNoise(String text) {
        for (String keyword : SQL_NOISE_KEYWORDS) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String firstReadableFragment(String reason) {
        String[] fragments = reason.split("###");
        for (String fragment : fragments) {
            String text = fragment.replaceAll("\\s+", " ").trim();
            if (text.isEmpty()) {
                continue;
            }
            if (text.startsWith("The error may") || text.startsWith("SQL:") || text.startsWith("Cause:")) {
                continue;
            }
            return text;
        }
        return FAIL_REASON_SQL_NOISE;
    }
}
