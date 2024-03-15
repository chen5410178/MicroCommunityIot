package com.java110.gateway.app.smartMeter;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.factory.LoggerFactory;
import com.java110.dto.meter.NotifyMeterWaterOrderDto;
import com.java110.intf.meter.INotifySmartMeterV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/app/smartMeter/notify")
public class NotifySmartMeterController {

    private final static Logger logger = LoggerFactory.getLogger(NotifySmartMeterController.class);

    @Autowired
    private INotifySmartMeterV1InnerServiceSMO notifySmartMeterV1InnerServiceSMOImpl;


    /**
     * <p>支付回调Api</p>
     *
     * @param request
     * @throws Exception
     */
    @RequestMapping(path = "/{implBean}", method = RequestMethod.POST)
    public ResponseEntity<String> notifyTq(
            @PathVariable String implBean,
            @RequestBody String postInfo,
            HttpServletRequest request) {
        String appId = "992020051967020024";

        JSONObject paramIn = new JSONObject();
        for (String key : request.getParameterMap().keySet()) {
            paramIn.put(key, request.getParameter(key));
            logger.debug("拓强回调报文form" + key + ":: " + request.getParameter(key));
        }
        logger.debug("拓强回调报文" + paramIn.toJSONString());

        //todo 为啥写的这么挫 因为拓强的电表 回调路径太长 他会失败
        switch (implBean) {
            case "a":
                implBean = "tqDianBiaoDanxiangDanFeiLvFactoryAdaptImpl";
                break;
            case "b":
                implBean = "tqDianBiaoRemoteDanFeiLvPreFactoryAdaptImpl";
                break;
            case "c":
                implBean = "tqShuiBiaoLoraReadFactoryAdaptImpl";
                break;
            case "d":
                implBean = "tqShuiBiaoLoraRechargeFactoryAdaptImpl";
                break;

            default:
        }


        return notifySmartMeterV1InnerServiceSMOImpl.notifySmartMater(new NotifyMeterWaterOrderDto(appId, paramIn.toJSONString(), implBean));

    }

    /**
     * <p>支付回调Api</p>
     *
     * @param request
     * @throws Exception
     */
    @RequestMapping(path = "/{implBean}/{appId}", method = RequestMethod.POST)
    public ResponseEntity<String> notify(
                                          @PathVariable String implBean,
                                          @PathVariable String appId,
                                          @RequestBody String postInfo,
                                          HttpServletRequest request) {

        return notifySmartMeterV1InnerServiceSMOImpl.notifySmartMater(new NotifyMeterWaterOrderDto(appId,postInfo,implBean));
    }



    @RequestMapping("/{implBean}/switchControl")
    public ResponseEntity<String> switchControlNotify(@PathVariable String implBean,
                                                      @RequestBody String postInfo,
                                                      HttpServletRequest request) {


        JSONObject paramIn = new JSONObject();
        for (String key : request.getParameterMap().keySet()) {
            paramIn.put(key, request.getParameter(key));
            logger.debug("拓强回调报文form" + key + ":: " + request.getParameter(key));
        }
        logger.debug("拓强回调报文" + paramIn.toJSONString());


        switch (implBean) {
            case "e":
                implBean ="tqDianBiaoRemoteDanFeiLvPreFactoryAdaptImpl";
                break;
            case "f":
                implBean = "tqDianBiaoRemoteDanFeiLvPreFactoryAdaptImpl";
            default:
        }

        return notifySmartMeterV1InnerServiceSMOImpl.switchControlNotify(implBean, paramIn.toJSONString());
    }

    @RequestMapping("/{implBean}/cleanControl")
    public ResponseEntity<String> cleanControlNotify(@PathVariable String implBean,
                                                      @RequestBody String postInfo,
                                                      HttpServletRequest request) {

        JSONObject paramIn = new JSONObject();
        for (String key : request.getParameterMap().keySet()) {
            paramIn.put(key, request.getParameter(key));
            logger.debug("拓强回调报文form" + key + ":: " + request.getParameter(key));
        }
        logger.debug("拓强回调报文" + paramIn.toJSONString());

        switch (implBean) {
            case "e":
                implBean ="tqDianBiaoRemoteDanFeiLvPreFactoryAdaptImpl";
                break;
            case "f":
                implBean = "tqDianBiaoRemoteDanFeiLvPreFactoryAdaptImpl";
            default:
        }

        return notifySmartMeterV1InnerServiceSMOImpl.cleanControlNotify(implBean, paramIn.toJSONString());
    }
}
