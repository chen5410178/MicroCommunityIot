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
package com.java110.barrier.dao.impl;

import com.java110.barrier.dao.ICarInoutEvasionV1ServiceDao;
import com.java110.core.db.dao.BaseServiceDao;
import com.java110.core.exception.DAOException;
import com.java110.core.factory.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("carInoutEvasionV1ServiceDaoImpl")
public class CarInoutEvasionV1ServiceDaoImpl extends BaseServiceDao implements ICarInoutEvasionV1ServiceDao {

    private static Logger logger = LoggerFactory.getLogger(CarInoutEvasionV1ServiceDaoImpl.class);

    @Override
    public int saveCarInoutEvasionInfo(Map info) throws DAOException {
        logger.debug("保存 saveCarInoutEvasionInfo 入参 info : {}", info);
        int saveFlag = sqlSessionTemplate.insert("carInoutEvasionV1ServiceDaoImpl.saveCarInoutEvasionInfo", info);
        return saveFlag;
    }

    @Override
    public int updateCarInoutEvasionInfo(Map info) throws DAOException {
        logger.debug("修改 updateCarInoutEvasionInfo 入参 info : {}", info);
        int saveFlag = sqlSessionTemplate.update("carInoutEvasionV1ServiceDaoImpl.updateCarInoutEvasionInfo", info);
        return saveFlag;
    }

    @Override
    public List<Map> getCarInoutEvasionInfo(Map info) throws DAOException {
        logger.debug("查询 getCarInoutEvasionInfo 入参 info : {}", info);
        List<Map> result = sqlSessionTemplate.selectList("carInoutEvasionV1ServiceDaoImpl.getCarInoutEvasionInfo", info);
        return result;
    }

    @Override
    public int queryCarInoutEvasionsCount(Map info) {
        logger.debug("查询 queryCarInoutEvasionsCount 入参 info : {}", info);
        List<Map> result = sqlSessionTemplate.selectList("carInoutEvasionV1ServiceDaoImpl.queryCarInoutEvasionsCount", info);
        if (result.size() < 1) {
            return 0;
        }
        return Integer.parseInt(result.get(0).get("count").toString());
    }
}
