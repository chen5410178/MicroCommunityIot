package com.java110.dto.lock;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class LockMachineFactoryDto extends PageDto implements Serializable {
    private String factoryId;
    private String factoryName;
    private String beanImpl;
    private String remark;
    private Date createTime;
    private String statusCd = "0";

    private List<LockMachineFactorySpecDto> lockMachineFactorySpecList;

    public String getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(String factoryId) {
        this.factoryId = factoryId;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getBeanImpl() {
        return beanImpl;
    }

    public void setBeanImpl(String beanImpl) {
        this.beanImpl = beanImpl;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public List<LockMachineFactorySpecDto> getLockMachineFactorySpecList() {
        return lockMachineFactorySpecList;
    }

    public void setLockMachineFactorySpecList(List<LockMachineFactorySpecDto> lockMachineFactorySpecList) {
        this.lockMachineFactorySpecList = lockMachineFactorySpecList;
    }
}
