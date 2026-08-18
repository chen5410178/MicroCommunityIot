package com.java110.community.smo.impl;

import com.java110.bean.dto.PageDto;
import com.java110.community.dao.IShopLocationV1ServiceDao;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.shop.ShopLocationDto;
import com.java110.intf.community.IShopLocationV1InnerServiceSMO;
import com.java110.po.shop.ShopLocationPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商铺位置内部服务实现
 */
@RestController
public class ShopLocationV1InnerServiceSMOImpl implements IShopLocationV1InnerServiceSMO {

    @Autowired
    private IShopLocationV1ServiceDao shopLocationV1ServiceDaoImpl;

    @Override
    public int saveShopLocation(@RequestBody ShopLocationPo shopLocationPo) {
        return shopLocationV1ServiceDaoImpl.saveShopLocationInfo(BeanConvertUtil.beanCovertMap(shopLocationPo));
    }

    @Override
    public int updateShopLocation(@RequestBody ShopLocationPo shopLocationPo) {
        return shopLocationV1ServiceDaoImpl.updateShopLocationInfo(BeanConvertUtil.beanCovertMap(shopLocationPo));
    }

    @Override
    public int deleteShopLocation(@RequestBody ShopLocationPo shopLocationPo) {
        shopLocationPo.setStatusCd("1");
        return shopLocationV1ServiceDaoImpl.updateShopLocationInfo(BeanConvertUtil.beanCovertMap(shopLocationPo));
    }

    @Override
    public List<ShopLocationDto> queryShopLocations(@RequestBody ShopLocationDto shopLocationDto) {
        int page = shopLocationDto.getPage();
        if (page != PageDto.DEFAULT_PAGE) {
            shopLocationDto.setPage((page - 1) * shopLocationDto.getRow());
        }
        List<ShopLocationDto> shopLocations = BeanConvertUtil.covertBeanList(
                shopLocationV1ServiceDaoImpl.getShopLocationInfo(BeanConvertUtil.beanCovertMap(shopLocationDto)),
                ShopLocationDto.class);
        return shopLocations;
    }

    @Override
    public int queryShopLocationsCount(@RequestBody ShopLocationDto shopLocationDto) {
        return shopLocationV1ServiceDaoImpl.queryShopLocationsCount(BeanConvertUtil.beanCovertMap(shopLocationDto));
    }
}
