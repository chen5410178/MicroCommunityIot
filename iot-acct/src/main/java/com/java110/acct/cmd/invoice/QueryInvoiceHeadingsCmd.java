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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.regex.Pattern;

/**
 * 按手机号查询商城发票抬头列表（PC 管理端开票页）
 * 服务编码：invoice.queryInvoiceHeadings
 * 抬头数据以商城为唯一源（只读转发 listInvoiceHeadingsBmoImpl），命中默认抬头由前端优先回填
 */
@Java110Cmd(serviceCode = "invoice.queryInvoiceHeadings")
public class QueryInvoiceHeadingsCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(QueryInvoiceHeadingsCmd.class);

    private static final String PHONE_REGEX = "^1\\d{10}$";

    @Autowired
    private IInvoiceStateSyncBmo invoiceStateSyncBmoImpl;

    @Autowired
    private IInvoiceAdminBmo invoiceAdminBmoImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "请先选择小区");
        Assert.hasKeyAndValue(reqJson, "userPhone", "未包含查询手机号");
        if (!Pattern.matches(PHONE_REGEX, reqJson.getString("userPhone"))) {
            throw new CmdException("手机号格式错误");
        }
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String communityId = reqJson.getString("communityId");
        invoiceAdminBmoImpl.validateCommunityAdmin(CmdContextUtils.getStoreId(context),
                CmdContextUtils.getLoginUserId(context), communityId);

        int page = reqJson.getIntValue("page") < 1 ? 1 : reqJson.getIntValue("page");
        int row = reqJson.getIntValue("row") < 1 ? 20 : reqJson.getIntValue("row");

        JSONObject data = invoiceStateSyncBmoImpl.queryHeadings(reqJson.getString("userPhone"),
                reqJson.getString("keyword"), page, row);

        context.setResponseEntity(ResultVo.createResponseEntity(data.getIntValue("records"),
                data.getIntValue("total"), data.getJSONArray("data")));
    }
}
