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
package com.java110.meter.cmd.instrument;

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
import com.java110.core.utils.StringUtil;
import com.java110.dto.instrument.InstrumentFactorySpecDto;
import com.java110.dto.instrument.InstrumentSensorDto;
import com.java110.intf.meter.IInstrumentFactorySpecV1InnerServiceSMO;
import com.java110.intf.meter.IInstrumentParamV1InnerServiceSMO;
import com.java110.intf.meter.IInstrumentSensorV1InnerServiceSMO;
import com.java110.intf.meter.IInstrumentV1InnerServiceSMO;
import com.java110.po.instrument.InstrumentParamPo;
import com.java110.po.instrument.InstrumentPo;
import com.java110.po.instrument.InstrumentSensorPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 类表述：更新
 * 服务编码：instrument.updateInstrument
 * 请求路劲：/app/instrument.UpdateInstrument
 * add by 吴学文 at 2023-11-23 10:13:04 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "instrument.updateInstrument")
public class UpdateInstrumentCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(UpdateInstrumentCmd.class);


    @Autowired
    private IInstrumentV1InnerServiceSMO instrumentV1InnerServiceSMOImpl;

    @Autowired
    private IInstrumentFactorySpecV1InnerServiceSMO instrumentFactorySpecV1InnerServiceSMOImpl;

    @Autowired
    private IInstrumentParamV1InnerServiceSMO instrumentParamV1InnerServiceSMOImpl;

    @Autowired
    private IInstrumentSensorV1InnerServiceSMO instrumentSensorV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "machineId", "machineId不能为空");
        Assert.hasKeyAndValue(reqJson, "machineCode", "machineCode不能为空");
        Assert.hasKeyAndValue(reqJson, "machineName", "machineName不能为空");
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
        Assert.hasKeyAndValue(reqJson, "typeId", "typeId不能为空");
        Assert.hasKeyAndValue(reqJson, "upMin", "upMin不能为空");
        Assert.hasKeyAndValue(reqJson, "checkSec", "checkSec不能为空");
        Assert.hasKeyAndValue(reqJson, "implBean", "implBean不能为空");
        Assert.hasKeyAndValue(reqJson, "specs", "specs不能为空");
        JSONArray specs = reqJson.getJSONArray("specs");

        InstrumentFactorySpecDto instrumentFactorySpecDto = new InstrumentFactorySpecDto();
        instrumentFactorySpecDto.setFactoryId(reqJson.getString("implBean"));
        int row = instrumentFactorySpecV1InnerServiceSMOImpl.queryInstrumentFactorySpecsCount(instrumentFactorySpecDto);
        if (row < 1) {
            throw new CmdException("厂家规格未添加");
        }

        for (int i = 0; i < specs.size(); i++) {
            instrumentFactorySpecDto = BeanConvertUtil.covertBean(specs.getJSONObject(i), InstrumentFactorySpecDto.class);
            if (StringUtil.isEmpty(instrumentFactorySpecDto.getValue())) {
                throw new CmdException("请将厂家规格值填写完整");
            }
        }

        JSONArray sensorList = reqJson.getJSONArray("sensorList");
        if (!CollectionUtils.isEmpty(sensorList)) {
            for (int i = 0; i < sensorList.size(); i++) {
                InstrumentSensorDto instrumentSensorDto = BeanConvertUtil.covertBean(sensorList.get(i), InstrumentSensorDto.class);
                Assert.hasKeyAndValue(instrumentSensorDto, "sensorType", "sensorType不能为空");
                if ("1001".equals(instrumentSensorDto.getParamType())) {
                    Assert.hasKeyAndValue(instrumentSensorDto, "minValue", "minValue不能为空");
                    Assert.hasKeyAndValue(instrumentSensorDto, "maxValue", "maxValue不能为空");
                    Assert.hasKeyAndValue(instrumentSensorDto, "units", "units不能为空");
                }
            }
        }
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        InstrumentPo instrumentPo = BeanConvertUtil.covertBean(reqJson, InstrumentPo.class);
        int flag = instrumentV1InnerServiceSMOImpl.updateInstrument(instrumentPo);

        if (flag < 1) {
            throw new CmdException("更新数据失败");
        }

        InstrumentParamPo instrumentParam = new InstrumentParamPo();
        instrumentParam.setMachineId(instrumentPo.getMachineId());
        instrumentParam.setStatusCd(instrumentPo.getCommunityId());
        instrumentParamV1InnerServiceSMOImpl.deleteInstrumentParam(instrumentParam);

        JSONArray specs = reqJson.getJSONArray("specs");
        List<InstrumentParamPo> instrumentParamPos = new ArrayList<>();
        specs.forEach(spec -> {
            InstrumentParamPo instrumentParamPo = BeanConvertUtil.covertBean(spec, InstrumentParamPo.class);
            instrumentParamPo.setParamId(GenerateCodeFactory.getGeneratorId("10"));
            instrumentParamPo.setMachineId(instrumentPo.getMachineId());
            instrumentParamPo.setCommunityId(reqJson.getString("communityId"));
            instrumentParamPos.add(instrumentParamPo);
        });

        flag = instrumentParamV1InnerServiceSMOImpl.saveInstrumentParamList(instrumentParamPos);
        if (flag != instrumentParamPos.size()) {
            throw new CmdException("更新数据失败");
        }

        InstrumentSensorPo instrumentSensor = new InstrumentSensorPo();
        instrumentSensor.setMachineId(instrumentPo.getMachineId());
        instrumentSensor.setCommunityId(instrumentPo.getCommunityId());
        instrumentSensorV1InnerServiceSMOImpl.deleteInstrumentSensor(instrumentSensor);

        JSONArray sensorList = reqJson.getJSONArray("sensorList");
        if (!CollectionUtils.isEmpty(sensorList)) {
            List<InstrumentSensorPo> instrumentSensorPos = new ArrayList<>();
            for (int i = 0; i < sensorList.size(); i++) {
                InstrumentSensorPo instrumentSensorPo = BeanConvertUtil.covertBean(sensorList.get(i), InstrumentSensorPo.class);
                instrumentSensorPo.setSensorId(GenerateCodeFactory.getGeneratorId("10"));
                instrumentSensorPo.setMachineId(instrumentPo.getMachineId());
                instrumentSensorPo.setCommunityId(reqJson.getString("communityId"));
                instrumentSensorPos.add(instrumentSensorPo);
            }
            flag = instrumentSensorV1InnerServiceSMOImpl.saveInstrumentSensorList(instrumentSensorPos);
            if (flag != instrumentSensorPos.size()) {
                throw new CmdException("更新数据失败");
            }
        }

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
