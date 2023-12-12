package com.java110.dto.dtModal;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 3d模型数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class DtModalDto extends PageDto implements Serializable {

    private String modalCode;
    private String modalType;
    private String modalTypeName;
    private String modalId;
    private String modalName;

    private String path;


    private Date createTime;

    private String statusCd = "0";


    public String getModalCode() {
        return modalCode;
    }

    public void setModalCode(String modalCode) {
        this.modalCode = modalCode;
    }

    public String getModalType() {
        return modalType;
    }

    public void setModalType(String modalType) {
        this.modalType = modalType;
    }

    public String getModalId() {
        return modalId;
    }

    public void setModalId(String modalId) {
        this.modalId = modalId;
    }

    public String getModalName() {
        return modalName;
    }

    public void setModalName(String modalName) {
        this.modalName = modalName;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getModalTypeName() {
        return modalTypeName;
    }

    public void setModalTypeName(String modalTypeName) {
        this.modalTypeName = modalTypeName;
    }
}
