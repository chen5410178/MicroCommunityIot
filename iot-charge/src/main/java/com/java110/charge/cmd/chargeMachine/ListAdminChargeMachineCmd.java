/*
 * Copyright 2017-2020 吴学文 and java110 team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.java110.charge.cmd.chargeMachine;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.charge.factory.IChargeCore;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cache.UrlCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.doc.annotation.*;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.chargeMachine.ChargeMachineDto;
import com.java110.dto.chargeMachine.ChargeRuleFeeDto;
import com.java110.dto.chargeRulePrice.ChargeRulePriceDto;
import com.java110.dto.community.CommunityDto;
import com.java110.intf.charge.IChargeMachineV1InnerServiceSMO;
import com.java110.intf.charge.IChargeRuleFeeV1InnerServiceSMO;
import com.java110.intf.charge.IChargeRulePriceV1InnerServiceSMO;
import com.java110.intf.charge.ISmallWeChatInnerServiceSMO;
import com.java110.intf.community.ICommunityV1InnerServiceSMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * 类表述：查询
 * 服务编码：chargeMachine.listChargeMachine
 * 请求路劲：/app/chargeMachine.ListChargeMachine
 * add by 吴学文 at 2023-03-02 01:06:24 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */

@Java110Cmd(serviceCode = "chargeMachine.listAdminChargeMachine")
public class ListAdminChargeMachineCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ListAdminChargeMachineCmd.class);
    @Autowired
    private IChargeMachineV1InnerServiceSMO chargeMachineV1InnerServiceSMOImpl;

    @Autowired
    private ISmallWeChatInnerServiceSMO smallWeChatInnerServiceSMOImpl;

    @Autowired
    private IChargeRuleFeeV1InnerServiceSMO chargeRuleFeeV1InnerServiceSMOImpl;

    @Autowired
    private IChargeRulePriceV1InnerServiceSMO chargeRulePriceV1InnerServiceSMOImpl;

    @Autowired
    private IChargeCore chargeCoreImpl;

    @Autowired
    private ICommunityV1InnerServiceSMO communityV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        super.validatePageInfo(reqJson);
        super.assertAdmin(cmdDataFlowContext);
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

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

        cmdDataFlowContext.setResponseEntity(responseEntity);
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
