package com.java110.dto.monitor;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.List;

public class MonitorMachineAttrsDto extends PageDto implements Serializable {
    private String attrId;
    private String machineId;
    private String communityId;
    private String specCd;
    private String value;
    private String statusCd = "0";

    public static String getSpecValue(List<MonitorMachineAttrsDto> attrs,String specCd){
        for(MonitorMachineAttrsDto monitorMachineAttrsDto : attrs){
            if(specCd.equals(monitorMachineAttrsDto.getSpecCd())){
                return monitorMachineAttrsDto.getValue();
            }
        }
        return null;
    }

    public String getAttrId() {
        return attrId;
    }

    public void setAttrId(String attrId) {
        this.attrId = attrId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getSpecCd() {
        return specCd;
    }

    public void setSpecCd(String specCd) {
        this.specCd = specCd;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }
}
