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
package com.java110.car.cmd.carMonth;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.car.OwnerCarDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.carMonthOrder.CarMonthOrderDto;
import com.java110.intf.car.ICarMonthOrderV1InnerServiceSMO;
import com.java110.intf.car.IOwnerCarV1InnerServiceSMO;
import com.java110.po.carMonthOrder.CarMonthOrderPo;
import com.java110.po.ownerCar.OwnerCarPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * 类表述：更新
 * 服务编码：carMonthOrder.updateCarMonthOrder
 * 请求路劲：/app/carMonthOrder.UpdateCarMonthOrder
 * add by 吴学文 at 2023-12-14 12:05:19 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "carMonth.refundCarMonthOrder")
public class RefundCarMonthOrderCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(RefundCarMonthOrderCmd.class);


    @Autowired
    private ICarMonthOrderV1InnerServiceSMO carMonthOrderV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerCarV1InnerServiceSMO ownerCarV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "orderId", "orderId不能为空");
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");


        CarMonthOrderDto carMonthOrderDto = new CarMonthOrderDto();
        carMonthOrderDto.setOrderId(reqJson.getString("orderId"));
        carMonthOrderDto.setCommunityId(reqJson.getString("communityId"));
        carMonthOrderDto.setState(CarMonthOrderDto.STATE_NORMAL);
        List<CarMonthOrderDto> carMonthOrderDtos = carMonthOrderV1InnerServiceSMOImpl.queryCarMonthOrders(carMonthOrderDto);

        if(ListUtil.isNull(carMonthOrderDtos)){
            throw new CmdException("不存在退费订单");
        }

        OwnerCarDto ownerCarDto = new OwnerCarDto();
        ownerCarDto.setCarNum(carMonthOrderDtos.get(0).getCarNum());
        ownerCarDto.setCarId(carMonthOrderDtos.get(0).getCarId());
        ownerCarDto.setCommunityId(carMonthOrderDtos.get(0).getCommunityId());
        List<OwnerCarDto> ownerCarDtos = ownerCarV1InnerServiceSMOImpl.queryOwnerCars(ownerCarDto);
        if(ListUtil.isNull(ownerCarDtos)){
            throw new CmdException("车辆不存在");
        }

        String endTime = DateUtil.getFormatTimeStringB(DateUtil.getDateFromStringA(carMonthOrderDtos.get(0).getEndTime()));

        if(!endTime.equals(DateUtil.getFormatTimeStringB(ownerCarDtos.get(0).getEndTime()))){
            throw new CmdException("车辆结束时间和订单结束时间不一致，不能退费");
        }

        reqJson.put("orderStartTime",carMonthOrderDtos.get(0).getStartTime());
        reqJson.put("carMemberId",ownerCarDtos.get(0).getMemberId());

    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        CarMonthOrderPo carMonthOrderPo = BeanConvertUtil.covertBean(reqJson, CarMonthOrderPo.class);
        carMonthOrderPo.setState(CarMonthOrderDto.STATE_REFUND);
        int flag = carMonthOrderV1InnerServiceSMOImpl.updateCarMonthOrder(carMonthOrderPo);

        if (flag < 1) {
            throw new CmdException("更新数据失败");
        }

        OwnerCarPo ownerCarPo = new OwnerCarPo();
        ownerCarPo.setMemberId(reqJson.getString("carMemberId"));
        ownerCarPo.setEndTime(reqJson.getString("orderStartTime"));
        ownerCarV1InnerServiceSMOImpl.updateOwnerCar(ownerCarPo);

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
