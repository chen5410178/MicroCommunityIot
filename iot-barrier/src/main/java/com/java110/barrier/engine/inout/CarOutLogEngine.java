package com.java110.barrier.engine.inout;

import com.java110.barrier.engine.CarInoutEvasionEngine;
import com.java110.bean.dto.car.OwnerCarDto;
import com.java110.bean.dto.coupon.ParkingCouponCarDto;
import com.java110.bean.po.coupon.ParkingCouponCarOrderPo;
import com.java110.bean.po.coupon.ParkingCouponCarPo;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.dto.parking.CarDayDto;
import com.java110.dto.parking.ParkingAreaDto;
import com.java110.intf.barrier.ICarInoutV1InnerServiceSMO;
import com.java110.intf.car.IParkingCouponCarOrderV1InnerServiceSMO;
import com.java110.intf.car.IParkingCouponCarV1InnerServiceSMO;
import com.java110.po.carInout.CarInoutPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarOutLogEngine extends CarEngine{


    @Autowired
    private ICarInoutV1InnerServiceSMO carInoutServiceImpl;

    @Autowired
    private IParkingCouponCarV1InnerServiceSMO parkingCouponCarV1InnerServiceSMOImpl;

    @Autowired
    private IParkingCouponCarOrderV1InnerServiceSMO parkingCouponCarOrderV1InnerServiceSMOImpl;

    @Autowired
    private JudgeOwnerCarEngine judgeOwnerCarEngine;

    @Autowired
    private CarInoutEvasionEngine carInoutEvasionEngine;


    public void saveCarOutLog(String carNum,
                              BarrierDto machineDto,
                              List<ParkingAreaDto> parkingAreaDtos,
                              String state,
                              String remark) throws Exception {
        saveCarOutLog(carNum,machineDto,parkingAreaDtos,state,remark,null);
    }

    public void saveCarOutLog(String carNum,
                              BarrierDto machineDto,
                              List<ParkingAreaDto> parkingAreaDtos,
                              String state,
                              String remark,
                              List<ParkingCouponCarDto> parkingCouponCarDtos) throws Exception {
        CarDayDto carDayDto = judgeOwnerCarEngine.judgeOwnerCarOut(machineDto, carNum, parkingAreaDtos);

        //查询是否有入场数据
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(carNum);
        carInoutDto.setPaId(getDefaultPaId(parkingAreaDtos));
        carInoutDto.setStates(new String[]{CarInoutDto.STATE_IN, CarInoutDto.STATE_PAY});
        carInoutDto.setCarInout(CarInoutDto.CAR_INOUT_IN);
        List<CarInoutDto> carInoutDtos = carInoutServiceImpl.queryCarInouts(carInoutDto);


        CarInoutPo carInoutPo = new CarInoutPo();
        carInoutPo.setCarNum(carNum);
        if (OwnerCarDto.LEASE_TYPE_TEMP.equals(carDayDto.getLeaseType())) {
            carInoutPo.setCarType(CarInoutDto.CAR_TYPE_TEMP);
            carInoutPo.setCarTypeName("临时车");
        } else {
            carInoutPo.setCarType(CarInoutDto.CAR_TYPE_MONTH);
            carInoutPo.setCarTypeName("月租车");
        }
        carInoutPo.setCommunityId(machineDto.getCommunityId());
        carInoutPo.setCiId(GenerateCodeFactory.getGeneratorId("11"));
        carInoutPo.setCarInout(CarInoutDto.CAR_INOUT_OUT);
        carInoutPo.setMachineCode(machineDto.getMachineCode());
        carInoutPo.setMachineId(machineDto.getMachineId());
        carInoutPo.setOpenTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        carInoutPo.setPaId(getDefaultPaId(parkingAreaDtos));
        carInoutPo.setPaNum(getDefaultPaNum(parkingAreaDtos));
        carInoutPo.setState(state);
        carInoutPo.setRemark(remark);
        carInoutPo.setPhotoJpg(machineDto.getPhotoJpg());
        if (carInoutDtos != null && carInoutDtos.size() > 0) {
//            carInoutPo.setPayCharge(carInoutDtos.get(0).getPayCharge());
//            carInoutPo.setRealCharge(carInoutDtos.get(0).getRealCharge());
//            carInoutPo.setPayType(carInoutDtos.get(0).getPayType());
            carInoutPo.setInTime(carInoutDtos.get(0).getOpenTime());
            carInoutPo.setInoutId(carInoutDtos.get(0).getInoutId());
            carInoutPo.setColorType(carInoutDtos.get(0).getColorType());
        } else {
            carInoutPo.setInTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
            carInoutPo.setInoutId(GenerateCodeFactory.getGeneratorId("11"));
//            carInoutPo.setPayCharge("0");
//            carInoutPo.setRealCharge("0");
//            carInoutPo.setPayType("1");
        }
        if(StringUtil.isEmpty(carInoutPo.getColorType())){
            carInoutPo.setColorType("1");// 蓝牌车
        }
        carInoutDto.setMachineCode(machineDto.getMachineCode());
        //carInoutDto.setParkingCouponCarDtos(parkingCouponCarDtos);
        carInoutServiceImpl.saveCarInout(carInoutPo);

        if (CarInoutDto.STATE_IN_FAIL.equals(state)) {
            carInoutEvasionEngine.detectTailgatingEvasion(machineDto, carNum);
            return;
        }

        if (carInoutDtos != null && carInoutDtos.size() > 0) {
            for(CarInoutDto tmpCarInoutDto :carInoutDtos) {
                carInoutPo = new CarInoutPo();
                carInoutPo.setCiId(tmpCarInoutDto.getCiId());
                carInoutPo.setState(CarInoutDto.STATE_OUT);
                carInoutServiceImpl.updateCarInout(carInoutPo);
            }
        }

        if(ListUtil.isNull(parkingCouponCarDtos)){
            return ;
        }
        //停车劵核销
        ParkingCouponCarPo tmpParkingCouponCarPo = null;
        for(ParkingCouponCarDto parkingCouponCarDto : parkingCouponCarDtos) {
            tmpParkingCouponCarPo = new ParkingCouponCarPo();
            tmpParkingCouponCarPo.setPccId(parkingCouponCarDto.getPccId());
            tmpParkingCouponCarPo.setState(ParkingCouponCarDto.STATE_F);
            tmpParkingCouponCarPo.setRemark("车辆出场核销");
            parkingCouponCarV1InnerServiceSMOImpl.updateParkingCouponCar(tmpParkingCouponCarPo);

            parkingCouponCarDto = new ParkingCouponCarDto();
            parkingCouponCarDto.setPccId(parkingCouponCarDto.getPccId());
            parkingCouponCarDtos = parkingCouponCarV1InnerServiceSMOImpl.queryParkingCouponCars(parkingCouponCarDto);
            if(ListUtil.isNull(parkingCouponCarDtos)){
                continue;
            }

            ParkingCouponCarOrderPo parkingCouponCarOrderPo = new ParkingCouponCarOrderPo();
            parkingCouponCarOrderPo.setOrderId(GenerateCodeFactory.getGeneratorId("11"));
            parkingCouponCarOrderPo.setCarNum(parkingCouponCarDtos.get(0).getCarNum());
            parkingCouponCarOrderPo.setCarOutId("-1");
            parkingCouponCarOrderPo.setCommunityId(parkingCouponCarDtos.get(0).getCommunityId());
            parkingCouponCarOrderPo.setMachineId(machineDto.getMachineId());
            parkingCouponCarOrderPo.setMachineName(machineDto.getMachineName());
            parkingCouponCarOrderPo.setPaId(parkingCouponCarDtos.get(0).getPaId());
            parkingCouponCarOrderPo.setPccId(parkingCouponCarDtos.get(0).getPccId());
            parkingCouponCarOrderPo.setRemark("pc端支付停车劵抵扣");

            parkingCouponCarOrderV1InnerServiceSMOImpl.saveParkingCouponCarOrder(parkingCouponCarOrderPo);
        }


//        //异步上报HC小区管理系统
//        carCallHcServiceImpl.carInout(carInoutDto);
    }
}
