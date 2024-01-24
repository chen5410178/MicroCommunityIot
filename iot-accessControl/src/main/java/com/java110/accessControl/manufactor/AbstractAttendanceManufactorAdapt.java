package com.java110.accessControl.manufactor;

import com.alibaba.fastjson.JSONObject;
import com.java110.accessControl.smo.impl.AttendanceMachineLogV1InnerServiceSMOImpl;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.file.FileDto;
import com.java110.core.cache.MappingCache;
import com.java110.core.client.RestTemplate;
import com.java110.core.constant.MappingConstant;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.PropertyHttpFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.MachineHeartbeatDto;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.accessControlLog.AccessControlLogDto;
import com.java110.dto.attendanceCheckin.AttendanceCheckinDto;
import com.java110.dto.attendanceMachine.AttendanceMachineDto;
import com.java110.dto.attendanceMachineLog.AttendanceMachineLogDto;
import com.java110.dto.attendanceStaff.AttendanceStaffDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.accessControl.*;
import com.java110.intf.community.IRoomV1InnerServiceSMO;
import com.java110.intf.system.IFileInnerServiceSMO;
import com.java110.intf.user.IPassQrcodeV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.accessControlFace.AccessControlFacePo;
import com.java110.po.accessControlLog.AccessControlLogPo;
import com.java110.po.attendanceCheckin.AttendanceCheckinPo;
import com.java110.po.attendanceMachine.AttendanceMachinePo;
import com.java110.po.attendanceMachineLog.AttendanceMachineLogPo;
import com.java110.po.attendanceStaff.AttendanceStaffPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractAttendanceManufactorAdapt implements IAttendanceManufactor {

    public static final String SUCCESS = "{\n" +
            "    \"code\": 0,\n" +
            "    \"msg\": \"成功\",\n" +
            "}";

    public static final String OPEN_TYPE_FACE = "1000"; // 开门方式，1000 人脸开门 2000 钥匙开门 3000 二维码开门 4000 密码开门
    public static final String OPEN_TYPE_CARD = "2000"; // 开门方式，1000 人脸开门 2000 钥匙开门 3000 二维码开门 4000 密码开门
    public static final String OPEN_TYPE_QRCODE = "3000"; // 开门方式，1000 人脸开门 2000 钥匙开门 3000 二维码开门 4000 密码开门
    public static final String OPEN_TYPE_PASSWORD = "4000"; // 开门方式，1000 人脸开门 2000 钥匙开门 3000 二维码开门 4000 密码开门

    @Autowired
    private IAttendanceMachineLogV1InnerServiceSMO attendanceMachineLogV1InnerServiceSMO;

    @Autowired
    private IAttendanceMachineV1InnerServiceSMO attendanceMachineV1InnerServiceSMOImpl;


    @Autowired
    private IAttendanceStaffV1InnerServiceSMO attendanceStaffV1InnerServiceSMOImpl;

    @Autowired
    private IFileInnerServiceSMO fileInnerServiceSMOImpl;


    @Autowired
    private IPassQrcodeV1InnerServiceSMO passQrcodeV1InnerServiceSMOImpl;

    @Autowired
    private IVisitV1InnerServiceSMO visitV1InnerServiceSMOImpl;

    @Autowired
    private IAttendanceCheckinV1InnerServiceSMO attendanceCheckinV1InnerServiceSMOImpl;

    @Autowired
    private IRoomV1InnerServiceSMO roomV1InnerServiceSMOImpl;

    @Autowired
    private IAttendanceMachineLogV1InnerServiceSMO attendanceMachineLogV1InnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private RestTemplate outRestTemplate;

    @Override
    public boolean initMachine(AttendanceMachineDto attendanceMachineDto) {
        return true;
    }

    @Override
    public boolean addMachine(AttendanceMachinePo attendanceMachinePo) {
        return true;
    }

    @Override
    public boolean updateMachine(AttendanceMachinePo attendanceMachinePo) {
        return true;
    }

    @Override
    public boolean deleteMachine(AttendanceMachinePo attendanceMachinePo) {
        return true;
    }

    /**
     * 添加用户
     *
     * @param attendanceMachineDto 门禁信息
     * @param attendanceStaffPo    人员信息
     * @return
     */
    @Override
    public abstract boolean addUser(AttendanceMachineDto attendanceMachineDto, AttendanceStaffPo attendanceStaffPo);

    /**
     * 修改用户
     *
     * @param attendanceMachineDto 门禁信息
     * @param attendanceStaffPo    人员信息
     * @return
     */
    @Override
    public abstract boolean updateUser(AttendanceMachineDto attendanceMachineDto, AttendanceStaffPo attendanceStaffPo);

    /**
     * 删除用户
     *
     * @param attendanceMachineDto 门禁信息
     * @param attendanceStaffPo    人员信息
     * @return
     */
    @Override
    public abstract boolean deleteUser(AttendanceMachineDto attendanceMachineDto, AttendanceStaffPo attendanceStaffPo);


    /**
     * 重启设备
     *
     * @param attendanceMachineDto 门禁信息
     * @return
     */
    @Override
    public abstract boolean restartMachine(AttendanceMachineDto attendanceMachineDto);


    /**
     * 门禁记录上报
     *
     * @param topic 回话
     * @param param 上报参数
     * @return
     */
    @Override
    public abstract String attendanceResult(String topic, String param);


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
    protected void saveLog(String logId, String machineId, String cmd, String communityId, String reqParam, String userId, String userName) {
        saveLog(logId, machineId, cmd, communityId, reqParam, "", AccessControlLogDto.STATE_REQ, userId, userName);
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
    protected void saveLog(String logId, String machineId, String cmd, String communityId, String reqParam, String resParam, String state, String userId, String userName) {
        AttendanceMachineLogPo attendanceMachineLogPo = new AttendanceMachineLogPo();
        if (StringUtil.isEmpty(logId)) {
            logId = GenerateCodeFactory.getGeneratorId("11");
        }
        attendanceMachineLogPo.setLogId(logId);

        attendanceMachineLogPo.setMachineId(machineId);
        attendanceMachineLogPo.setLogAction(cmd);
        attendanceMachineLogPo.setReqParam(reqParam);
        attendanceMachineLogPo.setResParam(resParam);
        attendanceMachineLogPo.setState(state);
        attendanceMachineLogPo.setStaffId(userId);
        attendanceMachineLogPo.setStaffName(userName);
        attendanceMachineLogPo.setCommunityId(communityId);
        attendanceMachineLogV1InnerServiceSMO.saveAttendanceMachineLog(attendanceMachineLogPo);
    }

    protected void heartbeat(String machineCode) {
        heartbeat(machineCode, DateUtil.getCurrentDate());
    }

    protected void heartbeat(String machineCode, Date heartbeatTime) {
        attendanceMachineV1InnerServiceSMOImpl.attendanceHeartbeat(
                new MachineHeartbeatDto(machineCode, DateUtil.getFormatTimeStringA(heartbeatTime)));
    }


    protected void cmdResult(int code, String msg, String logAction, String machineCode, String personId) {

        AttendanceMachineDto attendanceMachineDto = new AttendanceMachineDto();
        attendanceMachineDto.setMachineCode(machineCode);
        List<AttendanceMachineDto> attendanceMachineDtos = attendanceMachineV1InnerServiceSMOImpl.queryAttendanceMachines(attendanceMachineDto);

        if (ListUtil.isNull(attendanceMachineDtos)) {
            return;
        }
        //todo 更新下发日志
        AttendanceMachineLogDto attendanceMachineLogDto = new AttendanceMachineLogDto();
        attendanceMachineLogDto.setLogAction(logAction);
        attendanceMachineLogDto.setStaffId(personId);
        attendanceMachineLogDto.setMachineId(attendanceMachineDtos.get(0).getMachineId());
        attendanceMachineLogDto.setState(AccessControlLogDto.STATE_REQ);
        List<AttendanceMachineLogDto> attendanceMachineLogDtos = attendanceMachineLogV1InnerServiceSMOImpl.queryAttendanceMachineLogs(attendanceMachineLogDto);
        if (ListUtil.isNull(attendanceMachineLogDtos)) {
            return;
        }

        String state = AccessControlLogDto.STATE_RES;
        if (code != ResultVo.CODE_OK) {
            state = AccessControlLogDto.STATE_FAIL;
        }


        AttendanceMachineLogPo attendanceMachineLogPo = new AttendanceMachineLogPo();
        attendanceMachineLogPo.setLogId(attendanceMachineLogDtos.get(0).getLogId());
        attendanceMachineLogPo.setState(state);
        attendanceMachineLogPo.setResParam(msg);
        attendanceMachineLogV1InnerServiceSMOImpl.updateAttendanceMachineLog(attendanceMachineLogPo);

        //todo 更新人员日志

        AttendanceStaffDto attendanceStaffDto = new AttendanceStaffDto();
        attendanceStaffDto.setStaffId(attendanceMachineLogDtos.get(0).getStaffId());
        attendanceStaffDto.setMachineId(attendanceMachineDtos.get(0).getMachineId());
        attendanceStaffDto.setState(AccessControlFaceDto.STATE_WAIT);
        List<AttendanceStaffDto> attendanceStaffDtos = attendanceStaffV1InnerServiceSMOImpl.queryAttendanceStaffs(attendanceStaffDto);
        if (ListUtil.isNull(attendanceStaffDtos)) {
            return;
        }

        state = AttendanceStaffDto.STATE_COMPLETE;
        if (code != ResultVo.CODE_OK) {
            state = AttendanceStaffDto.STATE_FAIL;
        }


        AttendanceStaffPo attendanceStaffPo = new AttendanceStaffPo();
        attendanceStaffPo.setMsId(attendanceStaffDtos.get(0).getMsId());
        attendanceStaffPo.setState(state);
        attendanceStaffPo.setMessage(msg);
        attendanceStaffV1InnerServiceSMOImpl.updateAttendanceStaff(attendanceStaffPo);


    }

    protected ResultVo propertyCheckIn(String staffId, String photo, String machineCode) {

        AttendanceMachineDto attendanceMachineDto = new AttendanceMachineDto();
        attendanceMachineDto.setMachineCode(machineCode);
        List<AttendanceMachineDto> attendanceMachineDtos = attendanceMachineV1InnerServiceSMOImpl.queryAttendanceMachines(attendanceMachineDto);

        if (ListUtil.isNull(attendanceMachineDtos)) {
            return new ResultVo(-1, "设备不存在");
        }

        JSONObject param = new JSONObject();
        param.put("staffId", staffId);
        param.put("checkTime", DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        param.put("photo", photo);
        param.put("classesId", attendanceMachineDtos.get(0).getClassesId());

        Map<String, String> headers = new HashMap<>();

        ResponseEntity<String> tmpResponseEntity = PropertyHttpFactory.post(outRestTemplate, "/api/attendanceClasses.checkIn", param.toJSONString(), headers);

        JSONObject paramOut = JSONObject.parseObject(tmpResponseEntity.getBody());


        UserDto userDto = new UserDto();
        userDto.setUserId(staffId);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        if (ListUtil.isNull(userDtos)) {
            return new ResultVo(paramOut.getIntValue("code"), paramOut.getString("msg"), paramOut.get("data"));
        }


        if (!StringUtil.isEmpty(photo) && photo.length() > 512) { //说明是图片
            FileDto fileDto = new FileDto();
            fileDto.setFileId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_file_id));
            fileDto.setFileName(fileDto.getFileId());
            fileDto.setContext(photo);
            fileDto.setSuffix("jpeg");
            fileDto.setCommunityId(attendanceMachineDtos.get(0).getCommunityId());
            String fileName = fileInnerServiceSMOImpl.saveFile(fileDto);
            String imgUrl = MappingCache.getValue(MappingConstant.FILE_DOMAIN, "IMG_PATH");

            photo = imgUrl + fileName;
        }

        AttendanceCheckinPo attendanceCheckinPo = new AttendanceCheckinPo();
        attendanceCheckinPo.setCheckinId(GenerateCodeFactory.getGeneratorId("11"));
        attendanceCheckinPo.setCheckinTime(param.getString("checkTime"));
        attendanceCheckinPo.setCommunityId(attendanceMachineDtos.get(0).getCommunityId());
        attendanceCheckinPo.setMachineCode(machineCode);
        attendanceCheckinPo.setMachineId(attendanceMachineDtos.get(0).getMachineId());
        attendanceCheckinPo.setState(AttendanceCheckinDto.STATE_FAIL);
        attendanceCheckinPo.setFacePath(photo);
        attendanceCheckinPo.setOpenTypeCd("1000");
        attendanceCheckinPo.setRemark(paramOut.getString("msg"));
        attendanceCheckinPo.setStaffId(staffId);
        attendanceCheckinPo.setStaffName(userDtos.get(0).getName());
        attendanceCheckinPo.setTel(userDtos.get(0).getTel());


        if (paramOut.getIntValue("code") == 0) {
            attendanceCheckinPo.setState(AttendanceCheckinDto.STATE_SUCCESS);
        }

        attendanceCheckinV1InnerServiceSMOImpl.saveAttendanceCheckin(attendanceCheckinPo);

        return new ResultVo(paramOut.getIntValue("code"), paramOut.getString("msg"), paramOut.get("data"));

    }

}
