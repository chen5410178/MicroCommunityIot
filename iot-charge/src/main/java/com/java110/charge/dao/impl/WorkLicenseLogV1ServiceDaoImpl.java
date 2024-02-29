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
package com.java110.charge.dao.impl;

import com.java110.charge.dao.IWorkLicenseLogV1ServiceDao;
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
 * add by 吴学文 at 2024-02-29 10:10:34 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Service("workLicenseLogV1ServiceDaoImpl")
public class WorkLicenseLogV1ServiceDaoImpl extends BaseServiceDao implements IWorkLicenseLogV1ServiceDao {

    private static Logger logger = LoggerFactory.getLogger(WorkLicenseLogV1ServiceDaoImpl.class);





    /**
     * 保存工牌日志信息 到 instance
     * @param info   bId 信息
     * @throws DAOException DAO异常
     */
    @Override
    public int saveWorkLicenseLogInfo(Map info) throws DAOException {
        logger.debug("保存 saveWorkLicenseLogInfo 入参 info : {}",info);

        int saveFlag = sqlSessionTemplate.insert("workLicenseLogV1ServiceDaoImpl.saveWorkLicenseLogInfo",info);

        return saveFlag;
    }


    /**
     * 查询工牌日志信息（instance）
     * @param info bId 信息
     * @return List<Map>
     * @throws DAOException DAO异常
     */
    @Override
    public List<Map> getWorkLicenseLogInfo(Map info) throws DAOException {
        logger.debug("查询 getWorkLicenseLogInfo 入参 info : {}",info);

        List<Map> infos = sqlSessionTemplate.selectList("workLicenseLogV1ServiceDaoImpl.getWorkLicenseLogInfo",info);

        return infos;
    }


    /**
     * 修改工牌日志信息
     * @param info 修改信息
     * @throws DAOException DAO异常
     */
    @Override
    public int updateWorkLicenseLogInfo(Map info) throws DAOException {
        logger.debug("修改 updateWorkLicenseLogInfo 入参 info : {}",info);

        int saveFlag = sqlSessionTemplate.update("workLicenseLogV1ServiceDaoImpl.updateWorkLicenseLogInfo",info);

        return saveFlag;
    }

     /**
     * 查询工牌日志数量
     * @param info 工牌日志信息
     * @return 工牌日志数量
     */
    @Override
    public int queryWorkLicenseLogsCount(Map info) {
        logger.debug("查询 queryWorkLicenseLogsCount 入参 info : {}",info);

        List<Map> infos = sqlSessionTemplate.selectList("workLicenseLogV1ServiceDaoImpl.queryWorkLicenseLogsCount", info);
        if (ListUtil.isNull(infos)) {
            return 0;
        }

        return Integer.parseInt(infos.get(0).get("count").toString());
    }


}
