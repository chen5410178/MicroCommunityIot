package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.cache.UrlCache;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.chargeMachine.ChargeMachineDto;
import com.java110.dto.chargeMachine.ChargeRuleFeeDto;
import com.java110.dto.smallWeChat.SmallWeChatDto;
import com.java110.intf.charge.IChargeMachineV1InnerServiceSMO;
import com.java110.intf.charge.IChargeRuleFeeV1InnerServiceSMO;
import com.java110.intf.charge.ISmallWeChatInnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service("listChargeMachineBmoImpl")
public class ListChargeMachineBmoImpl implements IIotCommonApiBmo {

    private static Logger logger = LoggerFactory.getLogger(ListChargeMachineBmoImpl.class);
    @Autowired
    private IChargeMachineV1InnerServiceSMO chargeMachineV1InnerServiceSMOImpl;

    @Autowired
    private ISmallWeChatInnerServiceSMO smallWeChatInnerServiceSMOImpl;

    @Autowired
    private IChargeRuleFeeV1InnerServiceSMO chargeRuleFeeV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
        Assert.hasKeyAndValue(reqJson, "page", "未包含page");
        Assert.hasKeyAndValue(reqJson, "row", "未包含row");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        ChargeMachineDto chargeMachineDto = BeanConvertUtil.covertBean(reqJson, ChargeMachineDto.class);

        int count = chargeMachineV1InnerServiceSMOImpl.queryChargeMachinesCount(chargeMachineDto);

        List<ChargeMachineDto> chargeMachineDtos = null;

        if (count > 0) {
            chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(chargeMachineDto);
            freshQrCodeUrl(chargeMachineDtos);
// todo  查询设备是否在线
            queryChargeMachineState(chargeMachineDtos);
            // todo 刷入算费规则
            queryChargeRuleFee(chargeMachineDtos);
        } else {
            chargeMachineDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, chargeMachineDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }

    private void queryChargeRuleFee(List<ChargeMachineDto> chargeMachineDtos) {
        if (chargeMachineDtos == null || chargeMachineDtos.size() != 1) {
            return;
        }

        ChargeRuleFeeDto chargeRuleFeeDto = new ChargeRuleFeeDto();
        chargeRuleFeeDto.setRuleId(chargeMachineDtos.get(0).getRuleId());
        chargeRuleFeeDto.setCommunityId(chargeMachineDtos.get(0).getCommunityId());
        chargeRuleFeeDto.setChargeType(chargeMachineDtos.get(0).getChargeType());
        List<ChargeRuleFeeDto> fees = chargeRuleFeeV1InnerServiceSMOImpl.queryChargeRuleFees(chargeRuleFeeDto);

        chargeMachineDtos.get(0).setFees(fees);

    }

    /**
     * 充电桩二维码
     *
     * @param chargeMachineDtos
     */
    private void freshQrCodeUrl(List<ChargeMachineDto> chargeMachineDtos) {

        if (chargeMachineDtos == null || chargeMachineDtos.size() < 1) {
            return;
        }

        SmallWeChatDto smallWeChatDto = new SmallWeChatDto();
        smallWeChatDto.setObjId(chargeMachineDtos.get(0).getCommunityId());
        smallWeChatDto.setWeChatType(SmallWeChatDto.WECHAT_TYPE_PUBLIC);
        List<SmallWeChatDto> smallWeChatDtos = smallWeChatInnerServiceSMOImpl.querySmallWeChats(smallWeChatDto);
        String appId = "";
        if (smallWeChatDtos != null && smallWeChatDtos.size() > 0) {
            appId = smallWeChatDtos.get(0).getAppId();
        }
        String ownerUrl = UrlCache.getOwnerUrl();

        for (ChargeMachineDto chargeMachineDto : chargeMachineDtos) {
            chargeMachineDto.setQrCode(ownerUrl + "/#/packageCharge/pages/charge/machineToCharge?machineId="
                    + chargeMachineDto.getMachineId()
                    + "&communityId=" + chargeMachineDto.getCommunityId()
                    + "&wAppId=" + appId
            );
        }
    }

    public void queryChargeMachineState(List<ChargeMachineDto> chargeMachineDtos) {

        for (ChargeMachineDto chargeMachineDto : chargeMachineDtos) {

            String heartbeatTime = chargeMachineDto.getHeartbeatTime();
            try {
                if (StringUtil.isEmpty(heartbeatTime)) {
                    chargeMachineDto.setStateName("设备离线");
                    chargeMachineDto.setState("OFFLINE");
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(DateUtil.getDateFromString(heartbeatTime, DateUtil.DATE_FORMATE_STRING_A));
                    calendar.add(Calendar.MINUTE, 2);
                    if (calendar.getTime().getTime() <= DateUtil.getCurrentDate().getTime()) {
                        chargeMachineDto.setStateName("设备离线");
                        chargeMachineDto.setState("OFFLINE");
                    } else {
                        chargeMachineDto.setStateName("设备在线");
                        chargeMachineDto.setState("ONLINE");
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
                chargeMachineDto.setStateName("设备离线");
                chargeMachineDto.setState("OFFLINE");
            }
        }
    }
}
