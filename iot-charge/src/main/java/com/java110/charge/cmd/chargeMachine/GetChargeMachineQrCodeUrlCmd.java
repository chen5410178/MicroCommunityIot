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
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cache.UrlCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ListUtil;
import com.java110.dto.chargeMachine.ChargeMachineDto;
import com.java110.intf.charge.IChargeMachineV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 类表述：查询充电桩小程序/公众号双二维码短码地址
 * 服务编码：chargeMachine.getChargeMachineQrCodeUrl
 * 请求路劲：/app/chargeMachine.getChargeMachineQrCodeUrl
 * add by wuxw at 2026-09-01
 */
@Java110Cmd(serviceCode = "chargeMachine.getChargeMachineQrCodeUrl")
public class GetChargeMachineQrCodeUrlCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(GetChargeMachineQrCodeUrlCmd.class);

    @Autowired
    private IChargeMachineV1InnerServiceSMO chargeMachineV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "machineCode", "请求报文中未包含设备编码");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        ChargeMachineDto chargeMachineDto = new ChargeMachineDto();
        chargeMachineDto.setMachineCode(reqJson.getString("machineCode"));
        List<ChargeMachineDto> chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(chargeMachineDto);
        if (ListUtil.isNull(chargeMachineDtos)) {
            throw new CmdException("充电设备不存在");
        }

        String ownerUrl = UrlCache.getOwnerUrl();
        String machineCode = chargeMachineDtos.get(0).getMachineCode();
        String url = "";
        String miniUrl = "";
        if (ChargeMachineDto.CHARGE_TYPE_BIKE.equals(chargeMachineDtos.get(0).getChargeType())) {
            miniUrl = ownerUrl + "/app/chargeBikeMini/" + machineCode;
            url = ownerUrl + "/app/chargeBikeH5/" + machineCode;
        } else if (ChargeMachineDto.CHARGE_TYPE_CAR.equals(chargeMachineDtos.get(0).getChargeType())) {
            miniUrl = ownerUrl + "/app/chargeCarMini/" + machineCode;
            url = ownerUrl + "/app/chargeCarH5/" + machineCode;
        } else {
            throw new CmdException("不支持的设备");
        }

        reqJson.put("url", url);
        reqJson.put("miniUrl", miniUrl);
        cmdDataFlowContext.setResponseEntity(ResultVo.createResponseEntity(reqJson));
    }
}
