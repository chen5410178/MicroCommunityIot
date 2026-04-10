package com.java110.acct.payment.adapt.changk;

import com.alibaba.fastjson.JSONObject;
import com.java110.acct.payment.IPaymentFactoryAdapt;
import com.java110.bean.dto.owner.OwnerAppUserDto;
import com.java110.core.cache.MappingCache;
import com.java110.core.cache.UrlCache;
import com.java110.core.constant.MappingConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.PayUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.app.AppDto;
import com.java110.dto.payment.NotifyPaymentOrderDto;
import com.java110.dto.payment.PaymentOrderDto;
import com.java110.dto.paymentPoolValue.PaymentPoolValueDto;
import com.java110.dto.smallWeChat.SmallWeChatDto;
import com.java110.intf.acct.IOnlinePayV1InnerServiceSMO;
import com.java110.intf.acct.IPaymentPoolValueV1InnerServiceSMO;
import com.java110.intf.charge.ISmallWeChatInnerServiceSMO;
import com.java110.intf.system.ISmallWechatV1InnerServiceSMO;
import com.java110.intf.user.IAppUserV1InnerServiceSMO;
import com.java110.po.onlinePay.OnlinePayPo;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Service("changKPaymentFactoryAdapt")
public class ChangKPaymentFactoryAdapt implements IPaymentFactoryAdapt {

    private static final Logger logger = LoggerFactory.getLogger(ChangKPaymentFactoryAdapt.class);

    public static final String TRADE_TYPE_JSAPI = "JSAPI";
    public static final String TRADE_TYPE_NATIVE = "NATIVE";
    public static final String TRADE_TYPE_APP = "APP";

    @Autowired
    private ISmallWeChatInnerServiceSMO smallWechatV1InnerServiceSMOImpl;

    @Autowired
    private IAppUserV1InnerServiceSMO ownerAppUserInnerServiceSMOImpl;

    @Autowired
    private IOnlinePayV1InnerServiceSMO onlinePayV1InnerServiceSMOImpl;

    @Autowired
    private IPaymentPoolValueV1InnerServiceSMO paymentPoolValueV1InnerServiceSMOImpl;

    @Autowired
    private RestTemplate outRestTemplate;

    @Override
    public Map java110Payment(PaymentOrderDto paymentOrderDto, JSONObject reqJson, ICmdDataFlowContext context) throws Exception {
        String paymentPoolId = reqJson.getString("paymentPoolId");
        String tradeType = reqJson.getString("tradeType");
        String notifyUrl = UrlCache.getOwnerUrl() + "/app/payment/notify/changk/992020011134400001/" + paymentPoolId;
        SmallWeChatDto smallWeChatDto = getSmallWechat(reqJson);

        String appId = context.getReqHeaders().get("app-id");
        String userId = context.getReqHeaders().get("user-id");
        String busiId = reqJson.getString("busiId");

        String openId = reqJson.getString("openId");

        if (StringUtil.isEmpty(openId)) {
            throw new IllegalArgumentException("未包含openId");
        }

        double payAmount = PayUtil.getPayAmountByEnv(
                MappingCache.getValue(MappingConstant.ENV_DOMAIN, "HC_ENV"),
                paymentOrderDto.getMoney()
        );

        PaymentPoolValueDto paymentPoolValueDto = new PaymentPoolValueDto();
        paymentPoolValueDto.setPpId(paymentPoolId);
        List<PaymentPoolValueDto> paymentPoolValueDtos = paymentPoolValueV1InnerServiceSMOImpl.queryPaymentPoolValues(paymentPoolValueDto);

        if (paymentPoolValueDtos == null || paymentPoolValueDtos.isEmpty()) {
            throw new IllegalArgumentException("配置错误,未配置参数");
        }

        String merchantNo = PaymentPoolValueDto.getValue(paymentPoolValueDtos, "CHANGK_MERCHANT_NO");
        String terminalId = PaymentPoolValueDto.getValue(paymentPoolValueDtos, "CHANGK_TERMINAL_ID");
        String accessToken = PaymentPoolValueDto.getValue(paymentPoolValueDtos, "CHANGK_ACCESS_TOKEN");

        String payType = determinePayType(reqJson);

        SortedMap<String, String> resultMap = new TreeMap<>();


        resultMap = java110UnifieldOrder(paymentOrderDto, merchantNo, terminalId, accessToken,
                payType, smallWeChatDto, openId, payAmount, notifyUrl, paymentPoolId,paymentPoolValueDtos.get(0), busiId);

        return resultMap;
    }

