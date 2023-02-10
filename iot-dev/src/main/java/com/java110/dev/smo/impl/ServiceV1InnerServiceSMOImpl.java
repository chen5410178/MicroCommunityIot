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
package com.java110.dev.smo.impl;


import com.java110.bean.dto.PageDto;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dev.dao.IServiceV1ServiceDao;
import com.java110.intf.dev.IServiceV1InnerServiceSMO;
import com.java110.dto.service.ServiceDto;
import com.java110.po.service.ServicePo;
import com.java110.dto.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2023-02-11 01:08:41 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class ServiceV1InnerServiceSMOImpl implements IServiceV1InnerServiceSMO {

    @Autowired
    private IServiceV1ServiceDao serviceV1ServiceDaoImpl;


    @Override
    public int saveService(@RequestBody ServicePo servicePo) {
        int saveFlag = serviceV1ServiceDaoImpl.saveServiceInfo(BeanConvertUtil.beanCovertMap(servicePo));
        return saveFlag;
    }

    @Override
    public int updateService(@RequestBody ServicePo servicePo) {
        int saveFlag = serviceV1ServiceDaoImpl.updateServiceInfo(BeanConvertUtil.beanCovertMap(servicePo));
        return saveFlag;
    }

    @Override
    public int deleteService(@RequestBody ServicePo servicePo) {
        servicePo.setStatusCd("1");
        int saveFlag = serviceV1ServiceDaoImpl.updateServiceInfo(BeanConvertUtil.beanCovertMap(servicePo));
        return saveFlag;
    }

    @Override
    public List<ServiceDto> queryServices(@RequestBody ServiceDto serviceDto) {

        //校验是否传了 分页信息

        int page = serviceDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            serviceDto.setPage((page - 1) * serviceDto.getRow());
        }

        List<ServiceDto> services = BeanConvertUtil.covertBeanList(serviceV1ServiceDaoImpl.getServiceInfo(BeanConvertUtil.beanCovertMap(serviceDto)), ServiceDto.class);

        return services;
    }


    @Override
    public int queryServicesCount(@RequestBody ServiceDto serviceDto) {
        return serviceV1ServiceDaoImpl.queryServicesCount(BeanConvertUtil.beanCovertMap(serviceDto));
    }

}
