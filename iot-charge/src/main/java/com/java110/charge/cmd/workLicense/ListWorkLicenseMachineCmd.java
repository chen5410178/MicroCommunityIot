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
package com.java110.charge.cmd.workLicense;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.charge.workLicense.IWorkLicenseAdapt;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.*;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.workLicenseFactory.WorkLicenseFactoryDto;
import com.java110.intf.charge.IWorkLicenseFactoryV1InnerServiceSMO;
import com.java110.intf.charge.IWorkLicenseMachineV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import com.java110.dto.workLicenseMachine.WorkLicenseMachineDto;

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
 * 服务编码：workLicenseMachine.listWorkLicenseMachine
 * 请求路劲：/app/workLicenseMachine.ListWorkLicenseMachine
 * add by 吴学文 at 2024-02-29 02:21:42 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "workLicense.listWorkLicenseMachine")
public class ListWorkLicenseMachineCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ListWorkLicenseMachineCmd.class);
    @Autowired
    private IWorkLicenseMachineV1InnerServiceSMO workLicenseMachineV1InnerServiceSMOImpl;

    @Autowired
    private IWorkLicenseFactoryV1InnerServiceSMO workLicenseFactoryV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        super.validatePageInfo(reqJson);
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {
        String storeId = CmdContextUtils.getStoreId(cmdDataFlowContext);

        WorkLicenseMachineDto workLicenseMachineDto = BeanConvertUtil.covertBean(reqJson, WorkLicenseMachineDto.class);
        workLicenseMachineDto.setStoreId(storeId);
        int count = workLicenseMachineV1InnerServiceSMOImpl.queryWorkLicenseMachinesCount(workLicenseMachineDto);

        List<WorkLicenseMachineDto> workLicenseMachineDtos = null;

        if (count > 0) {
            workLicenseMachineDtos = workLicenseMachineV1InnerServiceSMOImpl.queryWorkLicenseMachines(workLicenseMachineDto);
            freshMachineStateName(workLicenseMachineDtos);

        } else {
            workLicenseMachineDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, workLicenseMachineDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        cmdDataFlowContext.setResponseEntity(responseEntity);
    }

    private void freshMachineStateName(List<WorkLicenseMachineDto> workLicenseMachineDtos) {
        if (ListUtil.isNull(workLicenseMachineDtos)) {
            return;
        }
        for (WorkLicenseMachineDto accessControlDto : workLicenseMachineDtos) {
            String heartbeatTime = accessControlDto.getHeartbeatTime();
            try {
                if (StringUtil.isEmpty(heartbeatTime)) {
                    accessControlDto.setStateName("设备离线");
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(DateUtil.getDateFromString(heartbeatTime, DateUtil.DATE_FORMATE_STRING_A));
                    calendar.add(Calendar.MINUTE, 2);
                    if (calendar.getTime().getTime() <= DateUtil.getCurrentDate().getTime()) {
                        accessControlDto.setStateName("设备离线");
                    } else {
                        accessControlDto.setStateName("设备在线");
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
                accessControlDto.setStateName("设备离线");
            }

            if(!"设备离线".equals(accessControlDto.getStateName())){
                continue;
            }

            WorkLicenseFactoryDto workLicenseFactoryDto = null;

            workLicenseFactoryDto = new WorkLicenseFactoryDto();
            workLicenseFactoryDto.setFactoryId(accessControlDto.getImplBean());
            List<WorkLicenseFactoryDto> workLicenseFactoryDtos = workLicenseFactoryV1InnerServiceSMOImpl.queryWorkLicenseFactorys(workLicenseFactoryDto);

            if(ListUtil.isNull(workLicenseFactoryDtos)){
                continue;
            }

            IWorkLicenseAdapt workLicenseAdapt = ApplicationContextFactory.getBean(workLicenseFactoryDtos.get(0).getBeanImpl(), IWorkLicenseAdapt.class);
            if (workLicenseAdapt == null) {
                continue;
            }

            boolean hasOnline = workLicenseAdapt.hasOnLine(accessControlDto);
            if(hasOnline){
                accessControlDto.setStateName("设备在线");
            }
        }
    }
}
