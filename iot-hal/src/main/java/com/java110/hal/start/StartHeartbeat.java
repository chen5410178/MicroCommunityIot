package com.java110.hal.start;

import com.java110.hal.mqtt.heartbeat.CustomMqttMsgThread;
import com.java110.hal.mqtt.heartbeat.MqttClientHeartbeat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 启动心跳
 */
public class StartHeartbeat {

    /**
     * 启动
     */
    public void start() {

        //todo 启动mqtt客户端心跳
        MqttClientHeartbeat mqttClientHeartbeat = new MqttClientHeartbeat();
        Thread mqttThread = new Thread(mqttClientHeartbeat, "MqttClientHeartbeat");
        mqttThread.start();

        //todo 启动mqtt 处理消息 线程
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        executorService.execute(new CustomMqttMsgThread());

    }

    public static void main(String[] args) {
        StartHeartbeat startHeartbeat = new StartHeartbeat();
        startHeartbeat.start();
    }
}
