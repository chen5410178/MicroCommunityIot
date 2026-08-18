package com.java110.intf.community;

import com.java110.dto.shop.ShopLocationDto;
import com.java110.intf.FeignConfiguration;
import com.java110.po.shop.ShopLocationPo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 商铺位置V1内部服务接口
 */
@FeignClient(name = "iot-community", configuration = {FeignConfiguration.class})
@RequestMapping("/shopLocationV1Api")
public interface IShopLocationV1InnerServiceSMO {

    @RequestMapping(value = "/saveShopLocation", method = RequestMethod.POST)
    int saveShopLocation(@RequestBody ShopLocationPo shopLocationPo);

    @RequestMapping(value = "/updateShopLocation", method = RequestMethod.POST)
    int updateShopLocation(@RequestBody ShopLocationPo shopLocationPo);

    @RequestMapping(value = "/deleteShopLocation", method = RequestMethod.POST)
    int deleteShopLocation(@RequestBody ShopLocationPo shopLocationPo);

    @RequestMapping(value = "/queryShopLocations", method = RequestMethod.POST)
    List<ShopLocationDto> queryShopLocations(@RequestBody ShopLocationDto shopLocationDto);

    @RequestMapping(value = "/queryShopLocationsCount", method = RequestMethod.POST)
    int queryShopLocationsCount(@RequestBody ShopLocationDto shopLocationDto);
}
