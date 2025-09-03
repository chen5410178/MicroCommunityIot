package com.java110.hal.mqtt.init;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttTestSubscriber implements MqttCallbackExtended {

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        System.out.println("MQTT连接已建立: " + serverURI);
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("MQTT连接丢失: " + cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("收到MQTT消息 - 主题: " + topic + ", 内容: " + new String(message.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // 消息发送完成回调，这里不需要处理
    }
}