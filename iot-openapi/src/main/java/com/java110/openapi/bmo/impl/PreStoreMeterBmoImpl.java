package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.Assert;
import com.java110.dto.communityMember.CommunityMemberDto;
import com.java110.dto.meter.MeterMachineDto;
import com.java110.intf.meter.IMeterMachineV1InnerServiceSMO;
import com.java110.intf.meter.IMeterTypeV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("preStoreMeterBmoImpl")
public class PreStoreMeterBmoImpl implements IIotCommonApiBmo {

    @Autowired
    private IMeterMachineV1InnerServiceSMO meterMachineV1InnerServiceSMOImpl;

    @Autowired
    private IMeterTypeV1InnerServiceSMO meterTypeV1InnerServiceSMOImpl;
    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson,"roomId","未包含房屋");
        Assert.hasKeyAndValue(reqJson,"communityId","未包含小区");
        Assert.hasKeyAndValue(reqJson, "receivedAmount", "请求报文中未包含receivedAmount");

    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {

        MeterMachineDto meterMachineDto = new MeterMachineDto();
        meterMachineDto.setRoomId(reqJson.getString("roomId"));
        List<MeterMachineDto> meterMachineDtos = meterMachineV1InnerServiceSMOImpl.queryMeterMachines(meterMachineDto);

        Assert.listOnlyOne(meterMachineDtos, "表不存在");



        double money = reqJson.getDoubleValue("receivedAmount");


        double degree = reqJson.getDoubleValue("degree");

        meterMachineDtos.get(0).setRechargeDegree(degree);
        meterMachineDtos.get(0).setRechargeMoney(money);

        //智能电表充值
        meterMachineV1InnerServiceSMOImpl.reChargeMeterMachines(meterMachineDtos.get(0));

    }
}
