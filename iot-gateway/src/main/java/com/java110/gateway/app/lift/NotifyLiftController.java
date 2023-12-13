package com.java110.gateway.app.lift;

import com.java110.core.factory.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/app/lift/notify")
public class NotifyLiftController {
    private final static Logger logger = LoggerFactory.getLogger(NotifyLiftController.class);


}
