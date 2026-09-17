package com.java110.acct.bmo.invoice;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.exception.CmdException;

import java.util.List;
import java.util.Map;

/**
 * 商城开票数据只读查询（物联网侧零落库）
 * 状态以商城为权威源：开票记录列表用本 Bmo 按开票单号批量回读商城状态回填展示，物联网侧不维护状态机；
 * 抬头列表只读转发商城 listInvoiceHeadingsBmoImpl（任意手机号仅 PC 管理端使用）；
 * 商城未开启/未部署时状态回读静默返回空结果，不阻塞本地列表查询。
 */
public interface IInvoiceStateSyncBmo {

    /**
     * 按物联网开票单号批量回读商城开票状态（只读，不落库）
     *
     * @param invoiceOrderIds 物联网开票单号集合（= 商城 ext_apply_id）
     * @return 开票单号 → { state, invoiceNo, invoiceCode, pdfUrl, failReason }
     */
    Map<String, JSONObject> queryMallStates(List<String> invoiceOrderIds) throws CmdException;

    /**
     * 按手机号查询商城发票抬头列表（含默认抬头标识 isDefault）
     *
     * @param userPhone 抬头归属手机号
     * @param keyword   抬头名称/税号/联系电话模糊关键字（可空）
     * @param page      页码（从1开始）
     * @param row       每页条数
     * @return { data: 抬头列表, records: 总页数, total: 总条数 }
     */
    JSONObject queryHeadings(String userPhone, String keyword, int page, int row) throws CmdException;
}
