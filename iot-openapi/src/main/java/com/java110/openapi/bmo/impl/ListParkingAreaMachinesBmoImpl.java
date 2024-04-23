package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ListUtil;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.parking.ParkingAreaDto;
import com.java110.dto.parking.ParkingBoxAreaDto;
import com.java110.intf.barrier.IBarrierV1InnerServiceSMO;
import com.java110.intf.barrier.IParkingBoxAreaV1InnerServiceSMO;
import com.java110.intf.car.IParkingAreaV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("listParkingAreaMachinesBmoImpl")
public class ListParkingAreaMachinesBmoImpl implements IIotCommonApiBmo {

    private static Logger logger = LoggerFactory.getLogger(ListParkingAreaMachinesBmoImpl.class);
    @Autowired
    private IBarrierV1InnerServiceSMO machineInnerServiceSMOImpl;

    @Autowired
    private IParkingBoxAreaV1InnerServiceSMO parkingBoxAreaV1InnerServiceSMOImpl;

    @Autowired
    private IParkingAreaV1InnerServiceSMO parkingAreaV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.jsonObjectHaveKey(reqJson, "communityId", "请求报文中未包含小区信息");
        Assert.jsonObjectHaveKey(reqJson, "paNum", "请求报文中未包含停车场");
        Assert.hasKeyAndValue(reqJson, "page", "未包含page");
        Assert.hasKeyAndValue(reqJson, "row", "未包含row");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setNum(reqJson.getString("paNum"));
        parkingAreaDto.setCommunityId(reqJson.getString("communityId"));
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaV1InnerServiceSMOImpl.queryParkingAreas(parkingAreaDto);
        if (ListUtil.isNull(parkingAreaDtos)) {
            throw new IllegalArgumentException("停车场不存在");
        }
        List<BarrierDto> machines = null;
        ParkingBoxAreaDto parkingBoxAreaDto = new ParkingBoxAreaDto();
        parkingBoxAreaDto.setCommunityId(reqJson.getString("communityId"));
        parkingBoxAreaDto.setPaId(parkingAreaDtos.get(0).getPaId());
        parkingBoxAreaDto.setDefaultArea(ParkingBoxAreaDto.DEFAULT_AREA_TRUE);
        List<ParkingBoxAreaDto> parkingBoxAreaDtos = parkingBoxAreaV1InnerServiceSMOImpl.queryParkingBoxAreas(parkingBoxAreaDto);

        if (ListUtil.isNull(parkingBoxAreaDtos)) {
            machines = new ArrayList<>();
            context.setResponseEntity(ResultVo.createResponseEntity(0, 0, machines));
            return;
        }

        List<String> boxIds = new ArrayList<>();

        for (ParkingBoxAreaDto tmpParkingBoxAreaDto : parkingBoxAreaDtos) {
            boxIds.add(tmpParkingBoxAreaDto.getBoxId());
        }

        BarrierDto machineDto = new BarrierDto();
        machineDto.setBoxIds(boxIds.toArray(new String[boxIds.size()]));
        machineDto.setCommunityId(reqJson.getString("communityId"));
        machineDto.setDirection(reqJson.getString("direction"));
        int count = machineInnerServiceSMOImpl.queryBarriersCount(machineDto);

        if (count > 0) {
            machines = machineInnerServiceSMOImpl.queryBarriers(machineDto);
        } else {
            machines = new ArrayList<>();
        }

        context.setResponseEntity(ResultVo.createResponseEntity((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, machines));

    }
}
