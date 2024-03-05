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
package com.java110.car.smo.impl;


import com.java110.bean.dto.PageDto;
import com.java110.car.dao.IParkingSpaceMachineV1ServiceDao;
import com.java110.car.manufactor.IParkingSpaceMachineManufactor;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.chargeMachine.ChargeMachineFactoryDto;
import com.java110.dto.parkingSpaceMachine.ParkingSpaceMachineDto;
import com.java110.dto.parkingSpaceMachine.ParkingSpaceMachineNotifyDto;
import com.java110.intf.car.IParkingSpaceMachineNotifyV1InnerServiceSMO;
import com.java110.intf.car.IParkingSpaceMachineV1InnerServiceSMO;
import com.java110.po.parkingSpaceMachine.ParkingSpaceMachinePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2024-03-05 20:57:20 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class ParkingSpaceMachineNotifyV1InnerServiceSMOImpl implements IParkingSpaceMachineNotifyV1InnerServiceSMO {
    private static final Map<String, ParkingSpaceMachineDto> factoryCache = new HashMap<>();

    @Autowired
    private IParkingSpaceMachineV1InnerServiceSMO parkingSpaceMachineV1InnerServiceSMOImpl;

    @Override
    public String heartbeat(ParkingSpaceMachineNotifyDto parkingSpaceMachineNotifyDto) {

        ParkingSpaceMachineDto parkingSpaceMachineDto = null;
        if (!factoryCache.containsKey(parkingSpaceMachineNotifyDto.getMachineCode())) {
            parkingSpaceMachineDto = new ParkingSpaceMachineDto();
            parkingSpaceMachineDto.setMachineCode(parkingSpaceMachineNotifyDto.getMachineCode());
            List<ParkingSpaceMachineDto> parkingSpaceMachineDtos = parkingSpaceMachineV1InnerServiceSMOImpl.queryParkingSpaceMachines(parkingSpaceMachineDto);

            Assert.listOnlyOne(parkingSpaceMachineDtos, "地磁设备厂家不存在");

            factoryCache.put(parkingSpaceMachineNotifyDto.getMachineCode(), parkingSpaceMachineDtos.get(0));
        }

        parkingSpaceMachineDto = factoryCache.get(parkingSpaceMachineNotifyDto.getMachineCode());

        IParkingSpaceMachineManufactor parkingSpaceMachineManufactor = ApplicationContextFactory.getBean(parkingSpaceMachineDto.getBeanImpl(), IParkingSpaceMachineManufactor.class);
        if (parkingSpaceMachineManufactor == null) {
            throw new CmdException("厂家接口未实现");
        }

        return parkingSpaceMachineManufactor.heartbeat(parkingSpaceMachineDto, parkingSpaceMachineNotifyDto.getReqBody());
    }

    @Override
    public String plateResult(ParkingSpaceMachineNotifyDto parkingSpaceMachineNotifyDto) {
        ParkingSpaceMachineDto parkingSpaceMachineDto = null;
        if (!factoryCache.containsKey(parkingSpaceMachineNotifyDto.getMachineCode())) {
            parkingSpaceMachineDto = new ParkingSpaceMachineDto();
            parkingSpaceMachineDto.setMachineCode(parkingSpaceMachineNotifyDto.getMachineCode());
            List<ParkingSpaceMachineDto> parkingSpaceMachineDtos = parkingSpaceMachineV1InnerServiceSMOImpl.queryParkingSpaceMachines(parkingSpaceMachineDto);

            Assert.listOnlyOne(parkingSpaceMachineDtos, "地磁设备厂家不存在");

            factoryCache.put(parkingSpaceMachineNotifyDto.getMachineCode(), parkingSpaceMachineDtos.get(0));
        }

        parkingSpaceMachineDto = factoryCache.get(parkingSpaceMachineNotifyDto.getMachineCode());

        IParkingSpaceMachineManufactor parkingSpaceMachineManufactor = ApplicationContextFactory.getBean(parkingSpaceMachineDto.getBeanImpl(), IParkingSpaceMachineManufactor.class);
        if (parkingSpaceMachineManufactor == null) {
            throw new CmdException("厂家接口未实现");
        }

        return parkingSpaceMachineManufactor.plateResult(parkingSpaceMachineDto, parkingSpaceMachineNotifyDto.getReqBody());
    }
}
