package com.java110.dto.mqtt;


import com.java110.bean.dto.PageDto;

import java.io.Serializable;

public class MqttResultDto extends PageDto implements Serializable {

    public MqttResultDto() {
    }

    public MqttResultDto(String hmId, String data) {
        this.hmId = hmId;
        this.data = data;
    }

    public MqttResultDto(String hmId, String topic, String data) {
        this.hmId = hmId;
        this.topic = topic;
        this.data = data;
    }

    private String hmId;
    private String topic;
    private String data;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHmId() {
        return hmId;
    }

    public void setHmId(String hmId) {
        this.hmId = hmId;
    }
}
