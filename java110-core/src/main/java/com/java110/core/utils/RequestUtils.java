package com.java110.core.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 请求信息处理工具类
 */
public class RequestUtils {


    public static void initHeadParam(HttpServletRequest request, Map headers) {

        Enumeration reqHeaderEnum = request.getHeaderNames();

        while (reqHeaderEnum.hasMoreElements()) {
            String headerName = (String) reqHeaderEnum.nextElement();
            headers.put(headerName.toLowerCase(), request.getHeader(headerName));
        }

        headers.put("IP", getIpAddr(request));

        //headers.put("hostName", request.getLocalName()); 这里导致部分主机 速度比较慢
        headers.put("hostName", "localhost");
        headers.put("port", request.getLocalPort() + "");

        //处理app-id
        if (headers.containsKey("app-id")) {
            headers.put("app_id", headers.get("app-id"));
        }

        //处理transaction-id
        if (headers.containsKey("transaction-id")) {
            headers.put("transaction_id", headers.get("transaction-id"));
        }

        //处理req-time
        if (headers.containsKey("req-time")) {
            headers.put("req_time", headers.get("req-time"));
        }

        //处理req-time
        if (headers.containsKey("user-id") && !"-1".equals(headers.get("user-id"))) {
            headers.put("user_id", headers.get("user-id"));
        }

    }


    /**
     * 获取IP地址
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    public static void initUrlParam(HttpServletRequest request, Map headers) {
        /*put real ip address*/

        Map readOnlyMap = request.getParameterMap();

        StringBuffer queryString = new StringBuffer(request.getRequestURL() != null ? request.getRequestURL() : "");

        if (readOnlyMap != null && !readOnlyMap.isEmpty()) {
            queryString.append("?");
            Set<String> keys = readOnlyMap.keySet();
            for (Iterator it = keys.iterator(); it.hasNext(); ) {
                String key = (String) it.next();
                String[] value = (String[]) readOnlyMap.get(key);
//                String[] value = (String[]) readOnlyMap.get(key);
                if (value.length > 1) {
                    for (int j = 0; j < value.length; j++) {
                        queryString.append(key);
                        queryString.append("=");
                        queryString.append(value[j]);
                        queryString.append("&");
                    }
                } else {
                    queryString.append(key);
                    queryString.append("=");
                    queryString.append(value[0]);
                    queryString.append("&");
                }
                if (!hasValidHeader(key.toLowerCase())) {
                    continue;
                }
                headers.put(key, value[0]);
            }
        }

        /*put requst url*/
        if (readOnlyMap != null && !readOnlyMap.isEmpty()) {
            headers.put("REQUEST_URL", queryString.toString().substring(0, queryString.toString().length() - 1));
        } else {
            headers.put("REQUEST_URL", queryString.toString());
        }

    }

    public static boolean hasValidHeader(String key) {
        if ("app-id".equals(key) || "app_id".equals(key)) {
            return true;
        }
        if ("transaction-id".equals(key) || "transaction_id".equals(key)) {
            return true;
        }
        if ("req-time".equals(key) || "req_time".equals(key)) {
            return true;
        }
        if ("sign".equals(key)) {
            return true;
        }
        if ("user-id".equals(key) || "user_id".equals(key)) {
            return true;
        }
        if ("java110-lang".equals(key)) {
            return true;
        }
        if ("store-id".equals(key)) {
            return true;
        }

        return false;
    }


    public static Map<String, String> getParameterStringMap(HttpServletRequest request) {
        Map<String, String[]> properties = request.getParameterMap();//把请求参数封装到Map<String, String[]>中
        Map<String, String> returnMap = new HashMap<String, String>();
        String name = "";
        String value = "";
        for (Map.Entry<String, String[]> entry : properties.entrySet()) {
            name = entry.getKey();
            String[] values = entry.getValue();
            if (null == values) {
                value = "";
            } else if (values.length > 1) {
                for (int i = 0; i < values.length; i++) { //用于请求参数中有多个相同名称
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length() - 1);
            } else {
                value = values[0];//用于请求参数中请求参数名唯一
            }
            returnMap.put(name, value);

        }
        return returnMap;
    }
}
