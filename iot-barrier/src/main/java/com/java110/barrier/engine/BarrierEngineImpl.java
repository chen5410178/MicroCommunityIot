package com.java110.barrier.engine;

import com.alibaba.fastjson.JSONObject;
import com.java110.barrier.engine.inout.SendInfoEngine;
import com.java110.barrier.factory.BarrierGateControlDto;
import com.java110.barrier.factory.CarMachineProcessFactory;
import com.java110.bean.ResultVo;
import com.java110.core.constant.ResponseConstant;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.dto.fee.TempCarPayOrderDto;
import com.java110.dto.manualOpenDoorLog.ManualOpenDoorLogDto;
import com.java110.dto.parking.*;
import com.java110.intf.barrier.IBarrierV1InnerServiceSMO;
import com.java110.intf.barrier.ICarInoutV1InnerServiceSMO;
import com.java110.intf.barrier.IManualOpenDoorLogV1InnerServiceSMO;
import com.java110.intf.car.IParkingAreaV1InnerServiceSMO;
import com.java110.po.carInout.CarInoutPo;
import com.java110.po.manualOpenDoorLog.ManualOpenDoorLogPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BarrierEngineImpl implements IBarrierEngine {

    @Autowired
    private IBarrierV1InnerServiceSMO barrierV1InnerServiceSMOImpl;

    @Autowired
    private IParkingAreaV1InnerServiceSMO parkingAreaV1InnerServiceSMOImpl;

    @Autowired
    private ICarInoutV1InnerServiceSMO carInoutV1InnerServiceSMOImpl;

    @Autowired
    private SendInfoEngine sendInfoEngine;

    @Autowired
    private IManualOpenDoorLogV1InnerServiceSMO manualOpenDoorLogV1InnerServiceSMOImpl;

    @Override
    public ResultVo getTempCarFeeOrder(TempCarPayOrderDto tempCarPayOrderDto) {
        return null;
    }

    @Override
    public ResultVo notifyTempCarFeeOrder(TempCarPayOrderDto tempCarPayOrderDto) {
        return null;
    }

    @Override
    public ResponseEntity<String> customCarInOut(JSONObject reqJson) {


        BarrierDto machineDto = new BarrierDto();
        machineDto.setMachineId(reqJson.getString("machineId"));
        List<BarrierDto> machineDtos = barrierV1InnerServiceSMOImpl.queryBarriers(machineDto);

        Assert.listOnlyOne(machineDtos, "设备不存在");
        machineDto = machineDtos.get(0);
        ResultVo resultDto = null;
        ResultParkingAreaTextDto parkingAreaTextDto = null;
        if ("1101".equals(reqJson.getString("type"))) {
            uploadcarin(machineDto, reqJson);
            parkingAreaTextDto
                    = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, reqJson.getString("carNum"),
                    "欢迎光临", "", "", reqJson.getString("carNum") + ",欢迎光临", reqJson.getString("carNum"));
        } else {
            uploadcarout(machineDto, reqJson);
            parkingAreaTextDto
                    = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_SUCCESS, reqJson.getString("carNum"),
                    "一路平安", "", "", reqJson.getString("carNum") + ",一路平安", reqJson.getString("carNum"));
        }
        parkingAreaTextDto.setCarNum(reqJson.getString("carNum"));
        resultDto = openDoor(machineDtos.get(0), parkingAreaTextDto, reqJson);

        return ResultVo.createResponseEntity(resultDto);
    }

    /**
     * 车辆进场记录
     *
     * @param acceptJson
     * @return
     */
    private JSONObject uploadcarin(BarrierDto machineDto, JSONObject acceptJson) {

        String paId = "";

        //查询 岗亭
        ParkingBoxDto parkingBoxDto = new ParkingBoxDto();
        parkingBoxDto.setBoxId(machineDto.getBoxId());
        parkingBoxDto.setCommunityId(machineDto.getCommunityId());
        parkingBoxDto.setDefaultArea(ParkingBoxAreaDto.DEFAULT_AREA_TRUE);
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaV1InnerServiceSMOImpl.queryParkingAreasByBox(parkingBoxDto);
        //Assert.listOnlyOne(parkingAreaDtos, "停车场不存在");
        if (parkingAreaDtos == null || parkingAreaDtos.size() < 1) {
            throw new IllegalArgumentException("停车场不存在");
        }

        paId = parkingAreaDtos.get(0).getPaId();

        //2.0 手工进场
        CarInoutPo carInoutPo = new CarInoutPo();
        carInoutPo.setCarNum(acceptJson.getString("carNum"));
        carInoutPo.setCarType("1");
        carInoutPo.setCommunityId(machineDto.getCommunityId());
        carInoutPo.setInoutId(GenerateCodeFactory.getGeneratorId("11"));
        carInoutPo.setMachineCode(machineDto.getMachineCode());
        carInoutPo.setOpenTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        carInoutPo.setPaId(paId);
        carInoutPo.setPaNum(parkingAreaDtos.get(0).getNum());
        carInoutPo.setState("1");
        carInoutPo.setRemark("手工进场");
        carInoutV1InnerServiceSMOImpl.saveCarInout(carInoutPo);

        BarrierGateControlDto barrierGateControlDto
                = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, acceptJson.getString("carNum"), machineDto, 0, null, acceptJson.getString("carNum") + "手工进场", "开门成功");
        sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getBoxId(), machineDto);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 0);
        jsonObject.put("msg", "成功");
        return jsonObject;
    }


    private JSONObject uploadcarout(BarrierDto machineDto, JSONObject acceptJson) {

        String paId = "";

        //查询 岗亭
        ParkingBoxDto parkingBoxDto = new ParkingBoxDto();
        parkingBoxDto.setBoxId(machineDto.getBoxId());
        parkingBoxDto.setCommunityId(machineDto.getCommunityId());
        parkingBoxDto.setDefaultArea(ParkingBoxAreaDto.DEFAULT_AREA_TRUE);
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaV1InnerServiceSMOImpl.queryParkingAreasByBox(parkingBoxDto);
        //Assert.listOnlyOne(parkingAreaDtos, "停车场不存在");
        if (parkingAreaDtos == null || parkingAreaDtos.size() < 1) {
            throw new IllegalArgumentException("停车场不存在");
        }

        paId = parkingAreaDtos.get(0).getPaId();

        //查询是否有入场数据
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(acceptJson.getString("carNum"));
        carInoutDto.setPaId(paId);
        carInoutDto.setStates(new String[]{CarInoutDto.STATE_IN, CarInoutDto.STATE_PAY});
        //carInoutDto.setCarType(CarInoutDto.);
        List<CarInoutDto> carInoutDtos = carInoutV1InnerServiceSMOImpl.queryCarInouts(carInoutDto);

        if (carInoutDtos != null && !carInoutDtos.isEmpty()) {
            carInoutDto.setState(CarInoutDto.STATE_OUT);
            carInoutDto.setPayType(acceptJson.containsKey("payType") ? acceptJson.getString("payType") : CarInoutDto.PAY_TYPE_CRASH);
            carInoutDto.setPayCharge(acceptJson.getString("amount"));
            carInoutDto.setRealCharge(acceptJson.getString("amount"));
            carInoutDto.setPayTime(DateUtil.getCurrentDate());
            carInoutV1InnerServiceSMOImpl.updateCarInout(BeanConvertUtil.covertBean(carInoutDto, CarInoutPo.class));
        }
        CarInoutPo carInoutPo = new CarInoutPo();
        carInoutPo.setCarNum(acceptJson.getString("carNum"));
        carInoutPo.setCarType("1");
        carInoutPo.setCommunityId(machineDto.getCommunityId());
        carInoutPo.setInoutId(GenerateCodeFactory.getGeneratorId("11"));
        carInoutPo.setMachineCode(machineDto.getMachineCode());
        carInoutPo.setOpenTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        carInoutPo.setPaId(paId);
        carInoutPo.setPaNum(parkingAreaDtos.get(0).getNum());
        carInoutPo.setState("3");
        carInoutPo.setRemark("手工出场");
//        carInoutPo.setCarType(CarInoutDto.C);
//        if (acceptJson.containsKey("payCharge")) {
//            carInoutPo.setPayCharge(acceptJson.getString("payCharge"));
//        } else {
//            carInoutPo.setPayCharge(acceptJson.getString("amount"));
//        }
//        carInoutPo.setRealCharge(acceptJson.getString("amount"));
//        carInoutPo.setPayType("1");
        carInoutPo.setMachineCode(machineDto.getMachineCode());
        carInoutV1InnerServiceSMOImpl.saveCarInout(carInoutPo);

        BarrierGateControlDto barrierGateControlDto
                = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, acceptJson.getString("carNum"), machineDto, 0, null, acceptJson.getString("carNum") + "手工出场", "开门成功");
        sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getBoxId(), machineDto);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 0);
        jsonObject.put("msg", "成功");
        return jsonObject;
    }

    public ResultVo openDoor(BarrierDto machineDto, ParkingAreaTextDto parkingAreaTextDto, JSONObject paramObj) {


        CarMachineProcessFactory.getCarImpl(machineDto.getHmId()).openDoor(machineDto, parkingAreaTextDto);


        if (paramObj.containsKey("extStaffId")) {
            ManualOpenDoorLogPo manualOpenDoorLogPo = new ManualOpenDoorLogPo();
            manualOpenDoorLogPo.setLogId(GenerateCodeFactory.getGeneratorId("11"));
            manualOpenDoorLogPo.setMachineName(machineDto.getMachineName());
            manualOpenDoorLogPo.setCommunityId(machineDto.getCommunityId());
            manualOpenDoorLogPo.setExtMachineId(machineDto.getMachineId());
            manualOpenDoorLogPo.setExtStaffId(paramObj.getString("extStaffId"));
            manualOpenDoorLogPo.setMachineId(machineDto.getMachineId());
            manualOpenDoorLogPo.setStaffId("-1");
            manualOpenDoorLogPo.setStaffName(paramObj.getString("staffName"));
            manualOpenDoorLogV1InnerServiceSMOImpl.saveManualOpenDoorLog(manualOpenDoorLogPo);


            CarMachineProcessFactory.getCarImpl(machineDto.getHmId()).triggerImage(machineDto, manualOpenDoorLogPo);

        }

//        JSONObject data = new JSONObject();
//        if (StringUtil.isEmpty(machineDto.getTaskId())) {
//            data.put("taskId", machineDto.getTaskId());
//        }
        return new ResultVo(ResultVo.CODE_OK, ResultVo.MSG_OK);
    }
}
