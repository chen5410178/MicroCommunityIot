package com.java110.dto.account;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.math.BigDecimal;

public class AccountRefundDto extends PageDto implements Serializable {

    public static final String DETAIL_TYPE_REFUND = AccountDetailDto.DETAIL_TYPE_IN_CANCEL;
    public static final String REFUND_REMARK = "余额退款";
    // 扣减账户余额并写退款明细（默认，业主余额退款）
    public static final String DEDUCT_ACCOUNT_AMOUNT_YES = "1";
    // 不扣减账户余额，仅原路退回（充电退款）
    public static final String DEDUCT_ACCOUNT_AMOUNT_NO = "0";

    private String acctId;
    private String communityId;
    private BigDecimal amount;
    private String refundUserId;
    private String remark;
    private String deductAccountAmount;

    private String refundId;
    private BigDecimal refundFee;

    public String getAcctId() {
        return acctId;
    }

    public void setAcctId(String acctId) {
        this.acctId = acctId;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getRefundUserId() {
        return refundUserId;
    }

    public void setRefundUserId(String refundUserId) {
        this.refundUserId = refundUserId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDeductAccountAmount() {
        return deductAccountAmount;
    }

    public void setDeductAccountAmount(String deductAccountAmount) {
        this.deductAccountAmount = deductAccountAmount;
    }


    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public BigDecimal getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(BigDecimal refundFee) {
        this.refundFee = refundFee;
    }
}
