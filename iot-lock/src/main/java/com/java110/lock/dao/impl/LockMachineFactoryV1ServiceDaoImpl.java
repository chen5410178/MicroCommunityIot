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
package com.java110.lock.dao.impl;

import com.java110.core.db.dao.BaseServiceDao;
import com.java110.core.exception.DAOException;
import com.java110.lock.dao.ILockMachineFactoryV1ServiceDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 类表述：
 * add by 吴学文 at 2023-10-30 09:28:53 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Service("lockMachineFactoryV1ServiceDaoImpl")
public class LockMachineFactoryV1ServiceDaoImpl extends BaseServiceDao implements ILockMachineFactoryV1ServiceDao {

    private static Logger logger = LoggerFactory.getLogger(LockMachineFactoryV1ServiceDaoImpl.class);


    /**
     * 保存门锁厂家信息 到 instance
     *
     * @param info bId 信息
     * @throws DAOException DAO异常
     */
    @Override
    public int saveLockMachineFactoryInfo(Map info) throws DAOException {
        logger.debug("保存 saveLockMachineFactoryInfo 入参 info : {}", info);

        int saveFlag = sqlSessionTemplate.insert("lockMachineFactoryV1ServiceDaoImpl.saveLockMachineFactoryInfo", info);

        return saveFlag;
    }


    /**
     * 查询门锁厂家信息（instance）
     *
     * @param info bId 信息
     * @return List<Map>
     * @throws DAOException DAO异常
     */
    @Override
    public List<Map> getLockMachineFactoryInfo(Map info) throws DAOException {
        logger.debug("查询 getLockMachineFactoryInfo 入参 info : {}", info);

        List<Map> businessLockMachineFactoryInfos = sqlSessionTemplate.selectList("lockMachineFactoryV1ServiceDaoImpl.getLockMachineFactoryInfo", info);

        return businessLockMachineFactoryInfos;
    }


    /**
     * 修改门锁厂家信息
     *
     * @param info 修改信息
     * @throws DAOException DAO异常
     */
    @Override
    public int updateLockMachineFactoryInfo(Map info) throws DAOException {
        logger.debug("修改 updateLockMachineFactoryInfo 入参 info : {}", info);

        int saveFlag = sqlSessionTemplate.update("lockMachineFactoryV1ServiceDaoImpl.updateLockMachineFactoryInfo", info);

        return saveFlag;
    }

    /**
     * 查询门锁厂家数量
     *
     * @param info 门锁厂家信息
     * @return 门锁厂家数量
     */
    @Override
    public int queryLockMachineFactorysCount(Map info) {
        logger.debug("查询 queryLockMachineFactorysCount 入参 info : {}", info);

        List<Map> businessLockMachineFactoryInfos = sqlSessionTemplate.selectList("lockMachineFactoryV1ServiceDaoImpl.queryLockMachineFactorysCount", info);
        if (businessLockMachineFactoryInfos.size() < 1) {
            return 0;
        }

        return Integer.parseInt(businessLockMachineFactoryInfos.get(0).get("count").toString());
    }

    @Override
    public List<Map> getLockMachineFactoryInfo1(Map info) throws DAOException {
        logger.debug("查询 getLockMachineFactoryInfo1 入参 info : {}", info);

        List<Map> businessLockMachineFactoryInfos = sqlSessionTemplate.selectList("lockMachineFactoryV1ServiceDaoImpl.getLockMachineFactoryInfo1", info);

        return businessLockMachineFactoryInfos;
    }


}
