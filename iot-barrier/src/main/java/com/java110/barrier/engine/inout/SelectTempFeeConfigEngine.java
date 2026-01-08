package com.java110.barrier.engine.inout;

import com.java110.bean.dto.car.TempInnerCarDto;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.dto.fee.TempCarFeeConfigDto;
import com.java110.intf.barrier.ITempCarFeeConfigV1InnerServiceSMO;
import com.java110.intf.car.ITempInnerCarV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SelectTempFeeConfigEngine {

    @Autowired
    private ITempInnerCarV1InnerServiceSMO tempInnerCarV1InnerServiceSMOImpl;

    @Autowired
    private ITempCarFeeConfigV1InnerServiceSMO tempCarFeeConfigV1InnerServiceSMOImpl;

    public TempCarFeeConfigDto getTempCarFeeConfig(CarInoutDto carInoutDto, String paId) {

        TempCarFeeConfigDto tempCarFeeConfigDto = null;

        //这里判断是否为内部临时车
        String carNum = carInoutDto.getCarNum();
        TempInnerCarDto tempInnerCarDto = new TempInnerCarDto();
        tempInnerCarDto.setCarNum(carNum);
        tempInnerCarDto.setPaId(paId);
        List<TempInnerCarDto> tempInnerCarDtos = tempInnerCarV1InnerServiceSMOImpl.queryTempInnerCars(tempInnerCarDto);
        if (!ListUtil.isNull(tempInnerCarDtos)) {
            //未缴费
            TempCarFeeConfigDto tempCarFeeConfigParamDto = new TempCarFeeConfigDto();
            tempCarFeeConfigParamDto.setConfigId(tempInnerCarDtos.get(0).getConfigId());
            tempCarFeeConfigParamDto.setCommunityId(carInoutDto.getCommunityId());
            List<TempCarFeeConfigDto> tempCarFeeConfigDtos = tempCarFeeConfigV1InnerServiceSMOImpl.queryTempCarFeeConfigs(tempCarFeeConfigParamDto);
            if (!ListUtil.isNull(tempCarFeeConfigDtos)) {
                return tempCarFeeConfigDtos.get(0);
            }
        }

        // 根据车牌颜色来
        /**
         * 车牌颜色
         * 未知 0
         * 蓝色 1
         * 黄色 2
         * 白色 3
         * 黑色 4
         * 绿色 5
         * 黄绿色 6
         */

        String colorType = carInoutDto.getColorType();
        if (StringUtil.isEmpty(colorType) || "0".equals(colorType)) {
            colorType = "1"; // 蓝牌
        }

        //未缴费
        TempCarFeeConfigDto tempCarFeeConfigParamDto = new TempCarFeeConfigDto();
        tempCarFeeConfigParamDto.setPaId(paId);
        tempCarFeeConfigParamDto.setCarType(colorType);
        tempCarFeeConfigParamDto.setCommunityId(carInoutDto.getCommunityId());
        List<TempCarFeeConfigDto> tempCarFeeConfigDtos = tempCarFeeConfigV1InnerServiceSMOImpl.queryTempCarFeeConfigs(tempCarFeeConfigDto);
        if (!ListUtil.isNull(tempCarFeeConfigDtos)) {
            return tempCarFeeConfigDtos.get(0);
        }

        //未缴费
        tempCarFeeConfigParamDto = new TempCarFeeConfigDto();
        tempCarFeeConfigParamDto.setPaId(paId);
        tempCarFeeConfigParamDto.setCarType("9901");
        tempCarFeeConfigParamDto.setCommunityId(carInoutDto.getCommunityId());
        tempCarFeeConfigDtos = tempCarFeeConfigV1InnerServiceSMOImpl.queryTempCarFeeConfigs(tempCarFeeConfigDto);
        if (!ListUtil.isNull(tempCarFeeConfigDtos)) {
            return tempCarFeeConfigDtos.get(0);
        }
        return tempCarFeeConfigDto;
    }
}
