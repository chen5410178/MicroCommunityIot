package com.java110.dto.barrier;


import com.java110.bean.dto.PageDto;

import java.io.Serializable;

/**
 * @ClassName FloorDto
 * @Description 门禁数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class BarrierResultDto extends PageDto implements Serializable {

    public BarrierResultDto() {
    }

    public BarrierResultDto(String hmId, String data) {
        this.hmId = hmId;
        this.data = data;
    }

    public BarrierResultDto(String hmId, String topic, String data,String taskId) {
        this.hmId = hmId;
        this.topic = topic;
        this.data = data;
        this.taskId = taskId;
    }

    private String hmId;
    private String topic;
    private String data;
    private String taskId;

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

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
