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
package com.java110.monitor.smo.impl;


import com.java110.bean.dto.PageDto;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.monitor.MonitorMachineDto;
import com.java110.dto.monitorManufactor.MonitorManufactorDto;
import com.java110.intf.monitor.IMonitorManufactorV1InnerServiceSMO;
import com.java110.monitor.dao.IMonitorMachineV1ServiceDao;
import com.java110.intf.monitor.IMonitorMachineV1InnerServiceSMO;
import com.java110.monitor.factory.IMonitorModelAdapt;
import com.java110.po.monitor.MonitorMachinePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2023-10-18 10:33:51 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class MonitorMachineV1InnerServiceSMOImpl implements IMonitorMachineV1InnerServiceSMO {

    @Autowired
    private IMonitorMachineV1ServiceDao monitorMachineV1ServiceDaoImpl;

    @Autowired
    private IMonitorManufactorV1InnerServiceSMO monitorManufactorV1InnerServiceSMOImpl;


    @Override
    public int saveMonitorMachine(@RequestBody MonitorMachinePo monitorMachinePo) {
        int saveFlag = monitorMachineV1ServiceDaoImpl.saveMonitorMachineInfo(BeanConvertUtil.beanCovertMap(monitorMachinePo));
        return saveFlag;
    }

    @Override
    public int updateMonitorMachine(@RequestBody MonitorMachinePo monitorMachinePo) {
        int saveFlag = monitorMachineV1ServiceDaoImpl.updateMonitorMachineInfo(BeanConvertUtil.beanCovertMap(monitorMachinePo));
        return saveFlag;
    }

    @Override
    public int deleteMonitorMachine(@RequestBody MonitorMachinePo monitorMachinePo) {
        monitorMachinePo.setStatusCd("1");
        int saveFlag = monitorMachineV1ServiceDaoImpl.updateMonitorMachineInfo(BeanConvertUtil.beanCovertMap(monitorMachinePo));
        return saveFlag;
    }

    @Override
    public List<MonitorMachineDto> queryMonitorMachines(@RequestBody MonitorMachineDto monitorMachineDto) {

        //校验是否传了 分页信息

        int page = monitorMachineDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            monitorMachineDto.setPage((page - 1) * monitorMachineDto.getRow());
        }

        List<MonitorMachineDto> monitorMachines = BeanConvertUtil.covertBeanList(monitorMachineV1ServiceDaoImpl.getMonitorMachineInfo(BeanConvertUtil.beanCovertMap(monitorMachineDto)), MonitorMachineDto.class);

        return monitorMachines;
    }


    @Override
    public int queryMonitorMachinesCount(@RequestBody MonitorMachineDto monitorMachineDto) {
        return monitorMachineV1ServiceDaoImpl.queryMonitorMachinesCount(BeanConvertUtil.beanCovertMap(monitorMachineDto));
    }

    @Override
    public List<MonitorMachineDto> queryMonitorMachineList(@RequestBody MonitorMachineDto monitorMachineDto) {
        int page = monitorMachineDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            monitorMachineDto.setPage((page - 1) * monitorMachineDto.getRow());
        }

        List<MonitorMachineDto> monitorMachines = BeanConvertUtil.covertBeanList(monitorMachineV1ServiceDaoImpl.getMonitorMachineInfo1(BeanConvertUtil.beanCovertMap(monitorMachineDto)), MonitorMachineDto.class);

        return monitorMachines;
    }

    @Override
    public String getPlayVideoUrl(@RequestBody MonitorMachineDto monitorMachineDto) {
        List<MonitorMachineDto> monitorMachineDtos = queryMonitorMachineList(monitorMachineDto);

        Assert.listOnlyOne(monitorMachineDtos, "摄像头不存在");


        MonitorManufactorDto monitorManufactorDto = new MonitorManufactorDto();
        monitorManufactorDto.setMmId(monitorMachineDtos.get(0).getMmId());
        List<MonitorManufactorDto> monitorManufactorDtos = monitorManufactorV1InnerServiceSMOImpl.queryMonitorManufactors(monitorManufactorDto);
        Assert.listOnlyOne(monitorManufactorDtos, "监控厂家不存在");


        IMonitorModelAdapt monitorModelAdapt = ApplicationContextFactory.getBean(monitorManufactorDtos.get(0).getMmBean(), IMonitorModelAdapt.class);

        if (monitorModelAdapt == null) {
            throw new CmdException("厂家协议未实现");
        }

        String url = monitorModelAdapt.getVideoUrl(monitorMachineDtos.get(0), monitorManufactorDtos.get(0));
        return url;
    }

}
