package com.java110.boot.controller.app.smartMeter;

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
    private INotifySmartMeterV1InnerServiceSMO notifySmartWeterV1InnerServiceSMOImpl;

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

        return notifySmartWeterV1InnerServiceSMOImpl.notifySmartMater(new NotifyMeterWaterOrderDto(appId,postInfo,implBean));
    }

}
