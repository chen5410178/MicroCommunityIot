package com.java110.community.dao.impl;

import com.java110.community.dao.IBuildingShopV1ServiceDao;
import com.java110.core.db.dao.BaseServiceDao;
import com.java110.core.exception.DAOException;
import com.java110.core.factory.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 商铺数据访问实现
 */
@Service("buildingShopV1ServiceDaoImpl")
public class BuildingShopV1ServiceDaoImpl extends BaseServiceDao implements IBuildingShopV1ServiceDao {

    private static Logger logger = LoggerFactory.getLogger(BuildingShopV1ServiceDaoImpl.class);

    @Override
    public int saveBuildingShopInfo(Map info) throws DAOException {
        logger.debug("保存 saveBuildingShopInfo 入参 info : {}", info);
        return sqlSessionTemplate.insert("buildingShopV1ServiceDaoImpl.saveBuildingShopInfo", info);
    }

    @Override
    public List<Map> getBuildingShopInfo(Map info) throws DAOException {
        logger.debug("查询 getBuildingShopInfo 入参 info : {}", info);
        return sqlSessionTemplate.selectList("buildingShopV1ServiceDaoImpl.getBuildingShopInfo", info);
    }

    @Override
    public int updateBuildingShopInfo(Map info) throws DAOException {
        logger.debug("修改 updateBuildingShopInfo 入参 info : {}", info);
        return sqlSessionTemplate.update("buildingShopV1ServiceDaoImpl.updateBuildingShopInfo", info);
    }

    @Override
    public int queryBuildingShopsCount(Map info) {
        logger.debug("查询 queryBuildingShopsCount 入参 info : {}", info);
        List<Map> infos = sqlSessionTemplate.selectList("buildingShopV1ServiceDaoImpl.queryBuildingShopsCount", info);
        if (infos.size() < 1) {
            return 0;
        }
        return Integer.parseInt(infos.get(0).get("count").toString());
    }
}
