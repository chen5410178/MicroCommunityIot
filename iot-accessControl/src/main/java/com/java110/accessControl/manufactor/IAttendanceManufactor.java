package com.java110.accessControl.manufactor;

import com.java110.bean.ResultVo;
import com.java110.dto.attendanceMachine.AttendanceMachineDto;
import com.java110.dto.attendanceStaff.AttendanceStaffDto;
import com.java110.dto.user.UserDto;
import com.java110.po.accessControlFace.AccessControlFacePo;
import com.java110.po.attendanceMachine.AttendanceMachinePo;
import com.java110.po.attendanceStaff.AttendanceStaffPo;

/**
 * 考勤机厂家实现接口类
 */
public interface IAttendanceManufactor {

    /**
     * 设备初始化，可能像 海康大华 等门禁他们需要在初始化时 做一些业务，看情况是否需要调用
     *
     * @param attendanceMachineDto 门禁
     * @return
     */
    boolean initMachine(AttendanceMachineDto attendanceMachineDto);

    /**
     * 添加门禁，
     *
     * @param attendanceMachinePo 门禁信息
     * @return
     */
    boolean addMachine(AttendanceMachinePo attendanceMachinePo);

    /**
     * 修改门禁，
     *
     * @param attendanceMachinePo 门禁信息
     * @return
     */
    boolean updateMachine(AttendanceMachinePo attendanceMachinePo);


    /**
     * 删除门禁
     *
     * @param attendanceMachinePo 门禁信息
     * @return
     */
    boolean deleteMachine(AttendanceMachinePo attendanceMachinePo);


    /**
     * 添加用户
     *
     * @param attendanceMachineDto 门禁信息
     * @param attendanceStaffPo  人员信息
     * @return
     */
    boolean addUser(AttendanceMachineDto attendanceMachineDto, AttendanceStaffPo attendanceStaffPo);

    /**
     * 修改用户
     *
     * @param attendanceMachineDto 门禁信息
     * @param attendanceStaffPo  人员信息
     * @return
     */
    boolean updateUser(AttendanceMachineDto attendanceMachineDto, AttendanceStaffPo attendanceStaffPo);

    /**
     * 删除用户
     *
     * @param attendanceMachineDto 门禁信息
     * @param attendanceStaffPo  人员信息
     * @return
     */
    boolean deleteUser(AttendanceMachineDto attendanceMachineDto, AttendanceStaffPo attendanceStaffPo);


    /**
     * 重启门禁
     *
     * @param attendanceMachineDto 门禁信息
     * @return
     */
    boolean restartMachine(AttendanceMachineDto attendanceMachineDto);


    /**
     * 结果上报
     *
     * @param topic 回话
     * @param param
     * @return
     */
    String attendanceResult(String topic, String param);

}
