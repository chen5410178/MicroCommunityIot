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
package com.java110.barrier.cmd.carInoutEvasion;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.Assert;
import com.java110.dto.carInoutEvasion.CarInoutEvasionConfigDto;
import com.java110.intf.barrier.ICarInoutEvasionConfigV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Java110Cmd(serviceCode = "carInoutEvasionConfig.listCarInoutEvasionConfig")
public class ListCarInoutEvasionConfigCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ListCarInoutEvasionConfigCmd.class);

    @Autowired
    private ICarInoutEvasionConfigV1InnerServiceSMO carInoutEvasionConfigV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {
        CarInoutEvasionConfigDto queryDto = new CarInoutEvasionConfigDto();
        queryDto.setCommunityId(reqJson.getString("communityId"));
        List<CarInoutEvasionConfigDto> configs = carInoutEvasionConfigV1InnerServiceSMOImpl.queryCarInoutEvasionConfigs(queryDto);
        cmdDataFlowContext.setResponseEntity(ResultVo.createResponseEntity(configs));
    }
}
