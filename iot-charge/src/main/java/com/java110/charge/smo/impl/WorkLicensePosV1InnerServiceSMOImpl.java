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
package com.java110.charge.smo.impl;


import com.java110.bean.dto.PageDto;
import com.java110.charge.dao.IWorkLicensePosV1ServiceDao;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.intf.charge.IWorkLicensePosV1InnerServiceSMO;
import com.java110.dto.workLicensePos.WorkLicensePosDto;
import com.java110.po.workLicensePos.WorkLicensePosPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2024-02-29 09:49:07 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class WorkLicensePosV1InnerServiceSMOImpl implements IWorkLicensePosV1InnerServiceSMO {

    @Autowired
    private IWorkLicensePosV1ServiceDao workLicensePosV1ServiceDaoImpl;


    @Override
    public int saveWorkLicensePos(@RequestBody  WorkLicensePosPo workLicensePosPo) {
        int saveFlag = workLicensePosV1ServiceDaoImpl.saveWorkLicensePosInfo(BeanConvertUtil.beanCovertMap(workLicensePosPo));
        return saveFlag;
    }

     @Override
    public int updateWorkLicensePos(@RequestBody  WorkLicensePosPo workLicensePosPo) {
        int saveFlag = workLicensePosV1ServiceDaoImpl.updateWorkLicensePosInfo(BeanConvertUtil.beanCovertMap(workLicensePosPo));
        return saveFlag;
    }

     @Override
    public int deleteWorkLicensePos(@RequestBody  WorkLicensePosPo workLicensePosPo) {
       workLicensePosPo.setStatusCd("1");
       int saveFlag = workLicensePosV1ServiceDaoImpl.updateWorkLicensePosInfo(BeanConvertUtil.beanCovertMap(workLicensePosPo));
       return saveFlag;
    }

    @Override
    public List<WorkLicensePosDto> queryWorkLicensePoss(@RequestBody  WorkLicensePosDto workLicensePosDto) {

        //校验是否传了 分页信息

        int page = workLicensePosDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            workLicensePosDto.setPage((page - 1) * workLicensePosDto.getRow());
        }

        List<WorkLicensePosDto> workLicensePoss = BeanConvertUtil.covertBeanList(workLicensePosV1ServiceDaoImpl.getWorkLicensePosInfo(BeanConvertUtil.beanCovertMap(workLicensePosDto)), WorkLicensePosDto.class);

        return workLicensePoss;
    }


    @Override
    public int queryWorkLicensePossCount(@RequestBody WorkLicensePosDto workLicensePosDto) {
        return workLicensePosV1ServiceDaoImpl.queryWorkLicensePossCount(BeanConvertUtil.beanCovertMap(workLicensePosDto));    }

}
