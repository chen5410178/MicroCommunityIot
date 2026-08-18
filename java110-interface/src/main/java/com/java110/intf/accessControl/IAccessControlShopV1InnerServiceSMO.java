package com.java110.intf.accessControl;

import com.java110.dto.accessControlShop.AccessControlShopDto;
import com.java110.intf.FeignConfiguration;
import com.java110.po.accessControlShop.AccessControlShopPo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 商铺授权门禁V1内部服务接口
 */
@FeignClient(name = "iot-accessControl", configuration = {FeignConfiguration.class})
@RequestMapping("/accessControlShopV1Api")
public interface IAccessControlShopV1InnerServiceSMO {

    @RequestMapping(value = "/saveAccessControlShop", method = RequestMethod.POST)
    int saveAccessControlShop(@RequestBody AccessControlShopPo accessControlShopPo);

    @RequestMapping(value = "/updateAccessControlShop", method = RequestMethod.POST)
    int updateAccessControlShop(@RequestBody AccessControlShopPo accessControlShopPo);

    @RequestMapping(value = "/deleteAccessControlShop", method = RequestMethod.POST)
    int deleteAccessControlShop(@RequestBody AccessControlShopPo accessControlShopPo);

    @RequestMapping(value = "/queryAccessControlShops", method = RequestMethod.POST)
    List<AccessControlShopDto> queryAccessControlShops(@RequestBody AccessControlShopDto accessControlShopDto);

    @RequestMapping(value = "/queryAccessControlShopsCount", method = RequestMethod.POST)
    int queryAccessControlShopsCount(@RequestBody AccessControlShopDto accessControlShopDto);
}
