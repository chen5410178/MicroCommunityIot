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
package com.java110.charge.smo.impl;


import com.java110.bean.ResultVo;
import com.java110.charge.factory.IChargeCore;
import com.java110.charge.factory.IChargeFactoryAdapt;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.Assert;
import com.java110.dto.chargeMachine.ChargeMachineByteDataDto;
import com.java110.dto.chargeMachine.ChargeMachineFactoryDto;
import com.java110.dto.chargeMachine.NotifyChargeOrderDto;
import com.java110.intf.charge.IChargeMachineFactoryV1InnerServiceSMO;
import com.java110.intf.charge.INotifyChargeV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2022-08-08 09:22:37 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class NotifyChargeV1InnerServiceSMOImpl implements INotifyChargeV1InnerServiceSMO {

    private static final Logger logger = LoggerFactory.getLogger(NotifyChargeV1InnerServiceSMOImpl.class);


    @Autowired
    private IChargeCore chargeCoreImpl;

    @Autowired
    private IChargeMachineFactoryV1InnerServiceSMO chargeMachineFactoryV1InnerServiceSMOImpl;


    @Override
    public ResultVo finishCharge(@RequestBody NotifyChargeOrderDto notifyChargeOrderDto) {

        ResultVo resultVo = null;
        try {
            resultVo = chargeCoreImpl.finishCharge(notifyChargeOrderDto);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("停止充电失败", e);
            throw e;
        }
        return resultVo;
    }

    @Override
    public ResultVo workHeartbeat(@RequestBody NotifyChargeOrderDto notifyChargeOrderDto) {
        return chargeCoreImpl.workHeartbeat(notifyChargeOrderDto);
    }

    @Override
    public ResultVo machineResult(@RequestBody ChargeMachineByteDataDto chargeMachineByteDataDto) {


        ChargeMachineFactoryDto chargeMachineFactoryDto = new ChargeMachineFactoryDto();
        chargeMachineFactoryDto.setFactoryId(chargeMachineByteDataDto.getChargeMachineDto().getImplBean());
        List<ChargeMachineFactoryDto> chargeMachineFactoryDtos = chargeMachineFactoryV1InnerServiceSMOImpl.queryChargeMachineFactorys(chargeMachineFactoryDto);

        Assert.listOnlyOne(chargeMachineFactoryDtos, "充电桩厂家不存在");

        IChargeFactoryAdapt chargeFactoryAdapt = ApplicationContextFactory.getBean(chargeMachineFactoryDtos.get(0).getBeanImpl(), IChargeFactoryAdapt.class);
        if (chargeFactoryAdapt == null) {
            throw new CmdException("厂家接口未实现");
        }

        chargeFactoryAdapt.chargeResult(chargeMachineByteDataDto.getChargeMachineDto(), chargeMachineByteDataDto.getData());

        return new ResultVo(ResultVo.CODE_OK, "成功");
    }


}
