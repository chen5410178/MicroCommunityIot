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
package com.java110.accessControl.cmd.accessControl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.StringUtil;
import com.java110.doc.annotation.*;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import com.java110.dto.accessControl.AccessControlDto;

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
 * 服务编码：accessControl.listAccessControl
 * 请求路劲：/app/accessControl.ListAccessControl
 * add by 吴学文 at 2023-08-15 02:16:27 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110CmdDoc(title = "查询门禁",
        description = "用于外系统查询门禁",
        httpMethod = "get",
        url = "http://{ip}:{port}/iot/api/accessControl.listAccessControl",
        resource = "accessControlDoc",
        author = "吴学文",
        serviceCode = "accessControl.listAccessControl",
        seq = 1
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
                @Java110ParamDoc(parentNodeName = "data", name = "machineId", type = "String", remark = "编号"),
                @Java110ParamDoc(parentNodeName = "data", name = "machineName", type = "String", remark = "名称"),
        }
)

@Java110ExampleDoc(
        reqBody = "http://{ip}:{port}/iot/api/accessControl.listAccessControl?page=1&row=10&communityId=123123",
        resBody = "{'code':0,'msg':'成功','data':[{'locationId':'123123','locationName':'123213'}]}"
)
@Java110Cmd(serviceCode = "accessControl.listAccessControl")
public class ListAccessControlCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ListAccessControlCmd.class);
    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        super.validatePageInfo(reqJson);
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        AccessControlDto accessControlDto = BeanConvertUtil.covertBean(reqJson, AccessControlDto.class);

        int count = accessControlV1InnerServiceSMOImpl.queryAccessControlsCount(accessControlDto);

        List<AccessControlDto> accessControlDtos = null;

        if (count > 0) {
            accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);
            freshMachineStateName(accessControlDtos);
        } else {
            accessControlDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, accessControlDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        cmdDataFlowContext.setResponseEntity(responseEntity);
    }

    private void freshMachineStateName(List<AccessControlDto> accessControlDtos) {

        if (accessControlDtos == null || accessControlDtos.size() < 1) {
            return;
        }
        for (AccessControlDto accessControlDto : accessControlDtos) {
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
        }
    }
}
