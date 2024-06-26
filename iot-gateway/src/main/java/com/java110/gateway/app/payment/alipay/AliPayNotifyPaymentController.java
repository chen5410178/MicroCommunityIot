package com.java110.gateway.app.payment.alipay;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.factory.LoggerFactory;
import com.java110.dto.payment.NotifyPaymentOrderDto;
import com.java110.intf.acct.INotifyPaymentV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
@RequestMapping(path = "/app/payment/Notify")
public class AliPayNotifyPaymentController {

    private final static Logger logger = LoggerFactory.getLogger(AliPayNotifyPaymentController.class);

    @Autowired
    private INotifyPaymentV1InnerServiceSMO notifyPaymentV1InnerServiceSMOImpl;

    /**
     * <p>支付回调Api</p>
     *
     * @param request
     * @throws Exception
     */
    @RequestMapping(path = "/alipay/{appId}/{paymentPoolId}", method = RequestMethod.POST)
    public ResponseEntity<String> notify(@RequestBody String postInfo, @PathVariable String appId,
                                         @PathVariable String paymentPoolId,HttpServletRequest request) {
        Map<String,String> params = new HashMap<String,String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }


        String out_trade_no = request.getParameter("out_trade_no");
        //支付宝交易号

        String trade_no = request.getParameter("trade_no");

        //交易状态
        String trade_status = request.getParameter("trade_status");

        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//

        JSONObject paramInfo = new JSONObject();
        paramInfo.put("trade_no",trade_no);
        paramInfo.put("out_trade_no",out_trade_no);
        paramInfo.put("trade_status",trade_status);
        paramInfo.put("params",params);
        logger.debug("微信支付回调报文" + paramInfo);


        return notifyPaymentV1InnerServiceSMOImpl.notifyPayment(new NotifyPaymentOrderDto(appId,paramInfo.toJSONString(),"",paymentPoolId));

    }

    /**
     * <p>支付回调Api</p>
     *
     * @param request
     * @throws Exception
     */
    @RequestMapping(path = "/nativeAliPay/{appId}/{paymentPoolId}", method = RequestMethod.POST)
    public ResponseEntity<String> nativeNotify(@RequestBody String postInfo, @PathVariable String appId,@PathVariable String paymentPoolId, HttpServletRequest request) {

        logger.debug("微信支付回调报文" + postInfo);

        return notifyPaymentV1InnerServiceSMOImpl.nativeNotifyPayment(new NotifyPaymentOrderDto(appId,postInfo,"",paymentPoolId));

    }
}
