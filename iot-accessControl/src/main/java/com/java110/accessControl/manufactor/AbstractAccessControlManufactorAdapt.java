package com.java110.accessControl.manufactor;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControl.MachineHeartbeatDto;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.accessControlInout.AccessControlInoutDto;
import com.java110.dto.accessControlLog.AccessControlLogDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.accessControl.IAccessControlFaceV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlInoutV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlLogV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
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
     * @param cmd       操作命令
     * @param reqParam  请求报文
     * @param userId    业主ID
     * @param userName  业主名称
     */
    protected void saveLog(String logId, String machineId, String cmd,String communityId, String reqParam, String userId, String userName) {
        saveLog(logId, machineId, cmd,communityId, reqParam, "", AccessControlLogDto.STATE_REQ, userId, userName);
    }

    /**
     * 存储日志
     *
     * @param logId     日志ID
     * @param machineId 设备ID
     * @param cmd       操作命令
     * @param reqParam  请求报文
     * @param resParam  返回报文
     * @param state     状态
     * @param userId    业主ID
     * @param userName  业主名称
     */
    protected void saveLog(String logId, String machineId, String cmd,String communityId, String reqParam, String resParam, String state, String userId, String userName) {
        AccessControlLogPo accessControlLogPo = new AccessControlLogPo();
        accessControlLogPo.setLogId(logId);
        accessControlLogPo.setMachineId(machineId);
        accessControlLogPo.setLogAction(cmd);
        accessControlLogPo.setReqParam(reqParam);
        accessControlLogPo.setResParam(resParam);
        accessControlLogPo.setState(state);
        accessControlLogPo.setUserId(userId);
        accessControlLogPo.setUserName(userName);
        accessControlLogPo.setCommunityId(communityId);
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

        if (accessControlDtos == null || accessControlDtos.size() < 1) {
            throw new IllegalArgumentException("门禁不存在" + machineCode);
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
        if (accessControlLogDtos == null || accessControlLogDtos.size() < 1) {
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
        accessControlFaceDto.setPersonId(personId);
        accessControlFaceDto.setMachineCode(machineCode);
        accessControlFaceDto.setState(AccessControlFaceDto.STATE_WAIT);
        List<AccessControlFaceDto> accessControlFaceDtos = accessControlFaceV1InnerServiceSMOImpl.queryAccessControlFaces(accessControlFaceDto);
        if (accessControlFaceDtos == null || accessControlFaceDtos.size() < 1) {
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
}
