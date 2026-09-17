package com.java110.acct.mall;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;

/**
 * 商城开放接口调用组件（iot-acct 内置，开票专用）
 * 参照 MicroCommunityV2 service-job SendMallImpl 实现
 */
public interface ISendMall {

    /**
     * post 请求商城开放接口
     *
     * @param url      商城接口路径（如 /mall/api/common.openCommonApi）
     * @param paramIn  业务入参（含 mallApiCode）
     * @return 商城响应 ResultVo
     */
    ResultVo post(String url, JSONObject paramIn);

    /**
     * post 请求商城开放接口，返回商城原始响应报文
     * 需要 records/total 等 data 外围字段（分页接口）时使用；商城开关未开启返回 null
     *
     * @param url      商城接口路径（如 /mall/api/common.openCommonApi）
     * @param paramIn  业务入参（含 mallApiCode）
     * @return 商城响应报文（含 code/msg/data/records/total），未开启商城时返回 null
     */
    JSONObject postForBody(String url, JSONObject paramIn);

    /**
     * get 请求商城开放接口
     *
     * @param url 商城接口路径
     * @return 商城响应 ResultVo
     */
    ResultVo get(String url);
}
