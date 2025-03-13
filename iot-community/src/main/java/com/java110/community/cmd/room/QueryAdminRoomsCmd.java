package com.java110.community.cmd.room;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.floor.FloorDto;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.bean.dto.room.RoomDto;
import com.java110.bean.dto.unit.UnitDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.ResponseConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.*;
import com.java110.doc.annotation.*;
import com.java110.intf.community.IFloorInnerServiceSMO;
import com.java110.intf.community.IRoomInnerServiceSMO;
import com.java110.intf.community.IUnitInnerServiceSMO;
import com.java110.intf.user.IMenuV1InnerServiceSMO;
import com.java110.intf.user.IOwnerInnerServiceSMO;
import com.java110.intf.user.IOwnerRoomRelInnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Java110Cmd(serviceCode = "room.queryAdminRooms")
public class QueryAdminRoomsCmd extends Cmd {

    @Autowired
    private IUnitInnerServiceSMO unitInnerServiceSMOImpl;

    @Autowired
    private IFloorInnerServiceSMO floorInnerServiceSMOImpl;

    @Autowired
    private IRoomInnerServiceSMO roomInnerServiceSMOImpl;

    @Autowired
    private IOwnerInnerServiceSMO ownerInnerServiceSMOImpl;

    @Autowired
    private IOwnerRoomRelInnerServiceSMO ownerRoomRelInnerServiceSMOImpl;


    @Autowired
    private IMenuV1InnerServiceSMO menuInnerServiceSMOImpl;

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;


    protected static final int MAX_ROW = 10000;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        super.assertAdmin(cmdDataFlowContext);
        super.validatePageInfo(reqJson);
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {
        RoomDto roomDto = BeanConvertUtil.covertBean(reqJson, RoomDto.class);


        String roomId = "";
        String unitId = "";
        //todo 计算参数
        computeParam(reqJson, roomDto, roomId, unitId);

        if (reqJson.containsKey("flag") && "1".equals(reqJson.getString("flag"))) {
            if (reqJson.containsKey("roomNum") && !StringUtil.isEmpty(reqJson.getString("roomNum"))) {
                String[] roomNums = reqJson.getString("roomNum").split("-", 3);
                if (roomNums != null && roomNums.length == 3) {
                    roomDto.setFloorNum(roomNums[0]);
                    roomDto.setUnitNum(roomNums[1]);
                    roomDto.setRoomNum(roomNums[2]);
                } else {
                    roomDto.setRoomNum(reqJson.getString("roomNum"));
                }
            } else {
                roomDto.setUnitNum("");
                roomDto.setFloorNum("");
                roomDto.setRoomNum("");
            }
        }

        //add by wuxw 商铺 两个短线方式处理
        if (reqJson.containsKey("roomType") && "2020602".equals(reqJson.getString("roomType"))) {
            if (reqJson.containsKey("roomNum") && !StringUtil.isEmpty(reqJson.getString("roomNum"))) {
                String[] roomNums = reqJson.getString("roomNum").split("-", 2);
                if (roomNums != null && roomNums.length == 2) {
                    roomDto.setFloorNum(roomNums[0]);
                    roomDto.setUnitNum("0");
                    roomDto.setRoomNum(roomNums[1]);
                } else {
                    roomDto.setRoomNum(reqJson.getString("roomNum"));
                }
            } else {
                roomDto.setUnitNum("");
                roomDto.setFloorNum("");
                roomDto.setRoomNum("");
            }
        }

        //查询总记录数
        int total = roomInnerServiceSMOImpl.queryRoomsCount(roomDto);
        List<RoomDto> roomDtoList = null;
        if (total > 0) {
            roomDtoList = roomInnerServiceSMOImpl.queryRooms(roomDto);
            refreshRoomOwners(reqJson.getString("loginUserId"), reqJson.getString("communityId"), roomDtoList);
            // todo 计算房屋人员
            queryOwnerMemberCount(roomDtoList);
        } else {
            roomDtoList = new ArrayList<>();
        }
        ResultVo resultVo = new ResultVo((int) Math.ceil((double) total / (double) reqJson.getInteger("row")), total, roomDtoList);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        cmdDataFlowContext.setResponseEntity(responseEntity);
    }

