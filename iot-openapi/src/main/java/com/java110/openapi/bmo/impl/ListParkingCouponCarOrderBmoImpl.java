package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.coupon.ParkingCouponCarOrderDto;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.intf.car.IParkingCouponCarOrderV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("listParkingCouponCarOrderBmoImpl")
public class ListParkingCouponCarOrderBmoImpl implements IIotCommonApiBmo {

    @Autowired
    private IParkingCouponCarOrderV1InnerServiceSMO parkingCouponCarOrderV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区");
        Assert.hasKeyAndValue(reqJson, "page", "未包含page");
        Assert.hasKeyAndValue(reqJson, "row", "未包含row");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        ParkingCouponCarOrderDto parkingCouponCarOrderDto = BeanConvertUtil.covertBean(reqJson, ParkingCouponCarOrderDto.class);

        int count = parkingCouponCarOrderV1InnerServiceSMOImpl.queryParkingCouponCarOrdersCount(parkingCouponCarOrderDto);

        List<ParkingCouponCarOrderDto> parkingCouponCarOrderDtos = null;

        if (count > 0) {
            parkingCouponCarOrderDtos = parkingCouponCarOrderV1InnerServiceSMOImpl.queryParkingCouponCarOrders(parkingCouponCarOrderDto);
        } else {
            parkingCouponCarOrderDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, parkingCouponCarOrderDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }
}
