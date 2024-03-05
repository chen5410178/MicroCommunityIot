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
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.*;
import com.java110.doc.annotation.*;
import com.java110.dto.parkingSpaceMachine.ParkingSpaceMachineDto;
import com.java110.dto.parkingSpaceMachineRel.ParkingSpaceMachineRelDto;
import com.java110.intf.car.IParkingSpaceV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import com.java110.dto.parking.ParkingSpaceDto;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 类表述：查询
 * 服务编码：parkingSpace.listParkingSpace
 * 请求路劲：/app/parkingSpace.ListParkingSpace
 * add by 吴学文 at 2023-08-23 10:01:12 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110CmdDoc(title = "查询停车位",
        description = "用于外系统查询停车位",
        httpMethod = "get",
        url = "http://{ip}:{port}/iot/api/parkingSpace.listParkingSpace",
        resource = "carDoc",
        author = "吴学文",
        serviceCode = "parkingSpace.listParkingSpace",
        seq = 5
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
                @Java110ParamDoc(parentNodeName = "data", name = "psId", type = "String", remark = "编号"),
                @Java110ParamDoc(parentNodeName = "data", name = "num", type = "String", remark = "名称"),
        }
)

@Java110ExampleDoc(
        reqBody = "http://{ip}:{port}/iot/api/parkingArea.listParkingArea?page=1&row=10&communityId=123123",
        resBody = "{\n" +
                "    \"code\": 0,\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"communityId\": \"102023021386560001\",\n" +
                "            \"createTime\": \"2023-08-23 09:07:59\",\n" +
                "            \"num\": \"2\",\n" +
                "            \"psId\": \"102023082384250002\",\n" +
                "            \"page\": -1,\n" +
                "            \"records\": 0,\n" +
                "            \"remark\": \"1\",\n" +
                "            \"row\": 0,\n" +
                "            \"statusCd\": \"0\",\n" +
                "            \"total\": 0,\n" +
                "            \"typeCd\": \"1001\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"msg\": \"成功\",\n" +
                "    \"page\": 0,\n" +
                "    \"records\": 1,\n" +
                "    \"rows\": 0,\n" +
                "    \"total\": 1\n" +
                "}"
)
@Java110Cmd(serviceCode = "parkingSpace.listParkingSpace")
public class ListParkingSpaceCmd extends Cmd {

  private static Logger logger = LoggerFactory.getLogger(ListParkingSpaceCmd.class);
    @Autowired
    private IParkingSpaceV1InnerServiceSMO parkingSpaceV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        super.validatePageInfo(reqJson);
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

           ParkingSpaceDto parkingSpaceDto = BeanConvertUtil.covertBean(reqJson, ParkingSpaceDto.class);

           int count = parkingSpaceV1InnerServiceSMOImpl.queryParkingSpacesCount(parkingSpaceDto);

           List<ParkingSpaceDto> parkingSpaceDtos = null;

           if (count > 0) {
               parkingSpaceDtos = parkingSpaceV1InnerServiceSMOImpl.queryParkingSpaces(parkingSpaceDto);
               freshMachineStateName(parkingSpaceDtos);

           } else {
               parkingSpaceDtos = new ArrayList<>();
           }

           ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, parkingSpaceDtos);

           ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

           cmdDataFlowContext.setResponseEntity(responseEntity);
    }

    private void freshMachineStateName(List<ParkingSpaceDto> parkingSpaceDtos) {
        if (ListUtil.isNull(parkingSpaceDtos)) {
            return;
        }
        for (ParkingSpaceDto parkingSpaceDto : parkingSpaceDtos) {

            if(ParkingSpaceMachineRelDto.PS_STATE_OCCUPY.equals(parkingSpaceDto.getPsState())){
                parkingSpaceDto.setPsStateName("占用");
            }else if(ParkingSpaceMachineRelDto.PS_STATE_FREE.equals(parkingSpaceDto.getPsState())){
                parkingSpaceDto.setPsStateName("空闲");
            }else{
                parkingSpaceDto.setPsStateName("未知");
            }

            if(ParkingSpaceMachineRelDto.LOCK_STATE_ON.equals(parkingSpaceDto.getLockState())){
                parkingSpaceDto.setPsStateName("开");
            }else if(ParkingSpaceMachineRelDto.LOCK_STATE_OFF.equals(parkingSpaceDto.getLockState())){
                parkingSpaceDto.setPsStateName("关");
            }else{
                parkingSpaceDto.setPsStateName("未知");
            }


            String heartbeatTime = parkingSpaceDto.getHeartbeatTime();
            try {
                if (StringUtil.isEmpty(heartbeatTime)) {
                    parkingSpaceDto.setMachineStateName("设备离线");
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(DateUtil.getDateFromString(heartbeatTime, DateUtil.DATE_FORMATE_STRING_A));
                    calendar.add(Calendar.MINUTE, 2);
                    if (calendar.getTime().getTime() <= DateUtil.getCurrentDate().getTime()) {
                        parkingSpaceDto.setMachineStateName("设备离线");
                    } else {
                        parkingSpaceDto.setMachineStateName("设备在线");
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
                parkingSpaceDto.setMachineStateName("设备离线");
            }
        }
    }
}
