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
package com.java110.accessControl.cmd.attendance;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.*;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.intf.accessControl.IAttendanceMachineV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import com.java110.dto.attendanceMachine.AttendanceMachineDto;

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
 * 服务编码：attendanceMachine.listAttendanceMachine
 * 请求路劲：/app/attendanceMachine.ListAttendanceMachine
 * add by 吴学文 at 2024-01-24 13:44:37 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "attendance.listAttendanceMachine")
public class ListAttendanceMachineCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ListAttendanceMachineCmd.class);
    @Autowired
    private IAttendanceMachineV1InnerServiceSMO attendanceMachineV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        super.validatePageInfo(reqJson);
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        AttendanceMachineDto attendanceMachineDto = BeanConvertUtil.covertBean(reqJson, AttendanceMachineDto.class);

        int count = attendanceMachineV1InnerServiceSMOImpl.queryAttendanceMachinesCount(attendanceMachineDto);

        List<AttendanceMachineDto> attendanceMachineDtos = null;

        if (count > 0) {
            attendanceMachineDtos = attendanceMachineV1InnerServiceSMOImpl.queryAttendanceMachines(attendanceMachineDto);
            freshMachineStateName(attendanceMachineDtos);

        } else {
            attendanceMachineDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, attendanceMachineDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        cmdDataFlowContext.setResponseEntity(responseEntity);
    }
    private void freshMachineStateName(List<AttendanceMachineDto> attendanceMachineDtos) {

        if (ListUtil.isNull(attendanceMachineDtos)) {
            return;
        }
        for (AttendanceMachineDto attendanceMachineDto : attendanceMachineDtos) {
            String heartbeatTime = attendanceMachineDto.getHeartbeatTime();
            try {
                if (StringUtil.isEmpty(heartbeatTime)) {
                    attendanceMachineDto.setStateName("设备离线");
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(DateUtil.getDateFromString(heartbeatTime, DateUtil.DATE_FORMATE_STRING_A));
                    calendar.add(Calendar.MINUTE, 2);
                    if (calendar.getTime().getTime() <= DateUtil.getCurrentDate().getTime()) {
                        attendanceMachineDto.setStateName("设备离线");
                    } else {
                        attendanceMachineDto.setStateName("设备在线");
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
                attendanceMachineDto.setStateName("设备离线");
            }
        }
    }
}
