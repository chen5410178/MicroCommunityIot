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
package com.java110.lamp.cmd.lampMachine;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.lamp.LampMachineDto;
import com.java110.intf.lamp.ILampMachineV1InnerServiceSMO;
import com.java110.lamp.factory.ILampCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

@Java110Cmd(serviceCode = "lampMachine.switchLampState")
public class SwitchLampStateCmd extends Cmd {
    private static Logger logger = LoggerFactory.getLogger(SwitchLampStateCmd.class);

    @Autowired
    private ILampMachineV1InnerServiceSMO lampMachineV1InnerServiceSMOImpl;

    @Autowired
    private ILampCore lampCoreImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区信息");
        Assert.hasKeyAndValue(reqJson, "machineId", "未包含路灯");
        Assert.hasKeyAndValue(reqJson, "logAction", "未包含路灯动作");
        Assert.hasKeyAndValue(reqJson, "userId", "未包含操作人");
        Assert.hasKeyAndValue(reqJson, "state", "未包含路灯状态");

        LampMachineDto lampMachineDto = new LampMachineDto();
        lampMachineDto.setMachineId(reqJson.getString("machineId"));
        lampMachineDto.setCommunityId(reqJson.getString("communityId"));
        List<LampMachineDto> lampMachineDtos = lampMachineV1InnerServiceSMOImpl.queryLampMachines(lampMachineDto);
        Assert.listOnlyOne(lampMachineDtos, "路灯 不存在");
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        LampMachineDto lampMachineDto = BeanConvertUtil.covertBean(reqJson, LampMachineDto.class);
        lampMachineDto.setOperationTime(new Date());

        ResultVo resultVo = lampCoreImpl.switchLampState(lampMachineDto);

        cmdDataFlowContext.setResponseEntity(ResultVo.createResponseEntity(resultVo));
    }
}
