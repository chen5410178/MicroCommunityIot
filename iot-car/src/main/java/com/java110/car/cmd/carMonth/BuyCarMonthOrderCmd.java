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
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.*;
import com.java110.dto.carMonthCard.CarMonthCardDto;
import com.java110.dto.carMonthOrder.CarMonthOrderDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.car.ICarMonthCardV1InnerServiceSMO;
import com.java110.intf.car.ICarMonthOrderV1InnerServiceSMO;
import com.java110.intf.car.IOwnerCarV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.carMonthOrder.CarMonthOrderPo;
import com.java110.po.ownerCar.OwnerCarPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * 类表述：保存
 * 服务编码：carMonthOrder.saveCarMonthOrder
 * 请求路劲：/app/carMonthOrder.SaveCarMonthOrder
 * add by 吴学文 at 2023-12-14 12:05:19 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "carMonth.buyCarMonthOrder")
public class BuyCarMonthOrderCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(BuyCarMonthOrderCmd.class);

    public static final String CODE_PREFIX_ID = "10";

    @Autowired
    private ICarMonthOrderV1InnerServiceSMO carMonthOrderV1InnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerCarV1InnerServiceSMO ownerCarV1InnerServiceSMOImpl;

    @Autowired
    private ICarMonthCardV1InnerServiceSMO carMonthCardV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "cardId", "请求报文中未包含cardId");
        Assert.hasKeyAndValue(reqJson, "carNum", "请求报文中未包含carNum");
        Assert.hasKeyAndValue(reqJson, "carId", "请求报文中未包含carId");
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId");
        Assert.hasKeyAndValue(reqJson, "primeRate", "请求报文中未包含primeRate");
        Assert.hasKeyAndValue(reqJson, "receivedAmount", "请求报文中未包含receivedAmount");

        OwnerCarDto ownerCarDto = new OwnerCarDto();
        ownerCarDto.setCarId(reqJson.getString("carId"));
        ownerCarDto.setCarNum(reqJson.getString("carNum"));
        ownerCarDto.setCommunityId(reqJson.getString("communityId"));
        List<OwnerCarDto> ownerCarDtos = ownerCarV1InnerServiceSMOImpl.queryOwnerCars(ownerCarDto);

        if (ListUtil.isNull(ownerCarDtos)) {
            throw new CmdException("月租车不存在");
        }

        CarMonthCardDto carMonthCardDto = new CarMonthCardDto();
        carMonthCardDto.setCardId(reqJson.getString("cardId"));
        carMonthCardDto.setCommunityId(reqJson.getString("communityId"));
        // 前端月卡列表以车牌查询返回的paId过滤（ownerCar.queryOwnerCars 会用车位所属停车场刷新paId），
        // 此处优先使用请求中的paId，避免 owner_car 原始pa_id 与车位所属停车场不一致时误报"月卡不存在"
        String paId = reqJson.getString("paId");
        if (StringUtil.isEmpty(paId)) {
            paId = ownerCarDtos.get(0).getPaId();
        }
        carMonthCardDto.setPaId(paId);
        List<CarMonthCardDto> carMonthCardDtos = carMonthCardV1InnerServiceSMOImpl.queryCarMonthCards(carMonthCardDto);
        if (ListUtil.isNull(carMonthCardDtos)) {
            throw new CmdException("月卡不存在");
        }
        reqJson.put("receivableAmount", carMonthCardDtos.get(0).getCardPrice());
        reqJson.put("cardMonth",carMonthCardDtos.get(0).getCardMonth());
        reqJson.put("carMemberId",ownerCarDtos.get(0).getMemberId());
        reqJson.put("endTime",ownerCarDtos.get(0).getEndTime());
        reqJson.put("psId",ownerCarDtos.get(0).getPsId());

    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        String userId = reqJson.getString("userId");
        if (StringUtil.isEmpty(userId)) {
            userId = CmdContextUtils.getUserId(cmdDataFlowContext);
        }

        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        userDto.setPage(1);
        userDto.setRow(1);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);

        if (ListUtil.isNull(userDtos)) {
            throw new CmdException("用户不存在");
        }

        Date startTime = reqJson.getDate("endTime");

        if(startTime.getTime()< DateUtil.getCurrentDate().getTime()){
            startTime = DateUtil.getCurrentDate();
        }

        String endTime = DateUtil.getAddMonthStringA(startTime,reqJson.getIntValue("cardMonth"));

        CarMonthOrderPo carMonthOrderPo = BeanConvertUtil.covertBean(reqJson, CarMonthOrderPo.class);
        carMonthOrderPo.setOrderId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));
        carMonthOrderPo.setCashierId(userDtos.get(0).getUserId());
        carMonthOrderPo.setCashierName(userDtos.get(0).getName());
        carMonthOrderPo.setStartTime(DateUtil.getFormatTimeStringA(startTime));
        carMonthOrderPo.setEndTime(endTime);
        carMonthOrderPo.setState(CarMonthOrderDto.STATE_NORMAL);
        int flag = carMonthOrderV1InnerServiceSMOImpl.saveCarMonthOrder(carMonthOrderPo);

        if (flag < 1) {
            throw new CmdException("保存数据失败");
        }


        OwnerCarPo ownerCarPo = new OwnerCarPo();
        ownerCarPo.setMemberId(reqJson.getString("carMemberId"));
        ownerCarPo.setEndTime(endTime);
        ownerCarV1InnerServiceSMOImpl.updateOwnerCar(ownerCarPo);

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
