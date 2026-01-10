package com.java110.barrier.engine.adapt;

import com.java110.barrier.engine.ICallCarService;
import com.java110.barrier.engine.IInCarEngine;
import com.java110.barrier.engine.IInOutCarTextEngine;
import com.java110.barrier.engine.IOutCarEngine;
import com.java110.barrier.engine.inout.SendInfoEngine;
import com.java110.barrier.factory.BarrierGateControlDto;
import com.java110.core.utils.DateUtil;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.dto.carInoutTempAuth.CarInoutTempAuthDto;
import com.java110.dto.parking.ParkingAreaDto;
import com.java110.dto.parking.ParkingBoxDto;
import com.java110.dto.parking.ResultParkingAreaTextDto;
import com.java110.intf.barrier.ICarInoutV1InnerServiceSMO;
import com.java110.intf.barrier.IParkingBoxV1InnerServiceSMO;
import com.java110.intf.barrier.ITempCarFeeConfigV1InnerServiceSMO;
import com.java110.intf.car.ICarBlackWhiteV1InnerServiceSMO;
import com.java110.intf.car.IOwnerCarV1InnerServiceSMO;
import com.java110.intf.car.IParkingAreaV1InnerServiceSMO;
import com.java110.intf.community.ICommunityV1InnerServiceSMO;
import com.java110.po.carInout.CarInoutPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 摄像头业务处理类
 */
@Service
public class CallCarServiceImpl implements ICallCarService {
    Logger logger = LoggerFactory.getLogger(CallCarServiceImpl.class);
    @Autowired
    private ICarBlackWhiteV1InnerServiceSMO carBlackWhiteServiceImpl;

    @Autowired
    private IOwnerCarV1InnerServiceSMO carServiceImpl;

    @Autowired
    private ICarInoutV1InnerServiceSMO carInoutServiceImpl;


    @Autowired
    private ITempCarFeeConfigV1InnerServiceSMO tempCarFeeConfigServiceImpl;

    @Autowired
    private IParkingAreaV1InnerServiceSMO parkingAreaServiceImpl;

    @Autowired
    private ICommunityV1InnerServiceSMO communityServiceImpl;

    @Autowired
    private IParkingBoxV1InnerServiceSMO parkingBoxServiceImpl;



    @Autowired
    private RestTemplate restTemplate;

//    @Autowired
//    private IApiInnerService apiInnerServiceImpl;

    @Autowired
    private IOutCarEngine outCarEngine;

    @Autowired
    private IInCarEngine inCarEngine;

    @Autowired
    private SendInfoEngine sendInfoEngine;


    @Override
    public ResultParkingAreaTextDto ivsResult(String type, String carNum, BarrierDto machineDto, IInOutCarTextEngine inOutCarTextEngine) throws Exception {


        String machineDirection = machineDto.getDirection();
        ResultParkingAreaTextDto resultParkingAreaTextDto = null;
        //查询 岗亭
        ParkingBoxDto parkingBoxDto = new ParkingBoxDto();
        parkingBoxDto.setBoxId(machineDto.getBoxId());
        parkingBoxDto.setCommunityId(machineDto.getCommunityId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaServiceImpl.queryParkingAreasByBox(parkingBoxDto);
        //Assert.listOnlyOne(parkingAreaDtos, "停车场不存在");
        if (parkingAreaDtos == null || parkingAreaDtos.size() < 1) {
            throw new IllegalArgumentException("停车场不存在");
        }

        if("无牌车".equals(carNum)){
        }

        BarrierGateControlDto barrierGateControlDto = new BarrierGateControlDto(BarrierGateControlDto.ACTION_INOUT, carNum, machineDto);
        sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getBoxId(), machineDto);
        switch (machineDirection) {
            case BarrierDto.MACHINE_DIRECTION_ENTER: // 车辆进场
                if("无牌车".equals(carNum)){
                    resultParkingAreaTextDto = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_ERROR, "无牌车","请扫码入场","","","无牌车,请扫码入场", carNum);
                }else {
                    resultParkingAreaTextDto = inCarEngine.enterParkingArea(type, carNum, machineDto, parkingAreaDtos, inOutCarTextEngine);
                }
                break;
            case BarrierDto.MACHINE_DIRECTION_OUT://车辆出场
                if("无牌车".equals(carNum)){
                    resultParkingAreaTextDto = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_ERROR, "无牌车","请扫码出场","","","无牌车,请扫码出场", carNum);
                }else {
                    resultParkingAreaTextDto = outCarEngine.outParkingArea(type, carNum, machineDto, parkingAreaDtos,inOutCarTextEngine);
                }
                break;
            default:
                resultParkingAreaTextDto = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_ERROR, "系统异常");
        }

        return resultParkingAreaTextDto;
    }

    @Override
    public void tempCarAuthOpen(CarInoutTempAuthDto carInoutTempAuthDto) throws Exception{
        inCarEngine.tempCarAuthOpen(carInoutTempAuthDto);
    }














}
