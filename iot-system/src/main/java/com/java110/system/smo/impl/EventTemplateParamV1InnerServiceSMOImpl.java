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
import com.java110.dto.event.EventTemplateParamDto;
import com.java110.po.event.EventTemplateParamPo;
import com.java110.system.dao.IEventTemplateParamV1ServiceDao;
import com.java110.intf.system.IEventTemplateParamV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2023-09-27 10:09:26 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class EventTemplateParamV1InnerServiceSMOImpl implements IEventTemplateParamV1InnerServiceSMO {

    @Autowired
    private IEventTemplateParamV1ServiceDao eventTemplateParamV1ServiceDaoImpl;


    @Override
    public int saveEventTemplateParam(@RequestBody EventTemplateParamPo eventTemplateParamPo) {
        int saveFlag = eventTemplateParamV1ServiceDaoImpl.saveEventTemplateParamInfo(BeanConvertUtil.beanCovertMap(eventTemplateParamPo));
        return saveFlag;
    }

    @Override
    public int updateEventTemplateParam(@RequestBody EventTemplateParamPo eventTemplateParamPo) {
        int saveFlag = eventTemplateParamV1ServiceDaoImpl.updateEventTemplateParamInfo(BeanConvertUtil.beanCovertMap(eventTemplateParamPo));
        return saveFlag;
    }

    @Override
    public int deleteEventTemplateParam(@RequestBody EventTemplateParamPo eventTemplateParamPo) {
        eventTemplateParamPo.setStatusCd("1");
        int saveFlag = eventTemplateParamV1ServiceDaoImpl.updateEventTemplateParamInfo(BeanConvertUtil.beanCovertMap(eventTemplateParamPo));
        return saveFlag;
    }

    @Override
    public List<EventTemplateParamDto> queryEventTemplateParams(@RequestBody EventTemplateParamDto eventTemplateParamDto) {

        //校验是否传了 分页信息

        int page = eventTemplateParamDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            eventTemplateParamDto.setPage((page - 1) * eventTemplateParamDto.getRow());
        }

        List<EventTemplateParamDto> eventTemplateParams = BeanConvertUtil.covertBeanList(eventTemplateParamV1ServiceDaoImpl.getEventTemplateParamInfo(BeanConvertUtil.beanCovertMap(eventTemplateParamDto)), EventTemplateParamDto.class);

        return eventTemplateParams;
    }


    @Override
    public int queryEventTemplateParamsCount(@RequestBody EventTemplateParamDto eventTemplateParamDto) {
        return eventTemplateParamV1ServiceDaoImpl.queryEventTemplateParamsCount(BeanConvertUtil.beanCovertMap(eventTemplateParamDto));
    }

    @Override
    public int saveEventTemplateParamList(List<EventTemplateParamPo> eventTemplateParamPoList) {
        List<Object> temp = eventTemplateParamPoList.stream().map(eventTemplateParamPo -> (Object) eventTemplateParamPo).collect(Collectors.toList());
        return eventTemplateParamV1ServiceDaoImpl.saveEventTemplateParamList(BeanConvertUtil.beanCovertMapList(temp));
    }

    @Override
    public int updateEventTemplateParamList(List<EventTemplateParamPo> eventTemplateParamPoList) {
        List<Object> temp = eventTemplateParamPoList.stream().map(eventTemplateParamPo -> (Object) eventTemplateParamPo).collect(Collectors.toList());
        return eventTemplateParamV1ServiceDaoImpl.updateEventTemplateParamList(BeanConvertUtil.beanCovertMapList(temp));
    }

}