    private SortedMap<String, String> java110UnifieldOrder(PaymentOrderDto paymentOrderDto, String merchantNo,
                                                           String terminalId, String accessToken,
                                                           String payType, SmallWeChatDto smallWeChatDto, String openId,
                                                           double payAmount, String notifyUrl, String ppId,
                                                           PaymentPoolValueDto paymentPoolValueDto,String busiId) throws Exception {
        TreeMap<String, String> params = new TreeMap<>();
        String orderId = paymentOrderDto.getOrderId();
        String terminalTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        params.put("pay_type", payType);
        params.put("merchant_no", merchantNo);
        params.put("terminal_id", terminalId);
        params.put("terminal_trace", orderId);
        params.put("terminal_time", terminalTime);
        params.put("total_fee", String.valueOf((int) (payAmount * 100)));
        params.put("sub_appid", smallWeChatDto.getAppId());
        params.put("open_id", openId);


        String feeName = paymentOrderDto.getName();
        if (feeName != null && feeName.length() > 127) {
            feeName = feeName.substring(0, 126);
        }
        params.put("order_body", feeName);
        params.put("notify_url", notifyUrl);
        params.put("attach", "");

        String sign = ChangKSignature.generateSign(params, accessToken);
        params.put("key_sign", sign);

        logger.info("【畅快公众号支付】请求参数: " + params);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(JSONObject.toJSONString(params), headers);
        String url =  ChangKConfig.JSP_PAY_URL;
        if(SmallWeChatDto.WECHAT_TYPE_MINI.equals(smallWeChatDto.getWechatType())){
            url = ChangKConfig.MINI_PAY_URL;
        }

        ResponseEntity<String> response = outRestTemplate.postForEntity(
                url, requestEntity, String.class);

        logger.info("【畅快公众号支付】响应: " + response.getBody());



        SortedMap<String, String> resultInfo  = null;
        if(SmallWeChatDto.WECHAT_TYPE_MINI.equals(smallWeChatDto.getWechatType())){
            resultInfo = parseMiniPayResponse(response.getBody(),orderId);
        }else {
            resultInfo = parseJsPayResponse(response.getBody(), orderId, ppId);
        }
        smallWeChatDto.setMchId(payType);
        doSaveOnlinePay(smallWeChatDto, openId,busiId, paymentOrderDto.getOrderId(), feeName, payAmount, "W",
                "待支付", paymentPoolValueDto.getPpId(),resultInfo.get("out_trade_no"));
        return resultInfo;
    }


