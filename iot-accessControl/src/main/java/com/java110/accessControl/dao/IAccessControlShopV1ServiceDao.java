package com.java110.accessControl.dao;

import com.java110.core.exception.DAOException;

import java.util.List;
import java.util.Map;

/**
 * 商铺授权门禁数据访问接口
 */
public interface IAccessControlShopV1ServiceDao {

    /**
     * 保存商铺授权信息
     *
     * @param info 信息
     * @throws DAOException DAO异常
     */
    int saveAccessControlShopInfo(Map info) throws DAOException;

    /**
     * 查询商铺授权信息
     *
     * @param info 信息
     * @return 商铺授权信息
     * @throws DAOException DAO异常
     */
    List<Map> getAccessControlShopInfo(Map info) throws DAOException;

    /**
     * 修改商铺授权信息
     *
     * @param info 修改信息
     * @throws DAOException DAO异常
     */
    int updateAccessControlShopInfo(Map info) throws DAOException;

    /**
     * 查询商铺授权总数
     *
     * @param info 信息
     * @return 商铺授权数量
     */
    int queryAccessControlShopsCount(Map info);
}
