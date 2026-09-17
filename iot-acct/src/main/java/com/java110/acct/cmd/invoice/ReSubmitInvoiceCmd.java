package com.java110.acct.cmd.invoice;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;

/**
 * 开票失败单重新提交（PC 管理端房产-发票管理）
 * 服务编码：invoice.reSubmitInvoice
 * 以本地开票单号作为商城幂等键（ext_apply_id）重开：商城建单/重开成功才回写本地发票号与状态
 */
@Java110Cmd(serviceCode = "invoice.reSubmitInvoice")
public class ReSubmitInvoiceCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ReSubmitInvoiceCmd.class);

    @Autowired
    private IIotInvoiceSubmitBmo iotInvoiceSubmitBmoImpl;

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
        invoiceAdminBmoImpl.validateCommunityAdmin(CmdContextUtils.getStoreId(context),
                CmdContextUtils.getLoginUserId(context), communityId);

        JSONObject data = iotInvoiceSubmitBmoImpl.reSubmitInvoice(communityId, reqJson.getString("invoiceOrderId"));

        context.setResponseEntity(ResultVo.createResponseEntity(ResultVo.CODE_OK, ResultVo.MSG_OK, data));
    }
}
