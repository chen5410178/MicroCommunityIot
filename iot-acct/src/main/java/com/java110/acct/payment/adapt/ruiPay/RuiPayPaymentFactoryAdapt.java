package com.java110.acct.payment.adapt.ruiPay;

import com.alibaba.fastjson.JSONObject;
import com.java110.acct.payment.IPaymentFactoryAdapt;
import com.java110.core.cache.MappingCache;
import com.java110.core.cache.UrlCache;
import com.java110.core.constant.MappingConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.factory.WechatFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.PayUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.app.AppDto;
import com.java110.dto.payment.NotifyPaymentOrderDto;
import com.java110.dto.payment.PaymentOrderDto;
import com.java110.dto.paymentPoolValue.PaymentPoolValueDto;
import com.java110.dto.smallWeChat.SmallWeChatDto;
import com.java110.intf.acct.IPaymentPoolValueV1InnerServiceSMO;
import com.java110.intf.charge.ISmallWeChatInnerServiceSMO;
import com.java110.intf.system.ISmallWechatV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * 聚合支付厂家
 * 名字：聚合支付
 * 支持接入通道：银盛、易生、拉卡拉、盛付通、随行付，富友
 * 联系方式：请加sanner9微信联系
 * <p>
 * https://help.ruishengyi.com/doc/1/
 *
 *
 INSERT INTO `payment_adapt` (`adapt_id`, `payment_type`, `name`, `bean_jsapi`, `bean_qrcode`, `bean_native`, `create_time`, `status_cd`, `bean_refund`)
 VALUES ('6', 'RUIPAY', '聚合支付', 'ruiPayPaymentFactoryAdapt', 'qrCodeRuiPayPaymentAdapt', NULL, '2025-4-10 17:00:08', '0', 'ruiPayRefundMoney');


 INSERT INTO `payment_key` (`key_id`, `payment_type`, `name`, `column_key`, `remark`, `create_time`, `status_cd`)
 VALUES ('19', 'RUIPAY', 'APPKEY', 'RUI_APPKEY', '请输入应用键', '2023-10-26 14:11:55', '0');
 INSERT INTO `payment_key` (`key_id`, `payment_type`, `name`, `column_key`, `remark`, `create_time`, `status_cd`)
 VALUES ('20', 'RUIPAY', 'SECRET', 'RUI_SECRET', '请输入应用秘钥', '2023-10-26 14:11:55', '0');
 INSERT INTO `payment_key` (`key_id`, `payment_type`, `name`, `column_key`, `remark`, `create_time`, `status_cd`)
 VALUES ('21', 'RUIPAY', 'RPI_NO', 'RPI_NO', '请输入商户唯一编号', '2023-10-26 14:11:55', '0');

 */
@Service("ruiPayPaymentFactoryAdapt")
public class RuiPayPaymentFactoryAdapt implements IPaymentFactoryAdapt {

    private static final Logger logger = LoggerFactory.getLogger(RuiPayPaymentFactoryAdapt.class);


    //微信支付
    public static final String DOMAIN_WECHAT_PAY = "WECHAT_PAY";
    // 微信服务商支付开关
    public static final String WECHAT_SERVICE_PAY_SWITCH = "WECHAT_SERVICE_PAY_SWITCH";

    //开关ON打开
    public static final String WECHAT_SERVICE_PAY_SWITCH_ON = "ON";

    public static final String RUI_PAY_URL = "http://api.ruishengyi.com/rb-client/platform/openPlatform/router";


    private static final String WECHAT_SERVICE_APP_ID = "SERVICE_APP_ID";

    private static final String WECHAT_SERVICE_MCH_ID = "SERVICE_MCH_ID";

    public static final String TRADE_TYPE_NATIVE = "NATIVE";
    public static final String TRADE_TYPE_JSAPI = "JSAPI";
    public static final String TRADE_TYPE_MWEB = "MWEB";
    public static final String TRADE_TYPE_APP = "APP";


    @Autowired
    private ISmallWeChatInnerServiceSMO smallWechatV1InnerServiceSMOImpl;




    @Autowired
    private RestTemplate outRestTemplate;

    @Autowired
    private IPaymentPoolValueV1InnerServiceSMO paymentPoolValueV1InnerServiceSMOImpl;


