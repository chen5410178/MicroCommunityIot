package com.java110.community.dao;

import com.java110.core.exception.DAOException;

import java.util.List;
import java.util.Map;

/**
 * 商铺位置数据访问接口
 */
public interface IShopLocationV1ServiceDao {

    /**
     * 保存商铺位置信息
     *
     * @param info 位置信息
     * @return 保存条数
     * @throws DAOException DAO异常
     */
    int saveShopLocationInfo(Map info) throws DAOException;

    /**
     * 查询商铺位置信息
     *
     * @param info 位置信息
     * @return 位置列表
     * @throws DAOException DAO异常
     */
    List<Map> getShopLocationInfo(Map info) throws DAOException;

    /**
     * 修改商铺位置信息
     *
     * @param info 修改信息
     * @return 修改条数
     * @throws DAOException DAO异常
     */
    int updateShopLocationInfo(Map info) throws DAOException;

    /**
     * 查询商铺位置总数
     *
     * @param info 位置信息
     * @return 位置数量
     */
    int queryShopLocationsCount(Map info);
}
