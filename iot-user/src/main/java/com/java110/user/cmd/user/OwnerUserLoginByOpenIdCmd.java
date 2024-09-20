package com.java110.user.cmd.user;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.systemInfo.SystemInfoDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.CommonConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.AuthenticationFactory;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.app.AppDto;
import com.java110.dto.appUser.AppUserDto;
import com.java110.dto.community.CommunityDto;
import com.java110.dto.user.UserDto;
import com.java110.dto.userAttr.UserAttrDto;
import com.java110.intf.community.ICommunityInnerServiceSMO;
import com.java110.intf.system.ISystemInfoV1InnerServiceSMO;
import com.java110.intf.user.IAppUserV1InnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.intf.user.IUserAttrV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.user.UserPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * 业主登录，专门提供业主 使用
 * 微信openId 登录
 */
@Java110Cmd(serviceCode = "user.ownerUserLoginByOpenId")
public class OwnerUserLoginByOpenIdCmd extends Cmd {

    private final static Logger logger = LoggerFactory.getLogger(OwnerUserLoginByOpenIdCmd.class);

    @Autowired
    private IUserV1InnerServiceSMO userInnerServiceSMOImpl;

    @Autowired
    private IUserAttrV1InnerServiceSMO userAttrV1InnerServiceSMOImpl;


    @Autowired
    private IAppUserV1InnerServiceSMO appUserV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;

    @Autowired
    private ICommunityInnerServiceSMO communityInnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private ISystemInfoV1InnerServiceSMO systemInfoV1InnerServiceSMOImpl;


    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "openId", "请求报文中未包含openId");

        //todo openId 转换
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        UserAttrDto userAttrDto = new UserAttrDto();
        userAttrDto.setSpecCd(UserAttrDto.SPEC_OPEN_ID);
        userAttrDto.setValue(reqJson.getString("openId"));
        List<UserAttrDto> userAttrDtos = userAttrV1InnerServiceSMOImpl.queryUserAttrs(userAttrDto);

        if (ListUtil.isNull(userAttrDtos)) {
            throw new CmdException("未找到用户信息");
        }

        // todo  2.0 校验 业主用户绑定表是否存在记录
        AppUserDto appUserDto = new AppUserDto();
        appUserDto.setUserId(userAttrDtos.get(0).getUserId());
        List<AppUserDto> ownerAppUserDtos = appUserV1InnerServiceSMOImpl.queryAppUsers(appUserDto);

        String communityId = "";
        if (!ListUtil.isNull(ownerAppUserDtos)) {
            // todo 4.0 查询小区是否存在
            communityId = ownerAppUserDtos.get(0).getCommunityId();
        } else {
            SystemInfoDto systemInfoDto = new SystemInfoDto();
            List<SystemInfoDto> systemInfoDtos = systemInfoV1InnerServiceSMOImpl.querySystemInfos(systemInfoDto);
            communityId = systemInfoDtos.get(0).getDefaultCommunityId();
        }
        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(communityId);
        List<CommunityDto> communityDtos = communityInnerServiceSMOImpl.queryCommunitys(communityDto);
        Assert.listOnlyOne(communityDtos, "小区不存在，确保开发者账户配置默认小区" + communityId);


        UserDto userDto = new UserDto();
        userDto.setLevelCd(UserDto.LEVEL_CD_PHONE);
        userDto.setUserId(userAttrDtos.get(0).getUserId());

        //todo 1.0 查询用户是否存在
        List<UserDto> userDtos = userInnerServiceSMOImpl.queryUsers(userDto);

        if (ListUtil.isNull(userDtos)) {
            throw new CmdException("业主不存在，请先注册");
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
            userInfo.put("communityId", communityId);
            userInfo.put("code", "0");
            userInfo.put("msg", "成功");
            context.setResponseEntity(ResultVo.createResponseEntity(userInfo));
        } catch (Exception e) {
            logger.error("登录异常：", e);
            throw new IllegalArgumentException("系统内部错误，请联系管理员");
        }
    }


    private UserAttrDto getCurrentUserAttrDto(List<UserAttrDto> userAttrDtos, String specCd) {
        if (userAttrDtos == null) {
            return null;
        }
        for (UserAttrDto userAttrDto : userAttrDtos) {
            if (specCd.equals(userAttrDto.getSpecCd())) {
                return userAttrDto;
            }
        }

        return null;
    }
}
