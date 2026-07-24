package com.java110.intf.car;

import com.java110.bean.dto.car.UnlicensedCarPlateDto;
import com.java110.intf.FeignConfiguration;
import com.java110.po.car.UnlicensedCarPlatePo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "iot-car", configuration = {FeignConfiguration.class})
@RequestMapping("/unlicensedCarPlateV1Api")
public interface IUnlicensedCarPlateV1InnerServiceSMO {

    @RequestMapping(value = "/saveUnlicensedCarPlate", method = RequestMethod.POST)
    int saveUnlicensedCarPlate(@RequestBody UnlicensedCarPlatePo unlicensedCarPlatePo);

    @RequestMapping(value = "/queryUnlicensedCarPlates", method = RequestMethod.POST)
    List<UnlicensedCarPlateDto> queryUnlicensedCarPlates(@RequestBody UnlicensedCarPlateDto unlicensedCarPlateDto);

    @RequestMapping(value = "/getUnlicensedCarPlateCount", method = RequestMethod.POST)
    int getUnlicensedCarPlateCount(@RequestBody UnlicensedCarPlateDto unlicensedCarPlateDto);
}
