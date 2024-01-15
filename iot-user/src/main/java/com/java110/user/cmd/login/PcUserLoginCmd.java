package com.java110.user.cmd.login;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cache.CommonCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.configuration.Java110RedisConfig;
import com.java110.core.constant.CommonConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.AuthenticationFactory;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.CmdContextUtils;
import com.java110.core.utils.StringUtil;
import com.java110.dto.app.AppDto;
import com.java110.dto.store.StoreDto;
import com.java110.dto.storeStaff.StoreStaffDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.user.IStoreStaffV1InnerServiceSMO;
import com.java110.intf.user.IStoreV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户登录 功能
 * 请求地址为/app/login.pcUserLogin
 */

@Java110Cmd(serviceCode = "login.pcUserLogin")
public class PcUserLoginCmd extends Cmd {
    private final static Logger logger = LoggerFactory.getLogger(PcUserLoginCmd.class);
//    @Autowired
//    private IUserLoginInnerServiceSMO userLoginInnerServiceSMOImpl;

    @Autowired
    private IStoreV1InnerServiceSMO storeV1InnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMO;

    @Autowired
    private IStoreStaffV1InnerServiceSMO storeStaffV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {

        Assert.hasKeyAndValue(reqJson, "username", "用户登录，未包含username节点，请检查");
        Assert.hasKeyAndValue(reqJson, "passwd", "用户登录，未包含passwd节点，请检查");

        String appId = CmdContextUtils.getAppId(cmdDataFlowContext);

        // web端需要 图片验证码校验
        if (AppDto.WEB_APP_ID.equals(appId)) {
            Assert.hasKeyAndValue(reqJson, "validateCode", "未包含图片验证码");
            String code = CommonCache.getValue(CmdContextUtils.getSessionId(cmdDataFlowContext) + "_validateCode");
            if (!reqJson.getString("validateCode").toLowerCase().equals(code)) {
                throw new IllegalArgumentException("验证码错误或失效");
            }
        }
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        ResponseEntity responseEntity = null;
        //根据AppId 查询 是否有登录的服务，查询登录地址调用
        UserDto userDto = new UserDto();
        userDto.setName(reqJson.getString("username"));
        userDto.setPassword(AuthenticationFactory.passwdMd5(reqJson.getString("passwd")));
        userDto.setLevelCds(new String[]{UserDto.LEVEL_CD_ADMIN, UserDto.LEVEL_CD_STAFF});
        List<UserDto> userDtos = userV1InnerServiceSMO.queryUsers(userDto);
        if (userDtos == null || userDtos.size() < 1) {
            userDto.setName("");
            userDto.setTel(reqJson.getString("username"));
            userDtos = userV1InnerServiceSMO.queryUsers(userDto);
        }
        if (userDtos == null || userDtos.size() < 1) {
            responseEntity = new ResponseEntity<String>("用户或密码错误", HttpStatus.UNAUTHORIZED);
            cmdDataFlowContext.setResponseEntity(responseEntity);
            return;
        }

        //检查商户状态
        StoreStaffDto storeUserDto = new StoreStaffDto();
        storeUserDto.setStaffId(userDtos.get(0).getUserId());
        List<StoreStaffDto> storeUserDtos = storeStaffV1InnerServiceSMOImpl.queryStoreStaffs(storeUserDto);

        if (storeUserDtos != null && storeUserDtos.size() > 0) {
            String state = storeUserDtos.get(0).getState();
            if (!StoreDto.STATE_NORMAL.equals(state)) {
                responseEntity = new ResponseEntity<String>("当前商户限制登录，请联系管理员", HttpStatus.UNAUTHORIZED);
                cmdDataFlowContext.setResponseEntity(responseEntity);
                return;
            }
        }

        clearUserCache(userDtos.get(0).getUserId(),storeUserDtos.get(0).getStoreId());


        try {
            Map userMap = new HashMap();
            userMap.put(CommonConstant.LOGIN_USER_ID, userDtos.get(0).getUserId());
            userMap.put(CommonConstant.LOGIN_USER_NAME, userDtos.get(0).getName());
            String token = AuthenticationFactory.createAndSaveToken(userMap);
            JSONObject userInfo = BeanConvertUtil.beanCovertJson(userDtos.get(0));
            userInfo.remove("userPwd");
            userInfo.put("token", token);
            userInfo.put("code", "0");
            userInfo.put("msg", "成功");
            //记录登录日志
//            UserLoginPo userLoginPo = new UserLoginPo();
//            userLoginPo.setLoginId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_loginId));
//            userLoginPo.setLoginTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
//            userLoginPo.setPassword(userDtos.get(0).getPassword());
//            userLoginPo.setSource(UserLoginDto.SOURCE_WEB);
//            userLoginPo.setToken(token);
//            userLoginPo.setUserId(userInfo.getString("userId"));
//            userLoginPo.setUserName(userInfo.getString("userName"));
//            userLoginInnerServiceSMOImpl.saveUserLogin(userLoginPo);
            responseEntity = new ResponseEntity<String>(userInfo.toJSONString(), HttpStatus.OK);
            cmdDataFlowContext.setResponseEntity(responseEntity);
        } catch (Exception e) {
            logger.error("登录异常：", e);
            throw new IllegalArgumentException("系统内部错误，请联系管理员");
        }
    }

    /**
     * 清理用户缓存
     *
     * @param userId
     */
    private void clearUserCache(String userId,String storeId) {
        //员工商户缓存 getStoreInfo

        String storeInfo = CommonCache.getValue("getStoreInfo" + Java110RedisConfig.GET_STORE_INFO_EXPIRE_TIME_KEY + "::" + userId);
        if(!StringUtil.isEmpty(storeInfo)){
            CommonCache.removeValue("getStoreInfo" + Java110RedisConfig.GET_STORE_INFO_EXPIRE_TIME_KEY + "::" + userId);
//            JSONObject storeObj = JSONObject.parseObject(storeInfo);
//            storeId = storeObj.getJSONObject("msg").getString("storeId");
        }
        CommonCache.removeValue("getStoreEnterCommunitys" + Java110RedisConfig.GET_STORE_ENTER_COMMUNITYS_EXPIRE_TIME_KEY + "::" + storeId);
        //员工权限
        CommonCache.removeValue("getUserPrivileges" + Java110RedisConfig.DEFAULT_EXPIRE_TIME_KEY + "::" + userId);
    }

}
