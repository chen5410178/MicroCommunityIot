package com.java110.dto.instrument;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

public class InstrumentSensorDto extends PageDto implements Serializable {
    public static final String PRESSURE_SENSOR = "1001";
    public static final String LIQUID_LEVEL_SENSOR = "2002";
    public static final String TEMPERATURE_AND_HUMIDITY_SENSOR = "3003";
    public static final String TEMPERATURE_SENSOR = "4004";
    public static final String WATER_QUALITY_SENSOR = "5005";
    public static final String COMBUSTIBLE_GAS_SENSOR = "6006";
    public static final String WATERLOGGING_SENSOR = "7007";

    private String sensorId;
    private String machineId;
    private String sensorType;
    private String communityId;
    private String minValue;
    private String maxValue;
    private String units;
    private Date createTime;
    private String statusCd = "0";

    private String paramType;
    private String sensorTypeName;

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getSensorTypeName() {
        return sensorTypeName;
    }

    public void setSensorTypeName(String sensorTypeName) {
        this.sensorTypeName = sensorTypeName;
    }
}
