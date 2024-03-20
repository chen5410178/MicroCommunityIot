package com.java110.barrier.engine.compute;

import com.java110.barrier.factory.TempCarFeeFactory;
import com.java110.bean.dto.coupon.ParkingCouponCarDto;
import com.java110.core.utils.DateUtil;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.dto.fee.TempCarFeeConfigAttrDto;
import com.java110.dto.fee.TempCarFeeConfigDto;
import com.java110.dto.fee.TempCarFeeResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Component(value = "6700012006")
public class TimePeriodComputeTempCarFeeImpl extends BaseComputeTempCarFee {

    /**
     * 5600012019	开始时间1
     * 5600012020	结束时间1
     * 5600012021	免费时间1(分钟)
     * 5600012022	最高收费1
     * 5600012023	首段时间1(分钟)
     * 5600012024	首段收费1
     * 5600012025	超过首段1(分钟)
     * 5600012026	超过首段收费1
     * 5600012027	开始时间2
     * 5600012028	结束时间2
     * 5600012029	免费时间2(分钟)
     * 5600012030	最高收费2
     * 5600012031	首段时间2(分钟)
     * 5600012032	首段收费2
     * 5600012033	超过首段2(分钟)
     * 5600012034	超过首段收费2
     */
    public static final String SPEC_CD_5600012019 = "5600012019";
    public static final String SPEC_CD_5600012020 = "5600012020";
    public static final String SPEC_CD_5600012021 = "5600012021";
    public static final String SPEC_CD_5600012022 = "5600012022";
    public static final String SPEC_CD_5600012023 = "5600012023";
    public static final String SPEC_CD_5600012024 = "5600012024";
    public static final String SPEC_CD_5600012025 = "5600012025";

    public static final String SPEC_CD_5600012026 = "5600012026";
    public static final String SPEC_CD_5600012027 = "5600012027";
    public static final String SPEC_CD_5600012028 = "5600012028";

    public static final String SPEC_CD_5600012029 = "5600012029";
    public static final String SPEC_CD_5600012030 = "5600012030";

    public static final String SPEC_CD_5600012031 = "5600012031";
    public static final String SPEC_CD_5600012032 = "5600012032";

    public static final String SPEC_CD_5600012033 = "5600012033";
    public static final String SPEC_CD_5600012034 = "5600012034";

