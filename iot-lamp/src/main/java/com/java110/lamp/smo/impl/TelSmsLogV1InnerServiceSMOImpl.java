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
package com.java110.lamp.smo.impl;


import com.java110.bean.dto.PageDto;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.lamp.dao.ITelSmsLogV1ServiceDao;
import com.java110.intf.lamp.ITelSmsLogV1InnerServiceSMO;
import com.java110.dto.tel.TelSmsLogDto;
import com.java110.po.tel.TelSmsLogPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2024-05-31 21:19:29 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class TelSmsLogV1InnerServiceSMOImpl implements ITelSmsLogV1InnerServiceSMO {

    @Autowired
    private ITelSmsLogV1ServiceDao telSmsLogV1ServiceDaoImpl;


    @Override
    public int saveTelSmsLog(@RequestBody  TelSmsLogPo telSmsLogPo) {
        int saveFlag = telSmsLogV1ServiceDaoImpl.saveTelSmsLogInfo(BeanConvertUtil.beanCovertMap(telSmsLogPo));
        return saveFlag;
    }

     @Override
    public int updateTelSmsLog(@RequestBody  TelSmsLogPo telSmsLogPo) {
        int saveFlag = telSmsLogV1ServiceDaoImpl.updateTelSmsLogInfo(BeanConvertUtil.beanCovertMap(telSmsLogPo));
        return saveFlag;
    }

     @Override
    public int deleteTelSmsLog(@RequestBody  TelSmsLogPo telSmsLogPo) {
       telSmsLogPo.setStatusCd("1");
       int saveFlag = telSmsLogV1ServiceDaoImpl.updateTelSmsLogInfo(BeanConvertUtil.beanCovertMap(telSmsLogPo));
       return saveFlag;
    }

    @Override
    public List<TelSmsLogDto> queryTelSmsLogs(@RequestBody  TelSmsLogDto telSmsLogDto) {

        //校验是否传了 分页信息

        int page = telSmsLogDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            telSmsLogDto.setPage((page - 1) * telSmsLogDto.getRow());
        }

        List<TelSmsLogDto> telSmsLogs = BeanConvertUtil.covertBeanList(telSmsLogV1ServiceDaoImpl.getTelSmsLogInfo(BeanConvertUtil.beanCovertMap(telSmsLogDto)), TelSmsLogDto.class);

        return telSmsLogs;
    }


    @Override
    public int queryTelSmsLogsCount(@RequestBody TelSmsLogDto telSmsLogDto) {
        return telSmsLogV1ServiceDaoImpl.queryTelSmsLogsCount(BeanConvertUtil.beanCovertMap(telSmsLogDto));    }

}
