package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.carMonthCard.CarMonthCardDto;
import com.java110.dto.parking.ParkingAreaDto;
import com.java110.intf.car.ICarMonthCardV1InnerServiceSMO;
import com.java110.intf.car.IParkingAreaV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询车辆能买的月卡
 */

@Service("queryCarMonthCardBmoImpl")
public class QueryCarMonthCardBmoImpl implements IIotCommonApiBmo {

    @Autowired
    private IParkingAreaV1InnerServiceSMO parkingAreaV1InnerServiceSMOImpl;

    @Autowired
    private ICarMonthCardV1InnerServiceSMO carMonthCardV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
        Assert.hasKeyAndValue(reqJson, "page", "page不能为空");
        Assert.hasKeyAndValue(reqJson, "row", "row不能为空");
        Assert.hasKeyAndValue(reqJson, "paNum", "paNum不能为空");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setNum(reqJson.getString("paNum"));
        parkingAreaDto.setCommunityId(reqJson.getString("communityId"));

        List<ParkingAreaDto> parkingAreaDtos = parkingAreaV1InnerServiceSMOImpl.queryParkingAreas(parkingAreaDto);

        if (ListUtil.isNull(parkingAreaDtos)) {
            throw new IllegalArgumentException("物联网未同步停车场");
        }

        CarMonthCardDto carMonthCardDto = BeanConvertUtil.covertBean(reqJson, CarMonthCardDto.class);
        carMonthCardDto.setPaId(parkingAreaDtos.get(0).getPaId());

        int count = carMonthCardV1InnerServiceSMOImpl.queryCarMonthCardsCount(carMonthCardDto);

        List<CarMonthCardDto> carMonthCardDtos = null;

        if (count > 0) {
            carMonthCardDtos = carMonthCardV1InnerServiceSMOImpl.queryCarMonthCards(carMonthCardDto);
        } else {
            carMonthCardDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, carMonthCardDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);

    }
}
