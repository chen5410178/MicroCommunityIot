package com.java110.gateway.app.payment.ruiPay;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.factory.LoggerFactory;
import com.java110.dto.payment.NotifyPaymentOrderDto;
import com.java110.intf.acct.INotifyPaymentV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/app/payment/notify")
public class RuiPayNotifyPaymentController {

    private final static Logger logger = LoggerFactory.getLogger(RuiPayNotifyPaymentController.class);

    @Autowired
    private INotifyPaymentV1InnerServiceSMO notifyPaymentV1InnerServiceSMOImpl;

    /**
     * <p>支付回调Api</p>
     *
     * @param request
     * @throws Exception
     */
    @RequestMapping(path = "/ruipay/{appId}/{paymentPoolId}", method = RequestMethod.POST)
    public ResponseEntity<String> notify(@PathVariable String appId,
                                         @PathVariable String paymentPoolId, HttpServletRequest request) {

        String notify_type = request.getParameter("notify_type");   //通知类型（可区分不同的回调业务）
        String sign = request.getParameter("sign");      //签名（仅加密 下列必传的参数），此字段不参与签名
        logger.info("收到回调，类型为：{},sign为：{}", notify_type, sign);

        //如果不是支付回调那么直接返回
        if (!notify_type.equals("pay.status.sync")) {
            return new ResponseEntity<String>("success", HttpStatus.OK);
        }
        String order_code = request.getParameter("order_code");     //系统订单号
        String trade_status = request.getParameter("trade_status"); //交易目前所处的状态。成功状态的值： TRADE_SUCCESS
        String out_trade_no = request.getParameter("out_trade_no"); //客户订单号
        String notice_time = request.getParameter("notice_time");   //发送请求的时间，格式"yyyy-MM-dd HH:mm:ss" 2022-11-7 14:29:55
        String total_amount = request.getParameter("total_amount"); //该笔订单的资金总额，单位为RMB-Yuan。取值范围为[0.01，100000000.00]，精确到小数点后两位。
        String account_date = request.getParameter("account_date"); //入账的时间
        String subject = request.getParameter("subject");   //订单备注(拉起交易时,传入的备注信息)
        String other_content = request.getParameter("other_content");//其他参数(拉起交易时,传入的其他参数)

        JSONObject paramIn = new JSONObject();
        paramIn.put("notify_type", notify_type);
        paramIn.put("sign", sign);
        paramIn.put("order_code", order_code);
        paramIn.put("trade_status", trade_status);
        paramIn.put("out_trade_no", out_trade_no);
        paramIn.put("notice_time", notice_time);
        paramIn.put("total_amount", total_amount);
        paramIn.put("account_date", account_date);
        paramIn.put("subject", subject);
        paramIn.put("other_content", other_content);

        return notifyPaymentV1InnerServiceSMOImpl.notifyPayment(new NotifyPaymentOrderDto(appId, paramIn.toJSONString(), "", paymentPoolId));

    }

    /**
     * <p>支付回调Api</p>
     *
     * @param request
     * @throws Exception
     */
    @RequestMapping(path = "/nativeruipay/{appId}/{paymentPoolId}", method = RequestMethod.POST)
    public ResponseEntity<String> nativeNotify(@RequestBody String postInfo, @PathVariable String appId, @PathVariable String paymentPoolId, HttpServletRequest request) {

        logger.debug("微信支付回调报文" + postInfo);

        return notifyPaymentV1InnerServiceSMOImpl.nativeNotifyPayment(new NotifyPaymentOrderDto(appId, postInfo, "", paymentPoolId));

    }
}
