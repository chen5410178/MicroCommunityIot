package com.java110.core.smo;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.context.DataQuery;
import org.springframework.http.ResponseEntity;

/**
 * 公用查询处理
 * Created by wuxw on 2018/4/19.
 */
public interface IQueryServiceSMO {

    /**
     * c_common_sql
     * 公共查询服务
     * @return
     */
    public void commonQueryService(DataQuery dataQuery) throws Exception;


    /**
     * c_common_sql
     * 公共受理服务
     * @return
     * @throws Exception
     */
    public void commonDoService(DataQuery dataQuery) throws Exception;


    /**
     * c_common_sql
     * 公共受理服务
     * @return
     * @throws Exception
     */
    public ResponseEntity<String> fallBack(String fallBackInfo) throws Exception;

    /**
     * 执行查询sql
     * @param param
     * @param sql
     * @return {
     *     th:[],
     *     td:[{}]
     * }
     */
    JSONObject execQuerySql(JSONObject param, String sql) throws Exception;

    /**
     * 执行java脚本
     *
     * @param javaCode
     * @throws Exception
     */
    JSONObject execJava(JSONObject params, String javaCode) throws Exception;
}
