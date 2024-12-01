package com.java110.gateway.app.charge;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.cache.UrlCache;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.LoggerFactory;
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
        String machineCode = no.substring(0,no.length()-2);
        String gunNo = no.substring(no.length()-2);
        ChargeMachineDto chargeMachineDto = new ChargeMachineDto();
        chargeMachineDto.setMachineCode(machineCode);
        List<ChargeMachineDto> chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(chargeMachineDto);
        if (ListUtil.isNull(chargeMachineDtos)) {
            return ResultVo.error("设备不存在");
        }

        ChargeMachinePortDto chargeMachinePortDto = new ChargeMachinePortDto();
        chargeMachinePortDto.setMachineId(chargeMachineDtos.get(0).getMachineId());
        chargeMachinePortDto.setPortCode(Integer.parseInt(gunNo, 16)+"");
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
                    + "&wAppId=" + appId+"&portId="+chargeMachinePortDtos.get(0).getPortId();
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
            @RequestParam(value = "socket",required = false,defaultValue = "222") String socket,
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
        if (!"222".equals(socket)){
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
            if (!"222".equals(socket)){
                url += "&portId=" + portId;
            }
        }  else {
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
                    + "&wAppId=" + appId+"&portId="+portId;
        } else if (ChargeMachineDto.CHARGE_TYPE_CAR.equals(chargeMachineDtos.get(0).getChargeType())) {
            url = ownerUrl + "/#/pages/charge/carChargePort?machineId="
                    + chargeMachineDtos.get(0).getMachineId()
                    + "&communityId=" + chargeMachineDtos.get(0).getCommunityId()
                    + "&wAppId=" + appId+"&portId="+portId;
        } else {
            return ResultVo.error("不支持的设备");
        }

        return ResultVo.redirectPage(url);

    }

}
