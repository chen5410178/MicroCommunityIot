package com.java110.accessControl.manufactor.adapt.accessControl.wiegand;

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
import com.java110.core.utils.*;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControlAttr.AccessControlAttrDto;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.accessControlLog.AccessControlLogDto;
import com.java110.dto.data.NettyReplyDataDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.accessControl.IAccessControlAttrV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlFaceV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlLogV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.community.IRoomV1InnerServiceSMO;
import com.java110.intf.hal.INotifyNettyDataV1InnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.accessControlAttr.AccessControlAttrPo;
import com.java110.po.accessControlFace.AccessControlFacePo;
import com.java110.po.accessControlLog.AccessControlLogPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 微耕门禁
 * <p>
 * 主要实现 门禁卡下发和二维码开门功能
 */
@Service("wiegandGatewayAccessControlAdapt")
public class WiegandGatewayAccessControlAdapt extends AbstractAccessControlManufactorAdapt {
    private static Logger logger = LoggerFactory.getLogger(WiegandGatewayAccessControlAdapt.class);


    public static final String CMD_HEARTBEAT = "92"; //todo 心跳指令
    public static final String CMD_REBOOT = "reboot"; //todo 心跳指令
    public static final String CMD_OPEN_DOOR = "40"; //todo 开门

    public static final String CMD_ADD_USER = "50";// todo 权限添加或修改；
    public static final String CMD_DELETE_USER = "52";// todo 权限删除（单个删除）；

    public static final String CMD_FACE_RESULT = "20"; // todo 开门记录

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
    private IAccessControlAttrV1InnerServiceSMO accessControlAttrV1InnerServiceSMOImpl;

    @Autowired
    private INotifyNettyDataV1InnerServiceSMO notifyNettyDataV1InnerServiceSMOImpl;


    @Autowired
    private RestTemplate outRestTemplate;

    /**
     * 添加人员 主要是添加卡号
     *
     * @param accessControlDto    门禁信息
     * @param accessControlFacePo 人员信息
     * @return
     */
    @Override
    public boolean addUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        String taskId = GenerateCodeFactory.getGeneratorId("11");

        String cardNum = accessControlFacePo.getCardNumber();
        if (StringUtil.isEmpty(cardNum)) {
            return true;
        }

        String startTimeStr = accessControlFacePo.getStartTime();
        String endTimeStr = accessControlFacePo.getEndTime();
        Date startTime = DateUtil.getDateFromStringA(startTimeStr);
        Date endTime = DateUtil.getDateFromStringA(endTimeStr);


        String userData = cardNum;
        userData += DateUtil.getFormatTimeStringH(startTime);// 起始日期
        userData += DateUtil.getFormatTimeStringH(endTime);// 截止日期
        userData += "01010101";//  1-4号门控制时段
        String tel = "";
        if (AccessControlFaceDto.PERSON_TYPE_OWNER.equals(accessControlFacePo.getState())) {
            OwnerDto ownerDto = new OwnerDto();
            ownerDto.setMemberId(accessControlFacePo.getPersonId());
            List<OwnerDto> ownerDtos = ownerV1InnerServiceSMOImpl.queryOwners(ownerDto);

            if (!ListUtil.isNull(ownerDtos)) {
                tel = ownerDtos.get(0).getLink();
            }
        } else {
            UserDto userDto = new UserDto();
            userDto.setUserId(accessControlFacePo.getPersonId());
            List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
            if (!ListUtil.isNull(userDtos)) {
                tel = userDtos.get(0).getTel();
            }
        }

        userData += WiegandUtil.getLastSixDigits(tel);//  1-4号门控制时段


