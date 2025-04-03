package com.java110.gateway.app.charge;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.cache.MappingCache;
import com.java110.core.cache.UrlCache;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.factory.WechatFactory;
import com.java110.core.utils.ListUtil;
import com.java110.dto.chargeMachine.ChargeMachineDto;
import com.java110.dto.chargeMachine.ChargeMachinePortDto;
import com.java110.dto.chargeMachine.NotifyChargeOrderDto;
import com.java110.dto.smallWeChat.SmallWeChatDto;
import com.java110.intf.charge.IChargeMachinePortV1InnerServiceSMO;
import com.java110.intf.charge.IChargeMachineV1InnerServiceSMO;
import com.java110.intf.charge.ISmallWeChatInnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(path = "/app/charge")
public class ChangeQrcodeController {
    private final static Logger logger = LoggerFactory.getLogger(ChangeQrcodeController.class);


    @Autowired
    private IChargeMachineV1InnerServiceSMO chargeMachineV1InnerServiceSMOImpl;

    @Autowired
    private ISmallWeChatInnerServiceSMO smallWeChatInnerServiceSMOImpl;

    @Autowired
    private IChargeMachinePortV1InnerServiceSMO chargeMachinePortV1InnerServiceSMOImpl;


    /**
     * <p>二维码转码</p>
     *
     * @param request
     * @throws Exception
     */
    @RequestMapping(path = "/ykc", method = RequestMethod.GET)
    public ResponseEntity<String> chargeYkc(
            @RequestParam("No") String no,
            HttpServletRequest request) {

        if (no.length() != 16) {
            return ResultVo.error("码不符合规范");
        }
        String machineCode = no.substring(0, no.length() - 2);
        String gunNo = no.substring(no.length() - 2);
        ChargeMachineDto chargeMachineDto = new ChargeMachineDto();
        chargeMachineDto.setMachineCode(machineCode);
        List<ChargeMachineDto> chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(chargeMachineDto);
        if (ListUtil.isNull(chargeMachineDtos)) {
            return ResultVo.error("设备不存在");
        }

        ChargeMachinePortDto chargeMachinePortDto = new ChargeMachinePortDto();
        chargeMachinePortDto.setMachineId(chargeMachineDtos.get(0).getMachineId());
        chargeMachinePortDto.setPortCode(Integer.parseInt(gunNo, 16) + "");
        List<ChargeMachinePortDto> chargeMachinePortDtos = chargeMachinePortV1InnerServiceSMOImpl.queryChargeMachinePorts(chargeMachinePortDto);
        if (ListUtil.isNull(chargeMachinePortDtos)) {
            throw new CmdException("枪不存在");
        }

        SmallWeChatDto smallWeChatDto = new SmallWeChatDto();
        smallWeChatDto.setObjId(chargeMachineDtos.get(0).getCommunityId());
        smallWeChatDto.setWeChatType(SmallWeChatDto.WECHAT_TYPE_PUBLIC);
        List<SmallWeChatDto> smallWeChatDtos = smallWeChatInnerServiceSMOImpl.querySmallWeChats(smallWeChatDto);
        String appId = "";
        if (ListUtil.isNull(smallWeChatDtos)) {
            return ResultVo.error("小区未配置公众号信息");
        }
        appId = smallWeChatDtos.get(0).getAppId();

        String ownerUrl = UrlCache.getOwnerUrl();
        String url = "";
        if (ChargeMachineDto.CHARGE_TYPE_BIKE.equals(chargeMachineDtos.get(0).getChargeType())) {
            url = ownerUrl + "/#/pages/charge/machineToCharge?machineId="
                    + chargeMachineDtos.get(0).getMachineId()
                    + "&communityId=" + chargeMachineDtos.get(0).getCommunityId()
                    + "&wAppId=" + appId;
        } else if (ChargeMachineDto.CHARGE_TYPE_CAR.equals(chargeMachineDtos.get(0).getChargeType())) {
            url = ownerUrl + "/#/pages/charge/carChargePort?machineId="
                    + chargeMachineDtos.get(0).getMachineId()
                    + "&communityId=" + chargeMachineDtos.get(0).getCommunityId()
                    + "&wAppId=" + appId + "&portId=" + chargeMachinePortDtos.get(0).getPortId();
        } else {
            return ResultVo.error("不支持的设备");
        }

        return ResultVo.redirectPage(url);

    }
//    桩二维码：域名  + /app/charge/songbing?No= + 设备编码
//    插座二维码地址：域名  + /app/charge/songbing?No= + 设备编码 &socket=1~10

