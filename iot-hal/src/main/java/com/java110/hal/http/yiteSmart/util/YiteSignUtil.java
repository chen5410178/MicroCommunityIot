package com.java110.hal.http.yiteSmart.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

public class YiteSignUtil {

    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String ALGORITHM = "HmacSHA1";

    public static String linkMapKeyValues(Map<String, String> map, String delimiter) {
        List<String> keys = new ArrayList<>(map.keySet());
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            String value = map.get(key);
            sb.append(key).append(delimiter).append(value == null ? "" : value);
        }
        return sb.toString();
    }

    public static String genHMAC(String data, String key) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(DEFAULT_ENCODING), ALGORITHM);
            mac.init(secretKey);
            byte[] signData = mac.doFinal(data.getBytes(DEFAULT_ENCODING));
            String base64 = Base64.getEncoder().encodeToString(signData);
            return base64.replace('/', '_').replace('+', '-').toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException("HMAC-SHA1签名失败", e);
        }
    }
}
