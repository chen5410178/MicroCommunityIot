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

import com.java110.barrier.dao.ICarInoutHisV1ServiceDao;
import com.java110.bean.dto.PageDto;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.carInout.CarInoutHisDto;
import com.java110.intf.barrier.ICarInoutHisV1InnerServiceSMO;
import com.java110.po.carInout.CarInoutHisPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CarInoutHisV1InnerServiceSMOImpl implements ICarInoutHisV1InnerServiceSMO {

    @Autowired
    private ICarInoutHisV1ServiceDao carInoutHisV1ServiceDaoImpl;

    @Override
    public int saveCarInoutHis(@RequestBody CarInoutHisPo carInoutHisPo) {
        return carInoutHisV1ServiceDaoImpl.saveCarInoutHisInfo(BeanConvertUtil.beanCovertMap(carInoutHisPo));
    }

    @Override
    public int saveCarInoutHiss(@RequestBody List<CarInoutHisPo> carInoutHisPos) {
        return 0;
    }

    @Override
    public List<CarInoutHisDto> queryCarInoutHiss(@RequestBody CarInoutHisDto carInoutHisDto) {
        int page = carInoutHisDto.getPage();
        if (page != PageDto.DEFAULT_PAGE) {
            carInoutHisDto.setPage((page - 1) * carInoutHisDto.getRow());
        }
        List<CarInoutHisDto> carInoutHiss = BeanConvertUtil.covertBeanList(
                carInoutHisV1ServiceDaoImpl.getCarInoutHisInfo(BeanConvertUtil.beanCovertMap(carInoutHisDto)),
                CarInoutHisDto.class);
        return carInoutHiss;
    }

    @Override
    public int queryCarInoutHissCount(@RequestBody CarInoutHisDto carInoutHisDto) {
        return carInoutHisV1ServiceDaoImpl.queryCarInoutHisCount(BeanConvertUtil.beanCovertMap(carInoutHisDto));
    }

    @Override
    public int deleteCarInoutHis(@RequestBody CarInoutHisDto carInoutHisDto) {
        return carInoutHisV1ServiceDaoImpl.deleteCarInoutHisInfo(BeanConvertUtil.beanCovertMap(carInoutHisDto));
    }
}
