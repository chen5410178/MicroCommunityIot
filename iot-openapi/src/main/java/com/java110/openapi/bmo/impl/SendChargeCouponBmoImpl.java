package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.DateUtil;
import com.java110.dto.couponPool.CouponPropertyUserDto;
import com.java110.intf.acct.ICouponPropertyUserV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import com.java110.po.couponPropertyUser.CouponPropertyUserPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 赠送两轮车充电券
 */
@Service("sendChargeCouponBmoImpl")
public class SendChargeCouponBmoImpl implements IIotCommonApiBmo {

    @Autowired
    private ICouponPropertyUserV1InnerServiceSMO couponPropertyUserV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区编号");
        Assert.hasKeyAndValue(reqJson, "cppId", "未包含cppId");
        Assert.hasKeyAndValue(reqJson, "couponName", "未包含优惠券名称");
        Assert.hasKeyAndValue(reqJson, "stock", "未包含优惠券数量");
        Assert.hasKeyAndValue(reqJson, "validityDay", "未包含优惠券有效期");
        Assert.hasKeyAndValue(reqJson, "tel", "未包含手机号");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {

        CouponPropertyUserPo couponPropertyUserPo = BeanConvertUtil.covertBean(reqJson, CouponPropertyUserPo.class);
        couponPropertyUserPo.setState(CouponPropertyUserDto.STATE_WAIT);
        couponPropertyUserPo.setCouponId(GenerateCodeFactory.getGeneratorId("10"));
        couponPropertyUserPo.setToType(CouponPropertyUserDto.TO_TYPE_CHARGE);
        couponPropertyUserPo.setStartTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_B));
        couponPropertyUserV1InnerServiceSMOImpl.saveCouponPropertyUser(couponPropertyUserPo);
        context.setResponseEntity(ResultVo.success());
    }
}
