package com.java110.intf.acct;

import com.java110.dto.account.AccountRefundDto;
import com.java110.intf.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 账户余额退款内部服务接口
 */
@FeignClient(name = "iot-acct", configuration = {FeignConfiguration.class})
@RequestMapping("/accountRefundV1Api")
public interface IAccountRefundV1InnerServiceSMO {

    /**
     * 业主账户余额退款（按 online_pay 逐笔原路退回，并扣减余额、写退款明细）
     *
     * @param accountRefundDto 退款请求（acctId / communityId / amount / refundUserId / remark）
     * @return 退款结果（refundId / refundFee）
     */
    @RequestMapping(value = "/refundAccountAmount", method = RequestMethod.POST)
    AccountRefundDto refundAccountAmount(@RequestBody AccountRefundDto accountRefundDto);
}
