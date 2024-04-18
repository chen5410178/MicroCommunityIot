package com.java110.barrier.cmd.carInout;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.car.OwnerCarDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.dto.fee.FeeDto;
import com.java110.dto.parking.ParkingSpaceDto;
import com.java110.intf.barrier.ICarInoutV1InnerServiceSMO;
import com.java110.intf.car.IOwnerCarInnerServiceSMO;
import com.java110.intf.car.IOwnerCarV1InnerServiceSMO;
import com.java110.intf.car.IParkingSpaceV1InnerServiceSMO;
import com.java110.intf.community.ICommunityInnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.*;

@Java110Cmd(serviceCode = "carInout.getFreeParkingSpace")
public class GetFreeParkingSpaceCmd extends Cmd {

    private static final String MACHINE_DIRECTION_IN = "3306"; // 进入

    private static final String MACHINE_DIRECTION_OUT = "3307"; //出去

    private static final String HIRE_SELL_OUT = "hireSellOut"; // 出租或出售车辆出场

    private static final String CAR_BLACK = "1111"; // 车辆黑名单
    private static final String CAR_WHITE = "2222"; // 车辆白名单


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
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含小区信息");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
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
        List<CarInoutDto> carInoutDtos = carInoutInnerServiceSMOImpl.queryCarInouts(carInoutDto);
        List<String> carNums = new ArrayList<>();//小区内的在场车辆车牌
        for (CarInoutDto tmpCarInoutDto : carInoutDtos) {
            carNums.add(tmpCarInoutDto.getCarNum());
        }
        OwnerCarDto ownerCarDto = new OwnerCarDto();
        ownerCarDto.setCommunityId(communityId);
        if (!carNums.isEmpty()) {
            ownerCarDto.setCarNums(carNums.toArray(new String[carNums.size()]));
        }
        List<OwnerCarDto> ownerCarDtos = ownerCarInnerServiceSMOImpl.queryOwnerCars(ownerCarDto);
        //付款方ID-车牌号
        Map<String, String> psIdAndCarNumMap = new HashMap<>();
        List<String> psIds = new ArrayList<>();
        for (OwnerCarDto tmpOwnerCarDto : ownerCarDtos) {
            psIds.add(tmpOwnerCarDto.getPsId());
            psIdAndCarNumMap.put(tmpOwnerCarDto.getPsId(), tmpOwnerCarDto.getCarNum());
        }
        FeeDto feeDto = new FeeDto();
        feeDto.setCommunityId(communityId);
        feeDto.setPayerObjIds(psIds.toArray(new String[psIds.size()]));
        feeDto.setNoArrearsEndTime(new Date());

//        int communityCarCount = feeInnerServiceSMOImpl.queryFeesCount(feeDto);

        //在场车辆车牌号【数组】-业主车牌号（有效的已租已售）【数组】，业主车牌有进场才去扣除，没进场不进行扣减
//        int realCarCount = carInoutDtos.size() - communityCarCount;
        int realCarCount = carNums.size();

        int realFreeParkingSpaceCount = freeParkingSpaceCount - realCarCount;

        JSONObject realFreeParkingSpace = new JSONObject();
        realFreeParkingSpace.put("total", freeParkingSpaceCount);
        realFreeParkingSpace.put("freeCount", realFreeParkingSpaceCount < 0 ? 0 : realFreeParkingSpaceCount);

        context.setResponseEntity(ResultVo.createResponseEntity(realFreeParkingSpace));

    }
}
