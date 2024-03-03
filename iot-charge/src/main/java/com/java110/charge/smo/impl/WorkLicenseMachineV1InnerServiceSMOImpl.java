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


import com.java110.bean.ResultVo;
import com.java110.bean.dto.PageDto;
import com.java110.charge.dao.IWorkLicenseMachineV1ServiceDao;
import com.java110.charge.workLicense.IWorkLicenseAdapt;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.workLicenseMachine.WorkLicenseTtsTextDto;
import com.java110.intf.charge.IWorkLicenseMachineV1InnerServiceSMO;
import com.java110.dto.workLicenseMachine.WorkLicenseMachineDto;
import com.java110.po.workLicenseMachine.WorkLicenseMachinePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2024-02-29 02:21:42 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class WorkLicenseMachineV1InnerServiceSMOImpl  implements IWorkLicenseMachineV1InnerServiceSMO {

    @Autowired
    private IWorkLicenseMachineV1ServiceDao workLicenseMachineV1ServiceDaoImpl;


    @Override
    public int saveWorkLicenseMachine(@RequestBody  WorkLicenseMachinePo workLicenseMachinePo) {
        int saveFlag = workLicenseMachineV1ServiceDaoImpl.saveWorkLicenseMachineInfo(BeanConvertUtil.beanCovertMap(workLicenseMachinePo));
        return saveFlag;
    }

     @Override
    public int updateWorkLicenseMachine(@RequestBody  WorkLicenseMachinePo workLicenseMachinePo) {
        int saveFlag = workLicenseMachineV1ServiceDaoImpl.updateWorkLicenseMachineInfo(BeanConvertUtil.beanCovertMap(workLicenseMachinePo));
        return saveFlag;
    }

     @Override
    public int deleteWorkLicenseMachine(@RequestBody  WorkLicenseMachinePo workLicenseMachinePo) {
       workLicenseMachinePo.setStatusCd("1");
       int saveFlag = workLicenseMachineV1ServiceDaoImpl.updateWorkLicenseMachineInfo(BeanConvertUtil.beanCovertMap(workLicenseMachinePo));
       return saveFlag;
    }

    @Override
    public List<WorkLicenseMachineDto> queryWorkLicenseMachines(@RequestBody  WorkLicenseMachineDto workLicenseMachineDto) {

        //校验是否传了 分页信息

        int page = workLicenseMachineDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            workLicenseMachineDto.setPage((page - 1) * workLicenseMachineDto.getRow());
        }

        List<WorkLicenseMachineDto> workLicenseMachines = BeanConvertUtil.covertBeanList(workLicenseMachineV1ServiceDaoImpl.getWorkLicenseMachineInfo(BeanConvertUtil.beanCovertMap(workLicenseMachineDto)), WorkLicenseMachineDto.class);

        return workLicenseMachines;
    }


    @Override
    public int queryWorkLicenseMachinesCount(@RequestBody WorkLicenseMachineDto workLicenseMachineDto) {
        return workLicenseMachineV1ServiceDaoImpl.queryWorkLicenseMachinesCount(BeanConvertUtil.beanCovertMap(workLicenseMachineDto));    }

    @Override
    public ResultVo playTts(@RequestBody WorkLicenseTtsTextDto workLicenseTtsTextDto) {
        IWorkLicenseAdapt workLicenseAdapt = ApplicationContextFactory.getBean(workLicenseTtsTextDto.getWorkLicenseFactoryDto().getBeanImpl(), IWorkLicenseAdapt.class);
        if (workLicenseAdapt == null) {
            throw new CmdException("厂家接口未实现");
        }

        ResultVo resultVo = workLicenseAdapt.playTts(workLicenseTtsTextDto.getWorkLicenseMachineDto(), workLicenseTtsTextDto.getTtsText());
        return resultVo;
    }

}
