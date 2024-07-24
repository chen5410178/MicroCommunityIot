package com.java110.barrier.cmd.carInout;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ListUtil;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.intf.barrier.ICarInoutV1InnerServiceSMO;
import com.java110.po.carInout.CarInoutPo;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

/**
 * 异常出场
 */
@Java110Cmd(serviceCode = "carInout.exceptionCarOut")
public class ExceptionCarOutCmd extends Cmd {

    @Autowired
    private ICarInoutV1InnerServiceSMO carInoutV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.hasKeyAndValue(reqJson, "ciId", "未包含ciId");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {


        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCiId(reqJson.getString("ciId"));
        carInoutDto.setCommunityId(reqJson.getString("communityId"));
        List<CarInoutDto> carInoutDtos = carInoutV1InnerServiceSMOImpl.queryCarInouts(carInoutDto);
        Assert.listOnlyOne(carInoutDtos, "未包含进场记录");

        CarInoutPo carInoutPo = new CarInoutPo();
        carInoutPo.setCiId(reqJson.getString("ciId"));
        carInoutPo.setCommunityId(reqJson.getString("communityId"));
        carInoutPo.setState(CarInoutDto.STATE_OUT);
        carInoutV1InnerServiceSMOImpl.updateCarInout(carInoutPo);

    }
}
