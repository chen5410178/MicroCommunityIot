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
package com.java110.user.dao.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.db.dao.BaseServiceDao;
import com.java110.core.exception.DAOException;
import com.java110.user.dao.IStoreStaffV1ServiceDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 类表述：
 * add by 吴学文 at 2023-02-11 00:50:50 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Service("storeStaffV1ServiceDaoImpl")
public class StoreStaffV1ServiceDaoImpl extends BaseServiceDao implements IStoreStaffV1ServiceDao {

    private static Logger logger = LoggerFactory.getLogger(StoreStaffV1ServiceDaoImpl.class);





    /**
     * 保存员工表信息 到 instance
     * @param info   bId 信息
     * @throws DAOException DAO异常
     */
    @Override
    public int saveStoreStaffInfo(Map info) throws DAOException {
        logger.debug("保存 saveStoreStaffInfo 入参 info : {}",info);

        int saveFlag = sqlSessionTemplate.insert("storeStaffV1ServiceDaoImpl.saveStoreStaffInfo",info);

        return saveFlag;
    }


    /**
     * 查询员工表信息（instance）
     * @param info bId 信息
     * @return List<Map>
     * @throws DAOException DAO异常
     */
    @Override
    public List<Map> getStoreStaffInfo(Map info) throws DAOException {
        logger.debug("查询 getStoreStaffInfo 入参 info : {}",info);

        List<Map> businessStoreStaffInfos = sqlSessionTemplate.selectList("storeStaffV1ServiceDaoImpl.getStoreStaffInfo",info);

        return businessStoreStaffInfos;
    }


    /**
     * 修改员工表信息
     * @param info 修改信息
     * @throws DAOException DAO异常
     */
    @Override
    public int updateStoreStaffInfo(Map info) throws DAOException {
        logger.debug("修改 updateStoreStaffInfo 入参 info : {}",info);

        int saveFlag = sqlSessionTemplate.update("storeStaffV1ServiceDaoImpl.updateStoreStaffInfo",info);

        return saveFlag;
    }

     /**
     * 查询员工表数量
     * @param info 员工表信息
     * @return 员工表数量
     */
    @Override
    public int queryStoreStaffsCount(Map info) {
        logger.debug("查询 queryStoreStaffsCount 入参 info : {}",info);

        List<Map> businessStoreStaffInfos = sqlSessionTemplate.selectList("storeStaffV1ServiceDaoImpl.queryStoreStaffsCount", info);
        if (businessStoreStaffInfos.size() < 1) {
            return 0;
        }

        return Integer.parseInt(businessStoreStaffInfos.get(0).get("count").toString());
    }


}
