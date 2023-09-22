package com.java110.core.smo;

import com.alibaba.fastjson.JSONObject;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.dto.fee.FeeDto;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 费用计算 服务类
 * <p>
 * add by wuxw 2020-09-23
 *
 * @openSource https://gitee.com/wuxw7/MicroCommunity.git
 */
public interface IComputeFeeSMO {



    /**
     * 时间差 按天折算
     *
     * @param fromDate 开始时间
     * @param toDate   结束时间
     * @return 相差月数
     */
//    double dayCompare(Date fromDate, Date toDate);

    /**
     　　 *字符串的日期格式的计算
     　　 */
    long daysBetween(Date smdate,Date bdate) ;



    /**
     * 计算停车时间和费用
     *
     * @param carInoutDtos
     */
    List<CarInoutDto> computeTempCarStopTimeAndFee(List<CarInoutDto> carInoutDtos);

    /**
     * 计算停车时间和费用
     *
     * @param carInoutDtos
     */
    List<CarInoutDto> computeTempCarInoutDetailStopTimeAndFee(List<CarInoutDto> carInoutDtos);


}
