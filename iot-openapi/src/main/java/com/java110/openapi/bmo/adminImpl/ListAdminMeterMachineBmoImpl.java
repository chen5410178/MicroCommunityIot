package com.java110.openapi.bmo.adminImpl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.community.CommunityDto;
import com.java110.dto.meter.MeterMachineDto;
import com.java110.dto.meter.MeterMachineSpecDto;
import com.java110.intf.community.ICommunityV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineSpecV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotAdminApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("listAdminMeterMachineBmoImpl")
public class ListAdminMeterMachineBmoImpl implements IIotAdminApiBmo {

    @Autowired
    private IMeterMachineV1InnerServiceSMO meterMachineV1InnerServiceSMOImpl;

    @Autowired
    private IMeterMachineSpecV1InnerServiceSMO meterMachineSpecV1InnerServiceSMOImpl;



    @Autowired
    private ICommunityV1InnerServiceSMO communityV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "page", "未包含page");
        Assert.hasKeyAndValue(reqJson, "row", "未包含row");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        MeterMachineDto meterMachineDto = BeanConvertUtil.covertBean(reqJson, MeterMachineDto.class);
        String communityIdStr = reqJson.getString("communityIdStr");
        if(!StringUtil.isEmpty(communityIdStr)){
            meterMachineDto.setCommunityIds(communityIdStr.split(","));
        }
        int count = meterMachineV1InnerServiceSMOImpl.queryMeterMachinesCount(meterMachineDto);

        List<MeterMachineDto> meterMachineDtos = null;

        if (count > 0) {
            meterMachineDtos = meterMachineV1InnerServiceSMOImpl.queryMeterMachines(meterMachineDto);
            freshSpecs(meterMachineDtos);
            queryMeterMachineDegree(meterMachineDtos);
            //queryMachineState(meterMachineDtos);
        } else {
            meterMachineDtos = new ArrayList<>();
        }
        refreshCommunityName(meterMachineDtos);

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, meterMachineDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }

    private void queryMeterMachineDegree(List<MeterMachineDto> meterMachineDtos) {

//        if (ListUtil.isNull(meterMachineDtos)) {
//            return;
//        }
//
//        if (!MeterMachineDto.MACHINE_MODEL_RECHARGE.equals(meterMachineDtos.get(0).getMachineModel())) {
//            return;
//        }
//        for (MeterMachineDto meterMachineDto : meterMachineDtos) {
//            double degree = smartMeterCoreReadImpl.getMeterDegree(meterMachineDto);
//            meterMachineDto.setDegree(degree + "");
//        }
    }

    /**
     * 刷入配置
     *
     * @param meterMachineDtos
     */
    private void freshSpecs(List<MeterMachineDto> meterMachineDtos) {

        if (ListUtil.isNull(meterMachineDtos)) {
            return;
        }

        List<String> machineIds = new ArrayList<>();
        for (MeterMachineDto meterMachineDto : meterMachineDtos) {
            machineIds.add(meterMachineDto.getMachineId());
        }

        MeterMachineSpecDto meterMachineSpecDto = new MeterMachineSpecDto();
        meterMachineSpecDto.setMachineIds(machineIds.toArray(new String[machineIds.size()]));

        List<MeterMachineSpecDto> meterMachineSpecDtos = meterMachineSpecV1InnerServiceSMOImpl.queryMeterMachineSpecs(meterMachineSpecDto);
        if (ListUtil.isNull(meterMachineSpecDtos)) {
            return;
        }
        List<MeterMachineSpecDto> specs = null;
        for (MeterMachineDto meterMachineDto : meterMachineDtos) {
            specs = new ArrayList<>();
            for (MeterMachineSpecDto tmpMeterMachineFactorySpecDto : meterMachineSpecDtos) {
                if (meterMachineDto.getMachineId().equals(tmpMeterMachineFactorySpecDto.getMachineId())) {
                    specs.add(tmpMeterMachineFactorySpecDto);
                }
            }
            meterMachineDto.setSpecs(specs);
        }
    }

    private void refreshCommunityName(List<MeterMachineDto> meterMachineDtos) {
        if(ListUtil.isNull(meterMachineDtos)){
            return;
        }

        List<String> communityIds = new ArrayList<>();
        for (MeterMachineDto meterMachineDto : meterMachineDtos) {
            communityIds.add(meterMachineDto.getCommunityId());
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
        for (MeterMachineDto meterMachineDto : meterMachineDtos) {
            for (CommunityDto tCommunityDto : communityDtos) {
                if (!meterMachineDto.getCommunityId().equals(tCommunityDto.getCommunityId())) {
                    continue;
                }
                meterMachineDto.setCommunityName(tCommunityDto.getName());
            }
        }
    }
}
