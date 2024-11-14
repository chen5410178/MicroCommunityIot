package com.java110.accessControl.manufactor.adapt.accessControl.dahua;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.accessControl.manufactor.AbstractAccessControlManufactorAdapt;
import com.java110.accessControl.manufactor.adapt.accessControl.HcAccessControlProcessAdapt;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.bean.dto.room.RoomDto;
import com.java110.core.client.RestTemplate;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.accessControlLog.AccessControlLogDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.accessControl.IAccessControlFaceV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlLogV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.community.IRoomV1InnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.accessControlFace.AccessControlFacePo;
import com.java110.po.accessControlLog.AccessControlLogPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 大华门禁 （网关版）
 * <p>
 * 门禁设备 部署海康网关设备后添加；
 */
@Service("dahuaGatewayAccessControlAdapt")
public class DahuaGatewayAccessControlAdapt extends AbstractAccessControlManufactorAdapt {
    private static Logger logger = LoggerFactory.getLogger(HcAccessControlProcessAdapt.class);

    public static final String ACTION = "DAHUA";

    public static final String SN = "{sn}";

    public static final String TOPIC_REQ = "mgateway/request";

    public static final String TOPIC_RES = "mgateway/response/dahua";

    public static final String CMD_HEARTBEAT = "heartbeat"; //todo 心跳指令
    public static final String CMD_REBOOT = "reboot"; //todo 心跳指令
    public static final String CMD_OPEN_DOOR = "openDoor"; //todo 心跳指令

    public static final String CMD_ADD_USER = "addUser";// todo 添加用户；
    public static final String CMD_UPDATE_USER = "updateUser";// todo 修改用户；
    public static final String CMD_DELETE_USER = "deleteUser";// todo 删除用户；

    public static final String CMD_FACE_RESULT = "faceResult"; // todo 开门记录

    public static final String CMD_QRCODE_OPEN_DOOR = "qrcodeOpenDoor"; // todo 二维码开门




    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;

    @Autowired
    private IRoomV1InnerServiceSMO roomV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlLogV1InnerServiceSMO accessControlLogV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlFaceV1InnerServiceSMO accessControlFaceV1InnerServiceSMOImpl;


    @Autowired
    private RestTemplate outRestTemplate;

    @Override
    public boolean addUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {

        String taskId = GenerateCodeFactory.getGeneratorId("11");
        JSONObject param = new JSONObject();
        param.put("cmd", CMD_ADD_USER);
        param.put("action", ACTION);
        param.put("sn", accessControlDto.getMachineCode());

        param.put("taskId", taskId);
        param.put("userId", accessControlFacePo.getPersonId());
        param.put("userName", accessControlFacePo.getName());
        param.put("pass", accessControlDto.getMachineMac());
        param.put("idcard", accessControlFacePo.getIdNumber());
        param.put("faceData", accessControlFacePo.getFacePath());
        param.put("idNumber", accessControlFacePo.getCardNumber());
        param.put("userType", accessControlFacePo.getPersonType());

        if (AccessControlFaceDto.PERSON_TYPE_OWNER.equals(accessControlFacePo.getState())) {
            getOwnerRooms(accessControlFacePo, param);
        } else {
            UserDto userDto = new UserDto();
            userDto.setUserId(accessControlFacePo.getPersonId());
            List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
            if (!ListUtil.isNull(userDtos)) {
                param.put("tel", userDtos.get(0).getTel());
            }
        }

        MqttFactory.publish(TOPIC_REQ, param.toJSONString());
        saveAddFaceLog(taskId, accessControlDto.getMachineId(), TOPIC_REQ + "/" + CMD_ADD_USER,
                accessControlDto.getCommunityId(), param.toJSONString(), accessControlFacePo.getPersonId(), accessControlFacePo.getName());
        return true;
    }

