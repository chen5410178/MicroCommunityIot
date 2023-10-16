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
package com.java110.system.cmd.eventPool;

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
import com.java110.dto.event.EventPoolDto;
import com.java110.dto.event.EventPoolStaffDto;
import com.java110.dto.event.EventRuleDto;
import com.java110.intf.system.IEventPoolStaffV1InnerServiceSMO;
import com.java110.intf.system.IEventPoolV1InnerServiceSMO;
import com.java110.intf.system.IEventRuleV1InnerServiceSMO;
import com.java110.po.event.EventPoolPo;
import com.java110.po.event.EventPoolStaffPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * 类表述：更新
 * 服务编码：eventPool.updateEventPool
 * 请求路劲：/app/eventPool.UpdateEventPool
 * add by 吴学文 at 2023-09-27 17:31:24 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "eventPool.updateEventPool")
public class UpdateEventPoolCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(UpdateEventPoolCmd.class);


    @Autowired
    private IEventPoolV1InnerServiceSMO eventPoolV1InnerServiceSMOImpl;

    @Autowired
    private IEventRuleV1InnerServiceSMO eventRuleV1InnerServiceSMOImpl;

    @Autowired
    private IEventPoolStaffV1InnerServiceSMO eventPoolStaffV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "eventId", "eventId不能为空");
        Assert.hasKeyAndValue(reqJson, "ruleId", "ruleId不能为空");
        if (EventPoolDto.EVENT_STATE_DOING.equals(reqJson.getString("state"))) {
            EventRuleDto eventRuleDto = new EventRuleDto();
            eventRuleDto.setRuleId(reqJson.getString("ruleId"));
            List<EventRuleDto> eventRuleDtoList = eventRuleV1InnerServiceSMOImpl.queryEventRules(eventRuleDto);
            reqJson.put("limitTime", eventRuleDtoList.get(0).getLimitTime());

            BigDecimal dealTime = new BigDecimal(new Date().getTime() - reqJson.getDate("createTime").getTime()).divide(new BigDecimal(1000 * 60), BigDecimal.ROUND_HALF_UP);

            if (eventRuleDtoList.get(0).getLimitTime() < dealTime.intValue()) {
                reqJson.put("state", "TC");
            } else {
                reqJson.put("state", "NC");
            }
        }
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        EventPoolPo eventPoolPo = BeanConvertUtil.covertBean(reqJson, EventPoolPo.class);
        int flag = eventPoolV1InnerServiceSMOImpl.updateEventPool(eventPoolPo);

        if (flag < 1) {
            throw new CmdException("更新数据失败");
        }

        if (EventPoolDto.EVENT_STATE_NORMAL_COMPLETE.equals(eventPoolPo.getState()) || EventPoolDto.EVENT_STATE_TIMEOUT_COMPLETE.equals(eventPoolPo.getState())) {
            EventPoolStaffDto eventPoolStaffDto = new EventPoolStaffDto();
            eventPoolStaffDto.setEventId(eventPoolPo.getEventId());
            eventPoolStaffDto.setState(EventPoolDto.EVENT_STATE_DOING);
            List<EventPoolStaffDto> eventPoolStaffDtoList = eventPoolStaffV1InnerServiceSMOImpl.queryEventPoolStaffs(eventPoolStaffDto);
            EventPoolStaffPo eventPoolStaffPo = BeanConvertUtil.covertBean(eventPoolStaffDtoList.get(0), EventPoolStaffPo.class);

            int limitTime = reqJson.getInteger("limitTime");
            BigDecimal dealTime = new BigDecimal(new Date().getTime() - eventPoolStaffDtoList.get(0).getCreateTime().getTime()).divide(new BigDecimal(1000 * 60), BigDecimal.ROUND_HALF_UP);

            if (limitTime < dealTime.intValue()) {
                eventPoolStaffPo.setState(EventPoolDto.EVENT_STATE_TIMEOUT_COMPLETE);
            } else {
                eventPoolStaffPo.setState(EventPoolDto.EVENT_STATE_NORMAL_COMPLETE);
            }
            eventPoolStaffPo.setEventHours(dealTime.intValue());

            flag = eventPoolStaffV1InnerServiceSMOImpl.updateEventPoolStaff(eventPoolStaffPo);
            if (flag < 1) {
                throw new CmdException("更新数据失败");
            }
        }

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
