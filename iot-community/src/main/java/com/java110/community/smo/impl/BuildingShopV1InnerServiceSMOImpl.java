package com.java110.community.smo.impl;

import com.java110.bean.dto.PageDto;
import com.java110.community.dao.IBuildingShopV1ServiceDao;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.shop.BuildingShopDto;
import com.java110.intf.community.IBuildingShopV1InnerServiceSMO;
import com.java110.po.shop.BuildingShopPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商铺内部服务实现
 */
@RestController
public class BuildingShopV1InnerServiceSMOImpl implements IBuildingShopV1InnerServiceSMO {

    @Autowired
    private IBuildingShopV1ServiceDao buildingShopV1ServiceDaoImpl;

    @Override
    public int saveBuildingShop(@RequestBody BuildingShopPo buildingShopPo) {
        return buildingShopV1ServiceDaoImpl.saveBuildingShopInfo(BeanConvertUtil.beanCovertMap(buildingShopPo));
    }

    @Override
    public int updateBuildingShop(@RequestBody BuildingShopPo buildingShopPo) {
        return buildingShopV1ServiceDaoImpl.updateBuildingShopInfo(BeanConvertUtil.beanCovertMap(buildingShopPo));
    }

    @Override
    public int deleteBuildingShop(@RequestBody BuildingShopPo buildingShopPo) {
        buildingShopPo.setStatusCd("1");
        return buildingShopV1ServiceDaoImpl.updateBuildingShopInfo(BeanConvertUtil.beanCovertMap(buildingShopPo));
    }

    @Override
    public List<BuildingShopDto> queryBuildingShops(@RequestBody BuildingShopDto buildingShopDto) {
        int page = buildingShopDto.getPage();
        if (page != PageDto.DEFAULT_PAGE) {
            buildingShopDto.setPage((page - 1) * buildingShopDto.getRow());
        }
        List<BuildingShopDto> buildingShops = BeanConvertUtil.covertBeanList(
                buildingShopV1ServiceDaoImpl.getBuildingShopInfo(BeanConvertUtil.beanCovertMap(buildingShopDto)),
                BuildingShopDto.class);
        return buildingShops;
    }

    @Override
    public int queryBuildingShopsCount(@RequestBody BuildingShopDto buildingShopDto) {
        return buildingShopV1ServiceDaoImpl.queryBuildingShopsCount(BeanConvertUtil.beanCovertMap(buildingShopDto));
    }
}
