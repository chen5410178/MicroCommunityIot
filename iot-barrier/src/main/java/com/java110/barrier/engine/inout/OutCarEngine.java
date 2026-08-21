package com.java110.barrier.engine.inout;

import com.java110.barrier.engine.CarInoutEvasionEngine;
import com.java110.barrier.engine.IComputeTempCarFee;
import com.java110.barrier.engine.IInOutCarTextEngine;
import com.java110.barrier.engine.IOutCarEngine;
import com.java110.barrier.factory.BarrierGateControlDto;
import com.java110.barrier.factory.TempCarFeeFactory;
import com.java110.bean.dto.car.OwnerCarDto;
import com.java110.bean.dto.coupon.ParkingCouponCarDto;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.dto.fee.TempCarFeeConfigDto;
import com.java110.dto.fee.TempCarFeeResult;
import com.java110.dto.parking.*;
import com.java110.dto.payment.CarInoutPaymentDto;
import com.java110.intf.barrier.ICarInoutPaymentV1InnerServiceSMO;
import com.java110.intf.barrier.ICarInoutV1InnerServiceSMO;
import com.java110.intf.barrier.IParkingBoxV1InnerServiceSMO;
import com.java110.intf.barrier.ITempCarFeeConfigV1InnerServiceSMO;
import com.java110.intf.car.IOwnerCarV1InnerServiceSMO;
import com.java110.intf.car.IParkingAreaV1InnerServiceSMO;
import com.java110.intf.car.IParkingCouponCarV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OutCarEngine extends CarEngine implements IOutCarEngine {

    @Autowired
    JudgeOwnerCarEngine judgeOwnerCarEngine;

    @Autowired
    private ICarInoutV1InnerServiceSMO carInoutServiceImpl;

    @Autowired
    private SendInfoEngine sendInfoEngine;

    @Autowired
    private IParkingBoxV1InnerServiceSMO parkingBoxServiceImpl;

    @Autowired
    private JudgeWhiteCarEngine judgeWhiteCarEngine;

    @Autowired
    private CarOutLogEngine carOutLogEngine;

    @Autowired
    private SelectTempFeeConfigEngine selectTempFeeConfigEngine;


    @Autowired
    private ITempCarFeeConfigV1InnerServiceSMO tempCarFeeConfigServiceImpl;

    @Autowired
    private IParkingCouponCarV1InnerServiceSMO parkingCouponCarServiceImpl;

    @Autowired
    private IOwnerCarV1InnerServiceSMO ownerCarV1InnerServiceSMOImpl;

    @Autowired
    private ICarInoutPaymentV1InnerServiceSMO carInoutPaymentV1InnerServiceSMOImpl;

    @Autowired
    private CarInoutEvasionEngine carInoutEvasionEngine;


    @Override
    public ResultParkingAreaTextDto outParkingArea(String type, String carNum, BarrierDto machineDto,
                                                   List<ParkingAreaDto> parkingAreaDtos, IInOutCarTextEngine inOutCarTextEngine) throws Exception {

        InOutCarTextDto inOutCarTextDto = null;
        //查询进场记录
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(carNum);
        carInoutDto.setPaId(getDefaultPaId(parkingAreaDtos));
        carInoutDto.setCommunityId(machineDto.getCommunityId());
        carInoutDto.setStates(new String[]{CarInoutDto.STATE_IN, CarInoutDto.STATE_PAY});
        List<CarInoutDto> carInoutDtos = carInoutServiceImpl.queryCarInouts(carInoutDto);
        CarDayDto carDayDto = judgeOwnerCarEngine.judgeOwnerCarOut(machineDto, carNum, parkingAreaDtos);

        // 场景2：无进场记录时尝试补录进场记录
        if (ListUtil.isNull(carInoutDtos)) {
            carInoutDtos = carInoutEvasionEngine.tryFillEntryAndReQuery(
                carNum, machineDto, getDefaultPaId(parkingAreaDtos));
        }

        if (ListUtil.isNull(carInoutDtos)) {
            //只有临时车 才 不让出，因为 摄像头 车牌识别错的概率比较大
            if ("N".equals(parkingAreaDtos.get(0).getYelowCarIn()) &&  OwnerCarDto.LEASE_TYPE_TEMP.equals(carDayDto.getLeaseType())) {
                inOutCarTextDto = inOutCarTextEngine.carNotInParkingArea(carNum, machineDto, getDefaultPaId(parkingAreaDtos));
                saveCarOutInfo(carNum, machineDto, inOutCarTextDto, 0, "开门失败", null, parkingAreaDtos, CarInoutDto.STATE_IN_FAIL);
                return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_NO_IN, inOutCarTextDto, carNum);
            } else {
                inOutCarTextDto = inOutCarTextEngine.carNotInParkingAreaCanOut(carNum, machineDto, getDefaultPaId(parkingAreaDtos));
                saveCarOutInfo(carNum, machineDto, inOutCarTextDto, 0, "开门成功", null, parkingAreaDtos, CarInoutDto.STATE_OUT);
                return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_FREE_CAR_OUT_SUCCESS, inOutCarTextDto, carNum);
            }
        }

        // todo 输入支付时间
        freshPayTime(carInoutDtos);

        //判断是否为黑名单
        if (judgeWhiteCarEngine.judgeBlackCar(machineDto, carNum, parkingAreaDtos, type, carInoutDtos)) {
            inOutCarTextDto = inOutCarTextEngine.blackCarOut(carNum, machineDto, getDefaultPaId(parkingAreaDtos));
            saveCarOutInfo(carNum, machineDto, inOutCarTextDto, 0, "开门失败", carInoutDtos.get(0), parkingAreaDtos, CarInoutDto.STATE_IN_FAIL);

            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_ERROR, inOutCarTextDto, carNum);
        }
        //判断是否为白名单
        if (judgeWhiteCarEngine.judgeWhiteCar(machineDto, carNum, parkingAreaDtos, type, carInoutDtos)) {
            inOutCarTextDto = inOutCarTextEngine.whiteCarOut(carNum, machineDto, getDefaultPaId(parkingAreaDtos));
            saveCarOutInfo(carNum, machineDto, inOutCarTextDto, 0, "开门成功", carInoutDtos.get(0), parkingAreaDtos, CarInoutDto.STATE_OUT);

            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_FREE_CAR_OUT_SUCCESS, inOutCarTextDto, carNum);
        }


        // todo 判断是否为内部车辆
        if (OwnerCarDto.LEASE_TYPE_INNER.equals(carDayDto.getLeaseType())) {
            inOutCarTextDto = inOutCarTextEngine.carOutInnerCar(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            saveCarOutInfo(carNum, machineDto, inOutCarTextDto, 0, "开门成功", carInoutDtos.get(0), parkingAreaDtos, CarInoutDto.STATE_OUT);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, inOutCarTextDto, carNum);
        }

        // todo 判断是否为免费车辆
        if (OwnerCarDto.LEASE_TYPE_NO_MONEY.equals(carDayDto.getLeaseType())) {
            inOutCarTextDto = inOutCarTextEngine.carOutInnerNoMoney(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            saveCarOutInfo(carNum, machineDto, inOutCarTextDto, 0, "开门成功", carInoutDtos.get(0), parkingAreaDtos, CarInoutDto.STATE_OUT);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, inOutCarTextDto, carNum);
        }

        // todo 如果是共享岗亭，则判断月租车或者出售车辆是否进入过它自己的停车场
        boolean cameInParking = true;
        //cameInParking = comeInParking(carNum, getDefaultPaId(parkingAreaDtos), machineDto.getCommunityId(), carInoutDtos.get(0));

        // todo 判断是否为出售车辆
        if (OwnerCarDto.LEASE_TYPE_SALE.equals(carDayDto.getLeaseType()) && cameInParking) {
            inOutCarTextDto = inOutCarTextEngine.carOutSaleCar(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            saveCarOutInfo(carNum, machineDto, inOutCarTextDto, 0, "开门成功", carInoutDtos.get(0), parkingAreaDtos, CarInoutDto.STATE_OUT);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, inOutCarTextDto, carNum);
        }

        //todo 说明是--------------------------------------------------------------------月租车---------------------------------------------------------------
        if (carDayDto.getDay() > 0 && cameInParking) {
            inOutCarTextDto = inOutCarTextEngine.carOutMonthCar(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            saveCarOutInfo(carNum, machineDto, inOutCarTextDto, 0, "开门成功", carInoutDtos.get(0), parkingAreaDtos, CarInoutDto.STATE_OUT);
            ResultParkingAreaTextDto resultParkingAreaTextDto
                    = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_MONTH_CAR_OUT_SUCCESS, inOutCarTextDto, carNum);
            resultParkingAreaTextDto.setDay(carDayDto.getDay());
            return resultParkingAreaTextDto;
        }

        if (carDayDto.getDay() == -2 && cameInParking) {
            inOutCarTextDto = inOutCarTextEngine.carOutMonthCarExpire(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            saveCarOutInfo(carNum, machineDto, inOutCarTextDto, 0, "开门失败", carInoutDtos.get(0), parkingAreaDtos, CarInoutDto.STATE_IN_FAIL);
            ResultParkingAreaTextDto resultParkingAreaTextDto
                    = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_ERROR, inOutCarTextDto, carNum);
            resultParkingAreaTextDto.setDay(0);
            return resultParkingAreaTextDto;
        }

        //todo 说明是--------------------------------------------------------------------临时车---------------------------------------------------------------

        //判断岗亭是否收费
        ParkingBoxDto parkingBoxDto = new ParkingBoxDto();
        parkingBoxDto.setBoxId(machineDto.getBoxId());
        parkingBoxDto.setCommunityId(machineDto.getCommunityId());
        List<ParkingBoxDto> parkingBoxDtos = parkingBoxServiceImpl.queryParkingBoxs(parkingBoxDto);

        Assert.listOnlyOne(parkingBoxDtos, "设备不存在 岗亭");

        if (!"Y".equals(parkingBoxDtos.get(0).getFee())) {
            inOutCarTextDto = inOutCarTextEngine.carOutConfigError(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            saveCarOutInfo(carNum, machineDto, inOutCarTextDto, 0, "开门成功", carInoutDtos.get(0), parkingAreaDtos, CarInoutDto.STATE_OUT);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_SUCCESS, inOutCarTextDto, carNum);
        }


        // 说明完成支付
        if (TempCarFeeFactory.judgeFinishPayTempCarFee(carInoutDtos.get(0))) {
            inOutCarTextDto = inOutCarTextEngine.carOutFinishPayFee(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            saveCarOutInfo(carNum, machineDto, inOutCarTextDto, 0, "开门成功", carInoutDtos.get(0), parkingAreaDtos, CarInoutDto.STATE_OUT);

            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_TEMP_CAR_OUT_SUCCESS, inOutCarTextDto, carNum);
        }

        //未缴费
