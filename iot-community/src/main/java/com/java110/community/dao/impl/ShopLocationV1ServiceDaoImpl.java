package com.java110.community.dao.impl;

import com.java110.community.dao.IShopLocationV1ServiceDao;
import com.java110.core.db.dao.BaseServiceDao;
import com.java110.core.exception.DAOException;
import com.java110.core.factory.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 商铺位置数据访问实现
 */
@Service("shopLocationV1ServiceDaoImpl")
public class ShopLocationV1ServiceDaoImpl extends BaseServiceDao implements IShopLocationV1ServiceDao {

    private static Logger logger = LoggerFactory.getLogger(ShopLocationV1ServiceDaoImpl.class);

    @Override
    public int saveShopLocationInfo(Map info) throws DAOException {
        logger.debug("保存 saveShopLocationInfo 入参 info : {}", info);
        return sqlSessionTemplate.insert("shopLocationV1ServiceDaoImpl.saveShopLocationInfo", info);
    }

    @Override
    public List<Map> getShopLocationInfo(Map info) throws DAOException {
        logger.debug("查询 getShopLocationInfo 入参 info : {}", info);
        return sqlSessionTemplate.selectList("shopLocationV1ServiceDaoImpl.getShopLocationInfo", info);
    }

    @Override
    public int updateShopLocationInfo(Map info) throws DAOException {
        logger.debug("修改 updateShopLocationInfo 入参 info : {}", info);
        return sqlSessionTemplate.update("shopLocationV1ServiceDaoImpl.updateShopLocationInfo", info);
    }

    @Override
    public int queryShopLocationsCount(Map info) {
        logger.debug("查询 queryShopLocationsCount 入参 info : {}", info);
        List<Map> infos = sqlSessionTemplate.selectList("shopLocationV1ServiceDaoImpl.queryShopLocationsCount", info);
        if (infos.size() < 1) {
            return 0;
        }
        return Integer.parseInt(infos.get(0).get("count").toString());
    }
}
