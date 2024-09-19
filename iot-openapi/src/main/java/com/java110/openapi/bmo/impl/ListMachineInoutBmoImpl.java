package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.Assert;
import com.java110.dto.accessControlInout.AccessControlInoutDto;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.intf.accessControl.IAccessControlInoutV1InnerServiceSMO;
import com.java110.intf.barrier.ICarInoutV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service("listMachineInoutBmoImpl")
public class ListMachineInoutBmoImpl implements IIotCommonApiBmo {

    @Autowired
    private ICarInoutV1InnerServiceSMO carInoutV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlInoutV1InnerServiceSMO accessControlInoutV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区");
        Assert.hasKeyAndValue(reqJson, "startDate", "未包含开始时间");
        Assert.hasKeyAndValue(reqJson, "endDate", "未包含结束时间");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {

        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCommunityId(reqJson.getString("communityId"));
        carInoutDto.setStartTime(reqJson.getString("startDate"));
        carInoutDto.setEndTime(reqJson.getString("endDate"));

        int carInCount = carInoutV1InnerServiceSMOImpl.getCarInCount(carInoutDto);

        carInoutDto = new CarInoutDto();
        carInoutDto.setCommunityId(reqJson.getString("communityId"));
        carInoutDto.setStartTime(reqJson.getString("startDate"));
        carInoutDto.setEndTime(reqJson.getString("endDate"));

        int carOutCount = carInoutV1InnerServiceSMOImpl.getCarOutCount(carInoutDto);


        AccessControlInoutDto accessControlInoutDto = new AccessControlInoutDto();
        accessControlInoutDto.setCommunityId(reqJson.getString("communityId"));
        accessControlInoutDto.setQueryStartTime(reqJson.getString("startDate"));
        accessControlInoutDto.setQueryEndTime(reqJson.getString("endDate"));
        int personInCount = accessControlInoutV1InnerServiceSMOImpl.getPersonInCount(accessControlInoutDto);


        //todo 查询人员进场

        JSONObject data = new JSONObject();
        data.put("carInCount", carInCount);
        data.put("carOutCount", carOutCount);
        data.put("personInCount", personInCount);

        context.setResponseEntity(ResultVo.createResponseEntity(data));
    }
}
