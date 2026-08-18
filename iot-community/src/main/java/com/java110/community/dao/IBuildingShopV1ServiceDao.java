package com.java110.community.dao;

import com.java110.core.exception.DAOException;

import java.util.List;
import java.util.Map;

/**
 * 商铺数据访问接口
 */
public interface IBuildingShopV1ServiceDao {

    /**
     * 保存商铺信息
     *
     * @param info 商铺信息
     * @return 保存条数
     * @throws DAOException DAO异常
     */
    int saveBuildingShopInfo(Map info) throws DAOException;

    /**
     * 查询商铺信息
     *
     * @param info 商铺信息
     * @return 商铺列表
     * @throws DAOException DAO异常
     */
    List<Map> getBuildingShopInfo(Map info) throws DAOException;

    /**
     * 修改商铺信息
     *
     * @param info 修改信息
     * @return 修改条数
     * @throws DAOException DAO异常
     */
    int updateBuildingShopInfo(Map info) throws DAOException;

    /**
     * 查询商铺总数
     *
     * @param info 商铺信息
     * @return 商铺数量
     */
    int queryBuildingShopsCount(Map info);
}
