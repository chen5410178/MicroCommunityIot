package com.java110.core.factory;

import com.java110.bean.ResultVo;
import com.java110.core.cache.MappingCache;
import com.java110.core.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * @ClassName HttpFactory
 * @Description Http 请求工厂类
 * @Author wuxw
 * @Date 2020/5/11 0:06
 * @Version 1.0
 * add by wuxw 2020/5/11
 **/
public class PropertyHttpFactory {
    private static Logger logger = LoggerFactory.getLogger(PropertyHttpFactory.class);


    public final static String HTTP_APP_ID = "app-id";
    public final static String HTTP_TRANSACTION_ID = "transaction-id";
    public final static String HTTP_REQ_TIME = "req-time";
    public final static String HTTP_RES_TIME = "res-time";
    public final static String HTTP_SIGN = "sign";
    public final static String HTTP_USER_ID = "user-id";
    public final static String ORDER_DEFAULT_USER_ID = "-1";

    public static final String DOMAIN_WUYE = "WUYE";

    public static HttpHeaders getHeader(String appId) {
        HttpHeaders header = new HttpHeaders();
        header.add("Content-Type", "application/json");
        header.add(HTTP_APP_ID.toLowerCase(), appId);
        header.add(HTTP_USER_ID.toLowerCase(), ORDER_DEFAULT_USER_ID);
        header.add(HTTP_TRANSACTION_ID.toLowerCase(), UUID.randomUUID().toString());
        header.add(HTTP_REQ_TIME.toLowerCase(), DateUtil.getyyyyMMddhhmmssDateString());
        header.add(HTTP_SIGN.toLowerCase(), "");
        return header;
    }

    /**
     * map 参数转 url get 参数 非空值转为get参数 空值忽略
     *
     * @param info map数据
     * @return url get 参数 带？
     */
    public static String mapToUrlParam(Map info) {
        String urlParam = "";
        if (info == null || info.isEmpty()) {
            return urlParam;
        }

        urlParam += "?";

        for (Object key : info.keySet()) {
            if (StringUtils.isEmpty(info.get(key) + "")) {
                continue;
            }

            urlParam += (key + "=" + info.get(key) + "&");
        }

        urlParam = urlParam.endsWith("&") ? urlParam.substring(0, urlParam.length() - 1) : urlParam;

        return urlParam;
    }

    public static ResponseEntity<String> post(RestTemplate restTemplate, String url, String param, Map<String, String> headers) {
        return exchange(restTemplate, url, param, headers, HttpMethod.POST);
    }

    public static ResponseEntity<String> get(RestTemplate restTemplate, String url, Map<String, String> headers) {
        return exchange(restTemplate, url, "", headers, HttpMethod.GET);
    }

    /**
     * http 调用接口
     *
     * @param restTemplate 请求模板
     * @param url          请求地址
     * @param param        请求参数
     * @param headers      请求头
     * @param httpMethod   请求方法
     * @return 返回 ResponseEntity
     */
    public static ResponseEntity<String> exchange(RestTemplate restTemplate, String url, String param, Map<String, String> headers, HttpMethod httpMethod) {

        String appId = MappingCache.getValue(DOMAIN_WUYE, "appId");
        String securityCode = MappingCache.getValue(DOMAIN_WUYE, "securityCode");
        String urlPre = MappingCache.getValue(DOMAIN_WUYE, "url");
        url = urlPre + url;
        HttpHeaders httpHeaders = getHeader(appId);
        Date startTime = DateUtil.getCurrentDate();
        if (headers != null && !headers.isEmpty()) {
            for (String key : headers.keySet()) {
                if (HTTP_APP_ID.toLowerCase().equals(key.toLowerCase())) {
                    httpHeaders.remove(key.toLowerCase());
                    httpHeaders.add(key.toLowerCase(), headers.get(key));
                } else {
                    httpHeaders.add(key, headers.get(key));
                }
            }
        }
        String tempGetParam = "";
        if (url.indexOf("?") > 0) {
            tempGetParam = url.substring(url.indexOf("?"));
        }
        String paramIn = HttpMethod.GET == httpMethod ? tempGetParam : param;

        // 生成sign
        String sign = AuthenticationFactory.generatorSign(httpHeaders.get(HTTP_APP_ID).get(0), httpHeaders.get(HTTP_TRANSACTION_ID).get(0),
                httpHeaders.get(HTTP_REQ_TIME).get(0),
                paramIn, securityCode);
        httpHeaders.remove(HTTP_SIGN);
        httpHeaders.add(HTTP_SIGN, sign);
        HttpEntity<String> httpEntity = new HttpEntity<String>(param, httpHeaders);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(url, httpMethod, httpEntity, String.class);
        } catch (HttpStatusCodeException e) { //这里spring 框架 在4XX 或 5XX 时抛出 HttpServerErrorException 异常，需要重新封装一下
            responseEntity = ResultVo.error(e.getMessage());
        } catch (Exception e) {
            responseEntity = ResultVo.error(e.getMessage());
        } finally {
            logger.debug("请求地址为,{} 请求中心服务信息，{},中心服务返回信息，{}", url, httpEntity, responseEntity);
            // Date endTime = DateUtil.getCurrentDate();
            //saveTranLog(url, headers, param, responseEntity.getHeaders().toString(), responseEntity.getBody().toString(), startTime, endTime);
            return responseEntity;
        }
    }


}
