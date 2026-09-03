package com.java110.intf.acct;

import com.java110.dto.invoice.InvoiceOrderDto;
import com.java110.intf.FeignConfiguration;
import com.java110.po.invoice.InvoiceOrderPo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 开票订单服务接口（物联网内部服务）
 */
@FeignClient(name = "iot-acct", configuration = {FeignConfiguration.class})
@RequestMapping("/invoiceOrderV1Api")
public interface IInvoiceOrderV1InnerServiceSMO {

    @RequestMapping(value = "/saveInvoiceOrder", method = RequestMethod.POST)
    int saveInvoiceOrder(@RequestBody InvoiceOrderPo invoiceOrderPo);

    @RequestMapping(value = "/updateInvoiceOrder", method = RequestMethod.POST)
    int updateInvoiceOrder(@RequestBody InvoiceOrderPo invoiceOrderPo);

    @RequestMapping(value = "/queryInvoiceOrders", method = RequestMethod.POST)
    List<InvoiceOrderDto> queryInvoiceOrders(@RequestBody InvoiceOrderDto invoiceOrderDto);

    @RequestMapping(value = "/queryInvoiceOrdersCount", method = RequestMethod.POST)
    int queryInvoiceOrdersCount(@RequestBody InvoiceOrderDto invoiceOrderDto);
}
