package com.java110.intf.community;

import com.java110.dto.shop.BuildingShopDto;
import com.java110.intf.FeignConfiguration;
import com.java110.po.shop.BuildingShopPo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 商铺V1内部服务接口
 */
@FeignClient(name = "iot-community", configuration = {FeignConfiguration.class})
@RequestMapping("/buildingShopV1Api")
public interface IBuildingShopV1InnerServiceSMO {

    @RequestMapping(value = "/saveBuildingShop", method = RequestMethod.POST)
    int saveBuildingShop(@RequestBody BuildingShopPo buildingShopPo);

    @RequestMapping(value = "/updateBuildingShop", method = RequestMethod.POST)
    int updateBuildingShop(@RequestBody BuildingShopPo buildingShopPo);

    @RequestMapping(value = "/deleteBuildingShop", method = RequestMethod.POST)
    int deleteBuildingShop(@RequestBody BuildingShopPo buildingShopPo);

    @RequestMapping(value = "/queryBuildingShops", method = RequestMethod.POST)
    List<BuildingShopDto> queryBuildingShops(@RequestBody BuildingShopDto buildingShopDto);

    @RequestMapping(value = "/queryBuildingShopsCount", method = RequestMethod.POST)
    int queryBuildingShopsCount(@RequestBody BuildingShopDto buildingShopDto);
}
