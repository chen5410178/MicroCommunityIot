package com.java110.acct.mall;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.cache.CommonCache;
import com.java110.core.cache.MappingCache;
import com.java110.core.client.OutRestTemplate;
import com.java110.core.constant.CommonConstant;
import com.java110.core.factory.AuthenticationFactory;
import com.java110.core.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Date;
import java.util.UUID;

/**
 * 商城开放接口调用实现（iot-acct 内置，开票专用）
 * 参照 MicroCommunityV2 service-job SendMallImpl 实现：
 * MALL_SWITCH 开关、管理员 token（pcUserLogin + CommonCache 缓存）、签名头、/mall/api/common.openCommonApi
 */
@Service
public class SendMallImpl implements ISendMall {

    private final static Logger logger = LoggerFactory.getLogger(SendMallImpl.class);

    public static final String GET_TOKEN_URL = "/mall/api/login.pcUserLogin";
    public static final String MALL_DOMAIN = "MALL"; // 商城配置域
    public static final String MALL_TOKEN = "MALL_ACCESS_TOKEN";
    private static final String MALL_URL_KEY = "MALL_URL";
    private static final String MALL_SWITCH_KEY = "MALL_SWITCH";
    private static final String DEFAULT_MALL_URL = "https://mall.homecommunity.cn";

    @Autowired
    private OutRestTemplate outRestTemplate;