    private void computeParam(JSONObject reqJson, RoomDto roomDto, String roomId, String unitId) {

        if (!"0".equals(reqJson.getString("flag"))) {
            return;
        }
        if (StringUtil.isEmpty(reqJson.getString("floorNum"))) {
            return;
        }
        if (StringUtil.isEmpty(reqJson.getString("unitNum"))) {
            return;
        }
        if (StringUtil.isEmpty(reqJson.getString("roomNum"))) {
            return;
        }

        FloorDto floorDto = new FloorDto();
        floorDto.setFloorNum(reqJson.getString("floorNum"));
        floorDto.setCommunityId(reqJson.getString("communityId"));
        List<FloorDto> floorDtos = floorInnerServiceSMOImpl.queryFloors(floorDto);
        if (ListUtil.isNull(floorDtos)) {
            return;
        }
        for (FloorDto floor : floorDtos) {
            UnitDto unitDto = new UnitDto();
            unitDto.setFloorId(floor.getFloorId());
            unitDto.setUnitNum(reqJson.getString("unitNum"));

            List<UnitDto> unitDtos = unitInnerServiceSMOImpl.queryUnits(unitDto);
            if (ListUtil.isNull(unitDtos)) {
                continue;
            }
            for (UnitDto unit : unitDtos) {
                RoomDto room = new RoomDto();
                room.setUnitId(unit.getUnitId());
                room.setRoomNum(reqJson.getString("roomNum"));
                room.setCommunityId(reqJson.getString("communityId"));
                List<RoomDto> roomDtos = roomInnerServiceSMOImpl.queryRooms(room);
                if (roomDtos != null && roomDtos.size() == 1) {
                    unitId = roomDtos.get(0).getUnitId();
                    roomId = roomDtos.get(0).getRoomId();
                }
            }
        }

        roomDto.setRoomId(roomId);
        roomDto.setUnitId(unitId);

    }


    /**
     * 刷入房屋业主信息
     *
     * @param roomDtos
     */
    private void refreshRoomOwners(String userId, String communityId, List<RoomDto> roomDtos) {

        /**
         * 量太大时 查询 会比较慢，如果其他地方有bug 切换 查询报表去，不能靠这个接口查询大量数据
         */
        if (roomDtos == null || roomDtos.size() > 20) {
            return;
        }
        List<String> roomIds = new ArrayList<>();
        for (RoomDto roomDto : roomDtos) {
            roomIds.add(roomDto.getRoomId());
        }
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setCommunityId(communityId);
        ownerDto.setRoomIds(roomIds.toArray(new String[roomIds.size()]));
        List<OwnerDto> ownerDtos = ownerInnerServiceSMOImpl.queryOwnersByRoom(ownerDto);
        String ownerName = "";
        String ownerId = "";
        for (RoomDto roomDto : roomDtos) {
            ownerName = "";
            ownerId = "";
            for (OwnerDto tmpOwnerDto : ownerDtos) {
                if (!roomDto.getRoomId().equals(tmpOwnerDto.getRoomId())) {
                    continue;
                }
                try {
                    roomDto.setStartTime(DateUtil.getDateFromString(tmpOwnerDto.getStartTime(), DateUtil.DATE_FORMATE_STRING_A));
                    roomDto.setEndTime(DateUtil.getDateFromString(tmpOwnerDto.getEndTime(), DateUtil.DATE_FORMATE_STRING_A));
                } catch (Exception e) {
                    //
                }
                ownerName += (tmpOwnerDto.getName() + "/");
                ownerId = tmpOwnerDto.getOwnerId();
                //对业主身份证号隐藏处理
            }
            roomDto.setOwnerId(ownerId);
            roomDto.setOwnerName(ownerName);
        }
    }


    /**
     * 查询 业主成员数
     *
     * @param roomDtos
     */
    private void queryOwnerMemberCount(List<RoomDto> roomDtos) {
        if (ListUtil.isNull(roomDtos)) {
            return;
        }
        List<String> roomIds = new ArrayList<>();
        for(RoomDto roomDto : roomDtos){
            roomIds.add(roomDto.getRoomId());
        }

        List<Map> memberCounts = ownerV1InnerServiceSMOImpl.queryOwnerMembersCount(roomIds);

        for (RoomDto roomDto : roomDtos) {
            if(StringUtil.isEmpty(roomDto.getOwnerId())){
                continue;
            }
            for (Map count : memberCounts) {
                if (roomDto.getRoomId().equals(count.get("roomId"))) {
                    roomDto.setMemberCount(count.get("memberCount").toString());
                }
            }
        }
    }
}
