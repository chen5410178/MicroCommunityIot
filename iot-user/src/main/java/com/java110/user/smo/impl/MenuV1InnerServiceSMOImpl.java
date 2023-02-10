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
import com.java110.bean.dto.basePrivilege.BasePrivilegeDto;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.user.dao.IMenuV1ServiceDao;
import com.java110.intf.user.IMenuV1InnerServiceSMO;
import com.java110.dto.menu.MenuDto;
import com.java110.po.menu.MenuPo;
import com.java110.dto.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2023-02-11 04:05:44 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class MenuV1InnerServiceSMOImpl  implements IMenuV1InnerServiceSMO {

    @Autowired
    private IMenuV1ServiceDao menuV1ServiceDaoImpl;


    @Override
    public int saveMenu(@RequestBody  MenuPo menuPo) {
        int saveFlag = menuV1ServiceDaoImpl.saveMenuInfo(BeanConvertUtil.beanCovertMap(menuPo));
        return saveFlag;
    }

     @Override
    public int updateMenu(@RequestBody  MenuPo menuPo) {
        int saveFlag = menuV1ServiceDaoImpl.updateMenuInfo(BeanConvertUtil.beanCovertMap(menuPo));
        return saveFlag;
    }

     @Override
    public int deleteMenu(@RequestBody  MenuPo menuPo) {
       menuPo.setStatusCd("1");
       int saveFlag = menuV1ServiceDaoImpl.updateMenuInfo(BeanConvertUtil.beanCovertMap(menuPo));
       return saveFlag;
    }

    @Override
    public List<MenuDto> queryMenus(@RequestBody  MenuDto menuDto) {

        //校验是否传了 分页信息

        int page = menuDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            menuDto.setPage((page - 1) * menuDto.getRow());
        }

        List<MenuDto> menus = BeanConvertUtil.covertBeanList(menuV1ServiceDaoImpl.getMenuInfo(BeanConvertUtil.beanCovertMap(menuDto)), MenuDto.class);

        return menus;
    }


    @Override
    public int queryMenusCount(@RequestBody MenuDto menuDto) {
        return menuV1ServiceDaoImpl.queryMenusCount(BeanConvertUtil.beanCovertMap(menuDto));    }


    /**
     * 查询<p>小区楼</p>总记录数
     *
     * @param basePrivilegeDto 数据对象分享
     * @return 小区下的小区楼记录数
     */
    @Override
    @RequestMapping(value = "/checkUserHasResource", method = RequestMethod.POST)
    public List<Map> checkUserHasResource(@RequestBody BasePrivilegeDto basePrivilegeDto) {
        return menuV1ServiceDaoImpl.checkUserHasResource(BeanConvertUtil.beanCovertMap(basePrivilegeDto));
    }

}
