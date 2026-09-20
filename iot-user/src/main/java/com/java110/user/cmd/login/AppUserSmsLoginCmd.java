package com.java110.user.cmd.login;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.systemInfo.SystemInfoDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cache.MappingCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.CommonConstant;
import com.java110.core.constant.MappingConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.AuthenticationFactory;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.factory.SendSmsFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.ValidatorUtil;
import com.java110.dto.appUser.AppUserDto;
import com.java110.dto.msg.SmsDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.system.ISmsInnerServiceSMO;
import com.java110.intf.system.ISystemInfoV1InnerServiceSMO;
import com.java110.intf.user.IAppUserV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.user.UserPo;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 手机用户短信验证码登录（免登录，验证码通过后未注册手机号自动注册）
 */
@Java110Cmd(serviceCode = "login.appUserSmsLogin")
public class AppUserSmsLoginCmd extends Cmd {

    private final static Logger logger = LoggerFactory.getLogger(AppUserSmsLoginCmd.class);

    @Autowired
    private ISmsInnerServiceSMO smsInnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IAppUserV1InnerServiceSMO appUserV1InnerServiceSMOImpl;

    @Autowired
    private ISystemInfoV1InnerServiceSMO systemInfoV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.hasKeyAndValue(reqJson, "link", "手机号不能为空");
        Assert.hasKeyAndValue(reqJson, "msgCode", "验证码不能为空");

        if (!ValidatorUtil.isMobile(reqJson.getString("link"))) {
            throw new IllegalArgumentException("手机号格式错误");
        }

        SmsDto smsDto = new SmsDto();
        smsDto.setTel(reqJson.getString("link"));
        smsDto.setCode(reqJson.getString("msgCode"));
        smsDto = smsInnerServiceSMOImpl.validateCode(smsDto);

        if (!smsDto.isSuccess() && "ON".equals(MappingCache.getValue(MappingConstant.SMS_DOMAIN, SendSmsFactory.SMS_SEND_SWITCH))) {
            throw new CmdException("验证码错误或已过期");
        }
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {

        UserDto userDto = new UserDto();
        userDto.setTel(reqJson.getString("link"));
        userDto.setLevelCd(UserDto.LEVEL_CD_PHONE);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);

        if (ListUtil.isNull(userDtos)) {
            registerUser(reqJson.getString("link"));
            userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        }

        if (ListUtil.isNull(userDtos)) {
            throw new CmdException("该手机号未注册，请先注册");
        }

        String communityId = "";
        //todo 查询业主是否 认证了，如果认证了获取小区ID
        AppUserDto appUserDto = new AppUserDto();
        appUserDto.setUserId(userDtos.get(0).getUserId());
        List<AppUserDto> appUserDtos = appUserV1InnerServiceSMOImpl.queryAppUsers(appUserDto);
        if (!ListUtil.isNull(appUserDtos)) {
            communityId = appUserDtos.get(0).getCommunityId();
        } else {
            SystemInfoDto systemInfoDto = new SystemInfoDto();
            List<SystemInfoDto> systemInfoDtos = systemInfoV1InnerServiceSMOImpl.querySystemInfos(systemInfoDto);
            communityId = systemInfoDtos.get(0).getDefaultCommunityId();
        }

        Map userMap = new HashMap();
        userMap.put(CommonConstant.LOGIN_USER_ID, userDtos.get(0).getUserId());
        userMap.put(CommonConstant.LOGIN_USER_NAME, userDtos.get(0).getName());

        JSONObject userInfo;
        try {
            String token = AuthenticationFactory.createAndSaveToken(userMap);
            userInfo = BeanConvertUtil.beanCovertJson(userDtos.get(0));
            userInfo.put("token", token);
        } catch (Exception e) {
            logger.error("验证码登录异常：", e);
            throw new CmdException("系统内部错误，请联系管理员");
        }

        userInfo.remove("userPwd");
        userInfo.put("communityId", communityId);
        userInfo.put("code", "0");
        userInfo.put("msg", "成功");
        context.setResponseEntity(ResultVo.createResponseEntity(userInfo));
    }

    /**
     * 验证码登录时手机号未注册，自动注册手机用户（密码为随机密码）
     *
     * @param phoneNumber 手机号
     */
    private void registerUser(String phoneNumber) {
        UserPo userPo = new UserPo();
        userPo.setUserId(GenerateCodeFactory.getGeneratorId("30"));
        userPo.setName(phoneNumber);
        userPo.setTel(phoneNumber);
        userPo.setLevelCd(UserDto.LEVEL_CD_PHONE);
        userPo.setPassword(AuthenticationFactory.passwdMd5(GenerateCodeFactory.getRandomCode(6)));
        int flag = userV1InnerServiceSMOImpl.saveUser(userPo);
        if (flag < 1) {
            throw new CmdException("注册失败");
        }
    }
}
