package com.java110.hal.http.yiteSmart.bmo.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.owner.OwnerRoomRelDto;
import com.java110.bean.dto.room.RoomDto;
import com.java110.bean.dto.unit.UnitDto;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.hal.http.yiteSmart.YiteSmartHal;
import com.java110.hal.http.yiteSmart.bmo.ISyncUserBidBmo;
import com.java110.intf.accessControl.IAccessControlFaceV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.community.IRoomV1InnerServiceSMO;
import com.java110.intf.community.IUnitInnerServiceSMO;
import com.java110.intf.community.IUnitV1InnerServiceSMO;
import com.java110.intf.user.IOwnerRoomRelV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SyncUserBidBmoImpl implements ISyncUserBidBmo {
    Logger logger = LoggerFactory.getLogger(YiteSmartHal.class);

    @Autowired
    private IRoomV1InnerServiceSMO roomV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerRoomRelV1InnerServiceSMO ownerRoomRelV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlFaceV1InnerServiceSMO accessControlFaceV1InnerServiceSMOImpl;

    @Autowired
    private IUnitInnerServiceSMO unitInnerServiceSMOImpl;

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Override
    public ResponseEntity<String> sync(String unitId, String machineCode) {
        if ("-1".equals(unitId)) {
            return syncStaff(unitId, machineCode);
        } else {
            return syncOwner(unitId);
        }
    }

    private ResponseEntity<String> syncStaff(String unitId, String machineCode) {
        long curTimeTimeStamp = new Date().getTime();
        curTimeTimeStamp = curTimeTimeStamp / 1000;


        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", curTimeTimeStamp);
        JSONObject data = new JSONObject();
        paramOut.put("data", data);

        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineCode(machineCode);
        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);

        if (ListUtil.isNull(accessControlDtos)) {
            throw new IllegalArgumentException("设备不存在");
        }


        AccessControlFaceDto accessControlFaceDto = new AccessControlFaceDto();
        accessControlFaceDto.setMachineId(accessControlDtos.get(0).getMachineId());
        accessControlFaceDto.setPersonType(AccessControlFaceDto.PERSON_TYPE_STAFF);
        List<AccessControlFaceDto> accessControlFaceDtos = accessControlFaceV1InnerServiceSMOImpl.queryYiteAccessControlFaces(accessControlFaceDto);
        if (ListUtil.isNull(accessControlFaceDtos)) {
            return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
        }


        JSONArray rooms = new JSONArray();
        JSONObject room = new JSONObject();
        room.put("b_id", unitId);
        room.put("b_name", "物业中心");

        JSONArray addRooms = new JSONArray();
        room.put("add", addRooms);

        JSONObject del = new JSONObject();
        JSONArray del_room = new JSONArray();
        del.put("del_room", del_room);
        JSONArray del_user = new JSONArray();
        del.put("del_user", del_user);
        room.put("del", del);

        rooms.add(room);
        data.put("rooms", rooms);
        JSONArray users = null;
        JSONObject addRoom = null;
        /**
         *  {\n" +
         *                       \"id\": 392188,\n" +
         *                       \"uid\": \"20ce9b7368ad918515650baee75420fd\",\n" +
         *                       \"name\": \"1003\",\n" +
         *                       \"num\": \"0101-1003\",\n" +
         *                       \"user\": [\n" +
         *                           {\n" +
         *                               \"id\": 416290,\n" +
         *                               \"uid\": \"6003e234c6f988a6fc7ab5275f766d1e\",\n" +
         *                               \"phone\": \"17782426083\",\n" +
         *                               \"expired\": " + timestrap + "\n" +
         *                           },\n" +
         *                           {\n" +
         *                               \"id\": 1004352,\n" +
         *                               \"uid\": \"7247871cfb24f48bf1877d8f6795cc23\",\n" +
         *                               \"phone\": \"17728043791\",\n" +
         *                               \"expired\": " + timestrap + "\n" +
         *                           }\n" +
         *                       ]\n" +
         *                   }
         */
        JSONObject user = null;

        addRoom = new JSONObject();
        addRoom.put("id", 1);
        addRoom.put("uid", 1);
        addRoom.put("name", "管理处");//修改物业中心名称
        addRoom.put("num", "9909-909");//添加楼栋单元号
        users = new JSONArray();
        addRoom.put("user", users);

        for (AccessControlFaceDto tmpAccessControlFaceDto : accessControlFaceDtos) {
            user = new JSONObject();
            user.put("id", tmpAccessControlFaceDto.getPersonId());
            user.put("uid", tmpAccessControlFaceDto.getPersonId());
            user.put("phone", tmpAccessControlFaceDto.getStaffLink());
            long timestrap = DateUtil.getDateFromStringA(tmpAccessControlFaceDto.getEndTime()).getTime();
            timestrap = timestrap / 1000;
            user.put("expired", timestrap);
            users.add(user);

        }
        addRooms.add(addRoom);


        logger.debug("/v3_1/sync/user/add/：{}", paramOut.toString());

        logger.debug("用户返回报文：{}", paramOut.toString());
        return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
    }

    private ResponseEntity<String> syncOwner(String unitId) {

        long curTimeTimeStamp = new Date().getTime();
        curTimeTimeStamp = curTimeTimeStamp / 1000;

        UnitDto unitDto = new UnitDto();
        unitDto.setUnitId(unitId);
        List<UnitDto> unitDtos = unitInnerServiceSMOImpl.queryUnits(unitDto);

        if (ListUtil.isNull(unitDtos)) {
            throw new IllegalArgumentException("单元错误");
        }

        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", curTimeTimeStamp);
        JSONObject data = new JSONObject();
        paramOut.put("data", data);

        RoomDto roomDto = new RoomDto();
        roomDto.setUnitId(unitId);
        List<RoomDto> roomDtos = roomV1InnerServiceSMOImpl.queryRooms(roomDto);
        if (ListUtil.isNull(roomDtos)) {
            return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
        }

        List<String> roomIds = new ArrayList<>();
        for (RoomDto tmpRoomDto : roomDtos) {
            roomIds.add(tmpRoomDto.getRoomId());
        }

        OwnerRoomRelDto ownerRoomRelDto = new OwnerRoomRelDto();
        ownerRoomRelDto.setRoomIds(roomIds.toArray(new String[roomIds.size()]));
        List<OwnerRoomRelDto> ownerRoomRelDtos = ownerRoomRelV1InnerServiceSMOImpl.queryOwnerRoomRels(ownerRoomRelDto);


        JSONArray rooms = new JSONArray();
        JSONObject room = new JSONObject();
        room.put("b_id", unitId);
        room.put("b_name", unitDtos.get(0).getFloorNum() + "栋" + unitDtos.get(0).getUnitNum() + "单元");

        JSONArray addRooms = new JSONArray();
        room.put("add", addRooms);

        JSONObject del = new JSONObject();
        JSONArray del_room = new JSONArray();
        del.put("del_room", del_room);
        JSONArray del_user = new JSONArray();
        del.put("del_user", del_user);
        room.put("del", del);

        rooms.add(room);
        data.put("rooms", rooms);
        JSONArray users = null;
        JSONObject addRoom = null;
        /**
         *  {\n" +
         *                       \"id\": 392188,\n" +
         *                       \"uid\": \"20ce9b7368ad918515650baee75420fd\",\n" +
         *                       \"name\": \"1003\",\n" +
         *                       \"num\": \"0101-1003\",\n" +
         *                       \"user\": [\n" +
         *                           {\n" +
         *                               \"id\": 416290,\n" +
         *                               \"uid\": \"6003e234c6f988a6fc7ab5275f766d1e\",\n" +
         *                               \"phone\": \"17782426083\",\n" +
         *                               \"expired\": " + timestrap + "\n" +
         *                           },\n" +
         *                           {\n" +
         *                               \"id\": 1004352,\n" +
         *                               \"uid\": \"7247871cfb24f48bf1877d8f6795cc23\",\n" +
         *                               \"phone\": \"17728043791\",\n" +
         *                               \"expired\": " + timestrap + "\n" +
         *                           }\n" +
         *                       ]\n" +
         *                   }
         */
        JSONObject user = null;
        for (RoomDto tmpRoomDto : roomDtos) {
            addRoom = new JSONObject();
            addRoom.put("id", tmpRoomDto.getRoomId());
            addRoom.put("uid", tmpRoomDto.getRoomId());
            addRoom.put("name", tmpRoomDto.getRoomNum());
            //addRoom.put("num", unitDtos.get(0).getFloorNum() + unitDtos.get(0).getUnitNum() + "-" + tmpRoomDto.getRoomNum());//添加楼栋单元号
			addRoom.put("num", String.format("%02d%02d-%s",
			    Integer.parseInt(unitDtos.get(0).getFloorNum()),
			    Integer.parseInt(unitDtos.get(0).getUnitNum()),
			    Integer.parseInt(tmpRoomDto.getRoomNum())));
			
            users = new JSONArray();
            addRoom.put("user", users);
            if (ListUtil.isNull(ownerRoomRelDtos)) {
                continue;
            }

            for (OwnerRoomRelDto tmpOwnerRoomRelDto : ownerRoomRelDtos) {
                if (tmpRoomDto.getRoomId().equals(tmpOwnerRoomRelDto.getRoomId())) {
                    user = new JSONObject();
                    user.put("id", tmpOwnerRoomRelDto.getOwnerId());
                    user.put("uid", tmpOwnerRoomRelDto.getOwnerId());
                    user.put("phone", tmpOwnerRoomRelDto.getLink());
                    long timestrap = tmpOwnerRoomRelDto.getEndTime().getTime();
                    timestrap = timestrap / 1000;
                    user.put("expired", timestrap);
                    users.add(user);
                }
            }
            addRooms.add(addRoom);
        }

        logger.debug("/v3_1/sync/user/add/：{}", paramOut.toString());

        logger.debug("用户返回报文：{}", paramOut.toString());
        return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
    }
}
