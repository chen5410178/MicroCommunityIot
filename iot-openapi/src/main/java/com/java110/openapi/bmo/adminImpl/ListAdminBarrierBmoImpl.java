package com.java110.openapi.bmo.adminImpl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.*;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.community.CommunityDto;
import com.java110.dto.parking.ParkingBoxAreaDto;
import com.java110.intf.barrier.IBarrierV1InnerServiceSMO;
import com.java110.intf.barrier.IParkingBoxAreaV1InnerServiceSMO;
import com.java110.intf.community.ICommunityV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotAdminApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service("listAdminBarrierBmoImpl")
public class ListAdminBarrierBmoImpl implements IIotAdminApiBmo {

    @Autowired
    private IBarrierV1InnerServiceSMO barrierV1InnerServiceSMOImpl;

    @Autowired
    private IParkingBoxAreaV1InnerServiceSMO parkingBoxAreaV1InnerServiceSMOImpl;

    @Autowired
    private ICommunityV1InnerServiceSMO communityV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "page", "未包含page");
        Assert.hasKeyAndValue(reqJson, "row", "未包含row");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        BarrierDto barrierDto = BeanConvertUtil.covertBean(reqJson, BarrierDto.class);

        containPaId(reqJson,barrierDto);

        int count = barrierV1InnerServiceSMOImpl.queryBarriersCount(barrierDto);

        List<BarrierDto> barrierDtos = null;

        if (count > 0) {
            barrierDtos = barrierV1InnerServiceSMOImpl.queryBarriers(barrierDto);
            freshMachineStateName(barrierDtos);
        } else {
            barrierDtos = new ArrayList<>();
        }

        refreshCommunityName(barrierDtos);

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, barrierDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }

    private void refreshCommunityName(List<BarrierDto> barrierDtos) {

        if(ListUtil.isNull(barrierDtos)){
            return;
        }

        List<String> communityIds = new ArrayList<>();
        for (BarrierDto barrierDto : barrierDtos) {
            communityIds.add(barrierDto.getCommunityId());
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
        for (BarrierDto barrierDto : barrierDtos) {
            for (CommunityDto tCommunityDto : communityDtos) {
                if (!barrierDto.getCommunityId().equals(tCommunityDto.getCommunityId())) {
                    continue;
                }
                barrierDto.setCommunityName(tCommunityDto.getName());
            }
        }
    }

    private void containPaId(JSONObject reqJson, BarrierDto barrierDto) {

        if(!reqJson.containsKey("paId")){
            return;
        }

        String paId = reqJson.getString("paId");
        if(StringUtil.isEmpty(paId)){
            return ;
        }

        ParkingBoxAreaDto parkingBoxAreaDto = new ParkingBoxAreaDto();
        parkingBoxAreaDto.setPaId(paId);
        List<ParkingBoxAreaDto> parkingBoxAreaDtos = parkingBoxAreaV1InnerServiceSMOImpl.queryParkingBoxAreas(parkingBoxAreaDto);
        if(ListUtil.isNull(parkingBoxAreaDtos)){
            return ;
        }
        List<String> boxIds = new ArrayList<>();

        for(ParkingBoxAreaDto tmpParkingBoxAreaDto: parkingBoxAreaDtos){
            boxIds.add(tmpParkingBoxAreaDto.getBoxId());
        }

        barrierDto.setBoxIds(boxIds.toArray(new String[boxIds.size()]));

    }

    private void freshMachineStateName(List<BarrierDto> barrierDtos) {

        if (barrierDtos == null || barrierDtos.size() < 1) {
            return;
        }
        for (BarrierDto barrierDto : barrierDtos) {
            String heartbeatTime = barrierDto.getHeartbeatTime();
            try {
                if (StringUtil.isEmpty(heartbeatTime)) {
                    barrierDto.setStateName("设备离线");
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(DateUtil.getDateFromString(heartbeatTime, DateUtil.DATE_FORMATE_STRING_A));
                    calendar.add(Calendar.MINUTE, 2);
                    if (calendar.getTime().getTime() <= DateUtil.getCurrentDate().getTime()) {
                        barrierDto.setStateName("设备离线");
                    } else {
                        barrierDto.setStateName("设备在线");
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
                barrierDto.setStateName("设备离线");
            }
        }
    }
}
