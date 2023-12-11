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
import com.java110.dto.instrument.InstrumentFactoryDto;
import com.java110.intf.meter.IInstrumentFactoryV1InnerServiceSMO;
import com.java110.meter.dao.IInstrumentFactoryV1ServiceDao;
import com.java110.po.instrument.InstrumentFactoryPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2023-11-22 11:22:24 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class InstrumentFactoryV1InnerServiceSMOImp implements IInstrumentFactoryV1InnerServiceSMO {

    @Autowired
    private IInstrumentFactoryV1ServiceDao instrumentFactoryV1ServiceDaoImpl;


    @Override
    public int saveInstrumentFactory(@RequestBody  InstrumentFactoryPo instrumentFactoryPo) {
        int saveFlag = instrumentFactoryV1ServiceDaoImpl.saveInstrumentFactoryInfo(BeanConvertUtil.beanCovertMap(instrumentFactoryPo));
        return saveFlag;
    }

     @Override
    public int updateInstrumentFactory(@RequestBody InstrumentFactoryPo instrumentFactoryPo) {
        int saveFlag = instrumentFactoryV1ServiceDaoImpl.updateInstrumentFactoryInfo(BeanConvertUtil.beanCovertMap(instrumentFactoryPo));
        return saveFlag;
    }

     @Override
    public int deleteInstrumentFactory(@RequestBody  InstrumentFactoryPo instrumentFactoryPo) {
       instrumentFactoryPo.setStatusCd("1");
       int saveFlag = instrumentFactoryV1ServiceDaoImpl.updateInstrumentFactoryInfo(BeanConvertUtil.beanCovertMap(instrumentFactoryPo));
       return saveFlag;
    }

    @Override
    public List<InstrumentFactoryDto> queryInstrumentFactorys(@RequestBody  InstrumentFactoryDto instrumentFactoryDto) {

        //校验是否传了 分页信息

        int page = instrumentFactoryDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            instrumentFactoryDto.setPage((page - 1) * instrumentFactoryDto.getRow());
        }

        List<InstrumentFactoryDto> instrumentFactorys = BeanConvertUtil.covertBeanList(instrumentFactoryV1ServiceDaoImpl.getInstrumentFactoryInfo(BeanConvertUtil.beanCovertMap(instrumentFactoryDto)), InstrumentFactoryDto.class);

        return instrumentFactorys;
    }


    @Override
    public int queryInstrumentFactorysCount(@RequestBody InstrumentFactoryDto instrumentFactoryDto) {
        return instrumentFactoryV1ServiceDaoImpl.queryInstrumentFactorysCount(BeanConvertUtil.beanCovertMap(instrumentFactoryDto));    }

    @Override
    public List<InstrumentFactoryDto> queryInstrumentFactoryList(InstrumentFactoryDto instrumentFactoryDto) {
        int page = instrumentFactoryDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            instrumentFactoryDto.setPage((page - 1) * instrumentFactoryDto.getRow());
        }

        List<InstrumentFactoryDto> instrumentFactorys = BeanConvertUtil.covertBeanList(instrumentFactoryV1ServiceDaoImpl.getInstrumentFactoryInfo1(BeanConvertUtil.beanCovertMap(instrumentFactoryDto)), InstrumentFactoryDto.class);

        return instrumentFactorys;
    }

}
