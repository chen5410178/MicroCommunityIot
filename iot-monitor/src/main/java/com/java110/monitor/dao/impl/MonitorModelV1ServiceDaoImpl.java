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
package com.java110.monitor.dao.impl;

import com.java110.core.db.dao.BaseServiceDao;
import com.java110.core.exception.DAOException;
import com.java110.monitor.dao.IMonitorModelV1ServiceDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 类表述：
 * add by 吴学文 at 2023-10-17 16:12:34 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Service("monitorModelV1ServiceDaoImpl")
public class MonitorModelV1ServiceDaoImpl extends BaseServiceDao implements IMonitorModelV1ServiceDao {

    private static Logger logger = LoggerFactory.getLogger(MonitorModelV1ServiceDaoImpl.class);


    /**
     * 保存监控模型信息 到 instance
     *
     * @param info bId 信息
     * @throws DAOException DAO异常
     */
    @Override
    public int saveMonitorModelInfo(Map info) throws DAOException {
        logger.debug("保存 saveMonitorModelInfo 入参 info : {}", info);

        int saveFlag = sqlSessionTemplate.insert("monitorModelV1ServiceDaoImpl.saveMonitorModelInfo", info);

        return saveFlag;
    }


    /**
     * 查询监控模型信息（instance）
     *
     * @param info bId 信息
     * @return List<Map>
     * @throws DAOException DAO异常
     */
    @Override
    public List<Map> getMonitorModelInfo(Map info) throws DAOException {
        logger.debug("查询 getMonitorModelInfo 入参 info : {}", info);

        List<Map> businessMonitorModelInfos = sqlSessionTemplate.selectList("monitorModelV1ServiceDaoImpl.getMonitorModelInfo", info);

        return businessMonitorModelInfos;
    }


    /**
     * 修改监控模型信息
     *
     * @param info 修改信息
     * @throws DAOException DAO异常
     */
    @Override
    public int updateMonitorModelInfo(Map info) throws DAOException {
        logger.debug("修改 updateMonitorModelInfo 入参 info : {}", info);

        int saveFlag = sqlSessionTemplate.update("monitorModelV1ServiceDaoImpl.updateMonitorModelInfo", info);

        return saveFlag;
    }

    /**
     * 查询监控模型数量
     *
     * @param info 监控模型信息
     * @return 监控模型数量
     */
    @Override
    public int queryMonitorModelsCount(Map info) {
        logger.debug("查询 queryMonitorModelsCount 入参 info : {}", info);

        List<Map> businessMonitorModelInfos = sqlSessionTemplate.selectList("monitorModelV1ServiceDaoImpl.queryMonitorModelsCount", info);
        if (businessMonitorModelInfos.size() < 1) {
            return 0;
        }

        return Integer.parseInt(businessMonitorModelInfos.get(0).get("count").toString());
    }


}
