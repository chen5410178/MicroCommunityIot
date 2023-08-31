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
package com.java110.meter.cmd.meterMachine;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.doc.annotation.*;
import com.java110.dto.meter.MeterMachineFactoryDto;
import com.java110.dto.meter.MeterMachineFactorySpecDto;
import com.java110.intf.meter.IMeterMachineFactorySpecV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineFactoryV1InnerServiceSMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * 类表述：查询
 * 服务编码：meterMachineFactory.listMeterMachineFactory
 * 请求路劲：/app/meterMachineFactory.ListMeterMachineFactory
 * add by 吴学文 at 2023-02-22 22:23:02 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */

@Java110CmdDoc(title = "查询厂家设备",
        description = "用于外系统查询厂家设备",
        httpMethod = "get",
        url = "http://{ip}:{port}/iot/api/meterMachine.listMeterMachineFactory",
        resource = "meterDoc",
        author = "吴学文",
        serviceCode = "meterMachine.listMeterMachineFactory",
        seq = 12
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "page", type = "int", length = 11, remark = "页数"),
        @Java110ParamDoc(name = "row", type = "int", length = 11, remark = "行数"),
})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
                @Java110ParamDoc(name = "data", type = "Array", remark = "有效数据"),
                @Java110ParamDoc(parentNodeName = "data", name = "factoryId", type = "String", remark = "厂家ID"),
                @Java110ParamDoc(parentNodeName = "data", name = "factoryName", type = "String", remark = "厂家名称"),
                @Java110ParamDoc(parentNodeName = "data", name = "beanImpl", type = "String", remark = "厂家处理类"),
        }
)

@Java110ExampleDoc(
        reqBody = "http://{ip}:{port}/iot/api/meterMachine.listMeterMachineFactory?page=1&row=10",
        resBody = "{'code':0,'msg':'成功','data':[{'factoryId':'123123','factoryName':'xx电表-xx电表','beanImpl':'tqDianBiaoDanxiangDanFeiLvFactoryAdaptImpl'}]}"
)
@Java110Cmd(serviceCode = "meterMachine.listMeterMachineFactory")
public class ListMeterMachineFactoryCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ListMeterMachineFactoryCmd.class);
    @Autowired
    private IMeterMachineFactoryV1InnerServiceSMO meterMachineFactoryV1InnerServiceSMOImpl;

    @Autowired
    private IMeterMachineFactorySpecV1InnerServiceSMO machineFactorySpecV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        super.validatePageInfo(reqJson);
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        MeterMachineFactoryDto meterMachineFactoryDto = BeanConvertUtil.covertBean(reqJson, MeterMachineFactoryDto.class);

        int count = meterMachineFactoryV1InnerServiceSMOImpl.queryMeterMachineFactorysCount(meterMachineFactoryDto);

        List<MeterMachineFactoryDto> meterMachineFactoryDtos = null;

        if (count > 0) {
            meterMachineFactoryDtos = meterMachineFactoryV1InnerServiceSMOImpl.queryMeterMachineFactorys(meterMachineFactoryDto);
            freshSpecs(meterMachineFactoryDtos);
        } else {
            meterMachineFactoryDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, meterMachineFactoryDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        cmdDataFlowContext.setResponseEntity(responseEntity);
    }

    /**
     * 刷入配置
     *
     * @param meterMachineFactoryDtos
     */
    private void freshSpecs(List<MeterMachineFactoryDto> meterMachineFactoryDtos) {

        if (meterMachineFactoryDtos == null || meterMachineFactoryDtos.size() < 1) {
            return;
        }

        List<String> factoryIds = new ArrayList<>();
        for (MeterMachineFactoryDto meterMachineFactoryDto : meterMachineFactoryDtos) {
            factoryIds.add(meterMachineFactoryDto.getFactoryId());
        }

        MeterMachineFactorySpecDto machineFactorySpecDto = new MeterMachineFactorySpecDto();
        machineFactorySpecDto.setFactoryIds(factoryIds.toArray(new String[factoryIds.size()]));

        List<MeterMachineFactorySpecDto> machineFactorySpecDtos = machineFactorySpecV1InnerServiceSMOImpl.queryMeterMachineFactorySpecs(machineFactorySpecDto);

        if (machineFactorySpecDtos == null || machineFactorySpecDtos.size() < 1) {
            return;
        }
        List<MeterMachineFactorySpecDto> specs = null;
        for (MeterMachineFactoryDto meterMachineFactoryDto : meterMachineFactoryDtos) {
            specs = new ArrayList<>();
            for (MeterMachineFactorySpecDto tmpMeterMachineFactorySpecDto : machineFactorySpecDtos) {
                if (meterMachineFactoryDto.getFactoryId().equals(tmpMeterMachineFactorySpecDto.getFactoryId())) {
                    specs.add(tmpMeterMachineFactorySpecDto);
                }
            }
            meterMachineFactoryDto.setSpecs(specs);
        }
    }
}
