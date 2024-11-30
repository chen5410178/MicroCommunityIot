package com.java110.core.utils;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.cache.MappingCache;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.AuthenticationFactory;
import com.java110.dto.monitorManufactor.MonitorManufactorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class MVideoHttpUtil {

    private static Logger logger = LoggerFactory.getLogger(MVideoHttpUtil.class);

    /**
     * post 请求mvideo 平台
     *
     * @param pathUrl
     * @param paramIn
     * @return
     * @throws Exception
     */
    public static JSONObject post(String pathUrl, JSONObject paramIn, MonitorManufactorDto monitorManufactorDto)  {
        RestTemplate outRestTemplate = ApplicationContextFactory.getBean("outRestTemplate", RestTemplate.class);
        HttpHeaders httpHeaders = new HttpHeaders();
        String appId = monitorManufactorDto.getAppId();
        String appSecure = monitorManufactorDto.getAppSecure();
        long timestamp = DateUtil.getCurrentDate().getTime();

        paramIn.put("appId", appId);
        paramIn.put("appSecure", appSecure);
        paramIn.put("timestamp", timestamp);

        String sign = AuthenticationFactory.md5(appId + paramIn.getString("mvApiCode") + timestamp + appSecure);
        paramIn.put("sign", sign);
        String url = monitorManufactorDto.getMmUrl();
        url = url.endsWith("/") ? (url + pathUrl) : (url + "/" + pathUrl);

        HttpHeaders headers = new HttpHeaders();

        HttpEntity httpEntity = new HttpEntity(paramIn.toJSONString(), headers);

        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        } catch (HttpStatusCodeException e) { //这里spring 框架 在4XX 或 5XX 时抛出 HttpServerErrorException 异常，需要重新封装一下
            responseEntity = ResultVo.error(e.getMessage());
            logger.error("调用接口" + pathUrl + "异常：", e);
            throw e;
        } catch (Exception e) {
            logger.error("调用接口" + pathUrl + "异常：", e);
            throw e;
        } finally {
            logger.debug("请求地址为,{} 请求中心服务信息，{},中心服务返回信息，{}", url, httpEntity, responseEntity);
        }
        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        return paramOut;
    }
}
