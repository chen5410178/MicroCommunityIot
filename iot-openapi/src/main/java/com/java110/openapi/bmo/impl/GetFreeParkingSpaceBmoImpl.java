package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.Assert;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.dto.parking.ParkingSpaceDto;
import com.java110.intf.barrier.ICarInoutV1InnerServiceSMO;
import com.java110.intf.car.IOwnerCarInnerServiceSMO;
import com.java110.intf.car.IOwnerCarV1InnerServiceSMO;
import com.java110.intf.car.IParkingSpaceV1InnerServiceSMO;
import com.java110.intf.community.ICommunityInnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("getFreeParkingSpaceBmoImpl")
public class GetFreeParkingSpaceBmoImpl implements IIotCommonApiBmo {

    @Autowired
    private ICarInoutV1InnerServiceSMO carInoutInnerServiceSMOImpl;

    @Autowired
    private IOwnerCarInnerServiceSMO carInnerServiceSMOImpl;

    @Autowired
    private IParkingSpaceV1InnerServiceSMO parkingSpaceInnerServiceSMOImpl;

    @Autowired
    private IOwnerCarV1InnerServiceSMO ownerCarInnerServiceSMOImpl;

    @Autowired
    private ICommunityInnerServiceSMO communityInnerServiceSMOImpl;


    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含小区信息");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
//JSONObject outParam = null;
        String communityId = reqJson.getString("communityId");

        //查询出小区内车位状态为空闲的数量
        ParkingSpaceDto parkingSpaceDto = new ParkingSpaceDto();
        parkingSpaceDto.setCommunityId(communityId);
        parkingSpaceDto.setState("F");
        int freeParkingSpaceCount = parkingSpaceInnerServiceSMOImpl.queryParkingSpacesCount(parkingSpaceDto);

        //查询出小区内的在场车辆
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCommunityId(communityId);
        carInoutDto.setStates(new String[]{"100300", "100400", "100600"});//状态，100300 进场状态 100400 支付完成 100500 离场状态 100600 支付超时重新支付
        carInoutDto.setCarType(CarInoutDto.CAR_TYPE_TEMP);
        int realCarCount = carInoutInnerServiceSMOImpl.queryCarInCount(carInoutDto);


        int realFreeParkingSpaceCount = freeParkingSpaceCount - realCarCount;

        JSONObject realFreeParkingSpace = new JSONObject();
        realFreeParkingSpace.put("total", freeParkingSpaceCount);
        realFreeParkingSpace.put("freeCount", realFreeParkingSpaceCount < 0 ? 0 : realFreeParkingSpaceCount);

        context.setResponseEntity(ResultVo.createResponseEntity(realFreeParkingSpace));
    }
}
