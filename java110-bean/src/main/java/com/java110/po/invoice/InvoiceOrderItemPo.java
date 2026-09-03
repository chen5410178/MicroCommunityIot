package com.java110.po.invoice;

import java.io.Serializable;

/**
 * 开票明细 Po（物联网 hc_iot 库，与商城 invoice_order_item 同名不同库）
 */
public class InvoiceOrderItemPo implements Serializable {

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
}
