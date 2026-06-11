package com.java110.hal.http.yiteSmart.bmo.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.room.RoomDto;
import com.java110.bean.dto.unit.UnitDto;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.hal.http.yiteSmart.bmo.IManagerRoomsBmo;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.community.IRoomV1InnerServiceSMO;
import com.java110.intf.community.IUnitV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManagerRoomsBmoImpl implements IManagerRoomsBmo {

    Logger logger = LoggerFactory.getLogger(ManagerRoomsBmoImpl.class);

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Autowired
    private IRoomV1InnerServiceSMO roomV1InnerServiceSMOImpl;

    @Autowired
    private IUnitV1InnerServiceSMO unitV1InnerServiceSMOImpl;

    @Override
    public ResponseEntity<String> rooms(String buildingUid, String uuid) {

        logger.debug("管理机房间列表请求：building_uid={}, uuid={}", buildingUid, uuid);

        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineCode(uuid);
        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);

        if (ListUtil.isNull(accessControlDtos)) {
            throw new IllegalArgumentException("管理机设备不存在");
        }

        RoomDto roomDto = new RoomDto();
        roomDto.setUnitId(buildingUid);
        roomDto.setCommunityId(accessControlDtos.get(0).getCommunityId());
        List<RoomDto> roomDtos = roomV1InnerServiceSMOImpl.queryRooms(roomDto);

        UnitDto unitDto = new UnitDto();
        unitDto.setUnitId(buildingUid);
        List<UnitDto> unitDtos = unitV1InnerServiceSMOImpl.queryUnits(unitDto);
        UnitDto unit = !ListUtil.isNull(unitDtos) ? unitDtos.get(0) : null;

        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime() / 1000);

        JSONArray data = new JSONArray();
        paramOut.put("data", data);

        if (!ListUtil.isNull(roomDtos)) {
            for (RoomDto room : roomDtos) {
                JSONObject roomJson = new JSONObject();
                roomJson.put("uid", room.getRoomId());
                roomJson.put("room_number", String.format("%04d", Integer.parseInt(room.getRoomNum())));

                String roomNamePath = room.getRoomNum();
                if (unit != null) {
                    roomNamePath = unit.getFloorNum() + "栋" + unit.getUnitNum() + "单元" + room.getRoomNum();
                }
                roomJson.put("room_name_path", room.getRoomNum());
                data.add(roomJson);
            }
        }

        logger.debug("管理机房间列表返回：{}", paramOut.toString());
        return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
    }
}
