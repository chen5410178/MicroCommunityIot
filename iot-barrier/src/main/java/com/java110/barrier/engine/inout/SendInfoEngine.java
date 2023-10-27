package com.java110.barrier.engine.inout;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.barrier.BarrierDto;
import com.java110.barrier.factory.BarrierGateControlDto;
import com.java110.intf.gateway.IWebsocketV1InnerSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SendInfoEngine {

//    @Autowired
//    private IApiInnerService apiInnerServiceImpl;
//
//
//    @Autowired
    //private ICarCallHcService carCallHcServiceImpl;

    @Autowired
    private IWebsocketV1InnerSMO websocketV1InnerSMOImpl;

    public void sendInfo(BarrierGateControlDto barrierGateControlDto, String boxId, BarrierDto machineDto) {

        // apiInnerServiceImpl.barrierGateControlWebSocketServerSendInfo(barrierGateControlDto.toString(), extBoxId);

        barrierGateControlDto.setBoxId(boxId);
        barrierGateControlDto.setMachineId(machineDto.getMachineId());
        websocketV1InnerSMOImpl.webSentParkingArea(BeanConvertUtil.beanCovertJson(barrierGateControlDto));


        //carCallHcServiceImpl.carInoutPageInfo(barrierGateControlDto, extBoxId, machineDto);


    }
}
