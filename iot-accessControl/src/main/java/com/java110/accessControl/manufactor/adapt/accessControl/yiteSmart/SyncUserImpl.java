package com.java110.accessControl.manufactor.adapt.accessControl.yiteSmart;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.accessControlFloor.AccessControlFloorDto;
import com.java110.intf.accessControl.IAccessControlFaceV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlFloorV1InnerServiceSMO;
import com.java110.po.accessControlFace.AccessControlFacePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SyncUserImpl implements ISyncUser {

    @Autowired
    private IAccessControlFloorV1InnerServiceSMO accessControlFloorV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlFaceV1InnerServiceSMO accessControlFaceV1InnerServiceSMOImpl;

    @Override
    public JSONObject getAddUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {


        AccessControlFloorDto accessControlFloorDto = new AccessControlFloorDto();
        accessControlFloorDto.setMachineId(accessControlDto.getMachineId());
        List<AccessControlFloorDto> accessControlFloorDtos = accessControlFloorV1InnerServiceSMOImpl.queryAccessControlFloors(accessControlFloorDto);

        AccessControlFaceDto accessControlFaceDto = new AccessControlFaceDto();
        accessControlFaceDto.setMfId(accessControlFacePo.getMfId());
        List<AccessControlFaceDto> accessControlFaceDtos = accessControlFaceV1InnerServiceSMOImpl.queryYiteAccessControlFaces(accessControlFaceDto);


        long curTimeTimeStamp = new Date().getTime();
        curTimeTimeStamp = curTimeTimeStamp / 1000;


        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", curTimeTimeStamp);
        JSONObject data = new JSONObject();
        paramOut.put("data", data);


        JSONArray rooms = new JSONArray();
        JSONObject room = new JSONObject();
        if (AccessControlFaceDto.PERSON_TYPE_STAFF.equals(accessControlFacePo.getPersonType())) {
            room.put("b_id", "-1");
            room.put("b_name", "物业中心");
        } else {
            if (ListUtil.isNull(accessControlFloorDtos)) {
                throw new IllegalArgumentException("设备未关联单元");
            }
            room.put("b_id", accessControlFloorDtos.get(0).getUnitId());
            room.put("b_name", accessControlFloorDtos.get(0).getFloorNum() + "栋" + accessControlFloorDtos.get(0).getUnitNum() + "单元");

        }

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
        addRoom.put("id", accessControlFacePo.getPersonId());
        addRoom.put("uid", accessControlFacePo.getPersonId());
        addRoom.put("name", "9999");
        addRoom.put("num", "9999");
        if (AccessControlFaceDto.PERSON_TYPE_OWNER.equals(accessControlFacePo.getPersonType())) {
            String roomName = accessControlFaceDtos.get(0).getRoomName();
            if (!StringUtil.isEmpty(roomName) && roomName.split("-", 3).length == 3) {
                addRoom.put("name", roomName.split("-", 3)[2]);
                addRoom.put("num", roomName.split("-", 3)[2]);
            }
        }
        users = new JSONArray();
        addRoom.put("user", users);

        user = new JSONObject();
        user.put("id", accessControlFaceDtos.get(0).getPersonId());
        user.put("uid", accessControlFaceDtos.get(0).getPersonId());
        if (AccessControlFaceDto.PERSON_TYPE_STAFF.equals(accessControlFacePo.getPersonType())) {
            user.put("phone", accessControlFaceDtos.get(0).getStaffLink());
        } else {
            user.put("phone", accessControlFaceDtos.get(0).getOwnerLink());
        }
        long timestrap = DateUtil.getDateFromStringA(accessControlFacePo.getEndTime()).getTime();
        timestrap = timestrap / 1000;
        user.put("expired", timestrap);
        users.add(user);

        addRooms.add(addRoom);
        return paramOut;
    }

    /**
     * {
     * 	"code": 200,
     * 	"msg": "success",
     * 	"data": {
     * 		"rooms": [
     * 			            {
     * 				"b_id": 288,
     * 				"b_name": [
     * 					"2栋4单元"
     * 				],
     * 				"add": [],
     * 				"del": {
     * 					"del_room": [],
     * 					"del_user": [
     * 						1325929
     * 					]
     *                }
     *            }
     * 		]
     * 	},
     * 	"timestamp": 1626701606
     * }
     * @param accessControlDto
     * @param accessControlFacePo
     * @return
     */
    @Override
    public JSONObject getDeleteUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        AccessControlFloorDto accessControlFloorDto = new AccessControlFloorDto();
        accessControlFloorDto.setMachineId(accessControlDto.getMachineId());
        List<AccessControlFloorDto> accessControlFloorDtos = accessControlFloorV1InnerServiceSMOImpl.queryAccessControlFloors(accessControlFloorDto);

        AccessControlFaceDto accessControlFaceDto = new AccessControlFaceDto();
        accessControlFaceDto.setMfId(accessControlFacePo.getMfId());
        List<AccessControlFaceDto> accessControlFaceDtos = accessControlFaceV1InnerServiceSMOImpl.queryYiteAccessControlFaces(accessControlFaceDto);


        long curTimeTimeStamp = new Date().getTime();
        curTimeTimeStamp = curTimeTimeStamp / 1000;


        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "success");
        paramOut.put("timestamp", curTimeTimeStamp);
        JSONObject data = new JSONObject();
        paramOut.put("data", data);


        JSONArray rooms = new JSONArray();
        JSONObject room = new JSONObject();
        if (AccessControlFaceDto.PERSON_TYPE_STAFF.equals(accessControlFacePo.getPersonType())) {
            room.put("b_id", "-1");
            room.put("b_name", "物业中心");
        } else {
            if (ListUtil.isNull(accessControlFloorDtos)) {
                throw new IllegalArgumentException("设备未关联单元");
            }
            room.put("b_id", accessControlFloorDtos.get(0).getUnitId());
            room.put("b_name", accessControlFloorDtos.get(0).getFloorNum() + "栋" + accessControlFloorDtos.get(0).getUnitNum() + "单元");

        }

        JSONArray addRooms = new JSONArray();
        room.put("add", addRooms);

        JSONObject del = new JSONObject();
        JSONArray del_room = new JSONArray();
        del.put("del_room", del_room);
        JSONArray del_user = new JSONArray();
        del_user.add(accessControlFacePo.getPersonId());
        del.put("del_user", del_user);
        room.put("del", del);
        rooms.add(room);
        data.put("rooms", rooms);

        return paramOut;
    }
}
