package com.java110.acct.cmd.invoice;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.acct.bmo.invoice.IInvoiceAdminBmo;
import com.java110.acct.bmo.invoice.IInvoiceStateSyncBmo;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.CmdContextUtils;
import com.java110.core.utils.StringUtil;
import com.java110.dto.invoice.InvoiceOrderDto;
import com.java110.intf.acct.IInvoiceOrderV1InnerServiceSMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 开票记录分页查询（PC 管理端房产-发票管理）
 * 服务编码：invoice.listInvoiceOrders
 * 记录取本地 invoice_order，展示状态用商城实时状态回填（商城未开启/异常时降级为本地状态，不阻塞列表）
 */
@Java110Cmd(serviceCode = "invoice.listInvoiceOrders")
public class ListInvoiceOrdersCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ListInvoiceOrdersCmd.class);

    @Autowired
    private IInvoiceOrderV1InnerServiceSMO invoiceOrderV1InnerServiceSMOImpl;

    @Autowired
    private IInvoiceStateSyncBmo invoiceStateSyncBmoImpl;

    @Autowired
    private IInvoiceAdminBmo invoiceAdminBmoImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "请先选择小区");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String communityId = reqJson.getString("communityId");
        invoiceAdminBmoImpl.validateCommunityAdmin(CmdContextUtils.getStoreId(context),
                CmdContextUtils.getLoginUserId(context), communityId);

        int page = reqJson.getIntValue("page") < 1 ? 1 : reqJson.getIntValue("page");
        int row = reqJson.getIntValue("row") < 1 ? 10 : reqJson.getIntValue("row");

        InvoiceOrderDto invoiceOrderQuery = new InvoiceOrderDto();
        invoiceOrderQuery.setCommunityId(communityId);
        invoiceOrderQuery.setPhone(reqJson.getString("phone"));
        invoiceOrderQuery.setBuyerNameLike(reqJson.getString("buyerName"));
        invoiceOrderQuery.setState(reqJson.getString("state"));
        invoiceOrderQuery.setInvoiceNo(reqJson.getString("invoiceNo"));
        invoiceOrderQuery.setBizCategory(reqJson.getString("bizCategory"));
        invoiceOrderQuery.setStatusCd("0");
        invoiceOrderQuery.setPage(page);
        invoiceOrderQuery.setRow(row);

        int total = invoiceOrderV1InnerServiceSMOImpl.queryInvoiceOrdersCount(invoiceOrderQuery);
        List<InvoiceOrderDto> invoiceOrders = new ArrayList<>();
        if (total > 0) {
            invoiceOrders = invoiceOrderV1InnerServiceSMOImpl.queryInvoiceOrders(invoiceOrderQuery);
        }

        // 商城状态批量回读（只读回填展示，不落库）
        List<String> invoiceOrderIds = new ArrayList<>();
        for (InvoiceOrderDto invoiceOrder : invoiceOrders) {
            invoiceOrderIds.add(invoiceOrder.getInvoiceOrderId());
        }
        Map<String, JSONObject> mallStates = invoiceStateSyncBmoImpl.queryMallStates(invoiceOrderIds);

        JSONArray data = new JSONArray();
        for (InvoiceOrderDto invoiceOrder : invoiceOrders) {
            data.add(toInvoiceOrderJson(invoiceOrder, mallStates.get(invoiceOrder.getInvoiceOrderId())));
        }

        int records = (int) Math.ceil((double) total / (double) row);
        context.setResponseEntity(ResultVo.createResponseEntity(records, total, data));
    }

    /**
     * 开票记录转报文：商城状态为权威源，回读不到时沿用本地状态
     */
    private JSONObject toInvoiceOrderJson(InvoiceOrderDto invoiceOrder, JSONObject mallState) {
        JSONObject result = new JSONObject();
        result.put("invoiceOrderId", invoiceOrder.getInvoiceOrderId());
        result.put("phone", invoiceOrder.getPhone());
        result.put("communityId", invoiceOrder.getCommunityId());
        result.put("headingType", invoiceOrder.getHeadingType());
        result.put("buyerName", invoiceOrder.getBuyerName());
        result.put("buyerTaxNo", invoiceOrder.getBuyerTaxNo());
        result.put("buyerAddress", invoiceOrder.getBuyerAddress());
        result.put("buyerBankNo", invoiceOrder.getBuyerBankNo());
        result.put("email", invoiceOrder.getEmail());
        result.put("invoiceType", invoiceOrder.getInvoiceType());
        result.put("bizCategory", invoiceOrder.getBizCategory());
        result.put("amount", invoiceOrder.getAmount());
        result.put("invoiceId", invoiceOrder.getInvoiceId());
        result.put("remark", invoiceOrder.getRemark());
        result.put("createTime", invoiceOrder.getCreateTime());

        String state = invoiceOrder.getState();
        String invoiceNo = invoiceOrder.getInvoiceNo();
        String invoiceCode = invoiceOrder.getInvoiceCode();
        String pdfUrl = invoiceOrder.getPdfUrl();
        String failReason = invoiceOrder.getFailReason();
        if (mallState != null) {
            state = StringUtil.isNullOrNone(mallState.getString("state")) ? state : mallState.getString("state");
            invoiceNo = StringUtil.isNullOrNone(mallState.getString("invoiceNo")) ? invoiceNo : mallState.getString("invoiceNo");
            invoiceCode = StringUtil.isNullOrNone(mallState.getString("invoiceCode")) ? invoiceCode : mallState.getString("invoiceCode");
            pdfUrl = StringUtil.isNullOrNone(mallState.getString("pdfUrl")) ? pdfUrl : mallState.getString("pdfUrl");
            failReason = StringUtil.isNullOrNone(mallState.getString("failReason")) ? failReason : mallState.getString("failReason");
        }
        result.put("state", state);
        result.put("invoiceNo", invoiceNo);
        result.put("invoiceCode", invoiceCode);
        result.put("pdfUrl", pdfUrl);
        result.put("failReason", failReason);
        return result;
    }
}
