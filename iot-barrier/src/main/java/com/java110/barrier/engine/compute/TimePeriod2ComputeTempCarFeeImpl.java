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

@Component(value = "6700012007")
public class TimePeriod2ComputeTempCarFeeImpl extends BaseComputeTempCarFee {

    /**
     * 5600012035	免费时间(分钟)
     * 5600012036	最高收费
     * 5600012037	开始时段1
     * 5600012038	结束时段1
     * 5600012039	首段时间1(分钟)
     * 5600012040	首段收费1
     * 5600012041	超过首段1(分钟)
     * 5600012042	超过首段收费1
     * 5600012043	开始时间2
     * 5600012044	结束时间2
     * 5600012045	首段时间2(分钟)
     * 5600012046	首段收费2
     * 5600012047	超过首段2(分钟)
     * 5600012048	超过首段收费2
     */
    public static final String SPEC_CD_5600012035 = "5600012035";
    public static final String SPEC_CD_5600012036 = "5600012036";
    public static final String SPEC_CD_5600012037 = "5600012037";
    public static final String SPEC_CD_5600012038 = "5600012038";
    public static final String SPEC_CD_5600012039 = "5600012039";
    public static final String SPEC_CD_5600012040 = "5600012040";
    public static final String SPEC_CD_5600012041 = "5600012041";
    public static final String SPEC_CD_5600012042 = "5600012042";
    public static final String SPEC_CD_5600012043 = "5600012043";
    public static final String SPEC_CD_5600012044 = "5600012044";
    public static final String SPEC_CD_5600012045 = "5600012045";
    public static final String SPEC_CD_5600012046 = "5600012046";
    public static final String SPEC_CD_5600012047 = "5600012047";
    public static final String SPEC_CD_5600012048 = "5600012048";

