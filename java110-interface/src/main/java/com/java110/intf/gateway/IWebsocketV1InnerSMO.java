package com.java110.intf.gateway;

import com.alibaba.fastjson.JSONObject;
import com.java110.intf.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "iot-gateway", configuration = {FeignConfiguration.class})
@RequestMapping("/websocketV1")
public interface IWebsocketV1InnerSMO {

    int webSentParkingArea(@RequestBody JSONObject reqJson);
}
