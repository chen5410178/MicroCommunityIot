package com.java110.acct.cmd.invoice;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.java110.core.utils.ListUtil;
import com.java110.dto.user.UserDto;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

/**
 * 手机端提交开票（iot-h5/iot-user 电子发票）
 * 服务编码：invoice.appOpenInvoice
 * 归属手机号强制取登录用户（忽略入参 phone，防越权），内部与 PC 管理端 invoice.saveInvoiceOrder 共用
 * IIotInvoiceSubmitBmo：先调商城 iotInvoiceBmoImpl（抬头随报文 upsert）→ 商城建单才落 invoice_order/invoice_order_item，
 * 未建单则零落库并返回 state=F + failReason
 */
@Java110Cmd(serviceCode = "invoice.appOpenInvoice")
public class AppOpenInvoiceCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(AppOpenInvoiceCmd.class);

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IIotInvoiceSubmitBmo iotInvoiceSubmitBmoImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "请先选择小区");
        Assert.hasKeyAndValue(reqJson, "headingType", "未包含抬头类型");
        Assert.hasKeyAndValue(reqJson, "headingName", "未包含抬头名称");
        JSONArray orderIds = reqJson.getJSONArray("orderIds");
        if (orderIds == null || orderIds.isEmpty()) {
            throw new CmdException("请先勾选需要开票的订单");
        }
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
        // 归属手机号一律取登录用户，不接受入参手机号
        reqJson.put("phone", userDtos.get(0).getTel());
        reqJson.put("userId", userId);

        JSONObject data = iotInvoiceSubmitBmoImpl.submitInvoice(reqJson);

        context.setResponseEntity(ResultVo.createResponseEntity(ResultVo.CODE_OK, ResultVo.MSG_OK, data));
    }
}
