package com.java110.community.dao;

import com.java110.core.exception.DAOException;

import java.util.List;
import java.util.Map;

/**
 * 商铺业主关联数据访问接口
 */
public interface IBuildingShopOwnerV1ServiceDao {

    /**
     * 保存商铺业主关联信息
     *
     * @param info 关联信息
     * @return 保存条数
     * @throws DAOException DAO异常
     */
    int saveBuildingShopOwnerInfo(Map info) throws DAOException;

    /**
     * 查询商铺业主关联信息
     *
     * @param info 关联信息
     * @return 关联列表
     * @throws DAOException DAO异常
     */
    List<Map> getBuildingShopOwnerInfo(Map info) throws DAOException;

    /**
     * 修改商铺业主关联信息
     *
     * @param info 修改信息
     * @return 修改条数
     * @throws DAOException DAO异常
     */
    int updateBuildingShopOwnerInfo(Map info) throws DAOException;

    /**
     * 查询商铺业主关联总数
     *
     * @param info 关联信息
     * @return 关联数量
     */
    int queryBuildingShopOwnersCount(Map info);
}
