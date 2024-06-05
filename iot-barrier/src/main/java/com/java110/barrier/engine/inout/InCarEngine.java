package com.java110.barrier.engine.inout;

import com.alibaba.fastjson.JSONObject;
import com.java110.barrier.engine.IInCarEngine;
import com.java110.barrier.engine.IInOutCarTextEngine;
import com.java110.barrier.factory.BarrierGateControlDto;
import com.java110.barrier.factory.CarMachineProcessFactory;
import com.java110.bean.dto.car.CarBlackWhiteDto;
import com.java110.bean.dto.car.OwnerCarDto;
import com.java110.core.cache.CommonCache;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.dto.carInoutTempAuth.CarInoutTempAuthDto;
import com.java110.dto.parking.CarDayDto;
import com.java110.dto.parking.InOutCarTextDto;
import com.java110.dto.parking.ParkingAreaDto;
import com.java110.dto.parking.ResultParkingAreaTextDto;
import com.java110.dto.visit.VisitDto;
import com.java110.intf.accessControl.IVisitV1InnerServiceSMO;
import com.java110.intf.barrier.IBarrierV1InnerServiceSMO;
import com.java110.intf.barrier.ICarInoutTempAuthV1InnerServiceSMO;
import com.java110.intf.barrier.ICarInoutV1InnerServiceSMO;
import com.java110.intf.car.ICarBlackWhiteV1InnerServiceSMO;
import com.java110.po.carInoutTempAuth.CarInoutTempAuthPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InCarEngine extends CarEngine implements IInCarEngine {

    @Autowired
    private ICarBlackWhiteV1InnerServiceSMO carBlackWhiteServiceImpl;


    @Autowired
    private ICarInoutV1InnerServiceSMO carInoutServiceImpl;

    @Autowired
    private ICarInoutTempAuthV1InnerServiceSMO carInoutTempAuthServiceImpl;

    @Autowired
    private SendInfoEngine sendInfoEngine;

    @Autowired
    private CarInLogEngine carInLogEngine;

    @Autowired
    private JudgeOwnerCarEngine judgeOwnerCarEngine;

    @Autowired
    private IBarrierV1InnerServiceSMO machineService;

    @Autowired
    private IVisitV1InnerServiceSMO visitV1InnerServiceSMOImpl;


    /**
     * 车辆进场
     *
     * @param type       车牌类型
     * @param carNum     车牌号
     * @param machineDto 设备信息
     * @return
     */
    public ResultParkingAreaTextDto enterParkingArea(String type, String carNum, BarrierDto machineDto, List<ParkingAreaDto> parkingAreaDtos, IInOutCarTextEngine inOutCarTextEngine) throws Exception {

        InOutCarTextDto inOutCarTextDto = null;
        //1.0 判断是否为黑名单
        List<String> paIds = new ArrayList<>();
        for (ParkingAreaDto parkingAreaDto : parkingAreaDtos) {
            paIds.add(parkingAreaDto.getPaId());
        }

        CarBlackWhiteDto carBlackWhiteDto = new CarBlackWhiteDto();
        carBlackWhiteDto.setCommunityId(machineDto.getCommunityId());
        carBlackWhiteDto.setPaIds(paIds.toArray(new String[paIds.size()]));
        carBlackWhiteDto.setCarNum(carNum);
        carBlackWhiteDto.setBlackWhite(CarBlackWhiteDto.BLACK_WHITE_BLACK);
        carBlackWhiteDto.setHasValid("Y");
        List<CarBlackWhiteDto> blackWhiteDtos = carBlackWhiteServiceImpl.queryCarBlackWhites(carBlackWhiteDto);

        //黑名单车辆不能进入
        if (blackWhiteDtos != null && blackWhiteDtos.size() > 0) {
            inOutCarTextDto = inOutCarTextEngine.blackCarCannotIn(carNum, machineDto, getDefaultPaId(parkingAreaDtos));
            saveCarInInfo(carNum, machineDto, inOutCarTextDto, "开门失败", type, parkingAreaDtos, CarInoutDto.STATE_IN_FAIL);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_BLACK, inOutCarTextDto, carNum);
        }

        carBlackWhiteDto = new CarBlackWhiteDto();
        carBlackWhiteDto.setCommunityId(machineDto.getCommunityId());
        carBlackWhiteDto.setPaIds(paIds.toArray(new String[paIds.size()]));
        carBlackWhiteDto.setCarNum(carNum);
        carBlackWhiteDto.setBlackWhite(CarBlackWhiteDto.BLACK_WHITE_WHITE);
        carBlackWhiteDto.setHasValid("Y");
        blackWhiteDtos = carBlackWhiteServiceImpl.queryCarBlackWhites(carBlackWhiteDto);

        //白名单车辆进场
        if (blackWhiteDtos != null && blackWhiteDtos.size() > 0) {
            inOutCarTextDto = inOutCarTextEngine.whiteCarCanIn(carNum, machineDto, getDefaultPaId(parkingAreaDtos));
            saveCarInInfo(carNum, machineDto, inOutCarTextDto, "开门成功", type, parkingAreaDtos, CarInoutDto.STATE_IN);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, inOutCarTextDto, carNum);
        }

        //判断车辆是否为月租车
        CarDayDto carDayDto = judgeOwnerCarEngine.judgeOwnerCar(machineDto, carNum, parkingAreaDtos);
        //判断车辆是否在 场内
        CarInoutDto inoutDto = new CarInoutDto();
        inoutDto.setCarNum(carNum);
        inoutDto.setPaIds(paIds.toArray(new String[paIds.size()]));
        inoutDto.setState(CarInoutDto.STATE_IN);
        List<CarInoutDto> carInoutDtos = carInoutServiceImpl.queryCarInouts(inoutDto);
        // 临时车再场内 不让进 需要工作人员处理 手工出场
        if (carInoutDtos != null && carInoutDtos.size() > 0 && "N".equals(parkingAreaDtos.get(0).getBlueCarIn())) {
            inOutCarTextDto = inOutCarTextEngine.carInParkingArea(carNum, machineDto, getDefaultPaId(parkingAreaDtos));
            saveCarInInfo(carNum, machineDto, inOutCarTextDto, "开门失败", type, parkingAreaDtos, CarInoutDto.STATE_IN_FAIL);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_INED, inOutCarTextDto, carNum);
        }

        // 判断是否为出售车辆
        if (OwnerCarDto.LEASE_TYPE_SALE.equals(carDayDto.getLeaseType())) {
            inOutCarTextDto = inOutCarTextEngine.carInSaleCar(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            saveCarInInfo(carNum, machineDto, inOutCarTextDto, "开门成功", type, parkingAreaDtos, CarInoutDto.STATE_IN);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, inOutCarTextDto, carNum);
        }

        // 判断是否为内部车辆
        if (OwnerCarDto.LEASE_TYPE_INNER.equals(carDayDto.getLeaseType())) {
            inOutCarTextDto = inOutCarTextEngine.carInInnerCar(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            saveCarInInfo(carNum, machineDto, inOutCarTextDto, "开门成功", type, parkingAreaDtos, CarInoutDto.STATE_IN);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, inOutCarTextDto, carNum);
        }

        // 判断是否为免费车辆
        if (OwnerCarDto.LEASE_TYPE_NO_MONEY.equals(carDayDto.getLeaseType())) {
            inOutCarTextDto = inOutCarTextEngine.carInInnerNoMoney(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            saveCarInInfo(carNum, machineDto, inOutCarTextDto, "开门成功", type, parkingAreaDtos, CarInoutDto.STATE_IN);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, inOutCarTextDto, carNum);
        }

        // 说明是月租车
        if (carDayDto.getDay() > 0) {
            //小于6天时的回复
            if (carDayDto.getDay() < 6) {
                inOutCarTextDto = inOutCarTextEngine.carInLastFiveDay(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
                saveCarInInfo(carNum, machineDto, inOutCarTextDto, "开门成功", type, parkingAreaDtos, CarInoutDto.STATE_IN);
                return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, inOutCarTextDto, carNum);
            }
            inOutCarTextDto = inOutCarTextEngine.carInMonthCar(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            saveCarInInfo(carNum, machineDto, inOutCarTextDto, "开门成功", type, parkingAreaDtos, CarInoutDto.STATE_IN);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, inOutCarTextDto, carNum);
        }

        //月租车 已过期时 可以进场 只是提示未 浙CS8417，月租车，已过期
        if (carDayDto.getDay() == -2) {
            inOutCarTextDto = inOutCarTextEngine.carInMonthExpire(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            saveCarInInfo(carNum, machineDto, inOutCarTextDto, "开门成功", type, parkingAreaDtos, CarInoutDto.STATE_IN);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, inOutCarTextDto, carNum);
        }

        // todo 校验临时车 是否 允许进场
        if ("N".equals(parkingAreaDtos.get(0).getTempCarIn())) {
            inOutCarTextDto = inOutCarTextEngine.tempCarCannotIn(carNum, machineDto, getDefaultPaId(parkingAreaDtos));
            saveCarInInfo(carNum, machineDto, inOutCarTextDto, "开门失败", type, parkingAreaDtos, CarInoutDto.STATE_IN_FAIL);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_BLACK, inOutCarTextDto, carNum);
        }

        // todo 校验临时车 需要审核
        if ("Y".equals(parkingAreaDtos.get(0).getTempAuth())) {
            inOutCarTextDto = inOutCarTextEngine.tempCarAuthIn(carNum, machineDto, getDefaultPaId(parkingAreaDtos));
            // todo 保存临时车审核数据，通知HC小区管理系统 确认审核 确认审核后，再开门
            saveTempCarAuthCarInout(carNum, machineDto, inOutCarTextDto, type, parkingAreaDtos);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_BLACK, inOutCarTextDto, carNum);
        }

        // todo 车辆访客记录校验
        if ("V".equals(parkingAreaDtos.get(0).getTempCarIn()) && noVisitInfo(carNum,machineDto)) {
            inOutCarTextDto = inOutCarTextEngine.visitCarAuthIn(carNum, machineDto, getDefaultPaId(parkingAreaDtos));
            // todo 暂存临时车数据，通知界面临时车需要登记访客信息
            saveVisitCarAuthCarInout(carNum, machineDto, inOutCarTextDto, type, parkingAreaDtos);
            saveCarInInfo(carNum, machineDto, inOutCarTextDto, "开门失败", type, parkingAreaDtos, CarInoutDto.STATE_IN_FAIL);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_BLACK, inOutCarTextDto, carNum);
        }


        inOutCarTextDto = inOutCarTextEngine.carInTempCar(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
        saveCarInInfo(carNum, machineDto, inOutCarTextDto, "开门成功", type, parkingAreaDtos, CarInoutDto.STATE_IN);
        return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, inOutCarTextDto, carNum);
    }



    @Override
    public void tempCarAuthOpen(CarInoutTempAuthDto carInoutTempAuthDto) throws Exception {


        BarrierDto machineDto = new BarrierDto();
        machineDto.setMachineId(carInoutTempAuthDto.getMachineId());
        machineDto.setCommunityId(carInoutTempAuthDto.getCommunityId());
        List<BarrierDto> machineDtos = machineService.queryBarriers(machineDto);

        Assert.listOnlyOne(machineDtos, "道闸不存在");
        machineDto = machineDtos.get(0);
        machineDto.setPhotoJpg(carInoutTempAuthDto.getPhoneJpg());
        // todo 目前只有臻识 这里 目前写死
        IInOutCarTextEngine inOutCarTextEngine = ApplicationContextFactory.getBean("zhenshiMqttInOutCarTextEngine", IInOutCarTextEngine.class);

        InOutCarTextDto inOutCarTextDto = inOutCarTextEngine.carInTempCar(carInoutTempAuthDto.getCarNum(), machineDtos.get(0), carInoutTempAuthDto.getPaId(), new CarDayDto(carInoutTempAuthDto.getCarNum(), "I", -1));

        BarrierGateControlDto barrierGateControlDto
                = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carInoutTempAuthDto.getCarNum(), machineDto, inOutCarTextDto.getRemark(), "开门成功");
        sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getBoxId(), machineDto);
        //保存 进场记录
        carInLogEngine.saveCarInLog(carInoutTempAuthDto.getCarNum(), carInoutTempAuthDto.getCarType(), machineDto, carInoutTempAuthDto.getPaId(), CarInoutDto.STATE_IN, inOutCarTextDto.getRemark());
        //播放语音
        ResultParkingAreaTextDto parkingAreaTextDto
                = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, inOutCarTextDto.getText1(),
                inOutCarTextDto.getText2(), inOutCarTextDto.getText3(), inOutCarTextDto.getText4(), inOutCarTextDto.getVoice(), carInoutTempAuthDto.getCarNum());
        //播放
        CarMachineProcessFactory.getCarImpl(machineDto.getHmId()).openDoor(machineDto, parkingAreaTextDto);

    }

    /**
     * 临时车 进入审核 数据保存
     *
     * @param carNum          临时车
     * @param machineDto      设备
     * @param inOutCarTextDto 进场提示
     * @param type            类型
     * @param parkingAreaDtos
     */
    private void saveTempCarAuthCarInout(String carNum, BarrierDto machineDto, InOutCarTextDto inOutCarTextDto, String type, List<ParkingAreaDto> parkingAreaDtos) {
        CarInoutTempAuthPo carInoutTempAuthPo = new CarInoutTempAuthPo();
        carInoutTempAuthPo.setAuthId(GenerateCodeFactory.getGeneratorId("11"));
        carInoutTempAuthPo.setCarType(type);
        carInoutTempAuthPo.setMachineCode(machineDto.getMachineCode());
        carInoutTempAuthPo.setMachineId(machineDto.getMachineId());
        carInoutTempAuthPo.setMachineName(machineDto.getMachineName());
        carInoutTempAuthPo.setPhoneJpg(machineDto.getPhotoJpg());
        carInoutTempAuthPo.setCommunityId(machineDto.getCommunityId());
        carInoutTempAuthPo.setPaId(getDefaultPaId(parkingAreaDtos));
        carInoutTempAuthPo.setPaNum(getDefaultPaNum(parkingAreaDtos));
        carInoutTempAuthPo.setState(CarInoutTempAuthDto.STATE_W);
        carInoutTempAuthPo.setRemark(inOutCarTextDto.getRemark());
        carInoutTempAuthPo.setCarNum(carNum);
        carInoutTempAuthServiceImpl.saveCarInoutTempAuth(carInoutTempAuthPo);


        BarrierGateControlDto barrierGateControlDto
                = new BarrierGateControlDto();
        barrierGateControlDto.setAction(BarrierGateControlDto.ACTION_TEMP_CAR_AUTH);
        barrierGateControlDto.setMachineId(carInoutTempAuthPo.getMachineId());
        barrierGateControlDto.setBody(carInoutTempAuthPo);

        try {
            sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getBoxId(), machineDto);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 保存临时车进入信息
     * @param carNum
     * @param machineDto
     * @param inOutCarTextDto
     * @param type
     * @param parkingAreaDtos
     */
    private void saveVisitCarAuthCarInout(String carNum, BarrierDto machineDto, InOutCarTextDto inOutCarTextDto, String type, List<ParkingAreaDto> parkingAreaDtos) {
        CarInoutTempAuthPo carInoutTempAuthPo = new CarInoutTempAuthPo();
        carInoutTempAuthPo.setCarType(type);
        carInoutTempAuthPo.setMachineCode(machineDto.getMachineCode());
        carInoutTempAuthPo.setMachineId(machineDto.getMachineId());
        carInoutTempAuthPo.setMachineName(machineDto.getMachineName());
        carInoutTempAuthPo.setPhoneJpg(machineDto.getPhotoJpg());
        carInoutTempAuthPo.setCommunityId(machineDto.getCommunityId());
        carInoutTempAuthPo.setPaId(getDefaultPaId(parkingAreaDtos));
        carInoutTempAuthPo.setPaNum(getDefaultPaNum(parkingAreaDtos));
        carInoutTempAuthPo.setState(CarInoutTempAuthDto.STATE_W);
        carInoutTempAuthPo.setRemark(inOutCarTextDto.getRemark());
        carInoutTempAuthPo.setCarNum(carNum);
        CommonCache.setValue(machineDto.getMachineId()+"_inCarEngine", JSONObject.toJSONString(carInoutTempAuthPo),15 * 60);
        CommonCache.setValue(carNum+"_inCarEngine", JSONObject.toJSONString(carInoutTempAuthPo),15 * 60);

    }


    private void saveCarInInfo(String carNum, BarrierDto machineDto, InOutCarTextDto inOutCarTextDto, String openStats, String type, List<ParkingAreaDto> parkingAreaDtos, String stateInFail) throws Exception {
        BarrierGateControlDto barrierGateControlDto
                = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, inOutCarTextDto.getRemark(), openStats);
        sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getBoxId(), machineDto);
        //保存 进场记录
        carInLogEngine.saveCarInLog(carNum, type, machineDto, parkingAreaDtos, stateInFail, inOutCarTextDto.getRemark());
    }


    private boolean noVisitInfo(String carNum, BarrierDto machineDto) {

        VisitDto visitDto = new VisitDto();
        visitDto.setCarNum(carNum);
        visitDto.setCommunityId(machineDto.getCommunityId());
        visitDto.setState(VisitDto.STATE_C);
        visitDto.setValidTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        List<VisitDto> visitDtos = visitV1InnerServiceSMOImpl.queryVisits(visitDto);

        if(!ListUtil.isNull(visitDtos)){
            return false;
        }

        return true;
    }

}
