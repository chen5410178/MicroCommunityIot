package com.java110.barrier.engine;


import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.parking.ParkingAreaDto;
import com.java110.dto.parking.ResultParkingAreaTextDto;

import java.util.List;

public interface IOutCarEngine {

    /**
     * 出场
     * @param type
     * @param carNum
     * @param machineDto
     * @param parkingAreaDtos
     * @return
     * @throws Exception
     */
    ResultParkingAreaTextDto outParkingArea(String type, String carNum, BarrierDto machineDto, List<ParkingAreaDto> parkingAreaDtos, IInOutCarTextEngine inOutCarTextEngine) throws Exception;

}
