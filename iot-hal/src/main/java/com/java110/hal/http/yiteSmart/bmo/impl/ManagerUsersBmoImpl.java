package com.java110.hal.http.yiteSmart.bmo.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.bean.dto.room.RoomDto;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.hal.http.yiteSmart.bmo.IManagerUsersBmo;
import com.java110.intf.accessControl.IAccessControlFaceV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.community.IRoomV1InnerServiceSMO;
import com.java110.intf.user.IOwnerInnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ManagerUsersBmoImpl implements IManagerUsersBmo {

    Logger logger = LoggerFactory.getLogger(ManagerUsersBmoImpl.class);

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Autowired
    private IRoomV1InnerServiceSMO roomV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerInnerServiceSMO ownerInnerServiceSMOImpl;

    @Autowired
    private IAccessControlFaceV1InnerServiceSMO accessControlFaceV1InnerServiceSMOImpl;

    @Override
    public ResponseEntity<String> users(String buildingUid, String roomUid, String uuid) {

        logger.debug("管理机用户列表请求：building_uid={}, room_uid={}, uuid={}", buildingUid, roomUid, uuid);

        if (StringUtil.isEmpty(buildingUid) && StringUtil.isEmpty(roomUid)) {
            return buildErrorResponse(400, "缺少building_uid或room_uid参数");
        }

        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineCode(uuid);
        List<AccessControlDto> devices = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);
        if (ListUtil.isNull(devices)) {
            return buildErrorResponse(202, "设备不存在，请检查您的设备绑定信息");
        }
        AccessControlDto device = devices.get(0);

        List<RoomDto> allRooms = new ArrayList<>();

        if (!StringUtil.isEmpty(roomUid)) {
            RoomDto roomDto = new RoomDto();
            roomDto.setRoomId(roomUid);
            roomDto.setCommunityId(device.getCommunityId());
            List<RoomDto> rooms = roomV1InnerServiceSMOImpl.queryRooms(roomDto);
            if (!ListUtil.isNull(rooms)) {
                allRooms.addAll(rooms);
            }
        } else if (!StringUtil.isEmpty(buildingUid)) {
            RoomDto roomDto = new RoomDto();
            roomDto.setUnitId(buildingUid);
            roomDto.setCommunityId(device.getCommunityId());
            allRooms = roomV1InnerServiceSMOImpl.queryRooms(roomDto);
        }

        JSONArray usersArray = new JSONArray();

        if (!ListUtil.isNull(allRooms)) {
            for (RoomDto room : allRooms) {
                OwnerDto ownerDto = new OwnerDto();
                ownerDto.setRoomId(room.getRoomId());
                ownerDto.setCommunityId(device.getCommunityId());
                List<OwnerDto> owners = ownerInnerServiceSMOImpl.queryOwners(ownerDto);

                if (!ListUtil.isNull(owners)) {
                    for (OwnerDto owner : owners) {
                        JSONObject userObj = buildUser(owner, room, device.getCommunityId());
                        usersArray.add(userObj);
                    }
                }
            }
        }

        JSONObject result = new JSONObject();
        result.put("code", 200);
        result.put("msg", "");
        result.put("timestamp", DateUtil.getCurrentDate().getTime() / 1000);
        result.put("data", usersArray);

        logger.debug("管理机用户列表返回：{}", result.toJSONString());
        return new ResponseEntity<>(result.toJSONString(), HttpStatus.OK);
    }

    private JSONObject buildUser(OwnerDto owner, RoomDto room, String communityId) {
        JSONObject userObj = new JSONObject();
        userObj.put("user_room_uid", owner.getOwnerId() + "_" + room.getRoomId());
        userObj.put("user_uid", owner.getOwnerId());
        userObj.put("user_phone", owner.getLink() != null ? owner.getLink() : "");
        userObj.put("user_realname", owner.getName() != null ? owner.getName() : "");
        userObj.put("ur_identity", owner.getOwnerTypeCd() != null ? owner.getOwnerTypeCd() : "1");
        userObj.put("ur_uid", owner.getOwnerId() + "_" + room.getRoomId());

        String faceImage = "";
        try {
            AccessControlFaceDto faceDto = new AccessControlFaceDto();
            faceDto.setPersonId(owner.getOwnerId());
            faceDto.setCommunityId(communityId);
            List<AccessControlFaceDto> faces = accessControlFaceV1InnerServiceSMOImpl.queryAccessControlFaces(faceDto);
            if (!ListUtil.isNull(faces) && !StringUtil.isEmpty(faces.get(0).getFacePath())) {
                faceImage = faces.get(0).getFacePath();
            }
        } catch (Exception e) {
            logger.error("查询业主人脸图片失败: {}", e.getMessage());
        }
        userObj.put("face_image", faceImage);
        userObj.put("source", "手机APP");
        userObj.put("updated_at", owner.getCreateTime() != null ?
                String.valueOf(owner.getCreateTime().getTime() / 1000) : "0");

        long expired = 4294967295L;
        if (owner.getEndTime() != null && !owner.getEndTime().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date endDate = sdf.parse(owner.getEndTime());
                expired = endDate.getTime() / 1000;
            } catch (Exception e) {
                logger.error("解析业主到期时间失败: {}", owner.getEndTime());
            }
        }
        userObj.put("expired", expired);
        userObj.put("room_uid", room.getRoomId());
        userObj.put("name", "");
        userObj.put("name_en", "");
        userObj.put("sex", "");
        userObj.put("nation", "");
        userObj.put("certificate_type", "");
        userObj.put("certificate_no", "");
        userObj.put("birthday", "");
        userObj.put("register_address", "");
        userObj.put("personnel_type", owner.getOwnerTypeCd() != null ? owner.getOwnerTypeCd() : "");
        userObj.put("owner_type", "");
        userObj.put("relationship", "");
        userObj.put("diploma", "");
        userObj.put("marriage_status", "");
        userObj.put("political_status", "");
        userObj.put("work_unit", "");
        userObj.put("email", "");
        userObj.put("remark", "");
        return userObj;
    }

    private ResponseEntity<String> buildErrorResponse(int code, String msg) {
        JSONObject result = new JSONObject();
        result.put("code", code);
        result.put("msg", msg);
        result.put("timestamp", DateUtil.getCurrentDate().getTime() / 1000);
        result.put("data", new JSONArray());
        return new ResponseEntity<>(result.toJSONString(), HttpStatus.OK);
    }
}
