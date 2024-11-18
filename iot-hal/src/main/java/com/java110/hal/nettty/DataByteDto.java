package com.java110.hal.nettty;

public class DataByteDto {

    private String address;

    private byte[] data;

    private int dataLen;

    private int totalLen;

    public DataByteDto() {
    }

    public DataByteDto(String address, byte[] data, int dataLen, int totalLen) {
        this.address = address;
        this.data = data;
        this.dataLen = dataLen;
        this.totalLen = totalLen;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getDataLen() {
        return dataLen;
    }

    public void setDataLen(int dataLen) {
        this.dataLen = dataLen;
    }

    public int getTotalLen() {
        return totalLen;
    }

    public void setTotalLen(int totalLen) {
        this.totalLen = totalLen;
    }
}
