package com.java110.accessControl.manufactor;

import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.MachineHeartbeatDto;
import com.java110.dto.accessControlLog.AccessControlLogDto;
import com.java110.dto.attendanceMachine.AttendanceMachineDto;
import com.java110.intf.accessControl.*;
import com.java110.intf.community.IRoomV1InnerServiceSMO;
import com.java110.intf.system.IFileInnerServiceSMO;
import com.java110.intf.user.IPassQrcodeV1InnerServiceSMO;
import com.java110.po.accessControlFace.AccessControlFacePo;
import com.java110.po.attendanceMachine.AttendanceMachinePo;
import com.java110.po.attendanceMachineLog.AttendanceMachineLogPo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

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
    private IAttendanceCheckinV1InnerServiceSMO attendanceCheckinV1InnerServiceSMOImpl;

    @Autowired
    private IAttendanceStaffV1InnerServiceSMO attendanceStaffV1InnerServiceSMOImpl;

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
     * @param accessControlFacePo  人员信息
     * @return
     */
    @Override
    public abstract boolean addUser(AttendanceMachineDto attendanceMachineDto, AccessControlFacePo accessControlFacePo);

    /**
     * 修改用户
     *
     * @param attendanceMachineDto 门禁信息
     * @param accessControlFacePo  人员信息
     * @return
     */
    @Override
    public abstract boolean updateUser(AttendanceMachineDto attendanceMachineDto, AccessControlFacePo accessControlFacePo);

    /**
     * 删除用户
     *
     * @param attendanceMachineDto 门禁信息
     * @param accessControlFacePo  人员信息
     * @return
     */
    @Override
    public abstract boolean deleteUser(AttendanceMachineDto attendanceMachineDto, AccessControlFacePo accessControlFacePo);


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


}
