package com.java110.gateway.app.charge;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.cache.UrlCache;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.factory.WechatFactory;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.chargeMachine.ChargeMachineDto;
import com.java110.dto.chargeMachine.ChargeMachinePortDto;
import com.java110.dto.smallWeChat.SmallWeChatDto;
import com.java110.intf.charge.IChargeMachinePortV1InnerServiceSMO;
import com.java110.intf.charge.IChargeMachineV1InnerServiceSMO;
import com.java110.intf.charge.ISmallWeChatInnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 充电桩二维码转跳（设备类型 x 端）
 * 二维码内容仅依赖 machineCode，设备删除重加无需更换二维码
 * 两轮/四轮 x 小程序/公众号 四个固定入口
 */
@RestController
public class ChargeTypeQrcodeController {

    private final static Logger logger = LoggerFactory.getLogger(ChargeTypeQrcodeController.class);

    @Autowired
    private IChargeMachineV1InnerServiceSMO chargeMachineV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMachinePortV1InnerServiceSMO chargeMachinePortV1InnerServiceSMOImpl;

    @Autowired
    private ISmallWeChatInnerServiceSMO smallWeChatInnerServiceSMOImpl;

    /**
     * 两轮充电桩 - 小程序转跳
     *
     * @param machineCode 设备编码
     */
    @RequestMapping(path = "/app/chargeBikeMini/{machineCode}", method = RequestMethod.GET)
    public ResponseEntity<String> chargeBikeMini(@PathVariable String machineCode, HttpServletRequest request) {
        ChargeMachineDto chargeMachineDto = queryMachine(machineCode);
        if (chargeMachineDto == null) {
            return ResultVo.error("设备不存在");
        }
        if (!ChargeMachineDto.CHARGE_TYPE_BIKE.equals(chargeMachineDto.getChargeType())) {
            return ResultVo.error("不支持的设备");
        }
        String urlScheme = getWechatURLScheme(chargeMachineDto.getCommunityId(),
                "/packageCharge/pages/charge/machineToCharge",
                "machineId=" + chargeMachineDto.getMachineId() + "&communityId=" + chargeMachineDto.getCommunityId());
        if (StringUtil.isEmpty(urlScheme)) {
            return ResultVo.error("未配置小程序");
        }
        return ResponseEntity.ok().body(getHtmlPage(urlScheme));
    }

    /**
     * 两轮充电桩 - 公众号转跳
     *
     * @param machineCode 设备编码
     */
    @RequestMapping(path = "/app/chargeBikeH5/{machineCode}", method = RequestMethod.GET)
    public ResponseEntity<String> chargeBikeH5(@PathVariable String machineCode, HttpServletRequest request) {
        ChargeMachineDto chargeMachineDto = queryMachine(machineCode);
        if (chargeMachineDto == null) {
            return ResultVo.error("设备不存在");
        }
        if (!ChargeMachineDto.CHARGE_TYPE_BIKE.equals(chargeMachineDto.getChargeType())) {
            return ResultVo.error("不支持的设备");
        }
        String appId = getPublicAppId(chargeMachineDto.getCommunityId());
        if (StringUtil.isEmpty(appId)) {
            return ResultVo.error("未配置公众号");
        }
        String ownerUrl = UrlCache.getOwnerUrl();
        String url = ownerUrl + "/#/packageCharge/pages/charge/machineToCharge?machineId="
                + chargeMachineDto.getMachineId()
                + "&communityId=" + chargeMachineDto.getCommunityId()
                + "&wAppId=" + appId;
        return ResultVo.redirectPage(url);
    }

    /**
     * 四轮充电桩 - 小程序转跳
     *
     * @param machineCode 设备编码
     */
    @RequestMapping(path = "/app/chargeCarMini/{machineCode}", method = RequestMethod.GET)
    public ResponseEntity<String> chargeCarMini(@PathVariable String machineCode, HttpServletRequest request) {
        ChargeMachineDto chargeMachineDto = queryMachine(machineCode);
        if (chargeMachineDto == null) {
            return ResultVo.error("设备不存在");
        }
        if (!ChargeMachineDto.CHARGE_TYPE_CAR.equals(chargeMachineDto.getChargeType())) {
            return ResultVo.error("不支持的设备");
        }
        String urlScheme = getWechatURLScheme(chargeMachineDto.getCommunityId(),
                "/packageCharge/pages/charge/machineToCarCharge",
                "machineId=" + chargeMachineDto.getMachineId() + "&communityId=" + chargeMachineDto.getCommunityId());
        if (StringUtil.isEmpty(urlScheme)) {
            return ResultVo.error("未配置小程序");
        }
        return ResponseEntity.ok().body(getHtmlPage(urlScheme));
    }

