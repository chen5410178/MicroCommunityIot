package com.java110.user.cmd.user;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.CmdContextUtils;
import com.java110.core.utils.ListUtil;
import com.java110.dto.userAttr.UserAttrDto;
import com.java110.intf.user.IUserAttrV1InnerServiceSMO;
import com.java110.po.userAttr.UserAttrPo;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

/**
 * 保存或者更新User charge car
 */
@Java110Cmd(serviceCode = "user.saveUserChargeCar")
public class SaveUserChargeCarCmd extends Cmd {

    @Autowired
    private IUserAttrV1InnerServiceSMO userAttrV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson,"carNum","未包含车牌号");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        String userId = CmdContextUtils.getUserId(context);

        // get user charge car
        UserAttrDto userAttrDto = new UserAttrDto();
        userAttrDto.setUserId(userId);
        userAttrDto.setSpecCd(UserAttrDto.CHARGE_CAR);
        List<UserAttrDto> userAttrDtos = userAttrV1InnerServiceSMOImpl.queryUserAttrs(userAttrDto);
        UserAttrPo userAttrPo = new UserAttrPo();
        userAttrPo.setSpecCd(UserAttrDto.CHARGE_CAR);
        userAttrPo.setUserId(userId);
        userAttrPo.setValue(reqJson.getString("carNum").trim());
        if(ListUtil.isNull(userAttrDtos)){
            userAttrPo.setAttrId(GenerateCodeFactory.getAttrId());
            userAttrV1InnerServiceSMOImpl.saveUserAttr(userAttrPo);
            return ;
        }
        userAttrPo.setAttrId(userAttrDtos.get(0).getAttrId());
        userAttrV1InnerServiceSMOImpl.updateUserAttr(userAttrPo);
    }
}
