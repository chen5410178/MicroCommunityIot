package com.java110.user.cmd.wechat;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cache.CommonCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.WechatConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.ownerCarOpenUser.OwnerCarOpenUserDto;
import com.java110.dto.smallWeChat.SmallWeChatDto;
import com.java110.intf.car.IOwnerCarOpenUserV1InnerServiceSMO;
import com.java110.intf.charge.ISmallWeChatInnerServiceSMO;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.List;

@Java110Cmd(serviceCode = "wechat.wechatNotifyOpenId")
public class WechatNotifyOpenIdCmd extends Cmd {
    private final static Logger logger = LoggerFactory.getLogger(WechatNotifyOpenIdCmd.class);

    @Autowired
    private ISmallWeChatInnerServiceSMO smallWeChatInnerServiceSMOImpl;

    @Autowired
    private IOwnerCarOpenUserV1InnerServiceSMO ownerCarOpenUserV1InnerServiceSMOImpl;

    @Autowired
    private RestTemplate outRestTemplate;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        Assert.hasKeyAndValue(reqJson, "urlCode", "未包含跳转urlCode");
        Assert.hasKeyAndValue(reqJson, "code", "未包含跳转code");

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        String authCode = reqJson.getString("code");
        String paramStr = CommonCache.getAndRemoveValue(reqJson.getString("urlCode"));

        if (StringUtil.isEmpty(paramStr)) {
            context.setResponseEntity(ResultVo.redirectPage("/"));
            return;
        }

        JSONObject param = JSONObject.parseObject(paramStr);
        String redirectUrl = param.getString("redirectUrl");
        String wId =  param.getString("wAppId");
        SmallWeChatDto smallWeChatDto = new SmallWeChatDto();
        smallWeChatDto.setAppId(param.getString("wAppId"));
        smallWeChatDto.setObjId(reqJson.getString("communityId"));
        List<SmallWeChatDto> smallWeChatDtos = smallWeChatInnerServiceSMOImpl.querySmallWeChats(smallWeChatDto);
        if (ListUtil.isNull(smallWeChatDtos)) {
            throw new CmdException("未配置公众号信息");
        }

        smallWeChatDto = smallWeChatDtos.get(0);

        String url = WechatConstant.APP_GET_ACCESS_TOKEN_URL.replace("APPID", smallWeChatDto.getAppId())
                .replace("SECRET", smallWeChatDto.getAppSecret())
                .replace("CODE", authCode);

        ResponseEntity<String> paramOut = outRestTemplate.getForEntity(url, String.class);

        logger.debug("调用微信换去openId " + paramOut);
        if (paramOut.getStatusCode() != HttpStatus.OK) {
            context.setResponseEntity(ResultVo.redirectPage("/"));
        }
        JSONObject paramObj = JSONObject.parseObject(paramOut.getBody());
        //获取 openId
        String openId = paramObj.getString("openid");
        redirectUrl = redirectUrl + "&openId=" + openId;

        //查询是否有车牌号
        OwnerCarOpenUserDto ownerCarOpenUserDto = new OwnerCarOpenUserDto();
        ownerCarOpenUserDto.setOpenId(openId);
        List<OwnerCarOpenUserDto> ownerCarOpenUserDtos = ownerCarOpenUserV1InnerServiceSMOImpl.queryOwnerCarOpenUsers(ownerCarOpenUserDto);
        if (ownerCarOpenUserDtos != null && ownerCarOpenUserDtos.size() > 0) {
            redirectUrl += ("&carNum=" + ownerCarOpenUserDtos.get(0).getCarNum());
        }

        context.setResponseEntity(ResultVo.redirectPage(redirectUrl));

    }
}
