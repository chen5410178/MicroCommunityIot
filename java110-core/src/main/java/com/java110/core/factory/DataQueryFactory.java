package com.java110.core.factory;

import com.java110.bean.dto.service.ServiceSql;
import com.java110.core.cache.ServiceSqlCache;
import com.java110.core.context.DataQuery;

/**
 * 数据查询工厂类
 * Created by wuxw on 2018/4/19.
 */
public class DataQueryFactory {

    public static DataQuery newInstance() {
        return new DataQuery();
    }

    public static ServiceSql getServiceSql(DataQuery dataQuery) {
        return ServiceSqlCache.getServiceSql(dataQuery.getServiceCode());
    }
}
