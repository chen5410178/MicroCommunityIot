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


import com.java110.accessControl.dao.IAccessControlFaceV1ServiceDao;
import com.java110.accessControl.manufactor.IAccessControlManufactor;
import com.java110.bean.dto.PageDto;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.*;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.hardwareManufacturer.HardwareManufacturerDto;
import com.java110.intf.accessControl.IAccessControlFaceV1InnerServiceSMO;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.system.IHardwareManufacturerV1InnerServiceSMO;
import com.java110.po.accessControlFace.AccessControlFacePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2023-08-15 11:48:56 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class AccessControlFaceV1InnerServiceSMOImpl implements IAccessControlFaceV1InnerServiceSMO {

    @Autowired
    private IAccessControlFaceV1ServiceDao accessControlFaceV1ServiceDaoImpl;

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Autowired
    private IHardwareManufacturerV1InnerServiceSMO hardwareManufacturerV1InnerServiceSMOImpl;


    @Override
    public int saveAccessControlFace(@RequestBody AccessControlFacePo accessControlFacePo) {



        int saveFlag = accessControlFaceV1ServiceDaoImpl.saveAccessControlFaceInfo(BeanConvertUtil.beanCovertMap(accessControlFacePo));

        if(!StringUtil.isEmpty(accessControlFacePo.getFacePath())) {
            accessControlFacePo.setFaceBase64(ImageUtils.getBase64ByImgUrl(accessControlFacePo.getFacePath()));
        }

        //todo 通知适配器 添加人脸

        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineId(accessControlFacePo.getMachineId());
        accessControlDto.setCommunityId(accessControlFacePo.getCommunityId());
        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);

        Assert.listOnlyOne(accessControlDtos, "门禁不存在");
        HardwareManufacturerDto hardwareManufacturerDto = new HardwareManufacturerDto();
        hardwareManufacturerDto.setHmId(accessControlDtos.get(0).getImplBean());
        List<HardwareManufacturerDto> hardwareManufacturerDtos = hardwareManufacturerV1InnerServiceSMOImpl.queryHardwareManufacturers(hardwareManufacturerDto);

        Assert.listOnlyOne(hardwareManufacturerDtos, "协议不存在");

        IAccessControlManufactor accessControlManufactor = ApplicationContextFactory.getBean(hardwareManufacturerDtos.get(0).getProtocolImpl(), IAccessControlManufactor.class);
        boolean saveUser = accessControlManufactor.addUser(accessControlDtos.get(0), accessControlFacePo);

        if (!saveUser) {
            throw new IllegalArgumentException("下发设备失败，失败原因厂家未返回");
        }
        return saveFlag;

    }

    @Override
    public int updateAccessControlFace(@RequestBody AccessControlFacePo accessControlFacePo) {

        int saveFlag = accessControlFaceV1ServiceDaoImpl.updateAccessControlFaceInfo(BeanConvertUtil.beanCovertMap(accessControlFacePo));

        //todo 通知适配器 添加人脸
        if(AccessControlFaceDto.STATE_WAIT.equals(accessControlFacePo.getState())) {

            if(!StringUtil.isEmpty(accessControlFacePo.getFacePath())) {
                accessControlFacePo.setFaceBase64(ImageUtils.getBase64ByImgUrl(accessControlFacePo.getFacePath()));
            }

            AccessControlDto accessControlDto = new AccessControlDto();
            accessControlDto.setMachineId(accessControlFacePo.getMachineId());
            accessControlDto.setCommunityId(accessControlFacePo.getCommunityId());
            List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);

            Assert.listOnlyOne(accessControlDtos, "门禁不存在");
            HardwareManufacturerDto hardwareManufacturerDto = new HardwareManufacturerDto();
            hardwareManufacturerDto.setHmId(accessControlDtos.get(0).getImplBean());
            List<HardwareManufacturerDto> hardwareManufacturerDtos = hardwareManufacturerV1InnerServiceSMOImpl.queryHardwareManufacturers(hardwareManufacturerDto);

            Assert.listOnlyOne(hardwareManufacturerDtos, "协议不存在");

            IAccessControlManufactor accessControlManufactor = ApplicationContextFactory.getBean(hardwareManufacturerDtos.get(0).getProtocolImpl(), IAccessControlManufactor.class);
            boolean saveUser = accessControlManufactor.updateUser(accessControlDtos.get(0), accessControlFacePo);

            if (!saveUser) {
                throw new IllegalArgumentException("下发设备失败，失败原因厂家未返回");
            }
        }

        return saveFlag;
    }

    @Override
    public int deleteAccessControlFace(@RequestBody AccessControlFacePo accessControlFacePo) {
        //todo 通知适配器 添加人脸

        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineId(accessControlFacePo.getMachineId());
        accessControlDto.setCommunityId(accessControlFacePo.getCommunityId());
        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);

        if(!ListUtil.isNull(accessControlDtos)) {
            HardwareManufacturerDto hardwareManufacturerDto = new HardwareManufacturerDto();
            hardwareManufacturerDto.setHmId(accessControlDtos.get(0).getImplBean());
            List<HardwareManufacturerDto> hardwareManufacturerDtos = hardwareManufacturerV1InnerServiceSMOImpl.queryHardwareManufacturers(hardwareManufacturerDto);

            Assert.listOnlyOne(hardwareManufacturerDtos, "协议不存在");

            IAccessControlManufactor accessControlManufactor = ApplicationContextFactory.getBean(hardwareManufacturerDtos.get(0).getProtocolImpl(), IAccessControlManufactor.class);
            boolean saveUser = accessControlManufactor.deleteUser(accessControlDtos.get(0), accessControlFacePo);

            if (!saveUser) {
                throw new IllegalArgumentException("下发设备失败，失败原因厂家未返回");
            }
        }
        accessControlFacePo.setStatusCd("1");
        int saveFlag = accessControlFaceV1ServiceDaoImpl.updateAccessControlFaceInfo(BeanConvertUtil.beanCovertMap(accessControlFacePo));
        return saveFlag;
    }

    @Override
    public List<AccessControlFaceDto> queryAccessControlFaces(@RequestBody AccessControlFaceDto accessControlFaceDto) {

        //校验是否传了 分页信息

        int page = accessControlFaceDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            accessControlFaceDto.setPage((page - 1) * accessControlFaceDto.getRow());
        }

        List<AccessControlFaceDto> accessControlFaces = BeanConvertUtil.covertBeanList(accessControlFaceV1ServiceDaoImpl.getAccessControlFaceInfo(BeanConvertUtil.beanCovertMap(accessControlFaceDto)), AccessControlFaceDto.class);

        return accessControlFaces;
    }


    @Override
    public int queryAccessControlFacesCount(@RequestBody AccessControlFaceDto accessControlFaceDto) {
        return accessControlFaceV1ServiceDaoImpl.queryAccessControlFacesCount(BeanConvertUtil.beanCovertMap(accessControlFaceDto));
    }

    @Override
    public List<AccessControlFaceDto> queryAccessControlFacesByComeId(@RequestBody AccessControlFaceDto accessControlFaceDto) {
        List<AccessControlFaceDto> accessControlFaces = BeanConvertUtil.covertBeanList(accessControlFaceV1ServiceDaoImpl.queryAccessControlFacesByComeId(BeanConvertUtil.beanCovertMap(accessControlFaceDto)), AccessControlFaceDto.class);

        return accessControlFaces;
    }

    @Override
    public List<AccessControlFaceDto> queryYiteAccessControlFaces(@RequestBody AccessControlFaceDto accessControlFaceDto) {
        List<AccessControlFaceDto> accessControlFaces = BeanConvertUtil.covertBeanList(accessControlFaceV1ServiceDaoImpl.queryYiteAccessControlFaces(BeanConvertUtil.beanCovertMap(accessControlFaceDto)), AccessControlFaceDto.class);

        return accessControlFaces;
    }

}
