package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.car.OwnerCarDto;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.carMonthCard.CarMonthCardDto;
import com.java110.dto.carMonthOrder.CarMonthOrderDto;
import com.java110.intf.car.ICarMonthCardV1InnerServiceSMO;
import com.java110.intf.car.ICarMonthOrderV1InnerServiceSMO;
import com.java110.intf.car.IOwnerCarV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import com.java110.po.carMonthOrder.CarMonthOrderPo;
import com.java110.po.ownerCar.OwnerCarPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("buyCarMonthOrderBmoImpl")
public class BuyCarMonthOrderBmoImpl implements IIotCommonApiBmo {


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
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "cardId", "请求报文中未包含cardId");
        Assert.hasKeyAndValue(reqJson, "carNum", "请求报文中未包含carNum");
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId");
        Assert.hasKeyAndValue(reqJson, "primeRate", "请求报文中未包含primeRate");
        Assert.hasKeyAndValue(reqJson, "receivedAmount", "请求报文中未包含receivedAmount");
        Assert.hasKeyAndValue(reqJson, "endTime", "请求报文中未包含endTime");
        Assert.hasKeyAndValue(reqJson, "cashierId", "请求报文中未包含cashierId");
        Assert.hasKeyAndValue(reqJson, "cashierName", "请求报文中未包含cashierName");

        OwnerCarDto ownerCarDto = new OwnerCarDto();
        ownerCarDto.setCarNum(reqJson.getString("carNum"));
        ownerCarDto.setCommunityId(reqJson.getString("communityId"));
        List<OwnerCarDto> ownerCarDtos = ownerCarV1InnerServiceSMOImpl.queryOwnerCars(ownerCarDto);

        if (ListUtil.isNull(ownerCarDtos)) {
            throw new CmdException("月租车不存在");
        }

        CarMonthCardDto carMonthCardDto = new CarMonthCardDto();
        carMonthCardDto.setCardId(reqJson.getString("cardId"));
        carMonthCardDto.setCommunityId(reqJson.getString("communityId"));
        carMonthCardDto.setPaId(ownerCarDtos.get(0).getPaId());
        List<CarMonthCardDto> carMonthCardDtos = carMonthCardV1InnerServiceSMOImpl.queryCarMonthCards(carMonthCardDto);
        if (ListUtil.isNull(carMonthCardDtos)) {
            throw new CmdException("月卡不存在");
        }
        reqJson.put("receivableAmount", carMonthCardDtos.get(0).getCardPrice());
        reqJson.put("cardMonth", carMonthCardDtos.get(0).getCardMonth());
        reqJson.put("carMemberId", ownerCarDtos.get(0).getMemberId());
        reqJson.put("startTime", ownerCarDtos.get(0).getEndTime());
        reqJson.put("psId", ownerCarDtos.get(0).getPsId());
        reqJson.put("carId", ownerCarDtos.get(0).getCarId());
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {

        Date startTime = reqJson.getDate("startTime");

        if (startTime.getTime() < DateUtil.getCurrentDate().getTime()) {
            startTime = DateUtil.getCurrentDate();
        }

        String endTime = reqJson.getString("endTime");

        CarMonthOrderPo carMonthOrderPo = BeanConvertUtil.covertBean(reqJson, CarMonthOrderPo.class);
        carMonthOrderPo.setOrderId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));
        carMonthOrderPo.setCashierId(reqJson.getString("cashierId"));
        carMonthOrderPo.setCashierName(reqJson.getString("cashierName"));
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

        context.setResponseEntity(ResultVo.success());

    }
}
