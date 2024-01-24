package com.java110.openapi.cmd.staff;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ListUtil;
import com.java110.dto.attendanceMachine.AttendanceMachineDto;
import com.java110.dto.attendanceStaff.AttendanceStaffDto;
import com.java110.intf.accessControl.IAttendanceMachineV1InnerServiceSMO;
import com.java110.intf.accessControl.IAttendanceStaffV1InnerServiceSMO;
import com.java110.po.attendanceStaff.AttendanceStaffPo;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

/**
 * 删除考勤班组员工
 */
@Java110Cmd(serviceCode = "staff.deleteAttendanceStaffApi")
public class DeleteAttendanceStaffApiCmd extends Cmd {

    @Autowired
    private IAttendanceStaffV1InnerServiceSMO attendanceStaffV1InnerServiceSMOImpl;

    @Autowired
    private IAttendanceMachineV1InnerServiceSMO attendanceMachineV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "classesId", "未包含班组ID");
        Assert.hasKeyAndValue(reqJson, "staffId", "未包含员工ID");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
//todo 检查设备是否存在
        AttendanceMachineDto attendanceMachineDto = new AttendanceMachineDto();
        attendanceMachineDto.setClassesId(reqJson.getString("classesId"));
        List<AttendanceMachineDto> attendanceMachineDtos = attendanceMachineV1InnerServiceSMOImpl.queryAttendanceMachines(attendanceMachineDto);
        if (ListUtil.isNull(attendanceMachineDtos)) {
            throw new CmdException("班组未包含设备");
        }

        for (AttendanceMachineDto tmpAttendanceMachine : attendanceMachineDtos) {

            doDeleteAttendanceStaff(reqJson, tmpAttendanceMachine);
        }


    }

    private void doDeleteAttendanceStaff(JSONObject reqJson, AttendanceMachineDto tmpAttendanceMachine) {

        AttendanceStaffDto attendanceStaffDto = new AttendanceStaffDto();
        attendanceStaffDto.setMachineId(tmpAttendanceMachine.getMachineId());
        attendanceStaffDto.setStaffId(reqJson.getString("staffId"));
        List<AttendanceStaffDto> attendanceStaffDtos = attendanceStaffV1InnerServiceSMOImpl.queryAttendanceStaffs(attendanceStaffDto);
        if (ListUtil.isNull(attendanceStaffDtos)) {
            return;
        }
        AttendanceStaffPo attendanceStaffPo = new AttendanceStaffPo();
        attendanceStaffPo.setMsId(attendanceStaffDtos.get(0).getMsId());

        attendanceStaffV1InnerServiceSMOImpl.deleteAttendanceStaff(attendanceStaffPo);
    }

}
