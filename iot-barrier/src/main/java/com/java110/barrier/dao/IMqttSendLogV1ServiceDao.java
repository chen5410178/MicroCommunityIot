
package com.java110.barrier.dao;

import com.java110.core.exception.DAOException;

import java.util.List;
import java.util.Map;

public interface IMqttSendLogV1ServiceDao {

    int saveMqttSendLog(Map info) throws DAOException;

    int queryMqttSendLogCount(Map info);

    Map selectCutoffTime(Map info) throws DAOException;

    int deleteByMachineCodeAndTime(Map info) throws DAOException;

    List<Map> queryMqttSendLogs(Map info) throws DAOException;
}
