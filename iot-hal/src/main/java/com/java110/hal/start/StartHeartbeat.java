package com.java110.hal.start;

import com.java110.hal.mqtt.heartbeat.MqttClientHeartbeat;

/**
 * 启动心跳
 */
public class StartHeartbeat {

    /**
     * 启动
     */
    public void start(){
        MqttClientHeartbeat mqttClientHeartbeat = new MqttClientHeartbeat();
        Thread mqttThread = new Thread(mqttClientHeartbeat, "MqttClientHeartbeat");
        mqttThread.start();
    }
}
