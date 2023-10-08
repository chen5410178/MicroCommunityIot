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
package com.java110.system.cmd.eventStaff;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.event.EventStaffDto;
import com.java110.intf.system.IEventStaffV1InnerServiceSMO;
import com.java110.po.event.EventStaffPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 类表述：保存
 * 服务编码：eventStaff.saveEventStaff
 * 请求路劲：/app/eventStaff.SaveEventStaff
 * add by 吴学文 at 2023-09-27 16:00:15 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "eventStaff.saveEventStaff")
public class SaveEventStaffCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(SaveEventStaffCmd.class);

    public static final String CODE_PREFIX_ID = "10";

    @Autowired
    private IEventStaffV1InnerServiceSMO eventStaffV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "ruleId", "请求报文中未包含ruleId");
        Assert.hasKeyAndValue(reqJson, "staffIdAndNameList", "请求报文中未包含staffIdAndNameList");
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId");
        Assert.hasKeyAndValue(reqJson, "state", "请求报文中未包含state");
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        List<EventStaffPo> eventStaffPoList = new ArrayList<>();
        JSONArray staffIdAndNameList = reqJson.getJSONArray("staffIdAndNameList");
        if (!CollectionUtils.isEmpty(staffIdAndNameList)) {
            for (int i = 0; i < staffIdAndNameList.size(); i++) {
                EventStaffPo eventStaffPo = BeanConvertUtil.covertBean(reqJson, EventStaffPo.class);
                eventStaffPo.setEsId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));
                eventStaffPo.setStaffId(staffIdAndNameList.getObject(i, EventStaffDto.class).getStaffId());
                eventStaffPo.setStaffName(staffIdAndNameList.getObject(i, EventStaffDto.class).getStaffName());
                eventStaffPoList.add(eventStaffPo);
            }
        }

        int flag = eventStaffV1InnerServiceSMOImpl.saveEventStaffList(eventStaffPoList);

        if (flag != eventStaffPoList.size()) {
            throw new CmdException("保存数据失败");
        }

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
