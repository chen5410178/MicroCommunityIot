package com.java110.acct.payment.adapt.ruiPay;

import com.alibaba.fastjson.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class RuiPaySignatureGenerator {
    /**
     * 生成签名
     * @param params 业务参数Map
     * @param secret 密钥
     * @return 签名值
     */
    public static String generateSign(Map<String, String> params, String secret) {
        try {
            // 1. 对参数按key升序排列
            Map<String, String> sortedParams = new TreeMap<>(params);

            // 2. 拼接键值对字符串
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                sb.append(entry.getKey()).append(entry.getValue());
            }
            String paramString = sb.toString();

            // 3. 拼接密钥
            String stringToSign = paramString + secret;

            // 4. 进行SHA1运算
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(stringToSign.getBytes());

            // 转换为16进制字符串
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not found", e);
        }
    }

    public static boolean checkSign(JSONObject param, String secret){
        String reSign = param.getString("sign");
        Map<String, String> signMap = new HashMap<>();
        signMap.put("order_code",param.getString("order_code"));
        signMap.put("notify_type",param.getString("notify_type"));
        signMap.put("trade_status",param.getString("trade_status"));
        signMap.put("out_trade_no",param.getString("out_trade_no"));
        signMap.put("notice_time",param.getString("notice_time"));
        String sign = generateSign(signMap,secret);
        if(reSign.equals(sign)){
            return true;
        }
        return false;
    }

    /**
     * 将字节数组转换为16进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * 将Map转换为URL查询字符串格式
     */
    public static String buildQueryString(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue()); // 注意：这里应该对值进行URL编码
        }
        return sb.toString();
    }

    // 示例用法
    public static void main(String[] args) {
        // 示例参数
        Map<String, String> params = new TreeMap<>();
        params.put("appkey", "000001");
        params.put("timestamp", "1234567890");
        params.put("method", "some.method");
        params.put("rpi_no", "MERCHANT123");
        params.put("biz_content", "{\"key1\":\"value1\",\"key2\":\"value2\"}");

        String secret = "your_secret_key";

        String sign = generateSign(params, secret);
        System.out.println("生成的签名: " + sign);
    }
}
