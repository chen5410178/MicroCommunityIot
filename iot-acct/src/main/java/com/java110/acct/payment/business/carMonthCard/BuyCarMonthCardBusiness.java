package com.java110.acct.payment.business.carMonthCard;

import com.alibaba.fastjson.JSONObject;
import com.java110.acct.payment.IPaymentBusiness;
import com.java110.bean.dto.car.OwnerCarDto;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.*;
import com.java110.dto.carMonthCard.CarMonthCardDto;
import com.java110.dto.carMonthOrder.CarMonthOrderDto;
import com.java110.dto.chargeMonthCard.ChargeMonthCardDto;
import com.java110.dto.chargeMonthOrder.ChargeMonthOrderDto;
import com.java110.dto.payment.PaymentOrderDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.car.ICarMonthCardV1InnerServiceSMO;
import com.java110.intf.car.ICarMonthOrderV1InnerServiceSMO;
import com.java110.intf.car.IOwnerCarV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.carMonthOrder.CarMonthOrderPo;
import com.java110.po.ownerCar.OwnerCarPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("buyCarMonthCard")
public class BuyCarMonthCardBusiness implements IPaymentBusiness {

    public static final String CODE_PREFIX_ID = "10";
    @Autowired
    private ICarMonthCardV1InnerServiceSMO carMonthCardV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerCarV1InnerServiceSMO ownerCarV1InnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private ICarMonthOrderV1InnerServiceSMO carMonthOrderV1InnerServiceSMOImpl;

    @Override
    public PaymentOrderDto unified(ICmdDataFlowContext context, JSONObject reqJson) {

        Assert.hasKeyAndValue(reqJson, "cardId", "请求报文中未包含cardId");
        Assert.hasKeyAndValue(reqJson, "carNum", "请求报文中未包含carNum");
        Assert.hasKeyAndValue(reqJson, "carId", "请求报文中未包含carId");
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId");
        Assert.hasKeyAndValue(reqJson, "receivedAmount", "请求报文中未包含receivedAmount");
        Assert.hasKeyAndValue(reqJson, "cashierUserId", "请求报文中未包含createUserId");


        UserDto userDto = new UserDto();
        userDto.setUserId(reqJson.getString("cashierUserId"));
        userDto.setPage(1);
        userDto.setRow(1);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);

        if (ListUtil.isNull(userDtos)) {
            throw new CmdException("用户不存在");
        }

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
        carMonthCardDto.setPaId(ownerCarDtos.get(0).getPaId());
        List<CarMonthCardDto> carMonthCardDtos = carMonthCardV1InnerServiceSMOImpl.queryCarMonthCards(carMonthCardDto);
        if (ListUtil.isNull(carMonthCardDtos)) {
            throw new CmdException("月卡不存在");
        }

        PaymentOrderDto paymentOrderDto = new PaymentOrderDto();
        paymentOrderDto.setOrderId(GenerateCodeFactory.getOId());
        paymentOrderDto.setMoney(Double.parseDouble(carMonthCardDtos.get(0).getCardPrice()));
        paymentOrderDto.setName(carMonthCardDtos.get(0).getCardName());
        reqJson.put("receivableAmount", paymentOrderDto.getMoney());
        reqJson.put("receivedAmount", paymentOrderDto.getMoney());
        reqJson.put("cardMonth",carMonthCardDtos.get(0).getCardMonth());
        reqJson.put("carMemberId",ownerCarDtos.get(0).getMemberId());
        reqJson.put("endTime",ownerCarDtos.get(0).getEndTime());
        reqJson.put("paId",ownerCarDtos.get(0).getPaId());

        return paymentOrderDto;

    }

    @Override
    public void notifyPayment(PaymentOrderDto paymentOrderDto, JSONObject reqJson) {

        String userId = reqJson.getString("cashierUserId");


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
        carMonthOrderPo.setPrimeRate("5");
        int flag = carMonthOrderV1InnerServiceSMOImpl.saveCarMonthOrder(carMonthOrderPo);

        if (flag < 1) {
            throw new CmdException("保存数据失败");
        }


        OwnerCarPo ownerCarPo = new OwnerCarPo();
        ownerCarPo.setMemberId(reqJson.getString("carMemberId"));
        ownerCarPo.setEndTime(endTime);
        ownerCarV1InnerServiceSMOImpl.updateOwnerCar(ownerCarPo);
    }
}
