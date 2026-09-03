package com.java110.dto.invoice;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * 开票明细 DTO
 */
public class InvoiceOrderItemDto extends PageDto implements Serializable {

    public static final String BIZ_CATEGORY_CHARGE = "充电费";// 业务类目-充电

    private String itemId;
    private String invoiceOrderId;
    private String orderId;
    private String communityId;
    private String bizCategory;
    private String invoiceCategory;
    private String taxCode;
    private String taxRate;
    private String amount;
    private String seq;
    private String statusCd = "0";
    private Date createTime;

    private String[] invoiceOrderIds;// 批量查询条件
    private String[] orderIds;// 批量查询条件

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getInvoiceOrderId() {
        return invoiceOrderId;
    }

    public void setInvoiceOrderId(String invoiceOrderId) {
        this.invoiceOrderId = invoiceOrderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getBizCategory() {
        return bizCategory;
    }

    public void setBizCategory(String bizCategory) {
        this.bizCategory = bizCategory;
    }

    public String getInvoiceCategory() {
        return invoiceCategory;
    }

    public void setInvoiceCategory(String invoiceCategory) {
        this.invoiceCategory = invoiceCategory;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
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

    public String[] getInvoiceOrderIds() {
        return invoiceOrderIds;
    }

    public void setInvoiceOrderIds(String[] invoiceOrderIds) {
        this.invoiceOrderIds = invoiceOrderIds;
    }

    public String[] getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(String[] orderIds) {
        this.orderIds = orderIds;
    }
}
