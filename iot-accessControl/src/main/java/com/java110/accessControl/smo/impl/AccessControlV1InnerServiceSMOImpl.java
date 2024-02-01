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
package com.java110.accessControl.smo.impl;


import com.alibaba.fastjson.JSONObject;
import com.java110.accessControl.dao.IAccessControlV1ServiceDao;
import com.java110.accessControl.manufactor.IAccessControlManufactor;
import com.java110.bean.dto.PageDto;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.accessControl.MachineHeartbeatDto;
import com.java110.dto.chargeMachine.ChargeMachineOrderDto;
import com.java110.dto.hardwareManufacturer.HardwareManufacturerDto;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.intf.system.IHardwareManufacturerV1InnerServiceSMO;
import com.java110.po.accessControl.AccessControlPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2023-08-15 02:16:27 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class AccessControlV1InnerServiceSMOImpl implements IAccessControlV1InnerServiceSMO {

    @Autowired
    private IAccessControlV1ServiceDao accessControlV1ServiceDaoImpl;

    @Autowired
    private IHardwareManufacturerV1InnerServiceSMO hardwareManufacturerV1InnerServiceSMOImpl;


    @Override
    public int saveAccessControl(@RequestBody AccessControlPo accessControlPo) {

        //todo 通知适配器 添加人脸
        HardwareManufacturerDto hardwareManufacturerDto = new HardwareManufacturerDto();
        hardwareManufacturerDto.setHmId(accessControlPo.getImplBean());
        List<HardwareManufacturerDto> hardwareManufacturerDtos = hardwareManufacturerV1InnerServiceSMOImpl.queryHardwareManufacturers(hardwareManufacturerDto);

        Assert.listOnlyOne(hardwareManufacturerDtos, "协议不存在");

        IAccessControlManufactor accessControlManufactor = ApplicationContextFactory.getBean(hardwareManufacturerDtos.get(0).getProtocolImpl(), IAccessControlManufactor.class);
        boolean saveMachine = accessControlManufactor.addMachine(accessControlPo);

        if (!saveMachine) {
            throw new IllegalArgumentException("通知三方平台失败，对端未返回失败原因");
        }

        int saveFlag = accessControlV1ServiceDaoImpl.saveAccessControlInfo(BeanConvertUtil.beanCovertMap(accessControlPo));
        return saveFlag;
    }

    @Override
    public int updateAccessControl(@RequestBody AccessControlPo accessControlPo) {

        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineId(accessControlPo.getMachineId());
        accessControlDto.setCommunityId(accessControlPo.getCommunityId());
        List<AccessControlDto> accessControlDtos = queryAccessControls(accessControlDto);

        Assert.listOnlyOne(accessControlDtos, "门禁不存在");

        //todo 通知适配器 添加人脸
        HardwareManufacturerDto hardwareManufacturerDto = new HardwareManufacturerDto();
        hardwareManufacturerDto.setHmId(accessControlDtos.get(0).getImplBean());
        List<HardwareManufacturerDto> hardwareManufacturerDtos = hardwareManufacturerV1InnerServiceSMOImpl.queryHardwareManufacturers(hardwareManufacturerDto);

        Assert.listOnlyOne(hardwareManufacturerDtos, "协议不存在");

        IAccessControlManufactor accessControlManufactor = ApplicationContextFactory.getBean(hardwareManufacturerDtos.get(0).getProtocolImpl(), IAccessControlManufactor.class);
        boolean updateMachine = accessControlManufactor.updateMachine(accessControlPo);

        if (!updateMachine) {
            throw new IllegalArgumentException("通知三方平台失败，对端未返回失败原因");
        }

        int saveFlag = accessControlV1ServiceDaoImpl.updateAccessControlInfo(BeanConvertUtil.beanCovertMap(accessControlPo));
        return saveFlag;
    }

    @Override
    public int deleteAccessControl(@RequestBody AccessControlPo accessControlPo) {

        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineId(accessControlPo.getMachineId());
        accessControlDto.setCommunityId(accessControlPo.getCommunityId());
        List<AccessControlDto> accessControlDtos = queryAccessControls(accessControlDto);

        Assert.listOnlyOne(accessControlDtos, "门禁不存在");

        //todo 通知适配器 添加人脸
        HardwareManufacturerDto hardwareManufacturerDto = new HardwareManufacturerDto();
        hardwareManufacturerDto.setHmId(accessControlPo.getImplBean());
        List<HardwareManufacturerDto> hardwareManufacturerDtos = hardwareManufacturerV1InnerServiceSMOImpl.queryHardwareManufacturers(hardwareManufacturerDto);

        Assert.listOnlyOne(hardwareManufacturerDtos, "协议不存在");

        IAccessControlManufactor accessControlManufactor = ApplicationContextFactory.getBean(hardwareManufacturerDtos.get(0).getProtocolImpl(), IAccessControlManufactor.class);
        boolean deleteMachine = accessControlManufactor.deleteMachine(accessControlPo);

        if (!deleteMachine) {
            throw new IllegalArgumentException("通知三方平台失败，对端未返回失败原因");
        }

        accessControlPo.setStatusCd("1");
        int saveFlag = accessControlV1ServiceDaoImpl.updateAccessControlInfo(BeanConvertUtil.beanCovertMap(accessControlPo));
        return saveFlag;
    }

    @Override
    public List<AccessControlDto> queryAccessControls(@RequestBody AccessControlDto accessControlDto) {

        //校验是否传了 分页信息

        int page = accessControlDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            accessControlDto.setPage((page - 1) * accessControlDto.getRow());
        }

        List<AccessControlDto> accessControls = BeanConvertUtil.covertBeanList(accessControlV1ServiceDaoImpl.getAccessControlInfo(BeanConvertUtil.beanCovertMap(accessControlDto)), AccessControlDto.class);

        return accessControls;
    }


    @Override
    public int queryAccessControlsCount(@RequestBody AccessControlDto accessControlDto) {
        return accessControlV1ServiceDaoImpl.queryAccessControlsCount(BeanConvertUtil.beanCovertMap(accessControlDto));
    }

    @Override
    public int accessControlHeartbeat(@RequestBody MachineHeartbeatDto machineHeartbeatDto) {

        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineCode(machineHeartbeatDto.getMachineCode());
        List<AccessControlDto> accessControlDtos = queryAccessControls(accessControlDto);

        if (accessControlDtos == null || accessControlDtos.size() < 1) {
            return 0;
        }

        AccessControlPo accessControlPo = null;
        for (AccessControlDto tmpAccessControlDto : accessControlDtos) {
            try {
                accessControlPo = new AccessControlPo();
                accessControlPo.setMachineId(tmpAccessControlDto.getMachineId());
                accessControlPo.setHeartbeatTime(machineHeartbeatDto.getHeartbeatTime());
                updateAccessControl(accessControlPo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 1;
    }

    @Override
    public List<Map> queryAcInoutStatistics(@RequestBody  JSONObject reqJson) {
        return accessControlV1ServiceDaoImpl.queryAcInoutStatistics(reqJson);
    }

    @Override
    public List<Map> queryAcLogStatistics(@RequestBody JSONObject reqJson) {
        return accessControlV1ServiceDaoImpl.queryAcLogStatistics(reqJson);
    }

    @Override
    public List<Map> queryAcVisitStatistics(@RequestBody JSONObject reqJson) {
        return accessControlV1ServiceDaoImpl.queryAcVisitStatistics(reqJson);
    }

    @Override
    public List<Map> queryAcFaceStatistics(@RequestBody JSONObject reqJson) {
        return accessControlV1ServiceDaoImpl.queryAcFaceStatistics(reqJson);
    }

    @Override
    public int queryOwnerAccessControlsCount(@RequestBody AccessControlDto accessControlDto) {
        return accessControlV1ServiceDaoImpl.queryOwnerAccessControlsCount(BeanConvertUtil.beanCovertMap(accessControlDto));
    }

    @Override
    public List<AccessControlDto> queryOwnerAccessControls(@RequestBody AccessControlDto accessControlDto) {
        //校验是否传了 分页信息

        int page = accessControlDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            accessControlDto.setPage((page - 1) * accessControlDto.getRow());
        }

        List<AccessControlDto> accessControls = BeanConvertUtil.covertBeanList(accessControlV1ServiceDaoImpl.queryOwnerAccessControls(BeanConvertUtil.beanCovertMap(accessControlDto)), AccessControlDto.class);

        return accessControls;
    }

}
