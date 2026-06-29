package com.java110.gateway.app.payment.changk;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.factory.LoggerFactory;
import com.java110.dto.payment.NotifyPaymentOrderDto;
import com.java110.intf.acct.INotifyPaymentV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/app/payment/notify")
public class ChangKNotifyPaymentController {

    private static final Logger logger = LoggerFactory.getLogger(ChangKNotifyPaymentController.class);

    @Autowired
    private INotifyPaymentV1InnerServiceSMO notifyPaymentV1InnerServiceSMOImpl;

    @RequestMapping(path = "/changk/{appId}/{paymentPoolId}", method = RequestMethod.POST)
    public ResponseEntity<String> notify(@PathVariable String appId,
                                          @PathVariable String paymentPoolId,
                                          @RequestBody String postInfo,
                                          HttpServletRequest request) {
        logger.info("【畅快聚合支付回调】appId: {}, paymentPoolId: {}", appId, paymentPoolId);
        logger.info("【畅快聚合支付回调】请求报文: " + postInfo);

        JSONObject paramIn = new JSONObject();
        paramIn.put("param", postInfo);

        NotifyPaymentOrderDto notifyPaymentOrderDto = new NotifyPaymentOrderDto();
        notifyPaymentOrderDto.setAppId(appId);
        notifyPaymentOrderDto.setParam(postInfo);
        notifyPaymentOrderDto.setPaymentPoolId(paymentPoolId);

        return notifyPaymentV1InnerServiceSMOImpl.notifyPayment(notifyPaymentOrderDto);
    }

    @RequestMapping(path = "/nativechangk/{appId}/{paymentPoolId}", method = RequestMethod.POST)
    public ResponseEntity<String> nativeNotify(@RequestBody String postInfo,
                                                @PathVariable String appId,
                                                @PathVariable String paymentPoolId) {
        logger.info("【畅快聚合支付-原生回调】appId: {}, paymentPoolId: {}", appId, paymentPoolId);
        logger.debug("【畅快聚合支付回调报文】" + postInfo);

        NotifyPaymentOrderDto notifyPaymentOrderDto = new NotifyPaymentOrderDto();
        notifyPaymentOrderDto.setAppId(appId);
        notifyPaymentOrderDto.setParam(postInfo);
        notifyPaymentOrderDto.setPaymentPoolId(paymentPoolId);

        return notifyPaymentV1InnerServiceSMOImpl.nativeNotifyPayment(notifyPaymentOrderDto);
    }
}
