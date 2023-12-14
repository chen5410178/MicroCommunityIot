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
package com.java110.car.smo.impl;


import com.java110.bean.dto.PageDto;
import com.java110.car.dao.ICarMonthCardV1ServiceDao;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.intf.car.ICarMonthCardV1InnerServiceSMO;
import com.java110.dto.carMonthCard.CarMonthCardDto;
import com.java110.po.carMonthCard.CarMonthCardPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2023-12-14 11:54:28 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class CarMonthCardV1InnerServiceSMOImpl  implements ICarMonthCardV1InnerServiceSMO {

    @Autowired
    private ICarMonthCardV1ServiceDao carMonthCardV1ServiceDaoImpl;


    @Override
    public int saveCarMonthCard(@RequestBody  CarMonthCardPo carMonthCardPo) {
        int saveFlag = carMonthCardV1ServiceDaoImpl.saveCarMonthCardInfo(BeanConvertUtil.beanCovertMap(carMonthCardPo));
        return saveFlag;
    }

     @Override
    public int updateCarMonthCard(@RequestBody  CarMonthCardPo carMonthCardPo) {
        int saveFlag = carMonthCardV1ServiceDaoImpl.updateCarMonthCardInfo(BeanConvertUtil.beanCovertMap(carMonthCardPo));
        return saveFlag;
    }

     @Override
    public int deleteCarMonthCard(@RequestBody  CarMonthCardPo carMonthCardPo) {
       carMonthCardPo.setStatusCd("1");
       int saveFlag = carMonthCardV1ServiceDaoImpl.updateCarMonthCardInfo(BeanConvertUtil.beanCovertMap(carMonthCardPo));
       return saveFlag;
    }

    @Override
    public List<CarMonthCardDto> queryCarMonthCards(@RequestBody  CarMonthCardDto carMonthCardDto) {

        //校验是否传了 分页信息

        int page = carMonthCardDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            carMonthCardDto.setPage((page - 1) * carMonthCardDto.getRow());
        }

        List<CarMonthCardDto> carMonthCards = BeanConvertUtil.covertBeanList(carMonthCardV1ServiceDaoImpl.getCarMonthCardInfo(BeanConvertUtil.beanCovertMap(carMonthCardDto)), CarMonthCardDto.class);

        return carMonthCards;
    }


    @Override
    public int queryCarMonthCardsCount(@RequestBody CarMonthCardDto carMonthCardDto) {
        return carMonthCardV1ServiceDaoImpl.queryCarMonthCardsCount(BeanConvertUtil.beanCovertMap(carMonthCardDto));    }

}
