package com.java110.barrier.engine;


import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.carInoutTempAuth.CarInoutTempAuthDto;
import com.java110.dto.parking.ParkingAreaDto;
import com.java110.dto.parking.ResultParkingAreaTextDto;

import java.util.List;

public interface IInCarEngine {

    ResultParkingAreaTextDto enterParkingArea(String type, String carNum, BarrierDto machineDto, List<ParkingAreaDto> parkingAreaDtos, IInOutCarTextEngine inOutCarTextEngine) throws Exception;

    void tempCarAuthOpen(CarInoutTempAuthDto carInoutTempAuthDto) throws Exception;
}
