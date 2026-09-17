package com.java110.acct.bmo.invoice.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.acct.bmo.invoice.IInvoiceStateSyncBmo;
import com.java110.acct.mall.ISendMall;
import com.java110.bean.ResultVo;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商城开票数据只读查询实现
 * 只读转发商城开放接口，不落任何物联网库表；商城开关未开启或调用异常时状态回读降级为空（列表仍可展示本地记录）
 */
@Service
public class InvoiceStateSyncBmoImpl implements IInvoiceStateSyncBmo {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceStateSyncBmoImpl.class);

    private static final String MALL_URL = "/mall/api/common.openCommonApi";
    private static final String MALL_API_STATUS_BATCH = "queryInvoiceStatusBatchBmoImpl";
    private static final String MALL_API_HEADINGS = "listInvoiceHeadingsBmoImpl";

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_ROW = 10;
    private static final String FAIL_REASON_MALL_CLOSED = "商城系统未开启或未部署，无法查询发票抬头";

    @Autowired
    private ISendMall sendMallImpl;

    @Override
    public Map<String, JSONObject> queryMallStates(List<String> invoiceOrderIds) throws CmdException {
        Map<String, JSONObject> states = new HashMap<>();
        if (ListUtil.isNull(invoiceOrderIds)) {
            return states;
        }
        JSONObject paramIn = new JSONObject();
        paramIn.put("mallApiCode", MALL_API_STATUS_BATCH);
        paramIn.put("applyIds", invoiceOrderIds);

        JSONObject mallBody = sendMallImpl.postForBody(MALL_URL, paramIn);
        if (mallBody == null) {
            logger.warn("商城系统未开启或未部署，开票状态回读降级为空，applyIds: {}", invoiceOrderIds);
            return states;
        }
        if (mallBody.getIntValue("code") != ResultVo.CODE_OK) {
            logger.warn("商城开票状态回读失败，降级为空，msg: {}", mallBody.getString("msg"));
            return states;
        }
        JSONObject data = mallBody.getJSONObject("data");
        if (data == null) {
            return states;
        }
        JSONArray invoices = data.getJSONArray("invoices");
        if (invoices == null) {
            return states;
        }
        for (int i = 0; i < invoices.size(); i++) {
            JSONObject invoice = invoices.getJSONObject(i);
            if (invoice == null || StringUtil.isNullOrNone(invoice.getString("applyId"))) {
                continue;
            }
            states.put(invoice.getString("applyId"), invoice);
        }
        return states;
    }

    @Override
    public JSONObject queryHeadings(String userPhone, String keyword, int page, int row) throws CmdException {
        JSONObject paramIn = new JSONObject();
        paramIn.put("mallApiCode", MALL_API_HEADINGS);
        paramIn.put("userPhone", userPhone);
        paramIn.put("page", page < 1 ? DEFAULT_PAGE : page);
        paramIn.put("row", row < 1 ? DEFAULT_ROW : row);
        if (!StringUtil.isNullOrNone(keyword)) {
            paramIn.put("keyword", keyword);
        }

        JSONObject mallBody = sendMallImpl.postForBody(MALL_URL, paramIn);
        if (mallBody == null) {
            throw new CmdException(FAIL_REASON_MALL_CLOSED);
        }
        if (mallBody.getIntValue("code") != ResultVo.CODE_OK) {
            throw new CmdException(mallBody.getString("msg"));
        }

        JSONObject result = new JSONObject();
        JSONArray headings = mallBody.getJSONArray("data");
        result.put("data", headings == null ? new JSONArray() : headings);
        result.put("records", mallBody.getIntValue("records"));
        result.put("total", mallBody.getIntValue("total"));
        return result;
    }
}
