package com.java110.dto.privilege;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 权限数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class PrivilegeDto extends PageDto implements Serializable {

    private String resource;
private String domain;
private String name;
private String description;
private String mId;
private String pId;


    private Date createTime;

    private String statusCd = "0";


    public String getResource() {
        return resource;
    }
public void setResource(String resource) {
        this.resource = resource;
    }
public String getDomain() {
        return domain;
    }
public void setDomain(String domain) {
        this.domain = domain;
    }
public String getName() {
        return name;
    }
public void setName(String name) {
        this.name = name;
    }
public String getDescription() {
        return description;
    }
public void setDescription(String description) {
        this.description = description;
    }
public String getMId() {
        return mId;
    }
public void setMId(String mId) {
        this.mId = mId;
    }
public String getPId() {
        return pId;
    }
public void setPId(String pId) {
        this.pId = pId;
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
