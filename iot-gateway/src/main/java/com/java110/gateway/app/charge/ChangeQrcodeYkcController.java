package com.java110.gateway.app.charge;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cache.UrlCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.chargeMachine.ChargeMachineDto;
import com.java110.dto.smallWeChat.SmallWeChatDto;
import com.java110.intf.charge.IChargeMachineOrderV1InnerServiceSMO;
import com.java110.intf.charge.IChargeMachineV1InnerServiceSMO;
import com.java110.intf.charge.ISmallWeChatInnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;
import java.util.Map;





@RestController
@RequestMapping(path = "/app")
public class ChangeQrcodeYkcController {
    private final static Logger logger = LoggerFactory.getLogger(ChangeQrcodeYkcController.class);


    @Autowired
    private IChargeMachineV1InnerServiceSMO chargeMachineV1InnerServiceSMOImpl;

    @Autowired
    private ISmallWeChatInnerServiceSMO smallWeChatInnerServiceSMOImpl;


    /**
     * <p>充电结束通知</p>
     *
     * @param request
     * @throws Exception
     */
    @RequestMapping(path = "/chargeYkc", method = RequestMethod.GET)
    public ResponseEntity<String> qrcode(
            @RequestParam("machineCode") String machineCode,
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

}
