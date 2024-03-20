package com.java110.barrier.engine.compute;

import com.java110.barrier.factory.TempCarFeeFactory;
import com.java110.bean.dto.coupon.ParkingCouponCarDto;
import com.java110.core.utils.DateUtil;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.dto.fee.TempCarFeeConfigAttrDto;
import com.java110.dto.fee.TempCarFeeConfigDto;
import com.java110.dto.fee.TempCarFeeResult;
import com.java110.dto.payment.CarInoutPaymentDto;
import com.java110.intf.barrier.ICarInoutPaymentV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Component(value = "6700012008")
public class TimePeriod3ComputeTempCarFeeImpl extends BaseComputeTempCarFee {


    @Autowired
    private ICarInoutPaymentV1InnerServiceSMO carInoutPaymentV1InnerServiceSMOImpl;

    /**
     5600012049	开始时段1
     5600012050	结束时段1
     5600012051	免费时间1(分钟)
     5600012052	最高收费1
     5600012053	首段时间1(分钟)
     5600012054	首段收费1
     5600012055	超过首段1(分钟)
     5600012056	超过首段收费1
     5600012057	开始时段2
     5600012058	结束时段2
     5600012059	免费时间2(分钟)
     5600012060	最高收费2
     5600012061	首段时间2(分钟)
     5600012062	首段收费2
     5600012063	超过首段2(分钟)
     5600012064	超过首段收费2
     */
    public static final String SPEC_CD_5600012049 = "5600012049";
    public static final String SPEC_CD_5600012050 = "5600012050";
    public static final String SPEC_CD_5600012051 = "5600012051";
    public static final String SPEC_CD_5600012052 = "5600012052";
    public static final String SPEC_CD_5600012053 = "5600012053";
    public static final String SPEC_CD_5600012054 = "5600012054";
    public static final String SPEC_CD_5600012055 = "5600012055";

    public static final String SPEC_CD_5600012056 = "5600012056";
    public static final String SPEC_CD_5600012057 = "5600012057";
    public static final String SPEC_CD_5600012058 = "5600012058";

    public static final String SPEC_CD_5600012059 = "5600012059";
    public static final String SPEC_CD_5600012060 = "5600012060";

    public static final String SPEC_CD_5600012061 = "5600012061";
    public static final String SPEC_CD_5600012062 = "5600012062";

    public static final String SPEC_CD_5600012063 = "5600012063";
    public static final String SPEC_CD_5600012064 = "5600012064";

