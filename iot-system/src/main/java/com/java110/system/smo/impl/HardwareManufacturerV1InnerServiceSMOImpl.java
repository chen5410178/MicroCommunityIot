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
package com.java110.system.smo.impl;


import com.java110.bean.dto.PageDto;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.system.dao.IHardwareManufacturerV1ServiceDao;
import com.java110.intf.system.IHardwareManufacturerV1InnerServiceSMO;
import com.java110.dto.hardwareManufacturer.HardwareManufacturerDto;
import com.java110.po.hardwareManufacturer.HardwareManufacturerPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2023-08-15 09:27:06 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class HardwareManufacturerV1InnerServiceSMOImpl  implements IHardwareManufacturerV1InnerServiceSMO {

    @Autowired
    private IHardwareManufacturerV1ServiceDao hardwareManufacturerV1ServiceDaoImpl;


    @Override
    public int saveHardwareManufacturer(@RequestBody  HardwareManufacturerPo hardwareManufacturerPo) {
        int saveFlag = hardwareManufacturerV1ServiceDaoImpl.saveHardwareManufacturerInfo(BeanConvertUtil.beanCovertMap(hardwareManufacturerPo));
        return saveFlag;
    }

     @Override
    public int updateHardwareManufacturer(@RequestBody  HardwareManufacturerPo hardwareManufacturerPo) {
        int saveFlag = hardwareManufacturerV1ServiceDaoImpl.updateHardwareManufacturerInfo(BeanConvertUtil.beanCovertMap(hardwareManufacturerPo));
        return saveFlag;
    }

     @Override
    public int deleteHardwareManufacturer(@RequestBody  HardwareManufacturerPo hardwareManufacturerPo) {
       hardwareManufacturerPo.setStatusCd("1");
       int saveFlag = hardwareManufacturerV1ServiceDaoImpl.updateHardwareManufacturerInfo(BeanConvertUtil.beanCovertMap(hardwareManufacturerPo));
       return saveFlag;
    }

    @Override
    public List<HardwareManufacturerDto> queryHardwareManufacturers(@RequestBody  HardwareManufacturerDto hardwareManufacturerDto) {

        //校验是否传了 分页信息

        int page = hardwareManufacturerDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            hardwareManufacturerDto.setPage((page - 1) * hardwareManufacturerDto.getRow());
        }

        List<HardwareManufacturerDto> hardwareManufacturers = BeanConvertUtil.covertBeanList(hardwareManufacturerV1ServiceDaoImpl.getHardwareManufacturerInfo(BeanConvertUtil.beanCovertMap(hardwareManufacturerDto)), HardwareManufacturerDto.class);

        return hardwareManufacturers;
    }


    @Override
    public int queryHardwareManufacturersCount(@RequestBody HardwareManufacturerDto hardwareManufacturerDto) {
        return hardwareManufacturerV1ServiceDaoImpl.queryHardwareManufacturersCount(BeanConvertUtil.beanCovertMap(hardwareManufacturerDto));    }

}