    private SortedMap<String, String> parseJsPayResponse(String responseBody, String orderId, String ppId) throws Exception {
        TreeMap<String, String> resultMap = new TreeMap<>();
        JSONObject result = JSONObject.parseObject(responseBody);

        String returnCode = result.getString("return_code");
        String resultCode = result.getString("result_code");
        String returnMsg = result.getString("return_msg");

        if (ChangKConfig.RESULT_CODE_SUCCESS.equals(returnCode)
                && ChangKConfig.RESULT_CODE_SUCCESS.equals(resultCode)) {
            resultMap.put("code", "0");
            resultMap.put("msg", "下单成功");

            if (result.containsKey("appId")) {
                resultMap.put("appId", result.getString("appId"));
            }
            if (result.containsKey("timeStamp")) {
                resultMap.put("timeStamp", result.getString("timeStamp"));
            }
            if (result.containsKey("nonceStr")) {
                resultMap.put("nonceStr", result.getString("nonceStr"));
            }
            if (result.containsKey("package_str")) {
                resultMap.put("package", result.getString("package_str"));
            }
            if (result.containsKey("signType")) {
                resultMap.put("signType", result.getString("signType"));
            }
            if (result.containsKey("paySign")) {
                resultMap.put("paySign", result.getString("paySign"));
            }

            if (result.containsKey("ali_trade_no")) {
                resultMap.put("ali_trade_no", result.getString("ali_trade_no"));
            }

            if (result.containsKey("app_map")) {
                resultMap.put("app_map", result.getString("app_map"));
            }

            if (result.containsKey("redirect_url")) {
                resultMap.put("redirect_url", result.getString("redirect_url"));
            }

            resultMap.put("orderId", orderId);
            resultMap.put("out_trade_no", result.getString("out_trade_no"));
        } else {
            resultMap.put("code", "1");
            resultMap.put("msg", StringUtil.isEmpty(returnMsg) ? "支付失败" : returnMsg);
        }

        return resultMap;
    }

    private SortedMap<String, String> parseMiniPayResponse(String responseBody, String orderId) throws Exception {
        TreeMap<String, String> resultMap = new TreeMap<>();
        JSONObject result = JSONObject.parseObject(responseBody);

        String returnCode = result.getString("return_code");
        String resultCode = result.getString("result_code");
        String returnMsg = result.getString("return_msg");

        if (ChangKConfig.RESULT_CODE_SUCCESS.equals(returnCode)
                && ChangKConfig.RESULT_CODE_SUCCESS.equals(resultCode)) {
            resultMap.put("code", "0");
            resultMap.put("msg", "下单成功");

            if (result.containsKey("appId")) {
                resultMap.put("appId", result.getString("appId"));
            }
            if (result.containsKey("timeStamp")) {
                resultMap.put("timeStamp", result.getString("timeStamp"));
            }
            if (result.containsKey("nonceStr")) {
                resultMap.put("nonceStr", result.getString("nonceStr"));
            }
            if (result.containsKey("package_str")) {
                resultMap.put("package", result.getString("package_str"));
            }
            if (result.containsKey("signType")) {
                resultMap.put("signType", result.getString("signType"));
            }
            if (result.containsKey("paySign")) {
                resultMap.put("paySign", result.getString("paySign"));
            }

            if (result.containsKey("ali_trade_no")) {
                resultMap.put("ali_trade_no", result.getString("ali_trade_no"));
            }

            if (result.containsKey("app_map")) {
                resultMap.put("app_map", result.getString("app_map"));
            }

            if (result.containsKey("bizContent")) {
                resultMap.put("bizContent", result.getString("bizContent"));
            }

            resultMap.put("orderId", orderId);
            resultMap.put("out_trade_no", result.getString("out_trade_no"));
        } else {
            resultMap.put("code", "1");
            resultMap.put("msg", StringUtil.isEmpty(returnMsg) ? "支付失败" : returnMsg);
        }

        return resultMap;
    }

    private String determinePayType(JSONObject reqJson) {
        String payType = reqJson.getString("payType");
        if (StringUtil.isEmpty(payType)) {
            return ChangKConfig.PAY_TYPE_AUTO;
        }
        return payType;
    }


