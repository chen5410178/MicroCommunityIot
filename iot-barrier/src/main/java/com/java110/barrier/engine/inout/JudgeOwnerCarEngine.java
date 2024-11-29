package com.java110.barrier.engine.inout;

import com.java110.bean.dto.car.OwnerCarDto;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.dto.parking.CarDayDto;
import com.java110.dto.parking.ParkingAreaDto;
import com.java110.intf.barrier.ICarInoutV1InnerServiceSMO;
import com.java110.intf.car.IOwnerCarV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JudgeOwnerCarEngine {


    @Autowired
    private IOwnerCarV1InnerServiceSMO carServiceImpl;

    @Autowired
    private ICarInoutV1InnerServiceSMO carInoutServiceImpl;


    public CarDayDto judgeOwnerCar(BarrierDto machineDto, String carNum, List<ParkingAreaDto> parkingAreaDtos) throws Exception {
        List<String> paIds = new ArrayList<>();
        for (ParkingAreaDto parkingAreaDto : parkingAreaDtos) {
            paIds.add(parkingAreaDto.getPaId());
        }
        OwnerCarDto ownerCarDto = new OwnerCarDto();
        ownerCarDto.setPaIds(paIds.toArray(new String[paIds.size()]));
        ownerCarDto.setCarNum(carNum);
        List<OwnerCarDto> ownerCarDtos = carServiceImpl.queryOwnerCars(ownerCarDto);

        if (ListUtil.isNull(ownerCarDtos)) {
            return new CarDayDto(carNum, OwnerCarDto.LEASE_TYPE_TEMP, -1);
        }

        //主副车辆中 有一个车辆在场，这个车场当做临时车处理
        if (hasInParkingArea(ownerCarDtos.get(0).getCarId(), carNum, paIds)) {
            return new CarDayDto(carNum, OwnerCarDto.LEASE_TYPE_TEMP, -1);
        }


        int day = DateUtil.differentDaysUp(DateUtil.getCurrentDate(), ownerCarDtos.get(0).getEndTime());
        //来个五天 缴费期
        if (day <= 0 && day > -5) {
            return new CarDayDto(carNum, ownerCarDtos.get(0).getLeaseType(), -2);
        }

        if (day <= -5) {
            return new CarDayDto(carNum, OwnerCarDto.LEASE_TYPE_TEMP, -1);
        }
        return new CarDayDto(carNum, ownerCarDtos.get(0).getLeaseType(), day);
    }

    /**
     * 判断 主副车辆中 有一个车辆在场，这个车场当做临时车处理
     *
     * @param carNum
     * @return
     */
    private boolean hasInParkingArea(String primaryCarId, String carNum, List<String> paIds) throws Exception {
        if (StringUtil.isEmpty(primaryCarId) || "-1".equals(primaryCarId)) {
            return false;
        }

        // 判断是否为主副车辆
        OwnerCarDto carDto = new OwnerCarDto();
        carDto.setPaIds(paIds.toArray(new String[paIds.size()]));
        //carDto.setCarNum(carNum);
        carDto.setCarId(primaryCarId);
        List<OwnerCarDto> tmpCarDtos = carServiceImpl.queryOwnerCars(carDto);
        if (tmpCarDtos == null || tmpCarDtos.size() < 2) {
            return false;
        }

        List<String> otherCarNums = new ArrayList<>();

        for (OwnerCarDto tempCarDto : tmpCarDtos) {
            if (tempCarDto.getCarNum().equals(carNum)) {
                continue;
            }
            otherCarNums.add(tempCarDto.getCarNum());
        }

        if (otherCarNums.size() < 1) {
            return false;
        }

        for (String otherCarNum : otherCarNums) {
            //判断车辆是否在 场内
            CarInoutDto inoutDto = new CarInoutDto();
            inoutDto.setCarNum(otherCarNum);
            inoutDto.setPaIds(paIds.toArray(new String[paIds.size()]));
            inoutDto.setCarType(CarInoutDto.CAR_TYPE_MONTH);
            inoutDto.setStates(new String[]{CarInoutDto.STATE_IN, CarInoutDto.STATE_PAY});
            List<CarInoutDto> carInoutDtos = carInoutServiceImpl.queryCarInouts(inoutDto);
            if (carInoutDtos != null && carInoutDtos.size() > 0) {
                return true;
            }
        }

        return false;
    }
}
