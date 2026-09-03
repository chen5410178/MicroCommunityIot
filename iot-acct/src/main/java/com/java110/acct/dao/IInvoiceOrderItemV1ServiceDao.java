package com.java110.acct.dao;

import com.java110.core.exception.DAOException;

import java.util.List;
import java.util.Map;

/**
 * 开票明细数据操作接口
 */
public interface IInvoiceOrderItemV1ServiceDao {

    /**
     * 保存开票明细信息
     *
     * @param info 开票明细信息
     * @return 影响行数
     * @throws DAOException DAO异常
     */
    int saveInvoiceOrderItemInfo(Map info) throws DAOException;

    /**
     * 查询开票明细信息
     *
     * @param info 开票明细信息
     * @return 开票明细信息
     * @throws DAOException DAO异常
     */
    List<Map> getInvoiceOrderItemInfo(Map info) throws DAOException;

    /**
     * 查询开票明细数量
     *
     * @param info 开票明细信息
     * @return 开票明细数量
     */
    int queryInvoiceOrderItemsCount(Map info);
}
