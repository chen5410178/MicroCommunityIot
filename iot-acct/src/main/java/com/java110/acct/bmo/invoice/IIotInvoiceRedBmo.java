package com.java110.acct.bmo.invoice;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.exception.CmdException;

/**
 * 物联网发票红冲（只读商城 + 商城执行，物联网侧零落库）
 * 红冲列表：按小区（+手机号）取本地开票单号集合后转发商城 queryInvoiceRedsBmoImpl，商城未开启时静默返回空分页；
 * 发起红冲：校验开票单归属当前小区 → 转发商城 iotInvoiceRedBmoImpl（source=IOT），冲红单与冲红状态只存商城。
 */
public interface IIotInvoiceRedBmo {

    /**
     * 红冲列表（读商城）
     * 入参：communityId、phone(可空)、state(可空)、page、row
     * 出参：data（红冲列表）、records（总页数）、total（总条数）
     *
     * @param paramIn 查询条件
     * @return 分页结果
     */
    JSONObject queryInvoiceReds(JSONObject paramIn) throws CmdException;

    /**
     * 发起红冲（商城执行）
     * 入参：communityId、invoiceOrderId（物联网开票单号）、reason、remark
     * 出参：applyId、invoiceId、redId、state、redNo、failReason
     *
     * @param paramIn 红冲参数
     * @return 红冲结果
     */
    JSONObject saveInvoiceRed(JSONObject paramIn) throws CmdException;
}
