package com.java110.user.cmd.user;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.CmdContextUtils;
import com.java110.core.utils.ListUtil;
import com.java110.dto.userAttr.UserAttrDto;
import com.java110.intf.user.IUserAttrV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

/**
 * 查询用户充电车辆
 */
@Java110Cmd(serviceCode = "user.getUserChargeCar")
public class GetUserChargeCarCmd extends Cmd {
    @Autowired
    private IUserAttrV1InnerServiceSMO userAttrV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String userId = CmdContextUtils.getUserId(context);

        // get user charge car
        UserAttrDto userAttrDto = new UserAttrDto();
        userAttrDto.setUserId(userId);
        userAttrDto.setSpecCd(UserAttrDto.CHARGE_CAR);
        List<UserAttrDto> userAttrDtos = userAttrV1InnerServiceSMOImpl.queryUserAttrs(userAttrDto);
        if (ListUtil.isNull(userAttrDtos)) {
            throw new CmdException("车牌不存在");
        }
        context.setResponseEntity(ResultVo.createResponseEntity(userAttrDtos.get(0).getValue()));
    }
}
