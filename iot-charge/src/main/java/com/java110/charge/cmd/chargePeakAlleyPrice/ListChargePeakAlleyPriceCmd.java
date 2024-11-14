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

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.chargePeakAlleyPrice.ChargePeakAlleyPriceDto;
import com.java110.intf.charge.IChargePeakAlleyPriceV1InnerServiceSMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * 类表述：查询
 * 服务编码：chargePeakAlleyPrice.listChargePeakAlleyPrice
 * 请求路劲：/app/chargePeakAlleyPrice.ListChargePeakAlleyPrice
 * add by 吴学文 at 2024-11-04 17:41:23 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "chargePeakAlleyPrice.listChargePeakAlleyPrice")
public class ListChargePeakAlleyPriceCmd extends Cmd {

  private static Logger logger = LoggerFactory.getLogger(ListChargePeakAlleyPriceCmd.class);
    @Autowired
    private IChargePeakAlleyPriceV1InnerServiceSMO chargePeakAlleyPriceV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        super.validatePageInfo(reqJson);
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

           ChargePeakAlleyPriceDto chargePeakAlleyPriceDto = BeanConvertUtil.covertBean(reqJson, ChargePeakAlleyPriceDto.class);

           int count = chargePeakAlleyPriceV1InnerServiceSMOImpl.queryChargePeakAlleyPricesCount(chargePeakAlleyPriceDto);

           List<ChargePeakAlleyPriceDto> chargePeakAlleyPriceDtos = null;

           if (count > 0) {
               chargePeakAlleyPriceDtos = chargePeakAlleyPriceV1InnerServiceSMOImpl.queryChargePeakAlleyPrices(chargePeakAlleyPriceDto);
           } else {
               chargePeakAlleyPriceDtos = new ArrayList<>();
               chargePeakAlleyPriceDto = new ChargePeakAlleyPriceDto();
               chargePeakAlleyPriceDto.setType("00");
               chargePeakAlleyPriceDto.setTypeName("尖费费率");
               chargePeakAlleyPriceDto.setRuleId("");
               chargePeakAlleyPriceDto.setPrice("");
               chargePeakAlleyPriceDto.setServicePrice("");
               chargePeakAlleyPriceDto.setRemark("");
               chargePeakAlleyPriceDtos.add(chargePeakAlleyPriceDto);

               chargePeakAlleyPriceDto = new ChargePeakAlleyPriceDto();
               chargePeakAlleyPriceDto.setType("01");
               chargePeakAlleyPriceDto.setTypeName("峰电费率");
               chargePeakAlleyPriceDto.setRuleId("");
               chargePeakAlleyPriceDto.setPrice("");
               chargePeakAlleyPriceDto.setServicePrice("");
               chargePeakAlleyPriceDto.setRemark("");
               chargePeakAlleyPriceDtos.add(chargePeakAlleyPriceDto);

               chargePeakAlleyPriceDto = new ChargePeakAlleyPriceDto();
               chargePeakAlleyPriceDto.setType("02");
               chargePeakAlleyPriceDto.setTypeName("平电费率");
               chargePeakAlleyPriceDto.setRuleId("");
               chargePeakAlleyPriceDto.setPrice("");
               chargePeakAlleyPriceDto.setServicePrice("");
               chargePeakAlleyPriceDto.setRemark("");
               chargePeakAlleyPriceDtos.add(chargePeakAlleyPriceDto);

               chargePeakAlleyPriceDto = new ChargePeakAlleyPriceDto();
               chargePeakAlleyPriceDto.setType("03");
               chargePeakAlleyPriceDto.setTypeName("谷电费率");
               chargePeakAlleyPriceDto.setRuleId("");
               chargePeakAlleyPriceDto.setPrice("");
               chargePeakAlleyPriceDto.setServicePrice("");
               chargePeakAlleyPriceDto.setRemark("");
               chargePeakAlleyPriceDtos.add(chargePeakAlleyPriceDto);
           }

           ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, chargePeakAlleyPriceDtos);

           ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

           cmdDataFlowContext.setResponseEntity(responseEntity);
    }
}
