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
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.Assert;
import com.java110.intf.barrier.ICarInoutEvasionV1InnerServiceSMO;
import com.java110.po.carInoutEvasion.CarInoutEvasionPo;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

@Java110Cmd(serviceCode = "carInoutEvasion.deleteCarInoutEvasion")
public class DeleteCarInoutEvasionCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(DeleteCarInoutEvasionCmd.class);

    @Autowired
    private ICarInoutEvasionV1InnerServiceSMO carInoutEvasionV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "evasionId", "请求报文中未包含evasionId");
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {
        CarInoutEvasionPo evasionPo = new CarInoutEvasionPo();
        evasionPo.setEvasionId(reqJson.getString("evasionId"));
        int flag = carInoutEvasionV1InnerServiceSMOImpl.deleteCarInoutEvasion(evasionPo);
        if (flag < 1) {
            throw new CmdException("删除数据失败");
        }
        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
