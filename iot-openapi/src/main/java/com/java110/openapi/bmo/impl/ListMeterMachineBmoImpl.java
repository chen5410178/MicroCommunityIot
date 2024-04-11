package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.meter.MeterMachineDto;
import com.java110.dto.meter.MeterMachineSpecDto;
import com.java110.intf.meter.IMeterMachineSpecV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("listMeterMachineBmoImpl")
public class ListMeterMachineBmoImpl implements IIotCommonApiBmo {

    @Autowired
    private IMeterMachineV1InnerServiceSMO meterMachineV1InnerServiceSMOImpl;

    @Autowired
    private IMeterMachineSpecV1InnerServiceSMO meterMachineSpecV1InnerServiceSMOImpl;



    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区");
        Assert.hasKeyAndValue(reqJson, "page", "未包含page");
        Assert.hasKeyAndValue(reqJson, "row", "未包含row");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        MeterMachineDto meterMachineDto = BeanConvertUtil.covertBean(reqJson, MeterMachineDto.class);

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

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, meterMachineDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }

    private void queryMeterMachineDegree(List<MeterMachineDto> meterMachineDtos) {

        if (ListUtil.isNull(meterMachineDtos)) {
            return;
        }

        if (!MeterMachineDto.MACHINE_MODEL_RECHARGE.equals(meterMachineDtos.get(0).getMachineModel())) {
            return;
        }
        for (MeterMachineDto meterMachineDto : meterMachineDtos) {
            double degree = meterMachineV1InnerServiceSMOImpl.getMeterDegree(meterMachineDto);
            meterMachineDto.setDegree(degree + "");
        }
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
}
