
package com.java110.barrier.engine.inout;

import com.java110.barrier.dao.IMqttSendLogV1ServiceDao;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.po.mqttSendLog.MqttSendLogPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("mqttSendLogEngine")
public class MqttSendLogEngine {

    private static Logger logger = LoggerFactory.getLogger(MqttSendLogEngine.class);

    private static final int MAX_COUNT = 50000;
    private static final int KEEP_COUNT = 5000;

    @Autowired
    private IMqttSendLogV1ServiceDao mqttSendLogV1ServiceDaoImpl;

    public void saveSendLog(String taskId, String machineCode, String carNum, String cmdType, String topic, String context) {
        try {
            MqttSendLogPo po = new MqttSendLogPo();
            po.setLogId(GenerateCodeFactory.getGeneratorId("10"));
            po.setBusinessId(taskId);
            po.setMachineCode(machineCode);
            po.setCarNum(carNum);
            po.setCmdType(cmdType);
            po.setTopic(topic);
            po.setContext(context);
            Map info = BeanConvertUtil.beanCovertMap(po);
            mqttSendLogV1ServiceDaoImpl.saveMqttSendLog(info);

            int count = queryCountByMachineCode(machineCode);
            if (count > MAX_COUNT) {
                cleanByMachineCode(machineCode);
            }
        } catch (Exception e) {
            logger.error("保存MQTT下发日志失败", e);
        }
    }

    private int queryCountByMachineCode(String machineCode) {
        MqttSendLogPo po = new MqttSendLogPo();
        po.setMachineCode(machineCode);
        Map info = BeanConvertUtil.beanCovertMap(po);
        return mqttSendLogV1ServiceDaoImpl.queryMqttSendLogCount(info);
    }

    private void cleanByMachineCode(String machineCode) {
        try {
            MqttSendLogPo po = new MqttSendLogPo();
            po.setMachineCode(machineCode);
            Map info = BeanConvertUtil.beanCovertMap(po);
            Map cutoff = mqttSendLogV1ServiceDaoImpl.selectCutoffTime(info);
            if (cutoff != null && cutoff.containsKey("createTime")) {
                info.put("cutoffTime", cutoff.get("createTime"));
                mqttSendLogV1ServiceDaoImpl.deleteByMachineCodeAndTime(info);
                logger.info("清理MQTT下发日志，machineCode={}, 保留最近{}条", machineCode, KEEP_COUNT);
            }
        } catch (Exception e) {
            logger.error("清理MQTT下发日志失败，machineCode={}", machineCode, e);
        }
    }
}
