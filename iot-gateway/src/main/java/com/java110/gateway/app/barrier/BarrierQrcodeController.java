package com.java110.gateway.app.barrier;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.cache.UrlCache;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.factory.WechatFactory;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.parking.ParkingAreaDto;
import com.java110.dto.smallWeChat.SmallWeChatDto;
import com.java110.intf.barrier.IBarrierV1InnerServiceSMO;
import com.java110.intf.car.IParkingAreaV1InnerServiceSMO;
import com.java110.intf.charge.ISmallWeChatInnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 道闸停车场二维码转跳
 * 场内二维码 -> 拉起用户端小程序/H5 场内临时车缴费页（packageFee/pages/fee/tempParkingFee）
 * 出口二维码 -> 按出口道闸反查停车场，拉起临时车缴费页并自动带出缴费车辆
 * 无牌车二维码 -> 拉起用户端无牌车登记入场页（packageCar/pages/car/unlicensedCarIn）
 * 共 3 场景 x 小程序/公众号 2 端 = 6 个转跳入口，扫码全程免登录（JwtFilter 放行）
 */
@RestController
public class BarrierQrcodeController {

    private final static Logger logger = LoggerFactory.getLogger(BarrierQrcodeController.class);

    @Autowired
    private ISmallWeChatInnerServiceSMO smallWeChatInnerServiceSMOImpl;

    @Autowired
    private IBarrierV1InnerServiceSMO barrierV1InnerServiceSMOImpl;

    @Autowired
    private IParkingAreaV1InnerServiceSMO parkingAreaV1InnerServiceSMOImpl;

    /**
     * 场内 - 小程序转跳
     *
     * @param communityId 小区ID
     * @param paId        停车场ID
     */
    @RequestMapping(path = "/app/parkingInMini/{communityId}/{paId}", method = RequestMethod.GET)
    public ResponseEntity<String> parkingInMini(@PathVariable String communityId, @PathVariable String paId, HttpServletRequest request) {
        if (StringUtil.isEmpty(communityId) || StringUtil.isEmpty(paId) || "-1".equals(paId)) {
            return ResultVo.error("参数错误");
        }
        ParkingAreaDto parkingAreaDto = queryParkingArea(paId, communityId);
        if (parkingAreaDto == null) {
            return ResultVo.error("停车场不存在");
        }
        String urlScheme = getWechatURLScheme(communityId, "packageFee/pages/fee/tempParkingFee",
                "paId=" + paId + "&communityId=" + communityId);
        if (StringUtil.isEmpty(urlScheme)) {
            return ResultVo.error("未配置小程序");
        }
        return ResponseEntity.ok().body(getHtmlPage(urlScheme));
    }

    /**
     * 场内 - 公众号转跳
     *
     * @param communityId 小区ID
     * @param paId        停车场ID
     */
    @RequestMapping(path = "/app/parkingInH5/{communityId}/{paId}", method = RequestMethod.GET)
    public ResponseEntity<String> parkingInH5(@PathVariable String communityId, @PathVariable String paId, HttpServletRequest request) {
        if (StringUtil.isEmpty(communityId) || StringUtil.isEmpty(paId) || "-1".equals(paId)) {
            return ResultVo.error("参数错误");
        }
        ParkingAreaDto parkingAreaDto = queryParkingArea(paId, communityId);
        if (parkingAreaDto == null) {
            return ResultVo.error("停车场不存在");
        }
        String appId = getPublicAppId(communityId);
        if (StringUtil.isEmpty(appId)) {
            return ResultVo.error("未配置公众号");
        }
        String ownerUrl = UrlCache.getOwnerUrl();
        String url = ownerUrl + "/#/packageFee/pages/fee/tempParkingFee?paId=" + paId
                + "&communityId=" + communityId
                + "&appId=" + appId;
        return ResultVo.redirectPage(url);
    }

    /**
     * 出口 - 小程序转跳
     *
     * @param communityId 小区ID
     * @param machineId   出口道闸设备ID
     */
    @RequestMapping(path = "/app/parkingOutMini/{communityId}/{machineId}", method = RequestMethod.GET)
    public ResponseEntity<String> parkingOutMini(@PathVariable String communityId, @PathVariable String machineId, HttpServletRequest request) {
        if (StringUtil.isEmpty(communityId) || StringUtil.isEmpty(machineId) || "-1".equals(machineId)) {
            return ResultVo.error("参数错误");
        }
        BarrierDto barrierDto = queryBarrier(machineId, communityId);
        if (barrierDto == null) {
            return ResultVo.error("设备不存在");
        }
        if (StringUtil.isEmpty(barrierDto.getPaId())) {
            return ResultVo.error("未查到停车场信息");
        }
        String urlScheme = getWechatURLScheme(communityId, "packageFee/pages/fee/tempParkingFee",
                "paId=" + barrierDto.getPaId() + "&machineId=" + machineId + "&communityId=" + communityId);
        if (StringUtil.isEmpty(urlScheme)) {
            return ResultVo.error("未配置小程序");
        }
        return ResponseEntity.ok().body(getHtmlPage(urlScheme));
    }

