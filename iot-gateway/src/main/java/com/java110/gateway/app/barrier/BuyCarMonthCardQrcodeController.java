package com.java110.gateway.app.barrier;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.cache.UrlCache;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.factory.WechatFactory;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.smallWeChat.SmallWeChatDto;
import com.java110.intf.charge.ISmallWeChatInnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 月卡购买二维码转跳
 * 小程序码 -> 拉起用户端小程序月卡购买页（packageFee/pages/fee/queryBuyCardCar，免登录）
 * 公众号码 -> H5 打开用户端月卡购买页（自动带公众号 appId，免登录）
 * 扫码全程免登录（JwtFilter 放行）
 */
@RestController
public class BuyCarMonthCardQrcodeController {

    private final static Logger logger = LoggerFactory.getLogger(BuyCarMonthCardQrcodeController.class);

    @Autowired
    private ISmallWeChatInnerServiceSMO smallWeChatInnerServiceSMOImpl;

    /**
     * 月卡购买 - 小程序转跳
     *
     * @param communityId  小区ID
     * @param createUserId 下单归属用户ID
     */
    @RequestMapping(path = "/app/buyCarMonthCardMini/{communityId}/{createUserId}", method = RequestMethod.GET)
    public ResponseEntity<String> buyCarMonthCardMini(@PathVariable String communityId, @PathVariable String createUserId, HttpServletRequest request) {
        if (StringUtil.isEmpty(communityId) || StringUtil.isEmpty(createUserId)) {
            return ResultVo.error("参数错误");
        }
        String urlScheme = getWechatURLScheme(communityId, "packageFee/pages/fee/queryBuyCardCar",
                "createUserId=" + createUserId + "&communityId=" + communityId);
        if (StringUtil.isEmpty(urlScheme)) {
            return ResultVo.error("未配置小程序");
        }
        return ResponseEntity.ok().body(getHtmlPage(urlScheme));
    }

    /**
     * 月卡购买 - 公众号转跳
     *
     * @param communityId  小区ID
     * @param createUserId 下单归属用户ID
     */
    @RequestMapping(path = "/app/buyCarMonthCardH5/{communityId}/{createUserId}", method = RequestMethod.GET)
    public ResponseEntity<String> buyCarMonthCardH5(@PathVariable String communityId, @PathVariable String createUserId, HttpServletRequest request) {
        if (StringUtil.isEmpty(communityId) || StringUtil.isEmpty(createUserId)) {
            return ResultVo.error("参数错误");
        }
        String appId = getPublicAppId(communityId);
        if (StringUtil.isEmpty(appId)) {
            return ResultVo.error("未配置公众号");
        }
        String ownerUrl = UrlCache.getOwnerUrl();
        String url = ownerUrl + "/#/packageFee/pages/fee/queryBuyCardCar?createUserId=" + createUserId
                + "&communityId=" + communityId
                + "&appId=" + appId;
        return ResultVo.redirectPage(url);
    }

    /**
     * 查小区公众号 appId，未配置返回空
     */
    private String getPublicAppId(String communityId) {
        SmallWeChatDto smallWeChatDto = new SmallWeChatDto();
        smallWeChatDto.setObjId(communityId);
        smallWeChatDto.setWeChatType(SmallWeChatDto.WECHAT_TYPE_PUBLIC);
        List<SmallWeChatDto> smallWeChatDtos = smallWeChatInnerServiceSMOImpl.querySmallWeChats(smallWeChatDto);
        if (ListUtil.isNull(smallWeChatDtos)) {
            return "";
        }
        return smallWeChatDtos.get(0).getAppId();
    }

    private String getHtmlPage(String urlScheme) {
        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\">\n" +
                "    <title>跳转小程序</title>\n" +
                "    <style>\n" +
                "        * { margin: 0; padding: 0; box-sizing: border-box; }\n" +
                "        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background: #f5f5f5; color: #333; line-height: 1.6; }\n" +
                "        .container { max-width: 100%; padding: 20px; text-align: center; }\n" +
                "        .loading { margin: 50px auto 30px; width: 60px; height: 60px; border: 4px solid rgba(0, 122, 255, 0.2); border-radius: 50%; border-top-color: #007aff; animation: spin 1s ease-in-out infinite; }\n" +
                "        @keyframes spin { to { transform: rotate(360deg); } }\n" +
                "        h1 { font-size: 20px; margin-bottom: 20px; color: #333; }\n" +
                "        .btn { display: inline-block; margin-top: 20px; padding: 12px 25px; background: #07C160; color: white; text-decoration: none; border-radius: 5px; font-weight: 500; font-size: 16px; box-shadow: 0 2px 10px rgba(7, 193, 96, 0.3); }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"loading\"></div>\n" +
                "        <h1>正在为您跳转小程序...</h1>\n" +
                "        <p>如果跳转失败，请点击下方按钮</p>\n" +
                "        <a href=\"" + urlScheme + "\" class=\"btn\">立即跳转</a>\n" +
                "    </div>\n" +
                "    <script>\n" +
                "        setTimeout(function() {\n" +
                "            window.location.href = \"" + urlScheme + "\";\n" +
                "        }, 1500);\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
        return html;
    }

    private String getWechatURLScheme(String communityId, String path, String query) {
        SmallWeChatDto smallWeChatDto = new SmallWeChatDto();
        smallWeChatDto.setObjId(communityId);
        smallWeChatDto.setWeChatType(SmallWeChatDto.WECHAT_TYPE_MINI);
        List<SmallWeChatDto> smallWeChatDtos = smallWeChatInnerServiceSMOImpl.querySmallWeChats(smallWeChatDto);
        if (ListUtil.isNull(smallWeChatDtos)) {
            return "";
        }

        String accessToken = WechatFactory.getAccessToken(smallWeChatDtos.get(0).getAppId(), smallWeChatDtos.get(0).getAppSecret());

        String generateSchemeUrl = "https://api.weixin.qq.com/wxa/generatescheme?access_token=" + accessToken;
        RestTemplate outRestTemplate = ApplicationContextFactory.getBean("outRestTemplate", RestTemplate.class);
        if (outRestTemplate == null) {
            throw new IllegalArgumentException("查询URLScheme 失败");
        }

        JSONObject paramIn = new JSONObject();
        JSONObject jump_wxa = new JSONObject();
        jump_wxa.put("path", path);
        jump_wxa.put("query", query);
        paramIn.put("jump_wxa", jump_wxa);

        ResponseEntity<String> responseStr = outRestTemplate.postForEntity(
                generateSchemeUrl, paramIn.toJSONString(), String.class);
        JSONObject response = JSONObject.parseObject(responseStr.getBody());

        if (response.getIntValue("errcode") != 0) {
            throw new IllegalArgumentException(response.getString("errmsg"));
        }
        return response.getString("openlink");
    }

}
