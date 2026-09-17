package com.java110.acct.cmd.invoice;

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
import com.java110.core.utils.ListUtil;
import com.java110.dto.invoice.InvoiceOrderDto;
import com.java110.intf.acct.IInvoiceOrderV1InnerServiceSMO;
import com.java110.po.invoice.InvoiceOrderPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 删除未成功开票记录（PC 管理端房产-发票管理）
 * 服务编码：invoice.deleteInvoiceOrder
 * 仅软删除本地开票记录（status_cd=1）；已开票/已红冲（商城状态为权威源）不允许删除，商城侧开票单不做删除
 */
@Java110Cmd(serviceCode = "invoice.deleteInvoiceOrder")
public class DeleteInvoiceOrderCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(DeleteInvoiceOrderCmd.class);

    @Autowired
    private IInvoiceOrderV1InnerServiceSMO invoiceOrderV1InnerServiceSMOImpl;

    @Autowired
    private IInvoiceStateSyncBmo invoiceStateSyncBmoImpl;

    @Autowired
    private IInvoiceAdminBmo invoiceAdminBmoImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "请先选择小区");
        Assert.hasKeyAndValue(reqJson, "invoiceOrderId", "未包含开票单号");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String communityId = reqJson.getString("communityId");
        String invoiceOrderId = reqJson.getString("invoiceOrderId");
        invoiceAdminBmoImpl.validateCommunityAdmin(CmdContextUtils.getStoreId(context),
                CmdContextUtils.getLoginUserId(context), communityId);

        InvoiceOrderDto invoiceOrder = getInvoiceOrder(communityId, invoiceOrderId);
        if (invoiceOrder == null) {
            throw new CmdException("开票单不存在");
        }
        if (isInvoicedInMall(invoiceOrderId) || InvoiceOrderDto.STATE_DONE.equals(invoiceOrder.getState())
                || InvoiceOrderDto.STATE_RED.equals(invoiceOrder.getState())) {
            throw new CmdException("该开票单已开票或已红冲，不能删除");
        }

        InvoiceOrderPo invoiceOrderPo = new InvoiceOrderPo();
        invoiceOrderPo.setInvoiceOrderId(invoiceOrderId);
        invoiceOrderPo.setStatusCd("1");
        int updateFlag = invoiceOrderV1InnerServiceSMOImpl.updateInvoiceOrder(invoiceOrderPo);
        if (updateFlag < 1) {
            throw new CmdException("删除开票单失败");
        }

        context.setResponseEntity(ResultVo.createResponseEntity(ResultVo.CODE_OK, ResultVo.MSG_OK,
                invoiceOrderId));
    }

    /**
     * 商城已开票/已红冲（商城状态为权威源，回读不到时按本地状态判定）
     */
    private boolean isInvoicedInMall(String invoiceOrderId) {
        List<String> invoiceOrderIds = new ArrayList<>();
        invoiceOrderIds.add(invoiceOrderId);
        Map<String, JSONObject> mallStates = invoiceStateSyncBmoImpl.queryMallStates(invoiceOrderIds);
        JSONObject mallState = mallStates.get(invoiceOrderId);
        if (mallState == null) {
            return false;
        }
        return InvoiceOrderDto.STATE_DONE.equals(mallState.getString("state"))
                || InvoiceOrderDto.STATE_RED.equals(mallState.getString("state"));
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
}
