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
package com.java110.lift.cmd.liftMachine;

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
import com.java110.dto.lift.LiftMachineDto;
import com.java110.dto.lift.LiftMachineReservationDto;
import com.java110.intf.lift.ILiftMachineV1InnerServiceSMO;
import com.java110.lift.factory.ILiftCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 类表述：删除
 * 服务编码：liftMachine.deleteLiftMachine
 * 请求路劲：/app/liftMachine.DeleteLiftMachine
 * add by 吴学文 at 2023-09-19 08:42:49 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "liftMachine.reservationLiftMachine")
public class ReservationLiftMachineCmd extends Cmd {
    private static Logger logger = LoggerFactory.getLogger(ReservationLiftMachineCmd.class);

    @Autowired
    private ILiftMachineV1InnerServiceSMO liftMachineV1InnerServiceSMOImpl;

    @Autowired
    private ILiftCore liftCoreImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "machineId", "未包含电梯");
        Assert.hasKeyAndValue(reqJson, "resWay", "未包含预约方式");
        Assert.hasKeyAndValue(reqJson, "personWay", "未包含人员类型");
        Assert.hasKeyAndValue(reqJson, "orgLayer", "未包含原始楼层");
        Assert.hasKeyAndValue(reqJson, "targetLayer", "未包含目标楼层");

        LiftMachineDto liftMachineDto = new LiftMachineDto();
        liftMachineDto.setMachineId(reqJson.getString("machineId"));
        List<LiftMachineDto> liftMachineDtos = liftMachineV1InnerServiceSMOImpl.queryLiftMachines(liftMachineDto);
        Assert.listOnlyOne(liftMachineDtos, "电梯 不存在");

        //todo: 数据校验
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        LiftMachineReservationDto liftMachineReservationDto = BeanConvertUtil.covertBean(reqJson, LiftMachineReservationDto.class);

        ResultVo resultVo = liftCoreImpl.reservationLiftMachine(liftMachineReservationDto);

        cmdDataFlowContext.setResponseEntity(ResultVo.createResponseEntity(resultVo));
    }
}
