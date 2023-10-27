package com.java110.barrier.engine.inout;

import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DateUtil;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.dto.parking.ParkingAreaDto;
import com.java110.intf.barrier.ICarInoutV1InnerServiceSMO;
import com.java110.intf.car.IParkingAreaV1InnerServiceSMO;
import com.java110.po.carInout.CarInoutPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarInLogEngine extends CarEngine {

    @Autowired
    private ICarInoutV1InnerServiceSMO carInoutServiceImpl;

    @Autowired
    private IParkingAreaV1InnerServiceSMO parkingAreaV1InnerServiceSMOImpl;

    /**
     * 保存经常记录
     *
     * @param carNum
     * @param type
     * @param machineDto
     * @param parkingAreaDtos
     * @param state
     * @param remark
     * @throws Exception
     */
    public void saveCarInLog(String carNum, String type, BarrierDto machineDto, List<ParkingAreaDto> parkingAreaDtos, String state, String remark) throws Exception {

        //2.0 进场
        CarInoutPo carInoutPo = new CarInoutPo();
        carInoutPo.setCarNum(carNum);
        carInoutPo.setCarType(type);
        if(CarInoutDto.CAR_TYPE_MONTH.equals(type)){
            carInoutPo.setCarTypeName("月租车");
        }else{
            carInoutPo.setCarTypeName("临时车");
        }

        carInoutPo.setCommunityId(machineDto.getCommunityId());
        carInoutPo.setMachineId(machineDto.getMachineId());
        carInoutPo.setMachineCode(machineDto.getMachineCode());
        carInoutPo.setInoutId(GenerateCodeFactory.getGeneratorId("11"));
        carInoutPo.setCarInout(CarInoutDto.CAR_INOUT_IN);
        carInoutPo.setMachineCode(machineDto.getMachineCode());
        carInoutPo.setOpenTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        carInoutPo.setPaId(getDefaultPaId(parkingAreaDtos));
        carInoutPo.setPaNum(getDefaultPaNum(parkingAreaDtos));
        carInoutPo.setState(state);
        carInoutPo.setRemark(remark);
        carInoutPo.setInTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        carInoutPo.setCiId(GenerateCodeFactory.getGeneratorId("11"));
        carInoutPo.setPhotoJpg(machineDto.getPhotoJpg());
        carInoutServiceImpl.saveCarInout(carInoutPo);
    }

    /**
     * 保存经常记录
     *
     * @param carNum
     * @param type
     * @param machineDto
     * @param paId
     * @param state
     * @param remark
     * @throws Exception
     */
    public void saveCarInLog(String carNum, String type, BarrierDto machineDto, String paId, String state, String remark) throws Exception {


        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(paId);
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaV1InnerServiceSMOImpl.queryParkingAreas(parkingAreaDto);

        Assert.listOnlyOne(parkingAreaDtos, "停车场不存在");

        //2.0 进场
        CarInoutPo carInoutPo = new CarInoutPo();
        carInoutPo.setCarNum(carNum);
        carInoutPo.setCarType(type);
        if(CarInoutDto.CAR_TYPE_MONTH.equals(type)){
            carInoutPo.setCarTypeName("月租车");
        }else{
            carInoutPo.setCarTypeName("临时车");
        }
        carInoutPo.setCommunityId(machineDto.getCommunityId());
        carInoutPo.setMachineId(machineDto.getMachineId());
        carInoutPo.setMachineCode(machineDto.getMachineCode());
        carInoutPo.setInoutId(GenerateCodeFactory.getGeneratorId("11"));
        carInoutPo.setCarInout(CarInoutDto.CAR_INOUT_OUT);
        carInoutPo.setMachineCode(machineDto.getMachineCode());
        carInoutPo.setOpenTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        carInoutPo.setPaId(paId);
        carInoutPo.setPaNum(parkingAreaDtos.get(0).getNum());
        carInoutPo.setState(state);
        carInoutPo.setRemark(remark);
        carInoutPo.setInTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        carInoutPo.setCiId(GenerateCodeFactory.getGeneratorId("11"));
        carInoutPo.setPhotoJpg(machineDto.getPhotoJpg());
        carInoutServiceImpl.saveCarInout(carInoutPo);
    }


}
