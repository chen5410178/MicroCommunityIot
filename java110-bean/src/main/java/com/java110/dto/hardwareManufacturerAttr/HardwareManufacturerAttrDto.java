package com.java110.dto.hardwareManufacturerAttr;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 厂家属性数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class HardwareManufacturerAttrDto extends PageDto implements Serializable {

    public static final String SPEC_TOPIC = "95001"; //topic

    private String hmId;
    private String attrId;
    private String specCd;
    private String value;
    private String hmName;
    private String protocolImpl;
    private String hmType;

    private String defaultProtocol;



    private Date createTime;

    private String statusCd = "0";


    public String getHmId() {
        return hmId;
    }

    public void setHmId(String hmId) {
        this.hmId = hmId;
    }

    public String getAttrId() {
        return attrId;
    }

    public void setAttrId(String attrId) {
        this.attrId = attrId;
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

    public String getHmName() {
        return hmName;
    }

    public void setHmName(String hmName) {
        this.hmName = hmName;
    }

    public String getProtocolImpl() {
        return protocolImpl;
    }

    public void setProtocolImpl(String protocolImpl) {
        this.protocolImpl = protocolImpl;
    }

    public String getHmType() {
        return hmType;
    }

    public void setHmType(String hmType) {
        this.hmType = hmType;
    }

    public String getDefaultProtocol() {
        return defaultProtocol;
    }

    public void setDefaultProtocol(String defaultProtocol) {
        this.defaultProtocol = defaultProtocol;
    }
}