    /**
     * <p>松饼二维码转码</p>
     *
     * @param request
     * @throws Exception
     */
    @RequestMapping(path = "/songbing", method = RequestMethod.GET)
    public ResponseEntity<String> chargeSongbing(
            @RequestParam("No") String no,
            @RequestParam(value = "socket", required = false, defaultValue = "222") String socket,
            HttpServletRequest request) {
        if (no.length() != 10) {
            return ResultVo.error("桩码不符合规范");
        }
        String machineCode = no;
        String gunNo = socket;
        String portId = "";
        ChargeMachineDto chargeMachineDto = new ChargeMachineDto();
        chargeMachineDto.setMachineCode(machineCode);
        List<ChargeMachineDto> chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(chargeMachineDto);
        if (ListUtil.isNull(chargeMachineDtos)) {
            return ResultVo.error("充电设备不存在");
        }
        if (!"222".equals(socket)) {
            ChargeMachinePortDto chargeMachinePortDto = new ChargeMachinePortDto();
            chargeMachinePortDto.setMachineId(chargeMachineDtos.get(0).getMachineId());
            chargeMachinePortDto.setPortCode(gunNo);
            List<ChargeMachinePortDto> chargeMachinePortDtos = chargeMachinePortV1InnerServiceSMOImpl.queryChargeMachinePorts(chargeMachinePortDto);
            if (ListUtil.isNull(chargeMachinePortDtos)) {
                throw new CmdException("充电插座不存在");
            }
            portId = chargeMachinePortDtos.get(0).getPortId();
        }
        SmallWeChatDto smallWeChatDto = new SmallWeChatDto();
        smallWeChatDto.setObjId(chargeMachineDtos.get(0).getCommunityId());
        smallWeChatDto.setWeChatType(SmallWeChatDto.WECHAT_TYPE_PUBLIC);
        List<SmallWeChatDto> smallWeChatDtos = smallWeChatInnerServiceSMOImpl.querySmallWeChats(smallWeChatDto);
        String appId = "";
        if (ListUtil.isNull(smallWeChatDtos)) {
            return ResultVo.error("小区未配置公众号信息");
        }
        appId = smallWeChatDtos.get(0).getAppId();
        String ownerUrl = UrlCache.getOwnerUrl();
        String url = "";
        if (ChargeMachineDto.CHARGE_TYPE_BIKE.equals(chargeMachineDtos.get(0).getChargeType())) {
            url = ownerUrl + "/#/pages/charge/machineToCharge?machineId="
                    + chargeMachineDtos.get(0).getMachineId()
                    + "&communityId=" + chargeMachineDtos.get(0).getCommunityId()
                    + "&wAppId=" + appId;
            if (!"222".equals(socket)) {
                url += "&portId=" + portId;
            }
        } else {
            return ResultVo.error("不支持的设备");
        }
        return ResultVo.redirectPage(url);
    }

    /**
     * <p>充电结束通知</p>
     *
     * @param request
     * @throws Exception
     */
    @RequestMapping(path = "/{machineCode}", method = RequestMethod.GET)
    public ResponseEntity<String> qrcode(
            @PathVariable String machineCode,
            HttpServletRequest request) {

        ChargeMachineDto chargeMachineDto = new ChargeMachineDto();
        chargeMachineDto.setMachineCode(machineCode);
        List<ChargeMachineDto> chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(chargeMachineDto);
        if (ListUtil.isNull(chargeMachineDtos)) {
            return ResultVo.error("设备不存在");
        }

        String chargePath = MappingCache.getValue("SYSTEM_SWITCH","CHARGE_PAGE");
        if("MINI".equals(chargePath)){
            return pageMiniPage(chargeMachineDtos.get(0));
        }

        SmallWeChatDto smallWeChatDto = new SmallWeChatDto();
        smallWeChatDto.setObjId(chargeMachineDtos.get(0).getCommunityId());
        smallWeChatDto.setWeChatType(SmallWeChatDto.WECHAT_TYPE_PUBLIC);
        List<SmallWeChatDto> smallWeChatDtos = smallWeChatInnerServiceSMOImpl.querySmallWeChats(smallWeChatDto);
        String appId = "";
        if (ListUtil.isNull(smallWeChatDtos)) {
            return ResultVo.error("小区未配置公众号信息");
        }
        appId = smallWeChatDtos.get(0).getAppId();

        String ownerUrl = UrlCache.getOwnerUrl();
        String url = "";
        if (ChargeMachineDto.CHARGE_TYPE_BIKE.equals(chargeMachineDtos.get(0).getChargeType())) {
            url = ownerUrl + "/#/pages/charge/machineToCharge?machineId="
                    + chargeMachineDtos.get(0).getMachineId()
                    + "&communityId=" + chargeMachineDtos.get(0).getCommunityId()
                    + "&wAppId=" + appId;
        } else if (ChargeMachineDto.CHARGE_TYPE_CAR.equals(chargeMachineDtos.get(0).getChargeType())) {
            url = ownerUrl + "/#/pages/charge/machineToCarCharge?machineId="
                    + chargeMachineDtos.get(0).getMachineId()
                    + "&communityId=" + chargeMachineDtos.get(0).getCommunityId()
                    + "&wAppId=" + appId;
        } else {
            return ResultVo.error("不支持的设备");
        }

        return ResultVo.redirectPage(url);

    }

