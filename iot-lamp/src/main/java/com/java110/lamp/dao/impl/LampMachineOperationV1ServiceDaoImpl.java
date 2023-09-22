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
package com.java110.lamp.dao.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.db.dao.BaseServiceDao;
import com.java110.core.exception.DAOException;
import com.java110.lamp.dao.ILampMachineOperationV1ServiceDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 类表述：
 * add by 吴学文 at 2023-09-21 17:14:16 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Service("lampMachineOperationV1ServiceDaoImpl")
public class LampMachineOperationV1ServiceDaoImpl extends BaseServiceDao implements ILampMachineOperationV1ServiceDao {

    private static Logger logger = LoggerFactory.getLogger(LampMachineOperationV1ServiceDaoImpl.class);


    /**
     * 保存操作日志信息 到 instance
     *
     * @param info bId 信息
     * @throws DAOException DAO异常
     */
    @Override
    public int saveLampMachineOperationInfo(Map info) throws DAOException {
        logger.debug("保存 saveLampMachineOperationInfo 入参 info : {}", info);

        int saveFlag = sqlSessionTemplate.insert("lampMachineOperationV1ServiceDaoImpl.saveLampMachineOperationInfo", info);

        return saveFlag;
    }


    /**
     * 查询操作日志信息（instance）
     *
     * @param info bId 信息
     * @return List<Map>
     * @throws DAOException DAO异常
     */
    @Override
    public List<Map> getLampMachineOperationInfo(Map info) throws DAOException {
        logger.debug("查询 getLampMachineOperationInfo 入参 info : {}", info);

        List<Map> businessLampMachineOperationInfos = sqlSessionTemplate.selectList("lampMachineOperationV1ServiceDaoImpl.getLampMachineOperationInfo", info);

        return businessLampMachineOperationInfos;
    }


    /**
     * 修改操作日志信息
     *
     * @param info 修改信息
     * @throws DAOException DAO异常
     */
    @Override
    public int updateLampMachineOperationInfo(Map info) throws DAOException {
        logger.debug("修改 updateLampMachineOperationInfo 入参 info : {}", info);

        int saveFlag = sqlSessionTemplate.update("lampMachineOperationV1ServiceDaoImpl.updateLampMachineOperationInfo", info);

        return saveFlag;
    }

    /**
     * 查询操作日志数量
     *
     * @param info 操作日志信息
     * @return 操作日志数量
     */
    @Override
    public int queryLampMachineOperationsCount(Map info) {
        logger.debug("查询 queryLampMachineOperationsCount 入参 info : {}", info);

        List<Map> businessLampMachineOperationInfos = sqlSessionTemplate.selectList("lampMachineOperationV1ServiceDaoImpl.queryLampMachineOperationsCount", info);
        if (businessLampMachineOperationInfos.size() < 1) {
            return 0;
        }

        return Integer.parseInt(businessLampMachineOperationInfos.get(0).get("count").toString());
    }


}
