package com.java110.dto.workLicenseMachine;

import com.java110.dto.workLicenseFactory.WorkLicenseFactoryDto;

import java.io.Serializable;

public class WorkLicenseTtsTextDto implements Serializable {

    public WorkLicenseTtsTextDto() {
    }

    public WorkLicenseTtsTextDto(WorkLicenseMachineDto workLicenseMachineDto,WorkLicenseFactoryDto workLicenseFactoryDto,  String ttsText) {
        this.workLicenseFactoryDto = workLicenseFactoryDto;
        this.workLicenseMachineDto = workLicenseMachineDto;
        this.ttsText = ttsText;
    }

    private WorkLicenseFactoryDto workLicenseFactoryDto;

    private WorkLicenseMachineDto workLicenseMachineDto;

    private String ttsText;

    public WorkLicenseFactoryDto getWorkLicenseFactoryDto() {
        return workLicenseFactoryDto;
    }

    public void setWorkLicenseFactoryDto(WorkLicenseFactoryDto workLicenseFactoryDto) {
        this.workLicenseFactoryDto = workLicenseFactoryDto;
    }

    public WorkLicenseMachineDto getWorkLicenseMachineDto() {
        return workLicenseMachineDto;
    }

    public void setWorkLicenseMachineDto(WorkLicenseMachineDto workLicenseMachineDto) {
        this.workLicenseMachineDto = workLicenseMachineDto;
    }

    public String getTtsText() {
        return ttsText;
    }

    public void setTtsText(String ttsText) {
        this.ttsText = ttsText;
    }
}
