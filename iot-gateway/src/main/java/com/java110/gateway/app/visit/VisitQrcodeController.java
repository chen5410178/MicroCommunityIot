package com.java110.gateway.app.visit;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.cache.UrlCache;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.factory.WechatFactory;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.smallWeChat.SmallWeChatDto;
import com.java110.intf.barrier.IBarrierV1InnerServiceSMO;
import com.java110.intf.charge.ISmallWeChatInnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 道闸访客二维码转跳
 * 小程序码 -> 拉起用户端小程序访客登记页（自动关联道闸待进场车辆）
 * 公众号码 -> H5 打开用户端访客登记页
 * machineId 为 -1 时表示未指定道闸（门禁侧复用场景），不拼接 machineId 参数
 */
@RestController
public class VisitQrcodeController {

    private final static Logger logger = LoggerFactory.getLogger(VisitQrcodeController.class);

    @Autowired
    private IBarrierV1InnerServiceSMO barrierV1InnerServiceSMOImpl;

    @Autowired
    private ISmallWeChatInnerServiceSMO smallWeChatInnerServiceSMOImpl;

    /**
     * 道闸访客 - 小程序转跳
     *
     * @param communityId 小区ID
     * @param machineId   道闸设备ID（-1 表示未指定）
     */
    @RequestMapping(path = "/app/visitMini/{communityId}/{machineId}", method = RequestMethod.GET)
    public ResponseEntity<String> visitMini(@PathVariable String communityId, @PathVariable String machineId, HttpServletRequest request) {
        if (StringUtil.isEmpty(communityId)) {
            return ResultVo.error("参数错误");
        }
        if (!"-1".equals(machineId)) {
            BarrierDto barrierDto = queryBarrier(machineId, communityId);
            if (barrierDto == null) {
                return ResultVo.error("设备不存在");
            }
        }
        String query = "communityId=" + communityId;
        if (!"-1".equals(machineId)) {
            query += "&machineId=" + machineId;
        }
        String urlScheme = getWechatURLScheme(communityId, "/packageVisit/pages/visit/addVisit", query);
        if (StringUtil.isEmpty(urlScheme)) {
            return ResultVo.error("未配置小程序");
        }
        return ResponseEntity.ok().body(getHtmlPage(urlScheme));
    }

    /**
     * 道闸访客 - 公众号转跳
     *
     * @param communityId 小区ID
     * @param machineId   道闸设备ID（-1 表示未指定）
     */
    @RequestMapping(path = "/app/visitH5/{communityId}/{machineId}", method = RequestMethod.GET)
    public ResponseEntity<String> visitH5(@PathVariable String communityId, @PathVariable String machineId, HttpServletRequest request) {
        if (StringUtil.isEmpty(communityId)) {
            return ResultVo.error("参数错误");
        }
        if (!"-1".equals(machineId)) {
            BarrierDto barrierDto = queryBarrier(machineId, communityId);
            if (barrierDto == null) {
                return ResultVo.error("设备不存在");
            }
        }
        String appId = getPublicAppId(communityId);
        if (StringUtil.isEmpty(appId)) {
            return ResultVo.error("未配置公众号");
        }
        String ownerUrl = UrlCache.getOwnerUrl();
        String url = ownerUrl + "/#/packageVisit/pages/visit/addVisit?communityId=" + communityId;
        if (!"-1".equals(machineId)) {
            url += "&machineId=" + machineId;
        }
        url += "&wAppId=" + appId;
        return ResultVo.redirectPage(url);
    }

    /**
     * 按 machineId + communityId 查道闸设备
     */
    private BarrierDto queryBarrier(String machineId, String communityId) {
        BarrierDto barrierDto = new BarrierDto();
        barrierDto.setMachineId(machineId);
        barrierDto.setCommunityId(communityId);
        List<BarrierDto> barrierDtos = barrierV1InnerServiceSMOImpl.queryBarriers(barrierDto);
        if (ListUtil.isNull(barrierDtos)) {
            return null;
        }
        return barrierDtos.get(0);
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
                "        <a href=\""+urlScheme+"\" class=\"btn\">立即跳转</a>\n" +
                "    </div>\n" +
                "    <script>\n" +
                "        setTimeout(function() {\n" +
                "            window.location.href = \""+urlScheme+"\";\n" +
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
