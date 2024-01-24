package com.java110.accessControl.manufactor.adapt.attendance;

import com.java110.accessControl.manufactor.AbstractAttendanceManufactorAdapt;
import com.java110.dto.attendanceMachine.AttendanceMachineDto;
import com.java110.po.accessControlFace.AccessControlFacePo;
import org.springframework.stereotype.Service;

@Service("changePaiAttendanceProcessAdapt")
public class ChangePaiAttendanceProcessAdapt extends AbstractAttendanceManufactorAdapt {
    @Override
    public boolean addUser(AttendanceMachineDto attendanceMachineDto, AccessControlFacePo accessControlFacePo) {
        return false;
    }

    @Override
    public boolean updateUser(AttendanceMachineDto attendanceMachineDto, AccessControlFacePo accessControlFacePo) {
        return false;
    }

    @Override
    public boolean deleteUser(AttendanceMachineDto attendanceMachineDto, AccessControlFacePo accessControlFacePo) {
        return false;
    }

    @Override
    public boolean restartMachine(AttendanceMachineDto attendanceMachineDto) {
        return false;
    }

    @Override
    public String attendanceResult(String topic, String param) {
        return null;
    }
}
