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
package com.java110.accessControl.dao.impl;

import com.java110.accessControl.dao.IAccessControlInoutV1ServiceDao;
import com.java110.core.db.dao.BaseServiceDao;
import com.java110.core.exception.DAOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 类表述：
 * add by 吴学文 at 2023-08-21 11:51:32 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Service("accessControlInoutV1ServiceDaoImpl")
public class AccessControlInoutV1ServiceDaoImpl extends BaseServiceDao implements IAccessControlInoutV1ServiceDao {

    private static Logger logger = LoggerFactory.getLogger(AccessControlInoutV1ServiceDaoImpl.class);





    /**
     * 保存开门记录信息 到 instance
     * @param info   bId 信息
     * @throws DAOException DAO异常
     */
    @Override
    public int saveAccessControlInoutInfo(Map info) throws DAOException {
        logger.debug("保存 saveAccessControlInoutInfo 入参 info : {}",info);

        int saveFlag = sqlSessionTemplate.insert("accessControlInoutV1ServiceDaoImpl.saveAccessControlInoutInfo",info);

        return saveFlag;
    }


    /**
     * 查询开门记录信息（instance）
     * @param info bId 信息
     * @return List<Map>
     * @throws DAOException DAO异常
     */
    @Override
    public List<Map> getAccessControlInoutInfo(Map info) throws DAOException {
        logger.debug("查询 getAccessControlInoutInfo 入参 info : {}",info);

        List<Map> infos = sqlSessionTemplate.selectList("accessControlInoutV1ServiceDaoImpl.getAccessControlInoutInfo",info);

        return infos;
    }


    /**
     * 修改开门记录信息
     * @param info 修改信息
     * @throws DAOException DAO异常
     */
    @Override
    public int updateAccessControlInoutInfo(Map info) throws DAOException {
        logger.debug("修改 updateAccessControlInoutInfo 入参 info : {}",info);

        int saveFlag = sqlSessionTemplate.update("accessControlInoutV1ServiceDaoImpl.updateAccessControlInoutInfo",info);

        return saveFlag;
    }

     /**
     * 查询开门记录数量
     * @param info 开门记录信息
     * @return 开门记录数量
     */
    @Override
    public int queryAccessControlInoutsCount(Map info) {
        logger.debug("查询 queryAccessControlInoutsCount 入参 info : {}",info);

        List<Map> infos = sqlSessionTemplate.selectList("accessControlInoutV1ServiceDaoImpl.queryAccessControlInoutsCount", info);
        if (infos.size() < 1) {
            return 0;
        }

        return Integer.parseInt(infos.get(0).get("count").toString());
    }

    @Override
    public int getPersonInCount(Map info) {
        logger.debug("查询 getPersonInCount 入参 info : {}",info);

        List<Map> infos = sqlSessionTemplate.selectList("accessControlInoutV1ServiceDaoImpl.getPersonInCount", info);
        if (infos.size() < 1) {
            return 0;
        }

        return Integer.parseInt(infos.get(0).get("count").toString());
    }

    @Override
    public List<Map> queryOpenDoorLineStats(Map info) {
        logger.debug("查询 queryOpenDoorLineStats 入参 info : {}",info);

        return sqlSessionTemplate.selectList("accessControlInoutV1ServiceDaoImpl.queryOpenDoorLineStats", info);
    }


}
