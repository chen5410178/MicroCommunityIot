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
package com.java110.car.dao.impl;

import com.java110.car.dao.ISpaceMachineFactoryV1ServiceDao;
import com.java110.core.db.dao.BaseServiceDao;
import com.java110.core.exception.DAOException;
import com.java110.core.utils.ListUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 类表述：
 * add by 吴学文 at 2024-03-05 21:24:25 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Service("spaceMachineFactoryV1ServiceDaoImpl")
public class SpaceMachineFactoryV1ServiceDaoImpl extends BaseServiceDao implements ISpaceMachineFactoryV1ServiceDao {

    private static Logger logger = LoggerFactory.getLogger(SpaceMachineFactoryV1ServiceDaoImpl.class);





    /**
     * 保存地磁厂家信息 到 instance
     * @param info   bId 信息
     * @throws DAOException DAO异常
     */
    @Override
    public int saveSpaceMachineFactoryInfo(Map info) throws DAOException {
        logger.debug("保存 saveSpaceMachineFactoryInfo 入参 info : {}",info);

        int saveFlag = sqlSessionTemplate.insert("spaceMachineFactoryV1ServiceDaoImpl.saveSpaceMachineFactoryInfo",info);

        return saveFlag;
    }


    /**
     * 查询地磁厂家信息（instance）
     * @param info bId 信息
     * @return List<Map>
     * @throws DAOException DAO异常
     */
    @Override
    public List<Map> getSpaceMachineFactoryInfo(Map info) throws DAOException {
        logger.debug("查询 getSpaceMachineFactoryInfo 入参 info : {}",info);

        List<Map> infos = sqlSessionTemplate.selectList("spaceMachineFactoryV1ServiceDaoImpl.getSpaceMachineFactoryInfo",info);

        return infos;
    }


    /**
     * 修改地磁厂家信息
     * @param info 修改信息
     * @throws DAOException DAO异常
     */
    @Override
    public int updateSpaceMachineFactoryInfo(Map info) throws DAOException {
        logger.debug("修改 updateSpaceMachineFactoryInfo 入参 info : {}",info);

        int saveFlag = sqlSessionTemplate.update("spaceMachineFactoryV1ServiceDaoImpl.updateSpaceMachineFactoryInfo",info);

        return saveFlag;
    }

     /**
     * 查询地磁厂家数量
     * @param info 地磁厂家信息
     * @return 地磁厂家数量
     */
    @Override
    public int querySpaceMachineFactorysCount(Map info) {
        logger.debug("查询 querySpaceMachineFactorysCount 入参 info : {}",info);

        List<Map> infos = sqlSessionTemplate.selectList("spaceMachineFactoryV1ServiceDaoImpl.querySpaceMachineFactorysCount", info);
        if (ListUtil.isNull(infos)) {
            return 0;
        }

        return Integer.parseInt(infos.get(0).get("count").toString());
    }


}
