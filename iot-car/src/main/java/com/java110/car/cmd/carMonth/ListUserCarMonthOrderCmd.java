package com.java110.car.cmd.carMonth;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.CmdContextUtils;
import com.java110.core.utils.ListUtil;
import com.java110.dto.appUser.AppUserDto;
import com.java110.dto.carMonthOrder.CarMonthOrderDto;
import com.java110.intf.car.ICarMonthOrderV1InnerServiceSMO;
import com.java110.intf.user.IAppUserV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Java110Cmd(serviceCode = "carMonth.listUserCarMonthOrder")
public class ListUserCarMonthOrderCmd extends Cmd {

    @Autowired
    private IAppUserV1InnerServiceSMO appUserV1InnerServiceSMOImpl;

    @Autowired
    private ICarMonthOrderV1InnerServiceSMO carMonthOrderV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区信息");
        String userId = CmdContextUtils.getUserId(context);


        AppUserDto appUserDto = new AppUserDto();
        appUserDto.setUserId(userId);
        appUserDto.setCommunityId(reqJson.getString("communityId"));
        List<AppUserDto> appUserDtos = appUserV1InnerServiceSMOImpl.queryAppUsers(appUserDto);

        if (ListUtil.isNull(appUserDtos)) {
            throw new CmdException("业主未认证");
        }

        reqJson.put("memberId", appUserDtos.get(0).getMemberId());
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        List<CarMonthOrderDto> carMonthOrderDtos = null;
        CarMonthOrderDto carMonthOrderDto = new CarMonthOrderDto();
        carMonthOrderDto.setOwnerId(reqJson.getString("memberId"));

        carMonthOrderDtos = carMonthOrderV1InnerServiceSMOImpl.queryCarMonthOrders(carMonthOrderDto);

        context.setResponseEntity(ResultVo.createResponseEntity(carMonthOrderDtos));
    }
}