    /**
     * 出口 - 公众号转跳
     *
     * @param communityId 小区ID
     * @param machineId   出口道闸设备ID
     */
    @RequestMapping(path = "/app/parkingOutH5/{communityId}/{machineId}", method = RequestMethod.GET)
    public ResponseEntity<String> parkingOutH5(@PathVariable String communityId, @PathVariable String machineId, HttpServletRequest request) {
        if (StringUtil.isEmpty(communityId) || StringUtil.isEmpty(machineId) || "-1".equals(machineId)) {
            return ResultVo.error("参数错误");
        }
        BarrierDto barrierDto = queryBarrier(machineId, communityId);
        if (barrierDto == null) {
            return ResultVo.error("设备不存在");
        }
        if (StringUtil.isEmpty(barrierDto.getPaId())) {
            return ResultVo.error("未查到停车场信息");
        }
        String appId = getPublicAppId(communityId);
        if (StringUtil.isEmpty(appId)) {
            return ResultVo.error("未配置公众号");
        }
        String ownerUrl = UrlCache.getOwnerUrl();
        String url = ownerUrl + "/#/packageFee/pages/fee/tempParkingFee?paId=" + barrierDto.getPaId()
                + "&machineId=" + machineId
                + "&communityId=" + communityId
                + "&appId=" + appId;
        return ResultVo.redirectPage(url);
    }

    /**
     * 无牌车 - 小程序转跳
     *
     * @param communityId 小区ID
     * @param machineId   入口道闸设备ID
     */
    @RequestMapping(path = "/app/unlicensedCarMini/{communityId}/{machineId}", method = RequestMethod.GET)
    public ResponseEntity<String> unlicensedCarMini(@PathVariable String communityId, @PathVariable String machineId, HttpServletRequest request) {
        if (StringUtil.isEmpty(communityId) || StringUtil.isEmpty(machineId) || "-1".equals(machineId)) {
            return ResultVo.error("参数错误");
        }
        BarrierDto barrierDto = queryBarrier(machineId, communityId);
        if (barrierDto == null) {
            return ResultVo.error("设备不存在");
        }
        String urlScheme = getWechatURLScheme(communityId, "packageCar/pages/car/unlicensedCarIn",
                "communityId=" + communityId + "&machineId=" + machineId);
        if (StringUtil.isEmpty(urlScheme)) {
            return ResultVo.error("未配置小程序");
        }
        return ResponseEntity.ok().body(getHtmlPage(urlScheme));
    }

    /**
     * 无牌车 - 公众号转跳
     *
     * @param communityId 小区ID
     * @param machineId   入口道闸设备ID
     */
    @RequestMapping(path = "/app/unlicensedCarH5/{communityId}/{machineId}", method = RequestMethod.GET)
    public ResponseEntity<String> unlicensedCarH5(@PathVariable String communityId, @PathVariable String machineId, HttpServletRequest request) {
        if (StringUtil.isEmpty(communityId) || StringUtil.isEmpty(machineId) || "-1".equals(machineId)) {
            return ResultVo.error("参数错误");
        }
        BarrierDto barrierDto = queryBarrier(machineId, communityId);
        if (barrierDto == null) {
            return ResultVo.error("设备不存在");
        }
        String appId = getPublicAppId(communityId);
        if (StringUtil.isEmpty(appId)) {
            return ResultVo.error("未配置公众号");
        }
        String ownerUrl = UrlCache.getOwnerUrl();
        String url = ownerUrl + "/#/packageCar/pages/car/unlicensedCarIn?communityId=" + communityId
                + "&machineId=" + machineId
                + "&appId=" + appId;
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
     * 按 paId + communityId 查停车场
     */
    private ParkingAreaDto queryParkingArea(String paId, String communityId) {
        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(paId);
        parkingAreaDto.setCommunityId(communityId);
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaV1InnerServiceSMOImpl.queryParkingAreas(parkingAreaDto);
        if (ListUtil.isNull(parkingAreaDtos)) {
            return null;
        }
        return parkingAreaDtos.get(0);
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
