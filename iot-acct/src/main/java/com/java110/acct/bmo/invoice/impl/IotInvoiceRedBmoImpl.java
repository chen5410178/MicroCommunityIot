package com.java110.acct.bmo.invoice.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.acct.bmo.invoice.IIotInvoiceRedBmo;
import com.java110.acct.mall.ISendMall;
import com.java110.bean.ResultVo;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.invoice.InvoiceOrderDto;
import com.java110.intf.acct.IInvoiceOrderV1InnerServiceSMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物联网发票红冲实现（商城为冲红单与冲红状态唯一权威源）
 * 列表：本地开票单（小区 + 手机号）→ applyIds → 商城 queryInvoiceRedsBmoImpl；
 * 冲红单量级小，一次取回后在物联网侧按状态过滤并分页，保证筛选条件与 total 一致；
 * 执行：转发商城 iotInvoiceRedBmoImpl（商城校验原单已开票、幂等、落冲红单并执行红字开票）。
 */
@Service
public class IotInvoiceRedBmoImpl implements IIotInvoiceRedBmo {

    private static final Logger logger = LoggerFactory.getLogger(IotInvoiceRedBmoImpl.class);

    private static final String MALL_URL = "/mall/api/common.openCommonApi";
    private static final String MALL_API_QUERY_REDS = "queryInvoiceRedsBmoImpl";
    private static final String MALL_API_SAVE_RED = "iotInvoiceRedBmoImpl";
    private static final String ORG_TYPE_COMMUNITY = "C";
    private static final int RED_QUERY_MAX_ROW = 500;
    private static final String FAIL_REASON_MALL_CLOSED = "商城系统未开启或未部署，无法发起红冲";

    @Autowired
    private IInvoiceOrderV1InnerServiceSMO invoiceOrderV1InnerServiceSMOImpl;

    @Autowired
    private ISendMall sendMallImpl;

    @Override
    public JSONObject queryInvoiceReds(JSONObject paramIn) throws CmdException {
        String communityId = paramIn.getString("communityId");
        String phone = paramIn.getString("phone");
        String state = paramIn.getString("state");
        int page = paramIn.getIntValue("page") < 1 ? 1 : paramIn.getIntValue("page");
        int row = paramIn.getIntValue("row") < 1 ? 10 : paramIn.getIntValue("row");

        // ① 本地开票单（小区 + 手机号）→ 商城 applyIds，并记录归属手机号供列表展示
        List<InvoiceOrderDto> invoiceOrders = queryInvoiceOrders(communityId, phone);
        if (ListUtil.isNull(invoiceOrders)) {
            return emptyPage(page, row);
        }
        List<String> applyIds = new ArrayList<>();
        Map<String, String> phoneByApplyId = new HashMap<>();
        for (InvoiceOrderDto invoiceOrder : invoiceOrders) {
            applyIds.add(invoiceOrder.getInvoiceOrderId());
            phoneByApplyId.put(invoiceOrder.getInvoiceOrderId(), invoiceOrder.getPhone());
        }

        // ② 商城读取红冲单（商城未开启/异常时静默返回空分页）
        JSONArray reds = queryMallReds(applyIds);
        if (reds.isEmpty()) {
            return emptyPage(page, row);
        }

        // ③ 状态过滤 + 归属手机号回填 + 分页
        List<JSONObject> matchedReds = new ArrayList<>();
        for (int i = 0; i < reds.size(); i++) {
            JSONObject red = reds.getJSONObject(i);
            if (red == null) {
                continue;
            }
            if (!StringUtil.isNullOrNone(state) && !state.equals(red.getString("state"))) {
                continue;
            }
            red.put("phone", phoneByApplyId.get(red.getString("applyId")));
            matchedReds.add(red);
        }

        int total = matchedReds.size();
        int fromIndex = (page - 1) * row;
        JSONArray pageReds = new JSONArray();
        if (fromIndex < total) {
            List<JSONObject> pageList = matchedReds.subList(fromIndex, Math.min(fromIndex + row, total));
            pageReds.addAll(pageList);
        }

        JSONObject result = new JSONObject();
        result.put("data", pageReds);
        result.put("records", (int) Math.ceil((double) total / (double) row));
        result.put("total", total);
        return result;
    }

