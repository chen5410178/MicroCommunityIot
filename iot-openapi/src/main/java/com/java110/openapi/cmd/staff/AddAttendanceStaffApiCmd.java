package com.java110.openapi.cmd.staff;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.file.FileDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.attendanceMachine.AttendanceMachineDto;
import com.java110.dto.attendanceStaff.AttendanceStaffDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.accessControl.IAttendanceMachineV1InnerServiceSMO;
import com.java110.intf.accessControl.IAttendanceStaffV1InnerServiceSMO;
import com.java110.intf.system.IFileInnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.attendanceStaff.AttendanceStaffPo;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

/**
 * 同步考勤员工
 */
@Java110Cmd(serviceCode = "staff.addAttendanceStaffApi")
public class AddAttendanceStaffApiCmd extends Cmd {

    @Autowired
    private IAttendanceStaffV1InnerServiceSMO attendanceStaffV1InnerServiceSMOImpl;

    @Autowired
    private IAttendanceMachineV1InnerServiceSMO attendanceMachineV1InnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IFileInnerServiceSMO fileInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "classesId", "未包含班组ID");
        Assert.hasKeyAndValue(reqJson, "staffId", "未包含员工ID");
        Assert.hasKeyAndValue(reqJson, "staffPhoto", "未包含员工人脸");


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

        String staffId = reqJson.getString("staffId");
        UserDto userDto = new UserDto();
        userDto.setStaffId(staffId);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);

        Assert.listOnlyOne(userDtos,"员工未同步");

        String faceUrl = getFaceUrl(reqJson);

        for(AttendanceMachineDto tmpAttendanceMachine : attendanceMachineDtos){

            doAttendanceStaff(reqJson,tmpAttendanceMachine,userDtos.get(0), faceUrl);
        }
    }

    private void doAttendanceStaff(JSONObject reqJson, AttendanceMachineDto tmpAttendanceMachine,UserDto userDto,String faceUrl) {

        AttendanceStaffDto attendanceStaffDto = new AttendanceStaffDto();
        attendanceStaffDto.setMachineId(tmpAttendanceMachine.getMachineId());
        attendanceStaffDto.setStaffId(userDto.getUserId());
        List<AttendanceStaffDto> attendanceStaffDtos = attendanceStaffV1InnerServiceSMOImpl.queryAttendanceStaffs(attendanceStaffDto);

        AttendanceStaffPo attendanceStaffPo = new AttendanceStaffPo();
        attendanceStaffPo.setStaffName(userDto.getName());
        attendanceStaffPo.setMessage("待下发考勤机");
        attendanceStaffPo.setState(AttendanceStaffDto.STATE_WAIT);
        attendanceStaffPo.setStaffId(userDto.getUserId());
        attendanceStaffPo.setCommunityId(tmpAttendanceMachine.getCommunityId());
        attendanceStaffPo.setMachineId(tmpAttendanceMachine.getMachineId());
        attendanceStaffPo.setFacePath(faceUrl);
        if(ListUtil.isNull(attendanceStaffDtos)){
            attendanceStaffPo.setMsId(GenerateCodeFactory.getGeneratorId("11"));
            attendanceStaffV1InnerServiceSMOImpl.saveAttendanceStaff(attendanceStaffPo);
            return;
        }
        attendanceStaffPo.setMsId(attendanceStaffDtos.get(0).getMsId());

        attendanceStaffV1InnerServiceSMOImpl.updateAttendanceStaff(attendanceStaffPo);
    }

    private String getFaceUrl(JSONObject reqJson) {
        if (!reqJson.containsKey("staffPhoto")) {
            return "";
        }

        String staffPhoto = reqJson.getString("staffPhoto");

        if (StringUtil.isEmpty(staffPhoto)) {
            return "";
        }

        FileDto fileDto = new FileDto();
        fileDto.setFileId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_file_id));
        fileDto.setFileName(fileDto.getFileId());
        fileDto.setContext(reqJson.getString("staffPhoto"));
        fileDto.setSuffix("jpeg");
        fileDto.setCommunityId("-1");
        String fileName = fileInnerServiceSMOImpl.saveFile(fileDto);

        return fileName;
    }
}
