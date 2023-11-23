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
import com.java110.dto.instrument.InstrumentTypeDto;
import com.java110.meter.dao.IInstrumentTypeV1ServiceDao;
import com.java110.intf.meter.IInstrumentTypeV1InnerServiceSMO;
import com.java110.po.instrument.InstrumentTypePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2023-11-23 09:04:53 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class InstrumentTypeV1InnerServiceSMOImpl implements IInstrumentTypeV1InnerServiceSMO {

    @Autowired
    private IInstrumentTypeV1ServiceDao instrumentTypeV1ServiceDaoImpl;


    @Override
    public int saveInstrumentType(@RequestBody InstrumentTypePo instrumentTypePo) {
        int saveFlag = instrumentTypeV1ServiceDaoImpl.saveInstrumentTypeInfo(BeanConvertUtil.beanCovertMap(instrumentTypePo));
        return saveFlag;
    }

    @Override
    public int updateInstrumentType(@RequestBody InstrumentTypePo instrumentTypePo) {
        int saveFlag = instrumentTypeV1ServiceDaoImpl.updateInstrumentTypeInfo(BeanConvertUtil.beanCovertMap(instrumentTypePo));
        return saveFlag;
    }

    @Override
    public int deleteInstrumentType(@RequestBody InstrumentTypePo instrumentTypePo) {
        instrumentTypePo.setStatusCd("1");
        int saveFlag = instrumentTypeV1ServiceDaoImpl.updateInstrumentTypeInfo(BeanConvertUtil.beanCovertMap(instrumentTypePo));
        return saveFlag;
    }

    @Override
    public List<InstrumentTypeDto> queryInstrumentTypes(@RequestBody InstrumentTypeDto instrumentTypeDto) {

        //校验是否传了 分页信息

        int page = instrumentTypeDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            instrumentTypeDto.setPage((page - 1) * instrumentTypeDto.getRow());
        }

        List<InstrumentTypeDto> instrumentTypes = BeanConvertUtil.covertBeanList(instrumentTypeV1ServiceDaoImpl.getInstrumentTypeInfo(BeanConvertUtil.beanCovertMap(instrumentTypeDto)), InstrumentTypeDto.class);

        return instrumentTypes;
    }


    @Override
    public int queryInstrumentTypesCount(@RequestBody InstrumentTypeDto instrumentTypeDto) {
        return instrumentTypeV1ServiceDaoImpl.queryInstrumentTypesCount(BeanConvertUtil.beanCovertMap(instrumentTypeDto));
    }

}