    @Override
    public JSONObject saveInvoiceRed(JSONObject paramIn) throws CmdException {
        String communityId = paramIn.getString("communityId");
        String invoiceOrderId = paramIn.getString("invoiceOrderId");

        // ① 校验开票单归属当前小区
        InvoiceOrderDto invoiceOrder = getInvoiceOrder(communityId, invoiceOrderId);
        if (invoiceOrder == null) {
            throw new CmdException("开票单不存在");
        }

        // ② 转发商城执行红冲（重复提交由商城按开票单幂等返回既有冲红单）
        JSONObject mallParam = new JSONObject();
        mallParam.put("mallApiCode", MALL_API_SAVE_RED);
        mallParam.put("orgId", communityId);
        mallParam.put("orgType", ORG_TYPE_COMMUNITY);
        mallParam.put("applyId", invoiceOrderId);
        mallParam.put("reason", paramIn.getString("reason"));
        mallParam.put("remark", paramIn.getString("remark"));

        JSONObject mallBody = sendMallImpl.postForBody(MALL_URL, mallParam);
        if (mallBody == null) {
            throw new CmdException(FAIL_REASON_MALL_CLOSED);
        }
        if (mallBody.getIntValue("code") != ResultVo.CODE_OK) {
            throw new CmdException(mallBody.getString("msg"));
        }
        JSONObject data = mallBody.getJSONObject("data");
        if (data == null) {
            throw new CmdException("商城未返回红冲结果");
        }
        return data;
    }

    /**
     * 本地开票单查询（小区 + 手机号，手机号为空则按小区全部）
     */
    private List<InvoiceOrderDto> queryInvoiceOrders(String communityId, String phone) {
        InvoiceOrderDto invoiceOrderQuery = new InvoiceOrderDto();
        invoiceOrderQuery.setCommunityId(communityId);
        if (!StringUtil.isNullOrNone(phone)) {
            invoiceOrderQuery.setPhone(phone);
        }
        invoiceOrderQuery.setStatusCd("0");
        invoiceOrderQuery.setPage(-1);
        List<InvoiceOrderDto> invoiceOrders = invoiceOrderV1InnerServiceSMOImpl.queryInvoiceOrders(invoiceOrderQuery);
        if (ListUtil.isNull(invoiceOrders)) {
            return new ArrayList<>();
        }
        return invoiceOrders;
    }

    private InvoiceOrderDto getInvoiceOrder(String communityId, String invoiceOrderId) {
        InvoiceOrderDto invoiceOrderQuery = new InvoiceOrderDto();
        invoiceOrderQuery.setInvoiceOrderId(invoiceOrderId);
        invoiceOrderQuery.setCommunityId(communityId);
        invoiceOrderQuery.setStatusCd("0");
        invoiceOrderQuery.setPage(-1);
        List<InvoiceOrderDto> invoiceOrders = invoiceOrderV1InnerServiceSMOImpl.queryInvoiceOrders(invoiceOrderQuery);
        if (ListUtil.isNull(invoiceOrders)) {
            return null;
        }
        return invoiceOrders.get(0);
    }

    /**
     * 商城按 applyIds（物联网开票单号）读取红冲单
     */
    private JSONArray queryMallReds(List<String> applyIds) {
        JSONObject mallParam = new JSONObject();
        mallParam.put("mallApiCode", MALL_API_QUERY_REDS);
        mallParam.put("applyIds", applyIds);
        mallParam.put("page", 1);
        mallParam.put("row", RED_QUERY_MAX_ROW);

        JSONObject mallBody = sendMallImpl.postForBody(MALL_URL, mallParam);
        if (mallBody == null) {
            logger.warn("商城系统未开启或未部署，红冲列表返回空分页，applyIds: {}", applyIds.size());
            return new JSONArray();
        }
        if (mallBody.getIntValue("code") != ResultVo.CODE_OK) {
            logger.warn("商城红冲列表查询失败，返回空分页，msg: {}", mallBody.getString("msg"));
            return new JSONArray();
        }
        JSONObject data = mallBody.getJSONObject("data");
        if (data == null || data.getJSONArray("reds") == null) {
            return new JSONArray();
        }
        return data.getJSONArray("reds");
    }

    private JSONObject emptyPage(int page, int row) {
        JSONObject result = new JSONObject();
        result.put("data", new JSONArray());
        result.put("records", 0);
        result.put("total", 0);
        return result;
    }
}
