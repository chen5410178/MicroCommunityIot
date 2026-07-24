package com.java110.intf.barrier;

import com.java110.intf.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "iot-barrier", configuration = {FeignConfiguration.class})
@RequestMapping("/carInoutHistoryMigrateV1Api")
public interface ICarInoutHistoryMigrateV1InnerServiceSMO {

    @RequestMapping(value = "/migrateCarInoutHis", method = RequestMethod.POST)
    int migrateCarInoutHis();
}
