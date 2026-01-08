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
package com.java110.car.cmd.car;

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
import com.java110.core.utils.ListUtil;
import com.java110.dto.fee.TempCarFeeConfigDto;
import com.java110.intf.barrier.ITempCarFeeConfigV1InnerServiceSMO;
import com.java110.intf.car.ITempInnerCarV1InnerServiceSMO;
import com.java110.po.car.TempInnerCarPo;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 类表述：保存
 * 服务编码：tempInnerCar.saveTempInnerCar
 * 请求路劲：/app/tempInnerCar.SaveTempInnerCar
 * add by 吴学文 at 2024-01-01 12:00:00 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "car.saveTempInnerCar")
public class SaveTempInnerCarCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(SaveTempInnerCarCmd.class);

    public static final String CODE_PREFIX_ID = "10";

    @Autowired
    private ITempInnerCarV1InnerServiceSMO tempInnerCarV1InnerServiceSMOImpl;

    @Autowired
    private ITempCarFeeConfigV1InnerServiceSMO tempCarFeeConfigV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {

        Assert.hasKeyAndValue(reqJson, "carNum", "必填，请填写车牌号");
        Assert.hasKeyAndValue(reqJson, "communityId", "必填，请填写小区ID");
        Assert.hasKeyAndValue(reqJson, "startTime", "必填，请选择开始时间");
        Assert.hasKeyAndValue(reqJson, "endTime", "必填，请选择结束时间");
        Assert.hasKeyAndValue(reqJson, "configId", "必填，请填写收费标准ID");

    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        TempInnerCarPo tempInnerCarPo = BeanConvertUtil.covertBean(reqJson, TempInnerCarPo.class);
        tempInnerCarPo.setTicId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));

        TempCarFeeConfigDto tempCarFeeConfigDto = new TempCarFeeConfigDto();
        tempCarFeeConfigDto.setConfigId(reqJson.getString("configId"));
        tempCarFeeConfigDto.setCommunityId(reqJson.getString("communityId"));
        List<TempCarFeeConfigDto> tempCarFeeConfigDtos = tempCarFeeConfigV1InnerServiceSMOImpl.queryTempCarFeeConfigs(tempCarFeeConfigDto);
        if (ListUtil.isNull(tempCarFeeConfigDtos)) {
            throw new CmdException("临时停车费不存在");
        }
        tempCarFeeConfigDto.setFeeName(tempCarFeeConfigDtos.get(0).getFeeName());
        tempCarFeeConfigDto.setPaId(tempCarFeeConfigDtos.get(0).getPaId());
        tempCarFeeConfigDto.setPaId(tempCarFeeConfigDtos.get(0).getAreaNum());
        int flag = tempInnerCarV1InnerServiceSMOImpl.saveTempInnerCar(tempInnerCarPo);

        if (flag < 1) {
            throw new CmdException("保存数据失败");
        }

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}

