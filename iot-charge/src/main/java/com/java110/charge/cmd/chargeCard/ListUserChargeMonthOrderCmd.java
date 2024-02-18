package com.java110.charge.cmd.chargeCard;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.CmdContextUtils;
import com.java110.dto.chargeMonthOrder.ChargeMonthOrderDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.charge.IChargeMonthOrderV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

/**
 * 查询充电月卡记录
 */
@Java110Cmd(serviceCode = "chargeCard.listUserChargeMonthOrder")
public class ListUserChargeMonthOrderCmd extends Cmd {

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMonthOrderV1InnerServiceSMO chargeMonthOrderV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String userId = CmdContextUtils.getUserId(context);

        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区");

        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        Assert.listOnlyOne(userDtos, "用户不存在");

        reqJson.put("link", userDtos.get(0).getTel());
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {


        List<ChargeMonthOrderDto> chargeMonthOrderDtos = null;

        ChargeMonthOrderDto chargeMonthOrderDto = new ChargeMonthOrderDto();
        chargeMonthOrderDto.setPersonTel(reqJson.getString("link"));
        chargeMonthOrderDto.setPage(1);
        chargeMonthOrderDto.setRow(30);
        chargeMonthOrderDtos = chargeMonthOrderV1InnerServiceSMOImpl.queryChargeMonthOrders(chargeMonthOrderDto);

        context.setResponseEntity(ResultVo.createResponseEntity(chargeMonthOrderDtos));
    }
}
