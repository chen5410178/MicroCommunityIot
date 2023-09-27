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

import com.java110.barrier.dao.ICarInoutTempAuthV1ServiceDao;
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
 * add by 吴学文 at 2023-09-27 13:22:09 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Service("carInoutTempAuthV1ServiceDaoImpl")
public class CarInoutTempAuthV1ServiceDaoImpl extends BaseServiceDao implements ICarInoutTempAuthV1ServiceDao {

    private static Logger logger = LoggerFactory.getLogger(CarInoutTempAuthV1ServiceDaoImpl.class);





    /**
     * 保存临时车审核信息 到 instance
     * @param info   bId 信息
     * @throws DAOException DAO异常
     */
    @Override
    public int saveCarInoutTempAuthInfo(Map info) throws DAOException {
        logger.debug("保存 saveCarInoutTempAuthInfo 入参 info : {}",info);

        int saveFlag = sqlSessionTemplate.insert("carInoutTempAuthV1ServiceDaoImpl.saveCarInoutTempAuthInfo",info);

        return saveFlag;
    }


    /**
     * 查询临时车审核信息（instance）
     * @param info bId 信息
     * @return List<Map>
     * @throws DAOException DAO异常
     */
    @Override
    public List<Map> getCarInoutTempAuthInfo(Map info) throws DAOException {
        logger.debug("查询 getCarInoutTempAuthInfo 入参 info : {}",info);

        List<Map> businessCarInoutTempAuthInfos = sqlSessionTemplate.selectList("carInoutTempAuthV1ServiceDaoImpl.getCarInoutTempAuthInfo",info);

        return businessCarInoutTempAuthInfos;
    }


    /**
     * 修改临时车审核信息
     * @param info 修改信息
     * @throws DAOException DAO异常
     */
    @Override
    public int updateCarInoutTempAuthInfo(Map info) throws DAOException {
        logger.debug("修改 updateCarInoutTempAuthInfo 入参 info : {}",info);

        int saveFlag = sqlSessionTemplate.update("carInoutTempAuthV1ServiceDaoImpl.updateCarInoutTempAuthInfo",info);

        return saveFlag;
    }

     /**
     * 查询临时车审核数量
     * @param info 临时车审核信息
     * @return 临时车审核数量
     */
    @Override
    public int queryCarInoutTempAuthsCount(Map info) {
        logger.debug("查询 queryCarInoutTempAuthsCount 入参 info : {}",info);

        List<Map> businessCarInoutTempAuthInfos = sqlSessionTemplate.selectList("carInoutTempAuthV1ServiceDaoImpl.queryCarInoutTempAuthsCount", info);
        if (businessCarInoutTempAuthInfos.size() < 1) {
            return 0;
        }

        return Integer.parseInt(businessCarInoutTempAuthInfos.get(0).get("count").toString());
    }


}
