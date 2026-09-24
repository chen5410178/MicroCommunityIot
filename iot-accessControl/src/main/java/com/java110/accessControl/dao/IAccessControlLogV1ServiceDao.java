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
package com.java110.accessControl.dao;



import com.java110.core.exception.DAOException;
import com.java110.dto.accessControlLog.AccessControlLogDto;

import java.util.List;
import java.util.Map;

/**
 * 类表述：
 * add by 吴学文 at 2023-08-16 17:22:49 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
public interface IAccessControlLogV1ServiceDao {


    /**
     * 保存 门禁日志信息
     * @param info
     * @throws DAOException DAO异常
     */
    int saveAccessControlLogInfo(Map info) throws DAOException;




    /**
     * 查询门禁日志信息（instance过程）
     * 根据bId 查询门禁日志信息
     * @param info bId 信息
     * @return 门禁日志信息
     * @throws DAOException DAO异常
     */
    List<Map> getAccessControlLogInfo(Map info) throws DAOException;



    /**
     * 修改门禁日志信息
     * @param info 修改信息
     * @throws DAOException DAO异常
     */
    int updateAccessControlLogInfo(Map info) throws DAOException;


    /**
     * 将同一设备、同一人员、同一命令的已同步历史有效下发记录置为失效
     * @param info 修改信息
     * @throws DAOException DAO异常
     */
    int updateAccessControlLogInfoInvalid(Map info) throws DAOException;


    /**
     * 查询门禁日志总数
     *
     * @param info 门禁日志信息
     * @return 门禁日志数量
     */
    int queryAccessControlLogsCount(Map info);

    List<Map> queryAccessControlLogParams(Map info);

    List<Map> getAssetOpenDoor(Map info);
}