    @Override
    public Map java110Payment(PaymentOrderDto paymentOrderDto, JSONObject reqJson, ICmdDataFlowContext context) throws Exception {

        SmallWeChatDto smallWeChatDto = getSmallWechat(reqJson);
        String paymentPoolId = reqJson.getString("paymentPoolId");


        String appId = context.getReqHeaders().get("app-id");
        String userId = context.getReqHeaders().get("user-id");
        String tradeType = reqJson.getString("tradeType");
        String notifyUrl = UrlCache.getOwnerUrl() + "/app/payment/notify/ruipay/992020011134400001/" + paymentPoolId;

        String openId = reqJson.getString("openId");


        logger.debug("【小程序支付】 统一下单开始, 订单编号=" + paymentOrderDto.getOrderId());
        SortedMap<String, String> resultMap = new TreeMap<String, String>();
        //生成支付金额，开发环境处理支付金额数到0.01、0.02、0.03元
        double payAmount = PayUtil.getPayAmountByEnv(MappingCache.getValue(MappingConstant.ENV_DOMAIN, "HC_ENV"), paymentOrderDto.getMoney());
        //添加或更新支付记录(参数跟进自己业务需求添加)

        JSONObject resMap = null;
        resMap = this.java110UnifieldOrder(paymentOrderDto.getName(),
                paymentOrderDto.getOrderId(),
                tradeType,
                payAmount,
                openId,
                smallWeChatDto,
                paymentPoolId,
                notifyUrl
        );


        if (TRADE_TYPE_JSAPI.equals(tradeType)) {
            resultMap.putAll(JSONObject.toJavaObject(resMap, Map.class));
            resultMap.put("sign", resultMap.get("paySign"));
        } else if (TRADE_TYPE_APP.equals(tradeType)) {
            resultMap.put("appId", smallWeChatDto.getAppId());
            resultMap.put("timeStamp", PayUtil.getCurrentTimeStamp());
            resultMap.put("nonceStr", PayUtil.makeUUID(32));
            resultMap.put("partnerid", smallWeChatDto.getMchId());
            resultMap.put("prepayid", resMap.getString("session_id"));
            //resultMap.put("signType", "MD5");
            resultMap.put("sign", PayUtil.createSign(resultMap, smallWeChatDto.getPayPassword()));
        } else if (TRADE_TYPE_NATIVE.equals(tradeType)) {
            resultMap.put("prepayId", resMap.getString("session_id"));
            resultMap.put("codeUrl", resMap.getString("qr_code"));
        }
        resultMap.put("code", "0");
        resultMap.put("msg", "下单成功");
        logger.info("【小程序支付】统一下单成功，返回参数:" + resultMap);
        return resultMap;
    }


    private JSONObject java110UnifieldOrder(String feeName, String orderNum,
                                            String tradeType, double payAmount, String openid,
                                            SmallWeChatDto smallWeChatDto,
                                            String paymentPoolId,
                                            String notifyUrl) throws Exception {
        PaymentPoolValueDto paymentPoolValueDto = new PaymentPoolValueDto();
        paymentPoolValueDto.setPpId(paymentPoolId);
        List<PaymentPoolValueDto> paymentPoolValueDtos = paymentPoolValueV1InnerServiceSMOImpl.queryPaymentPoolValues(paymentPoolValueDto);

        if (ListUtil.isNull(paymentPoolValueDtos)) {
            throw new IllegalArgumentException("配置错误,未配置参数");
        }

        String appKey = PaymentPoolValueDto.getValue(paymentPoolValueDtos, "RUI_APPKEY"); // 客户编号
        String secret = PaymentPoolValueDto.getValue(paymentPoolValueDtos, "RUI_SECRET");
        String rpiNo = PaymentPoolValueDto.getValue(paymentPoolValueDtos, "RPI_NO");


        if (feeName.length() > 127) {
            feeName = feeName.substring(0, 126);
        }

        // 1. 准备公共参数
        Map<String, String> params = new HashMap<>();
        params.put("appkey", appKey);
        params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        params.put("method", "rsy.online.barcodepay");
        params.put("rpi_no", rpiNo);
        params.put("notify_url", notifyUrl + "?wId=" + WechatFactory.getWId(smallWeChatDto.getAppId()));

        // 2. 准备业务参数
        Map<String, String> bizContent = new HashMap<>();
        bizContent.put("total_amount", payAmount+"");
        bizContent.put("pay_way", "1");//支付方式(1微信、2支付宝、3银联)
        bizContent.put("wx_auth_code", openid);//微信用户openId
        bizContent.put("out_trade_no", orderNum);
        bizContent.put("shopdate", DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_H));
        String sign = RuiPaySignatureGenerator.generateSign(bizContent, secret);
        bizContent.put("subject", feeName);
        bizContent.put("appid", smallWeChatDto.getAppId());


        // 将bizContent转为JSON字符串
        String bizContentJson = JSONObject.toJSONString(bizContent);
        params.put("biz_content", bizContentJson);

        // 4. 生成签名
        params.put("sign", sign);

        String queryString = RuiPaySignatureGenerator.buildQueryString(params);