//        TempCarFeeConfigDto tempCarFeeConfigDto = new TempCarFeeConfigDto();
//        tempCarFeeConfigDto.setPaId(getDefaultPaId(parkingAreaDtos));
//        tempCarFeeConfigDto.setCommunityId(carInoutDtos.get(0).getCommunityId());
//        List<TempCarFeeConfigDto> tempCarFeeConfigDtos = tempCarFeeConfigServiceImpl.queryTempCarFeeConfigs(tempCarFeeConfigDto);

        TempCarFeeConfigDto tempCarFeeConfigDtos= selectTempFeeConfigEngine.getTempCarFeeConfig(carInoutDtos.get(0),getDefaultPaId(parkingAreaDtos));

        if (tempCarFeeConfigDtos == null) {
            BarrierGateControlDto barrierGateControlDto
                    = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, 0, carInoutDtos.get(0), "未配置临时车收费规则", "开门失败");
            sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getBoxId(), machineDto);
            carOutLogEngine.saveCarOutLog(carNum, machineDto, parkingAreaDtos, CarInoutDto.STATE_IN_FAIL, "未配置临时车收费规则");
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_NO_PRI, "临时车无权限");
        }

        //考虑停车劵
        List<ParkingCouponCarDto> parkingCouponOwnerCarDtos = getCarCoupons(carNum, tempCarFeeConfigDtos.getPaId());

        IComputeTempCarFee computeTempCarFee = ApplicationContextFactory.getBean(tempCarFeeConfigDtos.getRuleId(), IComputeTempCarFee.class);
        TempCarFeeResult result = computeTempCarFee.computeTempCarFee(carInoutDtos.get(0), tempCarFeeConfigDtos, getDurationCarCoupons(parkingCouponOwnerCarDtos));

        // 停车劵处理 这里主要处理 打折 金额
        double amount = dealParkingCouponCar(result.getPayCharge(), parkingCouponOwnerCarDtos);
        result.setPayCharge(amount);

        //不收费，直接出场
        if (result.getPayCharge() == 0) {
            inOutCarTextDto = inOutCarTextEngine.carOutFinishPayFee(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            //刷入停车劵到车辆出场
            refreshParkingCouponToCarInout(parkingCouponOwnerCarDtos, carInoutDtos.get(0));
            saveCarOutInfo(carNum, machineDto, inOutCarTextDto, 0, "开门成功", carInoutDtos.get(0), parkingAreaDtos, CarInoutDto.STATE_OUT);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_SUCCESS, inOutCarTextDto, carNum);
        }
        inOutCarTextDto = inOutCarTextEngine.carOutNeedPayFee(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto, result);
        saveCarOutInfo(carNum, machineDto, inOutCarTextDto, result.getPayCharge(), "开门失败", carInoutDtos.get(0), parkingAreaDtos, CarInoutDto.STATE_IN_FAIL);
        //分钟
        ResultParkingAreaTextDto resultParkingAreaTextDto = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_ERROR,
                inOutCarTextDto, carNum);
        resultParkingAreaTextDto.setHours(result.getHours());
        resultParkingAreaTextDto.setMin(result.getMin());
        resultParkingAreaTextDto.setPayCharge(result.getPayCharge());
        return resultParkingAreaTextDto;
    }

    private void freshPayTime(List<CarInoutDto> carInoutDtos) {

        if (ListUtil.isNull(carInoutDtos)) {
            return;
        }

        if (!CarInoutDto.STATE_PAY.equals(carInoutDtos.get(0).getState())) {
            return;
        }

        CarInoutPaymentDto carInoutPaymentDto = new CarInoutPaymentDto();
        carInoutPaymentDto.setInoutId(carInoutDtos.get(0).getInoutId());
        carInoutPaymentDto.setCommunityId(carInoutDtos.get(0).getCommunityId());
        List<CarInoutPaymentDto> carInoutPaymentDtos = carInoutPaymentV1InnerServiceSMOImpl.queryCarInoutPayments(carInoutPaymentDto);
        if (ListUtil.isNull(carInoutPaymentDtos)) {
            return;
        }
        carInoutDtos.get(0).setPayTime(carInoutPaymentDtos.get(0).getCreateTime());

    }

    /**
     * 月租车和出售车是否进入到自己的停车场
     *
     * @param carNum
     * @param defaultPaId
     * @param communityId
     * @return
     */
    private boolean comeInParking(String carNum, String defaultPaId, String communityId, CarInoutDto carInoutDto) {

        //todo 查询车辆停车场
        OwnerCarDto ownerCarDto = new OwnerCarDto();
        ownerCarDto.setCarNum(carNum);
        ownerCarDto.setCommunityId(communityId);
        List<OwnerCarDto> ownerCarDtos = ownerCarV1InnerServiceSMOImpl.queryOwnerCars(ownerCarDto);

        if (ListUtil.isNull(ownerCarDtos)) {
            return true;
        }

        if (defaultPaId.equals(ownerCarDtos.get(0).getPaId())) {
            return true;
        }

        String carInTime = carInoutDto.getInTime();
        //todo 总停车时间
        long totalMillSec = DateUtil.mulMillSecA(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A), carInTime);

        // todo 如果总时间未超过两个小时那么就不做判断
        if (totalMillSec < 2 * 60 * 60 * 1000) {
            return true;
        }


        CarInoutDto inCarInoutDto = new CarInoutDto();
        inCarInoutDto.setCarNum(carNum);
        inCarInoutDto.setPaId(ownerCarDtos.get(0).getPaId());
        inCarInoutDto.setCommunityId(communityId);
        inCarInoutDto.setCarInout(CarInoutDto.CAR_INOUT_OUT);
        inCarInoutDto.setState(CarInoutDto.STATE_OUT);
        inCarInoutDto.setStartTime(carInTime);
        inCarInoutDto.setEndTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        List<CarInoutDto> carInoutDtos = carInoutServiceImpl.queryCarInouts(inCarInoutDto);
        //todo 说明车辆没有下地下停车场
        if (ListUtil.isNull(carInoutDtos)) {
            return false;
        }

        //todo 判断时间是否超过

        long inMillSec = 0;
        for (CarInoutDto tmpOutCarInoutDto : carInoutDtos) {
            inMillSec += DateUtil.mulMillSecA(tmpOutCarInoutDto.getOpenTime(), tmpOutCarInoutDto.getInTime());
        }

        long midMisSec = totalMillSec - inMillSec;
        // todo 如果外部停车场 停时间超过 4个小时就按
        if (midMisSec > 2 * 60 * 60 * 1000) {
            return false;
        }

        return true;

    }

    /**
     * 刷入停车劵
     *
     * @param parkingCouponOwnerCarDtos
     * @param carInoutDto
     */
    private void refreshParkingCouponToCarInout(List<ParkingCouponCarDto> parkingCouponOwnerCarDtos, CarInoutDto carInoutDto) {

        List<ParkingCouponCarDto> tmpParkingCouponCarDtos = new ArrayList<>();

        if (ListUtil.isNull(parkingCouponOwnerCarDtos)) {
            carInoutDto.setParkingCouponCarDtos(tmpParkingCouponCarDtos);
            return;
        }

        for (ParkingCouponCarDto tmpParkingCouponCarDto : parkingCouponOwnerCarDtos) {
            if (!tmpParkingCouponCarDto.isHasUser()) {
                continue;
            }
            tmpParkingCouponCarDtos.add(tmpParkingCouponCarDto);
        }

        carInoutDto.setParkingCouponCarDtos(tmpParkingCouponCarDtos);
    }

    /**
     * 获取时长优惠券
     *
     * @param parkingCouponOwnerCarDtos
     * @return
     */
    private List<ParkingCouponCarDto> getDurationCarCoupons(List<ParkingCouponCarDto> parkingCouponOwnerCarDtos) {

        List<ParkingCouponCarDto> tmpParkingCouponCarDtos = new ArrayList<>();

        if (parkingCouponOwnerCarDtos == null || parkingCouponOwnerCarDtos.size() < 1) {
            return tmpParkingCouponCarDtos;
        }

        for (ParkingCouponCarDto parkingCouponOwnerCarDto : parkingCouponOwnerCarDtos) {
            if (ParkingCouponCarDto.TYPE_CD_HOURS.equals(parkingCouponOwnerCarDto.getTypeCd())
                    || ParkingCouponCarDto.TYPE_CD_FREE.equals(parkingCouponOwnerCarDto.getTypeCd())
            ) {
                tmpParkingCouponCarDtos.add(parkingCouponOwnerCarDto);
            }
        }

        return tmpParkingCouponCarDtos;
    }

    private List<ParkingCouponCarDto> getCarCoupons(String carNum, String paId) {

        ParkingCouponCarDto parkingCouponOwnerCarDto = new ParkingCouponCarDto();
        parkingCouponOwnerCarDto.setCarNum(carNum);
        parkingCouponOwnerCarDto.setPaId(paId);
        parkingCouponOwnerCarDto.setState(ParkingCouponCarDto.STATE_W);
        List<ParkingCouponCarDto> parkingCouponOwnerCarDtos = parkingCouponCarServiceImpl.queryParkingCouponCars(parkingCouponOwnerCarDto);

        if (ListUtil.isNull(parkingCouponOwnerCarDtos)) {
            parkingCouponOwnerCarDtos = new ArrayList<>();
        }

        return parkingCouponOwnerCarDtos;
    }

    private void saveCarOutInfo(String carNum,
                                BarrierDto machineDto,
                                InOutCarTextDto inOutCarTextDto,
                                double payCharge,
                                String openStats,
                                CarInoutDto carInoutDto,
                                List<ParkingAreaDto> parkingAreaDtos,
                                String stateInFail) throws Exception {
        BarrierGateControlDto barrierGateControlDto
                = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, payCharge, carInoutDto, inOutCarTextDto.getRemark(), openStats);
        sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getBoxId(), machineDto);
        carOutLogEngine.saveCarOutLog(carNum, machineDto, parkingAreaDtos, stateInFail, inOutCarTextDto.getRemark(), carInoutDto == null ? null : carInoutDto.getParkingCouponCarDtos());
    }


    /**
     * 停车劵处理 这里主要处理 全免 打折 金额
     *
     * @param payCharge
     * @param parkingCouponCarDtos
     * @return
     */
    public double dealParkingCouponCar(double payCharge, List<ParkingCouponCarDto> parkingCouponCarDtos) {


        if (ListUtil.isNull(parkingCouponCarDtos)) {
            return payCharge;
        }

        if (payCharge == 0) {
            return payCharge;
        }

        BigDecimal amountDec = new BigDecimal(payCharge);

        double couponAmount = 0.0;

        for (ParkingCouponCarDto parkingCouponCarDto : parkingCouponCarDtos) {
            try {
                if (ParkingCouponCarDto.TYPE_CD_HOURS.equals(parkingCouponCarDto.getTypeCd())) {
                    continue;
                }
                if (ParkingCouponCarDto.TYPE_CD_FREE.equals(parkingCouponCarDto.getTypeCd())) {
                    continue;
                }
                parkingCouponCarDto.setHasUser(true);

                couponAmount = Double.parseDouble(parkingCouponCarDto.getValue());

                if (ParkingCouponCarDto.TYPE_CD_MONEY.equals(parkingCouponCarDto.getTypeCd())) {
                    if (amountDec.doubleValue() < couponAmount) {
                        return 0;
                    }
                    amountDec = amountDec.subtract(new BigDecimal(couponAmount));
                }

                if (ParkingCouponCarDto.TYPE_CD_DISCOUNT.equals(parkingCouponCarDto.getTypeCd())) {
                    if (amountDec.doubleValue() < couponAmount) {
                        return 0;
                    }
                    amountDec = amountDec.multiply(new BigDecimal(couponAmount)).setScale(2, BigDecimal.ROUND_HALF_UP);
                }
            } catch (Exception e) {
                //logger.error("停车劵 处理失败",e);
                e.printStackTrace();
            }

        }


        return amountDec.doubleValue();
    }
}
