package com.java110.user.cmd.login;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.systemInfo.SystemInfoDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.client.RestTemplate;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.CommonConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.AuthenticationFactory;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.appUser.AppUserDto;
import com.java110.dto.smallWeChat.SmallWeChatDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.system.ISmallWechatV1InnerServiceSMO;
import com.java110.intf.system.ISystemInfoV1InnerServiceSMO;
import com.java110.intf.user.IAppUserV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.user.UserPo;
import org.apache.commons.net.util.Base64;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信一键登陆
 */
@Java110Cmd(serviceCode = "login.phoneWechatLogin")
public class PhoneWechatLoginCmd extends Cmd {
    private final static Logger logger = LoggerFactory.getLogger(PhoneWechatLoginCmd.class);

    @Autowired
    private ISmallWechatV1InnerServiceSMO smallWechatV1InnerServiceSMOImpl;

    @Autowired
    private RestTemplate outRestTemplate;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IAppUserV1InnerServiceSMO appUserV1InnerServiceSMOImpl;

    @Autowired
    private ISystemInfoV1InnerServiceSMO systemInfoV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.hasKeyAndValue(reqJson, "encryptedData", "未包含encryptedData");
        Assert.hasKeyAndValue(reqJson, "iv", "未包含iv");
        Assert.hasKeyAndValue(reqJson, "code", "未包含code");
        Assert.hasKeyAndValue(reqJson, "appId", "未包含appId");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {

        //调用微信获取手机号
        String code = reqJson.getString("code");
        String encryptedData = reqJson.getString("encryptedData");
        String iv = reqJson.getString("iv");
        String appId = reqJson.getString("appId");

        String sessionKey = getSessionKey(code,appId);

        if (sessionKey == null) {
            throw new CmdException("获取session_key失败");
        }

        // 3. 解密手机号
        String phoneNumber = "";
        try {
            phoneNumber = decryptData(encryptedData, iv, sessionKey);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CmdException(e.getMessage());
        }

        if (StringUtil.isEmpty(phoneNumber)) {
            throw new CmdException("获取手机号失败");
        }

        UserDto userDto = new UserDto();
        userDto.setTel(phoneNumber);
        userDto.setLevelCd(UserDto.LEVEL_CD_PHONE);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        if (ListUtil.isNull(userDtos)) {
            registerUser(phoneNumber);
            userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
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

    private void registerUser(String phoneNumber) {
        // 密码就随机数
        String userPassword = AuthenticationFactory.passwdMd5(GenerateCodeFactory.getRandomCode(6));

        UserPo userPo = new UserPo();
        userPo.setUserId(GenerateCodeFactory.getGeneratorId("30"));
        userPo.setName(phoneNumber);
        userPo.setTel(phoneNumber);
        userPo.setLevelCd(UserDto.LEVEL_CD_PHONE);
        userPo.setPassword(AuthenticationFactory.passwdMd5(userPassword));
        int flag = userV1InnerServiceSMOImpl.saveUser(userPo);
        if (flag < 1) {
            throw new CmdException("注册失败");
        }
    }

    private String getSessionKey(String code, String appId) {

        SmallWeChatDto smallWeChatDto = new SmallWeChatDto();
        smallWeChatDto.setAppId(appId);
        List<SmallWeChatDto> smallWeChatDtos = smallWechatV1InnerServiceSMOImpl.querySmallWechats(smallWeChatDto);
        if (ListUtil.isNull(smallWeChatDtos)) {
            throw new CmdException("未配置小程序信息");
        }
        String secret = smallWeChatDtos.get(0).getAppSecret();
        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appId, secret, code
        );
        String responseStr = outRestTemplate.getForObject(url, String.class);
        JSONObject response = JSONObject.parseObject(responseStr);

        if (response == null || response.containsKey("errcode")) {
            throw new CmdException("微信接口调用失败");
        }

        return response.getString("session_key");
    }

    public String decryptData(String encryptedData, String iv, String sessionKey) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(sessionKey);
        byte[] ivBytes = Base64.decodeBase64(iv);
        byte[] encryptedDataBytes = Base64.decodeBase64(encryptedData);

        // 初始化AES cipher
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        // 解密数据
        byte[] decryptedBytes = cipher.doFinal(encryptedDataBytes);
        String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);

        JSONObject data = JSONObject.parseObject(decryptedText);

        // 解析JSON获取手机号
        // 实际项目中应该使用JSON解析库
        // 这里简化为直接返回
        return data.getString("purePhoneNumber");
    }
}
