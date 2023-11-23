package com.java110.acct.payment.business.meter;

import com.alibaba.fastjson.JSONObject;
import com.java110.acct.payment.IPaymentBusiness;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.Assert;
import com.java110.dto.communityMember.CommunityMemberDto;
import com.java110.dto.fee.FeeAttrDto;
import com.java110.dto.fee.FeeDto;
import com.java110.dto.meter.MeterMachineDto;
import com.java110.dto.payment.PaymentOrderDto;
import com.java110.intf.community.ICommunityMemberV1InnerServiceSMO;
import com.java110.intf.community.ICommunityV1InnerServiceSMO;
import com.java110.intf.community.IRoomV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineV1InnerServiceSMO;
import com.java110.intf.user.IOwnerRoomRelV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 欠费缴费
 */
@Service("preStoreMeter")
public class PreStoreMeterPaymentBusiness implements IPaymentBusiness {


    private final static Logger logger = LoggerFactory.getLogger(PreStoreMeterPaymentBusiness.class);

    public static final String CODE_PREFIX_ID = "10";



    @Autowired
    private IMeterMachineV1InnerServiceSMO meterMachineV1InnerServiceSMOImpl;

    @Autowired
    private ICommunityV1InnerServiceSMO communityV1InnerServiceSMOImpl;

    @Autowired
    private ICommunityMemberV1InnerServiceSMO communityMemberV1InnerServiceSMOImpl;


    @Autowired
    private IOwnerRoomRelV1InnerServiceSMO ownerRoomRelV1InnerServiceSMOImpl;


    @Autowired
    private IRoomV1InnerServiceSMO roomV1InnerServiceSMOImpl;


    @Override
    public PaymentOrderDto unified(ICmdDataFlowContext context, JSONObject reqJson) {

        Assert.hasKeyAndValue(reqJson, "machineId", "请求报文中未包含machineId");
        Assert.hasKeyAndValue(reqJson, "receivedAmount", "请求报文中未包含receivedAmount");
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId");

        MeterMachineDto meterMachineDto = new MeterMachineDto();
        meterMachineDto.setMachineId(reqJson.getString("machineId"));
        List<MeterMachineDto> meterMachineDtos = meterMachineV1InnerServiceSMOImpl.queryMeterMachines(meterMachineDto);

        Assert.listOnlyOne(meterMachineDtos, "表不存在");

        PaymentOrderDto paymentOrderDto = new PaymentOrderDto();
        paymentOrderDto.setOrderId(GenerateCodeFactory.getOId());
        paymentOrderDto.setMoney(reqJson.getDoubleValue("receivedAmount"));
        paymentOrderDto.setName(reqJson.getString("feeName"));

        reqJson.put("receivableAmount", reqJson.getDoubleValue("receivedAmount"));
        reqJson.put("receivedAmount", reqJson.getDoubleValue("receivedAmount"));
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


    }



}
