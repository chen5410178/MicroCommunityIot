package com.java110.acct.smo.impl;

import com.java110.acct.dao.IInvoiceOrderV1ServiceDao;
import com.java110.bean.dto.PageDto;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.invoice.InvoiceOrderDto;
import com.java110.intf.acct.IInvoiceOrderV1InnerServiceSMO;
import com.java110.po.invoice.InvoiceOrderPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 开票订单服务实现（物联网内部服务）
 */
@RestController
public class InvoiceOrderV1InnerServiceSMOImpl implements IInvoiceOrderV1InnerServiceSMO {

    @Autowired
    private IInvoiceOrderV1ServiceDao invoiceOrderV1ServiceDaoImpl;

    @Override
    public int saveInvoiceOrder(@RequestBody InvoiceOrderPo invoiceOrderPo) {
        return invoiceOrderV1ServiceDaoImpl.saveInvoiceOrderInfo(BeanConvertUtil.beanCovertMap(invoiceOrderPo));
    }

    @Override
    public int updateInvoiceOrder(@RequestBody InvoiceOrderPo invoiceOrderPo) {
        return invoiceOrderV1ServiceDaoImpl.updateInvoiceOrderInfo(BeanConvertUtil.beanCovertMap(invoiceOrderPo));
    }

    @Override
    public List<InvoiceOrderDto> queryInvoiceOrders(@RequestBody InvoiceOrderDto invoiceOrderDto) {
        int page = invoiceOrderDto.getPage();
        if (page != PageDto.DEFAULT_PAGE) {
            invoiceOrderDto.setPage((page - 1) * invoiceOrderDto.getRow());
        }
        List<Map> maps = invoiceOrderV1ServiceDaoImpl.getInvoiceOrderInfo(BeanConvertUtil.beanCovertMap(invoiceOrderDto));
        return BeanConvertUtil.covertBeanList(maps, InvoiceOrderDto.class);
    }

    @Override
    public int queryInvoiceOrdersCount(@RequestBody InvoiceOrderDto invoiceOrderDto) {
        return invoiceOrderV1ServiceDaoImpl.queryInvoiceOrdersCount(BeanConvertUtil.beanCovertMap(invoiceOrderDto));
    }
}