    @Override
    public TempCarFeeResult doCompute(CarInoutDto carInoutDto, TempCarFeeConfigDto tempCarFeeConfigDto, List<TempCarFeeConfigAttrDto> tempCarFeeConfigAttrDtos, List<ParkingCouponCarDto> parkingCouponCarDtos) {

        String startTime1 = TempCarFeeFactory.getAttrValueString(tempCarFeeConfigAttrDtos, SPEC_CD_5600012037);
        String endTime1 = TempCarFeeFactory.getAttrValueString(tempCarFeeConfigAttrDtos, SPEC_CD_5600012038);
        String startTime2 = TempCarFeeFactory.getAttrValueString(tempCarFeeConfigAttrDtos, SPEC_CD_5600012043);
        String endTime2 = TempCarFeeFactory.getAttrValueString(tempCarFeeConfigAttrDtos, SPEC_CD_5600012044);
        String inTime = carInoutDto.getInTime();

        if (compareTime(startTime1, endTime1, inTime) && compareTime(startTime1, endTime1, DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A))) {
            //todo 走时段1 的设置
            return doComputeOne(carInoutDto, tempCarFeeConfigDto, tempCarFeeConfigAttrDtos, parkingCouponCarDtos);
        } else if (compareTime(startTime2, endTime2, inTime) && compareTime(startTime2, endTime2, DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A))) {
            // todo 时段2 的设置
            return doComputeTwo(carInoutDto, tempCarFeeConfigDto, tempCarFeeConfigAttrDtos, parkingCouponCarDtos);
        } else {
            //todo 部分时段1 部分 时段2
            return doComputeOneTwo(carInoutDto, tempCarFeeConfigDto, tempCarFeeConfigAttrDtos, parkingCouponCarDtos);
        }
    }

    private TempCarFeeResult doComputeOneTwo(CarInoutDto carInoutDto, TempCarFeeConfigDto tempCarFeeConfigDto, List<TempCarFeeConfigAttrDto> tempCarFeeConfigAttrDtos, List<ParkingCouponCarDto> parkingCouponCarDtos) {
        //获取停车时间
        long min = TempCarFeeFactory.getTempCarCeilMin(carInoutDto, parkingCouponCarDtos);

        int freeMin = TempCarFeeFactory.getAttrValueInt(tempCarFeeConfigAttrDtos, SPEC_CD_5600012035);

        //最大收费
        double maxFeeMoney = TempCarFeeFactory.getAttrValueDouble(tempCarFeeConfigAttrDtos, SPEC_CD_5600012036);
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


        //todo 按时段1 来算费
        /*****************************************按时段1来算费(start)*****************************************/
        int firstMin = TempCarFeeFactory.getAttrValueInt(tempCarFeeConfigAttrDtos, SPEC_CD_5600012039);
        double firstMoney = TempCarFeeFactory.getAttrValueDouble(tempCarFeeConfigAttrDtos, SPEC_CD_5600012040);

        //超过手段
        int afterMin = TempCarFeeFactory.getAttrValueInt(tempCarFeeConfigAttrDtos, SPEC_CD_5600012041);
        double afterByMoney = TempCarFeeFactory.getAttrValueDouble(tempCarFeeConfigAttrDtos, SPEC_CD_5600012042);

        //todo 计算时段1 的时间，当前时间(出厂时间) - 时段2 开始时间
        long time2Min = computeTime2Min(tempCarFeeConfigAttrDtos);
        min = min - time2Min;
        //在首段时长(分钟)中
        double money = 0;
        if (min < firstMin) {
            money = firstMoney;
        } else {
            //超过的时间
            BigDecimal afterFirstMin = minDeci.subtract(new BigDecimal(firstMin)).subtract(new BigDecimal(time2Min));
            //时间差 除以 没多少分钟 向上取整
            money = afterFirstMin.divide(new BigDecimal(afterMin), 0, BigDecimal.ROUND_UP)
                    .multiply(new BigDecimal(afterByMoney))
                    .setScale(2, BigDecimal.ROUND_HALF_UP).add(new BigDecimal(firstMoney)).doubleValue();
        }
        /*****************************************按时段1来算费(end)*****************************************/

        min = time2Min;
        minDeci = new BigDecimal(min);
        if (min == 0) {
            return new TempCarFeeResult(carInoutDto.getCarNum(), money, maxFeeMoney, baseMoney);
        }
        //todo 按时段2 来算费
        /*****************************************按时段2来算费(start)*****************************************/
        firstMin = TempCarFeeFactory.getAttrValueInt(tempCarFeeConfigAttrDtos, SPEC_CD_5600012045);
        firstMoney = TempCarFeeFactory.getAttrValueDouble(tempCarFeeConfigAttrDtos, SPEC_CD_5600012046);
        // todo 这里加上 时段1 的费用
        firstMoney = new BigDecimal(firstMoney).add(new BigDecimal(money)).doubleValue();
        //在首段时长(分钟)中
        if (min < firstMin) {
            return new TempCarFeeResult(carInoutDto.getCarNum(), firstMoney, maxFeeMoney, baseMoney);
        }

        //超过手段
        afterMin = TempCarFeeFactory.getAttrValueInt(tempCarFeeConfigAttrDtos, SPEC_CD_5600012047);
        afterByMoney = TempCarFeeFactory.getAttrValueDouble(tempCarFeeConfigAttrDtos, SPEC_CD_5600012048);

        //超过的时间
        BigDecimal afterFirstMin = minDeci.subtract(new BigDecimal(firstMin));
        //时间差 除以 没多少分钟 向上取整
        money = afterFirstMin.divide(new BigDecimal(afterMin), 0, BigDecimal.ROUND_UP)
                .multiply(new BigDecimal(afterByMoney))
                .setScale(2, BigDecimal.ROUND_HALF_UP).add(new BigDecimal(firstMoney)).doubleValue();
        return new TempCarFeeResult(carInoutDto.getCarNum(), money, maxFeeMoney, baseMoney);
        /*****************************************按时段2来算费(end)*****************************************/

    }


    /**
     * 5600012043	开始时间2
     * 5600012044	结束时间2
     * 5600012045	首段时间2(分钟)
     * 5600012046	首段收费2
     * 5600012047	超过首段2(分钟)
     * 5600012048	超过首段收费2
     */
    private TempCarFeeResult doComputeTwo(CarInoutDto carInoutDto, TempCarFeeConfigDto tempCarFeeConfigDto, List<TempCarFeeConfigAttrDto> tempCarFeeConfigAttrDtos, List<ParkingCouponCarDto> parkingCouponCarDtos) {
        //获取停车时间
        long min = TempCarFeeFactory.getTempCarCeilMin(carInoutDto, parkingCouponCarDtos);

        int freeMin = TempCarFeeFactory.getAttrValueInt(tempCarFeeConfigAttrDtos, SPEC_CD_5600012035);

        //最大收费
        double maxFeeMoney = TempCarFeeFactory.getAttrValueDouble(tempCarFeeConfigAttrDtos, SPEC_CD_5600012036);
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

        int firstMin = TempCarFeeFactory.getAttrValueInt(tempCarFeeConfigAttrDtos, SPEC_CD_5600012045);
        double firstMoney = TempCarFeeFactory.getAttrValueDouble(tempCarFeeConfigAttrDtos, SPEC_CD_5600012046);
        //在首段时长(分钟)中
        if (min < firstMin) {
            return new TempCarFeeResult(carInoutDto.getCarNum(), firstMoney, maxFeeMoney, baseMoney);
        }

        //超过手段
        int afterMin = TempCarFeeFactory.getAttrValueInt(tempCarFeeConfigAttrDtos, SPEC_CD_5600012047);
        double afterByMoney = TempCarFeeFactory.getAttrValueDouble(tempCarFeeConfigAttrDtos, SPEC_CD_5600012048);


        //超过的时间
        BigDecimal afterFirstMin = minDeci.subtract(new BigDecimal(firstMin));
        //时间差 除以 没多少分钟 向上取整
        double money = afterFirstMin.divide(new BigDecimal(afterMin), 0, BigDecimal.ROUND_UP)
                .multiply(new BigDecimal(afterByMoney))
                .setScale(2, BigDecimal.ROUND_HALF_UP).add(new BigDecimal(firstMoney)).doubleValue();
        return new TempCarFeeResult(carInoutDto.getCarNum(), money, maxFeeMoney, baseMoney);
    }

    /*
     * 5600012037	开始时段1
     * 5600012038	结束时段1
     * 5600012039	首段时间1(分钟)
     * 5600012040	首段收费1
     * 5600012041	超过首段1(分钟)
     * 5600012042	超过首段收费1
     */
    private TempCarFeeResult doComputeOne(CarInoutDto carInoutDto, TempCarFeeConfigDto tempCarFeeConfigDto, List<TempCarFeeConfigAttrDto> tempCarFeeConfigAttrDtos, List<ParkingCouponCarDto> parkingCouponCarDtos) {
        //获取停车时间
        long min = TempCarFeeFactory.getTempCarCeilMin(carInoutDto, parkingCouponCarDtos);

        int freeMin = TempCarFeeFactory.getAttrValueInt(tempCarFeeConfigAttrDtos, SPEC_CD_5600012035);

        //最大收费
        double maxFeeMoney = TempCarFeeFactory.getAttrValueDouble(tempCarFeeConfigAttrDtos, SPEC_CD_5600012036);
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

        int firstMin = TempCarFeeFactory.getAttrValueInt(tempCarFeeConfigAttrDtos, SPEC_CD_5600012039);
        double firstMoney = TempCarFeeFactory.getAttrValueDouble(tempCarFeeConfigAttrDtos, SPEC_CD_5600012040);
        //在首段时长(分钟)中
        if (min < firstMin) {
            return new TempCarFeeResult(carInoutDto.getCarNum(), firstMoney, maxFeeMoney, baseMoney);
        }

        //超过手段
        int afterMin = TempCarFeeFactory.getAttrValueInt(tempCarFeeConfigAttrDtos, SPEC_CD_5600012041);
        double afterByMoney = TempCarFeeFactory.getAttrValueDouble(tempCarFeeConfigAttrDtos, SPEC_CD_5600012042);


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


    private static long computeTime2Min(List<TempCarFeeConfigAttrDto> tempCarFeeConfigAttrDtos) {
        String startTime2 = TempCarFeeFactory.getAttrValueString(tempCarFeeConfigAttrDtos, SPEC_CD_5600012043);
        Date oTime = DateUtil.getCurrentDate();
        startTime2 = DateUtil.getFormatTimeStringB(oTime) + " " + startTime2 + ":00";
        Date sTime = DateUtil.getDateFromStringA(startTime2);
        if (oTime.getTime() < sTime.getTime()) {
            startTime2 = DateUtil.getAddDayStringA(sTime, -1);
            sTime = DateUtil.getDateFromStringA(startTime2);
        }
        double time2Min = (oTime.getTime() - sTime.getTime()) / (60 * 1000 * 1.00);
        long time2MinLong = new Double(Math.ceil(time2Min)).longValue();
        if (time2MinLong < 0) {
            return 0;
        }
        return time2MinLong;
    }
}
