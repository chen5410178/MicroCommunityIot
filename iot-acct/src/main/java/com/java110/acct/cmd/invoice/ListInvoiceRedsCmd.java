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
 * 发票红冲列表（PC 管理端房产-发票管理）
 * 服务编码：invoice.listInvoiceReds
 * 红冲数据直接读取商城（物联网侧零落库），商城未开启/异常时返回空分页
 */
@Java110Cmd(serviceCode = "invoice.listInvoiceReds")
public class ListInvoiceRedsCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ListInvoiceRedsCmd.class);

    @Autowired
    private IIotInvoiceRedBmo iotInvoiceRedBmoImpl;

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

        JSONObject data = iotInvoiceRedBmoImpl.queryInvoiceReds(reqJson);

        context.setResponseEntity(ResultVo.createResponseEntity(data.getIntValue("records"),
                data.getIntValue("total"), data.getJSONArray("data")));
    }
}
