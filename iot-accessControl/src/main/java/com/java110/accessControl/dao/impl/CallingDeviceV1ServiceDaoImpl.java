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

import com.java110.accessControl.dao.ICallingDeviceV1ServiceDao;
import com.java110.core.db.dao.BaseServiceDao;
import com.java110.core.exception.DAOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 类表述：
 * add by 吴学文 at 2024-08-30 11:31:06 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Service("callingDeviceV1ServiceDaoImpl")
public class CallingDeviceV1ServiceDaoImpl extends BaseServiceDao implements ICallingDeviceV1ServiceDao {

    private static Logger logger = LoggerFactory.getLogger(CallingDeviceV1ServiceDaoImpl.class);





    /**
     * 保存呼叫平台信息 到 instance
     * @param info   bId 信息
     * @throws DAOException DAO异常
     */
    @Override
    public int saveCallingDeviceInfo(Map info) throws DAOException {
        logger.debug("保存 saveCallingDeviceInfo 入参 info : {}",info);

        int saveFlag = sqlSessionTemplate.insert("callingDeviceV1ServiceDaoImpl.saveCallingDeviceInfo",info);

        return saveFlag;
    }


    /**
     * 查询呼叫平台信息（instance）
     * @param info bId 信息
     * @return List<Map>
     * @throws DAOException DAO异常
     */
    @Override
    public List<Map> getCallingDeviceInfo(Map info) throws DAOException {
        logger.debug("查询 getCallingDeviceInfo 入参 info : {}",info);

        List<Map> businessCallingDeviceInfos = sqlSessionTemplate.selectList("callingDeviceV1ServiceDaoImpl.getCallingDeviceInfo",info);

        return businessCallingDeviceInfos;
    }


    /**
     * 修改呼叫平台信息
     * @param info 修改信息
     * @throws DAOException DAO异常
     */
    @Override
    public int updateCallingDeviceInfo(Map info) throws DAOException {
        logger.debug("修改 updateCallingDeviceInfo 入参 info : {}",info);

        int saveFlag = sqlSessionTemplate.update("callingDeviceV1ServiceDaoImpl.updateCallingDeviceInfo",info);

        return saveFlag;
    }

     /**
     * 查询呼叫平台数量
     * @param info 呼叫平台信息
     * @return 呼叫平台数量
     */
    @Override
    public int queryCallingDevicesCount(Map info) {
        logger.debug("查询 queryCallingDevicesCount 入参 info : {}",info);

        List<Map> businessCallingDeviceInfos = sqlSessionTemplate.selectList("callingDeviceV1ServiceDaoImpl.queryCallingDevicesCount", info);
        if (businessCallingDeviceInfos.size() < 1) {
            return 0;
        }

        return Integer.parseInt(businessCallingDeviceInfos.get(0).get("count").toString());
    }


}