    @Override
    public TempCarFeeResult doCompute(CarInoutDto carInoutDto, TempCarFeeConfigDto tempCarFeeConfigDto, List<TempCarFeeConfigAttrDto> tempCarFeeConfigAttrDtos, List<ParkingCouponCarDto> parkingCouponCarDtos) {

        //todo 24小时内收费就不在收费

        Date nTime = DateUtil.getCurrentDate();
        String startTime = DateUtil.getAddDayStringA(nTime,-1);
        String endTime = DateUtil.getFormatTimeStringA(nTime);

        CarInoutPaymentDto carInoutPaymentDto = new CarInoutPaymentDto();
        carInoutPaymentDto.setCarNum(carInoutDto.getCarNum());
        carInoutPaymentDto.setPaId(carInoutDto.getPaId());
        carInoutPaymentDto.setStartTime(startTime);
        carInoutPaymentDto.setEndTime(endTime);
       int count =  carInoutPaymentV1InnerServiceSMOImpl.queryCarInoutPaymentsCount(carInoutPaymentDto);

       if(count > 0){
           return new TempCarFeeResult(carInoutDto.getCarNum(), 0.0, 0.0, 0.0);
       }

        //todo 计算 应该采用那个时段的设置

        String startTime1 = TempCarFeeFactory.getAttrValueString(tempCarFeeConfigAttrDtos, SPEC_CD_5600012049);
        String endTime1 = TempCarFeeFactory.getAttrValueString(tempCarFeeConfigAttrDtos, SPEC_CD_5600012050);
        String inTime = carInoutDto.getInTime();

        if (compareTime(startTime1, endTime1, inTime) || compareTime(startTime1, endTime1, DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A))) {
            //todo 走时段1 的设置
            return doComputeOne(carInoutDto, tempCarFeeConfigDto, tempCarFeeConfigAttrDtos, parkingCouponCarDtos);
        } else {
            // todo 时段2 的设置
            return doComputeTwo(carInoutDto, tempCarFeeConfigDto, tempCarFeeConfigAttrDtos, parkingCouponCarDtos);
        }

    }

    /**
     * 5600012049	开始时间1
     * 5600012050	结束时间1
     * 5600012051	免费时间1(分钟)
     * 5600012052	最高收费1
     * 5600012053	首段时间1(分钟)
     * 5600012054	首段收费1
     * 5600012055	超过首段1(分钟)
     * 5600012056	超过首段收费1
     */
    private TempCarFeeResult doComputeTwo(CarInoutDto carInoutDto, TempCarFeeConfigDto tempCarFeeConfigDto, List<TempCarFeeConfigAttrDto> tempCarFeeConfigAttrDtos, List<ParkingCouponCarDto> parkingCouponCarDtos) {
        //获取停车时间
        long min = TempCarFeeFactory.getTempCarCeilMin(carInoutDto, parkingCouponCarDtos);

        int freeMin = TempCarFeeFactory.getAttrValueInt(tempCarFeeConfigAttrDtos, SPEC_CD_5600012051);

        //最大收费
        double maxFeeMoney = TempCarFeeFactory.getAttrValueDouble(tempCarFeeConfigAttrDtos, SPEC_CD_5600012052);
        //判断 时间是否超过24小时
        double baseMoney = 0.0;

        //免费时间中
        if (min <= freeMin && !TempCarFeeFactory.judgeFinishPayTempCarFee(carInoutDto)) {
            return new TempCarFeeResult(carInoutDto.getCarNum(), 0.0, maxFeeMoney, baseMoney);
        }

        BigDecimal minDeci = new BigDecimal(min);

        //处理超过 一天的数据
        if (min > 24 * 60) {
            BigDecimal dayDeci = minDeci.divide(new BigDecimal(24 * 60), 0, BigDecimal.ROUND_DOWN);
            baseMoney = dayDeci.multiply(new BigDecimal(maxFeeMoney)).doubleValue();

            minDeci = minDeci.subtract(dayDeci.multiply(new BigDecimal(24 * 60))).setScale(0, BigDecimal.ROUND_DOWN);
            min = minDeci.intValue();
        }

        // 特殊情况  好几天后刚好 min为0
        if (min == 0) {
            return new TempCarFeeResult(carInoutDto.getCarNum(), 0.0, maxFeeMoney, baseMoney);
        }

        int firstMin = TempCarFeeFactory.getAttrValueInt(tempCarFeeConfigAttrDtos, SPEC_CD_5600012053);
        double firstMoney = TempCarFeeFactory.getAttrValueDouble(tempCarFeeConfigAttrDtos, SPEC_CD_5600012054);
        //在首段时长(分钟)中
        if (min < firstMin) {
            return new TempCarFeeResult(carInoutDto.getCarNum(), firstMoney, maxFeeMoney, baseMoney);
        }

        //超过手段
        int afterMin = TempCarFeeFactory.getAttrValueInt(tempCarFeeConfigAttrDtos, SPEC_CD_5600012055);
        double afterByMoney = TempCarFeeFactory.getAttrValueDouble(tempCarFeeConfigAttrDtos, SPEC_CD_5600012056);


        //超过的时间
        BigDecimal afterFirstMin = minDeci.subtract(new BigDecimal(firstMin));
        //时间差 除以 没多少分钟 向上取整
        double money = afterFirstMin.divide(new BigDecimal(afterMin), 0, BigDecimal.ROUND_UP)
                .multiply(new BigDecimal(afterByMoney))
                .setScale(2, BigDecimal.ROUND_HALF_UP).add(new BigDecimal(firstMoney)).doubleValue();
        return new TempCarFeeResult(carInoutDto.getCarNum(), money, maxFeeMoney, baseMoney);
    }

    /*
     * 5600012057	开始时间2
     * 5600012058	结束时间2
     * 5600012059	免费时间2(分钟)
     * 5600012060	最高收费2
     * 5600012061	首段时间2(分钟)
     * 5600012062	首段收费2
     * 5600012063	超过首段2(分钟)
     * 5600012064	超过首段收费2
     */
    private TempCarFeeResult doComputeOne(CarInoutDto carInoutDto, TempCarFeeConfigDto tempCarFeeConfigDto, List<TempCarFeeConfigAttrDto> tempCarFeeConfigAttrDtos, List<ParkingCouponCarDto> parkingCouponCarDtos) {
        //获取停车时间
        long min = TempCarFeeFactory.getTempCarCeilMin(carInoutDto, parkingCouponCarDtos);

        int freeMin = TempCarFeeFactory.getAttrValueInt(tempCarFeeConfigAttrDtos, SPEC_CD_5600012059);

        //最大收费
        double maxFeeMoney = TempCarFeeFactory.getAttrValueDouble(tempCarFeeConfigAttrDtos, SPEC_CD_5600012060);
        //判断 时间是否超过24小时
        double baseMoney = 0.0;

        //免费时间中
        if (min <= freeMin && !TempCarFeeFactory.judgeFinishPayTempCarFee(carInoutDto)) {
            return new TempCarFeeResult(carInoutDto.getCarNum(), 0.0, maxFeeMoney, baseMoney);
        }

        BigDecimal minDeci = new BigDecimal(min);

        //处理超过 一天的数据
        if (min > 24 * 60) {
            BigDecimal dayDeci = minDeci.divide(new BigDecimal(24 * 60), 0, BigDecimal.ROUND_DOWN);
            baseMoney = dayDeci.multiply(new BigDecimal(maxFeeMoney)).doubleValue();

            minDeci = minDeci.subtract(dayDeci.multiply(new BigDecimal(24 * 60))).setScale(0, BigDecimal.ROUND_DOWN);
            min = minDeci.intValue();
        }

        // 特殊情况  好几天后刚好 min为0
        if (min == 0) {
            return new TempCarFeeResult(carInoutDto.getCarNum(), 0.0, maxFeeMoney, baseMoney);
        }

        int firstMin = TempCarFeeFactory.getAttrValueInt(tempCarFeeConfigAttrDtos, SPEC_CD_5600012061);
        double firstMoney = TempCarFeeFactory.getAttrValueDouble(tempCarFeeConfigAttrDtos, SPEC_CD_5600012062);
        //在首段时长(分钟)中
        if (min < firstMin) {
            return new TempCarFeeResult(carInoutDto.getCarNum(), firstMoney, maxFeeMoney, baseMoney);
        }

        //超过手段
        int afterMin = TempCarFeeFactory.getAttrValueInt(tempCarFeeConfigAttrDtos, SPEC_CD_5600012063);
        double afterByMoney = TempCarFeeFactory.getAttrValueDouble(tempCarFeeConfigAttrDtos, SPEC_CD_5600012064);


        //超过的时间
        BigDecimal afterFirstMin = minDeci.subtract(new BigDecimal(firstMin));
        //时间差 除以 没多少分钟 向上取整
        double money = afterFirstMin.divide(new BigDecimal(afterMin), 0, BigDecimal.ROUND_UP)
                .multiply(new BigDecimal(afterByMoney))
                .setScale(2, BigDecimal.ROUND_HALF_UP).add(new BigDecimal(firstMoney)).doubleValue();
        return new TempCarFeeResult(carInoutDto.getCarNum(), money, maxFeeMoney, baseMoney);
    }

    private boolean compareTime(String startTime, String endTime, String inTime) {

        Date iTime = DateUtil.getDateFromStringA(inTime);

        startTime = DateUtil.getFormatTimeStringB(iTime) + " " + startTime + ":00";
        endTime = DateUtil.getFormatTimeStringB(iTime) + " " + endTime + ":00";

        Date sTime = DateUtil.getDateFromStringA(startTime);
        Date eTime = DateUtil.getDateFromStringA(endTime);
        if (eTime.getTime() < sTime.getTime()) {
            endTime = DateUtil.getAddDayStringA(eTime, 1);
            eTime = DateUtil.getDateFromStringA(endTime);
        }

        if (DateUtil.belongCalendar(iTime, sTime, eTime)) {
            return true;
        }

        return false;
    }
}