    @Override
    public ResultVo get(String url) {
        url = getUrl(url);
        HttpHeaders header = getHeaders(url, "", HttpMethod.GET);
        HttpEntity<String> httpEntity = new HttpEntity<>("", header);
        ResponseEntity<String> tokenRes = null;
        try {
            tokenRes = outRestTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        } catch (HttpStatusCodeException e) {
            logger.error("调用商城异常", e);
            return new ResultVo(ResultVo.CODE_ERROR, e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("调用商城异常", e);
            return new ResultVo(ResultVo.CODE_ERROR, e.getMessage());
        }

        String body = tokenRes.getBody();
        JSONObject paramOut = JSONObject.parseObject(body);

        return new ResultVo(paramOut.getIntValue("code"), paramOut.getString("msg"), paramOut.get("data"));
    }

    @Override
    public ResultVo post(String url, JSONObject paramIn) {
        JSONObject paramOut = postForBody(url, paramIn);
        // 商城开关：未开启直接返回成功（data 为空，由调用方判定"商城开票未生效"）
        if (paramOut == null) {
            return new ResultVo(ResultVo.CODE_OK, ResultVo.MSG_OK, null);
        }
        return new ResultVo(paramOut.getIntValue("code"), paramOut.getString("msg"), paramOut.get("data"));
    }

    @Override
    public JSONObject postForBody(String url, JSONObject paramIn) {
        // 商城开关：未开启直接返回 null（由调用方判定"商城未开启/未部署"）
        if (!"ON".equals(MappingCache.getValue(MALL_DOMAIN, MALL_SWITCH_KEY))) {
            logger.warn("商城系统未部署( MALL_SWITCH != ON )，跳过调用 {}", url);
            return null;
        }

        url = getUrl(url);
        HttpHeaders header = getHeaders(url, paramIn.toJSONString(), HttpMethod.POST);
        HttpEntity<String> httpEntity = new HttpEntity<>(paramIn.toJSONString(), header);
        ResponseEntity<String> tokenRes = null;
        try {
            tokenRes = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        } catch (HttpStatusCodeException e) {
            logger.error("调用商城异常", e);
            return signErrorBody(e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("调用商城异常", e);
            return signErrorBody(e.getMessage());
        }

        String body = tokenRes.getBody();
        JSONObject paramOut = JSONObject.parseObject(body);

        if (paramOut == null) {
            return signErrorBody("商城响应异常");
        }

        return paramOut;
    }

    /**
     * 调用商城异常时统一封装错误报文（与 ResultVo 错误码保持一致）
     */
    private JSONObject signErrorBody(String msg) {
        JSONObject errorBody = new JSONObject();
        errorBody.put("code", ResultVo.CODE_ERROR);
        errorBody.put("msg", msg);
        return errorBody;
    }

    /**
     * 封装头信息（与商城门禁鉴权约定一致）
     */
    private HttpHeaders getHeaders(String url, String param, HttpMethod method) {
        HttpHeaders header = new HttpHeaders();
        header.add(CommonConstant.APP_ID.toLowerCase(), getMappingValue("APP_ID"));
        header.add(CommonConstant.USER_ID.toLowerCase(), CommonConstant.ORDER_DEFAULT_USER_ID);
        header.add(CommonConstant.TRANSACTION_ID.toLowerCase(), UUID.randomUUID().toString());
        header.add(CommonConstant.REQUEST_TIME.toLowerCase(), DateUtil.getDefaultFormateTimeString(new Date()));
        createSign(header, method, url, param);
        header.add("Authorization", "Bearer " + getToken(false));
        return header;
    }

    private String getMappingValue(String key) {
        String value = MappingCache.getValue(MALL_DOMAIN, key);
        return value == null ? "" : value;
    }

    public String getToken(boolean refreshAccessToken) {
        String token = CommonCache.getValue(MALL_TOKEN);
        if (token != null && !token.isEmpty() && !refreshAccessToken) {
            return token;
        }

        String url = getUrl(GET_TOKEN_URL);

        String userName = getMappingValue("MALL_USERNAME");
        String password = getMappingValue("MALL_PASSWORD");
        JSONObject param = new JSONObject();
        param.put("username", userName);
        param.put("passwd", password);

        HttpHeaders header = new HttpHeaders();
        header.add(CommonConstant.APP_ID.toLowerCase(), getMappingValue("APP_ID"));
        header.add(CommonConstant.USER_ID.toLowerCase(), CommonConstant.ORDER_DEFAULT_USER_ID);
        header.add(CommonConstant.TRANSACTION_ID.toLowerCase(), UUID.randomUUID().toString());
        header.add(CommonConstant.REQUEST_TIME.toLowerCase(), DateUtil.getDefaultFormateTimeString(new Date()));
        createSign(header, HttpMethod.POST, url, param.toJSONString());
        HttpEntity<String> httpEntity = new HttpEntity<>(param.toJSONString(), header);

        ResponseEntity<String> tokenRes = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

        JSONObject tokenObj = JSONObject.parseObject(tokenRes.getBody());
        if (tokenObj.getIntValue("code") != 0) {
            throw new IllegalArgumentException("获取token失败" + tokenRes.getBody());
        }
        if (!tokenObj.containsKey("token")) {
            throw new IllegalArgumentException("获取token失败" + tokenRes.getBody());
        }

        token = tokenObj.getString("token");
        int expiresIn = 30 * 60; //token 30分钟，过期前200s 刷新

        CommonCache.setValue(MALL_TOKEN, token, expiresIn - 200);

        return token;
    }

    private static String getUrl(String param) {
        String url = MappingCache.getValue(MALL_DOMAIN, MALL_URL_KEY);

        if (url == null || url.isEmpty()) {
            return DEFAULT_MALL_URL + param;
        }

        return url + param;
    }

    /**
     * 创建鉴权
     *
     * @param header    请求头
     * @param httpMethod 请求方式
     * @param url       请求地址
     * @param param     请求报文
     */
    public static void createSign(HttpHeaders header, HttpMethod httpMethod, String url, String param) {
        String appId = header.getFirst(CommonConstant.APP_ID);
        String transactionId = header.getFirst(CommonConstant.TRANSACTION_ID);
        String requestTime = header.getFirst(CommonConstant.REQUEST_TIME);
        String securityCode = MappingCache.getValue(MALL_DOMAIN, "APP_SECRET");

        if (securityCode == null || securityCode.isEmpty()) {
            return;
        }
        String paramStr = "";
        if (HttpMethod.GET == httpMethod) {
            int indexOf = url.indexOf("?");
            if (indexOf > -1) {
                paramStr = url.substring(indexOf);
            }
        } else {
            paramStr = param;
        }
        String sign = transactionId + requestTime + appId + paramStr + securityCode;
        header.remove("sign");
        try {
            header.add("sign", AuthenticationFactory.md5(sign));
        } catch (Exception e) {
            logger.error("生成签名失败", e);
        }
    }
}
