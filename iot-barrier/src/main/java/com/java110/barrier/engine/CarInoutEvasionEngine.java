package com.java110.barrier.engine;

import com.alibaba.fastjson.JSONObject;
import com.java110.barrier.engine.inout.JudgeOwnerCarEngine;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.dto.carInoutEvasion.CarInoutEvasionConfigDto;
import com.java110.dto.carInoutEvasion.CarInoutEvasionDto;
import com.java110.dto.parking.CarDayDto;
import com.java110.dto.parking.ParkingAreaDto;
import com.java110.intf.barrier.ICarInoutEvasionConfigV1InnerServiceSMO;
import com.java110.intf.barrier.ICarInoutEvasionV1InnerServiceSMO;
import com.java110.intf.barrier.ICarInoutV1InnerServiceSMO;
import com.java110.intf.car.IParkingAreaV1InnerServiceSMO;
import com.java110.po.carInout.CarInoutPo;
import com.java110.po.carInoutEvasion.CarInoutEvasionPo;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarInoutEvasionEngine {

    private static Logger logger = LoggerFactory.getLogger(CarInoutEvasionEngine.class);

    @Autowired
    private ICarInoutEvasionConfigV1InnerServiceSMO carInoutEvasionConfigV1InnerServiceSMOImpl;

    @Autowired
    private ICarInoutEvasionV1InnerServiceSMO carInoutEvasionV1InnerServiceSMOImpl;

    @Autowired
    private ICarInoutV1InnerServiceSMO carInoutV1InnerServiceSMOImpl;

    @Autowired
    private IParkingAreaV1InnerServiceSMO parkingAreaV1InnerServiceSMOImpl;

    @Autowired
    JudgeOwnerCarEngine judgeOwnerCarEngine;

    public void detectTailgatingEvasion(BarrierDto machineDto, String carNum) {
        String communityId = machineDto.getCommunityId();
        String boxId = machineDto.getBoxId();

        CarInoutEvasionConfigDto configDto = new CarInoutEvasionConfigDto();
        configDto.setCommunityId(communityId);
        List<CarInoutEvasionConfigDto> configs = carInoutEvasionConfigV1InnerServiceSMOImpl.queryCarInoutEvasionConfigs(configDto);
        if (configs == null || configs.isEmpty() || !"Y".equals(configs.get(0).getEvasionOpen())) {
            return;
        }

        CarInoutDto queryDto = new CarInoutDto();
        queryDto.setBoxId(boxId);
        queryDto.setCarInout(CarInoutDto.CAR_INOUT_OUT);
        queryDto.setState(CarInoutDto.STATE_IN_FAIL);
        queryDto.setPage(1);
        queryDto.setRow(1);
        List<CarInoutDto> failedOuts = carInoutV1InnerServiceSMOImpl.queryCarInouts(queryDto);
        if (failedOuts == null || failedOuts.isEmpty()) {
            return;
        }

        CarInoutDto failedCar = failedOuts.get(0);
        if (failedCar.getCarNum().equals(carNum)) {
            return;
        }

        CarInoutDto inCheckDto = new CarInoutDto();
        inCheckDto.setCarNum(failedCar.getCarNum());
        inCheckDto.setCarInout(CarInoutDto.CAR_INOUT_IN);
        inCheckDto.setStates(new String[]{CarInoutDto.STATE_IN, CarInoutDto.STATE_PAY, CarInoutDto.STATE_REPAY});
        inCheckDto.setPaId(failedCar.getPaId());
        List<CarInoutDto> inRecords = carInoutV1InnerServiceSMOImpl.queryCarInouts(inCheckDto);
        if (ListUtil.isNull(inRecords)) {
            return;
        }

        CarInoutDto inRecord = inRecords.get(0);

        CarInoutPo updatePo = new CarInoutPo();
        updatePo.setCiId(inRecord.getCiId());
        updatePo.setState(CarInoutDto.STATE_OUT);
        carInoutV1InnerServiceSMOImpl.updateCarInout(updatePo);

        CarInoutEvasionPo evasionPo = new CarInoutEvasionPo();
        evasionPo.setEvasionId(GenerateCodeFactory.getGeneratorId("11"));
        evasionPo.setCarNum(failedCar.getCarNum());
        evasionPo.setCommunityId(communityId);
        evasionPo.setPaId(inRecord.getPaId());
        evasionPo.setPaNum(inRecord.getPaNum());
        evasionPo.setBoxId(boxId);
        evasionPo.setInoutId(inRecord.getInoutId());
        evasionPo.setCiId(inRecord.getCiId());
        evasionPo.setInTime(inRecord.getInTime());
        evasionPo.setEvasionTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        evasionPo.setState(CarInoutEvasionDto.STATE_W);
        evasionPo.setRemark("尾随逃费");
        evasionPo.setPhotoJpg(failedCar.getPhotoJpg());
        carInoutEvasionV1InnerServiceSMOImpl.saveCarInoutEvasion(evasionPo);

        logger.info("检测到尾随逃费车辆：{}，岗亭：{}", failedCar.getCarNum(), boxId);
    }

    public void detectTailgatingEvasion(BarrierDto machineDto, JSONObject reqJson) {
        detectTailgatingEvasion(machineDto, reqJson.getString("carNum"));
    }

    public List<CarInoutDto> tryFillEntryAndReQuery(String carNum, BarrierDto machineDto, String paId) {
        String communityId = machineDto.getCommunityId();

        String resolvedTime = resolveEntryTime(carNum, communityId, paId);
        if (resolvedTime == null) {
            return null;
        }

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(paId);
        parkingAreaDto.setCommunityId(communityId);
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaV1InnerServiceSMOImpl.queryParkingAreas(parkingAreaDto);
        if (ListUtil.isNull(parkingAreaDtos)) {
            return null;
        }

        String carType = getCarType(carNum, machineDto, parkingAreaDtos);


        CarInoutPo carInoutPo = new CarInoutPo();
        carInoutPo.setCiId(GenerateCodeFactory.getGeneratorId("11"));
        carInoutPo.setInoutId(GenerateCodeFactory.getGeneratorId("11"));
        carInoutPo.setCarNum(carNum);
        carInoutPo.setCarInout(CarInoutDto.CAR_INOUT_IN);
        carInoutPo.setState(CarInoutDto.STATE_IN);
        carInoutPo.setPaId(paId);
        carInoutPo.setCommunityId(communityId);
        carInoutPo.setOpenTime(resolvedTime);
        carInoutPo.setInTime(resolvedTime);
        carInoutPo.setRemark("出厂时未找到进场记录-系统补录");
        carInoutPo.setMachineId(machineDto.getMachineId());
        carInoutPo.setMachineCode(machineDto.getMachineCode());
        carInoutPo.setColorType("1");
        carInoutPo.setPaNum(parkingAreaDtos.get(0).getNum());

        carInoutPo.setUnlicense("F"); // 无牌车 T 无牌车 F 有牌车
// 如果有车辆类型信息
        carInoutPo.setCarType(carType); // 根据业务定义
        carInoutPo.setCarTypeName("临时车");

        if (CarInoutDto.CAR_TYPE_MONTH.equals(carType)) {
            carInoutPo.setCarTypeName("月租车");
        }
// 如果有照片信息
        carInoutPo.setPhotoJpg(null);
        carInoutV1InnerServiceSMOImpl.saveCarInout(carInoutPo);

        CarInoutDto queryDto = new CarInoutDto();
        queryDto.setCarNum(carNum);
        queryDto.setPaId(paId);
        queryDto.setCommunityId(communityId);
        queryDto.setStates(new String[]{CarInoutDto.STATE_IN, CarInoutDto.STATE_PAY});
        queryDto.setCarInout(CarInoutDto.CAR_INOUT_IN);
        return carInoutV1InnerServiceSMOImpl.queryCarInouts(queryDto);
    }

    private String getCarType(String carNum, BarrierDto machineDto, List<ParkingAreaDto> parkingAreaDtos) {

        CarDayDto carDayDto = null;
        String carType = "1003";
        try {
            carDayDto = judgeOwnerCarEngine.judgeOwnerCarOut(machineDto, carNum, parkingAreaDtos);
            if (carDayDto.getDay() > 0) {
                return "1001";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return carType;
    }

    private String resolveEntryTime(String carNum, String communityId, String paId) {
        CarInoutEvasionConfigDto configDto = new CarInoutEvasionConfigDto();
        configDto.setCommunityId(communityId);
        List<CarInoutEvasionConfigDto> configs = carInoutEvasionConfigV1InnerServiceSMOImpl.queryCarInoutEvasionConfigs(configDto);
        if (configs == null || configs.isEmpty() || !"Y".equals(configs.get(0).getEvasionOpen())) {
            return null;
        }

        CarInoutEvasionConfigDto config = configs.get(0);
        int queryDays = Integer.parseInt(config.getQueryDays());
        int queryMinutes = Integer.parseInt(config.getQueryMinutes());

        CarInoutDto queryDto = new CarInoutDto();
        queryDto.setCarNum(carNum);
        queryDto.setPaId(paId);
        queryDto.setCarInout(CarInoutDto.CAR_INOUT_OUT);
        queryDto.setState(CarInoutDto.STATE_OUT);
        List<CarInoutDto> outRecords = carInoutV1InnerServiceSMOImpl.queryCarInouts(queryDto);
        if (outRecords == null || outRecords.isEmpty()) {
            return null;
        }

        String nowStr = DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A);
        for (CarInoutDto outRecord : outRecords) {
            String openTime = outRecord.getOpenTime();
            String inTime = outRecord.getInTime();
            if (StringUtil.isEmpty(openTime) || StringUtil.isEmpty(inTime)) {
                continue;
            }
            // todo: 检查是否在 queryDays 天内
            long diffMinutes = DateUtil.mulMillSecA(nowStr, openTime) / (1000 * 60);
            if (diffMinutes > queryDays * 24 * 60) {
                continue;
            }
            long stopMinutes = DateUtil.mulMillSecA(openTime, inTime) / (1000 * 60);
            if (stopMinutes > queryMinutes) {
                continue;
            }
            return openTime;
        }

        return null;
    }
}
