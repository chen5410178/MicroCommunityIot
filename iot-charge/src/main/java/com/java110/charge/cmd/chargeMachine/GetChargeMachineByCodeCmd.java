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
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.doc.annotation.*;
import com.java110.dto.chargeMachine.ChargeMachineDto;
import com.java110.dto.chargeMachine.ChargeMachinePortDto;
import com.java110.intf.charge.IChargeMachinePortV1InnerServiceSMO;
import com.java110.intf.charge.IChargeMachineV1InnerServiceSMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 类表述：按设备编码查询充电桩（扫码直达充电页）
 * 服务编码：chargeMachine.getAppChargeMachineByCode
 * 请求路劲：/iot/api/chargeMachine.getAppChargeMachineByCode
 * add by 吴学文 at 2026-09-02 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */

@Java110CmdDoc(title = "按设备编码查询充电桩",
        description = "用于小程序扫码直达充电页，按设备编码查询充电桩及端口信息",
        httpMethod = "get",
        url = "http://{ip}:{port}/iot/api/chargeMachine.getAppChargeMachineByCode",
        resource = "chargeDoc",
        author = "吴学文",
        serviceCode = "chargeMachine.getAppChargeMachineByCode",
        seq = 15
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "machineCode", length = 64, remark = "设备编码"),
        @Java110ParamDoc(name = "portCode", length = 30, remark = "端口/插座编码（可选）"),
})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
                @Java110ParamDoc(name = "data", type = "Object", remark = "有效数据"),
                @Java110ParamDoc(parentNodeName = "data", name = "machineId", type = "String", remark = "设备ID"),
                @Java110ParamDoc(parentNodeName = "data", name = "machineName", type = "String", remark = "设备名称"),
                @Java110ParamDoc(parentNodeName = "data", name = "machineCode", type = "String", remark = "设备编码"),
                @Java110ParamDoc(parentNodeName = "data", name = "communityId", type = "String", remark = "小区ID"),
                @Java110ParamDoc(parentNodeName = "data", name = "chargeType", type = "String", remark = "充电类型 1001 两轮 1002 汽车"),
                @Java110ParamDoc(parentNodeName = "data", name = "portId", type = "String", remark = "端口ID"),
        }
)

@Java110ExampleDoc(
        reqBody = "http://{ip}:{port}/iot/api/chargeMachine.getAppChargeMachineByCode?machineCode=1001xxxxx&portCode=1",
        resBody = "{'code':0,'msg':'成功','data':{'machineId':'123123','machineName':'123213','machineCode':'1001xxxxx','communityId':'123123','chargeType':'1001','portId':'123123'}}"
)
@Java110Cmd(serviceCode = "chargeMachine.getAppChargeMachineByCode")
public class GetChargeMachineByCodeCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(GetChargeMachineByCodeCmd.class);

    @Autowired
    private IChargeMachineV1InnerServiceSMO chargeMachineV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMachinePortV1InnerServiceSMO chargeMachinePortV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "machineCode", "设备编码不能为空");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        ChargeMachineDto chargeMachineDto = new ChargeMachineDto();
        chargeMachineDto.setMachineCode(reqJson.getString("machineCode"));
        List<ChargeMachineDto> chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(chargeMachineDto);
        if (ListUtil.isNull(chargeMachineDtos)) {
            cmdDataFlowContext.setResponseEntity(ResultVo.error("充电桩不存在"));
            return;
        }

        ChargeMachineDto machine = chargeMachineDtos.get(0);

        // 按端口编码查询端口，返回 portId
        String portId = "";
        String portCode = reqJson.getString("portCode");
        if (!StringUtil.isEmpty(portCode)) {
            ChargeMachinePortDto chargeMachinePortDto = new ChargeMachinePortDto();
            chargeMachinePortDto.setMachineId(machine.getMachineId());
            chargeMachinePortDto.setPortCode(portCode);
            List<ChargeMachinePortDto> chargeMachinePortDtos = chargeMachinePortV1InnerServiceSMOImpl.queryChargeMachinePorts(chargeMachinePortDto);
            if (!ListUtil.isNull(chargeMachinePortDtos)) {
                portId = chargeMachinePortDtos.get(0).getPortId();
            }
        }

        JSONObject data = new JSONObject();
        data.put("machineId", machine.getMachineId());
        data.put("machineName", machine.getMachineName());
        data.put("machineCode", machine.getMachineCode());
        data.put("communityId", machine.getCommunityId());
        data.put("chargeType", machine.getChargeType());
        data.put("portId", portId);

        cmdDataFlowContext.setResponseEntity(ResultVo.createResponseEntity(new ResultVo(data)));
    }
}
