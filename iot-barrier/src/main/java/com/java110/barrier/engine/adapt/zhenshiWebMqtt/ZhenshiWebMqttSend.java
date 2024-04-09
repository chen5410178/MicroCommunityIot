package com.java110.barrier.engine.adapt.zhenshiWebMqtt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.barrier.engine.adapt.kafaScreen.KeFaScreenFactory;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.core.factory.MqttLogFactory;
import com.java110.core.utils.DateUtil;
import com.java110.dto.barrier.BarrierDto;

/**
 * 臻识 道闸摄像头 字节和 字符串转换处理类
 * <p>
 * add by 吴学文 2021-01-04
 */
public class ZhenshiWebMqttSend {


    // 开闸关闸
    public static final String TOPIC_GPIO = "/SN/device/message/down/gpio_out";

    // 串口数据转发
    public static final String TOPIC_SERIAL_DATA = "/SN/device/message/down/serial_data";

    // 发布抓拍事件
    public static final String TOPIC_SNAPSHOT = "/SN/device/message/down/snapshot";




    public static final String SN = "SN";

    /**
     * 开闸
     *
     * @param taskId
     * @param carNum
     * @param machineDto
     */
    public static void sendOpenDoor(String taskId, String carNum, BarrierDto machineDto) {
        JSONObject paramIn = new JSONObject();
        paramIn.put("id", taskId);
        paramIn.put("sn", machineDto.getMachineCode());
        paramIn.put("name", "gpio_out");
        paramIn.put("version", "1.0");
        paramIn.put("timestamp", DateUtil.getCurrentDate().getTime());
        JSONObject payload = new JSONObject();
        payload.put("type", "gpio_out");
        JSONObject body = new JSONObject();
        body.put("delay", 1000);
        body.put("io", 0);
        body.put("value", 2);
        payload.put("body", body);
        paramIn.put("payload", payload);

        try {
            MqttFactory.publish(TOPIC_GPIO.replace(SN, machineDto.getMachineCode()), paramIn.toJSONString());
            MqttLogFactory.saveSendLog(taskId, carNum, TOPIC_GPIO.replace(SN, machineDto.getMachineCode()), paramIn.toJSONString());
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
        paramIn.put("id", taskId);
        paramIn.put("sn", machineDto.getMachineCode());
        paramIn.put("name", "gpio_out");
        paramIn.put("version", "1.0");
        paramIn.put("timestamp", DateUtil.getCurrentDate().getTime());
        JSONObject payload = new JSONObject();
        payload.put("type", "gpio_out");
        JSONObject body = new JSONObject();
        body.put("delay", 1000);
        body.put("io", 1);
        body.put("value", 2);
        payload.put("body", body);
        paramIn.put("payload", payload);

        try {
            MqttFactory.publish(TOPIC_GPIO.replace(SN, machineDto.getMachineCode()), paramIn.toJSONString());
            MqttLogFactory.saveSendLog(taskId, carNum, TOPIC_GPIO.replace(SN, machineDto.getMachineCode()), paramIn.toJSONString());
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
    }



    /**
     * 播放语音
     * <p>
     * {
     * "id": "NYGtiXpPy5ratyzU", # 消息 ID，用于关联具体消息
     * "sn": "12345678-87654321", # 设备序列号
     * "name": "serial_data", # 消息名称
     * "version": "1.0", # 消息版本，目前都填 1.0
     * "timestamp": 1597285865, # 时间戳
     * "payload": {
     * "type": "serial_data",
     * "body": {
     * "serialData": [
     * {
     * "serialChannel": 0,
     * "data": "123",
     * "dataLen": 3
     * },
     * {
     * "serialChannel": 1,
     * "data": "4567",
     * "dataLen": 4
     * }
     * ]
     * }
     * }
     * }
     *
     * @param taskId
     * @param carNum
     * @param machineDto
     * @param voice
     */
    public static void pay(String taskId, String carNum, BarrierDto machineDto, String voice) {

        String data = KeFaScreenFactory.getPayData(voice);
        JSONObject paramIn = new JSONObject();
        paramIn.put("id", taskId);
        paramIn.put("sn", machineDto.getMachineCode());
        paramIn.put("name", "serial_data");
        paramIn.put("version", "1.0");
        paramIn.put("timestamp", DateUtil.getCurrentDate().getTime());
        JSONObject payload = new JSONObject();
        payload.put("type", "serial_data");
        JSONObject body = new JSONObject();

        JSONArray serialData = new JSONArray();
        JSONObject sData = new JSONObject();
        sData.put("serialChannel", 0);
        sData.put("data", data);
        sData.put("dataLen", data.length());
        serialData.add(sData);
        body.put("serialData", serialData);
        payload.put("body", body);
        paramIn.put("payload", payload);
        try {
            MqttFactory.publish(TOPIC_SERIAL_DATA.replace(SN, machineDto.getMachineCode()), paramIn.toJSONString());
            MqttLogFactory.saveSendLog(taskId, carNum, TOPIC_SERIAL_DATA.replace(SN, machineDto.getMachineCode()), paramIn.toJSONString());
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }

    }

    public static void downloadTempTexts(String taskId, String carNum, BarrierDto machineDto, int line, String text){
        downloadTempTexts(taskId,carNum,machineDto,line,text,(byte) 0x10, (byte) 0x01);
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
    public static void downloadTempTexts(String taskId, String carNum, BarrierDto machineDto, int line, String text,byte dt, byte drs) {
        String data = KeFaScreenFactory.getDownloadTempTexts(line, text,dt,drs);
        JSONObject paramIn = new JSONObject();
        paramIn.put("id", taskId);
        paramIn.put("sn", machineDto.getMachineCode());
        paramIn.put("name", "serial_data");
        paramIn.put("version", "1.0");
        paramIn.put("timestamp", DateUtil.getCurrentDate().getTime());
        JSONObject payload = new JSONObject();
        payload.put("type", "serial_data");
        JSONObject body = new JSONObject();

        JSONArray serialData = new JSONArray();
        JSONObject sData = new JSONObject();
        sData.put("serialChannel", 0);
        sData.put("data", data);
        sData.put("dataLen", data.length());
        serialData.add(sData);
        body.put("serialData", serialData);
        payload.put("body", body);
        paramIn.put("payload", payload);
        try {
            MqttFactory.publish(TOPIC_SERIAL_DATA.replace(SN, machineDto.getMachineCode()), paramIn.toJSONString());
            MqttLogFactory.saveSendLog(taskId, carNum, TOPIC_SERIAL_DATA.replace(SN, machineDto.getMachineCode()), paramIn.toJSONString());
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
    }

    /**
     * 发布抓拍事件
     * {
     *  "id": "NYGtiXpPy5ratyzU", # 消息 ID，用于关联具体消息
     *  "sn": "12345678-87654321", # 设备序列号
     *  "name": "snapshot", # 消息名称
     *  "version": "1.0", # 消息版本，目前都填 1.0
     *  "timestamp": 1597285865, # 时间戳
     *  "payload": {
     *  "type":"snapshot",
     *  "body": {}
     * 臻识科技版权所有
     *  车牌识别一体机 Mqtt 对接协议
     *  }
     * }
     * @param taskId
     * @param carNum
     * @param machineDto
     */
    public static void triggerImage(String taskId, String carNum, BarrierDto machineDto) {

        JSONObject paramIn = new JSONObject();
        paramIn.put("id", taskId);
        paramIn.put("sn", machineDto.getMachineCode());
        paramIn.put("name", "snapshot");
        paramIn.put("version", "1.0");
        paramIn.put("timestamp", DateUtil.getCurrentDate().getTime());
        JSONObject payload = new JSONObject();
        payload.put("type", "snapshot");
        JSONObject body = new JSONObject();
        payload.put("body", body);
        paramIn.put("payload", payload);
        try {
            MqttFactory.publish(TOPIC_SNAPSHOT.replace(SN, machineDto.getMachineCode()), paramIn.toJSONString());
            MqttLogFactory.saveSendLog(taskId, carNum, TOPIC_SNAPSHOT.replace(SN, machineDto.getMachineCode()), paramIn.toJSONString());
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
    }
}
