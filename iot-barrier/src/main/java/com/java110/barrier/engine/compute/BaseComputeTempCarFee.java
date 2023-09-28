package com.java110.barrier.engine.compute;

import com.java110.barrier.factory.TempCarFeeFactory;
import com.java110.barrier.engine.IComputeTempCarFee;
import com.java110.bean.dto.coupon.ParkingCouponCarDto;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.dto.fee.TempCarFeeConfigAttrDto;
import com.java110.dto.fee.TempCarFeeConfigDto;
import com.java110.dto.fee.TempCarFeeResult;
import com.java110.intf.barrier.ITempCarFeeConfigAttrInnerServiceSMO;
import com.java110.intf.barrier.ITempCarFeeConfigInnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class BaseComputeTempCarFee implements IComputeTempCarFee {
    @Autowired
    private ITempCarFeeConfigInnerServiceSMO tempCarFeeConfigServiceImpl;


    @Autowired
    private ITempCarFeeConfigAttrInnerServiceSMO tempCarFeeConfigAttrInnerServiceSMOImpl;


    @Override
    public TempCarFeeResult computeTempCarFee(CarInoutDto carInoutDto,
                                              TempCarFeeConfigDto tempCarFeeConfigDto,
                                              List<ParkingCouponCarDto> parkingCouponCarDtos) throws Exception {
        TempCarFeeConfigAttrDto tempCarFeeConfigAttrDto = new TempCarFeeConfigAttrDto();
        tempCarFeeConfigAttrDto.setConfigId(tempCarFeeConfigDto.getConfigId());
        tempCarFeeConfigAttrDto.setCommunityId(tempCarFeeConfigDto.getCommunityId());

        List<TempCarFeeConfigAttrDto> tempCarFeeConfigAttrDtos = tempCarFeeConfigAttrInnerServiceSMOImpl.queryTempCarFeeConfigAttrs(tempCarFeeConfigAttrDto);
        //long couponMin = getCouponMin(parkingCouponCarDtos);
        TempCarFeeResult result = doCompute(carInoutDto, tempCarFeeConfigDto, tempCarFeeConfigAttrDtos,parkingCouponCarDtos);
        //获取停车时间
        long min = TempCarFeeFactory.getTempCarMin(carInoutDto);
        long hours = min / 60; //因为两者都是整数，你得到一个int
        long minutes = min%60;
        result.setMin(minutes);
        result.setHours(hours);
        return result;
    }

    /**
     * 计算 费用
     *
     * @param carInoutDto
     * @param tempCarFeeConfigDto
     * @param tempCarFeeConfigAttrDtos
     * @return
     */
    public abstract TempCarFeeResult doCompute(CarInoutDto carInoutDto,
                                               TempCarFeeConfigDto tempCarFeeConfigDto,
                                               List<TempCarFeeConfigAttrDto> tempCarFeeConfigAttrDtos,
                                               List<ParkingCouponCarDto> parkingCouponCarDtos);

    /**
     * 查询 停车分钟
     * @param parkingCouponCarDtos
     * @return
     */
    private long getCouponMin(List<ParkingCouponCarDto> parkingCouponCarDtos){

        long couponMin = 0;
        if(parkingCouponCarDtos == null || parkingCouponCarDtos.size()<1){
            return couponMin;
        }

        for(ParkingCouponCarDto parkingCouponCarDto : parkingCouponCarDtos){
            if(!ParkingCouponCarDto.TYPE_CD_HOURS.equals(parkingCouponCarDto.getTypeCd())){
                continue;
            }

            couponMin += Long.parseLong(parkingCouponCarDto.getValue());
        }

        return couponMin;
    }


}
