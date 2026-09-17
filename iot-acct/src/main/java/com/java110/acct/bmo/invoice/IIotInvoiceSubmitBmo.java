package com.java110.acct.bmo.invoice;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.exception.CmdException;
import com.java110.dto.chargeMachine.ChargeMachineOrderDto;

import java.util.List;

/**
 * 物联网提交开票编排（PC 管理端 invoice.saveInvoiceOrder 与手机端 invoice.appOpenInvoice 共用）
 * 与物业一致：先调商城（抬头随报文 upsert）→ 商城返回开票单号才落物联网 invoice_order/invoice_order_item；
 * 商城未建单则零落库，返回 state=F + failReason，失败原因原样直达前端；
 * 审核与开票进度以商城为权威源，物联网侧不维护状态机。
 */
public interface IIotInvoiceSubmitBmo {

    /**
     * 提交开票（两端共用）
     * 入参：communityId、userId(归属用户)、phone(归属手机号)、bizCategory、orderIds[]、
     * headingType、headingName、taxNo、buyerAddress、buyerBankNo、email、invoiceType、remark
     * 出参：invoiceOrderId、invoiceId、state、invoiceNo、invoiceCode、pdfUrl、failReason
     * （state=F 表示商城未建单，物联网侧零落库）
     *
     * @param paramIn 提交参数
     * @return 开票结果
     */
    JSONObject submitInvoice(JSONObject paramIn) throws CmdException;

    /**
     * 失败单重新提交：以本地开票单号作为商城幂等键（ext_apply_id）重开，成功回写发票号/PDF
     *
     * @param communityId    小区ID
     * @param invoiceOrderId 物联网开票单号
     * @return 开票结果
     */
    JSONObject reSubmitInvoice(String communityId, String invoiceOrderId) throws CmdException;

    /**
     * 查询未开票充电订单（已完成、实付金额>0、未在本地建过有效开票单）
     *
     * @param communityId 小区ID
     * @param phone       归属手机号
     * @return 未开票订单列表
     */
    List<ChargeMachineOrderDto> queryUnInvoicedOrders(String communityId, String phone) throws CmdException;
}
