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
package com.java110.system.dao.impl;

import com.java110.core.db.dao.BaseServiceDao;
import com.java110.core.exception.DAOException;
import com.java110.system.dao.IHardwareManufacturerAttrV1ServiceDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 类表述：
 * add by 吴学文 at 2023-08-15 09:36:10 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Service("hardwareManufacturerAttrV1ServiceDaoImpl")
public class HardwareManufacturerAttrV1ServiceDaoImpl extends BaseServiceDao implements IHardwareManufacturerAttrV1ServiceDao {

    private static Logger logger = LoggerFactory.getLogger(HardwareManufacturerAttrV1ServiceDaoImpl.class);





    /**
     * 保存厂家属性信息 到 instance
     * @param info   bId 信息
     * @throws DAOException DAO异常
     */
    @Override
    public int saveHardwareManufacturerAttrInfo(Map info) throws DAOException {
        logger.debug("保存 saveHardwareManufacturerAttrInfo 入参 info : {}",info);

        int saveFlag = sqlSessionTemplate.insert("hardwareManufacturerAttrV1ServiceDaoImpl.saveHardwareManufacturerAttrInfo",info);

        return saveFlag;
    }


    /**
     * 查询厂家属性信息（instance）
     * @param info bId 信息
     * @return List<Map>
     * @throws DAOException DAO异常
     */
    @Override
    public List<Map> getHardwareManufacturerAttrInfo(Map info) throws DAOException {
        logger.debug("查询 getHardwareManufacturerAttrInfo 入参 info : {}",info);

        List<Map> businessHardwareManufacturerAttrInfos = sqlSessionTemplate.selectList("hardwareManufacturerAttrV1ServiceDaoImpl.getHardwareManufacturerAttrInfo",info);

        return businessHardwareManufacturerAttrInfos;
    }


    /**
     * 修改厂家属性信息
     * @param info 修改信息
     * @throws DAOException DAO异常
     */
    @Override
    public int updateHardwareManufacturerAttrInfo(Map info) throws DAOException {
        logger.debug("修改 updateHardwareManufacturerAttrInfo 入参 info : {}",info);

        int saveFlag = sqlSessionTemplate.update("hardwareManufacturerAttrV1ServiceDaoImpl.updateHardwareManufacturerAttrInfo",info);

        return saveFlag;
    }

     /**
     * 查询厂家属性数量
     * @param info 厂家属性信息
     * @return 厂家属性数量
     */
    @Override
    public int queryHardwareManufacturerAttrsCount(Map info) {
        logger.debug("查询 queryHardwareManufacturerAttrsCount 入参 info : {}",info);

        List<Map> businessHardwareManufacturerAttrInfos = sqlSessionTemplate.selectList("hardwareManufacturerAttrV1ServiceDaoImpl.queryHardwareManufacturerAttrsCount", info);
        if (businessHardwareManufacturerAttrInfos.size() < 1) {
            return 0;
        }

        return Integer.parseInt(businessHardwareManufacturerAttrInfos.get(0).get("count").toString());
    }


}
