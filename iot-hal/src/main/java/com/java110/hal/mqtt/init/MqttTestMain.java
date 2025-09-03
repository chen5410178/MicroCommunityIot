package com.java110.hal.mqtt.init;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttTestMain {
    
    public static void main(String[] args) {
        String broker = "tcp://xx:1883"; // MQTT broker地址，请根据实际情况修改
        String clientId = "Java110TestClient"; // 客户端ID
        //String topic = "$queue//device/message/up/ivs_result"; // 要订阅的主题
        String topic = "$share/g1//device/message/up/ivs_result"; // 要订阅的主题
        String username = "wuxw";
        String password = "wuxw2015";

        try {
            // 创建MQTT客户端
            MqttClient client = new MqttClient(broker, clientId, new MemoryPersistence());
            
            // 设置连接选项
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setConnectionTimeout(30);
            options.setKeepAliveInterval(60);
            
            // 设置回调
            client.setCallback(new MqttTestSubscriber());
            
            // 连接到MQTT broker
            System.out.println("连接到MQTT broker: " + broker);
            client.connect(options);
            
            // 订阅主题
            System.out.println("订阅主题: " + topic);
            client.subscribe(topic, 1);
            
            System.out.println("已成功订阅主题，等待消息...");
            System.out.println("按 CTRL+C 退出");
            
            // 保持程序运行
            Thread.sleep(Long.MAX_VALUE);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}