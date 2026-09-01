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
package com.java110.system.cmd.system;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.registerProtocol.RegisterProtocolDto;
import com.java110.bean.po.registerProtocol.RegisterProtocolPo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.intf.system.IRegisterProtocolV1InnerServiceSMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 类表述：保存注册协议（无记录插入、有记录更新，支持空串清空内容）
 * 服务编码：system.updateRegisterProtocol
 * 请求路劲：/iot/api/system.updateRegisterProtocol
 * add by java110 team at 2026-09-01
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "system.updateRegisterProtocol")
public class UpdateRegisterProtocolCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(UpdateRegisterProtocolCmd.class);

    @Autowired
    private IRegisterProtocolV1InnerServiceSMO registerProtocolV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKey(reqJson, "userProtocol", "用户协议不能为空");
        Assert.hasKey(reqJson, "merchantProtocol", "隐私保护指引不能为空");
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        RegisterProtocolDto registerProtocolDto = new RegisterProtocolDto();
        registerProtocolDto.setStatusCd("0");
        List<RegisterProtocolDto> registerProtocolDtos = registerProtocolV1InnerServiceSMOImpl.queryRegisterProtocols(registerProtocolDto);

        RegisterProtocolPo registerProtocolPo = new RegisterProtocolPo();
        registerProtocolPo.setProtolcolId("1");
        registerProtocolPo.setUserProtocol(reqJson.getString("userProtocol"));
        registerProtocolPo.setMerchantProtocol(reqJson.getString("merchantProtocol"));
        registerProtocolPo.setStatusCd("0");

        int flag;
        if (registerProtocolDtos == null || registerProtocolDtos.isEmpty()) {
            flag = registerProtocolV1InnerServiceSMOImpl.saveRegisterProtocol(registerProtocolPo);
        } else {
            flag = registerProtocolV1InnerServiceSMOImpl.updateRegisterProtocol(registerProtocolPo);
        }

        if (flag < 1) {
            throw new CmdException("保存协议失败");
        }

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
