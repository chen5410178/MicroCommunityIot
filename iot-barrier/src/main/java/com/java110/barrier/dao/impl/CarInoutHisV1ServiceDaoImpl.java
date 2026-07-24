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

import com.java110.barrier.dao.ICarInoutHisV1ServiceDao;
import com.java110.core.db.dao.BaseServiceDao;
import com.java110.core.exception.DAOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("carInoutHisV1ServiceDaoImpl")
public class CarInoutHisV1ServiceDaoImpl extends BaseServiceDao implements ICarInoutHisV1ServiceDao {

    private static Logger logger = LoggerFactory.getLogger(CarInoutHisV1ServiceDaoImpl.class);

    @Override
    public int saveCarInoutHisInfo(Map info) throws DAOException {
        logger.debug("保存 saveCarInoutHisInfo 入参 info : {}", info);
        return sqlSessionTemplate.insert("carInoutHisV1ServiceDaoImpl.saveCarInoutHisInfo", info);
    }

    @Override
    public int saveCarInoutHisInfos(Map info) throws DAOException {
        logger.debug("批量保存 saveCarInoutHisInfos 入参 info : {}", info);
        return sqlSessionTemplate.insert("carInoutHisV1ServiceDaoImpl.saveCarInoutHisInfos", info);
    }

    @Override
    public List<Map> getCarInoutHisInfo(Map info) throws DAOException {
        logger.debug("查询 getCarInoutHisInfo 入参 info : {}", info);
        return sqlSessionTemplate.selectList("carInoutHisV1ServiceDaoImpl.getCarInoutHisInfo", info);
    }

    @Override
    public int queryCarInoutHisCount(Map info) {
        logger.debug("查询 queryCarInoutHisCount 入参 info : {}", info);
        List<Map> infos = sqlSessionTemplate.selectList("carInoutHisV1ServiceDaoImpl.queryCarInoutHisCount", info);
        if (infos.size() < 1) {
            return 0;
        }
        return Integer.parseInt(infos.get(0).get("count").toString());
    }

    @Override
    public int deleteCarInoutHisInfo(Map info) throws DAOException {
        logger.debug("删除 deleteCarInoutHisInfo 入参 info : {}", info);
        return sqlSessionTemplate.delete("carInoutHisV1ServiceDaoImpl.deleteCarInoutHisInfo", info);
    }
}
