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
package com.java110.barrier.cmd.barrier;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.doc.annotation.*;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.community.CommunityDto;
import com.java110.dto.parking.ParkingBoxAreaDto;
import com.java110.intf.barrier.IBarrierV1InnerServiceSMO;
import com.java110.intf.barrier.IParkingBoxAreaV1InnerServiceSMO;
import com.java110.intf.community.ICommunityInnerServiceSMO;
import com.java110.intf.community.IFloorInnerServiceSMO;
import com.java110.intf.community.IRoomInnerServiceSMO;
import com.java110.intf.community.IUnitInnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Java110CmdDoc(title = "查询停车场设备",
        description = "查询系统中的停车场设备",
        httpMethod = "get",
        url = "http://{ip}:{port}/app/machine.listParkingAreaMachines",
        resource = "commonDoc",
        author = "吴学文",
        serviceCode = "machine.listParkingAreaMachines"
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "page", type = "int", length = 11, remark = "分页页数"),
        @Java110ParamDoc(name = "row", type = "int", length = 11, remark = "分页行数"),
        @Java110ParamDoc(name = "paId", length = 30, remark = "停车场Id"),
        @Java110ParamDoc(name = "communityId", length = 30, remark = "小区ID"),
})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
                @Java110ParamDoc(name = "data", type = "Array", remark = "有效数据"),
                @Java110ParamDoc(parentNodeName = "data", name = "machineId", type = "String", remark = "设备ID"),
                @Java110ParamDoc(parentNodeName = "data", name = "machineCode", type = "String", remark = "设备编号"),
                @Java110ParamDoc(parentNodeName = "data", name = "machineIp", type = "String", remark = "设备IP"),
                @Java110ParamDoc(parentNodeName = "data", name = "direction", type = "String", remark = "方向"),
        }
)

@Java110ExampleDoc(
        reqBody = "http://{ip}:{port}/app/machine.listParkingAreaMachines?paId=123123&communityId=123213&page=1&row=10",
        resBody = "{\"data\":[{\"machineId\":\"123123\",\"machineCode\":\"123123123\",\"machineIp\":\"192.168.1.1\",\"direction\":\"3306\"}],\"page\":0,\"records\":1,\"rows\":0,\"total\":2}"
)
/**
 * 类表述： 查询停车场 下的所有摄像头
 * 服务编码：machine.listMachine
 * 请求路劲：/app/machine.ListMachine
 * add by 吴学文 at 2021-11-09 15:42:41 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "barrier.listParkingAreaMachines")
public class ListParkingAreaMachinesCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ListParkingAreaMachinesCmd.class);
    @Autowired
    private IBarrierV1InnerServiceSMO machineInnerServiceSMOImpl;

    @Autowired
    private ICommunityInnerServiceSMO communityInnerServiceSMOImpl;

    @Autowired
    private IFloorInnerServiceSMO floorInnerServiceSMOImpl;

    @Autowired
    private IUnitInnerServiceSMO unitInnerServiceSMOImpl;

    @Autowired
    private IRoomInnerServiceSMO roomInnerServiceSMOImpl;


    @Autowired
    private IParkingBoxAreaV1InnerServiceSMO parkingBoxAreaV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        super.validatePageInfo(reqJson);

        Assert.jsonObjectHaveKey(reqJson, "communityId", "请求报文中未包含小区信息");
        Assert.jsonObjectHaveKey(reqJson, "paId", "请求报文中未包含停车场");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        List<BarrierDto> machines = null;
        ParkingBoxAreaDto parkingBoxAreaDto = new ParkingBoxAreaDto();
        parkingBoxAreaDto.setCommunityId(reqJson.getString("communityId"));
        parkingBoxAreaDto.setPaId(reqJson.getString("paId"));
        parkingBoxAreaDto.setDefaultArea(ParkingBoxAreaDto.DEFAULT_AREA_TRUE);
        List<ParkingBoxAreaDto> parkingBoxAreaDtos = parkingBoxAreaV1InnerServiceSMOImpl.queryParkingBoxAreas(parkingBoxAreaDto);

        if (ListUtil.isNull(parkingBoxAreaDtos)) {
            machines = new ArrayList<>();
            cmdDataFlowContext.setResponseEntity(ResultVo.createResponseEntity(0, 0, machines));
            return;
        }

        List<String> boxIds = new ArrayList<>();

        for (ParkingBoxAreaDto tmpParkingBoxAreaDto : parkingBoxAreaDtos) {
            boxIds.add(tmpParkingBoxAreaDto.getBoxId());
        }

        BarrierDto machineDto = new BarrierDto();
        machineDto.setBoxIds(boxIds.toArray(new String[boxIds.size()]));
        machineDto.setCommunityId(reqJson.getString("communityId"));
        machineDto.setDirection(reqJson.getString("direction"));
        int count = machineInnerServiceSMOImpl.queryBarriersCount(machineDto);

        if (count > 0) {
            machines = machineInnerServiceSMOImpl.queryBarriers(machineDto);
        } else {
            machines = new ArrayList<>();
        }

        cmdDataFlowContext.setResponseEntity(ResultVo.createResponseEntity((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, machines));

    }


}
