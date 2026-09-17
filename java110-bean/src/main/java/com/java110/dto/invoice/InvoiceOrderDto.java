package com.java110.dto.invoice;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * 开票订单 DTO
 */
public class InvoiceOrderDto extends PageDto implements Serializable {

    public static final String STATE_AUDIT = "AUDIT";// 待审核（商城开启审核开关）
    public static final String STATE_WAIT = "WAIT";// 待开票
    public static final String STATE_DONE = "DONE";// 已开票
    public static final String STATE_FAIL = "FAIL";// 开票失败
    public static final String STATE_RED = "RED";// 已红冲

    public static final String BIZ_CATEGORY_CHARGE = "充电费";// 业务类目-充电

    private String invoiceOrderId;
    private String userId;
    private String phone;
    private String communityId;
    private String headingId;
    private String headingType;
    private String buyerName;
    private String buyerNameLike;// 抬头名称模糊查询
    private String buyerTaxNo;
    private String buyerAddress;
    private String buyerBank;
    private String buyerBankNo;
    private String email;
    private String invoiceType;
    private String bizCategory;
    private String amount;
    private String invoiceId;
    private String state;
    private String invoiceNo;
    private String invoiceCode;
    private String pdfUrl;
    private String failReason;
    private String remark;
    private String statusCd = "0";
    private Date createTime;
    private Date updateTime;

    private String[] invoiceOrderIds;// 批量查询条件
    private String[] states;// 批量状态条件

    public String getInvoiceOrderId() {
        return invoiceOrderId;
    }

    public void setInvoiceOrderId(String invoiceOrderId) {
        this.invoiceOrderId = invoiceOrderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getHeadingId() {
        return headingId;
    }

    public void setHeadingId(String headingId) {
        this.headingId = headingId;
    }

    public String getHeadingType() {
        return headingType;
    }

    public void setHeadingType(String headingType) {
        this.headingType = headingType;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerNameLike() {
        return buyerNameLike;
    }

    public void setBuyerNameLike(String buyerNameLike) {
        this.buyerNameLike = buyerNameLike;
    }

    public String getBuyerTaxNo() {
        return buyerTaxNo;
    }

    public void setBuyerTaxNo(String buyerTaxNo) {
        this.buyerTaxNo = buyerTaxNo;
    }

    public String getBuyerAddress() {
        return buyerAddress;
    }

    public void setBuyerAddress(String buyerAddress) {
        this.buyerAddress = buyerAddress;
    }

    public String getBuyerBank() {
        return buyerBank;
    }

    public void setBuyerBank(String buyerBank) {
        this.buyerBank = buyerBank;
    }

    public String getBuyerBankNo() {
        return buyerBankNo;
    }

    public void setBuyerBankNo(String buyerBankNo) {
        this.buyerBankNo = buyerBankNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getBizCategory() {
        return bizCategory;
    }

    public void setBizCategory(String bizCategory) {
        this.bizCategory = bizCategory;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getInvoiceCode() {
        return invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String[] getInvoiceOrderIds() {
        return invoiceOrderIds;
    }

    public void setInvoiceOrderIds(String[] invoiceOrderIds) {
        this.invoiceOrderIds = invoiceOrderIds;
    }

    public String[] getStates() {
        return states;
    }

    public void setStates(String[] states) {
        this.states = states;
    }
}
