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
import com.java110.core.utils.*;
import com.java110.doc.annotation.*;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.chargeMachine.ChargeMachineDto;
import com.java110.dto.chargeMachine.ChargeRuleFeeDto;
import com.java110.dto.chargeRulePrice.ChargeRulePriceDto;
import com.java110.dto.smallWeChat.SmallWeChatDto;
import com.java110.intf.charge.IChargeMachineV1InnerServiceSMO;
import com.java110.intf.charge.IChargeRuleFeeV1InnerServiceSMO;
import com.java110.intf.charge.IChargeRulePriceV1InnerServiceSMO;
import com.java110.intf.charge.ISmallWeChatInnerServiceSMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
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

@Java110CmdDoc(title = "查询充电桩",
        description = "用于外系统查询充电桩",
        httpMethod = "get",
        url = "http://{ip}:{port}/iot/api/chargeMachine.listChargeMachine",
        resource = "chargeDoc",
        author = "吴学文",
        serviceCode = "chargeMachine.listChargeMachine",
        seq = 9
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "communityId", length = 30, remark = "小区ID"),
        @Java110ParamDoc(name = "page", type = "int", length = 11, remark = "页数"),
        @Java110ParamDoc(name = "row", type = "int", length = 11, remark = "行数"),
})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
                @Java110ParamDoc(name = "data", type = "Array", remark = "有效数据"),
                @Java110ParamDoc(parentNodeName = "data", name = "machineId", type = "String", remark = "设备ID"),
                @Java110ParamDoc(parentNodeName = "data", name = "machineName", type = "String", remark = "设备名称"),
                @Java110ParamDoc(parentNodeName = "data", name = "machineCode", type = "String", remark = "设备编码"),
                @Java110ParamDoc(parentNodeName = "data", name = "chargeType", type = "String", remark = "充电类型"),
        }
)

@Java110ExampleDoc(
        reqBody = "http://{ip}:{port}/iot/api/chargeMachine.listChargeMachine?page=1&row=10&communityId=123123",
        resBody = "{'code':0,'msg':'成功','data':[{'machineId':'123123','machineName':'123213','machineCode':'123213','chargeType':'1001'}]}"
)
@Java110Cmd(serviceCode = "chargeMachine.listChargeMachine")
public class ListChargeMachineCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ListChargeMachineCmd.class);
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

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        super.validatePageInfo(reqJson);
        //super.assertProperty(cmdDataFlowContext);
        Assert.hasKeyAndValue(reqJson, "communityId", "查询小区ID");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        ChargeMachineDto chargeMachineDto = BeanConvertUtil.covertBean(reqJson, ChargeMachineDto.class);

        int count = chargeMachineV1InnerServiceSMOImpl.queryChargeMachinesCount(chargeMachineDto);

        List<ChargeMachineDto> chargeMachineDtos = null;

        if (count > 0) {
            chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(chargeMachineDto);
            freshQrCodeUrl(chargeMachineDtos);

            // todo  查询设备是否在线
            queryMachineState(chargeMachineDtos);

            // todo 刷入算费规则
            queryChargeRuleFee(chargeMachineDtos);
        } else {
            chargeMachineDtos = new ArrayList<>();
        }

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

    private void queryMachineState(List<ChargeMachineDto> chargeMachineDtos) {

        if (ListUtil.isNull(chargeMachineDtos) || chargeMachineDtos.size() > 10) {
            return;
        }

        chargeCoreImpl.queryChargeMachineState(chargeMachineDtos);


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
//            if (ChargeMachineDto.CHARGE_TYPE_BIKE.equals(chargeMachineDto.getChargeType())){
//                chargeMachineDto.setQrCode(ownerUrl + "/#/pages/charge/machineToCharge?machineId="
//                        + chargeMachineDto.getMachineId()
//                        + "&communityId=" + chargeMachineDto.getCommunityId()
//                        + "&wAppId=" + appId
//                );
//            }else if (ChargeMachineDto.CHARGE_TYPE_CAR.equals(chargeMachineDto.getChargeType())){
//                chargeMachineDto.setQrCode(ownerUrl + "/#/pages/charge/machineToCarCharge?machineId="
//                        + chargeMachineDto.getMachineId()
//                        + "&communityId=" + chargeMachineDto.getCommunityId()
//                        + "&wAppId=" + appId
//                );
//            }
            //todo 修改为短号
            chargeMachineDto.setQrCode(ownerUrl + "/app/charge/"+ chargeMachineDto.getMachineCode());
        }
    }
}