    /**
     * <p>充电结束通知</p>
     *
     * @param request
     * @throws Exception
     */
    @RequestMapping(path = "/{machineCode}/{portId}", method = RequestMethod.GET)
    public ResponseEntity<String> portQrcode(
            @PathVariable String machineCode,
            @PathVariable String portId,
            HttpServletRequest request) {

        ChargeMachineDto chargeMachineDto = new ChargeMachineDto();
        chargeMachineDto.setMachineCode(machineCode);
        List<ChargeMachineDto> chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(chargeMachineDto);
        if (ListUtil.isNull(chargeMachineDtos)) {
            return ResultVo.error("设备不存在");
        }

        String chargePath = MappingCache.getValue("SYSTEM_SWITCH","CHARGE_PAGE");
        if("MINI".equals(chargePath)){
            return pageMiniPagePort(chargeMachineDtos.get(0),portId);
        }

        SmallWeChatDto smallWeChatDto = new SmallWeChatDto();
        smallWeChatDto.setObjId(chargeMachineDtos.get(0).getCommunityId());
        smallWeChatDto.setWeChatType(SmallWeChatDto.WECHAT_TYPE_PUBLIC);
        List<SmallWeChatDto> smallWeChatDtos = smallWeChatInnerServiceSMOImpl.querySmallWeChats(smallWeChatDto);
        String appId = "";
        if (ListUtil.isNull(smallWeChatDtos)) {
            return ResultVo.error("小区未配置公众号信息");
        }
        appId = smallWeChatDtos.get(0).getAppId();

        String ownerUrl = UrlCache.getOwnerUrl();
        String url = "";
        if (ChargeMachineDto.CHARGE_TYPE_BIKE.equals(chargeMachineDtos.get(0).getChargeType())) {
            url = ownerUrl + "/#/pages/charge/machineToCharge?machineId="
                    + chargeMachineDtos.get(0).getMachineId()
                    + "&communityId=" + chargeMachineDtos.get(0).getCommunityId()
                    + "&wAppId=" + appId + "&portId=" + portId;
        } else if (ChargeMachineDto.CHARGE_TYPE_CAR.equals(chargeMachineDtos.get(0).getChargeType())) {
            url = ownerUrl + "/#/pages/charge/carChargePort?machineId="
                    + chargeMachineDtos.get(0).getMachineId()
                    + "&communityId=" + chargeMachineDtos.get(0).getCommunityId()
                    + "&wAppId=" + appId + "&portId=" + portId;
        } else {
            return ResultVo.error("不支持的设备");
        }

        return ResultVo.redirectPage(url);

    }

    private ResponseEntity<String> pageMiniPagePort(ChargeMachineDto chargeMachineDto, String portId) {
        String urlScheme;
        String path = "";
        String query = "";

        if (ChargeMachineDto.CHARGE_TYPE_BIKE.equals(chargeMachineDto.getChargeType())) {
            path = "/pages/charge/machineToCharge";
            query = "machineId=" + chargeMachineDto.getMachineId()
                    + "&communityId=" + chargeMachineDto.getCommunityId()
                    + "&portId=" + portId;
        } else if (ChargeMachineDto.CHARGE_TYPE_CAR.equals(chargeMachineDto.getChargeType())) {
            path = "/pages/charge/carChargePort";
            query = "machineId=" + chargeMachineDto.getMachineId()
                    + "&communityId=" + chargeMachineDto.getCommunityId()
                    + "&portId=" + portId;
        }

        urlScheme = getWechatURLScheme(chargeMachineDto.getCommunityId(), path, query);

        String html = getHtmlPage(urlScheme);


        return ResponseEntity.ok().body(html);
    }


    private ResponseEntity<String> pageMiniPage(ChargeMachineDto chargeMachineDto) {
        String urlScheme ;
        String path = "";
        String query = "";
        if (ChargeMachineDto.CHARGE_TYPE_BIKE.equals(chargeMachineDto.getChargeType())) {
            path = "/pages/charge/machineToCharge";
            query = "machineId="
                    + chargeMachineDto.getMachineId()
                    + "&communityId=" + chargeMachineDto.getCommunityId();
        } else if (ChargeMachineDto.CHARGE_TYPE_CAR.equals(chargeMachineDto.getChargeType())) {
            path = "/pages/charge/machineToCarCharge";
            query = "machineId="
                    + chargeMachineDto.getMachineId()
                    + "&communityId=" + chargeMachineDto.getCommunityId();
        }
        urlScheme = getWechatURLScheme(chargeMachineDto.getCommunityId(),path,query);
        String html = getHtmlPage(urlScheme);

        return ResponseEntity.ok().body(html);
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

        if(response.getIntValue("errcode") != 0){
            throw new IllegalArgumentException(response.getString("errmsg"));
        }
        return response.getString("openlink");
    }

}
