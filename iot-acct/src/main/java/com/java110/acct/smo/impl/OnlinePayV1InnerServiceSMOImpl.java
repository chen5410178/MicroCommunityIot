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
package com.java110.acct.smo.impl;

import com.java110.bean.dto.PageDto;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.onlinePay.OnlinePayDto;
import com.java110.intf.acct.IOnlinePayV1InnerServiceSMO;
import com.java110.po.onlinePay.OnlinePayPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 类表述： 在线支付订单服务实现类
 * add by 吴学文 at 2022-11-21 12:08:05 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class OnlinePayV1InnerServiceSMOImpl  implements IOnlinePayV1InnerServiceSMO {

    @Autowired
    private com.java110.acct.dao.IOnlinePayV1ServiceDao onlinePayV1ServiceDaoImpl;

    @Override
    public int saveOnlinePay(@RequestBody OnlinePayPo onlinePayPo) {
        Map info = BeanConvertUtil.beanCovertMap(onlinePayPo);
        return onlinePayV1ServiceDaoImpl.saveOnlinePayInfo(info);
    }

    @Override
    public int updateOnlinePay(@RequestBody OnlinePayPo onlinePayPo) {
        Map info = BeanConvertUtil.beanCovertMap(onlinePayPo);
        return onlinePayV1ServiceDaoImpl.updateOnlinePayInfo(info);
    }

    @Override
    public int deleteOnlinePay(@RequestBody OnlinePayPo onlinePayPo) {
        onlinePayPo.setStatusCd("1");
        Map info = BeanConvertUtil.beanCovertMap(onlinePayPo);
        return onlinePayV1ServiceDaoImpl.updateOnlinePayInfo(info);
    }

    @Override
    public List<OnlinePayDto> queryOnlinePays(@RequestBody OnlinePayDto onlinePayDto) {
        //校验是否传了 分页信息
        int page = onlinePayDto.getPage();
        if (page != PageDto.DEFAULT_PAGE) {
            onlinePayDto.setPage((page - 1) * onlinePayDto.getRow());
        }
        Map info = BeanConvertUtil.beanCovertMap(onlinePayDto);
        List<Map> businessOnlinePayInfos = onlinePayV1ServiceDaoImpl.getOnlinePayInfo(info);

        List<OnlinePayDto> onlinePayDtos = null;
        if (businessOnlinePayInfos != null && businessOnlinePayInfos.size() > 0) {
            onlinePayDtos = new ArrayList<>();
            for (int businessOnlinePayInfoIndex = 0; businessOnlinePayInfoIndex < businessOnlinePayInfos.size(); businessOnlinePayInfoIndex++) {
                Map businessOnlinePayInfo = businessOnlinePayInfos.get(businessOnlinePayInfoIndex);
                onlinePayDto = BeanConvertUtil.covertBean(businessOnlinePayInfo, OnlinePayDto.class);
                onlinePayDtos.add(onlinePayDto);
            }
        }
        return onlinePayDtos;
    }

    @Override
    public int queryOnlinePaysCount(@RequestBody OnlinePayDto onlinePayDto) {
        Map info = BeanConvertUtil.beanCovertMap(onlinePayDto);
        return onlinePayV1ServiceDaoImpl.queryOnlinePaysCount(info);
    }
}