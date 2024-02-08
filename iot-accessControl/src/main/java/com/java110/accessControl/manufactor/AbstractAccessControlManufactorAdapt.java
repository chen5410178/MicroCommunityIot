package com.java110.accessControl.manufactor;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.file.FileDto;
import com.java110.bean.dto.room.RoomDto;
import com.java110.core.cache.MappingCache;
import com.java110.core.constant.MappingConstant;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControl.MachineHeartbeatDto;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.accessControlFloor.AccessControlFloorDto;
import com.java110.dto.accessControlInout.AccessControlInoutDto;
import com.java110.dto.accessControlLog.AccessControlLogDto;
import com.java110.dto.passQrcode.PassQrcodeDto;
import com.java110.dto.user.UserDto;
import com.java110.dto.visit.VisitDto;
import com.java110.intf.accessControl.*;
import com.java110.intf.community.IRoomV1InnerServiceSMO;
import com.java110.intf.system.IFileInnerServiceSMO;
import com.java110.intf.user.IPassQrcodeV1InnerServiceSMO;
import com.java110.po.accessControl.AccessControlPo;
import com.java110.po.accessControlFace.AccessControlFacePo;
import com.java110.po.accessControlInout.AccessControlInoutPo;
import com.java110.po.accessControlLog.AccessControlLogPo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public abstract class AbstractAccessControlManufactorAdapt implements IAccessControlManufactor {

    public static final String SUCCESS = "{\n" +
            "    \"code\": 0,\n" +
            "    \"msg\": \"成功\",\n" +
            "}";

    public static final String OPEN_TYPE_FACE = "1000"; // 开门方式，1000 人脸开门 2000 钥匙开门 3000 二维码开门 4000 密码开门
    public static final String OPEN_TYPE_CARD = "2000"; // 开门方式，1000 人脸开门 2000 钥匙开门 3000 二维码开门 4000 密码开门
    public static final String OPEN_TYPE_QRCODE = "3000"; // 开门方式，1000 人脸开门 2000 钥匙开门 3000 二维码开门 4000 密码开门
    public static final String OPEN_TYPE_PASSWORD = "4000"; // 开门方式，1000 人脸开门 2000 钥匙开门 3000 二维码开门 4000 密码开门

    @Autowired
    private IAccessControlLogV1InnerServiceSMO accessControlLogV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlInoutV1InnerServiceSMO accessControlInoutV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlFaceV1InnerServiceSMO accessControlFaceV1InnerServiceSMOImpl;

    @Autowired
    private IFileInnerServiceSMO fileInnerServiceSMOImpl;


    @Autowired
    private IPassQrcodeV1InnerServiceSMO passQrcodeV1InnerServiceSMOImpl;

    @Autowired
    private IVisitV1InnerServiceSMO visitV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlFloorV1InnerServiceSMO accessControlFloorV1InnerServiceSMOImpl;

    @Autowired
    private IRoomV1InnerServiceSMO roomV1InnerServiceSMOImpl;

    @Override
    public boolean initMachine(AccessControlDto accessControlDto) {
        return true;
    }

    @Override
    public boolean addMachine(AccessControlPo accessControlPo) {
        return true;
    }

    @Override
    public boolean updateMachine(AccessControlPo accessControlPo) {
        return true;
    }

    @Override
    public boolean deleteMachine(AccessControlPo accessControlPo) {
        return true;
    }

    /**
     * 添加用户
     *
     * @param accessControlDto    门禁信息
     * @param accessControlFacePo 人员信息
     * @return
     */
    @Override
    public abstract boolean addUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo);

    /**
     * 修改用户
     *
     * @param accessControlDto    门禁信息
     * @param accessControlFacePo 人员信息
     * @return
     */
    @Override
    public abstract boolean updateUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo);

    /**
     * 删除用户
     *
     * @param accessControlDto    门禁信息
     * @param accessControlFacePo 人员信息
     * @return
     */
    @Override
    public abstract boolean deleteUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo);

    @Override
    public boolean clearUser(AccessControlDto accessControlDto) {
        return true;
    }

    /**
     * 设备开门
     *
     * @param accessControlDto 门禁信息
     * @return
     */
    @Override
    public abstract boolean openDoor(AccessControlDto accessControlDto);


    /**
     * 重启设备
     *
     * @param accessControlDto 门禁信息
     * @return
     */
    @Override
    public abstract boolean restartMachine(AccessControlDto accessControlDto);


    @Override
    public ResultVo getQRCode(UserDto userDto) {
        return null;
    }

    @Override
    public String qrCode(AccessControlDto accessControlDto, String param) {
        return null;
    }

    /**
     * 门禁记录上报
     *
     * @param topic 回话
     * @param param 上报参数
     * @return
     */
    @Override
    public abstract String accessControlResult(String topic, String param);


    /**
     * 存储日志
     *
     * @param logId     日志ID
     * @param machineId 设备ID
     * @param logAction 操作命令
     * @param reqParam  请求报文
     * @param userId    业主ID
     * @param userName  业主名称
     */
    protected void saveAddFaceLog(String logId,
                                  String machineId,
                                  String logAction,
                                  String communityId,
                                  String reqParam,
                                  String userId,
                                  String userName) {
        saveLog(logId, machineId, logAction, communityId, reqParam, "", AccessControlLogDto.STATE_REQ, userId, userName,AccessControlLogDto.CMD_ADD_FACE);
    }

    protected void saveUpdateFaceLog(String logId,
                                  String machineId,
                                  String logAction,
                                  String communityId,
                                  String reqParam,
                                  String userId,
                                  String userName) {
        saveLog(logId, machineId, logAction, communityId, reqParam, "", AccessControlLogDto.STATE_REQ, userId, userName,AccessControlLogDto.CMD_UPDATE_FACE);
    }

    protected void saveDeleteFaceLog(String logId,
                                     String machineId,
                                     String logAction,
                                     String communityId,
                                     String reqParam,
                                     String userId,
                                     String userName) {
        saveLog(logId, machineId, logAction, communityId, reqParam, "", AccessControlLogDto.STATE_REQ, userId, userName,AccessControlLogDto.CMD_DELETE_FACE);
    }

    protected void saveRebootAcLog(String logId,
                                     String machineId,
                                     String logAction,
                                     String communityId,
                                     String reqParam,
                                     String userId,
                                     String userName) {
        saveLog(logId, machineId, logAction, communityId, reqParam, "", AccessControlLogDto.STATE_REQ, userId, userName,AccessControlLogDto.CMD_REBOOT);
    }

    protected void saveOpenDoorLog(String logId,
                                   String machineId,
                                   String logAction,
                                   String communityId,
                                   String reqParam,
                                   String userId,
                                   String userName) {
        saveLog(logId, machineId, logAction, communityId, reqParam, "", AccessControlLogDto.STATE_REQ, userId, userName,AccessControlLogDto.CMD_OPEN_DOOR);
    }


    /**
     * 存储日志
     *
     * @param logId     日志ID
     * @param machineId 设备ID
     * @param logAction 操作命令
     * @param reqParam  请求报文
     * @param resParam  返回报文
     * @param state     状态
     * @param userId    业主ID
     * @param userName  业主名称
     */
    protected void saveLog(String logId,
                           String machineId,
                           String logAction,
                           String communityId,
                           String reqParam,
                           String resParam,
                           String state,
                           String userId,
                           String userName,
                           String logCmd) {
        AccessControlLogPo accessControlLogPo = new AccessControlLogPo();
        if (StringUtil.isEmpty(logId)) {
            logId = GenerateCodeFactory.getGeneratorId("11");
        }
        accessControlLogPo.setLogId(logId);

        accessControlLogPo.setMachineId(machineId);
        accessControlLogPo.setLogAction(logAction);
        accessControlLogPo.setReqParam(reqParam);
        accessControlLogPo.setResParam(resParam);
        accessControlLogPo.setState(state);
        accessControlLogPo.setUserId(userId);
        accessControlLogPo.setUserName(userName);
        accessControlLogPo.setCommunityId(communityId);
        accessControlLogPo.setLogCmd(logCmd);
        accessControlLogV1InnerServiceSMOImpl.saveAccessControlLog(accessControlLogPo);
    }

    protected void heartbeat(String machineCode) {
        heartbeat(machineCode, DateUtil.getCurrentDate());
    }

    protected void heartbeat(String machineCode, Date heartbeatTime) {
        accessControlV1InnerServiceSMOImpl.accessControlHeartbeat(
                new MachineHeartbeatDto(machineCode, DateUtil.getFormatTimeStringA(heartbeatTime)));
    }

    protected void saveOpenDoorResult(String machineCode,
                                      String facePath,
                                      String personId,
                                      String name,
                                      String openTypeCd,
                                      int similar) {
        saveOpenDoorResult(machineCode, GenerateCodeFactory.getGeneratorId("11"), facePath, personId, name, openTypeCd, similar);
    }

    protected void saveOpenDoorResult(String machineCode, String inoutId,
                                      String facePath,
                                      String personId,
                                      String name,
                                      String openTypeCd,
                                      int similar) {

        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineCode(machineCode);
        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);

        if (ListUtil.isNull(accessControlDtos)) {
            throw new IllegalArgumentException("门禁不存在" + machineCode);
        }

        if (!StringUtil.isEmpty(facePath)) {

        }
        if (!StringUtil.isEmpty(facePath) && facePath.length() > 512) { //说明是图片
            FileDto fileDto = new FileDto();
            fileDto.setFileId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_file_id));
            fileDto.setFileName(fileDto.getFileId());
            fileDto.setContext(facePath);
            fileDto.setSuffix("jpeg");
            fileDto.setCommunityId(accessControlDtos.get(0).getCommunityId());
            String fileName = fileInnerServiceSMOImpl.saveFile(fileDto);
            String imgUrl = MappingCache.getValue(MappingConstant.FILE_DOMAIN, "IMG_PATH");

            facePath = imgUrl + fileName;
        }

        AccessControlFaceDto accessControlFaceDto = queryAccessControlFace(personId, accessControlDtos);
        AccessControlInoutPo accessControlInoutPo = new AccessControlInoutPo();
        accessControlInoutPo.setMachineCode(machineCode);
        accessControlInoutPo.setCommunityId(accessControlDtos.get(0).getCommunityId());
        accessControlInoutPo.setInoutId(inoutId);
        accessControlInoutPo.setMachineId(accessControlDtos.get(0).getMachineId());
        accessControlInoutPo.setState((similar > 50) ? "C" : "F");
        accessControlInoutPo.setFacePath(facePath);
        accessControlInoutPo.setName(name);
        accessControlInoutPo.setIdCard(accessControlFaceDto.getIdNumber());
        accessControlInoutPo.setOpenTypeCd(openTypeCd);
        accessControlInoutPo.setSimilar(similar + "");
        accessControlInoutPo.setTel(accessControlFaceDto.getTel());
        accessControlInoutV1InnerServiceSMOImpl.saveAccessControlInout(accessControlInoutPo);

    }

    private AccessControlFaceDto queryAccessControlFace(String personId, List<AccessControlDto> accessControlDtos) {
        AccessControlFaceDto accessControlFaceDto = new AccessControlFaceDto();
        if (StringUtil.isEmpty(personId) || personId.startsWith("-")) {
            accessControlFaceDto.setIdNumber("无");
            accessControlFaceDto.setCardNumber("无");
            accessControlFaceDto.setTel("无");
            return accessControlFaceDto;
        }
        accessControlFaceDto.setPersonId(personId);
        accessControlFaceDto.setCommunityId(accessControlDtos.get(0).getCommunityId());
        List<AccessControlFaceDto> accessControlFaceDtos = accessControlFaceV1InnerServiceSMOImpl.queryAccessControlFaces(accessControlFaceDto);
        if (accessControlFaceDtos == null || accessControlFaceDtos.size() < 1) {
            accessControlFaceDto.setIdNumber("无");
            accessControlFaceDto.setCardNumber("无");
            accessControlFaceDto.setTel("无");
            return accessControlFaceDto;
        }
        accessControlFaceDto = accessControlFaceDtos.get(0);
        return accessControlFaceDto;
    }

    protected void cmdResult(int code, String msg, String logAction, String machineCode, String personId) {

        //todo 更新下发日志
        AccessControlLogDto accessControlLogDto = new AccessControlLogDto();
        accessControlLogDto.setLogAction(logAction);
        accessControlLogDto.setUserId(personId);
        accessControlLogDto.setMachineCode(machineCode);
        accessControlLogDto.setState(AccessControlLogDto.STATE_REQ);
        List<AccessControlLogDto> accessControlLogDtos = accessControlLogV1InnerServiceSMOImpl.queryAccessControlLogs(accessControlLogDto);
        if (ListUtil.isNull(accessControlLogDtos)) {
            return;
        }

        String state = AccessControlLogDto.STATE_RES;
        if (code != ResultVo.CODE_OK) {
            state = AccessControlLogDto.STATE_FAIL;
        }


        AccessControlLogPo accessControlLogPo = new AccessControlLogPo();
        accessControlLogPo.setLogId(accessControlLogDtos.get(0).getLogId());
        accessControlLogPo.setState(state);
        accessControlLogPo.setResParam(msg);
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
        if (code != ResultVo.CODE_OK) {
            state = AccessControlFaceDto.STATE_FAIL;
        }


        AccessControlFacePo accessControlFacePo = new AccessControlFacePo();
        accessControlFacePo.setMfId(accessControlFaceDtos.get(0).getMfId());
        accessControlFacePo.setState(state);
        accessControlFacePo.setMessage(msg);
        accessControlFaceV1InnerServiceSMOImpl.updateAccessControlFace(accessControlFacePo);


    }

    /**
     * 二维码核验
     *
     * @param qrcode
     * @param machineCode
     * @return
     */

    protected boolean qrcodeCheck(String qrcode, String machineCode) {

        PassQrcodeDto passQrcodeDto = new PassQrcodeDto();
        passQrcodeDto.setQrcodeTime(qrcode);
        List<PassQrcodeDto> passQrcodeDtos = passQrcodeV1InnerServiceSMOImpl.queryPassQrcodes(passQrcodeDto);
        if (!ListUtil.isNull(passQrcodeDtos)) {
            String ownerId = passQrcodeDtos.get(0).getOwnerId();
            AccessControlFaceDto accessControlFaceDto = new AccessControlFaceDto();
            accessControlFaceDto.setPersonId(ownerId);
            accessControlFaceDto.setMachineCode(machineCode);
            int count = accessControlFaceV1InnerServiceSMOImpl.queryAccessControlFacesCount(accessControlFaceDto);
            if (count < 1) {
                return false;
            }
            return true;
        }

        //todo 核验访客人员
        VisitDto visitDto = new VisitDto();
        visitDto.setQrcode(qrcode);
        List<VisitDto> visitDtos = visitV1InnerServiceSMOImpl.queryVisits(visitDto);
        if (ListUtil.isNull(visitDtos)) {

            return false;
        }
        String roomId = visitDtos.get(0).getRoomId();

        RoomDto roomDto = new RoomDto();
        roomDto.setRoomId(roomId);
        List<RoomDto> roomDtos = roomV1InnerServiceSMOImpl.queryRooms(roomDto);
        if (ListUtil.isNull(roomDtos)) {
            return false;
        }
        AccessControlFloorDto accessControlFloorDto = new AccessControlFloorDto();
        accessControlFloorDto.setMachineCode(machineCode);
        accessControlFloorDto.setUnitId(roomDtos.get(0).getUnitId());
        int count = accessControlFloorV1InnerServiceSMOImpl.queryAccessControlFloorsCount(accessControlFloorDto);
        if (count < 1) {
            return false;
        }
        return true;
    }

}
