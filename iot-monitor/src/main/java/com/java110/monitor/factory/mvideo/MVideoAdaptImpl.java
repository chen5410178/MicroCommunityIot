package com.java110.monitor.factory.mvideo;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.client.RestTemplate;
import com.java110.core.factory.AuthenticationFactory;
import com.java110.core.factory.PropertyHttpFactory;
import com.java110.core.utils.DateUtil;
import com.java110.dto.monitor.MonitorMachineDto;
import com.java110.dto.monitorManufactor.MonitorManufactorDto;
import com.java110.monitor.factory.IMonitorModelAdapt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * mvideo 监控平台
 */
@Service("mvideoAdaptImpl")
public class MVideoAdaptImpl implements IMonitorModelAdapt {
    private static Logger logger = LoggerFactory.getLogger(MVideoAdaptImpl.class);

    @Autowired
    private RestTemplate outRestTemplate;

    public static final String QUERY_VIDEO_URL = "oapp/community/getVideoUrl";

    @Override
    public void queryMonitorMachineState(MonitorMachineDto monitorMachineDto) {

    }

    @Override
    public String getVideoUrl(MonitorMachineDto monitorMachineDto, MonitorManufactorDto monitorManufactorDto) {

        String url = monitorManufactorDto.getMmUrl();
        url = url.endsWith("/")?(url+QUERY_VIDEO_URL):(url+"/"+QUERY_VIDEO_URL);
        String appId = monitorManufactorDto.getAppId();
        String appSecure = monitorManufactorDto.getAppSecure();
        long timestamp = DateUtil.getCurrentDate().getTime();
        String sign = AuthenticationFactory.md5(appId+monitorMachineDto.getMachineCode()+timestamp+appSecure);
        url +=("?appId="+appId+"&timestamp="+timestamp+"&machineCode="+monitorMachineDto.getMachineCode()+"&sign="+sign);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity httpEntity = new HttpEntity("",headers);

        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = outRestTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        } catch (HttpStatusCodeException e) { //这里spring 框架 在4XX 或 5XX 时抛出 HttpServerErrorException 异常，需要重新封装一下
            responseEntity = ResultVo.error(e.getMessage());
            logger.error("调用接口"+QUERY_VIDEO_URL+"异常：",e);
            throw e;
        } catch (Exception e) {
            logger.error("调用接口"+QUERY_VIDEO_URL+"异常：",e);
            throw e;
        } finally {
            logger.debug("请求地址为,{} 请求中心服务信息，{},中心服务返回信息，{}", url, httpEntity, responseEntity);
        }
        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        if(paramOut.getIntValue("code") != 0){
            throw new IllegalArgumentException(paramOut.getString("msg"));
        }

        return paramOut.getJSONObject("data").getString("url");
    }
}
