package com.java110.core.context;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;
import java.util.Map;

/**
 * 数据流上下文
 * Created by wuxw on 2018/5/18.
 */
public abstract class AbstractCmdDataFlowContext implements ICmdDataFlowContext {


    private String dataFlowId;

    /**
     * 请求头信息
     */
    private Map<String, String> reqHeaders;

    /**
     * 请求体信息（只支持json）
     */
    private JSONObject reqJson;

    private String reqData;

    /**
     * 返回头信息
     */
    private Map<String, String> resHeaders;

    /**
     * 返回体信息 （只支持json）
     */
    private JSONObject resJson;

    protected AbstractCmdDataFlowContext() {
    }

    protected AbstractCmdDataFlowContext(Date startDate, String code) {

    }


    /**
     * 构建 对象信息
     *
     * @param reqInfo
     * @param headerAll
     * @return
     * @throws Exception
     */
    public <T> T builder(String reqInfo, Map<String, String> headerAll) throws Exception {
        //预处理
        preBuilder(reqInfo, headerAll);
        //调用builder
        T dataFlowContext = (T) doBuilder(reqInfo, headerAll);
        //后处理
        afterBuilder((ICmdDataFlowContext) dataFlowContext);
        return dataFlowContext;
    }


    /**
     * 预处理
     *
     * @param reqInfo
     * @param headerAll
     */
    protected void preBuilder(String reqInfo, Map<String, String> headerAll) {

    }

    /**
     * 构建对象
     *
     * @param reqInfo
     * @param headerAll
     * @return
     * @throws Exception
     */
    public abstract ICmdDataFlowContext doBuilder(String reqInfo, Map<String, String> headerAll) throws Exception;

    protected void afterBuilder(ICmdDataFlowContext dataFlowContext) {

    }

    @Override
    public String getDataFlowId() {
        return dataFlowId;
    }

    public void setDataFlowId(String dataFlowId) {
        this.dataFlowId = dataFlowId;
    }

    @Override
    public Map<String, String> getReqHeaders() {
        return reqHeaders;
    }

    public void setReqHeaders(Map<String, String> reqHeaders) {
        this.reqHeaders = reqHeaders;
    }

    @Override
    public JSONObject getReqJson() {
        return reqJson;
    }

    public void setReqJson(JSONObject reqJson) {
        this.reqJson = reqJson;
    }

    @Override
    public String getReqData() {
        return reqData;
    }

    public void setReqData(String reqData) {
        this.reqData = reqData;
    }

    @Override
    public Map<String, String> getResHeaders() {
        return resHeaders;
    }

    @Override
    public void setResHeaders(Map<String, String> resHeaders) {
        this.resHeaders = resHeaders;
    }

    @Override
    public JSONObject getResJson() {
        return resJson;
    }

    @Override
    public void setResJson(JSONObject resJson) {
        this.resJson = resJson;
    }
}
