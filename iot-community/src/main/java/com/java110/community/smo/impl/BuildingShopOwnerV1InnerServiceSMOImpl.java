package com.java110.community.smo.impl;

import com.java110.bean.dto.PageDto;
import com.java110.community.dao.IBuildingShopOwnerV1ServiceDao;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.shop.BuildingShopOwnerDto;
import com.java110.intf.community.IBuildingShopOwnerV1InnerServiceSMO;
import com.java110.po.shop.BuildingShopOwnerPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商铺业主关联内部服务实现
 */
@RestController
public class BuildingShopOwnerV1InnerServiceSMOImpl implements IBuildingShopOwnerV1InnerServiceSMO {

    @Autowired
    private IBuildingShopOwnerV1ServiceDao buildingShopOwnerV1ServiceDaoImpl;

    @Override
    public int saveBuildingShopOwner(@RequestBody BuildingShopOwnerPo buildingShopOwnerPo) {
        return buildingShopOwnerV1ServiceDaoImpl.saveBuildingShopOwnerInfo(BeanConvertUtil.beanCovertMap(buildingShopOwnerPo));
    }

    @Override
    public int deleteBuildingShopOwner(@RequestBody BuildingShopOwnerPo buildingShopOwnerPo) {
        buildingShopOwnerPo.setStatusCd("1");
        return buildingShopOwnerV1ServiceDaoImpl.updateBuildingShopOwnerInfo(BeanConvertUtil.beanCovertMap(buildingShopOwnerPo));
    }

    @Override
    public int updateBuildingShopOwner(@RequestBody BuildingShopOwnerPo buildingShopOwnerPo) {
        return buildingShopOwnerV1ServiceDaoImpl.updateBuildingShopOwnerInfo(BeanConvertUtil.beanCovertMap(buildingShopOwnerPo));
    }

    @Override
    public List<BuildingShopOwnerDto> queryBuildingShopOwners(@RequestBody BuildingShopOwnerDto buildingShopOwnerDto) {
        int page = buildingShopOwnerDto.getPage();
        if (page != PageDto.DEFAULT_PAGE) {
            buildingShopOwnerDto.setPage((page - 1) * buildingShopOwnerDto.getRow());
        }
        List<BuildingShopOwnerDto> buildingShopOwners = BeanConvertUtil.covertBeanList(
                buildingShopOwnerV1ServiceDaoImpl.getBuildingShopOwnerInfo(BeanConvertUtil.beanCovertMap(buildingShopOwnerDto)),
                BuildingShopOwnerDto.class);
        return buildingShopOwners;
    }

    @Override
    public int queryBuildingShopOwnersCount(@RequestBody BuildingShopOwnerDto buildingShopOwnerDto) {
        return buildingShopOwnerV1ServiceDaoImpl.queryBuildingShopOwnersCount(BeanConvertUtil.beanCovertMap(buildingShopOwnerDto));
    }
}
