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
package com.java110.charge.cmd.chargeCard;

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
import com.java110.doc.annotation.*;
import com.java110.intf.charge.IChargeMonthCardV1InnerServiceSMO;
import com.java110.po.chargeMonthCard.ChargeMonthCardPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 类表述：更新
 * 服务编码：chargeMonthCard.updateChargeMonthCard
 * 请求路劲：/app/chargeMonthCard.UpdateChargeMonthCard
 * add by 吴学文 at 2023-04-24 10:09:58 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */

@Java110CmdDoc(title = "修改充电月卡",
        description = "用于外系统修改充电月卡",
        httpMethod = "post",
        url = "http://{ip}:{port}/iot/api/chargeCard.updateChargeMonthCard",
        resource = "chargeDoc",
        author = "吴学文",
        serviceCode = "chargeCard.updateChargeMonthCard",
        seq = 21
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "communityId", length = 30, remark = "小区ID"),
        @Java110ParamDoc(name = "cardId", length = 30, remark = "月卡ID"),
        @Java110ParamDoc(name = "cardName", length = 30, remark = "月卡名称"),
        @Java110ParamDoc(name = "cardMonth", type = "int", length = 11, remark = "月卡有限时间(月)"),
        @Java110ParamDoc(name = "cardPrice", length = 30, remark = "月卡价格"),
        @Java110ParamDoc(name = "dayHours", length = 30, remark = "每日充电时间"),
})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
        }
)

@Java110ExampleDoc(
        reqBody = "{\"communityId\":\"2022081539020475\",\"cardId\":\"123123\",\"cardMonth\":\"1\",\"cardPrice\":\"30\",\"cardName\":\"月卡套餐1\",\"dayHours\":\"8\"}",
        resBody = "{'code':0,'msg':'成功'}"
)
@Java110Cmd(serviceCode = "chargeCard.updateChargeMonthCard")
public class UpdateChargeMonthCardCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(UpdateChargeMonthCardCmd.class);


    @Autowired
    private IChargeMonthCardV1InnerServiceSMO chargeMonthCardV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "cardId", "cardId不能为空");
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");

    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        ChargeMonthCardPo chargeMonthCardPo = BeanConvertUtil.covertBean(reqJson, ChargeMonthCardPo.class);
        int flag = chargeMonthCardV1InnerServiceSMOImpl.updateChargeMonthCard(chargeMonthCardPo);

        if (flag < 1) {
            throw new CmdException("更新数据失败");
        }

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
