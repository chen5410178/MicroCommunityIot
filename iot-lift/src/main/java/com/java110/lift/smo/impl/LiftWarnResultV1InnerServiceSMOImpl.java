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
package com.java110.lift.smo.impl;


import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ListUtil;
import com.java110.dto.hardwareManufacturer.HardwareManufacturerDto;
import com.java110.dto.lift.LiftCameraDto;
import com.java110.dto.lift.LiftWarnNotifyDto;
import com.java110.intf.lift.ILiftCameraV1InnerServiceSMO;
import com.java110.intf.lift.ILiftWarnResultV1InnerServiceSMO;
import com.java110.intf.system.IHardwareManufacturerV1InnerServiceSMO;
import com.java110.lift.factory.ILiftCameraAdapt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2023-08-15 02:16:27 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class LiftWarnResultV1InnerServiceSMOImpl implements ILiftWarnResultV1InnerServiceSMO {

    private ILiftCameraAdapt liftCameraAdaptImpl;

    @Autowired
    private IHardwareManufacturerV1InnerServiceSMO hardwareManufacturerV1InnerServiceSMOImpl;

    @Autowired
    private ILiftCameraV1InnerServiceSMO liftCameraV1InnerServiceSMOImpl;


    @Override
    public String liftWarnResult(@RequestBody LiftWarnNotifyDto liftWarnNotifyDto) {

        LiftCameraDto liftCameraDto = new LiftCameraDto();
        liftCameraDto.setCameraCode(liftWarnNotifyDto.getMachineCode());
        List<LiftCameraDto> liftCameraDtos = liftCameraV1InnerServiceSMOImpl.queryLiftCameras(liftCameraDto);
        if(ListUtil.isNull(liftCameraDtos)){
            throw new IllegalArgumentException("电梯相机不存在");
        }

        liftCameraAdaptImpl = ApplicationContextFactory.getBean(liftCameraDtos.get(0).getFactoryId(), ILiftCameraAdapt.class);

        String result = liftCameraAdaptImpl.liftWarnResult(liftCameraDtos.get(0), liftWarnNotifyDto.getReqBody());
        return result;
    }

    @Override
    public String heartbeat(@RequestBody LiftWarnNotifyDto liftWarnNotifyDto) {

        LiftCameraDto liftCameraDto = new LiftCameraDto();
        liftCameraDto.setCameraCode(liftWarnNotifyDto.getMachineCode());
        List<LiftCameraDto> liftCameraDtos = liftCameraV1InnerServiceSMOImpl.queryLiftCameras(liftCameraDto);
        if(ListUtil.isNull(liftCameraDtos)){
            throw new IllegalArgumentException("电梯相机不存在");
        }

        liftCameraAdaptImpl = ApplicationContextFactory.getBean(liftCameraDtos.get(0).getFactoryId(), ILiftCameraAdapt.class);

        String result = liftCameraAdaptImpl.heartbeat(liftCameraDtos.get(0), liftWarnNotifyDto.getReqBody());
        return result;
    }


}
