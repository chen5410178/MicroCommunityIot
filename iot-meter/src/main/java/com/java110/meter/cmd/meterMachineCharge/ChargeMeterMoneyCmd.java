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
package com.java110.meter.cmd.meterMachineCharge;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.meter.MeterMachineDto;
import com.java110.dto.meter.MeterMachineFactoryDto;
import com.java110.dto.meter.MeterTypeDto;
import com.java110.dto.meterMachineCharge.MeterMachineChargeDto;
import com.java110.intf.meter.IMeterMachineChargeV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineFactoryV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineV1InnerServiceSMO;
import com.java110.intf.meter.IMeterTypeV1InnerServiceSMO;
import com.java110.meter.factory.ISmartMeterFactoryAdapt;
import com.java110.po.meterMachineCharge.MeterMachineChargePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

/**
 * 类表述：保存
 * 服务编码：meterMachineCharge.saveMeterMachineCharge
 * 请求路劲：/app/meterMachineCharge.SaveMeterMachineCharge
 * add by 吴学文 at 2023-11-21 23:24:34 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "meterMachineCharge.chargeMeterMoney")
public class ChargeMeterMoneyCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ChargeMeterMoneyCmd.class);

    public static final String CODE_PREFIX_ID = "10";



    @Autowired
    private IMeterMachineV1InnerServiceSMO meterMachineV1InnerServiceSMOImpl;

    @Autowired
    private IMeterMachineFactoryV1InnerServiceSMO meterMachineFactoryV1InnerServiceSMOImpl;

    @Autowired
    private IMeterTypeV1InnerServiceSMO meterTypeV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "machineId", "请求报文中未包含machineId");
        Assert.hasKeyAndValue(reqJson, "chargeMoney", "请求报文中未包含chargeMoney");
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId");

        double chargeMoney = reqJson.getDouble("chargeMoney");
        if (chargeMoney <= 0) {
            throw new CmdException("充值金额不能为0");
        }


    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        MeterMachineDto meterMachineDto = new MeterMachineDto();
        meterMachineDto.setCommunityId(reqJson.getString("communityId"));
        meterMachineDto.setMachineId(reqJson.getString("machineId"));
        List<MeterMachineDto> machineDtos = meterMachineV1InnerServiceSMOImpl.queryMeterMachines(meterMachineDto);

        if (ListUtil.isNull(machineDtos)) {
            throw new CmdException("表不存在");
        }

        if (!MeterMachineDto.MACHINE_MODEL_RECHARGE.equals(machineDtos.get(0).getMachineModel())) {
            throw new CmdException("不是充值表");
        }


        MeterMachineFactoryDto meterMachineFactoryDto = new MeterMachineFactoryDto();
        meterMachineFactoryDto.setFactoryId(machineDtos.get(0).getImplBean());
        List<MeterMachineFactoryDto> meterMachineFactoryDtos = meterMachineFactoryV1InnerServiceSMOImpl.queryMeterMachineFactorys(meterMachineFactoryDto);
        Assert.listOnlyOne(meterMachineFactoryDtos, "智能水电表厂家不存在");

        ISmartMeterFactoryAdapt smartMeterFactoryAdapt = null;
        try {
            smartMeterFactoryAdapt = ApplicationContextFactory.getBean(meterMachineFactoryDtos.get(0).getBeanImpl(), ISmartMeterFactoryAdapt.class);
        } finally {
            if (smartMeterFactoryAdapt == null) {
                throw new CmdException("厂家接口未实现");
            }
        }

        double chargeMoney = reqJson.getDouble("chargeMoney");

        MeterTypeDto meterTypeDto = new MeterTypeDto();
        meterTypeDto.setTypeId(machineDtos.get(0).getMeterType());
        meterTypeDto.setCommunityId(reqJson.getString("communityId"));
        List<MeterTypeDto> meterTypeDtos = meterTypeV1InnerServiceSMOImpl.queryMeterTypes(meterTypeDto);

        if (ListUtil.isNull(meterTypeDtos)) {
            throw new CmdException("类型不存在");
        }

        String priceStr = meterTypeDtos.get(0).getPrice();

//        if (!StringUtil.isNumber(priceStr)) {
//            throw new CmdException("单价不是数字");
//        }

        double price = Double.parseDouble(priceStr);

        if (price <= 0) {
            throw new CmdException("单价小于等于0");
        }

        BigDecimal chargeMoneyDec = new BigDecimal(chargeMoney);
        BigDecimal degree = chargeMoneyDec.divide(new BigDecimal(price), 2, BigDecimal.ROUND_HALF_UP);


        ResultVo resultVo = smartMeterFactoryAdapt.requestRecharge(machineDtos.get(0), degree.doubleValue(), chargeMoney);

        if (ResultVo.CODE_OK != resultVo.getCode()) {
            throw new CmdException(resultVo.getMsg());
        }



        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
