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
package com.java110.barrier.smo.impl;


import com.java110.barrier.dao.ICarInoutEvasionConfigV1ServiceDao;
import com.java110.bean.dto.PageDto;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.carInoutEvasion.CarInoutEvasionConfigDto;
import com.java110.intf.barrier.ICarInoutEvasionConfigV1InnerServiceSMO;
import com.java110.po.carInoutEvasion.CarInoutEvasionConfigPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CarInoutEvasionConfigV1InnerServiceSMOImpl implements ICarInoutEvasionConfigV1InnerServiceSMO {

    @Autowired
    private ICarInoutEvasionConfigV1ServiceDao carInoutEvasionConfigV1ServiceDaoImpl;

    @Override
    public int saveCarInoutEvasionConfig(@RequestBody CarInoutEvasionConfigPo carInoutEvasionConfigPo) {
        int saveFlag = carInoutEvasionConfigV1ServiceDaoImpl.saveCarInoutEvasionConfigInfo(BeanConvertUtil.beanCovertMap(carInoutEvasionConfigPo));
        return saveFlag;
    }

    @Override
    public int updateCarInoutEvasionConfig(@RequestBody CarInoutEvasionConfigPo carInoutEvasionConfigPo) {
        int saveFlag = carInoutEvasionConfigV1ServiceDaoImpl.updateCarInoutEvasionConfigInfo(BeanConvertUtil.beanCovertMap(carInoutEvasionConfigPo));
        return saveFlag;
    }

    @Override
    public List<CarInoutEvasionConfigDto> queryCarInoutEvasionConfigs(@RequestBody CarInoutEvasionConfigDto carInoutEvasionConfigDto) {
        int page = carInoutEvasionConfigDto.getPage();
        if (page != PageDto.DEFAULT_PAGE) {
            carInoutEvasionConfigDto.setPage((page - 1) * carInoutEvasionConfigDto.getRow());
        }
        List<CarInoutEvasionConfigDto> result = BeanConvertUtil.covertBeanList(
                carInoutEvasionConfigV1ServiceDaoImpl.getCarInoutEvasionConfigInfo(BeanConvertUtil.beanCovertMap(carInoutEvasionConfigDto)),
                CarInoutEvasionConfigDto.class);
        return result;
    }
}
