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
import com.java110.core.utils.BeanConvertUtil;
import com.java110.doc.annotation.*;
import com.java110.dto.chargeMachine.ChargeMachinePortDto;
import com.java110.intf.charge.IChargeMachinePortV1InnerServiceSMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * 类表述：查询
 * 服务编码：chargeMachinePort.listChargeMachinePort
 * 请求路劲：/app/chargeMachinePort.ListChargeMachinePort
 * add by 吴学文 at 2023-03-02 01:17:43 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */

@Java110CmdDoc(title = "查询充电桩插槽",
        description = "用于外系统查询充电桩插槽",
        httpMethod = "get",
        url = "http://{ip}:{port}/iot/api/chargeMachine.listChargeMachinePort",
        resource = "chargeDoc",
        author = "吴学文",
        serviceCode = "chargeMachine.listChargeMachinePort",
        seq = 14
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "page", type = "int", length = 11, remark = "页数"),
        @Java110ParamDoc(name = "row", type = "int", length = 1, remark = "行数"),
        @Java110ParamDoc(name = "communityId", length = 30, remark = "小区ID"),
        @Java110ParamDoc(name = "machineId", length = 30, remark = "设备ID"),
})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
                @Java110ParamDoc(name = "data", type = "Array", remark = "有效数据"),
                @Java110ParamDoc(parentNodeName = "data", name = "portCode", type = "String", remark = "插槽编号"),
                @Java110ParamDoc(parentNodeName = "data", name = "portName", type = "String", remark = "插槽名称"),
                @Java110ParamDoc(parentNodeName = "data", name = "state", type = "String", remark = "插槽状态"),
        }
)

@Java110ExampleDoc(
        reqBody = "http://{ip}:{port}/iot/api/chargeMachine.listChargeMachinePort?page=1&row=10&communityId=123123&machineId=123123",
        resBody = "{'code':0,'msg':'成功','data':[{'portCode':'123','portName':'通用充电桩插槽1','state':'FREE'}]}"
)
@Java110Cmd(serviceCode = "chargeMachine.listChargeMachinePort")
public class ListChargeMachinePortCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ListChargeMachinePortCmd.class);
    @Autowired
    private IChargeMachinePortV1InnerServiceSMO chargeMachinePortV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        super.validatePageInfo(reqJson);
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        ChargeMachinePortDto chargeMachinePortDto = BeanConvertUtil.covertBean(reqJson, ChargeMachinePortDto.class);

        int count = chargeMachinePortV1InnerServiceSMOImpl.queryChargeMachinePortsCount(chargeMachinePortDto);

        List<ChargeMachinePortDto> chargeMachinePortDtos = null;

        if (count > 0) {
            chargeMachinePortDtos = chargeMachinePortV1InnerServiceSMOImpl.queryChargeMachinePorts(chargeMachinePortDto);

            //调用 第三方查询 插槽状态
           // queryPortState(chargeMachinePortDtos);
        } else {
            chargeMachinePortDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, chargeMachinePortDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        cmdDataFlowContext.setResponseEntity(responseEntity);
    }

//    private void queryPortState(List<ChargeMachinePortDto> chargeMachinePortDtos) {
//        if (chargeMachinePortDtos == null || chargeMachinePortDtos.size() < 1) {
//            return;
//
//        }
//
//        for (ChargeMachinePortDto chargeMachinePortDto : chargeMachinePortDtos) {
//            chargeMachinePortDto.setStateName("空闲");
//        }
//    }
}
