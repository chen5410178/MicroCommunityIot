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
package com.java110.community.cmd.location;

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
import com.java110.intf.community.IMachineLocationV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import com.java110.bean.dto.location.MachineLocationDto;
import java.util.List;
import java.util.ArrayList;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Java110CmdDoc(title = "查询设备位置",
        description = "用于外系统查询设备位置功能",
        httpMethod = "get",
        url = "http://{ip}:{port}/iot/api/location.listMachineLocation",
        resource = "communityDoc",
        author = "吴学文",
        serviceCode = "location.listMachineLocation"
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "communityId", length = 30, remark = "小区ID"),
        @Java110ParamDoc(name = "page", type = "int", length = 11, remark = "页数"),
        @Java110ParamDoc(name = "row", type = "int", length = 11, remark = "行数"),
})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
                @Java110ParamDoc(name = "data", type = "Array", remark = "有效数据"),
                @Java110ParamDoc(parentNodeName = "data", name = "locationId", type = "String", remark = "编号"),
                @Java110ParamDoc(parentNodeName = "data", name = "locationName", type = "String", remark = "名称"),
        }
)

@Java110ExampleDoc(
        reqBody = "http://{ip}:{port}/iot/api/location.listMachineLocation?page=1&row=10&communityId=123123",
        resBody = "{'code':0,'msg':'成功','data':[{'locationId':'123123','locationName':'123213'}]}"
)

@Java110Cmd(serviceCode = "location.listMachineLocation")
public class ListMachineLocationCmd extends Cmd {

  private static Logger logger = LoggerFactory.getLogger(ListMachineLocationCmd.class);
    @Autowired
    private IMachineLocationV1InnerServiceSMO machineLocationV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        super.validatePageInfo(reqJson);
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

           MachineLocationDto machineLocationDto = BeanConvertUtil.covertBean(reqJson, MachineLocationDto.class);

           int count = machineLocationV1InnerServiceSMOImpl.queryMachineLocationsCount(machineLocationDto);

           List<MachineLocationDto> machineLocationDtos = null;

           if (count > 0) {
               machineLocationDtos = machineLocationV1InnerServiceSMOImpl.queryMachineLocations(machineLocationDto);
           } else {
               machineLocationDtos = new ArrayList<>();
           }

           ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, machineLocationDtos);

           ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

           cmdDataFlowContext.setResponseEntity(responseEntity);
    }
}
