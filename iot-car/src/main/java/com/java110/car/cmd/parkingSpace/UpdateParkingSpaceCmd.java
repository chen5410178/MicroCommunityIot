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
package com.java110.car.cmd.parkingSpace;

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
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.doc.annotation.*;
import com.java110.dto.monitor.MonitorMachineDto;
import com.java110.intf.car.IParkingSpaceV1InnerServiceSMO;
import com.java110.intf.monitor.IMonitorMachineV1InnerServiceSMO;
import com.java110.po.parking.ParkingSpacePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * 类表述：更新
 * 服务编码：parkingSpace.updateParkingSpace
 * 请求路劲：/app/parkingSpace.UpdateParkingSpace
 * add by 吴学文 at 2023-08-23 10:01:12 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */

@Java110CmdDoc(title = "修改停车位",
        description = "用于外系统修改停车位功能",
        httpMethod = "post",
        url = "http://{ip}:{port}/iot/api/parkingSpace.updateParkingSpace",
        resource = "carDoc",
        author = "吴学文",
        serviceCode = "parkingSpace.updateParkingSpace",
        seq = 3
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "communityId", length = 30, remark = "小区ID"),
        @Java110ParamDoc(name = "num",  length = 64, remark = "编号"),
        @Java110ParamDoc(name = "psId",  length = 64, remark = "ID"),
})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
        }
)

@Java110ExampleDoc(
        reqBody = "{\"num\":\"22\",\"psId\":\"22\",\"communityId\":\"2022081539020475\"}",
        resBody = "{'code':0,'msg':'成功'}"
)
@Java110Cmd(serviceCode = "parkingSpace.updateParkingSpace")
public class UpdateParkingSpaceCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(UpdateParkingSpaceCmd.class);


    @Autowired
    private IParkingSpaceV1InnerServiceSMO parkingSpaceV1InnerServiceSMOImpl;
    @Autowired
    private IMonitorMachineV1InnerServiceSMO monitorMachineV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "psId", "psId不能为空");
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
        String monitorId = reqJson.getString("monitorId");

        if (!StringUtil.isEmpty(monitorId) && !"-1".equals(monitorId)) {
            MonitorMachineDto monitorMachineDto = new MonitorMachineDto();
            monitorMachineDto.setMachineId(reqJson.getString("monitorId"));
            monitorMachineDto.setCommunityId(reqJson.getString("communityId"));
            List<MonitorMachineDto> monitorMachineDtos = monitorMachineV1InnerServiceSMOImpl.queryMonitorMachines(monitorMachineDto);
            if (ListUtil.isNull(monitorMachineDtos)) {
                throw new CmdException("监控设备不存在");
            }
            reqJson.put("monitorName", monitorMachineDtos.get(0).getMachineName());
        }
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        ParkingSpacePo parkingSpacePo = BeanConvertUtil.covertBean(reqJson, ParkingSpacePo.class);
        int flag = parkingSpaceV1InnerServiceSMOImpl.updateParkingSpace(parkingSpacePo);

        if (flag < 1) {
            throw new CmdException("更新数据失败");
        }

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
