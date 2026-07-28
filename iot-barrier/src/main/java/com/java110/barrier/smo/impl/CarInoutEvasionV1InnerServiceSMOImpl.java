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


import com.java110.barrier.dao.ICarInoutEvasionV1ServiceDao;
import com.java110.bean.dto.PageDto;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.carInoutEvasion.CarInoutEvasionDto;
import com.java110.intf.barrier.ICarInoutEvasionV1InnerServiceSMO;
import com.java110.po.carInoutEvasion.CarInoutEvasionPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CarInoutEvasionV1InnerServiceSMOImpl implements ICarInoutEvasionV1InnerServiceSMO {

    @Autowired
    private ICarInoutEvasionV1ServiceDao carInoutEvasionV1ServiceDaoImpl;

    @Override
    public int saveCarInoutEvasion(@RequestBody CarInoutEvasionPo carInoutEvasionPo) {
        int saveFlag = carInoutEvasionV1ServiceDaoImpl.saveCarInoutEvasionInfo(BeanConvertUtil.beanCovertMap(carInoutEvasionPo));
        return saveFlag;
    }

    @Override
    public int updateCarInoutEvasion(@RequestBody CarInoutEvasionPo carInoutEvasionPo) {
        int saveFlag = carInoutEvasionV1ServiceDaoImpl.updateCarInoutEvasionInfo(BeanConvertUtil.beanCovertMap(carInoutEvasionPo));
        return saveFlag;
    }

    @Override
    public int deleteCarInoutEvasion(@RequestBody CarInoutEvasionPo carInoutEvasionPo) {
        carInoutEvasionPo.setStatusCd("1");
        int saveFlag = carInoutEvasionV1ServiceDaoImpl.updateCarInoutEvasionInfo(BeanConvertUtil.beanCovertMap(carInoutEvasionPo));
        return saveFlag;
    }

    @Override
    public List<CarInoutEvasionDto> queryCarInoutEvasions(@RequestBody CarInoutEvasionDto carInoutEvasionDto) {
        int page = carInoutEvasionDto.getPage();
        if (page != PageDto.DEFAULT_PAGE) {
            carInoutEvasionDto.setPage((page - 1) * carInoutEvasionDto.getRow());
        }
        List<CarInoutEvasionDto> result = BeanConvertUtil.covertBeanList(
                carInoutEvasionV1ServiceDaoImpl.getCarInoutEvasionInfo(BeanConvertUtil.beanCovertMap(carInoutEvasionDto)),
                CarInoutEvasionDto.class);
        return result;
    }

    @Override
    public int queryCarInoutEvasionsCount(@RequestBody CarInoutEvasionDto carInoutEvasionDto) {
        return carInoutEvasionV1ServiceDaoImpl.queryCarInoutEvasionsCount(BeanConvertUtil.beanCovertMap(carInoutEvasionDto));
    }
}
