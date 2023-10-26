package com.java110.hal.mqtt.queue;

import com.java110.dto.mqtt.MqttMsgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MqttQueue {

    private static final Logger log = LoggerFactory.getLogger(MqttQueue.class);


    private static final BlockingQueue<MqttMsgDto> msgs = new LinkedBlockingQueue<MqttMsgDto>(100);

    /**
     * 添加消息到队列
     *
     * @param topic
     * @param msg
     */
    public static void addMsg(String topic, String msg, String taskId) {
        try {
            MqttMsgDto mqttMsgDto = new MqttMsgDto();
            mqttMsgDto.setTopic(topic);
            mqttMsgDto.setMsg(msg);
            mqttMsgDto.setTaskId(taskId);
            msgs.offer(mqttMsgDto, 3, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("写入队列失败", e);
            e.printStackTrace();
        }

        log.debug("当前mqtt消息队列数量：" + msgs.size());
    }

    public static MqttMsgDto getMqttMsg() {
        try {
            return msgs.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
