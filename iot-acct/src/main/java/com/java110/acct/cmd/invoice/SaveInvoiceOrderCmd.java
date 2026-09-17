package com.java110.acct.cmd.invoice;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.acct.bmo.invoice.IInvoiceAdminBmo;
import com.java110.acct.bmo.invoice.IIotInvoiceSubmitBmo;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
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
 * 管理端提交开票（PC 管理端房产-发票管理）
 * 服务编码：invoice.saveInvoiceOrder
 * 处理逻辑与物业一致：按业主手机号校验订单 → 组装抬头+明细 → 先调商城 iotInvoiceBmoImpl（抬头随报文 upsert，
 * ext_apply_id=物联网开票单号）→ 商城返回 invoiceId 才落 invoice_order/invoice_order_item；
 * 商城未建单则零落库，返回 state=F + failReason 由前端原样提示
 */
@Java110Cmd(serviceCode = "invoice.saveInvoiceOrder")
public class SaveInvoiceOrderCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(SaveInvoiceOrderCmd.class);

    private static final String PHONE_REGEX = "^1\\d{10}$";

    @Autowired
    private IIotInvoiceSubmitBmo iotInvoiceSubmitBmoImpl;

    @Autowired
    private IInvoiceAdminBmo invoiceAdminBmoImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "请先选择小区");
        Assert.hasKeyAndValue(reqJson, "phone", "未包含开票手机号");
        Assert.hasKeyAndValue(reqJson, "headingType", "未包含抬头类型");
        Assert.hasKeyAndValue(reqJson, "headingName", "未包含抬头名称");
        JSONArray orderIds = reqJson.getJSONArray("orderIds");
        if (orderIds == null || orderIds.isEmpty()) {
            throw new CmdException("请先勾选需要开票的订单");
        }
        if (!Pattern.matches(PHONE_REGEX, reqJson.getString("phone"))) {
            throw new CmdException("手机号格式错误");
        }
        if ("公司".equals(reqJson.getString("headingType"))) {
            Assert.hasKeyAndValue(reqJson, "taxNo", "企业抬头必须填写税号");
        }
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String communityId = reqJson.getString("communityId");
        invoiceAdminBmoImpl.validateCommunityAdmin(CmdContextUtils.getStoreId(context),
                CmdContextUtils.getLoginUserId(context), communityId);

        // 归属用户取当前操作人（PC 端可按任意手机号代业主开票，手机号即订单与抬头归属维度）
        reqJson.put("userId", CmdContextUtils.getLoginUserId(context));

        JSONObject data = iotInvoiceSubmitBmoImpl.submitInvoice(reqJson);

        context.setResponseEntity(ResultVo.createResponseEntity(ResultVo.CODE_OK, ResultVo.MSG_OK, data));
    }
}
