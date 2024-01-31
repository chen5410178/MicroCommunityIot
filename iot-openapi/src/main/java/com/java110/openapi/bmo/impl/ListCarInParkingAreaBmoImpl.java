package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.smo.IComputeFeeSMO;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.dto.payment.CarInoutPaymentDto;
import com.java110.intf.barrier.ICarInoutPaymentV1InnerServiceSMO;
import com.java110.intf.barrier.ICarInoutV1InnerServiceSMO;
import com.java110.intf.barrier.IParkingBoxAreaV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("listCarInParkingAreaBmoImpl")
public class ListCarInParkingAreaBmoImpl implements IIotCommonApiBmo {

    private static Logger logger = LoggerFactory.getLogger(ListCarInParkingAreaBmoImpl.class);

    @Autowired
    private ICarInoutV1InnerServiceSMO carInoutV1InnerServiceSMOImpl;

    @Autowired
    private IParkingBoxAreaV1InnerServiceSMO parkingBoxAreaV1InnerServiceSMOImpl;

    @Autowired
    private IComputeFeeSMO computeFeeSMOImpl;


    @Autowired
    private ICarInoutPaymentV1InnerServiceSMO carInoutPaymentV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
        Assert.hasKeyAndValue(reqJson, "page", "未包含page");
        Assert.hasKeyAndValue(reqJson, "row", "未包含row");
        Assert.hasKeyAndValue(reqJson, "paId", "未包含paId");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        CarInoutDto carInoutDto = BeanConvertUtil.covertBean(reqJson, CarInoutDto.class);
        carInoutDto.setStates(new String[]{CarInoutDto.STATE_IN, CarInoutDto.STATE_PAY, CarInoutDto.STATE_REPAY});

        carInoutDto.setPaId(reqJson.getString("paId"));

        int count = carInoutV1InnerServiceSMOImpl.queryCarInoutsCount(carInoutDto);

        List<CarInoutDto> carInoutDtos = null;

        if (count > 0) {
            carInoutDtos = carInoutV1InnerServiceSMOImpl.queryCarInouts(carInoutDto);
            freshPayTime(carInoutDtos);
            carInoutDtos = computeCarInouts(carInoutDtos);
        } else {
            carInoutDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, carInoutDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }

    /**
     * 输入最新的支付时间
     *
     * @param carInoutDtos
     */
    private void freshPayTime(List<CarInoutDto> carInoutDtos) {
        for (CarInoutDto carInoutDto : carInoutDtos) {
            if (CarInoutDto.STATE_PAY.equals(carInoutDto.getState()) || CarInoutDto.STATE_REPAY.equals(carInoutDto.getState())) {

                //查询 支付记录 刷新payTime
                CarInoutPaymentDto carInoutPaymentDto = new CarInoutPaymentDto();
                carInoutPaymentDto.setInoutId(carInoutDto.getInoutId());
                carInoutPaymentDto.setPage(1);
                carInoutPaymentDto.setRow(1);
                List<CarInoutPaymentDto> carInoutPaymentDtos = carInoutPaymentV1InnerServiceSMOImpl.queryCarInoutPayments(carInoutPaymentDto);

                if (ListUtil.isNull(carInoutPaymentDtos)) {
                    carInoutDto.setPayTime(DateUtil.getCurrentDate());
                    continue;
                }
                carInoutDto.setPayTime(carInoutPaymentDtos.get(0).getPayTime());
            }
        }
    }


    private List<CarInoutDto> computeCarInouts(List<CarInoutDto> carInoutDtos) {
        return computeFeeSMOImpl.computeTempCarStopTimeAndFee(carInoutDtos);
    }
}
