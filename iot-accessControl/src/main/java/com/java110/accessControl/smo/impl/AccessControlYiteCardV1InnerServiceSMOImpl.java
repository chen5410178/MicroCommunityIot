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


import com.java110.accessControl.dao.IAccessControlYiteCardV1ServiceDao;
import com.java110.bean.dto.PageDto;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.intf.accessControl.IAccessControlYiteCardV1InnerServiceSMO;
import com.java110.dto.accessControlYiteCard.AccessControlYiteCardDto;
import com.java110.po.accessControlYiteCard.AccessControlYiteCardPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2024-09-02 14:29:57 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class AccessControlYiteCardV1InnerServiceSMOImpl  implements IAccessControlYiteCardV1InnerServiceSMO {

    @Autowired
    private IAccessControlYiteCardV1ServiceDao accessControlYiteCardV1ServiceDaoImpl;


    @Override
    public int saveAccessControlYiteCard(@RequestBody  AccessControlYiteCardPo accessControlYiteCardPo) {
        int saveFlag = accessControlYiteCardV1ServiceDaoImpl.saveAccessControlYiteCardInfo(BeanConvertUtil.beanCovertMap(accessControlYiteCardPo));
        return saveFlag;
    }

     @Override
    public int updateAccessControlYiteCard(@RequestBody  AccessControlYiteCardPo accessControlYiteCardPo) {
        int saveFlag = accessControlYiteCardV1ServiceDaoImpl.updateAccessControlYiteCardInfo(BeanConvertUtil.beanCovertMap(accessControlYiteCardPo));
        return saveFlag;
    }

     @Override
    public int deleteAccessControlYiteCard(@RequestBody  AccessControlYiteCardPo accessControlYiteCardPo) {
       accessControlYiteCardPo.setStatusCd("1");
       int saveFlag = accessControlYiteCardV1ServiceDaoImpl.updateAccessControlYiteCardInfo(BeanConvertUtil.beanCovertMap(accessControlYiteCardPo));
       return saveFlag;
    }

    @Override
    public List<AccessControlYiteCardDto> queryAccessControlYiteCards(@RequestBody  AccessControlYiteCardDto accessControlYiteCardDto) {

        //校验是否传了 分页信息

        int page = accessControlYiteCardDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            accessControlYiteCardDto.setPage((page - 1) * accessControlYiteCardDto.getRow());
        }

        List<AccessControlYiteCardDto> infos = BeanConvertUtil.covertBeanList(accessControlYiteCardV1ServiceDaoImpl.getAccessControlYiteCardInfo(BeanConvertUtil.beanCovertMap(accessControlYiteCardDto)), AccessControlYiteCardDto.class);

        return infos;
    }


    @Override
    public int queryAccessControlYiteCardsCount(@RequestBody AccessControlYiteCardDto accessControlYiteCardDto) {
        return accessControlYiteCardV1ServiceDaoImpl.queryAccessControlYiteCardsCount(BeanConvertUtil.beanCovertMap(accessControlYiteCardDto));    }

}
