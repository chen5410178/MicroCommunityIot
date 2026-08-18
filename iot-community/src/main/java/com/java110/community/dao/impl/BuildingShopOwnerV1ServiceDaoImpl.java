package com.java110.community.dao.impl;

import com.java110.community.dao.IBuildingShopOwnerV1ServiceDao;
import com.java110.core.db.dao.BaseServiceDao;
import com.java110.core.exception.DAOException;
import com.java110.core.factory.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 商铺业主关联数据访问实现
 */
@Service("buildingShopOwnerV1ServiceDaoImpl")
public class BuildingShopOwnerV1ServiceDaoImpl extends BaseServiceDao implements IBuildingShopOwnerV1ServiceDao {

    private static Logger logger = LoggerFactory.getLogger(BuildingShopOwnerV1ServiceDaoImpl.class);

    @Override
    public int saveBuildingShopOwnerInfo(Map info) throws DAOException {
        logger.debug("保存 saveBuildingShopOwnerInfo 入参 info : {}", info);
        return sqlSessionTemplate.insert("buildingShopOwnerV1ServiceDaoImpl.saveBuildingShopOwnerInfo", info);
    }

    @Override
    public List<Map> getBuildingShopOwnerInfo(Map info) throws DAOException {
        logger.debug("查询 getBuildingShopOwnerInfo 入参 info : {}", info);
        return sqlSessionTemplate.selectList("buildingShopOwnerV1ServiceDaoImpl.getBuildingShopOwnerInfo", info);
    }

    @Override
    public int updateBuildingShopOwnerInfo(Map info) throws DAOException {
        logger.debug("修改 updateBuildingShopOwnerInfo 入参 info : {}", info);
        return sqlSessionTemplate.update("buildingShopOwnerV1ServiceDaoImpl.updateBuildingShopOwnerInfo", info);
    }

    @Override
    public int queryBuildingShopOwnersCount(Map info) {
        logger.debug("查询 queryBuildingShopOwnersCount 入参 info : {}", info);
        List<Map> infos = sqlSessionTemplate.selectList("buildingShopOwnerV1ServiceDaoImpl.queryBuildingShopOwnersCount", info);
        if (infos.size() < 1) {
            return 0;
        }
        return Integer.parseInt(infos.get(0).get("count").toString());
    }
}
