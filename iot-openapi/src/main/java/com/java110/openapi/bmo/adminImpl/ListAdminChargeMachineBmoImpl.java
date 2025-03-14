package com.java110.openapi.bmo.adminImpl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.cache.UrlCache;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.chargeMachine.ChargeMachineDto;
import com.java110.dto.chargeMachine.ChargeRuleFeeDto;
import com.java110.dto.chargeRulePrice.ChargeRulePriceDto;
import com.java110.dto.community.CommunityDto;
import com.java110.intf.charge.IChargeMachineV1InnerServiceSMO;
import com.java110.intf.charge.IChargeRuleFeeV1InnerServiceSMO;
import com.java110.intf.charge.IChargeRulePriceV1InnerServiceSMO;
import com.java110.intf.charge.ISmallWeChatInnerServiceSMO;
import com.java110.intf.community.ICommunityV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotAdminApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("listAdminChargeMachineBmoImpl")
public class ListAdminChargeMachineBmoImpl implements IIotAdminApiBmo {
    @Autowired
    private IChargeMachineV1InnerServiceSMO chargeMachineV1InnerServiceSMOImpl;

    @Autowired
    private ISmallWeChatInnerServiceSMO smallWeChatInnerServiceSMOImpl;

    @Autowired
    private IChargeRuleFeeV1InnerServiceSMO chargeRuleFeeV1InnerServiceSMOImpl;

    @Autowired
    private IChargeRulePriceV1InnerServiceSMO chargeRulePriceV1InnerServiceSMOImpl;



    @Autowired
    private ICommunityV1InnerServiceSMO communityV1InnerServiceSMOImpl;
    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
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



            // todo 刷入算费规则
            queryChargeRuleFee(chargeMachineDtos);
        } else {
            chargeMachineDtos = new ArrayList<>();
        }

        refreshCommunityName(chargeMachineDtos);


        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, chargeMachineDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }

    private void queryChargeRuleFee(List<ChargeMachineDto> chargeMachineDtos) {
        if (ListUtil.isNull(chargeMachineDtos)) {
            return;
        }

        for (ChargeMachineDto chargeMachineDto : chargeMachineDtos) {
            if (ChargeMachineDto.CHARGE_TYPE_BIKE.equals(chargeMachineDto.getChargeType())){
                ChargeRuleFeeDto chargeRuleFeeDto = new ChargeRuleFeeDto();
                chargeRuleFeeDto.setRuleId(chargeMachineDto.getRuleId());
                chargeRuleFeeDto.setCommunityId(chargeMachineDto.getCommunityId());
                chargeRuleFeeDto.setChargeType(chargeMachineDto.getChargeType());
                List<ChargeRuleFeeDto> fees = chargeRuleFeeV1InnerServiceSMOImpl.queryChargeRuleFees(chargeRuleFeeDto);
                chargeMachineDto.setFees(fees);
            }else if (ChargeMachineDto.CHARGE_TYPE_CAR.equals(chargeMachineDto.getChargeType())){
                ChargeRulePriceDto chargeRulePriceDto = new ChargeRulePriceDto();
                chargeRulePriceDto.setRuleId(chargeMachineDto.getRuleId());
                chargeRulePriceDto.setCommunityId(chargeMachineDto.getCommunityId());
                List<ChargeRulePriceDto> prices = chargeRulePriceV1InnerServiceSMOImpl.queryChargeRulePrices(chargeRulePriceDto);
                chargeMachineDto.setPrices(prices);
            }
        }
    }



    /**
     * 充电桩二维码
     *
     * @param chargeMachineDtos
     */
    private void freshQrCodeUrl(List<ChargeMachineDto> chargeMachineDtos) {

        if (ListUtil.isNull(chargeMachineDtos)) {
            return;
        }

        String ownerUrl = UrlCache.getOwnerUrl();
        for (ChargeMachineDto chargeMachineDto : chargeMachineDtos) {
            //todo 修改为短号
            chargeMachineDto.setQrCode(ownerUrl + "/app/charge/"+ chargeMachineDto.getMachineCode());
        }
    }

    private void refreshCommunityName(List<ChargeMachineDto> chargeMachineDtos) {
        if(ListUtil.isNull(chargeMachineDtos)){
            return;
        }

        List<String> communityIds = new ArrayList<>();
        for (ChargeMachineDto chargeMachineDto : chargeMachineDtos) {
            communityIds.add(chargeMachineDto.getCommunityId());
        }

        if(ListUtil.isNull(communityIds)){
            return ;
        }
        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityIds(communityIds.toArray(new String[communityIds.size()]));
        List<CommunityDto> communityDtos = communityV1InnerServiceSMOImpl.queryCommunitys(communityDto);
        if(ListUtil.isNull(communityDtos)){
            return;
        }
        for (ChargeMachineDto chargeMachineDto : chargeMachineDtos) {
            for (CommunityDto tCommunityDto : communityDtos) {
                if (!chargeMachineDto.getCommunityId().equals(tCommunityDto.getCommunityId())) {
                    continue;
                }
                chargeMachineDto.setCommunityName(tCommunityDto.getName());
            }
        }
    }
}
