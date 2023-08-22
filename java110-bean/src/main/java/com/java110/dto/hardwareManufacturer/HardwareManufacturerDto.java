package com.java110.dto.hardwareManufacturer;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 厂家数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class HardwareManufacturerDto extends PageDto implements Serializable {

    // 门禁
    public static final String HM_TYPE_ACCESS_CONTROL = "1001";

    // 道闸
    public static final String HM_TYPE_BARRIER = "2002";

    private String hmId;
    private String license;
    private String prodUrl;
    private String author;
    private String link;
    private String defaultProtocol;
    private String hmName;
    private String protocolImpl;
    private String version;
    private String hmType;


    private Date createTime;

    private String statusCd = "0";


    public String getHmId() {
        return hmId;
    }

    public void setHmId(String hmId) {
        this.hmId = hmId;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getProdUrl() {
        return prodUrl;
    }

    public void setProdUrl(String prodUrl) {
        this.prodUrl = prodUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDefaultProtocol() {
        return defaultProtocol;
    }

    public void setDefaultProtocol(String defaultProtocol) {
        this.defaultProtocol = defaultProtocol;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHmType() {
        return hmType;
    }

    public void setHmType(String hmType) {
        this.hmType = hmType;
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
}
