package com.java110.intf.charge;

import com.alibaba.fastjson.JSONObject;
import com.java110.intf.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 充电订单超时自动结算与停止
 */
@FeignClient(name = "iot-charge", configuration = {FeignConfiguration.class})
@RequestMapping("/chargeTimeoutV1Api")
public interface IChargeTimeoutV1InnerServiceSMO {

    /**
     * 处理超时未结束的充电订单，返回本次处理订单数
     *
     * @param reqJson bufferMinute / carTimeoutHour / row 可覆盖默认参数
     * @return 处理订单数
     */
    @RequestMapping(value = "/timeoutStopCharge", method = RequestMethod.POST)
    int timeoutStopCharge(@RequestBody JSONObject reqJson);
}
