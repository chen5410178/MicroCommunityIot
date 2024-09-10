package com.java110.user.cmd.login;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.systemInfo.SystemInfoDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cache.MappingCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.CommonConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.AuthenticationFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.factory.PropertyHttpFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.appUser.AppUserDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.system.ISystemInfoV1InnerServiceSMO;
import com.java110.intf.user.IAppUserV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户登录
 */
@Java110Cmd(serviceCode = "login.appUserLogin")
public class AppUserLoginCmd extends Cmd {
    private final static Logger logger = LoggerFactory.getLogger(AppUserLoginCmd.class);

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IAppUserV1InnerServiceSMO appUserV1InnerServiceSMOImpl;

    @Autowired
    private ISystemInfoV1InnerServiceSMO systemInfoV1InnerServiceSMOImpl;



    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "link", "手机号不能为空");
        Assert.hasKeyAndValue(reqJson, "passwd", "密码不能为空");

//        String appId = CmdContextUtils.getAppId(context);
//        if (AppDto.equals(appId)) {
//            Assert.hasKeyAndValue(reqJson, "validateCode", "未包含图片验证码");
//            String code = CommonCache.getValue(CmdContextUtils.getSessionId(cmdDataFlowContext) + "_validateCode");
//            if (!reqJson.getString("validateCode").equals(code)) {
//                throw new IllegalArgumentException("验证码错误");
//            }
//        }
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        UserDto userDto = new UserDto();
        userDto.setTel(reqJson.getString("link"));
        userDto.setPassword(AuthenticationFactory.passwdMd5(reqJson.getString("passwd")));
        userDto.setLevelCd(UserDto.LEVEL_CD_PHONE);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);

        if (ListUtil.isNull(userDtos)) {
            throw new CmdException("用户名或密码错误");
        }

        String communityId = "";
        //todo 查询业主是否 认证了，如果认证了获取小区ID
        AppUserDto appUserDto = new AppUserDto();
        appUserDto.setUserId(userDtos.get(0).getUserId());
        List<AppUserDto> appUserDtos = appUserV1InnerServiceSMOImpl.queryAppUsers(appUserDto);
        if(!ListUtil.isNull(appUserDtos)){
            communityId = appUserDtos.get(0).getCommunityId();
        }else{
            SystemInfoDto systemInfoDto = new SystemInfoDto();
            List<SystemInfoDto> systemInfoDtos = systemInfoV1InnerServiceSMOImpl.querySystemInfos(systemInfoDto);
            communityId = systemInfoDtos.get(0).getDefaultCommunityId();
        }


        ResponseEntity<String> responseEntity = null;
        try {
            Map userMap = new HashMap();
            userMap.put(CommonConstant.LOGIN_USER_ID, userDtos.get(0).getUserId());
            userMap.put(CommonConstant.LOGIN_USER_NAME, userDtos.get(0).getName());
            String token = AuthenticationFactory.createAndSaveToken(userMap);
            JSONObject userInfo = BeanConvertUtil.beanCovertJson(userDtos.get(0));
            userInfo.remove("userPwd");
            userInfo.put("token", token);
            userInfo.put("communityId",communityId);
            userInfo.put("code", "0");
            userInfo.put("msg", "成功");
            context.setResponseEntity(ResultVo.createResponseEntity(userInfo));
        } catch (Exception e) {
            logger.error("登录异常：", e);
            throw new IllegalArgumentException("系统内部错误，请联系管理员");
        }
    }

}
