package com.java110.barrier.engine.inout;

import com.java110.bean.dto.car.CarBlackWhiteDto;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.dto.parking.ParkingAreaDto;
import com.java110.intf.car.ICarBlackWhiteV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JudgeWhiteCarEngine {

    @Autowired
    private ICarBlackWhiteV1InnerServiceSMO carBlackWhiteServiceImpl;
    /**
     * 白名单车辆
     */
    public Boolean judgeWhiteCar(BarrierDto machineDto, String carNum,
                                 List<ParkingAreaDto> parkingAreaDtos,
                                 String type, List<CarInoutDto> carInoutDtos) throws Exception {
        List<String> paIds = new ArrayList<>();
        for (ParkingAreaDto parkingAreaDto : parkingAreaDtos) {
            paIds.add(parkingAreaDto.getPaId());
        }
        //1.0 判断是否为黑名单
        CarBlackWhiteDto carBlackWhiteDto = new CarBlackWhiteDto();
        carBlackWhiteDto.setCommunityId(machineDto.getCommunityId());
        carBlackWhiteDto.setPaIds(paIds.toArray(new String[paIds.size()]));
        carBlackWhiteDto.setCarNum(carNum);
        carBlackWhiteDto.setBlackWhite(CarBlackWhiteDto.BLACK_WHITE_WHITE);
        carBlackWhiteDto.setHasValid("Y");
        List<CarBlackWhiteDto> blackWhiteDtos = carBlackWhiteServiceImpl.queryCarBlackWhites(carBlackWhiteDto);

        //白名单直接出场
        CarInoutDto carInoutDto = null;
        if (blackWhiteDtos != null && blackWhiteDtos.size() > 0) {
            return true;
        }

        return false;
    }

    public Boolean judgeBlackCar(BarrierDto machineDto, String carNum,
                                 List<ParkingAreaDto> parkingAreaDtos,
                                 String type, List<CarInoutDto> carInoutDtos) throws Exception {
        List<String> paIds = new ArrayList<>();
        for (ParkingAreaDto parkingAreaDto : parkingAreaDtos) {
            paIds.add(parkingAreaDto.getPaId());
        }
        //1.0 判断是否为黑名单
        CarBlackWhiteDto carBlackWhiteDto = new CarBlackWhiteDto();
        carBlackWhiteDto.setCommunityId(machineDto.getCommunityId());
        carBlackWhiteDto.setPaIds(paIds.toArray(new String[paIds.size()]));
        carBlackWhiteDto.setCarNum(carNum);
        carBlackWhiteDto.setBlackWhite(CarBlackWhiteDto.BLACK_WHITE_BLACK);
        carBlackWhiteDto.setHasValid("Y");
        List<CarBlackWhiteDto> blackWhiteDtos = carBlackWhiteServiceImpl.queryCarBlackWhites(carBlackWhiteDto);

        //白名单直接出场
        CarInoutDto carInoutDto = null;
        if (blackWhiteDtos != null && blackWhiteDtos.size() > 0) {
            return true;
        }

        return false;
    }
}
