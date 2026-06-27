package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.bean.dto.owner.OwnerRoomRelDto;
import com.java110.bean.dto.room.RoomDto;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.accessControlFloor.AccessControlFloorDto;
import com.java110.dto.accessControlLog.AccessControlLogDto;
import com.java110.intf.accessControl.IAccessControlFaceV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlFloorV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlLogV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.community.IRoomV1InnerServiceSMO;
import com.java110.intf.user.IOwnerRoomRelV1InnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import com.java110.po.accessControlLog.AccessControlLogPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("applyYiteVisitorPassQrcodeBmoImpl")
public class ApplyYiteVisitorPassQrcodeBmoImpl implements IIotCommonApiBmo {

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlFloorV1InnerServiceSMO accessControlFloorV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlLogV1InnerServiceSMO accessControlLogV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlFaceV1InnerServiceSMO accessControlFaceV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerRoomRelV1InnerServiceSMO ownerRoomRelV1InnerServiceSMOImpl;

    @Autowired
    private IRoomV1InnerServiceSMO roomV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区信息");
        Assert.hasKeyAndValue(reqJson, "memberId", "未包含人员信息");
        Assert.hasKeyAndValue(reqJson, "roomId", "未包含房屋信息");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        String communityId = reqJson.getString("communityId");
        String memberId = reqJson.getString("memberId");
        String roomId = reqJson.getString("roomId");

        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setMemberId(memberId);
        ownerDto.setCommunityId(communityId);
        List<OwnerDto> ownerDtos = ownerV1InnerServiceSMOImpl.queryOwners(ownerDto);
        Assert.listOnlyOne(ownerDtos, "业主不存在");

        String userName = ownerDtos.get(0).getName();
        String ownerId = ownerDtos.get(0).getOwnerId();

        OwnerRoomRelDto ownerRoomRelDto = new OwnerRoomRelDto();
        ownerRoomRelDto.setOwnerId(ownerId);
        ownerRoomRelDto.setRoomId(roomId);
        List<OwnerRoomRelDto> ownerRoomRelDtos = ownerRoomRelV1InnerServiceSMOImpl.queryOwnerRoomRels(ownerRoomRelDto);
        Assert.listOnlyOne(ownerRoomRelDtos, "未找到该业主的房屋关系");

        RoomDto roomDto = new RoomDto();
        roomDto.setRoomId(roomId);
        roomDto.setCommunityId(communityId);
        List<RoomDto> roomDtos = roomV1InnerServiceSMOImpl.queryRooms(roomDto);
        Assert.listOnlyOne(roomDtos, "房屋不存在");

        String roomName = roomDtos.get(0).getFloorNum() + "-" + roomDtos.get(0).getUnitNum() + "-" + roomDtos.get(0).getRoomNum();

        AccessControlFaceDto accessControlFaceDto = new AccessControlFaceDto();
        accessControlFaceDto.setPersonId(memberId);
        accessControlFaceDto.setCommunityId(communityId);
        List<AccessControlFaceDto> accessControlFaceDtos = accessControlFaceV1InnerServiceSMOImpl.queryAccessControlFaces(accessControlFaceDto);

        List<AccessControlFaceDto> filteredFaces = new ArrayList<>();
        for (AccessControlFaceDto face : accessControlFaceDtos) {
            if (roomName.equals(face.getRoomName())) {
                filteredFaces.add(face);
            }
        }
        if (filteredFaces.isEmpty()) {
            throw new IllegalArgumentException("未找到该人员在该房屋的门禁授权信息");
        }

        AccessControlFaceDto faceDto = filteredFaces.get(0);
        String machineCode = faceDto.getMachineCode();

        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineCode(machineCode);
        accessControlDto.setCommunityId(communityId);
        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);
        Assert.listOnlyOne(accessControlDtos, "门禁设备不存在");

        AccessControlFloorDto accessControlFloorDto = new AccessControlFloorDto();
        accessControlFloorDto.setMachineId(accessControlDtos.get(0).getMachineId());
        List<AccessControlFloorDto> accessControlFloorDtos = accessControlFloorV1InnerServiceSMOImpl.queryAccessControlFloors(accessControlFloorDto);

        int pwd = (int) (Math.random() * 900000) + 100000;
        String password = String.valueOf(pwd);

        String[] parts = roomName.split("-", 3);
        if (parts.length != 3) {
            throw new IllegalArgumentException("房屋名称格式错误，应为 楼栋-单元-房间号");
        }
        String ysfjh = parts[2];
        String thfjh = parts[2];
        switch (ysfjh) {
            case "1B01":
                thfjh = "2201";
                break;
            case "1B02":
                thfjh = "2202";
                break;
            case "1A01":
                thfjh = "2301";
                break;
            case "1A02":
                thfjh = "2302";
                break;
            case "17A1":
                thfjh = "2401";
                break;
            case "17A2":
                thfjh = "2402";
                break;
            default:
        }
        String roomnum = String.format("%02d%02d-%04d",
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(thfjh));

        long expiredTimestamp = System.currentTimeMillis() / 1000 + 86400;


        JSONObject passwordData = new JSONObject();
        passwordData.put("code", 200);
        passwordData.put("msg", "success");

        JSONObject data = new JSONObject();
        JSONArray passwordArray = new JSONArray();

        JSONObject pwdGroup = new JSONObject();
        pwdGroup.put("del", new JSONArray());

        JSONArray addArray = new JSONArray();
        JSONObject addItem = new JSONObject();
        addItem.put("pwd", password);
        addItem.put("expired", expiredTimestamp);
        addItem.put("roomnum", roomnum);
        addItem.put("limit", 2);
        addArray.add(addItem);

        pwdGroup.put("add", addArray);
        passwordArray.add(pwdGroup);
        data.put("password", passwordArray);
        passwordData.put("data", data);
        passwordData.put("timestamp", System.currentTimeMillis() / 1000);

        AccessControlLogPo accessControlLogPo = new AccessControlLogPo();
        accessControlLogPo.setLogId(GenerateCodeFactory.getGeneratorId("11"));
        accessControlLogPo.setLogAction("/v3_1/sync/passwords/add/sync");
        accessControlLogPo.setMachineId(accessControlDtos.get(0).getMachineId());
        accessControlLogPo.setCommunityId(communityId);
        accessControlLogPo.setState(AccessControlLogDto.STATE_REQ);
        accessControlLogPo.setReqParam(passwordData.toJSONString());
        accessControlLogPo.setLogCmd("VISITOR_PWD");
        accessControlLogPo.setUserName(userName);
        accessControlLogPo.setUserId(memberId);
        accessControlLogV1InnerServiceSMOImpl.saveAccessControlLog(accessControlLogPo);

        JSONObject mqttMsg = new JSONObject();
        mqttMsg.put("f", "server_php");
        mqttMsg.put("t", machineCode);
        mqttMsg.put("c", "syncPwd");
        JSONObject m = new JSONObject();
        m.put("p", password);
        m.put("t", GenerateCodeFactory.getUUID());
        mqttMsg.put("m", m);

        MqttFactory.publish("zghl_door/" + machineCode, mqttMsg.toJSONString());

        JSONObject resultData = new JSONObject();
        resultData.put("visitorPassword", password);
        resultData.put("expired", expiredTimestamp);
        resultData.put("expireTime", DateUtil.getFormatTimeStringA(new Date(expiredTimestamp * 1000)));
        resultData.put("expiredText", "24小时内有效");
        context.setResponseEntity(ResultVo.createResponseEntity(resultData));
    }
}
