package com.java110.intf.acct;

import com.java110.dto.invoice.InvoiceOrderItemDto;
import com.java110.intf.FeignConfiguration;
import com.java110.po.invoice.InvoiceOrderItemPo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 开票明细服务接口（物联网内部服务）
 */
@FeignClient(name = "iot-acct", configuration = {FeignConfiguration.class})
@RequestMapping("/invoiceOrderItemV1Api")
public interface IInvoiceOrderItemV1InnerServiceSMO {

    @RequestMapping(value = "/saveInvoiceOrderItem", method = RequestMethod.POST)
    int saveInvoiceOrderItem(@RequestBody InvoiceOrderItemPo invoiceOrderItemPo);

    @RequestMapping(value = "/queryInvoiceOrderItems", method = RequestMethod.POST)
    List<InvoiceOrderItemDto> queryInvoiceOrderItems(@RequestBody InvoiceOrderItemDto invoiceOrderItemDto);

    @RequestMapping(value = "/queryInvoiceOrderItemsCount", method = RequestMethod.POST)
    int queryInvoiceOrderItemsCount(@RequestBody InvoiceOrderItemDto invoiceOrderItemDto);
}
