package com.java110.dto.chargeMachine;

import com.java110.dto.workLicenseMachine.WorkLicenseMachineDto;

public class LicenseMachineByteDataDto {

    public LicenseMachineByteDataDto() {
    }

    public LicenseMachineByteDataDto(WorkLicenseMachineDto workLicenseMachineDto, byte[] data) {
        this.workLicenseMachineDto = workLicenseMachineDto;
        this.data = data;
    }

    private WorkLicenseMachineDto workLicenseMachineDto;

    private byte[] data;

    public WorkLicenseMachineDto getWorkLicenseMachineDto() {
        return workLicenseMachineDto;
    }

    public void setWorkLicenseMachineDto(WorkLicenseMachineDto workLicenseMachineDto) {
        this.workLicenseMachineDto = workLicenseMachineDto;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
