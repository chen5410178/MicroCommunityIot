package com.java110.accessControl.manufactor.adapt.accessControl.wiegand;

import java.io.Serializable;

/**
 * 微耕数据对象
 *
 */
public class WiegandDataDto implements Serializable {
    private String type;
    private String cmd;
    private String machineCode;
    private String sequenceId;

    private String businessData;

    public WiegandDataDto() {
    }

    public WiegandDataDto(String type, String cmd, String machineCode, String sequenceId) {
        this.type = type;
        this.cmd = cmd;
        this.machineCode = machineCode;
        this.sequenceId = sequenceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getBusinessData() {
        return businessData;
    }

    public void setBusinessData(String businessData) {
        this.businessData = businessData;
    }
}
