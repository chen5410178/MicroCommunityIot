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
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.carInoutEvasion.CarInoutEvasionConfigDto;
import com.java110.intf.barrier.ICarInoutEvasionConfigV1InnerServiceSMO;
import com.java110.po.carInoutEvasion.CarInoutEvasionConfigPo;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Java110Cmd(serviceCode = "carInoutEvasionConfig.saveCarInoutEvasionConfig")
public class SaveCarInoutEvasionConfigCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(SaveCarInoutEvasionConfigCmd.class);

    public static final String CODE_PREFIX_ID = "11";

    @Autowired
    private ICarInoutEvasionConfigV1InnerServiceSMO carInoutEvasionConfigV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId");
        Assert.hasKeyAndValue(reqJson, "evasionOpen", "请求报文中未包含evasionOpen");
        Assert.hasKeyAndValue(reqJson, "queryDays", "请求报文中未包含queryDays");
        Assert.hasKeyAndValue(reqJson, "queryMinutes", "请求报文中未包含queryMinutes");
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {
        CarInoutEvasionConfigDto queryDto = new CarInoutEvasionConfigDto();
        queryDto.setCommunityId(reqJson.getString("communityId"));
        List<CarInoutEvasionConfigDto> existing = carInoutEvasionConfigV1InnerServiceSMOImpl.queryCarInoutEvasionConfigs(queryDto);

        if (existing != null && !existing.isEmpty()) {
            CarInoutEvasionConfigPo updatePo = new CarInoutEvasionConfigPo();
            updatePo.setConfigId(existing.get(0).getConfigId());
            updatePo.setEvasionOpen(reqJson.getString("evasionOpen"));
            updatePo.setQueryDays(reqJson.getString("queryDays"));
            updatePo.setQueryMinutes(reqJson.getString("queryMinutes"));
            int flag = carInoutEvasionConfigV1InnerServiceSMOImpl.updateCarInoutEvasionConfig(updatePo);
            if (flag < 1) {
                throw new CmdException("修改数据失败");
            }
        } else {
            CarInoutEvasionConfigPo configPo = BeanConvertUtil.covertBean(reqJson, CarInoutEvasionConfigPo.class);
            configPo.setConfigId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));
            int flag = carInoutEvasionConfigV1InnerServiceSMOImpl.saveCarInoutEvasionConfig(configPo);
            if (flag < 1) {
                throw new CmdException("保存数据失败");
            }
        }
        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
