package com.java110.charge.cmd.charge;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.CmdContextUtils;
import com.java110.dto.chargeMachine.ChargeMachineOrderDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.charge.IChargeMachineOrderV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Java110Cmd(serviceCode = "charge.queryPhoneChargeOrder")
public class QueryPhoneChargeOrderCmd extends Cmd {
    private static Logger logger = LoggerFactory.getLogger(QueryPhoneChargeOrderCmd.class);

    @Autowired
    private IChargeMachineOrderV1InnerServiceSMO chargeMachineOrderV1InnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        super.validatePageInfo(reqJson);
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String userId = CmdContextUtils.getUserId(context);
        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        Assert.listOnlyOne(userDtos,"用户不存在");
        ChargeMachineOrderDto chargeMachineOrderDto = new ChargeMachineOrderDto();
        chargeMachineOrderDto.setPersonTel(userDtos.get(0).getTel());
        chargeMachineOrderDto.setPage(reqJson.getIntValue("page"));
        chargeMachineOrderDto.setRow(reqJson.getIntValue("row"));
        chargeMachineOrderDto.setOrderId(reqJson.getString("orderId"));

        int count = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrdersCount(chargeMachineOrderDto);

        List<ChargeMachineOrderDto> chargeMachineOrderDtos = null;

        if (count > 0) {
            chargeMachineOrderDtos = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrders(chargeMachineOrderDto);
        } else {
            chargeMachineOrderDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, chargeMachineOrderDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }
}
