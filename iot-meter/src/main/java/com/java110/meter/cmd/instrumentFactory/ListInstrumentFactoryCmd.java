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
package com.java110.meter.cmd.instrumentFactory;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.instrument.InstrumentFactoryDto;
import com.java110.dto.instrument.InstrumentFactorySpecDto;
import com.java110.intf.meter.IInstrumentFactorySpecV1InnerServiceSMO;
import com.java110.intf.meter.IInstrumentFactoryV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.ArrayList;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;


/**
 * 类表述：查询
 * 服务编码：instrumentFactory.listInstrumentFactory
 * 请求路劲：/app/instrumentFactory.ListInstrumentFactory
 * add by 吴学文 at 2023-11-22 11:22:24 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "instrumentFactory.listInstrumentFactory")
public class ListInstrumentFactoryCmd extends Cmd {

  private static Logger logger = LoggerFactory.getLogger(ListInstrumentFactoryCmd.class);
    @Autowired
    private IInstrumentFactoryV1InnerServiceSMO instrumentFactoryV1InnerServiceSMOImpl;

    @Autowired
    private IInstrumentFactorySpecV1InnerServiceSMO instrumentFactorySpecV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        super.validatePageInfo(reqJson);
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

           InstrumentFactoryDto instrumentFactoryDto = BeanConvertUtil.covertBean(reqJson, InstrumentFactoryDto.class);

           int count = instrumentFactoryV1InnerServiceSMOImpl.queryInstrumentFactorysCount(instrumentFactoryDto);

           List<InstrumentFactoryDto> instrumentFactoryDtos = null;

           if (count > 0) {
               instrumentFactoryDtos = instrumentFactoryV1InnerServiceSMOImpl.queryInstrumentFactoryList(instrumentFactoryDto);
               refreshSpec(instrumentFactoryDtos);
           } else {
               instrumentFactoryDtos = new ArrayList<>();
           }

           ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, instrumentFactoryDtos);

           ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

           cmdDataFlowContext.setResponseEntity(responseEntity);
    }

    private void refreshSpec(List<InstrumentFactoryDto> instrumentFactoryDtos) {
        if (CollectionUtils.isEmpty(instrumentFactoryDtos)) {
            return;
        }
        for (InstrumentFactoryDto instrumentFactoryDto : instrumentFactoryDtos) {
            InstrumentFactorySpecDto instrumentFactorySpecDto = new InstrumentFactorySpecDto();
            instrumentFactorySpecDto.setFactoryId(instrumentFactoryDto.getFactoryId());
            List<InstrumentFactorySpecDto> instrumentFactorySpecDtos = instrumentFactorySpecV1InnerServiceSMOImpl.queryInstrumentFactorySpecs(instrumentFactorySpecDto);
            instrumentFactoryDto.setInstrumentFactorySpecList(instrumentFactorySpecDtos);
        }
    }
}
