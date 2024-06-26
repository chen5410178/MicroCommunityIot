package com.java110.acct.payment.adapt.alipay;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.java110.acct.payment.IPaymentFactoryAdapt;
import com.java110.core.cache.MappingCache;
import com.java110.core.cache.UrlCache;
import com.java110.core.client.RestTemplate;
import com.java110.core.constant.MappingConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.PayUtil;
import com.java110.dto.app.AppDto;
import com.java110.dto.payment.NotifyPaymentOrderDto;
import com.java110.dto.payment.PaymentOrderDto;
import com.java110.dto.paymentPoolValue.PaymentPoolValueDto;
import com.java110.dto.smallWeChat.SmallWeChatDto;
import com.java110.intf.acct.IPaymentPoolValueV1InnerServiceSMO;
import com.java110.intf.charge.ISmallWeChatInnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 微信支付厂家类
 * <p>
 * 微信官方原生 支付实现类
 */
@Service("aliPaymentFactory")
public class AliPaymentFactoryAdapt implements IPaymentFactoryAdapt {

    private static final Logger logger = LoggerFactory.getLogger(AliPaymentFactoryAdapt.class);


    //微信支付
    public static final String DOMAIN_WECHAT_PAY = "WECHAT_PAY";
    // 微信服务商支付开关
    public static final String WECHAT_SERVICE_PAY_SWITCH = "WECHAT_SERVICE_PAY_SWITCH";

    //开关ON打开
    public static final String WECHAT_SERVICE_PAY_SWITCH_ON = "ON";


    private static final String WECHAT_SERVICE_APP_ID = "SERVICE_APP_ID";

    private static final String WECHAT_SERVICE_MCH_ID = "SERVICE_MCH_ID";

    public static final String TRADE_TYPE_NATIVE = "NATIVE";
    public static final String TRADE_TYPE_JSAPI = "JSAPI";
    public static final String TRADE_TYPE_MWEB = "MWEB";
    public static final String TRADE_TYPE_APP = "APP";


    public static final String wxPayUnifiedOrder = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    @Autowired
    private ISmallWeChatInnerServiceSMO smallWechatV1InnerServiceSMOImpl;


//    @Autowired
//    private IOwnerAppUserInnerServiceSMO ownerAppUserInnerServiceSMOImpl;


//    @Autowired
//    private IOnlinePayV1InnerServiceSMO onlinePayV1InnerServiceSMOImpl;

    @Autowired
    private IPaymentPoolValueV1InnerServiceSMO paymentPoolValueV1InnerServiceSMOImpl;

    @Autowired
    private RestTemplate outRestTemplate;


    @Override
    public Map java110Payment(PaymentOrderDto paymentOrderDto, JSONObject reqJson, ICmdDataFlowContext context) throws Exception {

        SmallWeChatDto smallWeChatDto = getSmallWechat(reqJson);

        String paymentPoolId = reqJson.getString("paymentPoolId");

        String tradeType = reqJson.getString("tradeType");
        String notifyUrl = UrlCache.getOwnerUrl() + "/app/payment/notify/wechat/992020011134400001/" + paymentPoolId;

        String openId = reqJson.getString("openId");


        logger.debug("【小程序支付】 统一下单开始, 订单编号=" + paymentOrderDto.getOrderId());
        SortedMap<String, String> resultMap = new TreeMap<String, String>();
        //生成支付金额，开发环境处理支付金额数到0.01、0.02、0.03元
        double payAmount = PayUtil.getPayAmountByEnv(MappingCache.getValue(MappingConstant.ENV_DOMAIN, "HC_ENV"), paymentOrderDto.getMoney());
        //添加或更新支付记录(参数跟进自己业务需求添加)

        PaymentPoolValueDto paymentPoolValueDto = new PaymentPoolValueDto();
        paymentPoolValueDto.setPpId(paymentPoolId);
        List<PaymentPoolValueDto> paymentPoolValueDtos = paymentPoolValueV1InnerServiceSMOImpl.queryPaymentPoolValues(paymentPoolValueDto);

        if (paymentPoolValueDtos == null || paymentPoolValueDtos.isEmpty()) {
            throw new IllegalArgumentException("配置错误,未配置参数");
        }

        JSONObject resMap = null;
        resMap = this.java110UnifieldOrder(paymentOrderDto.getName(), paymentOrderDto.getOrderId(), tradeType, payAmount, openId, smallWeChatDto, paymentPoolValueDtos, notifyUrl);

        return resMap;
    }


