package com.java110.car.cmd.ownerCar;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.car.OwnerCarDto;
import com.java110.bean.dto.owner.OwnerRoomRelDto;
import com.java110.bean.dto.room.RoomDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.intf.car.IOwnerCarInnerServiceSMO;
import com.java110.intf.community.IRoomInnerServiceSMO;
import com.java110.intf.user.IOwnerRoomRelInnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@Java110Cmd(serviceCode = "ownerCar.queryOwnerCars")
public class QueryOwnerCarsCmd extends Cmd {

    @Autowired
    private IOwnerCarInnerServiceSMO ownerCarInnerServiceSMOImpl;


    @Autowired
    private IOwnerRoomRelInnerServiceSMO ownerRoomRelInnerServiceSMOImpl;

    @Autowired
    private IRoomInnerServiceSMO roomInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {
        Assert.jsonObjectHaveKey(reqJson, "page", "请求中未包含page信息");
        Assert.jsonObjectHaveKey(reqJson, "row", "请求中未包含row信息");
        Assert.jsonObjectHaveKey(reqJson, "communityId", "请求中未包含communityId信息");
        Assert.isInteger(reqJson.getString("page"), "不是有效数字");
        Assert.isInteger(reqJson.getString("row"), "不是有效数字");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {
        int row = reqJson.getInteger("row");
        OwnerCarDto ownerCarDto = BeanConvertUtil.covertBean(reqJson, OwnerCarDto.class);
        if (reqJson.containsKey("carTypeCds")) {
            ownerCarDto.setCarTypeCd("");
            ownerCarDto.setCarTypeCds(reqJson.getString("carTypeCds").split(","));
        }
        //查询总记录数
        int total = ownerCarInnerServiceSMOImpl.queryOwnerCarsCount(ownerCarDto);
//        int count = 0;
        List<OwnerCarDto> ownerCarDtoList = null;
        if (total > 0) {
            ownerCarDtoList = ownerCarInnerServiceSMOImpl.queryOwnerCars(ownerCarDto);
            //小区20条时刷房屋信息
            if (row < 20) {
                freshRoomInfo(ownerCarDtoList);
                //刷入同步物联网状态
                //freshTransactionIotState(ownerCarDtoList);
            }
        } else {
            ownerCarDtoList = new ArrayList<>();
        }
        //查询是否有脱敏权限

        ResponseEntity<String> responseEntity = ResultVo.createResponseEntity((int) Math.ceil((double) total / (double) row), total, ownerCarDtoList);
        cmdDataFlowContext.setResponseEntity(responseEntity);
    }



    /**
     * 刷入房屋信息
     *
     * @param ownerCarDtos
     */
    private void freshRoomInfo(List<OwnerCarDto> ownerCarDtos) {
        for (OwnerCarDto ownerCarDto : ownerCarDtos) {
            doFreshRoomInfo(ownerCarDto);
        }
    }

    /**
     * 车位信息刷入房屋信息
     *
     * @param ownerCarDto
     */
    private void doFreshRoomInfo(OwnerCarDto ownerCarDto) {
        OwnerRoomRelDto ownerRoomRelDto = new OwnerRoomRelDto();
        ownerRoomRelDto.setOwnerId(ownerCarDto.getOwnerId());
        ownerRoomRelDto.setPage(1);
        ownerRoomRelDto.setRow(3); //只展示3个房屋以内 不然页面太乱
        List<OwnerRoomRelDto> ownerRoomRelDtos = ownerRoomRelInnerServiceSMOImpl.queryOwnerRoomRels(ownerRoomRelDto);
        if (ownerRoomRelDtos == null || ownerRoomRelDtos.size() < 1) {
            ownerCarDto.setRoomName("-");
            return;
        }
        List<String> roomIds = new ArrayList<>();
        for (OwnerRoomRelDto tOwnerRoomRelDto : ownerRoomRelDtos) {
            roomIds.add(tOwnerRoomRelDto.getRoomId());
        }
        RoomDto roomDto = new RoomDto();
        roomDto.setCommunityId(ownerCarDto.getCommunityId());
        roomDto.setRoomIds(roomIds.toArray(new String[roomIds.size()]));
        List<RoomDto> roomDtos = roomInnerServiceSMOImpl.queryRooms(roomDto);
        String roomName = "";
        for (RoomDto tRoomDto : roomDtos) {
            roomName += (tRoomDto.getFloorNum() + "栋" + tRoomDto.getUnitNum() + "单元" + tRoomDto.getRoomNum() + "室" + "/");
        }
        roomName = roomName.endsWith("/") ? roomName.substring(0, roomName.length() - 1) : roomName;
        ownerCarDto.setRoomName(roomName);
    }
}
