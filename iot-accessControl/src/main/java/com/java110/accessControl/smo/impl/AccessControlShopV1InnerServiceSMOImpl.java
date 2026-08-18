package com.java110.accessControl.smo.impl;

import com.java110.accessControl.dao.IAccessControlShopV1ServiceDao;
import com.java110.bean.dto.PageDto;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.accessControlShop.AccessControlShopDto;
import com.java110.intf.accessControl.IAccessControlShopV1InnerServiceSMO;
import com.java110.po.accessControlShop.AccessControlShopPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商铺授权门禁内部服务实现
 */
@RestController
public class AccessControlShopV1InnerServiceSMOImpl implements IAccessControlShopV1InnerServiceSMO {

    @Autowired
    private IAccessControlShopV1ServiceDao accessControlShopV1ServiceDaoImpl;

    @Override
    public int saveAccessControlShop(@RequestBody AccessControlShopPo accessControlShopPo) {
        return accessControlShopV1ServiceDaoImpl.saveAccessControlShopInfo(BeanConvertUtil.beanCovertMap(accessControlShopPo));
    }

    @Override
    public int updateAccessControlShop(@RequestBody AccessControlShopPo accessControlShopPo) {
        return accessControlShopV1ServiceDaoImpl.updateAccessControlShopInfo(BeanConvertUtil.beanCovertMap(accessControlShopPo));
    }

    @Override
    public int deleteAccessControlShop(@RequestBody AccessControlShopPo accessControlShopPo) {
        accessControlShopPo.setStatusCd("1");
        return accessControlShopV1ServiceDaoImpl.updateAccessControlShopInfo(BeanConvertUtil.beanCovertMap(accessControlShopPo));
    }

    @Override
    public List<AccessControlShopDto> queryAccessControlShops(@RequestBody AccessControlShopDto accessControlShopDto) {
        int page = accessControlShopDto.getPage();
        if (page != PageDto.DEFAULT_PAGE) {
            accessControlShopDto.setPage((page - 1) * accessControlShopDto.getRow());
        }
        return BeanConvertUtil.covertBeanList(
                accessControlShopV1ServiceDaoImpl.getAccessControlShopInfo(BeanConvertUtil.beanCovertMap(accessControlShopDto)),
                AccessControlShopDto.class);
    }

    @Override
    public int queryAccessControlShopsCount(@RequestBody AccessControlShopDto accessControlShopDto) {
        return accessControlShopV1ServiceDaoImpl.queryAccessControlShopsCount(BeanConvertUtil.beanCovertMap(accessControlShopDto));
    }
}
