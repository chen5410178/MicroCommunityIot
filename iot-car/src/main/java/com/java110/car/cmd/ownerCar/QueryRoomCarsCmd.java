package com.java110.car.cmd.ownerCar;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.car.OwnerCarDto;
import com.java110.bean.dto.owner.OwnerRoomRelDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.parking.ParkingSpaceDto;
import com.java110.intf.car.IOwnerCarInnerServiceSMO;
import com.java110.intf.car.IOwnerCarV1InnerServiceSMO;
import com.java110.intf.car.IParkingSpaceV1InnerServiceSMO;
import com.java110.intf.user.IOwnerRoomRelV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 查询房屋车辆，
 * 房屋关联业主 业主 关联车辆
 */
@Java110Cmd(serviceCode = "ownerCar.queryRoomCars")
public class QueryRoomCarsCmd extends Cmd {

    @Autowired
    private IOwnerRoomRelV1InnerServiceSMO ownerRoomRelV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerCarInnerServiceSMO ownerCarInnerServiceSMOImpl;

    @Autowired
    private IParkingSpaceV1InnerServiceSMO parkingSpaceInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        Assert.jsonObjectHaveKey(reqJson, "page", "请求中未包含page信息");
        Assert.jsonObjectHaveKey(reqJson, "row", "请求中未包含row信息");
        Assert.jsonObjectHaveKey(reqJson, "communityId", "请求中未包含communityId信息");
        Assert.jsonObjectHaveKey(reqJson, "roomId", "请求中未包含房屋ID信息");
        Assert.isInteger(reqJson.getString("page"), "不是有效数字");
        Assert.isInteger(reqJson.getString("row"), "不是有效数字");

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        //todo 根据房屋ID查询房屋人员信息

        OwnerRoomRelDto ownerRoomRelDto = new OwnerRoomRelDto();
        ownerRoomRelDto.setRoomId(reqJson.getString("roomId"));
        ownerRoomRelDto.setCommunityId(reqJson.getString("communityId"));
        List<OwnerRoomRelDto> ownerRoomRelDtos = ownerRoomRelV1InnerServiceSMOImpl.queryOwnerRoomRels(ownerRoomRelDto);

        if(ListUtil.isNull(ownerRoomRelDtos)){
            context.setResponseEntity(ResultVo.createResponseEntity(new ArrayList<>()));
            return ;
        }

        List<String> ownerIds = new ArrayList<>();
        for(OwnerRoomRelDto tOwnerRoomRelDto: ownerRoomRelDtos){
            ownerIds.add(tOwnerRoomRelDto.getOwnerId());
        }

        OwnerCarDto ownerCarDto = BeanConvertUtil.covertBean(reqJson,OwnerCarDto.class);
        ownerCarDto.setOwnerIds(ownerIds.toArray(new String[ownerIds.size()]));

        int total = ownerCarInnerServiceSMOImpl.queryOwnerCarsCount(ownerCarDto);
//        int count = 0;
        int row = reqJson.getInteger("row");
        List<OwnerCarDto> ownerCarDtoList = null;
        if (total > 0) {
            ownerCarDtoList = ownerCarInnerServiceSMOImpl.queryOwnerCars(ownerCarDto);
            //小区20条时刷房屋和车位信息
            if (row < 20) {
                freshPs(ownerCarDtoList);
            }
        } else {
            ownerCarDtoList = new ArrayList<>();
        }
        //查询是否有脱敏权限

        ResponseEntity<String> responseEntity = ResultVo.createResponseEntity((int) Math.ceil((double) total / (double) row), total, ownerCarDtoList);
        context.setResponseEntity(responseEntity);

    }

    private void freshPs(List<OwnerCarDto> ownerCarDtoList) {
        if (ownerCarDtoList == null || ownerCarDtoList.size() < 1) {
            return;
        }
        List<String> psIds = new ArrayList<>();
        for (OwnerCarDto ownerCarDto : ownerCarDtoList) {
            if (StringUtil.isEmpty(ownerCarDto.getPsId())) {
                continue;
            }
            psIds.add(ownerCarDto.getPsId());
        }
        ParkingSpaceDto parkingSpaceDto = new ParkingSpaceDto();
        parkingSpaceDto.setCommunityId(ownerCarDtoList.get(0).getCommunityId());
        parkingSpaceDto.setPsIds(psIds.toArray(new String[psIds.size()]));
        List<ParkingSpaceDto> parkingSpaceDtos = parkingSpaceInnerServiceSMOImpl.queryParkingSpaces(parkingSpaceDto);
        for (ParkingSpaceDto tmpParkingSpaceDto : parkingSpaceDtos) {
            for (OwnerCarDto ownerCarDto : ownerCarDtoList) {
                if (tmpParkingSpaceDto.getPsId().equals(ownerCarDto.getPsId())) {
                    ownerCarDto.setAreaNum(tmpParkingSpaceDto.getAreaNum());
                    ownerCarDto.setNum(tmpParkingSpaceDto.getNum());
                    ownerCarDto.setParkingType(tmpParkingSpaceDto.getParkingType());
                }
            }
        }
    }
}
