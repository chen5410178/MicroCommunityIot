package com.java110.user.cmd.wechat;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cache.CommonCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.ResponseConstant;
import com.java110.core.constant.WechatConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ListUtil;
import com.java110.dto.smallWeChat.SmallWeChatDto;
import com.java110.intf.charge.ISmallWeChatInnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.List;
import java.util.UUID;

@Java110Cmd(serviceCode = "wechat.wechatRefreshOpenId")
public class WechatRefreshOpenIdCmd extends Cmd {

    private final static Logger logger = LoggerFactory.getLogger(WechatRefreshOpenIdCmd.class);

    @Autowired
    private ISmallWeChatInnerServiceSMO smallWeChatInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        Assert.hasKeyAndValue(reqJson, "redirectUrl", "未包含跳转url");
        Assert.hasKeyAndValue(reqJson, "wAppId", "未包含appId");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        SmallWeChatDto smallWeChatDto = new SmallWeChatDto();
        smallWeChatDto.setAppId(reqJson.getString("wAppId"));
        smallWeChatDto.setObjId(reqJson.getString("communityId"));
        List<SmallWeChatDto> smallWeChatDtos = smallWeChatInnerServiceSMOImpl.querySmallWeChats(smallWeChatDto);
        if (ListUtil.isNull(smallWeChatDtos)) {
            throw new CmdException("未配置公众号信息");
        }

        String redirectUrl = reqJson.getString("redirectUrl");

        //分配urlCode
        String urlCode = UUID.randomUUID().toString();
        JSONObject param = new JSONObject();
        if (!redirectUrl.contains("appId")) {
            if (redirectUrl.indexOf("?") > 0) {
                redirectUrl += ("&appId=" + smallWeChatDto.getAppId());
            } else {
                redirectUrl += ("?appId=" + smallWeChatDto.getAppId());
            }
        }
        param.put("redirectUrl", redirectUrl);
        param.put("wAppId", reqJson.getString("wAppId"));
        CommonCache.setValue(urlCode, param.toJSONString(), CommonCache.defaultExpireTime);

        URL url = null;
        String openUrl = "";
        try {
            url = new URL(redirectUrl);

            String newUrl = url.getProtocol() + "://" + url.getHost();
            if (url.getPort() > 0) {
                newUrl += (":" + url.getPort());
            }

            openUrl = WechatConstant.OPEN_AUTH
                    .replace("APPID", smallWeChatDto.getAppId())
                    .replace("SCOPE", "snsapi_base")
                    .replace(
                            "REDIRECT_URL",
                            URLEncoder
                                    .encode(
                                            (newUrl
                                                    + "/iot/api/wechat.wechatNotifyOpenId/992023111817510002?urlCode=" +
                                                    urlCode),
                                            "UTF-8")).replace("STATE", "1");

        } catch (Exception e) {
            logger.error("微信公众号鉴权 redirectUrl 错误 " + redirectUrl, e);
            throw new CmdException(ResponseConstant.RESULT_CODE_ERROR, e.getLocalizedMessage());
        }
        JSONObject urlObj = new JSONObject();
        urlObj.put("openUrl", openUrl);

        context.setResponseEntity(ResultVo.createResponseEntity(ResultVo.CODE_MACHINE_OK, ResultVo.MSG_OK, urlObj));

    }
}
