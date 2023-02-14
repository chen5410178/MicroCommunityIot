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
package com.java110.user.smo.impl;


import com.java110.bean.dto.PageDto;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.user.dao.IPrivilegeV1ServiceDao;
import com.java110.intf.user.IPrivilegeV1InnerServiceSMO;
import com.java110.dto.privilege.PrivilegeDto;
import com.java110.po.privilege.PrivilegePo;
import com.java110.dto.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2023-02-11 02:13:20 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class PrivilegeV1InnerServiceSMOImpl  implements IPrivilegeV1InnerServiceSMO {

    @Autowired
    private IPrivilegeV1ServiceDao privilegeV1ServiceDaoImpl;


    @Override
    public int savePrivilege(@RequestBody  PrivilegePo privilegePo) {
        int saveFlag = privilegeV1ServiceDaoImpl.savePrivilegeInfo(BeanConvertUtil.beanCovertMap(privilegePo));
        return saveFlag;
    }

     @Override
    public int updatePrivilege(@RequestBody  PrivilegePo privilegePo) {
        int saveFlag = privilegeV1ServiceDaoImpl.updatePrivilegeInfo(BeanConvertUtil.beanCovertMap(privilegePo));
        return saveFlag;
    }

     @Override
    public int deletePrivilege(@RequestBody  PrivilegePo privilegePo) {
       privilegePo.setStatusCd("1");
       int saveFlag = privilegeV1ServiceDaoImpl.updatePrivilegeInfo(BeanConvertUtil.beanCovertMap(privilegePo));
       return saveFlag;
    }

    @Override
    public List<PrivilegeDto> queryPrivileges(@RequestBody  PrivilegeDto privilegeDto) {

        //校验是否传了 分页信息

        int page = privilegeDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            privilegeDto.setPage((page - 1) * privilegeDto.getRow());
        }

        List<PrivilegeDto> privileges = BeanConvertUtil.covertBeanList(privilegeV1ServiceDaoImpl.getPrivilegeInfo(BeanConvertUtil.beanCovertMap(privilegeDto)), PrivilegeDto.class);

        return privileges;
    }


    @Override
    public int queryPrivilegesCount(@RequestBody PrivilegeDto privilegeDto) {
        return privilegeV1ServiceDaoImpl.queryPrivilegesCount(BeanConvertUtil.beanCovertMap(privilegeDto));    }

    @Override
    public List<PrivilegeDto> queryPrivilegesNoInPrivilegeGroup(@RequestBody PrivilegeDto privilegeDto) {
        //校验是否传了 分页信息

        int page = privilegeDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            privilegeDto.setPage((page - 1) * privilegeDto.getRow());
        }

        List<PrivilegeDto> privileges = BeanConvertUtil.covertBeanList(privilegeV1ServiceDaoImpl.queryPrivilegesNoInPrivilegeGroup(BeanConvertUtil.beanCovertMap(privilegeDto)), PrivilegeDto.class);

        return privileges;    }

}