    /**
     * 四轮充电桩 - 公众号转跳
     *
     * @param machineCode 设备编码
     */
    @RequestMapping(path = "/app/chargeCarH5/{machineCode}", method = RequestMethod.GET)
    public ResponseEntity<String> chargeCarH5(@PathVariable String machineCode, HttpServletRequest request) {
        ChargeMachineDto chargeMachineDto = queryMachine(machineCode);
        if (chargeMachineDto == null) {
            return ResultVo.error("设备不存在");
        }
        if (!ChargeMachineDto.CHARGE_TYPE_CAR.equals(chargeMachineDto.getChargeType())) {
            return ResultVo.error("不支持的设备");
        }
        String appId = getPublicAppId(chargeMachineDto.getCommunityId());
        if (StringUtil.isEmpty(appId)) {
            return ResultVo.error("未配置公众号");
        }
        String ownerUrl = UrlCache.getOwnerUrl();
        String url = ownerUrl + "/#/packageCharge/pages/charge/machineToCarCharge?machineId="
                + chargeMachineDto.getMachineId()
                + "&communityId=" + chargeMachineDto.getCommunityId()
                + "&wAppId=" + appId;
        return ResultVo.redirectPage(url);
    }

    /**
     * 两轮充电桩端口 - 小程序转跳（扫码自动选中端口）
     *
     * @param machineCode 设备编码
     * @param port        端口号（portCode）
     */
    @RequestMapping(path = "/app/chargeBikeMiniPort/{machineCode}/{port}", method = RequestMethod.GET)
    public ResponseEntity<String> chargeBikeMiniPort(@PathVariable String machineCode, @PathVariable String port, HttpServletRequest request) {
        ChargeMachineDto chargeMachineDto = queryMachine(machineCode);
        if (chargeMachineDto == null) {
            return ResultVo.error("设备不存在");
        }
        if (!ChargeMachineDto.CHARGE_TYPE_BIKE.equals(chargeMachineDto.getChargeType())) {
            return ResultVo.error("不支持的设备");
        }
        ChargeMachinePortDto portDto = queryPort(machineCode, port);
        if (portDto == null) {
            return ResultVo.error("端口不存在");
        }
        String urlScheme = getWechatURLScheme(chargeMachineDto.getCommunityId(),
                "/packageCharge/pages/charge/machineToCharge",
                "machineId=" + chargeMachineDto.getMachineId() + "&communityId=" + chargeMachineDto.getCommunityId() + "&portId=" + portDto.getPortId());
        if (StringUtil.isEmpty(urlScheme)) {
            return ResultVo.error("未配置小程序");
        }
        return ResponseEntity.ok().body(getHtmlPage(urlScheme));
    }

    /**
     * 两轮充电桩端口 - 公众号转跳（扫码自动选中端口）
     *
     * @param machineCode 设备编码
     * @param port        端口号（portCode）
     */
    @RequestMapping(path = "/app/chargeBikeH5Port/{machineCode}/{port}", method = RequestMethod.GET)
    public ResponseEntity<String> chargeBikeH5Port(@PathVariable String machineCode, @PathVariable String port, HttpServletRequest request) {
        ChargeMachineDto chargeMachineDto = queryMachine(machineCode);
        if (chargeMachineDto == null) {
            return ResultVo.error("设备不存在");
        }
        if (!ChargeMachineDto.CHARGE_TYPE_BIKE.equals(chargeMachineDto.getChargeType())) {
            return ResultVo.error("不支持的设备");
        }
        String appId = getPublicAppId(chargeMachineDto.getCommunityId());
        if (StringUtil.isEmpty(appId)) {
            return ResultVo.error("未配置公众号");
        }
        ChargeMachinePortDto portDto = queryPort(machineCode, port);
        if (portDto == null) {
            return ResultVo.error("端口不存在");
        }
        String ownerUrl = UrlCache.getOwnerUrl();
        String url = ownerUrl + "/#/packageCharge/pages/charge/machineToCharge?machineId="
                + chargeMachineDto.getMachineId()
                + "&communityId=" + chargeMachineDto.getCommunityId()
                + "&portId=" + portDto.getPortId()
                + "&wAppId=" + appId;
        return ResultVo.redirectPage(url);
    }

