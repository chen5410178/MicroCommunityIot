package com.java110.acct.smo.impl;

import com.java110.acct.dao.IInvoiceOrderItemV1ServiceDao;
import com.java110.bean.dto.PageDto;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.invoice.InvoiceOrderItemDto;
import com.java110.intf.acct.IInvoiceOrderItemV1InnerServiceSMO;
import com.java110.po.invoice.InvoiceOrderItemPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 开票明细服务实现（物联网内部服务）
 */
@RestController
public class InvoiceOrderItemV1InnerServiceSMOImpl implements IInvoiceOrderItemV1InnerServiceSMO {

    @Autowired
    private IInvoiceOrderItemV1ServiceDao invoiceOrderItemV1ServiceDaoImpl;

    @Override
    public int saveInvoiceOrderItem(@RequestBody InvoiceOrderItemPo invoiceOrderItemPo) {
        return invoiceOrderItemV1ServiceDaoImpl.saveInvoiceOrderItemInfo(BeanConvertUtil.beanCovertMap(invoiceOrderItemPo));
    }

    @Override
    public List<InvoiceOrderItemDto> queryInvoiceOrderItems(@RequestBody InvoiceOrderItemDto invoiceOrderItemDto) {
        int page = invoiceOrderItemDto.getPage();
        if (page != PageDto.DEFAULT_PAGE) {
            invoiceOrderItemDto.setPage((page - 1) * invoiceOrderItemDto.getRow());
        }
        List<Map> maps = invoiceOrderItemV1ServiceDaoImpl.getInvoiceOrderItemInfo(BeanConvertUtil.beanCovertMap(invoiceOrderItemDto));
        return BeanConvertUtil.covertBeanList(maps, InvoiceOrderItemDto.class);
    }

    @Override
    public int queryInvoiceOrderItemsCount(@RequestBody InvoiceOrderItemDto invoiceOrderItemDto) {
        return invoiceOrderItemV1ServiceDaoImpl.queryInvoiceOrderItemsCount(BeanConvertUtil.beanCovertMap(invoiceOrderItemDto));
    }
}
