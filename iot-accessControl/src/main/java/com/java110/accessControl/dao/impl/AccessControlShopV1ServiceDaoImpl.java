package com.java110.accessControl.dao.impl;

import com.java110.accessControl.dao.IAccessControlShopV1ServiceDao;
import com.java110.core.db.dao.BaseServiceDao;
import com.java110.core.exception.DAOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 商铺授权门禁数据访问实现
 */
@Service("accessControlShopV1ServiceDaoImpl")
public class AccessControlShopV1ServiceDaoImpl extends BaseServiceDao implements IAccessControlShopV1ServiceDao {

    private static Logger logger = LoggerFactory.getLogger(AccessControlShopV1ServiceDaoImpl.class);

    /**
     * 保存商铺授权信息
     *
     * @param info bId 信息
     * @throws DAOException DAO异常
     */
    @Override
    public int saveAccessControlShopInfo(Map info) throws DAOException {
        logger.debug("保存 saveAccessControlShopInfo 入参 info : {}", info);
        return sqlSessionTemplate.insert("accessControlShopV1ServiceDaoImpl.saveAccessControlShopInfo", info);
    }

    /**
     * 查询商铺授权信息
     *
     * @param info bId 信息
     * @return List<Map>
     * @throws DAOException DAO异常
     */
    @Override
    public List<Map> getAccessControlShopInfo(Map info) throws DAOException {
        logger.debug("查询 getAccessControlShopInfo 入参 info : {}", info);
        return sqlSessionTemplate.selectList("accessControlShopV1ServiceDaoImpl.getAccessControlShopInfo", info);
    }

    /**
     * 修改商铺授权信息
     *
     * @param info 修改信息
     * @throws DAOException DAO异常
     */
    @Override
    public int updateAccessControlShopInfo(Map info) throws DAOException {
        logger.debug("修改 updateAccessControlShopInfo 入参 info : {}", info);
        return sqlSessionTemplate.update("accessControlShopV1ServiceDaoImpl.updateAccessControlShopInfo", info);
    }

    /**
     * 查询商铺授权数量
     *
     * @param info 商铺授权信息
     * @return 商铺授权数量
     */
    @Override
    public int queryAccessControlShopsCount(Map info) {
        logger.debug("查询 queryAccessControlShopsCount 入参 info : {}", info);
        List<Map> businessAccessControlShopInfos = sqlSessionTemplate.selectList("accessControlShopV1ServiceDaoImpl.queryAccessControlShopsCount", info);
        if (businessAccessControlShopInfos.size() < 1) {
            return 0;
        }
        return Integer.parseInt(businessAccessControlShopInfos.get(0).get("count").toString());
    }
}
