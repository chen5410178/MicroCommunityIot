package com.java110.barrier.engine.adapt.hc;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.dto.barrier.BarrierDto;

public class TTsViewFactory {

    public static final String SN = "{sn}";
    // todo 请求topic
    public static final String TOPIC_REQ = "hc/barrier/request/{sn}";

    //todo 返回 topic
    public static final String TOPIC_RES = "hc/barrier/response";

    public static final String CMD_PLAY_TTS = "playTts";// todo 播放语音

    public static final String CMD_VIEW_TEXT = "viewText";// todo 显示文字

    public static void pay( String carNum, BarrierDto barrierDto,String tts){

        String taskId = GenerateCodeFactory.getGeneratorId("11");
        JSONObject param = new JSONObject();
        param.put("cmd", CMD_PLAY_TTS);
        param.put("taskId", taskId);
        param.put("text", tts);

        MqttFactory.publish(TOPIC_REQ.replace(SN, barrierDto.getMachineCode()), param.toJSONString());
    }


    public static void downloadTempTexts( String carNum, BarrierDto barrierDto,int line,String text){

        String taskId = GenerateCodeFactory.getGeneratorId("11");
        JSONObject param = new JSONObject();
        param.put("cmd", CMD_PLAY_TTS);
        param.put("taskId", taskId);
        param.put("line", line);
        param.put("text", text);

        MqttFactory.publish(TOPIC_REQ.replace(SN, barrierDto.getMachineCode()), param.toJSONString());
    }
}
