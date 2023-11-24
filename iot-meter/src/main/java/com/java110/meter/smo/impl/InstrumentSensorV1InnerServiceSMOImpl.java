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
package com.java110.meter.smo.impl;


import com.java110.bean.dto.PageDto;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.instrument.InstrumentSensorDto;
import com.java110.meter.dao.IInstrumentSensorV1ServiceDao;
import com.java110.intf.meter.IInstrumentSensorV1InnerServiceSMO;
import com.java110.po.instrument.InstrumentSensorPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2023-11-24 10:17:29 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class InstrumentSensorV1InnerServiceSMOImpl implements IInstrumentSensorV1InnerServiceSMO {

    @Autowired
    private IInstrumentSensorV1ServiceDao instrumentSensorV1ServiceDaoImpl;


    @Override
    public int saveInstrumentSensor(@RequestBody InstrumentSensorPo instrumentSensorPo) {
        int saveFlag = instrumentSensorV1ServiceDaoImpl.saveInstrumentSensorInfo(BeanConvertUtil.beanCovertMap(instrumentSensorPo));
        return saveFlag;
    }

    @Override
    public int updateInstrumentSensor(@RequestBody InstrumentSensorPo instrumentSensorPo) {
        int saveFlag = instrumentSensorV1ServiceDaoImpl.updateInstrumentSensorInfo(BeanConvertUtil.beanCovertMap(instrumentSensorPo));
        return saveFlag;
    }

    @Override
    public int deleteInstrumentSensor(@RequestBody InstrumentSensorPo instrumentSensorPo) {
        instrumentSensorPo.setStatusCd("1");
        int saveFlag = instrumentSensorV1ServiceDaoImpl.updateInstrumentSensorInfo(BeanConvertUtil.beanCovertMap(instrumentSensorPo));
        return saveFlag;
    }

    @Override
    public List<InstrumentSensorDto> queryInstrumentSensors(@RequestBody InstrumentSensorDto instrumentSensorDto) {

        //校验是否传了 分页信息

        int page = instrumentSensorDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            instrumentSensorDto.setPage((page - 1) * instrumentSensorDto.getRow());
        }

        List<InstrumentSensorDto> instrumentSensors = BeanConvertUtil.covertBeanList(instrumentSensorV1ServiceDaoImpl.getInstrumentSensorInfo(BeanConvertUtil.beanCovertMap(instrumentSensorDto)), InstrumentSensorDto.class);

        return instrumentSensors;
    }


    @Override
    public int queryInstrumentSensorsCount(@RequestBody InstrumentSensorDto instrumentSensorDto) {
        return instrumentSensorV1ServiceDaoImpl.queryInstrumentSensorsCount(BeanConvertUtil.beanCovertMap(instrumentSensorDto));
    }

    @Override
    public int saveInstrumentSensorList(List<InstrumentSensorPo> instrumentSensorPos) {
        List<Object> temp = instrumentSensorPos.stream().map(instrumentSensorPo -> (Object) instrumentSensorPo).collect(Collectors.toList());
        return instrumentSensorV1ServiceDaoImpl.saveInstrumentSensorList(BeanConvertUtil.beanCovertMapList(temp));
    }

}
