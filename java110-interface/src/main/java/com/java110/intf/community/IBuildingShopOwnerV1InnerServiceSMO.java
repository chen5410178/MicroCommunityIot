package com.java110.intf.community;

import com.java110.dto.shop.BuildingShopOwnerDto;
import com.java110.intf.FeignConfiguration;
import com.java110.po.shop.BuildingShopOwnerPo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 商铺业主关联V1内部服务接口
 */
@FeignClient(name = "iot-community", configuration = {FeignConfiguration.class})
@RequestMapping("/buildingShopOwnerV1Api")
public interface IBuildingShopOwnerV1InnerServiceSMO {

    @RequestMapping(value = "/saveBuildingShopOwner", method = RequestMethod.POST)
    int saveBuildingShopOwner(@RequestBody BuildingShopOwnerPo buildingShopOwnerPo);

    @RequestMapping(value = "/deleteBuildingShopOwner", method = RequestMethod.POST)
    int deleteBuildingShopOwner(@RequestBody BuildingShopOwnerPo buildingShopOwnerPo);

    @RequestMapping(value = "/updateBuildingShopOwner", method = RequestMethod.POST)
    int updateBuildingShopOwner(@RequestBody BuildingShopOwnerPo buildingShopOwnerPo);

    @RequestMapping(value = "/queryBuildingShopOwners", method = RequestMethod.POST)
    List<BuildingShopOwnerDto> queryBuildingShopOwners(@RequestBody BuildingShopOwnerDto buildingShopOwnerDto);

    @RequestMapping(value = "/queryBuildingShopOwnersCount", method = RequestMethod.POST)
    int queryBuildingShopOwnersCount(@RequestBody BuildingShopOwnerDto buildingShopOwnerDto);
}
