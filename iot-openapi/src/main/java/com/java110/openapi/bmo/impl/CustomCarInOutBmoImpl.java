package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.car.CarBlackWhiteDto;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.intf.barrier.ICarInoutV1InnerServiceSMO;
import com.java110.intf.car.ICarBlackWhiteV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("customCarInOutBmoImpl")
public class CustomCarInOutBmoImpl implements IIotCommonApiBmo {

    @Autowired
    private ICarInoutV1InnerServiceSMO carInoutV1InnerServiceSMOImpl;

    @Autowired
    private ICarBlackWhiteV1InnerServiceSMO carBlackWhiteV1InnerServiceSMOImpl;



    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含小区信息");
        Assert.hasKeyAndValue(reqJson, "machineId", "请求报文中未包含设备信息");
        Assert.hasKeyAndValue(reqJson, "carNum", "请求报文中未包含车牌号");
        Assert.hasKeyAndValue(reqJson, "type", "请求报文中未包含类型");

        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(reqJson.getString("carNum"));
        carInoutDto.setCommunityId(reqJson.getString("communityId"));
        carInoutDto.setStates(new String[]{
                CarInoutDto.STATE_IN,
                CarInoutDto.STATE_REPAY
        });
        int count = carInoutV1InnerServiceSMOImpl.queryCarInoutsCount(carInoutDto);

        //出场
        if(!"1101".equals(reqJson.getString("type"))) {
            Assert.hasKeyAndValue(reqJson,"payType","未包含支付方式");
            Assert.hasKeyAndValue(reqJson,"amount","未包含支付金额");
            if(count < 1){
                throw new CmdException("车辆未入场");
            }
        }else{
            if(count > 0){
                throw new CmdException("车辆已经在场，请先出场");
            }
            //进场时 判断是否为黑名单
            CarBlackWhiteDto carBlackWhiteDto = new CarBlackWhiteDto();
            carBlackWhiteDto.setCarNum(reqJson.getString("carNum"));
            count =  carBlackWhiteV1InnerServiceSMOImpl.queryCarBlackWhitesCount(carBlackWhiteDto);
            if(count > 0){
                throw new CmdException("黑名单车辆禁止入场");
            }
        }
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {

        ResultVo resultVo = carInoutV1InnerServiceSMOImpl.carInout(reqJson);
        context.setResponseEntity(ResultVo.createResponseEntity(resultVo));
    }
}
