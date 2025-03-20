package com.java110.openapi.bmo.adminImpl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.community.CommunityDto;
import com.java110.dto.meterMachineCharge.MeterMachineChargeDto;
import com.java110.intf.community.ICommunityV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineChargeV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotAdminApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("listAdminMeterChargeBmoImpl")
public class ListAdminMeterChargeBmoImpl implements IIotAdminApiBmo {

    @Autowired
    private IMeterMachineChargeV1InnerServiceSMO meterMachineChargeV1InnerServiceSMOImpl;


    @Autowired
    private ICommunityV1InnerServiceSMO communityV1InnerServiceSMOImpl;
    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "page", "未包含page");
        Assert.hasKeyAndValue(reqJson, "row", "未包含row");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        MeterMachineChargeDto meterMachineChargeDto = BeanConvertUtil.covertBean(reqJson, MeterMachineChargeDto.class);
        String communityIdStr = reqJson.getString("communityIdStr");
        if(!StringUtil.isEmpty(communityIdStr)){
            meterMachineChargeDto.setCommunityIds(communityIdStr.split(","));
        }
        int count = meterMachineChargeV1InnerServiceSMOImpl.queryMeterMachineChargesCount(meterMachineChargeDto);

        List<MeterMachineChargeDto> meterMachineChargeDtos = null;

        if (count > 0) {
            meterMachineChargeDtos = meterMachineChargeV1InnerServiceSMOImpl.queryMeterMachineCharges(meterMachineChargeDto);
        } else {
            meterMachineChargeDtos = new ArrayList<>();
        }

        refreshCommunityName(meterMachineChargeDtos);

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, meterMachineChargeDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }

    private void refreshCommunityName(List<MeterMachineChargeDto> meterMachineChargeDtos) {
        if(ListUtil.isNull(meterMachineChargeDtos)){
            return;
        }

        List<String> communityIds = new ArrayList<>();
        for (MeterMachineChargeDto meterMachineChargeDto : meterMachineChargeDtos) {
            communityIds.add(meterMachineChargeDto.getCommunityId());
        }

        if(ListUtil.isNull(communityIds)){
            return ;
        }
        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityIds(communityIds.toArray(new String[communityIds.size()]));
        List<CommunityDto> communityDtos = communityV1InnerServiceSMOImpl.queryCommunitys(communityDto);
        if(ListUtil.isNull(communityDtos)){
            return;
        }
        for (MeterMachineChargeDto meterMachineChargeDto : meterMachineChargeDtos) {
            for (CommunityDto tCommunityDto : communityDtos) {
                if (!meterMachineChargeDto.getCommunityId().equals(tCommunityDto.getCommunityId())) {
                    continue;
                }
                meterMachineChargeDto.setCommunityName(tCommunityDto.getName());
            }
        }
    }
}
