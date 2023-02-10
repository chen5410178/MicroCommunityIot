/*
 * Copyright 2017-2020 吴学文 and java110 team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.java110.dev.dao.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.db.dao.BaseServiceDao;
import com.java110.core.exception.DAOException;
import com.java110.dev.dao.IRouteV1ServiceDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 类表述：
 * add by 吴学文 at 2023-02-11 01:15:12 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Service("routeV1ServiceDaoImpl")
public class RouteV1ServiceDaoImpl extends BaseServiceDao implements IRouteV1ServiceDao {

    private static Logger logger = LoggerFactory.getLogger(RouteV1ServiceDaoImpl.class);


    /**
     * 保存接口权限信息 到 instance
     *
     * @param info bId 信息
     * @throws DAOException DAO异常
     */
    @Override
    public int saveRouteInfo(Map info) throws DAOException {
        logger.debug("保存 saveRouteInfo 入参 info : {}", info);

        int saveFlag = sqlSessionTemplate.insert("routeV1ServiceDaoImpl.saveRouteInfo", info);

        return saveFlag;
    }


    /**
     * 查询接口权限信息（instance）
     *
     * @param info bId 信息
     * @return List<Map>
     * @throws DAOException DAO异常
     */
    @Override
    public List<Map> getRouteInfo(Map info) throws DAOException {
        logger.debug("查询 getRouteInfo 入参 info : {}", info);

        List<Map> businessRouteInfos = sqlSessionTemplate.selectList("routeV1ServiceDaoImpl.getRouteInfo", info);

        return businessRouteInfos;
    }


    /**
     * 修改接口权限信息
     *
     * @param info 修改信息
     * @throws DAOException DAO异常
     */
    @Override
    public int updateRouteInfo(Map info) throws DAOException {
        logger.debug("修改 updateRouteInfo 入参 info : {}", info);

        int saveFlag = sqlSessionTemplate.update("routeV1ServiceDaoImpl.updateRouteInfo", info);

        return saveFlag;
    }

    /**
     * 查询接口权限数量
     *
     * @param info 接口权限信息
     * @return 接口权限数量
     */
    @Override
    public int queryRoutesCount(Map info) {
        logger.debug("查询 queryRoutesCount 入参 info : {}", info);

        List<Map> businessRouteInfos = sqlSessionTemplate.selectList("routeV1ServiceDaoImpl.queryRoutesCount", info);
        if (businessRouteInfos.size() < 1) {
            return 0;
        }

        return Integer.parseInt(businessRouteInfos.get(0).get("count").toString());
    }


}
