package com.java110.barrier.engine.inout;

import com.java110.bean.dto.car.TempInnerCarDto;
import com.java110.core.utils.ListUtil;
import com.java110.intf.car.ITempInnerCarV1InnerServiceSMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TempInnerCarEngine {

    private static Logger logger = LoggerFactory.getLogger(TempInnerCarEngine.class);

    @Autowired
    private ITempInnerCarV1InnerServiceSMO tempInnerCarV1InnerServiceSMOImpl;

    /**
     * 查询当前有效期内 的内部临时车
     *
     * @param carNum      车牌号
     * @param communityId 小区ID
     * @param paId        停车场ID
     * @return 内部临时车信息，未登记或已过期返回 null
     */
    public TempInnerCarDto getValidTempInnerCar(String carNum, String communityId, String paId) {

        TempInnerCarDto tempInnerCarDto = new TempInnerCarDto();
        tempInnerCarDto.setCarNum(carNum);
        tempInnerCarDto.setCommunityId(communityId);
        tempInnerCarDto.setPaId(paId);
        tempInnerCarDto.setValid("Y");
        tempInnerCarDto.setStatusCd("0");
        tempInnerCarDto.setPage(-1);
        List<TempInnerCarDto> tempInnerCarDtos = tempInnerCarV1InnerServiceSMOImpl.queryTempInnerCars(tempInnerCarDto);

        if (ListUtil.isNull(tempInnerCarDtos)) {
            return null;
        }

        return tempInnerCarDtos.get(0);
    }

    /**
     * 车辆是否为有效期内 的内部临时车
     *
     * @param carNum      车牌号
     * @param communityId 小区ID
     * @param paId        停车场ID
     * @return true 为内部临时车
     */
    public boolean isTempInnerCar(String carNum, String communityId, String paId) {

        TempInnerCarDto tempInnerCarDto = getValidTempInnerCar(carNum, communityId, paId);

        if (tempInnerCarDto == null) {
            return false;
        }

        logger.info("内部临时车命中，carNum={},communityId={},paId={},ticId={}", carNum, communityId, paId, tempInnerCarDto.getTicId());

        return true;
    }
}