    @Override
    public boolean updateUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        deleteUser(accessControlDto, accessControlFacePo);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return addUser(accessControlDto, accessControlFacePo);
    }

    @Override
    public boolean deleteUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        String taskId = GenerateCodeFactory.getGeneratorId("11");
        JSONObject param = new JSONObject();
        param.put("cmd", CMD_DELETE_USER);
        param.put("action", ACTION);
        param.put("sn", accessControlDto.getMachineCode());
        param.put("pass", accessControlDto.getMachineMac());
        param.put("taskId", taskId);
        param.put("userId", accessControlFacePo.getPersonId());

        MqttFactory.publish(TOPIC_REQ, param.toJSONString());
        saveDeleteFaceLog(taskId, accessControlDto.getMachineId(), TOPIC_REQ + "/" + CMD_DELETE_USER,
                accessControlDto.getCommunityId(), param.toJSONString(), accessControlFacePo.getPersonId(), accessControlFacePo.getName());
        return true;
    }

    @Override
    public boolean openDoor(AccessControlDto accessControlDto) {

        String taskId = GenerateCodeFactory.getGeneratorId("11");
        JSONObject param = new JSONObject();
        param.put("cmd", CMD_OPEN_DOOR);
        param.put("action", ACTION);
        param.put("pass", accessControlDto.getMachineMac());
        param.put("sn", accessControlDto.getMachineCode());
        param.put("taskId", taskId);
        MqttFactory.publish(TOPIC_REQ, param.toJSONString());
        saveOpenDoorLog(taskId, accessControlDto.getMachineId(), TOPIC_REQ + "/" + CMD_OPEN_DOOR,
                accessControlDto.getCommunityId(), param.toJSONString(), accessControlDto.getUserId(), accessControlDto.getUserName());
        return true;
    }

    @Override
    public boolean restartMachine(AccessControlDto accessControlDto) {
        String taskId = GenerateCodeFactory.getGeneratorId("11");
        JSONObject param = new JSONObject();
        param.put("cmd", CMD_REBOOT);
        param.put("action", ACTION);
        param.put("sn", accessControlDto.getMachineCode());
        param.put("pass", accessControlDto.getMachineMac());
        param.put("taskId", taskId);
        MqttFactory.publish(TOPIC_REQ, param.toJSONString());
        saveRebootAcLog(taskId, accessControlDto.getMachineId(), TOPIC_REQ + "/" + CMD_REBOOT,
                accessControlDto.getCommunityId(), param.toJSONString(), accessControlDto.getUserId(), accessControlDto.getUserName());
        return true;
    }

    @Override
    public String accessControlResult(String topic, String param) {

        JSONObject paramIn = JSONObject.parseObject(param);
        String cmd = paramIn.getString("cmd");
        String taskId = paramIn.getString("taskId");
        String machineCode = paramIn.getString("sn");

        Assert.hasLength(cmd, "未包含cmd");
        Assert.hasLength(taskId, "未包含taskId");
        Assert.hasLength(machineCode, "未包含sn");

        switch (cmd) {
            case CMD_HEARTBEAT: // todo 心跳
                heartbeatHc(cmd, taskId, machineCode, paramIn);
                break;
            case CMD_REBOOT: //todo 重启返回
                response(cmd, taskId, machineCode, paramIn);
                break;
            case CMD_OPEN_DOOR: //todo 开门返回
                response(cmd, taskId, machineCode, paramIn);
                break;
            case CMD_ADD_USER: //todo 添加用户返回
                response(cmd, taskId, machineCode, paramIn);
                break;
            case CMD_UPDATE_USER: //todo 修改用户返回
                response(cmd, taskId, machineCode, paramIn);
                break;
            case CMD_DELETE_USER: //todo 删除用户返回
                response(cmd, taskId, machineCode, paramIn);
                break;
            case CMD_FACE_RESULT: //todo 开门记录
                faceResult(cmd, taskId, machineCode, paramIn);
                break;
            case CMD_QRCODE_OPEN_DOOR: //todo 二维码开门
                qrCodeOpenDoor(cmd, taskId, machineCode, paramIn);
                break;
        }
        return null;
    }

    /**
     * 心跳处理
     *
     * @param cmd
     * @param taskId
     * @param machineCode
     * @param paramIn
     */
    private void heartbeatHc(String cmd, String taskId, String machineCode, JSONObject paramIn) {
        heartbeat(machineCode, DateUtil.getCurrentDate());
//        JSONObject paramOut = new JSONObject();
//        paramOut.put("cmd", cmd);
//        paramOut.put("taskId", taskId);
//        paramOut.put("code", 0);
//        paramOut.put("msg", "处理成功");
//        paramOut.put("machineCode", machineCode);
//        MqttFactory.publish(TOPIC_REQ.replace(SN, machineCode), paramOut.toJSONString());
    }

    public void response(String cmd, String taskId, String machineCode, JSONObject paramIn) {

        //todo 更新下发日志
        AccessControlLogDto accessControlLogDto = new AccessControlLogDto();
        accessControlLogDto.setLogId(taskId);
        List<AccessControlLogDto> accessControlLogDtos = accessControlLogV1InnerServiceSMOImpl.queryAccessControlLogs(accessControlLogDto);
        if (ListUtil.isNull(accessControlLogDtos)) {
            return;
        }

        String state = AccessControlLogDto.STATE_RES;
        if (paramIn.getIntValue("code") != ResultVo.CODE_OK) {
            state = AccessControlLogDto.STATE_FAIL;
        }


        AccessControlLogPo accessControlLogPo = new AccessControlLogPo();
        accessControlLogPo.setLogId(accessControlLogDtos.get(0).getLogId());
        accessControlLogPo.setState(state);
        accessControlLogPo.setResParam(paramIn.getString("msg"));
        accessControlLogV1InnerServiceSMOImpl.updateAccessControlLog(accessControlLogPo);

        //todo 更新人员日志

        AccessControlFaceDto accessControlFaceDto = new AccessControlFaceDto();
        accessControlFaceDto.setPersonId(accessControlLogDtos.get(0).getUserId());
        accessControlFaceDto.setMachineCode(machineCode);
        accessControlFaceDto.setState(AccessControlFaceDto.STATE_WAIT);
        List<AccessControlFaceDto> accessControlFaceDtos = accessControlFaceV1InnerServiceSMOImpl.queryAccessControlFaces(accessControlFaceDto);
        if (ListUtil.isNull(accessControlFaceDtos)) {
            return;
        }

        state = AccessControlFaceDto.STATE_COMPLETE;
        if (paramIn.getIntValue("code") != ResultVo.CODE_OK) {
            state = AccessControlFaceDto.STATE_FAIL;
        }


        AccessControlFacePo accessControlFacePo = new AccessControlFacePo();
        accessControlFacePo.setMfId(accessControlFaceDtos.get(0).getMfId());
        accessControlFacePo.setState(state);
        accessControlFacePo.setMessage(paramIn.getString("msg"));
        accessControlFaceV1InnerServiceSMOImpl.updateAccessControlFace(accessControlFacePo);


    }
    /**
     * 查询业主房屋
     *
     * @param accessControlFacePo
     * @param param
     */
    private void getOwnerRooms(AccessControlFacePo accessControlFacePo, JSONObject param) {
        JSONArray rooms = new JSONArray();

        JSONObject room;
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setMemberId(accessControlFacePo.getPersonId());
        List<OwnerDto> ownerDtos = ownerV1InnerServiceSMOImpl.queryOwners(ownerDto);

        if (ListUtil.isNull(ownerDtos)) {
            throw new CmdException("业主不存在");
        }
        param.put("tel", ownerDtos.get(0).getLink());

        List<RoomDto> roomDtos = roomV1InnerServiceSMOImpl.queryRoomsByOwner(ownerDtos.get(0));
        if (!ListUtil.isNull(roomDtos)) {
            for (RoomDto roomDto : roomDtos) {
                room = new JSONObject();
                room.put("roomId", roomDto.getRoomId());
                room.put("roomName", roomDto.getFloorNum() + "-" + roomDto.getUnitNum() + "-" + roomDto.getRoomNum());
                rooms.add(room);
            }
        }
        param.put("rooms", rooms);
    }

    /**
     * 开门记录
     * <p>
     * {
     * “cmd”:”faceResult”,
     * “taskId”:”123123123123”,
     * “userId”:”12312312”,
     * “userName”:”张三”,
     * “faceData”:”sdfsdf”,
     * “state”:”0”,
     * “message”:”开门成功”,
     * “openTypeCd”:”1000”,
     * “machineCode”:”123123123123”
     * }
     *
     * @param cmd
     * @param taskId
     * @param machineCode
     * @param paramIn
     */
    private void faceResult(String cmd, String taskId, String machineCode, JSONObject paramIn) {
        logger.debug("门禁内容,{}", paramIn.toJSONString());

        String staffId = paramIn.getString("userId");
        String staffName = paramIn.getString("userName");
        String openTypeCd = paramIn.getString("openTypeCd");
        int similar = paramIn.getIntValue("similar");
        if (StringUtil.isEmpty(staffId)) {
            staffId = "-1";
            staffName = "未知";
        }
        String images = paramIn.getString("faceData");

        saveOpenDoorResult(machineCode, images, staffId, staffName, openTypeCd, similar);

        JSONObject paramOut = new JSONObject();
        paramOut.put("cmd", cmd);
        paramOut.put("taskId", taskId);
        paramOut.put("code", 0);
        paramOut.put("msg", "处理成功");
        paramOut.put("machineCode", machineCode);
        MqttFactory.publish(TOPIC_REQ, paramOut.toJSONString());

    }

    /**
     * 二维码开门请求
     * <p>
     * {
     * “cmd”:”qrcodeOpenDoor”,
     * “taskId”:”123123123123”,
     * “qrcode”:”xxxxxxx”,
     * “machineCode”:”123123123123”
     * }
     *
     * @param cmd
     * @param taskId
     * @param machineCode
     * @param paramIn
     */
    private void qrCodeOpenDoor(String cmd, String taskId, String machineCode, JSONObject paramIn) {

        String qrcode = paramIn.getString("qrcode");

        //todo 检查是否为通行码

        boolean hasOpenDoor = qrcodeCheck(qrcode, machineCode);
        if (!hasOpenDoor) {
            return;
        }

        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineCode(machineCode);
        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);
        if (ListUtil.isNull(accessControlDtos)) {
            return;
        }

        JSONObject param = new JSONObject();
        param.put("cmd", CMD_OPEN_DOOR);
        param.put("action", ACTION);
        param.put("pass", accessControlDtos.get(0).getMachineMac());
        param.put("sn", accessControlDtos.get(0).getMachineCode());
        param.put("taskId", taskId);
        MqttFactory.publish(TOPIC_REQ, param.toJSONString());
        saveOpenDoorLog(taskId, accessControlDtos.get(0).getMachineId(), TOPIC_REQ + "/" + CMD_OPEN_DOOR,
                accessControlDtos.get(0).getCommunityId(), param.toJSONString(), accessControlDtos.get(0).getUserId(), accessControlDtos.get(0).getUserName());
    }
}
