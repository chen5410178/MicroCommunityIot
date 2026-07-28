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
package com.java110.barrier.dao;



import com.java110.core.exception.DAOException;

import java.util.List;
import java.util.Map;

public interface ICarInoutEvasionV1ServiceDao {

    int saveCarInoutEvasionInfo(Map info) throws DAOException;

    int updateCarInoutEvasionInfo(Map info) throws DAOException;

    List<Map> getCarInoutEvasionInfo(Map info) throws DAOException;

    int queryCarInoutEvasionsCount(Map info);
}