    @Override
    public PaymentOrderDto java110NotifyPayment(NotifyPaymentOrderDto notifyPaymentOrderDto) {
        PaymentPoolValueDto paymentPoolValueDto = new PaymentPoolValueDto();
        paymentPoolValueDto.setPpId(notifyPaymentOrderDto.getPaymentPoolId());
        paymentPoolValueDto.setCommunityId(notifyPaymentOrderDto.getCommunityId());
        List<PaymentPoolValueDto> paymentPoolValueDtos = paymentPoolValueV1InnerServiceSMOImpl.queryPaymentPoolValues(paymentPoolValueDto);

        if (paymentPoolValueDtos == null || paymentPoolValueDtos.isEmpty()) {
            throw new IllegalArgumentException("配置错误,未配置参数");
        }

        String accessToken = PaymentPoolValueDto.getValue(paymentPoolValueDtos, "CHANGK_ACCESS_TOKEN");

        String param = notifyPaymentOrderDto.getParam();
        logger.info("【畅快支付回调】回调参数: " + param);

        JSONObject params = JSONObject.parseObject(param);

        if (!ChangKSignature.verifyNotifySign(params, accessToken)) {
            throw new IllegalArgumentException("签名验证失败");
        }

        String returnCode = params.getString("return_code");
        String resultCode = params.getString("result_code");

        PaymentOrderDto paymentOrderDto = new PaymentOrderDto();
        paymentOrderDto.setOrderId(params.getString("terminal_trace"));
        paymentOrderDto.setTransactionId(params.getString("channel_trade_no"));

        if (ChangKConfig.RESULT_CODE_SUCCESS.equals(returnCode)
                && ChangKConfig.RESULT_CODE_SUCCESS.equals(resultCode)) {
            doUpdateOnlinePay(paymentOrderDto.getOrderId(), "C", "支付成功");
        }

        JSONObject resJson = new JSONObject();
        resJson.put("return_code", ChangKConfig.RETURN_CODE_SUCCESS);
        resJson.put("return_msg", "成功");

        paymentOrderDto.setResponseEntity(new ResponseEntity<String>(resJson.toJSONString(), HttpStatus.OK));
        return paymentOrderDto;
    }

    private void doUpdateOnlinePay(String orderId, String state, String message) {
        OnlinePayPo onlinePayPo = new OnlinePayPo();
        onlinePayPo.setMessage(message.length() > 1000 ? message.substring(0, 1000) : message);
        onlinePayPo.setOrderId(orderId);
        onlinePayPo.setState(state);
        onlinePayV1InnerServiceSMOImpl.updateOnlinePay(onlinePayPo);
    }

    private void doSaveOnlinePay(SmallWeChatDto smallWeChatDto, String openId,String busiId,  String orderId, String feeName,
                                 double money, String state, String message, String ppId,String transactionId) {
        OnlinePayPo onlinePayPo = new OnlinePayPo();
        onlinePayPo.setPayId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_orderId));
        onlinePayPo.setOrderId(orderId);
        onlinePayPo.setTransactionId("");
        onlinePayPo.setBusiId(busiId);
        onlinePayPo.setTotalFee(money + "");
        onlinePayPo.setRemainingFee(money + "");
        onlinePayPo.setPayName(feeName);
        onlinePayPo.setOpenId(openId);
        onlinePayPo.setState(state);
        onlinePayPo.setMessage(message.length() > 1000 ? message.substring(0, 1000) : message);
        onlinePayPo.setPaymentPoolId(ppId);
        onlinePayPo.setAppId(smallWeChatDto.getAppId());
        onlinePayV1InnerServiceSMOImpl.saveOnlinePay(onlinePayPo);
    }

    private SmallWeChatDto getSmallWechat(JSONObject paramIn) {

        SmallWeChatDto smallWeChatDto = new SmallWeChatDto();
        smallWeChatDto.setObjId(paramIn.getString("communityId"));
        smallWeChatDto.setAppId(paramIn.getString("appId"));
        smallWeChatDto.setPage(1);
        smallWeChatDto.setRow(1);
        List<SmallWeChatDto> smallWeChatDtos = smallWechatV1InnerServiceSMOImpl.querySmallWeChats(smallWeChatDto);

        if (ListUtil.isNull(smallWeChatDtos)) {
            throw new IllegalArgumentException("未配置公众号信息");
        }

        return smallWeChatDtos.get(0);
    }
}