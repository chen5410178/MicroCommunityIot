package com.java110.car.cmd.parkingArea;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.parking.ParkingAreaDto;
import com.java110.dto.parkingRegion.ParkingRegionDto;
import com.java110.intf.car.IParkingAreaV1InnerServiceSMO;
import com.java110.intf.car.IParkingRegionV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@Java110Cmd(serviceCode = "parkingArea.queryParkingAreaAndRegions")
public class QueryParkingAreaAndRegionsCmd extends Cmd {

    @Autowired
    private IParkingAreaV1InnerServiceSMO parkingAreaV1InnerServiceSMOImpl;

    @Autowired
    private IParkingRegionV1InnerServiceSMO parkingRegionV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId节点");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {
        List<JSONObject> data = new ArrayList<>();

        ParkingAreaDto parkingAreaDto = BeanConvertUtil.covertBean(reqJson, ParkingAreaDto.class);
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaV1InnerServiceSMOImpl.queryParkingAreas(parkingAreaDto);

        if (parkingAreaDtos == null || parkingAreaDtos.size() < 1) {
            cmdDataFlowContext.setResponseEntity(ResultVo.createResponseEntity(data));
            return;
        }

        ParkingRegionDto parkingRegionDto = new ParkingRegionDto();
        parkingRegionDto.setCommunityId(reqJson.getString("communityId"));
        List<ParkingRegionDto> parkingRegionDtos = parkingRegionV1InnerServiceSMOImpl.queryParkingRegions(parkingRegionDto);

        for (ParkingAreaDto tmpParkingAreaDto : parkingAreaDtos) {
            JSONObject paJson = new JSONObject();
            paJson.put("paId", tmpParkingAreaDto.getPaId());
            paJson.put("num", tmpParkingAreaDto.getNum());
            paJson.put("prId", "");
            paJson.put("regionCode", "");
            data.add(paJson);

            if (parkingRegionDtos == null || parkingRegionDtos.size() < 1) {
                continue;
            }
            for (ParkingRegionDto tmpRegionDto : parkingRegionDtos) {
                if (!tmpParkingAreaDto.getPaId().equals(tmpRegionDto.getPaId())) {
                    continue;
                }
                JSONObject regionJson = new JSONObject();
                regionJson.put("paId", tmpParkingAreaDto.getPaId());
                regionJson.put("num", tmpParkingAreaDto.getNum());
                regionJson.put("prId", tmpRegionDto.getPrId());
                regionJson.put("regionCode", tmpRegionDto.getRegionCode());
                data.add(regionJson);
            }
        }

        cmdDataFlowContext.setResponseEntity(ResultVo.createResponseEntity(data));
    }
}