    private JSONObject java110UnifieldOrder(String feeName, String orderNum, String tradeType,
                                            double payAmount, String openid,
                                            SmallWeChatDto smallWeChatDto, List<PaymentPoolValueDto> paymentPoolValueDtos, String notifyUrl) throws Exception {


        String appId = PaymentPoolValueDto.getValue(paymentPoolValueDtos, "ALIPAY_APP_ID");
        String privateKey = PaymentPoolValueDto.getValue(paymentPoolValueDtos, "ALIPAY_PRIVATE_KEY");
        String publicKey = PaymentPoolValueDto.getValue(paymentPoolValueDtos, "ALIPAY_PUBLIC_KEY");

        if (feeName.length() > 127) {
            feeName = feeName.substring(0, 126);
        }

        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appId, privateKey,
                "json",
                "GBK", publicKey, "RSA2");
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
//异步接收地址，仅支持http/https，公网可访问
        request.setNotifyUrl(notifyUrl);
//同步跳转地址，仅支持http/https
        request.setReturnUrl(UrlCache.getOwnerUrl() + "/#/pages/successPage/successPage?msg=支付成功&objType=3003");
/******必传参数******/
        JSONObject bizContent = new JSONObject();
//商户订单号，商家自定义，保持唯一性
        bizContent.put("out_trade_no", orderNum);
//支付金额，最小值0.01元
        bizContent.put("total_amount", payAmount);
//订单标题，不可使用特殊符号
        bizContent.put("subject", feeName);

/******可选参数******/
//手机网站支付默认传值QUICK_WAP_WAY
        bizContent.put("product_code", "QUICK_WAP_WAY");
//bizContent.put("time_expire", "2022-08-01 22:00:00");

//// 商品明细信息，按需传入
//JSONArray goodsDetail = new JSONArray();
//JSONObject goods1 = new JSONObject();
//goods1.put("goods_id", "goodsNo1");
//goods1.put("goods_name", "子商品1");
//goods1.put("quantity", 1);
//goods1.put("price", 0.01);
//goodsDetail.add(goods1);
//bizContent.put("goods_detail", goodsDetail);

//// 扩展信息，按需传入
//JSONObject extendParams = new JSONObject();
//extendParams.put("sys_service_provider_id", "2088511833207846");
//bizContent.put("extend_params", extendParams);

        request.setBizContent(bizContent.toString());
        //   AlipayTradeWapPayResponse response = alipayClient.pageExecute(request, "POST");
// 如果需要返回GET请求，请使用
        AlipayTradeWapPayResponse response = alipayClient.pageExecute(request, "GET");
        String pageRedirectionData = response.getBody();
        System.out.println(pageRedirectionData);

        JSONObject paramObj = new JSONObject();

        if (response.isSuccess()) {
            System.out.println("调用成功");
            paramObj.put("code", 0);
            paramObj.put("msg", "调用成功");
            paramObj.put("url", pageRedirectionData);
        } else {
            System.out.println("调用失败");
            paramObj.put("code", 404);
            paramObj.put("msg", "调用失败" + response.getMsg());
        }
        // doSaveOnlinePay(smallWeChatDto, openid, orderNum, feeName, payAmount, OnlinePayDto.STATE_WAIT, "待支付",paymentPoolValueDtos.get(0).getPpId());
        return paramObj;
    }


    @Override
    public PaymentOrderDto java110NotifyPayment(NotifyPaymentOrderDto notifyPaymentOrderDto) {
        String resXml = "success";
        String param = notifyPaymentOrderDto.getParam();
        PaymentOrderDto paymentOrderDto = new PaymentOrderDto();
        PaymentPoolValueDto paymentPoolValueDto = new PaymentPoolValueDto();
        paymentPoolValueDto.setPpId(notifyPaymentOrderDto.getPaymentPoolId());
        paymentPoolValueDto.setCommunityId(notifyPaymentOrderDto.getCommunityId());
        List<PaymentPoolValueDto> paymentPoolValueDtos = paymentPoolValueV1InnerServiceSMOImpl.queryPaymentPoolValues(paymentPoolValueDto);

        if (paymentPoolValueDtos == null || paymentPoolValueDtos.isEmpty()) {
            throw new IllegalArgumentException("配置错误,未配置参数");
        }
        String publicKey = PaymentPoolValueDto.getValue(paymentPoolValueDtos, "ALIPAY_PUBLIC_KEY");

        Map<String, String> map = JSONObject.parseObject(param, Map.class);
        boolean verify_result = false;
        try {
            verify_result = AlipaySignature.rsaCheckV1(map, publicKey, "UTF-8", "RSA2");
            if (verify_result && "TRADE_SUCCESS".equals(map.get("trade_status"))) {//验证成功
                int result = confirmPayFee(map, paymentOrderDto, notifyPaymentOrderDto);
            } else {
                throw new IllegalArgumentException("签名错误");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


        paymentOrderDto.setResponseEntity(new ResponseEntity<String>(resXml, HttpStatus.OK));
        return paymentOrderDto;
    }

    public int confirmPayFee(Map<String, String> map, PaymentOrderDto paymentOrderDto, NotifyPaymentOrderDto notifyPaymentOrderDto) {
        //兼容 港币交易时 或者微信有时不会掉参数的问题

        SortedMap<String, String> paramMap = new TreeMap<String, String>();
        ResponseEntity<String> responseEntity = null;

        PaymentPoolValueDto paymentPoolValueDto = new PaymentPoolValueDto();
        paymentPoolValueDto.setPpId(notifyPaymentOrderDto.getPaymentPoolId());
        paymentPoolValueDto.setCommunityId(notifyPaymentOrderDto.getCommunityId());
        List<PaymentPoolValueDto> paymentPoolValueDtos = paymentPoolValueV1InnerServiceSMOImpl.queryPaymentPoolValues(paymentPoolValueDto);

        if (paymentPoolValueDtos == null || paymentPoolValueDtos.isEmpty()) {
            throw new IllegalArgumentException("配置错误,未配置参数");
        }


        String outTradeNo = map.get("out_trade_no").toString();
        paymentOrderDto.setOrderId(outTradeNo);
        paymentOrderDto.setTransactionId(map.get("trade_no").toString());

//        doUpdateOnlinePay(outTradeNo, OnlinePayDto.STATE_COMPILE, "支付成功");
        return 1;
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

}
