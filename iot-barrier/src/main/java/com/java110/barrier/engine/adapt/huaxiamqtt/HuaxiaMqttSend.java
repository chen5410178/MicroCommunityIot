package com.java110.barrier.engine.adapt.huaxiamqtt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.barrier.engine.adapt.kafaScreen.KeFaScreenFactory;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.core.factory.MqttLogFactory;
import com.java110.core.utils.DateUtil;
import com.java110.dto.barrier.BarrierDto;

import java.util.Random;

public class HuaxiaMqttSend {


    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static Random random = new Random();

    //todo IO 开闸控制
    public static final String CMD_IO_OUTPUT = "ioOutput";

    //todo rs485Transmit
    public static final String CMD_RS485 = "rs485Transmit";

    //todo 抓拍请求
    public static final String CMD_CAPTURE = "capture";


    //todo 触发识别
    public static final String CMD_TRIGGER = "sfTrigger";



    public static final String TOPIC_PUBLISH = "/device/SN/get";

    public static final String SN = "SN";

    /**
     * 开闸
     * {
     * "cmd": "ioOutput",
     * "msgId": "1562566753001402b681",
     * "utcTs": 143650994,
     * "gpioData": {
     * "ioNum": "io1",
     * "action": "on"
     * }
     * }
     *
     * @param taskId
     * @param carNum
     * @param machineDto
     */
    public static void sendOpenDoor(String taskId, String carNum, BarrierDto machineDto) {
        JSONObject paramIn = new JSONObject();
        paramIn.put("cmd", CMD_IO_OUTPUT);
        paramIn.put("msgId", generateUniqueId());
        paramIn.put("utcTs", DateUtil.getCurrentDate().getTime());

        JSONObject gpioData = new JSONObject();
        gpioData.put("ioNum", "io1");
        gpioData.put("action","on");
        paramIn.put("gpioData", gpioData);

        try {
            MqttFactory.publish(TOPIC_PUBLISH.replace(SN, machineDto.getMachineCode()), paramIn.toJSONString());
            MqttLogFactory.saveSendLog(taskId, carNum, TOPIC_PUBLISH.replace(SN, machineDto.getMachineCode()), paramIn.toJSONString());
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
    }

    /**
     * 关闸闸
     *
     * @param taskId
     * @param carNum
     * @param machineDto
     */
    public static void sendCloseDoor(String taskId, String carNum, BarrierDto machineDto) {
        JSONObject paramIn = new JSONObject();
        paramIn.put("cmd", CMD_IO_OUTPUT);
        paramIn.put("msgId", generateUniqueId());
        paramIn.put("utcTs", DateUtil.getCurrentDate().getTime());

        JSONObject gpioData = new JSONObject();
        gpioData.put("ioNum", "io2");
        gpioData.put("action","on");
        paramIn.put("gpioData", gpioData);

        try {
            MqttFactory.publish(TOPIC_PUBLISH.replace(SN, machineDto.getMachineCode()), paramIn.toJSONString());
            MqttLogFactory.saveSendLog(taskId, carNum, TOPIC_PUBLISH.replace(SN, machineDto.getMachineCode()), paramIn.toJSONString());
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
    }


    /**
     * 播放语音
     * <p>
     * {
     * "cmd": "rs485Transmit",
     * "msgId": "1562566753001402b681",
     * "utcTs": 1562566751,
     * "encodeType": "hex2string",
     * "chn1Data": [
     * {
     * "data": "AA553097BEEF"
     * },
     * {
     * "data": "AA5509307208BEEF"
     * }
     * ],
     * "chn2Data": [
     * {
     * "data": "AA550102056607BEEF"
     * }
     * ]}
     *
     * @param taskId
     * @param carNum
     * @param machineDto
     * @param voice
     */
    public static void pay(String taskId, String carNum, BarrierDto machineDto, String voice) {

        String data = KeFaScreenFactory.getPayData(voice);
        JSONObject paramIn = new JSONObject();
        paramIn.put("cmd", CMD_RS485);
        paramIn.put("msgId", generateUniqueId());
        paramIn.put("utcTs", DateUtil.getCurrentDate().getTime());
        paramIn.put("encodeType", "base64");

        JSONArray chn1Data = new JSONArray();
        JSONObject gpioData = new JSONObject();
        gpioData.put("data", data);
        chn1Data.add(gpioData);
        paramIn.put("chn1Data", chn1Data);
        try {
            MqttFactory.publish(TOPIC_PUBLISH.replace(SN, machineDto.getMachineCode()), paramIn.toJSONString());
            MqttLogFactory.saveSendLog(taskId, carNum, TOPIC_PUBLISH.replace(SN, machineDto.getMachineCode()), paramIn.toJSONString());
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }

    }

    public static void downloadTempTexts(String taskId, String carNum, BarrierDto machineDto, int line, String text) {
        downloadTempTexts(taskId, carNum, machineDto, line, text, (byte) 0x10, (byte) 0x01);
    }

    /**
     * 下发文字
     *
     * @param taskId
     * @param carNum
     * @param machineDto
     * @param line
     * @param text
     */
    public static void downloadTempTexts(String taskId, String carNum, BarrierDto machineDto, int line, String text, byte dt, byte drs) {
        String data = KeFaScreenFactory.getDownloadTempTexts(line, text, dt, drs);
        JSONObject paramIn = new JSONObject();
        paramIn.put("cmd", CMD_RS485);
        paramIn.put("msgId", generateUniqueId());
        paramIn.put("utcTs", DateUtil.getCurrentDate().getTime());
        paramIn.put("encodeType", "base64");

        JSONArray chn1Data = new JSONArray();
        JSONObject gpioData = new JSONObject();
        gpioData.put("data", data);
        chn1Data.add(gpioData);
        paramIn.put("chn1Data", chn1Data);
        try {
            MqttFactory.publish(TOPIC_PUBLISH.replace(SN, machineDto.getMachineCode()), paramIn.toJSONString());
            MqttLogFactory.saveSendLog(taskId, carNum, TOPIC_PUBLISH.replace(SN, machineDto.getMachineCode()), paramIn.toJSONString());
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
    }

    /**
     * 发布抓拍事件
     * {
     * "cmd":"capture",
     * "msgId":"1562566753001402b999",
     * "utcTs":1562566751
     * }
     *
     * @param taskId
     * @param carNum
     * @param machineDto
     */
    public static void triggerImage(String taskId, String carNum, BarrierDto machineDto) {

        JSONObject paramIn = new JSONObject();
        paramIn.put("cmd", CMD_CAPTURE);
        paramIn.put("msgId", generateUniqueId());
        paramIn.put("utcTs", DateUtil.getCurrentDate().getTime());
        try {
            MqttFactory.publish(TOPIC_PUBLISH.replace(SN, machineDto.getMachineCode()), paramIn.toJSONString());
            MqttLogFactory.saveSendLog(taskId, carNum, TOPIC_PUBLISH.replace(SN, machineDto.getMachineCode()), paramIn.toJSONString());
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
    }

    /**
     * {
     * "cmd":"sfTrigger",
     * "msgId":"1562566753001402b681",
     * "utcTs":1562566751
     * }
     * @param taskId
     * @param carNum
     * @param machineDto
     */
    public static void manualTrigger(String taskId, String carNum, BarrierDto machineDto) {
        JSONObject paramIn = new JSONObject();
        paramIn.put("cmd", CMD_TRIGGER);
        paramIn.put("msgId", generateUniqueId());
        paramIn.put("utcTs", DateUtil.getCurrentDate().getTime());
        try {
            MqttFactory.publish(TOPIC_PUBLISH.replace(SN, machineDto.getMachineCode()), paramIn.toJSONString());
            MqttLogFactory.saveSendLog(taskId, carNum, TOPIC_PUBLISH.replace(SN, machineDto.getMachineCode()), paramIn.toJSONString());
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
    }

    public static String generateUniqueId() {
        // 获取当前时间的毫秒时间戳并转换为字符串
        long timestamp = System.currentTimeMillis();


        // 生成7位随机字母和数字组合
        StringBuilder randomStr = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            randomStr.append(ALPHA_NUMERIC_STRING.charAt(random.nextInt(ALPHA_NUMERIC_STRING.length())));
        }

        // 组合成20位的唯一标识符
        return timestamp + randomStr.toString();
    }


}
