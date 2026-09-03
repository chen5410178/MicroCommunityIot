package com.java110.acct.dao.impl;

import com.java110.acct.dao.IInvoiceOrderV1ServiceDao;
import com.java110.core.db.dao.BaseServiceDao;
import com.java110.core.exception.DAOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 开票订单数据操作实现
 */
@Service("invoiceOrderV1ServiceDaoImpl")
public class InvoiceOrderV1ServiceDaoImpl extends BaseServiceDao implements IInvoiceOrderV1ServiceDao {

    private static Logger logger = LoggerFactory.getLogger(InvoiceOrderV1ServiceDaoImpl.class);

    @Override
    public int saveInvoiceOrderInfo(Map info) throws DAOException {
        logger.debug("保存 saveInvoiceOrderInfo 入参 info : {}", info);
        return sqlSessionTemplate.insert("invoiceOrderV1ServiceDaoImpl.saveInvoiceOrderInfo", info);
    }

    @Override
    public List<Map> getInvoiceOrderInfo(Map info) throws DAOException {
        logger.debug("查询 getInvoiceOrderInfo 入参 info : {}", info);
        return sqlSessionTemplate.selectList("invoiceOrderV1ServiceDaoImpl.getInvoiceOrderInfo", info);
    }

    @Override
    public int updateInvoiceOrderInfo(Map info) throws DAOException {
        logger.debug("修改 updateInvoiceOrderInfo 入参 info : {}", info);
        return sqlSessionTemplate.update("invoiceOrderV1ServiceDaoImpl.updateInvoiceOrderInfo", info);
    }

    @Override
    public int queryInvoiceOrdersCount(Map info) {
        logger.debug("查询 queryInvoiceOrdersCount 入参 info : {}", info);
        List<Map> infos = sqlSessionTemplate.selectList("invoiceOrderV1ServiceDaoImpl.queryInvoiceOrdersCount", info);
        if (infos.size() < 1) {
            return 0;
        }
        return Integer.parseInt(infos.get(0).get("count").toString());
    }
}