        String sendData = WiegandUtil.createSendData(CMD_ADD_USER, accessControlDto.getMachineCode(), userData);

        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(accessControlDto.getMachineCode(), BytesUtil.hexStringToByteArray(sendData)));

        saveAddFaceLog(taskId, accessControlDto.getMachineId(), CMD_ADD_USER,
                accessControlDto.getCommunityId(), sendData, accessControlFacePo.getPersonId(), accessControlFacePo.getName());
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

        String cardNum = accessControlFacePo.getCardNumber();
        if (StringUtil.isEmpty(cardNum)) {
            return true;
        }
        String userData = cardNum;

        String sendData = WiegandUtil.createSendData(CMD_DELETE_USER, accessControlDto.getMachineCode(), userData);

        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(accessControlDto.getMachineCode(), BytesUtil.hexStringToByteArray(sendData)));

        saveDeleteFaceLog(taskId, accessControlDto.getMachineId(), CMD_DELETE_USER,
                accessControlDto.getCommunityId(), userData, accessControlFacePo.getPersonId(), accessControlFacePo.getName());
        return true;
    }

    @Override
    public boolean openDoor(AccessControlDto accessControlDto) {
        String taskId = GenerateCodeFactory.getGeneratorId("11");

        String gateNum = "01";//门号 (从1开始算. 最大为4)
        String sendData = WiegandUtil.createSendData(CMD_OPEN_DOOR, accessControlDto.getMachineCode(), gateNum);

        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(accessControlDto.getMachineCode(), BytesUtil.hexStringToByteArray(sendData)));
        saveDeleteFaceLog(taskId, accessControlDto.getMachineId(), "openDoor",
                accessControlDto.getCommunityId(), sendData, accessControlDto.getUserId(), accessControlDto.getUserName());
        return true;
    }

    @Override
    public boolean restartMachine(AccessControlDto accessControlDto) {
        // 微耕不支持重启
        return true;
    }

    @Override
    public ResultVo task(AccessControlDto accessControlDto) {

        //todo  读取接受服务器ip和端口作为设备的心跳，因为设备没有心跳，
        String dataStr = "";
        String sendData = WiegandUtil.createSendData(CMD_HEARTBEAT, accessControlDto.getMachineCode(), dataStr);
        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(accessControlDto.getMachineCode(), BytesUtil.hexStringToByteArray(sendData)));


        return new ResultVo(ResultVo.CODE_OK, "成功");
    }

    @Override
    public String accessControlResult(String topic, String data) {

        WiegandDataDto wiegandDataDto = WiegandUtil.parseData(data);

        String cmd = wiegandDataDto.getCmd();
        String taskId = wiegandDataDto.getSequenceId();
        String machineCode = wiegandDataDto.getMachineCode();

        Assert.hasLength(cmd, "未包含cmd");
        Assert.hasLength(taskId, "未包含taskId");
        Assert.hasLength(machineCode, "未包含sn");

        switch (cmd) {
            case CMD_HEARTBEAT: // todo 心跳
                heartbeatHc(cmd, taskId, machineCode, wiegandDataDto);
                break;
//            case CMD_REBOOT: //todo 重启返回
//                response(cmd, taskId, machineCode, paramIn);
//                break;
            case CMD_OPEN_DOOR: //todo 开门返回
                response(cmd, taskId, machineCode, wiegandDataDto);
                break;
            case CMD_ADD_USER: //todo 添加用户返回
                response(cmd, taskId, machineCode, wiegandDataDto);
                break;
            case CMD_DELETE_USER: //todo 删除用户返回
                response(cmd, taskId, machineCode, wiegandDataDto);
                break;
            case CMD_FACE_RESULT: //todo 开门记录
                faceResult(cmd, taskId, machineCode, data);
                break;
//            case CMD_QRCODE_OPEN_DOOR: //todo 二维码开门
//                qrCodeOpenDoor(cmd, taskId, machineCode, paramIn);
//                break;
        }
        return null;
    }

    /**
     * 心跳处理
     *
     * @param cmd
     * @param taskId
     * @param machineCode
     * @param wiegandDataDto
     */
    private void heartbeatHc(String cmd, String taskId, String machineCode, WiegandDataDto wiegandDataDto) {
        heartbeat(machineCode, DateUtil.getCurrentDate());
    }

    public void response(String cmd, String taskId, String machineCode, WiegandDataDto wiegandDataDto) {

        //todo 更新下发日志
        AccessControlLogDto accessControlLogDto = new AccessControlLogDto();
        accessControlLogDto.setLogId(taskId);
        List<AccessControlLogDto> accessControlLogDtos = accessControlLogV1InnerServiceSMOImpl.queryAccessControlLogs(accessControlLogDto);
        if (ListUtil.isNull(accessControlLogDtos)) {
            return;
        }

        String state = AccessControlLogDto.STATE_RES;


        AccessControlLogPo accessControlLogPo = new AccessControlLogPo();
        accessControlLogPo.setLogId(accessControlLogDtos.get(0).getLogId());
        accessControlLogPo.setState(state);
        accessControlLogPo.setResParam("下发成功");
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
//        if (paramIn.getIntValue("code") != ResultVo.CODE_OK) {
//            state = AccessControlFaceDto.STATE_FAIL;
//        }


        AccessControlFacePo accessControlFacePo = new AccessControlFacePo();
        accessControlFacePo.setMfId(accessControlFaceDtos.get(0).getMfId());
        accessControlFacePo.setState(state);
        //accessControlFacePo.setMessage(paramIn.getString("msg"));
        accessControlFacePo.setMessage("下发成功");
        accessControlFaceV1InnerServiceSMOImpl.updateAccessControlFace(accessControlFacePo);


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
     */
    private void faceResult(String cmd, String taskId, String machineCode,String data ) {
        logger.debug("门禁内容,{}", data);

        WiegandDataDto wiegandDataDto = WiegandUtil.parseOpenDoorResult(data);
        if(StringUtil.isEmpty(wiegandDataDto.getCardNum())){
            return;
        }
        // 不是刷卡记录
        if(!"1".equals(wiegandDataDto.getState())){
            return;
        }

        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setCardNumber(wiegandDataDto.getCardNum());
       List<OwnerDto> ownerDtos = ownerV1InnerServiceSMOImpl.queryOwners(ownerDto);
        String staffId = "";
        String staffName = "";
       if(ListUtil.isNull(ownerDtos)){
           staffId = "-1";
           staffName = "未知";
       }else{
           staffId = ownerDtos.get(0).getMemberId();
           staffName = ownerDtos.get(0).getName();
       }


        String openTypeCd = "2000";
        int similar = 100;

        String images = "";

        saveOpenDoorResult(machineCode, images, staffId, staffName, openTypeCd, similar);


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
//        param.put("cmd", CMD_OPEN_DOOR);
//        param.put("action", ACTION);
//        param.put("pass", accessControlDtos.get(0).getMachineMac());
//        param.put("sn", accessControlDtos.get(0).getMachineCode());
//        param.put("taskId", taskId);
//        MqttFactory.publish(TOPIC_REQ, param.toJSONString());
//        saveOpenDoorLog(taskId, accessControlDtos.get(0).getMachineId(), TOPIC_REQ + "/" + CMD_OPEN_DOOR,
//                accessControlDtos.get(0).getCommunityId(), param.toJSONString(), accessControlDtos.get(0).getUserId(), accessControlDtos.get(0).getUserName());
    }
}
