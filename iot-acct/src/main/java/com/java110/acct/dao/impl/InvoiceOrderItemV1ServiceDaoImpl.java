package com.java110.acct.dao.impl;

import com.java110.acct.dao.IInvoiceOrderItemV1ServiceDao;
import com.java110.core.db.dao.BaseServiceDao;
import com.java110.core.exception.DAOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 开票明细数据操作实现
 */
@Service("invoiceOrderItemV1ServiceDaoImpl")
public class InvoiceOrderItemV1ServiceDaoImpl extends BaseServiceDao implements IInvoiceOrderItemV1ServiceDao {

    private static Logger logger = LoggerFactory.getLogger(InvoiceOrderItemV1ServiceDaoImpl.class);

    @Override
    public int saveInvoiceOrderItemInfo(Map info) throws DAOException {
        logger.debug("保存 saveInvoiceOrderItemInfo 入参 info : {}", info);
        return sqlSessionTemplate.insert("invoiceOrderItemV1ServiceDaoImpl.saveInvoiceOrderItemInfo", info);
    }

    @Override
    public List<Map> getInvoiceOrderItemInfo(Map info) throws DAOException {
        logger.debug("查询 getInvoiceOrderItemInfo 入参 info : {}", info);
        return sqlSessionTemplate.selectList("invoiceOrderItemV1ServiceDaoImpl.getInvoiceOrderItemInfo", info);
    }

    @Override
    public int queryInvoiceOrderItemsCount(Map info) {
        logger.debug("查询 queryInvoiceOrderItemsCount 入参 info : {}", info);
        List<Map> infos = sqlSessionTemplate.selectList("invoiceOrderItemV1ServiceDaoImpl.queryInvoiceOrderItemsCount", info);
        if (infos.size() < 1) {
            return 0;
        }
        return Integer.parseInt(infos.get(0).get("count").toString());
    }
}
