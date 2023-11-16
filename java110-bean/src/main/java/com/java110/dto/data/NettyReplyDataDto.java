package com.java110.dto.data;

import java.io.Serializable;

public class NettyReplyDataDto implements Serializable {

    public NettyReplyDataDto() {
    }

    public NettyReplyDataDto(String machineCode, byte[] data) {
        this.machineCode = machineCode;
        this.data = data;
    }

    private String machineCode;

    private byte[] data;

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