        // 6. 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        System.out.println("\n请求报文：" + queryString);
        // 7. 发送请求
        HttpEntity<String> requestEntity = new HttpEntity<>(queryString, headers);
        ResponseEntity<String> response = outRestTemplate.postForEntity(RUI_PAY_URL, requestEntity, String.class);
        System.out.println("\n响应报文：" + response);

        // 8. 处理响应
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new IllegalArgumentException(response.getBody());
        }

        JSONObject paramOut = JSONObject.parseObject(response.getBody());
        if(paramOut.getIntValue("code") != 200){
            throw new IllegalArgumentException(paramOut.getString("msg"));
        }
        JSONObject data = paramOut.getJSONObject("data").getJSONObject("rsy_online_pay_response");
        //wxWcPayData
        String wxWcPayData = data.getString("js_api");

        return JSONObject.parseObject(wxWcPayData);
    }


    @Override
    public PaymentOrderDto java110NotifyPayment(NotifyPaymentOrderDto notifyPaymentOrderDto) {

        PaymentPoolValueDto paymentPoolValueDto = new PaymentPoolValueDto();
        paymentPoolValueDto.setPpId(notifyPaymentOrderDto.getPaymentPoolId());
        paymentPoolValueDto.setCommunityId(notifyPaymentOrderDto.getCommunityId());
        List<PaymentPoolValueDto> paymentPoolValueDtos = paymentPoolValueV1InnerServiceSMOImpl.queryPaymentPoolValues(paymentPoolValueDto);


        if (ListUtil.isNull(paymentPoolValueDtos)) {
            throw new IllegalArgumentException("配置错误,未配置参数");
        }

        String secret = PaymentPoolValueDto.getValue(paymentPoolValueDtos, "RUI_SECRET");

        String param = notifyPaymentOrderDto.getParam();
        System.out.println("支付结果返回值:" + param);
        JSONObject paramJson = JSONObject.parseObject(param);

        if(!RuiPaySignatureGenerator.checkSign(paramJson, secret)){
            throw new IllegalArgumentException("签名错误");
        }

        String outTradeNo = paramJson.getString("out_trade_no");
        String orderCode = paramJson.getString("order_code");




        PaymentOrderDto paymentOrderDto = new PaymentOrderDto();

        paymentOrderDto.setOrderId(outTradeNo);
        paymentOrderDto.setTransactionId(orderCode);

       // doUpdateOnlinePay(outTradeNo, OnlinePayDto.STATE_COMPILE, "支付成功");


        paymentOrderDto.setResponseEntity(new ResponseEntity<String>("success", HttpStatus.OK));
        return paymentOrderDto;
    }

    private SmallWeChatDto getSmallWechat(JSONObject paramIn) {

        SmallWeChatDto smallWeChatDto = new SmallWeChatDto();
        smallWeChatDto.setObjId(paramIn.getString("communityId"));
        smallWeChatDto.setAppId(paramIn.getString("appId"));
        smallWeChatDto.setPage(1);
        smallWeChatDto.setRow(1);
        List<SmallWeChatDto> smallWeChatDtos = smallWechatV1InnerServiceSMOImpl.querySmallWeChats(smallWeChatDto);

        if (smallWeChatDtos == null || smallWeChatDtos.size() < 1) {
            throw new IllegalArgumentException("未配置公众号信息");
        }

        return smallWeChatDtos.get(0);
    }
//
//
//    private void doUpdateOnlinePay(String orderId, String state, String message) {
//        OnlinePayPo onlinePayPo = new OnlinePayPo();
//        onlinePayPo.setMessage(message.length() > 1000 ? message.substring(0, 1000) : message);
//        onlinePayPo.setOrderId(orderId);
//        onlinePayPo.setState(state);
//        onlinePayV1InnerServiceSMOImpl.updateOnlinePay(onlinePayPo);
//    }
//
//    private void doSaveOnlinePay(SmallWeChatDto smallWeChatDto, String openId, String orderId, String feeName,
//                                 double money, String state, String message,String ppId) {
//        OnlinePayPo onlinePayPo = new OnlinePayPo();
//        onlinePayPo.setAppId(smallWeChatDto.getAppId());
//        onlinePayPo.setMchId(smallWeChatDto.getMchId());
//        onlinePayPo.setMessage(message.length() > 1000 ? message.substring(0, 1000) : message);
//        onlinePayPo.setOpenId(openId);
//        onlinePayPo.setOrderId(orderId);
//        onlinePayPo.setPayId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_orderId));
//        onlinePayPo.setPayName(feeName);
//        onlinePayPo.setRefundFee("0");
//        onlinePayPo.setState(state);
//        onlinePayPo.setTotalFee(money + "");
//        onlinePayPo.setTransactionId(orderId);
//        onlinePayPo.setPaymentPoolId(ppId);
//        onlinePayV1InnerServiceSMOImpl.saveOnlinePay(onlinePayPo);
//    }

}