    @Override
    public TempCarFeeResult doCompute(CarInoutDto carInoutDto, TempCarFeeConfigDto tempCarFeeConfigDto, List<TempCarFeeConfigAttrDto> tempCarFeeConfigAttrDtos, List<ParkingCouponCarDto> parkingCouponCarDtos) {

        //todo 计算 应该采用那个时段的设置

        String startTime1 = TempCarFeeFactory.getAttrValueString(tempCarFeeConfigAttrDtos, SPEC_CD_5600012019);
        String endTime1 = TempCarFeeFactory.getAttrValueString(tempCarFeeConfigAttrDtos, SPEC_CD_5600012020);
        String inTime = carInoutDto.getInTime();

        if (compareTime(startTime1, endTime1, inTime) && compareTime(startTime1, endTime1, DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A))) {
            //todo 走时段1 的设置
            return doComputeOne(carInoutDto, tempCarFeeConfigDto, tempCarFeeConfigAttrDtos, parkingCouponCarDtos);
        } else {
            // todo 时段2 的设置
            return doComputeTwo(carInoutDto, tempCarFeeConfigDto, tempCarFeeConfigAttrDtos, parkingCouponCarDtos);
        }

    }

    /**
     * 5600012019	开始时间1
     * 5600012020	结束时间1
     * 5600012021	免费时间1(分钟)
     * 5600012022	最高收费1
     * 5600012023	首段时间1(分钟)
     * 5600012024	首段收费1
     * 5600012025	超过首段1(分钟)
     * 5600012026	超过首段收费1
     */
    private TempCarFeeResult doComputeTwo(CarInoutDto carInoutDto, TempCarFeeConfigDto tempCarFeeConfigDto, List<TempCarFeeConfigAttrDto> tempCarFeeConfigAttrDtos, List<ParkingCouponCarDto> parkingCouponCarDtos) {
        //获取停车时间
        long min = TempCarFeeFactory.getTempCarCeilMin(carInoutDto, parkingCouponCarDtos);

        int freeMin = TempCarFeeFactory.getAttrValueInt(tempCarFeeConfigAttrDtos, SPEC_CD_5600012021);

        //最大收费
        double maxFeeMoney = TempCarFeeFactory.getAttrValueDouble(tempCarFeeConfigAttrDtos, SPEC_CD_5600012022);
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

        int firstMin = TempCarFeeFactory.getAttrValueInt(tempCarFeeConfigAttrDtos, SPEC_CD_5600012023);
        double firstMoney = TempCarFeeFactory.getAttrValueDouble(tempCarFeeConfigAttrDtos, SPEC_CD_5600012024);
        //在首段时长(分钟)中
        if (min < firstMin) {
            return new TempCarFeeResult(carInoutDto.getCarNum(), firstMoney, maxFeeMoney, baseMoney);
        }

        //超过手段
        int afterMin = TempCarFeeFactory.getAttrValueInt(tempCarFeeConfigAttrDtos, SPEC_CD_5600012025);
        double afterByMoney = TempCarFeeFactory.getAttrValueDouble(tempCarFeeConfigAttrDtos, SPEC_CD_5600012026);


        //超过的时间
        BigDecimal afterFirstMin = minDeci.subtract(new BigDecimal(firstMin));
        //时间差 除以 没多少分钟 向上取整
        double money = afterFirstMin.divide(new BigDecimal(afterMin), 0, BigDecimal.ROUND_UP)
                .multiply(new BigDecimal(afterByMoney))
                .setScale(2, BigDecimal.ROUND_HALF_UP).add(new BigDecimal(firstMoney)).doubleValue();
        return new TempCarFeeResult(carInoutDto.getCarNum(), money, maxFeeMoney, baseMoney);
    }

    /*
     * 5600012027	开始时间2
     * 5600012028	结束时间2
     * 5600012029	免费时间2(分钟)
     * 5600012030	最高收费2
     * 5600012031	首段时间2(分钟)
     * 5600012032	首段收费2
     * 5600012033	超过首段2(分钟)
     * 5600012034	超过首段收费2
     */
    private TempCarFeeResult doComputeOne(CarInoutDto carInoutDto, TempCarFeeConfigDto tempCarFeeConfigDto, List<TempCarFeeConfigAttrDto> tempCarFeeConfigAttrDtos, List<ParkingCouponCarDto> parkingCouponCarDtos) {
        //获取停车时间
        long min = TempCarFeeFactory.getTempCarCeilMin(carInoutDto, parkingCouponCarDtos);

        int freeMin = TempCarFeeFactory.getAttrValueInt(tempCarFeeConfigAttrDtos, SPEC_CD_5600012029);

        //最大收费
        double maxFeeMoney = TempCarFeeFactory.getAttrValueDouble(tempCarFeeConfigAttrDtos, SPEC_CD_5600012030);
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

        int firstMin = TempCarFeeFactory.getAttrValueInt(tempCarFeeConfigAttrDtos, SPEC_CD_5600012031);
        double firstMoney = TempCarFeeFactory.getAttrValueDouble(tempCarFeeConfigAttrDtos, SPEC_CD_5600012032);
        //在首段时长(分钟)中
        if (min < firstMin) {
            return new TempCarFeeResult(carInoutDto.getCarNum(), firstMoney, maxFeeMoney, baseMoney);
        }

        //超过手段
        int afterMin = TempCarFeeFactory.getAttrValueInt(tempCarFeeConfigAttrDtos, SPEC_CD_5600012033);
        double afterByMoney = TempCarFeeFactory.getAttrValueDouble(tempCarFeeConfigAttrDtos, SPEC_CD_5600012034);


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
