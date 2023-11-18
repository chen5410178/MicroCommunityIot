package com.java110.user.cmd.login;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cache.MappingCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.MappingConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.AuthenticationFactory;
import com.java110.core.factory.SendSmsFactory;
import com.java110.core.utils.Assert;
import com.java110.dto.msg.SmsDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.system.ISmsInnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.user.UserPo;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;

/**
 * 用户注册
 */
@Java110Cmd(serviceCode = "login.appUserRegister")
public class AppUserRegisterCmd extends Cmd {

    @Autowired
    private ISmsInnerServiceSMO smsInnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson,"link","手机号不能为空");
        Assert.hasKeyAndValue(reqJson,"password","密码不能为空");
        Assert.hasKeyAndValue(reqJson,"msgCode","验证码不能为空");

        SmsDto smsDto = new SmsDto();
        smsDto.setTel(reqJson.getString("link"));
        smsDto.setCode(reqJson.getString("msgCode"));
        smsDto = smsInnerServiceSMOImpl.validateCode(smsDto);

        if (!smsDto.isSuccess() && "ON".equals(MappingCache.getValue(MappingConstant.SMS_DOMAIN, SendSmsFactory.SMS_SEND_SWITCH))) {
            throw new IllegalArgumentException(smsDto.getMsg());
        }
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        UserPo userPo = new UserPo();
        userPo.setName(reqJson.getString("link"));
        userPo.setTel(reqJson.getString("link"));
        userPo.setLevelCd(UserDto.LEVEL_CD_PHONE);
        userPo.setPassword(AuthenticationFactory.passwdMd5(reqJson.getString("password")));
        int flag = userV1InnerServiceSMOImpl.saveUser(userPo);
        if (flag < 1) {
            throw new CmdException("注册失败");
        }
    }
}
