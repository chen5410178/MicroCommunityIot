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
package com.java110.system.dao;

import com.java110.core.exception.DAOException;

import java.util.List;
import java.util.Map;

/**
 * 类表述：注册协议数据访问接口
 * add by java110 team at 2026-09-01
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
public interface IRegisterProtocolV1ServiceDao {

    /**
     * 保存 注册协议信息
     * @param info 协议信息
     * @throws DAOException DAO异常
     */
    int saveRegisterProtocolInfo(Map info) throws DAOException;

    /**
     * 查询注册协议信息
     * @param info 协议信息
     * @return 注册协议信息
     * @throws DAOException DAO异常
     */
    List<Map> getRegisterProtocolInfo(Map info) throws DAOException;

    /**
     * 修改注册协议信息
     * @param info 修改信息
     * @throws DAOException DAO异常
     */
    int updateRegisterProtocolInfo(Map info) throws DAOException;

    /**
     * 查询注册协议总数
     *
     * @param info 注册协议信息
     * @return 注册协议数量
     */
    int queryRegisterProtocolsCount(Map info);
}
