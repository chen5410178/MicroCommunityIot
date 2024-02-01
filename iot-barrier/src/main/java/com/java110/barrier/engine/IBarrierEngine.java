package com.java110.barrier.engine;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.fee.TempCarPayOrderDto;
import com.java110.dto.parking.ParkingAreaTextDto;
import org.springframework.http.ResponseEntity;

public interface IBarrierEngine {
    ResultVo getTempCarFeeOrder(TempCarPayOrderDto tempCarPayOrderDto);

    ResultVo notifyTempCarFeeOrder(TempCarPayOrderDto tempCarPayOrderDto);

    ResultVo customCarInOut(JSONObject reqJson);

    ResultVo openDoor(BarrierDto machineDto, ParkingAreaTextDto parkingAreaTextDto, JSONObject paramObj);

    ResultVo closeDoor(BarrierDto barrierDto, ParkingAreaTextDto parkingAreaTextDto, JSONObject reqJson);
}
