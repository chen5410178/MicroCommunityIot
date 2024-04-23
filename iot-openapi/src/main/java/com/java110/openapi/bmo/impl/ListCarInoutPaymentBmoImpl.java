package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.*;
import com.java110.dto.parking.ParkingAreaDto;
import com.java110.dto.payment.CarInoutPaymentDto;
import com.java110.intf.barrier.ICarInoutPaymentV1InnerServiceSMO;
import com.java110.intf.barrier.IParkingBoxAreaV1InnerServiceSMO;
import com.java110.intf.car.IParkingAreaV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("listCarInoutPaymentBmoImpl")
public class ListCarInoutPaymentBmoImpl implements IIotCommonApiBmo {
    @Autowired
    private ICarInoutPaymentV1InnerServiceSMO carInoutPaymentV1InnerServiceSMOImpl;
    @Autowired
    private IParkingBoxAreaV1InnerServiceSMO parkingBoxAreaV1InnerServiceSMOImpl;

    @Autowired
    private IParkingAreaV1InnerServiceSMO parkingAreaV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
        Assert.hasKeyAndValue(reqJson, "page", "未包含page");
        Assert.hasKeyAndValue(reqJson, "row", "未包含row");
        Assert.hasKeyAndValue(reqJson, "paNum", "未包含paId");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setNum(reqJson.getString("paNum"));
        parkingAreaDto.setCommunityId(reqJson.getString("communityId"));
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaV1InnerServiceSMOImpl.queryParkingAreas(parkingAreaDto);
        if (ListUtil.isNull(parkingAreaDtos)) {
            throw new IllegalArgumentException("停车场不存在");
        }

        CarInoutPaymentDto carInoutPaymentDto = BeanConvertUtil.covertBean(reqJson, CarInoutPaymentDto.class);

        carInoutPaymentDto.setPaId(parkingAreaDtos.get(0).getPaId());

        if (!StringUtil.isEmpty(carInoutPaymentDto.getEndTime())) {
            Date endTime = DateUtil.getDateFromStringB(carInoutPaymentDto.getEndTime());
            carInoutPaymentDto.setEndTime(DateUtil.getAddDayStringB(endTime, 1));
        }

        int count = carInoutPaymentV1InnerServiceSMOImpl.queryCarInoutPaymentsCount(carInoutPaymentDto);

        List<CarInoutPaymentDto> carInoutPaymentDtos = null;

        if (count > 0) {
            carInoutPaymentDtos = carInoutPaymentV1InnerServiceSMOImpl.queryCarInoutPayments(carInoutPaymentDto);
        } else {
            carInoutPaymentDtos = new ArrayList<>();
        }

        if (carInoutPaymentDtos != null && carInoutPaymentDtos.size() > 0) {
            List<CarInoutPaymentDto> tempCarInoutPaymentDtos = carInoutPaymentV1InnerServiceSMOImpl.queryCarInoutPaymentMarjor(carInoutPaymentDto);
            for (CarInoutPaymentDto carInoutPaymentDto1 : carInoutPaymentDtos) {
                carInoutPaymentDto1.setPayChargeTotal(tempCarInoutPaymentDtos.get(0).getPayChargeTotal());
                carInoutPaymentDto1.setRealChargeTotal(tempCarInoutPaymentDtos.get(0).getRealChargeTotal());
            }
        } else {
            for (CarInoutPaymentDto carInoutPaymentDto1 : carInoutPaymentDtos) {
                carInoutPaymentDto1.setPayChargeTotal("0");
                carInoutPaymentDto1.setRealChargeTotal("0");
            }
        }


        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, carInoutPaymentDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }
}
