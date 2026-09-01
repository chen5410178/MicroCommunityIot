package com.java110.user.cmd.user;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
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
import com.java110.core.utils.ValidatorUtil;
import com.java110.dto.msg.SmsDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.system.ISmsInnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.user.UserPo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 用户忘记密码重置（免登录，短信验证码校验后重置）
 */
@Java110Cmd(serviceCode = "user.resetUserPwd")
public class ResetUserPwdCmd extends Cmd {

    @Autowired
    private ISmsInnerServiceSMO smsInnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.hasKeyAndValue(reqJson, "link", "手机号不能为空");
        Assert.hasKeyAndValue(reqJson, "msgCode", "验证码不能为空");
        Assert.hasKeyAndValue(reqJson, "newPwd", "新密码不能为空");

        if (!ValidatorUtil.isMobile(reqJson.getString("link"))) {
            throw new IllegalArgumentException("手机号格式错误");
        }

        SmsDto smsDto = new SmsDto();
        smsDto.setTel(reqJson.getString("link"));
        smsDto.setCode(reqJson.getString("msgCode"));
        smsDto = smsInnerServiceSMOImpl.validateCode(smsDto);

        if (!smsDto.isSuccess() && "ON".equals(MappingCache.getValue(MappingConstant.SMS_DOMAIN, SendSmsFactory.SMS_SEND_SWITCH))) {
            throw new IllegalArgumentException(smsDto.getMsg());
        }

        UserDto userDto = new UserDto();
        userDto.setTel(reqJson.getString("link"));
        userDto.setLevelCd(UserDto.LEVEL_CD_PHONE);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);

        Assert.listOnlyOne(userDtos, "该手机号未注册");

        reqJson.put("userId", userDtos.get(0).getUserId());
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {

        UserPo userPo = new UserPo();
        userPo.setUserId(reqJson.getString("userId"));
        userPo.setPassword(AuthenticationFactory.passwdMd5(reqJson.getString("newPwd")));
        int flag = userV1InnerServiceSMOImpl.updateUser(userPo);
        if (flag < 1) {
            throw new CmdException("重置密码失败");
        }

        context.setResponseEntity(ResultVo.success());
    }
}
