package com.java110.hal.mqtt.heartbeat;

import com.java110.core.factory.MqttFactory;
import com.java110.hal.mqtt.subscribe.MqttClientSubscribeFactory;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mqtt 客户端心跳,定时去订阅topic
 * <p>
 * add by wuxw 2023-08-20
 */
public class MqttClientHeartbeat implements Runnable {

    Logger logger = LoggerFactory.getLogger(MqttClientHeartbeat.class);

    public static final long DEFAULT_WAIT_SECOND = 30 * 1000; // 默认30秒执行一次

    @Override
    public void run() {

        while (true) {
            try {
                executeTask();

            } catch (Throwable e) {
                logger.error("连接mqtt 失败", e);
            }
            try {
                Thread.sleep(DEFAULT_WAIT_SECOND);
            }catch (Exception e){
                logger.error("超时终端", e);
            }
        }
    }

    private void executeTask() throws Exception {

        MqttClient mqttClient = MqttFactory.getMqttClient();

        if (mqttClient.isConnected()) {
            logger.debug("CheckMqttConnectThread:=====>mqtt connect success!!!<=====");
            MqttClientSubscribeFactory.subscribe();
            return;
        }

        logger.warn("CheckMqttConnectThread:=====>mqtt disconnected, waiting for auto reconnect<=====");
    }
}
