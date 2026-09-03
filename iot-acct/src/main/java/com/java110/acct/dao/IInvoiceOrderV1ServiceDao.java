package com.java110.acct.dao;

import com.java110.core.exception.DAOException;

import java.util.List;
import java.util.Map;

/**
 * 开票订单数据操作接口
 */
public interface IInvoiceOrderV1ServiceDao {

    /**
     * 保存开票订单信息
     *
     * @param info 开票订单信息
     * @return 影响行数
     * @throws DAOException DAO异常
     */
    int saveInvoiceOrderInfo(Map info) throws DAOException;

    /**
     * 查询开票订单信息
     *
     * @param info 开票订单信息
     * @return 开票订单信息
     * @throws DAOException DAO异常
     */
    List<Map> getInvoiceOrderInfo(Map info) throws DAOException;

    /**
     * 修改开票订单信息
     *
     * @param info 修改信息
     * @return 影响行数
     * @throws DAOException DAO异常
     */
    int updateInvoiceOrderInfo(Map info) throws DAOException;

    /**
     * 查询开票订单数量
     *
     * @param info 开票订单信息
     * @return 开票订单数量
     */
    int queryInvoiceOrdersCount(Map info);
}
