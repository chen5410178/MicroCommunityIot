package com.java110.barrier.engine.inout;

import com.java110.dto.barrier.BarrierDto;
import com.java110.barrier.factory.BarrierGateControlDto;
import org.springframework.stereotype.Service;

@Service
public class SendInfoEngine {

//    @Autowired
//    private IApiInnerService apiInnerServiceImpl;
//
//
//    @Autowired
    //private ICarCallHcService carCallHcServiceImpl;

    public void sendInfo(BarrierGateControlDto barrierGateControlDto, String extBoxId, BarrierDto machineDto) {

        // apiInnerServiceImpl.barrierGateControlWebSocketServerSendInfo(barrierGateControlDto.toString(), extBoxId);


        //carCallHcServiceImpl.carInoutPageInfo(barrierGateControlDto, extBoxId, machineDto);


    }
}
