package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.coupon.ParkingCouponCarDto;
import com.java110.bean.po.coupon.ParkingCouponCarPo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.couponPool.CouponPropertyUserDto;
import com.java110.dto.parking.ParkingAreaDto;
import com.java110.intf.acct.ICouponPropertyUserV1InnerServiceSMO;
import com.java110.intf.car.IParkingAreaV1InnerServiceSMO;
import com.java110.intf.car.IParkingCouponCarV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import com.java110.po.couponPropertyUser.CouponPropertyUserPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 赠送停车券
 */
@Service("sendCarCouponBmoImpl")
public class SendCarCouponBmoImpl implements IIotCommonApiBmo {

    @Autowired
    private IParkingAreaV1InnerServiceSMO parkingAreaV1InnerServiceSMOImpl;

    @Autowired
    private IParkingCouponCarV1InnerServiceSMO parkingCouponCarV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区编号");
        Assert.hasKeyAndValue(reqJson, "carNum", "未包含车辆");
        Assert.hasKeyAndValue(reqJson, "value", "未包含value");
        Assert.hasKeyAndValue(reqJson, "remark", "未包含备注");
        Assert.hasKeyAndValue(reqJson, "paNum", "未包含停车场");
        Assert.hasKeyAndValue(reqJson, "paId", "未包含停车场");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setNum(reqJson.getString("paNum"));
        parkingAreaDto.setCommunityId(reqJson.getString("communityId"));
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaV1InnerServiceSMOImpl.queryParkingAreas(parkingAreaDto);
        if (ListUtil.isNull(parkingAreaDtos)) {
            parkingAreaDto = new ParkingAreaDto();
            parkingAreaDto.setPaId(reqJson.getString("paId"));
            parkingAreaDto.setCommunityId(reqJson.getString("communityId"));
            parkingAreaDtos = parkingAreaV1InnerServiceSMOImpl.queryParkingAreas(parkingAreaDto);
        }
        if (ListUtil.isNull(parkingAreaDtos)) {
            throw new CmdException("未包含停车场");
        }

        ParkingCouponCarPo parkingCouponCarPo = BeanConvertUtil.covertBean(reqJson, ParkingCouponCarPo.class);
        parkingCouponCarPo.setPccId(GenerateCodeFactory.getGeneratorId("10"));
        parkingCouponCarPo.setCouponId(reqJson.getString("couponId"));
        parkingCouponCarPo.setCouponShopId(reqJson.getString("couponId"));
        parkingCouponCarPo.setCommunityId(reqJson.getString("communityId"));
        parkingCouponCarPo.setStartTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        parkingCouponCarPo.setEndTime(DateUtil.getAddDayString(DateUtil.getCurrentDate(), DateUtil.DATE_FORMATE_STRING_A, 1));
        parkingCouponCarPo.setPaId(parkingAreaDtos.get(0).getPaId());
        parkingCouponCarPo.setState(ParkingCouponCarDto.STATE_WAIT);
        parkingCouponCarPo.setTypeCd("1001"); // 时长赠送
        parkingCouponCarPo.setGiveWay("4004"); //物业缴费赠送
        //parkingCouponCarPo.setValue(value + "");
        parkingCouponCarPo.setCarNum(reqJson.getString("carNum"));
        //parkingCouponCarPo.setRemark(userDtos.get(0).getName() + "-" + userDtos.get(0).getTel() + "赠送");
        //parkingCouponCarPo.setShopId(userDtos.get(0).getUserId());

        parkingCouponCarV1InnerServiceSMOImpl.saveParkingCouponCar(parkingCouponCarPo);

        context.setResponseEntity(ResultVo.success());
    }
}
