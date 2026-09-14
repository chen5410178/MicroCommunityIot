package com.java110.acct.payment;

import com.java110.bean.ResultVo;
import com.java110.dto.onlinePay.OnlinePayDto;
import com.java110.dto.onlinePay.OnlinePayRefundDto;
import com.java110.dto.paymentPool.PaymentPoolDto;

public interface IRefundMoneyAdapt {

    /**
     * 退款处理
     *
     * @param onlinePayDto       原支付记录（orderId/totalFee/appId 等，total_fee 必须为原订单金额）
     * @param onlinePayRefundDto 本次退款单（refundId 作为 out_refund_no，refundFee 为本次退款金额）
     * @param paymentPoolDto     支付厂家配置
     * @return 退款结果
     */
    ResultVo refund(OnlinePayDto onlinePayDto, OnlinePayRefundDto onlinePayRefundDto, PaymentPoolDto paymentPoolDto) throws Exception;
}
