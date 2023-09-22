package com.java110.barrier.smo;


import com.java110.dto.carInout.CarInoutDto;
import com.java110.dto.fee.TempCarFeeConfigAttrDto;
import com.java110.dto.fee.TempCarFeeConfigDto;
import com.java110.dto.fee.TempCarFeeResult;

import java.util.List;

/**
 * 计算 临时车 停车费
 */
public interface IComputeTempCarFee {


    /**
     * 临时车停车费计算
     *
     * @param carInoutDto
     * @param tempCarFeeConfigDto
     * @return
     */
    TempCarFeeResult computeTempCarFee(CarInoutDto carInoutDto, TempCarFeeConfigDto tempCarFeeConfigDto) throws Exception;


     TempCarFeeResult computeTempCarFee(CarInoutDto carInoutDto, TempCarFeeConfigDto tempCarFeeConfigDto, List<TempCarFeeConfigAttrDto> tempCarFeeConfigAttrDtos) throws Exception;
}
