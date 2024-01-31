package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.parking.ParkingBoxDto;
import com.java110.intf.barrier.IParkingBoxV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("listParkingBoxBmoImpl")
public class ListParkingBoxBmoImpl implements IIotCommonApiBmo {


    private static Logger logger = LoggerFactory.getLogger(ListParkingBoxBmoImpl.class);
    @Autowired
    private IParkingBoxV1InnerServiceSMO parkingBoxV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区ID");
        Assert.hasKeyAndValue(reqJson, "page", "未包含page");
        Assert.hasKeyAndValue(reqJson, "row", "未包含row");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        ParkingBoxDto parkingBoxDto = BeanConvertUtil.covertBean(reqJson, ParkingBoxDto.class);

        int count = parkingBoxV1InnerServiceSMOImpl.queryParkingBoxsCount(parkingBoxDto);

        List<ParkingBoxDto> parkingBoxDtos = null;

        if (count > 0) {
            parkingBoxDtos = parkingBoxV1InnerServiceSMOImpl.queryParkingBoxs(parkingBoxDto);
        } else {
            parkingBoxDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, parkingBoxDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);

    }
}
