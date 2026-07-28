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

import com.java110.barrier.dao.ICarInoutEvasionConfigV1ServiceDao;
import com.java110.core.db.dao.BaseServiceDao;
import com.java110.core.exception.DAOException;
import com.java110.core.factory.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("carInoutEvasionConfigV1ServiceDaoImpl")
public class CarInoutEvasionConfigV1ServiceDaoImpl extends BaseServiceDao implements ICarInoutEvasionConfigV1ServiceDao {

    private static Logger logger = LoggerFactory.getLogger(CarInoutEvasionConfigV1ServiceDaoImpl.class);

    @Override
    public int saveCarInoutEvasionConfigInfo(Map info) throws DAOException {
        logger.debug("保存 saveCarInoutEvasionConfigInfo 入参 info : {}", info);
        int saveFlag = sqlSessionTemplate.insert("carInoutEvasionConfigV1ServiceDaoImpl.saveCarInoutEvasionConfigInfo", info);
        return saveFlag;
    }

    @Override
    public int updateCarInoutEvasionConfigInfo(Map info) throws DAOException {
        logger.debug("修改 updateCarInoutEvasionConfigInfo 入参 info : {}", info);
        int saveFlag = sqlSessionTemplate.update("carInoutEvasionConfigV1ServiceDaoImpl.updateCarInoutEvasionConfigInfo", info);
        return saveFlag;
    }

    @Override
    public List<Map> getCarInoutEvasionConfigInfo(Map info) throws DAOException {
        logger.debug("查询 getCarInoutEvasionConfigInfo 入参 info : {}", info);
        List<Map> result = sqlSessionTemplate.selectList("carInoutEvasionConfigV1ServiceDaoImpl.getCarInoutEvasionConfigInfo", info);
        return result;
    }
}
