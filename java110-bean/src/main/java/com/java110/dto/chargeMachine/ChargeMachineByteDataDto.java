package com.java110.dto.chargeMachine;

public class ChargeMachineByteDataDto {

    public ChargeMachineByteDataDto() {
    }

    public ChargeMachineByteDataDto(ChargeMachineDto chargeMachineDto, byte[] data) {
        this.chargeMachineDto = chargeMachineDto;

        this.data = data;
    }

    private ChargeMachineDto chargeMachineDto;

    private byte[] data;

    public ChargeMachineDto getChargeMachineDto() {
        return chargeMachineDto;
    }

    public void setChargeMachineDto(ChargeMachineDto chargeMachineDto) {
        this.chargeMachineDto = chargeMachineDto;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
