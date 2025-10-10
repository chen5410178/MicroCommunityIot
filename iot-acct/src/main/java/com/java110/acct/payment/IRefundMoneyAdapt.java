package com.java110.acct.payment;

import com.java110.bean.ResultVo;
import com.java110.dto.onlinePay.OnlinePayDto;
import com.java110.dto.paymentPool.PaymentPoolDto;

public interface IRefundMoneyAdapt {

    /**
     * 退款处理
     * @param onlinePayDto
     * @param paymentPoolDto
     * @return
     */
    ResultVo refund(OnlinePayDto onlinePayDto, PaymentPoolDto paymentPoolDto) throws Exception;
}
