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
package com.java110.charge.cmd.chargePeakAlleyPrice;

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
import com.java110.intf.charge.IChargePeakAlleyPriceV1InnerServiceSMO;
import com.java110.po.chargePeakAlleyPrice.ChargePeakAlleyPricePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 类表述：保存
 * 服务编码：chargePeakAlleyPrice.saveChargePeakAlleyPrice
 * 请求路劲：/app/chargePeakAlleyPrice.SaveChargePeakAlleyPrice
 * add by 吴学文 at 2024-11-04 17:41:23 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "chargePeakAlleyPrice.saveChargePeakAlleyPrice")
public class SaveChargePeakAlleyPriceCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(SaveChargePeakAlleyPriceCmd.class);

    public static final String CODE_PREFIX_ID = "23";

    @Autowired
    private IChargePeakAlleyPriceV1InnerServiceSMO chargePeakAlleyPriceV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "ruleId", "请求报文中未包含ruleId");
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId");
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        JSONArray prices = reqJson.getJSONArray("prices");
        String ruleId = reqJson.getString("ruleId");
        ChargePeakAlleyPricePo chargePeakAlleyPricePo = new ChargePeakAlleyPricePo();
        chargePeakAlleyPricePo.setRuleId(ruleId);
        int flag = chargePeakAlleyPriceV1InnerServiceSMOImpl.deleteChargePeakAlleyPrice(chargePeakAlleyPricePo);
        for (int index = 0; index < prices.size(); index++) {
            JSONObject priceObj = prices.getJSONObject(index);
            chargePeakAlleyPricePo = new ChargePeakAlleyPricePo() ;
            chargePeakAlleyPricePo.setCpaId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));
            chargePeakAlleyPricePo.setType(priceObj.getString("type"));
            chargePeakAlleyPricePo.setRuleId(ruleId);
            chargePeakAlleyPricePo.setCommunityId(reqJson.getString("communityId"));
            chargePeakAlleyPricePo.setPrice(priceObj.getString("price"));
            chargePeakAlleyPricePo.setServicePrice(priceObj.getString("servicePrice"));
            chargePeakAlleyPricePo.setRemark(priceObj.getString("remark"));
            flag = chargePeakAlleyPriceV1InnerServiceSMOImpl.saveChargePeakAlleyPrice(chargePeakAlleyPricePo);
        }
        if (flag < 1) {
            throw new CmdException("保存数据失败");
        }

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
