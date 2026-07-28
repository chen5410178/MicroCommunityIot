
package com.java110.barrier.dao.impl;

import com.java110.barrier.dao.IMqttSendLogV1ServiceDao;
import com.java110.core.db.dao.BaseServiceDao;
import com.java110.core.exception.DAOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("mqttSendLogV1ServiceDaoImpl")
public class MqttSendLogV1ServiceDaoImpl extends BaseServiceDao implements IMqttSendLogV1ServiceDao {

    private static Logger logger = LoggerFactory.getLogger(MqttSendLogV1ServiceDaoImpl.class);

    @Override
    public int saveMqttSendLog(Map info) throws DAOException {
        logger.debug("保存 saveMqttSendLog 入参 info : {}", info);
        return sqlSessionTemplate.insert("mqttSendLogV1ServiceDaoImpl.saveMqttSendLog", info);
    }

    @Override
    public int queryMqttSendLogCount(Map info) {
        logger.debug("查询 queryMqttSendLogCount 入参 info : {}", info);
        List<Map> result = sqlSessionTemplate.selectList("mqttSendLogV1ServiceDaoImpl.queryMqttSendLogCount", info);
        if (result.size() < 1) {
            return 0;
        }
        return Integer.parseInt(result.get(0).get("count").toString());
    }

    @Override
    public Map selectCutoffTime(Map info) throws DAOException {
        logger.debug("查询 selectCutoffTime 入参 info : {}", info);
        List<Map> result = sqlSessionTemplate.selectList("mqttSendLogV1ServiceDaoImpl.selectCutoffTime", info);
        if (result.size() < 1) {
            return null;
        }
        return result.get(0);
    }

    @Override
    public int deleteByMachineCodeAndTime(Map info) throws DAOException {
        logger.debug("删除 deleteByMachineCodeAndTime 入参 info : {}", info);
        return sqlSessionTemplate.delete("mqttSendLogV1ServiceDaoImpl.deleteByMachineCodeAndTime", info);
    }

    @Override
    public List<Map> queryMqttSendLogs(Map info) throws DAOException {
        logger.debug("查询 queryMqttSendLogs 入参 info : {}", info);
        return sqlSessionTemplate.selectList("mqttSendLogV1ServiceDaoImpl.queryMqttSendLogs", info);
    }
}
