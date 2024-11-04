package com.java110.core.smo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.context.Environment;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.smo.IComputeFeeSMO;
import com.java110.core.utils.ListUtil;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.dto.community.CommunityDto;
import com.java110.dto.fee.FeeAttrDto;
import com.java110.dto.fee.FeeDto;
import com.java110.dto.parking.ParkingSpaceDto;
import com.java110.intf.barrier.ITempCarFeeConfigAttrInnerServiceSMO;
import com.java110.intf.barrier.ITempCarFeeConfigInnerServiceSMO;
import com.java110.intf.car.IOwnerCarInnerServiceSMO;
import com.java110.intf.car.IParkingSpaceV1InnerServiceSMO;
import com.java110.intf.community.ICommunityInnerServiceSMO;
import com.java110.intf.community.IRoomInnerServiceSMO;
import com.java110.intf.user.IOwnerInnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * 费用计算 服务类
 * <p>
 * add by wuxw 2020-09-23
 *
 * @openSource https://gitee.com/wuxw7/MicroCommunity.git
 */

@Service
public class ComputeFeeSMOImpl implements IComputeFeeSMO {

    protected static final Logger logger = LoggerFactory.getLogger(ComputeFeeSMOImpl.class);


    @Autowired(required = false)
    private IRoomInnerServiceSMO roomInnerServiceSMOImpl;

    @Autowired(required = false)
    private IOwnerCarInnerServiceSMO ownerCarInnerServiceSMOImpl;

    @Autowired(required = false)
    private IParkingSpaceV1InnerServiceSMO parkingSpaceInnerServiceSMOImpl;

    @Autowired(required = false)
    private ICommunityInnerServiceSMO communityInnerServiceSMOImpl;

    @Autowired(required = false)
    private IOwnerInnerServiceSMO ownerInnerServiceSMOImpl;


    @Autowired(required = false)
    private ITempCarFeeConfigInnerServiceSMO tempCarFeeConfigInnerServiceSMOImpl;

    @Autowired(required = false)
    private ITempCarFeeConfigAttrInnerServiceSMO tempCarFeeConfigAttrInnerServiceSMOImpl;






    /**
     * 　　 *字符串的日期格式的计算
     */
    public long daysBetween(Date smdate, Date bdate) {
        long between_days = 0;
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        between_days = (time2 - time1) / (1000 * 3600 * 24);

        return between_days;
    }



    @Override
    public List<CarInoutDto> computeTempCarStopTimeAndFee(List<CarInoutDto> carInoutDtos) {

        if (carInoutDtos == null || carInoutDtos.size() < 1) {
            return null;
        }


        carInoutDtos = tempCarFeeConfigInnerServiceSMOImpl.computeTempCarFee(carInoutDtos);

        return carInoutDtos;

    }

    @Override
    public List<CarInoutDto> computeTempCarInoutDetailStopTimeAndFee(List<CarInoutDto> carInoutDtos) {
        if (ListUtil.isNull(carInoutDtos)) {
            return null;
        }


        carInoutDtos = tempCarFeeConfigInnerServiceSMOImpl.computeTempCarInoutDetailFee(carInoutDtos);

        return carInoutDtos;
    }


//    public static void main(String[] args) {
//        BigDecimal squarePrice = new BigDecimal(Double.parseDouble("4.50"));
//        BigDecimal builtUpArea = new BigDecimal(Double.parseDouble("52.69"));
//        BigDecimal additionalAmount = new BigDecimal(Double.parseDouble("0"));
//            BigDecimal cycle = new BigDecimal(Double.parseDouble("3"));
//        BigDecimal  feeTotalPrice = (squarePrice.multiply(builtUpArea).add(additionalAmount)).multiply(cycle).setScale(3, BigDecimal.ROUND_HALF_DOWN);
//        System.out.println(feeTotalPrice.doubleValue());
//    }

    //    public static void main(String[] args) {
//        ComputeFeeSMOImpl computeFeeSMO = new ComputeFeeSMOImpl();
//        try {
//            Date startTime = DateUtil.getDateFromString("2020-12-31 00:00:00", DateUtil.DATE_FORMATE_STRING_A);
//            Date endTime = DateUtil.getDateFromString("2021-1-2 00:00:00", DateUtil.DATE_FORMATE_STRING_A);
//            double day = (endTime.getTime() - startTime.getTime()) * 1.00 / (24 * 60 * 60 * 1000);
//
//            System.out.println(day);
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }
    static int[] getDiff(LocalDate start, LocalDate end) {

        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Start must not be before end.");
        }

        int year1 = start.getYear();
        int month1 = start.getMonthValue();
        int day1 = start.getDayOfMonth();

        int year2 = end.getYear();
        int month2 = end.getMonthValue();
        int day2 = end.getDayOfMonth();

        int yearDiff = year2 - year1;     // yearDiff >= 0
        int monthDiff = month2 - month1;

        int dayDiff = day2 - day1;

        if (dayDiff < 0) {
            LocalDate endMinusOneMonth = end.minusMonths(1);   // end 的上一个月
            int monthDays = endMinusOneMonth.lengthOfMonth();  // 该月的天数

            dayDiff += monthDays;  // 用上一个月的天数补上这个月差掉的日子

            if (monthDiff > 0) {   // eg. start is 2018-04-03, end is 2018-08-01
                monthDiff--;

            } else {  // eg. start is 2018-04-03, end is 2019-02-01
                monthDiff += 11;
                yearDiff--;

            }
        }

        int[] diff = new int[2];

        diff[0] = yearDiff * 12 + monthDiff;
        diff[1] = dayDiff;

        return diff;
    }

    /**
     * 计算两个日期的时间差
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public Map<String, Object> dateDiff(Date startDate, String endDate) {
        Map<String, Object> cycle = new HashMap<>();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date endDates = null;
        try {
            endDates = format.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(endDates);
        calendar.add(calendar.DATE, 1);
        Date date = calendar.getTime();
        LocalDate end = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int[] diff = getDiff(start, end);
        cycle.put("months", diff[0]);//几个月
        cycle.put("days", diff[1]);//几天
        String startDateString = format.format(startDate);
        String endDateString = format.format(endDates);
        cycle.put("startMonthDays", getDayOfMonth(startDateString));//开始月份天数
        cycle.put("endMonthDays", getDayOfMonth(endDateString));//结束月份天数
        cycle.put("isOneMonth", false);// false 不跨月 true月份
        if (diff[0] == 0) {
            //判断是否同一个月
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            String date1 = sdf.format(startDate);
            String date2 = sdf.format(endDates);
            if (!date1.equals(date2)) {
                cycle.put("isOneMonth", true);
                //计算夸月分两个月分别是多少天
                SimpleDateFormat sdfday = new SimpleDateFormat("dd");
                Integer startDate1 = Integer.valueOf(sdfday.format(startDate));
                String endDates2 = sdfday.format(endDates);
                cycle.put("startEndOfMonth", getDayOfMonth(startDateString) - startDate1 + 1);//开始月份天数
                cycle.put("endBeginningOfMonth", Integer.valueOf(endDates2));//结束月份天数
            }
        }
        return cycle;

    }

    /**
     * 获取日期内的天数
     *
     * @param dateStr
     * @return
     */
    public int getDayOfMonth(String dateStr) {
        int year = Integer.parseInt(dateStr.substring(0, 4));
        int month = Integer.parseInt(dateStr.substring(5, 7));
        Calendar c = Calendar.getInstance();
        c.set(year, month, 0); //输入类型为int类型
        return c.get(Calendar.DAY_OF_MONTH);
    }


}

