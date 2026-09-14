package com.java110.acct.payment.adapt.changk.refund;

import com.alibaba.fastjson.JSONObject;
import com.java110.acct.payment.IRefundMoneyAdapt;
import com.java110.acct.payment.adapt.changk.ChangKSignature;
import com.java110.bean.ResultVo;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.onlinePay.OnlinePayDto;
import com.java110.dto.onlinePay.OnlinePayRefundDto;
import com.java110.dto.paymentPool.PaymentPoolDto;
import com.java110.dto.paymentPoolValue.PaymentPoolValueDto;
import com.java110.intf.acct.IPaymentPoolValueV1InnerServiceSMO;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TreeMap;

@Service("changKRefundAdapt")
public class ChangKRefundAdapt implements IRefundMoneyAdapt {

    private static final Logger logger = LoggerFactory.getLogger(ChangKRefundAdapt.class);

    @Autowired
    private IPaymentPoolValueV1InnerServiceSMO paymentPoolValueV1InnerServiceSMOImpl;

    @Autowired
    private RestTemplate outRestTemplate;

    @Override
    public ResultVo refund(OnlinePayDto onlinePayDto, OnlinePayRefundDto onlinePayRefundDto, PaymentPoolDto paymentPoolDto) throws Exception {

        if (onlinePayRefundDto == null || StringUtil.isEmpty(onlinePayRefundDto.getRefundId())) {
            throw new IllegalArgumentException("退款单号不能为空");
        }
        if (onlinePayRefundDto.getRefundFee() == null || onlinePayRefundDto.getRefundFee().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("退款金额必须大于0");
        }
        if (onlinePayDto.getTotalFee() == null
                || onlinePayRefundDto.getRefundFee().compareTo(onlinePayDto.getTotalFee()) > 0) {
            throw new IllegalArgumentException("退款金额不能大于原订单金额");
        }

        logger.info("【畅快聚合支付-退款】 订单编号=" + onlinePayDto.getOrderId() + ", 退款金额=" + onlinePayRefundDto.getRefundFee().doubleValue());

        PaymentPoolValueDto paymentPoolValueDto = new PaymentPoolValueDto();
        paymentPoolValueDto.setPpId(paymentPoolDto.getPpId());
        List<PaymentPoolValueDto> paymentPoolValueDtos = paymentPoolValueV1InnerServiceSMOImpl.queryPaymentPoolValues(paymentPoolValueDto);

        if (ListUtil.isNull(paymentPoolValueDtos)) {
            throw new IllegalArgumentException("配置错误,未配置参数");
        }

        String merchantNo = PaymentPoolValueDto.getValue(paymentPoolValueDtos, "CHANGK_MERCHANT_NO");
        String terminalId = PaymentPoolValueDto.getValue(paymentPoolValueDtos, "CHANGK_TERMINAL_ID");
        String accessToken = PaymentPoolValueDto.getValue(paymentPoolValueDtos, "CHANGK_ACCESS_TOKEN");

        // 退款流水号取本次退款单号，失败重试沿用同一单号与同一金额
        String tranNo = onlinePayRefundDto.getRefundId();

        String channelOutTradeNo = onlinePayDto.getTransactionId(); // 畅快侧订单号

        // 统一时间格式
        String terminalTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        TreeMap<String, String> params = new TreeMap<>();
        params.put("pay_type", "010");                    // 必填
        params.put("merchant_no", merchantNo);
        params.put("terminal_id", terminalId);
        params.put("terminal_trace", tranNo);
        params.put("terminal_time", terminalTime);          // 格式 yyyyMMddHHmmss
        params.put("refund_fee", String.valueOf((int) (onlinePayRefundDto.getRefundFee().doubleValue() * 100)));
        params.put("out_trade_no", channelOutTradeNo);
//        params.put("refund_reason", "用户主动退款");

        String sign = ChangKSignature.generateSign(params, accessToken);
        params.put("key_sign", sign);

        logger.info("【畅快退款】请求参数: " + params);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(JSONObject.toJSONString(params), headers);

        ResponseEntity<String> response = outRestTemplate.postForEntity(
                com.java110.acct.payment.adapt.changk.ChangKConfig.REFUND_URL, requestEntity, String.class);

        logger.info("【畅快退款】响应: " + response.getBody());

        return parseRefundResponse(response.getBody());
    }

    private ResultVo parseRefundResponse(String responseBody) {
        JSONObject result = JSONObject.parseObject(responseBody);
        String returnCode = result.getString("return_code");
        String resultCode = result.getString("result_code");
        String returnMsg = result.getString("return_msg");

        if (com.java110.acct.payment.adapt.changk.ChangKConfig.RESULT_CODE_SUCCESS.equals(returnCode)
                && com.java110.acct.payment.adapt.changk.ChangKConfig.RESULT_CODE_SUCCESS.equals(resultCode)) {
            return new ResultVo(ResultVo.CODE_OK, "退款成功");
        }
        return new ResultVo(ResultVo.CODE_ERROR, returnMsg);
    }
}