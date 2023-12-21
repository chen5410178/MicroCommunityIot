package com.java110.acct.payment.business.meter;

import com.alibaba.fastjson.JSONObject;
import com.java110.acct.payment.IPaymentBusiness;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.communityMember.CommunityMemberDto;
import com.java110.dto.meter.MeterMachineDto;
import com.java110.dto.meter.MeterTypeDto;
import com.java110.dto.meterMachineCharge.MeterMachineChargeDto;
import com.java110.dto.payment.PaymentOrderDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.community.ICommunityMemberV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineChargeV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineV1InnerServiceSMO;
import com.java110.intf.meter.IMeterTypeV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.meter.MeterMachinePo;
import com.java110.po.meterMachineCharge.MeterMachineChargePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service("meterRecharge")
public class MeterRechargePaymentBusiness implements IPaymentBusiness {

    public static final String CODE_PREFIX_ID = "10";

    @Autowired
    private IMeterMachineV1InnerServiceSMO meterMachineV1InnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IMeterTypeV1InnerServiceSMO meterTypeV1InnerServiceSMOImpl;

    @Autowired
    private ICommunityMemberV1InnerServiceSMO communityMemberV1InnerServiceSMOImpl;

    @Autowired
    private IMeterMachineChargeV1InnerServiceSMO meterMachineChargeV1InnerServiceSMOImpl;


    @Override
    public PaymentOrderDto unified(ICmdDataFlowContext context, JSONObject reqJson) {

        Assert.hasKeyAndValue(reqJson, "machineId", "请求报文中未包含machineId");
        Assert.hasKeyAndValue(reqJson, "receivedAmount", "请求报文中未包含receivedAmount");
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId");

        UserDto userDto = new UserDto();
        userDto.setUserId(reqJson.getString("cashierUserId"));
        userDto.setPage(1);
        userDto.setRow(1);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);

        if (ListUtil.isNull(userDtos)) {
            throw new CmdException("用户不存在");
        }

        MeterMachineDto meterMachineDto = new MeterMachineDto();
        meterMachineDto.setMachineId(reqJson.getString("machineId"));
        List<MeterMachineDto> meterMachineDtos = meterMachineV1InnerServiceSMOImpl.queryMeterMachines(meterMachineDto);

        Assert.listOnlyOne(meterMachineDtos, "表不存在");

        MeterTypeDto meterTypeDto = new MeterTypeDto();
        meterTypeDto.setTypeId(meterMachineDtos.get(0).getMeterType());
        meterTypeDto.setCommunityId(reqJson.getString("communityId"));
        List<MeterTypeDto> meterTypeDtos = meterTypeV1InnerServiceSMOImpl.queryMeterTypes(meterTypeDto);

        Assert.listOnlyOne(meterTypeDtos, "表类型不存在");


        PaymentOrderDto paymentOrderDto = new PaymentOrderDto();
        paymentOrderDto.setOrderId(GenerateCodeFactory.getOId());
        paymentOrderDto.setMoney(reqJson.getDoubleValue("receivedAmount"));
        paymentOrderDto.setName(reqJson.getString("feeName"));
        reqJson.put("receivableAmount", reqJson.getDoubleValue("receivedAmount"));
        reqJson.put("receivedAmount", reqJson.getDoubleValue("receivedAmount"));
        reqJson.put("price", meterTypeDtos.get(0).getPrice());
        return paymentOrderDto;
    }

    @Override
    public void notifyPayment(PaymentOrderDto paymentOrderDto, JSONObject reqJson) {

        MeterMachineDto meterMachineDto = new MeterMachineDto();
        meterMachineDto.setMachineId(reqJson.getString("machineId"));
        List<MeterMachineDto> meterMachineDtos = meterMachineV1InnerServiceSMOImpl.queryMeterMachines(meterMachineDto);

        Assert.listOnlyOne(meterMachineDtos, "表不存在");

        CommunityMemberDto communityMemberDto = new CommunityMemberDto();
        communityMemberDto.setCommunityId(meterMachineDtos.get(0).getCommunityId());
        communityMemberDto.setMemberTypeCd(CommunityMemberDto.MEMBER_TYPE_PROPERTY);
        List<CommunityMemberDto> communityMemberDtos = communityMemberV1InnerServiceSMOImpl.queryCommunityMembers(communityMemberDto);

        Assert.listOnlyOne(communityMemberDtos, "物业不存在");

        MeterMachineChargePo meterMachineChargePo = BeanConvertUtil.covertBean(reqJson, MeterMachineChargePo.class);
        meterMachineChargePo.setChargeId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));
        meterMachineChargePo.setChargeMoney(reqJson.getString("receivedAmount"));
        String chargeDegrees = new BigDecimal(reqJson.getString("receivedAmount")).divide(new BigDecimal(reqJson.getString("price")), 2, RoundingMode.HALF_UP).toString();
        meterMachineChargePo.setChargeDegrees(chargeDegrees);
        meterMachineChargePo.setState(MeterMachineChargeDto.STATE_COMPLETE);
        int flag = meterMachineChargeV1InnerServiceSMOImpl.saveMeterMachineCharge(meterMachineChargePo);

        if (flag < 1) {
            throw new CmdException("保存数据失败");
        }

        MeterMachinePo meterMachinePo = BeanConvertUtil.covertBean(meterMachineDtos.get(0), MeterMachinePo.class);
        meterMachinePo.setPrestoreDegrees(new BigDecimal(meterMachinePo.getPrestoreDegrees()).add(new BigDecimal(chargeDegrees)).toString());
        meterMachineV1InnerServiceSMOImpl.updateMeterMachine(meterMachinePo);
    }
}