    /**
     * 四轮充电桩端口 - 小程序转跳（直达端口充电页）
     *
     * @param machineCode 设备编码
     * @param port        端口号（portCode）
     */
    @RequestMapping(path = "/app/chargeCarMiniPort/{machineCode}/{port}", method = RequestMethod.GET)
    public ResponseEntity<String> chargeCarMiniPort(@PathVariable String machineCode, @PathVariable String port, HttpServletRequest request) {
        ChargeMachineDto chargeMachineDto = queryMachine(machineCode);
        if (chargeMachineDto == null) {
            return ResultVo.error("设备不存在");
        }
        if (!ChargeMachineDto.CHARGE_TYPE_CAR.equals(chargeMachineDto.getChargeType())) {
            return ResultVo.error("不支持的设备");
        }
        ChargeMachinePortDto portDto = queryPort(machineCode, port);
        if (portDto == null) {
            return ResultVo.error("端口不存在");
        }
        String urlScheme = getWechatURLScheme(chargeMachineDto.getCommunityId(),
                "/packageCharge/pages/charge/carChargePort",
                "machineId=" + chargeMachineDto.getMachineId() + "&communityId=" + chargeMachineDto.getCommunityId() + "&portId=" + portDto.getPortId());
        if (StringUtil.isEmpty(urlScheme)) {
            return ResultVo.error("未配置小程序");
        }
        return ResponseEntity.ok().body(getHtmlPage(urlScheme));
    }

    /**
     * 四轮充电桩端口 - 公众号转跳（直达端口充电页）
     *
     * @param machineCode 设备编码
     * @param port        端口号（portCode）
     */
    @RequestMapping(path = "/app/chargeCarH5Port/{machineCode}/{port}", method = RequestMethod.GET)
    public ResponseEntity<String> chargeCarH5Port(@PathVariable String machineCode, @PathVariable String port, HttpServletRequest request) {
        ChargeMachineDto chargeMachineDto = queryMachine(machineCode);
        if (chargeMachineDto == null) {
            return ResultVo.error("设备不存在");
        }
        if (!ChargeMachineDto.CHARGE_TYPE_CAR.equals(chargeMachineDto.getChargeType())) {
            return ResultVo.error("不支持的设备");
        }
        String appId = getPublicAppId(chargeMachineDto.getCommunityId());
        if (StringUtil.isEmpty(appId)) {
            return ResultVo.error("未配置公众号");
        }
        ChargeMachinePortDto portDto = queryPort(machineCode, port);
        if (portDto == null) {
            return ResultVo.error("端口不存在");
        }
        String ownerUrl = UrlCache.getOwnerUrl();
        String url = ownerUrl + "/#/packageCharge/pages/charge/carChargePort?machineId="
                + chargeMachineDto.getMachineId()
                + "&communityId=" + chargeMachineDto.getCommunityId()
                + "&portId=" + portDto.getPortId()
                + "&wAppId=" + appId;
        return ResultVo.redirectPage(url);
    }

    /**
     * 按设备编码查充电桩
     */
    private ChargeMachineDto queryMachine(String machineCode) {
        ChargeMachineDto chargeMachineDto = new ChargeMachineDto();
        chargeMachineDto.setMachineCode(machineCode);
        List<ChargeMachineDto> chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(chargeMachineDto);
        if (ListUtil.isNull(chargeMachineDtos)) {
            return null;
        }
        return chargeMachineDtos.get(0);
    }

    /**
     * 按设备编码 + 端口号（portCode）查端口
     */
    private ChargeMachinePortDto queryPort(String machineCode, String port) {
        ChargeMachineDto chargeMachineDto = new ChargeMachineDto();
        chargeMachineDto.setMachineCode(machineCode);
        List<ChargeMachineDto> chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(chargeMachineDto);
        if (ListUtil.isNull(chargeMachineDtos)) {
            return null;
        }
        ChargeMachinePortDto portDto = new ChargeMachinePortDto();
        portDto.setMachineId(chargeMachineDtos.get(0).getMachineId());
        portDto.setPortCode(port);
        List<ChargeMachinePortDto> portDtos = chargeMachinePortV1InnerServiceSMOImpl.queryChargeMachinePorts(portDto);
        if (ListUtil.isNull(portDtos)) {
            return null;
        }
        return portDtos.get(0);
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
