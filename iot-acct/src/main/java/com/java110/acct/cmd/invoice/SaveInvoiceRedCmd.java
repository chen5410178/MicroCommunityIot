package com.java110.acct.cmd.invoice;

import com.alibaba.fastjson.JSONObject;
import com.java110.acct.bmo.invoice.IInvoiceAdminBmo;
import com.java110.acct.bmo.invoice.IIotInvoiceRedBmo;
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
 * 发起发票红冲（PC 管理端房产-发票管理）
 * 服务编码：invoice.saveInvoiceRed
 * 校验开票单归属当前小区后转发商城 iotInvoiceRedBmoImpl 执行：商城落 invoice_red + 原单置 RED 并同步红字开票
 */
@Java110Cmd(serviceCode = "invoice.saveInvoiceRed")
public class SaveInvoiceRedCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(SaveInvoiceRedCmd.class);

    @Autowired
    private IIotInvoiceRedBmo iotInvoiceRedBmoImpl;

    @Autowired
    private IInvoiceAdminBmo invoiceAdminBmoImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "请先选择小区");
        Assert.hasKeyAndValue(reqJson, "invoiceOrderId", "未包含开票单号");
        Assert.hasKeyAndValue(reqJson, "reason", "未包含冲红原因");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String communityId = reqJson.getString("communityId");
        invoiceAdminBmoImpl.validateCommunityAdmin(CmdContextUtils.getStoreId(context),
                CmdContextUtils.getLoginUserId(context), communityId);

        JSONObject data = iotInvoiceRedBmoImpl.saveInvoiceRed(reqJson);

        context.setResponseEntity(ResultVo.createResponseEntity(ResultVo.CODE_OK, ResultVo.MSG_OK, data));
    }
}
