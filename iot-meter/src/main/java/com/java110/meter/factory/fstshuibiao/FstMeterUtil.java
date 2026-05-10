package com.java110.meter.factory.fstshuibiao;

import com.java110.core.cache.MappingCache;
import com.java110.core.constant.MappingConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * FST智慧水务平台工具类
 * 根据API文档V2.03实现
 */
public class FstMeterUtil {

    private static final Logger logger = LoggerFactory.getLogger(FstMeterUtil.class);

//    // 默认值
//    private static final String DEFAULT_API_VERSION = "";
//    private static final String DEFAULT_BASE_URL = "";

    // 响应解析最大尝试次数
    private static final int MAX_PARSE_ATTEMPTS = 3;

    /**
     * 同步请求（带通知URL）- 实际HTTP实现
     */
    public static String requestSync(String url, String requestBody, String notifyUrl) {
        long startTime = System.currentTimeMillis();
        logger.info("开始请求: {}", url);
        logger.debug("请求体: {}", requestBody);

        if (notifyUrl != null && !notifyUrl.isEmpty()) {
            logger.debug("通知URL: {}", notifyUrl);
        }

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        OutputStreamWriter writer = null;

        try {
            // 创建URL对象
            URL apiUrl = new URL(url);

            // 打开连接
            connection = (HttpURLConnection) apiUrl.openConnection();

            // 设置请求方法
            connection.setRequestMethod("POST");

            // 设置请求头
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("User-Agent", "Java110-FstMeter/1.0");

            // 设置超时时间
            connection.setConnectTimeout(30000); // 30秒连接超时
            connection.setReadTimeout(60000);    // 60秒读取超时

            // 启用输入输出
            connection.setDoOutput(true);
            connection.setDoInput(true);

            // 发送请求体
            writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
            writer.write(requestBody);
            writer.flush();

            // 获取响应码
            int responseCode = connection.getResponseCode();
            logger.debug("HTTP响应码: {}", responseCode);

            // 读取响应
            StringBuilder response = new StringBuilder();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 成功响应
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } else {
                // 错误响应
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                logger.warn("HTTP请求失败，响应码: {}，响应体: {}", responseCode, response);
            }

            String responseStr = response.toString();
            logger.debug("HTTP响应体: {}", responseStr);

            return responseStr;

        } catch (Exception e) {
            logger.error("HTTP请求异常: {}", e.getMessage(), e);
            return null;
        } finally {
            // 关闭资源
            try {
                if (writer != null) writer.close();
                if (reader != null) reader.close();
                if (connection != null) connection.disconnect();
            } catch (Exception e) {
                logger.warn("关闭资源失败", e);
            }

            // 记录性能日志
            long cost = System.currentTimeMillis() - startTime;
            logger.info("请求完成: {}, 耗时: {}ms", url, cost);
            if (cost > 1000) { // 超过1秒记录为慢请求
                logger.warn("慢请求警告: {}, 耗时: {}ms", url, cost);
            }
        }
    }

    /**
     * 同步请求（不带通知URL）
     */
    public static String requestSync(String url, String requestBody) {
        return requestSync(url, requestBody, null);
    }

    /**
     * 同步请求（带重试机制）
     */
    public static String requestSyncWithRetry(String url, String requestBody, int maxRetries) {
        return requestSyncWithRetry(url, requestBody, maxRetries, null);
    }

    /**
     * 同步请求（带重试机制和通知URL）
     */
    public static String requestSyncWithRetry(String url, String requestBody, int maxRetries, String notifyUrl) {
        int retryCount = 0;
        while (retryCount <= maxRetries) {
            try {
                String response = requestSync(url, requestBody, notifyUrl);
                if (response != null) {
                    return response;
                }
            } catch (Exception e) {
                logger.warn("第{}次请求失败: {}", retryCount, e.getMessage());
            }

            retryCount++;
            if (retryCount <= maxRetries) {
                try {
                    long sleepTime = 1000L * retryCount; // 指数退避
                    logger.info("等待{}ms后重试第{}次", sleepTime, retryCount);
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    logger.warn("请求重试被中断");
                    break;
                }
            }
        }

        logger.error("请求失败，已达到最大重试次数: {}", maxRetries);
        return null;
    }

    /**
     * 构建请求JSON
     */
    public static String buildRequestJson(Map<String, Object> requestData) {
        try {
            Map<String, Object> globalInfo = new HashMap<>();
            globalInfo.put("APIid", getApiId());
            globalInfo.put("APIversion", getApiVersion());
            long timestamp = System.currentTimeMillis() / 1000;
            globalInfo.put("Timestamp", timestamp);

            Map<String, Object> signParams = new HashMap<>();
            signParams.put("APIid", globalInfo.get("APIid"));
            signParams.put("APIversion", globalInfo.get("APIversion"));
            signParams.put("Timestamp", globalInfo.get("Timestamp"));

            String sign = generateSign(signParams);
            globalInfo.put("sign", sign);

            logger.info("签名生成 - Timestamp: {}, APIid: {}", timestamp, globalInfo.get("APIid"));

            Map<String, Object> request = new HashMap<>();
            request.put("globalinfo", globalInfo);
            request.put("requestinfo", requestData);

            String json = JSONObject.toJSONString(request);
            logger.debug("构建的请求JSON: {}", json);
            return json;

        } catch (Exception e) {
            logger.error("构建请求JSON失败", e);
            throw new RuntimeException("构建请求JSON失败: " + e.getMessage(), e);
        }
    }

    /**
     * 生成签名（根据FSTAPI文档V2.03算法）
     */
    public static String generateSign(Map<String, Object> params) {
        try {
            params.remove("sign");

            Map<String, String> stringParams = new HashMap<>();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                Object value = entry.getValue();
                if (value != null) {
                    stringParams.put(entry.getKey(), value.toString());
                }
            }

            List<String> keys = new ArrayList<>(stringParams.keySet());
            Collections.sort(keys);

            StringBuilder stringA = new StringBuilder();
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                String value = stringParams.get(key);
                if (i > 0) stringA.append("&");
                stringA.append(key).append("=").append(value);
            }

            String apiKey = getApiKey();
            String stringSignTemp = stringA + "&APIkey=" + apiKey;

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(stringSignTemp.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString().toUpperCase();

        } catch (Exception e) {
            logger.error("生成签名失败: {}", e.getMessage(), e);
            throw new RuntimeException("生成签名失败: " + e.getMessage(), e);
        }
    }

    /**
     * 验证签名（用于异步通知）
     */
    @SuppressWarnings("unchecked")
    public static boolean verifySign(Map<String, Object> params, String receivedSign) {
        try {
            String calculatedSign = generateSign(params);
            boolean isValid = calculatedSign.equalsIgnoreCase(receivedSign);
            if (!isValid) {
                logger.warn("签名不匹配 - 计算签名: {}, 收到签名: {}", calculatedSign, receivedSign);
            }
            return isValid;
        } catch (Exception e) {
            logger.error("验证签名异常", e);
            return false;
        }
    }

    /**
     * 构建接口URL
     */
    public static String buildApiUrl(String path) {
        String baseUrl = getBaseUrl();
        if (!baseUrl.endsWith("/") && !path.startsWith("/")) {
            return baseUrl + "/" + path;
        }
        return baseUrl + path;
    }

    /**
     * 解析FST平台返回的JSON响应（增强版，处理双重序列化）
     */
    public static Map<String, Object> parseResponse(String responseStr) {
        try {
            // 检查响应是否为空
            if (responseStr == null || responseStr.trim().isEmpty()) {
                logger.warn("响应数据为空");
                return null;
            }

            String trimmed = responseStr.trim();

            // 记录原始响应用于调试
            if (logger.isDebugEnabled()) {
                logger.debug("原始响应数据: {}", trimmed);
            }

            // 处理双重序列化的情况
            int attemptCount = 0;
            String originalTrimmed = trimmed;

            while (attemptCount < MAX_PARSE_ATTEMPTS) {
                try {
                    // 如果以引号开头和结尾，可能是双重序列化的JSON字符串
                    if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
                        // 去除外层引号
                        trimmed = trimmed.substring(1, trimmed.length() - 1);
                        // 处理转义字符
                        trimmed = trimmed.replace("\\\"", "\"");
                        attemptCount++;

                        logger.debug("第{}次解析尝试，去除外层引号后: {}", attemptCount,
                                trimmed.length() > 200 ? trimmed.substring(0, 200) + "..." : trimmed);
                    } else if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
                        // 已经是JSON对象格式，直接解析
                        Map<String, Object> result = JSON.parseObject(trimmed);
                        logger.debug("成功解析为JSON对象");
                        return result;
                    } else if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
                        // JSON数组格式
                        Map<String, Object> result = new HashMap<>();
                        result.put("array", JSON.parseArray(trimmed));
                        logger.debug("成功解析为JSON数组");
                        return result;
                    } else {
                        // 其他格式，尝试直接解析
                        logger.debug("尝试直接解析非标准格式");
                        Map<String, Object> result = JSON.parseObject(trimmed);
                        return result;
                    }
                } catch (Exception e) {
                    // 解析失败，继续尝试或返回null
                    logger.debug("第{}次解析失败: {}", attemptCount, e.getMessage());
                    if (attemptCount >= MAX_PARSE_ATTEMPTS - 1) {
                        // 最后一次尝试失败
                        logger.error("解析响应数据失败，已尝试{}次", MAX_PARSE_ATTEMPTS, e);
                        logger.error("最终解析失败的数据: {}", originalTrimmed);
                        return null;
                    }
                    attemptCount++;
                }
            }

            // 所有尝试都失败了
            logger.error("无法解析响应数据: {}", originalTrimmed);
            return null;

        } catch (Exception e) {
            logger.error("解析响应数据异常", e);
            logger.error("异常响应数据: {}", responseStr);
            return null;
        }
    }

    /**
     * 检查响应是否成功
     */
    @SuppressWarnings("unchecked")
    public static boolean isSuccessResponse(Map<String, Object> response) {
        try {
            if (response == null || response.isEmpty()) {
                logger.warn("响应数据为空");
                return false;
            }

            if (response.containsKey("returninfo")) {
                Map<String, Object> returnInfo = (Map<String, Object>) response.get("returninfo");
                if (returnInfo == null) {
                    logger.warn("returninfo为空");
                    return false;
                }

                Object codeObj = returnInfo.get("code");
                String status = (String) returnInfo.get("status");

                if (codeObj == null || status == null) {
                    logger.warn("code或status为空: code={}, status={}", codeObj, status);
                    return false;
                }

                String code = String.valueOf(codeObj);
                boolean success = "0".equals(code) && "success".equals(status);

                if (!success) {
                    String message = (String) returnInfo.get("message");
                    logger.warn("响应失败: code={}, status={}, message={}", code, status, message);
                }

                return success;
            }

            logger.warn("响应缺少returninfo字段");
            return false;
        } catch (Exception e) {
            logger.error("检查响应是否成功时发生异常", e);
            return false;
        }
    }

    /**
     * 获取错误信息（增强版）
     */
    @SuppressWarnings("unchecked")
    public static String getErrorMessage(Map<String, Object> response) {
        try {
            if (response == null || response.isEmpty()) {
                return "响应数据为空";
            }

            // 尝试从不同位置获取错误信息
            StringBuilder errorMsg = new StringBuilder();

            if (response.containsKey("returninfo")) {
                Map<String, Object> returnInfo = (Map<String, Object>) response.get("returninfo");
                if (returnInfo == null) {
                    return "returninfo为空";
                }

                String message = (String) returnInfo.get("message");
                Object code = returnInfo.get("code");
                String status = (String) returnInfo.get("status");

                if (code == null && message == null && status == null) {
                    return "未知错误";
                }

                errorMsg.append("错误码:").append(code != null ? code : "未知")
                        .append(", 状态:").append(status != null ? status : "未知")
                        .append(", 信息:").append(message != null ? message : "未知");

                // 如果有嵌套的data字段，也检查一下
                if (returnInfo.containsKey("data")) {
                    Object data = returnInfo.get("data");
                    if (data instanceof Map) {
                        Map<String, Object> dataMap = (Map<String, Object>) data;
                        if (dataMap.containsKey("message") || dataMap.containsKey("code")) {
                            String nestedMessage = (String) dataMap.get("message");
                            Object nestedCode = dataMap.get("code");
                            if (nestedMessage != null || nestedCode != null) {
                                errorMsg.append(" [嵌套错误: 码=").append(nestedCode != null ? nestedCode : "未知")
                                        .append(", 信息=").append(nestedMessage != null ? nestedMessage : "未知")
                                        .append("]");
                            }
                        }
                    }
                }

                return errorMsg.toString();
            } else if (response.containsKey("error")) {
                // 其他可能的错误字段
                Object error = response.get("error");
                return "错误: " + (error != null ? error.toString() : "未知");
            } else if (response.containsKey("errmsg")) {
                Object errmsg = response.get("errmsg");
                return "错误消息: " + (errmsg != null ? errmsg.toString() : "未知");
            }

            return "响应格式错误，缺少错误信息字段";
        } catch (Exception e) {
            logger.error("获取错误信息时发生异常", e);
            return "解析错误信息失败: " + e.getMessage();
        }
    }

    /**
     * 获取完整的响应数据（包括嵌套的data字段）
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getResponseData(Map<String, Object> response) {
        try {
            if (response == null || response.isEmpty()) {
                return null;
            }

            if (response.containsKey("returninfo")) {
                Map<String, Object> returnInfo = (Map<String, Object>) response.get("returninfo");
                if (returnInfo != null && returnInfo.containsKey("data")) {
                    Object data = returnInfo.get("data");
                    if (data instanceof Map) {
                        return (Map<String, Object>) data;
                    } else if (data instanceof String) {
                        // 如果是字符串，尝试解析
                        try {
                            return JSON.parseObject((String) data);
                        } catch (Exception e) {
                            logger.debug("data字段是字符串但无法解析为JSON: {}", data);
                        }
                    }
                }
            }

            // 如果没有嵌套的data，直接返回整个response
            return response;
        } catch (Exception e) {
            logger.error("获取响应数据时发生异常", e);
            return null;
        }
    }

    /**
     * 获取API ID
     */
    public static String getApiId() {
        try {
            String apiId = MappingCache.getValue(MappingConstant.FSTMETER_DOMAIN, "api_id");
            if (apiId == null || apiId.isEmpty()) {
                throw new RuntimeException("未配置FSTAPI ID");
            }
            return apiId;
        } catch (Exception e) {
            throw new RuntimeException("获取API ID失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取API密钥
     */
    public static String getApiKey() {
        try {
            String apiKey = MappingCache.getValue(MappingConstant.FSTMETER_DOMAIN, "api_key");
            if (apiKey == null || apiKey.isEmpty()) {
                throw new RuntimeException("未配置FSTAPI密钥");
            }
            return apiKey;
        } catch (Exception e) {
            throw new RuntimeException("获取API密钥失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取API版本
     */
    public static String getApiVersion() {
        try {
            String apiVersion = MappingCache.getValue(MappingConstant.FSTMETER_DOMAIN, "api_version");
//            return apiVersion != null ? apiVersion : DEFAULT_API_VERSION;
            if (apiVersion == null || apiVersion.isEmpty()) {
                throw new RuntimeException("未配置FSTAPI版本号");
            }
            return apiVersion;
        } catch (Exception e) {
            throw new RuntimeException("获取API版本号失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取基础URL
     */
    public static String getBaseUrl() {
        try {
            String baseUrl = MappingCache.getValue(MappingConstant.FSTMETER_DOMAIN, "base_url");
            if (baseUrl == null || baseUrl.isEmpty()) {
                throw new RuntimeException("未配置FSTAPIURL");
            }
            return baseUrl;
        } catch (Exception e) {
            throw new RuntimeException("获取APIURL失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查配置是否可用
     */
    public static boolean isConfigAvailable() {
        try {
            getApiId();
            getApiKey();
            getApiVersion();
            getBaseUrl();
            return true;
        } catch (Exception e) {
            logger.debug("配置检查失败: {}", e.getMessage());
            return false;
        }
    }
    /**
     * 获取配置错误信息，如果配置正常返回 null
     */
    public static String getConfigErrorMessage() {
        try {
            getApiId();
            getApiKey();
            getBaseUrl();
            getApiVersion();
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    /**
     * 测试HTTP请求连接（可选）
     */
    @SuppressWarnings("unused")
    public static boolean testHttpConnection() {
        try {
            String url = getBaseUrl();
            if (!url.endsWith("/")) {
                url += "/";
            }
            url += "test";

            URL testUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) testUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            connection.disconnect();

            boolean success = responseCode == HttpURLConnection.HTTP_OK ||
                    responseCode == HttpURLConnection.HTTP_NOT_FOUND;

            logger.info("HTTP连接测试 {}: {}", success ? "成功" : "失败", url);
            return success;
        } catch (Exception e) {
            logger.error("HTTP连接测试失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 生成银行流水号（用于缴费请求）
     */
    public static String generateBankSerialNumber(String orderId) {
        long timestamp = System.currentTimeMillis();
        return String.format("BANK_%d_%s", timestamp, orderId);
    }

    /**
     * 构建完整的充值状态说明
     */
    public static String buildChargeRemark(String statusName, String statusCode,
                                           String orderId, Double chargeAmount,
                                           Double chargeDegrees, String apiBankSerial,
                                           String localBankSerial, String completeTime,
                                           boolean isFinalStatus) {
        StringBuilder remark = new StringBuilder();
        remark.append("状态名:").append(statusName)
                .append("|状态码:").append(statusCode)
                .append("|完成时间:").append(completeTime)
                .append("|订单号:").append(orderId);

        if (chargeAmount != null) {
            remark.append("|充值金额:").append(String.format("%.2f", chargeAmount)).append("元");
        }

        if (chargeDegrees != null) {
            remark.append("|充值水量:").append(String.format("%.2f", chargeDegrees)).append("吨");
        }

        if (apiBankSerial != null && !apiBankSerial.isEmpty()) {
            remark.append("|API银行流水:").append(apiBankSerial);
        }

        if (localBankSerial != null && !localBankSerial.isEmpty()) {
            remark.append("|本地流水:").append(localBankSerial);
        }

        remark.append("|最后更新时间:").append(getCurrentTime())
                .append("|是否终态:").append(isFinalStatus ? "是" : "否");

        return remark.toString();
    }

    /**
     * 获取当前时间字符串
     */
    private static String getCurrentTime() {
        return String.format("%tF %tT", new Date(), new Date());
    }

    /**
     * 充电状态枚举（用于状态机管理）
     */
    public enum ChargeStatus {
        // 订单创建成功，等待缴费
        CREATED("D", "订单创建", false),
        // API调用成功，银行处理中
        API_SUCCESS("C", "API调用成功", false),
        // 业务处理完成
        BUSINESS_SUCCESS("C", "缴费成功", true),
        // 失败状态
        FAILED("E", "失败", true);

        private final String code;
        private final String name;
        private final boolean finalStatus;

        ChargeStatus(String code, String name, boolean finalStatus) {
            this.code = code;
            this.name = name;
            this.finalStatus = finalStatus;
        }

        public String getCode() { return code; }
        public String getName() { return name; }
        public boolean isFinalStatus() { return finalStatus; }

        /**
         * 检查状态流转是否合法
         */
        public boolean canTransitionTo(ChargeStatus nextStatus) {
            // 定义状态流转规则
            switch (this) {
                case CREATED:
                    return nextStatus == API_SUCCESS || nextStatus == FAILED;
                case API_SUCCESS:
                    return nextStatus == BUSINESS_SUCCESS || nextStatus == FAILED;
                case BUSINESS_SUCCESS:
                case FAILED:
                    return false; // 终态不能再流转
                default:
                    return false;
            }
        }

        /**
         * 根据状态码获取枚举
         */
        public static ChargeStatus fromCode(String code) {
            for (ChargeStatus status : values()) {
                if (status.code.equals(code)) {
                    return status;
                }
            }
            return null;
        }
    }
}