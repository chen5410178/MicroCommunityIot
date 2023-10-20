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
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.monitor.MonitorMachineModelDto;
import com.java110.monitor.dao.IMonitorMachineModelV1ServiceDao;
import com.java110.intf.monitor.IMonitorMachineModelV1InnerServiceSMO;
import com.java110.po.monitor.MonitorMachineModelPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2023-10-18 14:26:05 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class MonitorMachineModelV1InnerServiceSMOImpl implements IMonitorMachineModelV1InnerServiceSMO {

    @Autowired
    private IMonitorMachineModelV1ServiceDao monitorMachineModelV1ServiceDaoImpl;


    @Override
    public int saveMonitorMachineModel(@RequestBody MonitorMachineModelPo monitorMachineModelPo) {
        int saveFlag = monitorMachineModelV1ServiceDaoImpl.saveMonitorMachineModelInfo(BeanConvertUtil.beanCovertMap(monitorMachineModelPo));
        return saveFlag;
    }

    @Override
    public int updateMonitorMachineModel(@RequestBody MonitorMachineModelPo monitorMachineModelPo) {
        int saveFlag = monitorMachineModelV1ServiceDaoImpl.updateMonitorMachineModelInfo(BeanConvertUtil.beanCovertMap(monitorMachineModelPo));
        return saveFlag;
    }

    @Override
    public int deleteMonitorMachineModel(@RequestBody MonitorMachineModelPo monitorMachineModelPo) {
        monitorMachineModelPo.setStatusCd("1");
        int saveFlag = monitorMachineModelV1ServiceDaoImpl.updateMonitorMachineModelInfo(BeanConvertUtil.beanCovertMap(monitorMachineModelPo));
        return saveFlag;
    }

    @Override
    public List<MonitorMachineModelDto> queryMonitorMachineModels(@RequestBody MonitorMachineModelDto monitorMachineModelDto) {

        //校验是否传了 分页信息

        int page = monitorMachineModelDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            monitorMachineModelDto.setPage((page - 1) * monitorMachineModelDto.getRow());
        }

        List<MonitorMachineModelDto> monitorMachineModels = BeanConvertUtil.covertBeanList(monitorMachineModelV1ServiceDaoImpl.getMonitorMachineModelInfo(BeanConvertUtil.beanCovertMap(monitorMachineModelDto)), MonitorMachineModelDto.class);

        return monitorMachineModels;
    }


    @Override
    public int queryMonitorMachineModelsCount(@RequestBody MonitorMachineModelDto monitorMachineModelDto) {
        return monitorMachineModelV1ServiceDaoImpl.queryMonitorMachineModelsCount(BeanConvertUtil.beanCovertMap(monitorMachineModelDto));
    }

    @Override
    public int saveMonitorMachineModelList(List<MonitorMachineModelPo> monitorMachineModelPoList) {
        List<Object> temp = monitorMachineModelPoList.stream().map(monitorMachineModelPo -> (Object) monitorMachineModelPo).collect(Collectors.toList());
        return monitorMachineModelV1ServiceDaoImpl.saveMonitorMachineModelList(BeanConvertUtil.beanCovertMapList(temp));
    }

}
