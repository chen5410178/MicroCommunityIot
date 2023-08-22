package com.java110.core.factory;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.cache.MappingCache;
import com.java110.core.utils.StringUtil;
import com.java110.dto.mqttLog.MqttLogDto;
import com.java110.intf.system.IMqttLogV1InnerServiceSMO;
import com.java110.po.mqttLog.MqttLogPo;

public class MqttLogFactory {

    public static void saveReceiveLog(String taskId, String topic, String param) {

        String mqttLogSwitch = MappingCache.getValue("MQTT_LOG_SWITCH");

        if (!"ON".equals(mqttLogSwitch)) {
            return;
        }

        IMqttLogV1InnerServiceSMO saveMqttLogServiceImpl = ApplicationContextFactory.getBean("mqttLogV1InnerServiceSMOImpl", IMqttLogV1InnerServiceSMO.class);
        if (saveMqttLogServiceImpl == null) {
            saveMqttLogServiceImpl = ApplicationContextFactory.getBean(IMqttLogV1InnerServiceSMO.class.getName(), IMqttLogV1InnerServiceSMO.class);
        }

        MqttLogPo mqttLogPo = new MqttLogPo();
        mqttLogPo.setMqttId(GenerateCodeFactory.getGeneratorId("10"));
        mqttLogPo.setBusinessId(taskId);
        mqttLogPo.setBusinessKey(getBusinessKey(topic, param));
        mqttLogPo.setContext(param);
        mqttLogPo.setTopic(topic);
        mqttLogPo.setRemark("接受");
        saveMqttLogServiceImpl.saveMqttLog(mqttLogPo);

    }

    /**
     * {
     * "AlarmInfoPlate": {
     * "serialno": "69ee31dd-462720e8",
     * "result": {
     * "PlateResult": {
     * "isoffline": 0,
     * "plateid": 1624,
     * "license": "粤S7Y12C",
     * "type": 0
     * },
     * "imagePath": "picture/0/69ee31dd-462720e8/20220912/04/20220912_041503_361.jpg"* 			}
     * }* 	}
     * }
     *
     * @param topic
     * @param param
     * @return
     */
    private static String getBusinessKey(String topic, String param) {

        if (!"/device/push/result".equals(topic)) {
            return "其他业务";
        }

        JSONObject paramIn = JSONObject.parseObject(param);
        if (!paramIn.containsKey("AlarmInfoPlate")) {
            return "其他业务";
        }

        JSONObject alarmInfoPlateObj = paramIn.getJSONObject("AlarmInfoPlate");

        if (alarmInfoPlateObj.containsKey("heartbeat")) {
            return "心跳";
        }

        if (!alarmInfoPlateObj.containsKey("result")) {
            return "其他业务";
        }

        JSONObject result = alarmInfoPlateObj.getJSONObject("result");

        if (!result.containsKey("PlateResult")) {
            return "其他业务";
        }

        JSONObject plateResult = result.getJSONObject("PlateResult");

        String serialno = plateResult.getString("license");
        if (StringUtil.isEmpty(serialno)) {
            return "其他业务";
        }


        return serialno;
    }

    /**
     * 保存发送数据
     *
     * @param taskId
     * @param carNum
     * @param topic
     * @param param
     */
    public static void saveSendLog(String taskId, String carNum, String topic, String param) {

        String mqttLogSwitch = MappingCache.getValue("MQTT_LOG_SWITCH");

        if (!"ON".equals(mqttLogSwitch)) {
            return;
        }
        IMqttLogV1InnerServiceSMO saveMqttLogServiceImpl = ApplicationContextFactory.getBean("mqttLogV1InnerServiceSMOImpl", IMqttLogV1InnerServiceSMO.class);
        if (saveMqttLogServiceImpl == null) {
            saveMqttLogServiceImpl = ApplicationContextFactory.getBean(IMqttLogV1InnerServiceSMO.class.getName(), IMqttLogV1InnerServiceSMO.class);
        }

        MqttLogPo mqttLogPo = new MqttLogPo();
        mqttLogPo.setMqttId(GenerateCodeFactory.getGeneratorId("10"));
        mqttLogPo.setBusinessId(taskId);
        mqttLogPo.setBusinessKey(getBusinessKey(topic, param));
        mqttLogPo.setContext(param);
        mqttLogPo.setTopic(topic);
        mqttLogPo.setRemark("发送");
        saveMqttLogServiceImpl.saveMqttLog(mqttLogPo);

    }
}
