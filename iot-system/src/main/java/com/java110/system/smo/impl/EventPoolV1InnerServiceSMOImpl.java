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


import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.PageDto;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.event.EventPoolDto;
import com.java110.po.event.EventPoolPo;
import com.java110.system.dao.IEventPoolV1ServiceDao;
import com.java110.intf.system.IEventPoolV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2023-09-27 17:31:24 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class EventPoolV1InnerServiceSMOImpl implements IEventPoolV1InnerServiceSMO {

    @Autowired
    private IEventPoolV1ServiceDao eventPoolV1ServiceDaoImpl;


    @Override
    public int saveEventPool(@RequestBody EventPoolPo eventPoolPo) {
        int saveFlag = eventPoolV1ServiceDaoImpl.saveEventPoolInfo(BeanConvertUtil.beanCovertMap(eventPoolPo));
        return saveFlag;
    }

    @Override
    public int updateEventPool(@RequestBody EventPoolPo eventPoolPo) {
        int saveFlag = eventPoolV1ServiceDaoImpl.updateEventPoolInfo(BeanConvertUtil.beanCovertMap(eventPoolPo));
        return saveFlag;
    }

    @Override
    public int deleteEventPool(@RequestBody EventPoolPo eventPoolPo) {
        eventPoolPo.setStatusCd("1");
        int saveFlag = eventPoolV1ServiceDaoImpl.updateEventPoolInfo(BeanConvertUtil.beanCovertMap(eventPoolPo));
        return saveFlag;
    }

    @Override
    public List<EventPoolDto> queryEventPools(@RequestBody EventPoolDto eventPoolDto) {

        //校验是否传了 分页信息

        int page = eventPoolDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            eventPoolDto.setPage((page - 1) * eventPoolDto.getRow());
        }

        List<EventPoolDto> eventPools = BeanConvertUtil.covertBeanList(eventPoolV1ServiceDaoImpl.getEventPoolInfo(BeanConvertUtil.beanCovertMap(eventPoolDto)), EventPoolDto.class);

        return eventPools;
    }


    @Override
    public int queryEventPoolsCount(@RequestBody EventPoolDto eventPoolDto) {
        return eventPoolV1ServiceDaoImpl.queryEventPoolsCount(BeanConvertUtil.beanCovertMap(eventPoolDto));
    }

    @Override
    public int saveEventPoolList(@RequestBody List<EventPoolPo> eventPoolPoList) {
        List<Object> temp = eventPoolPoList.stream().map(eventPoolPo -> (Object) eventPoolPo).collect(Collectors.toList());
        return eventPoolV1ServiceDaoImpl.saveEventPoolInfoList(BeanConvertUtil.beanCovertMapList(temp));
    }

    @Override
    public List<Map> getCommunityEventCount(@RequestBody Map info) {
        List<Map> eventPools = eventPoolV1ServiceDaoImpl.getCommunityEventCount(info);

        return eventPools;
    }

}
