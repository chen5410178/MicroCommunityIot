/*
 * Copyright 2017-2020 吴学文 and java110 team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.java110.barrier.engine.adapt.zhenshiWebMqtt;

import com.alibaba.fastjson.JSONObject;
import com.java110.barrier.engine.ICallCarService;
import com.java110.barrier.engine.ICarMachineProcess;
import com.java110.barrier.engine.IInOutCarTextEngine;
import com.java110.barrier.engine.adapt.BaseMachineAdapt;
import com.java110.barrier.engine.adapt.zhenshiMqtt.JinjieScreenMqttFactory;
import com.java110.barrier.engine.adapt.zhenshiMqtt.ZhenshiMqttSend;
import com.java110.barrier.engine.adapt.zhenshiMqtt.http.HttpClient;
import com.java110.barrier.engine.adapt.zhenshiMqtt.http.HttpMethod;
import com.java110.barrier.engine.adapt.zhenshiMqtt.http.HttpRequest;
import com.java110.barrier.engine.adapt.zhenshiMqtt.http.HttpResponse;
import com.java110.bean.ResultVo;
import com.java110.core.cache.CommonCache;
import com.java110.core.cache.MappingCache;
import com.java110.core.constant.MappingConstant;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Base64Convert;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.parking.ParkingAreaTextDto;
import com.java110.dto.parking.ResultParkingAreaTextDto;
import com.java110.intf.barrier.IBarrierV1InnerServiceSMO;
import com.java110.intf.barrier.IManualOpenDoorLogV1InnerServiceSMO;
import com.java110.po.barrier.BarrierPo;
import com.java110.po.manualOpenDoorLog.ManualOpenDoorLogPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 臻识 车辆道闸摄像头接口协议
 * <p>
 * 相关 网站地址 http://vzenith.com/case/ivs-fc-productpage/
 */
@Service("zhenshiWebMqttCarMachineAdapt")
public class ZhenshiWebMqttCarMachineAdapt extends BaseMachineAdapt implements ICarMachineProcess {
    Logger logger = LoggerFactory.getLogger(ZhenshiWebMqttCarMachineAdapt.class);

    private static final String TOPIC_IVS_RESULT = "/device/message/up/ivs_result";//订阅识别结果
    private static final String TOPIC_SNAPSHOT = "/device/message/up/snapshot";//订阅抓图结果
    private static final String TOPIC_IVS_KEEP_ALIVE = "/device/message/up/keep_alive";//订阅心跳
//    private static final String TOPIC_IVS_RESULT = "/device/message/up/ivs_result";//订阅识别结果
//    private static final String TOPIC_IVS_RESULT = "/device/message/up/ivs_result";//订阅识别结果


    @Autowired
    private IBarrierV1InnerServiceSMO barrierV1InnerServiceSMOImpl;

    @Autowired
    private ICallCarService callCarServiceImpl;

    @Autowired
    private IManualOpenDoorLogV1InnerServiceSMO manualOpenDoorLogServiceImpl;



    /**
     * {"AlarmInfoPlate": {"channel": 0,"deviceName": "","ipaddr": "192.168.1.100","serialno": "5cdc5841-3d038816","heartbeat": 1}}
     *
     * @param topic
     * @param s
     */
    @Override
    public void mqttMessageArrived(String taskId, String topic, String s) {

        System.out.println("s=" + s);
//        try {
//            System.out.println("s=" + new String(s.getBytes(StandardCharsets.UTF_8), "GB2312"));
//            s = new String(s.getBytes(StandardCharsets.UTF_8), "GBK");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        System.out.println("s utf-8 =" + s);
        JSONObject paramIn = JSONObject.parseObject(s);


        switch (topic) {
            case TOPIC_IVS_KEEP_ALIVE: // todo 心跳
                doHeartBeat(taskId, paramIn);
                break;
            case TOPIC_IVS_RESULT:
                doResult(taskId, paramIn);
                break;
            case TOPIC_SNAPSHOT:
                doTriggerImage(taskId, paramIn);
                break;

        }

    }


    /**
     * 触发识别
     * {
     * "id": "NYGtiXpPy5ratyzU", # 消息 ID，用于关联具体消息
     * "sn": "12345678-87654321", # 设备序列号
     * "name": "ivs_trigger", # 消息名称
     * "version": "1.0", # 消息版本，目前都填 1.0
     * "timestamp": 1597285865, # 时间戳
     * "payload": {
     * "type": "ivs_trigger",
     * "body": {}
     * }
     * }
     *
     * @param machineDto
     */
    @Override
    public void manualTrigger(BarrierDto machineDto) {
        JSONObject data = JSONObject.parseObject("{\n" +
                "\"Response_AlarmInfoPlate\": \n" +
                "{\n" +
                "\"manualTrigger\" : \"ok\"" +
                "}\n" +
                "}");
        String taskId = GenerateCodeFactory.getUUID();
        ZhenshiMqttSend.sendCmd(taskId, "无", machineDto, "SNAPSHOT", data.toJSONString());
    }

    /**
     * 触发抓拍
     * <p>
     * {
     * "id": "NYGtiXpPy5ratyzU", # 消息 ID，用于关联具体消息
     * "sn": "12345678-87654321", # 设备序列号
     * "name": "snapshot", # 消息名称
     * "version": "1.0", # 消息版本，目前都填 1.0
     * "timestamp": 1597285865, # 时间戳
     * "payload": {
     * "type":"snapshot",
     * "body": {}
     * 臻识科技版权所有
     * 车牌识别一体机 Mqtt 对接协议
     * }
     * }
     *
     * @param machineDto
     * @param manualOpenDoorLogDto
     */
    @Override
    public void triggerImage(BarrierDto machineDto, ManualOpenDoorLogPo manualOpenDoorLogDto) {
        CommonCache.setValue("triggerImage_" + machineDto.getMachineCode(), manualOpenDoorLogDto.getLogId());
        ZhenshiWebMqttSend.triggerImage(manualOpenDoorLogDto.getLogId(), "手工开闸", machineDto);
    }

    @Override
    public ResultVo getCloudFlvVideo(BarrierDto barrierDto) {

        String accessKey = MappingCache.getValue("VZICLOUD", "AccessKey");
        String AccessKeySecret = MappingCache.getValue("VZICLOUD", "AccessKeySecret");
        String baseUrl = "https://open.vzicloud.com";
        HttpClient client = new HttpClient(baseUrl, accessKey, AccessKeySecret);
        String path = "/openapi/v1/stp/user/devices/vurl";

        HttpRequest request = new HttpRequest(HttpMethod.GET, path);
        request.addParam("sn", barrierDto.getMachineCode());
        request.addParam("type", "flv");
        request.addParam("proto", "ws");

        HttpResponse response = client.doRequest(request);

        int status = response.getStatus();
        if (status != 200) {
            // throw new IllegalArgumentException("调用臻识云异常");
            return new ResultVo(ResultVo.CODE_ERROR, "调用臻识云异常:" + response.getBodyStr());
        }
        String result = response.getBodyStr();
        return new ResultVo(ResultVo.CODE_OK, ResultVo.MSG_OK, JSONObject.parseObject(result));
    }

    @Override
    public ResultVo getDeviceWebUrl(BarrierDto barrierDto) {
        String accessKey = MappingCache.getValue("VZICLOUD", "AccessKey");
        String AccessKeySecret = MappingCache.getValue("VZICLOUD", "AccessKeySecret");
        String baseUrl = "https://open.vzicloud.com";
        HttpClient client = new HttpClient(baseUrl, accessKey, AccessKeySecret);
        String path = "/openapi/v1/stp/user/devices/pdns/web";

        HttpRequest request = new HttpRequest(HttpMethod.GET, path);
        request.addParam("sn", barrierDto.getMachineCode());
        request.addParam("type", "json");

        HttpResponse response = client.doRequest(request);
        int status = response.getStatus();
        if (status != 200) {
            return new ResultVo(ResultVo.CODE_ERROR, "调用臻识云获取设备网页异常:" + response.getBodyStr());
        }
        String result = response.getBodyStr();
        return new ResultVo(ResultVo.CODE_OK, ResultVo.MSG_OK, JSONObject.parseObject(result));
    }


    /**
     * 车辆识别结果
     * {
     * "id": "iUA4fO7yCGrHZO2C",
     * "bv": 12378,
     * "sn": "265e1040-85e01fb7",
     * "name": "ivs_result",
     * "version": "1.0",
     * "timestamp": 1621926595,
     * "payload": {
     * "AlarmInfoPlate": {
     * "channel": 0,
     * "deviceName": "0001",
     * "ipaddr": "192.168.6.94",
     * "result": {
     * "PlateResult": {
     * "bright": 0,
     * "carBright": 0,
     * "carColor": 0,
     * "car_brand": {
     * "brand": 42,
     * 臻识科技版权所有
     * 车牌识别一体机 Mqtt 对接协议
     * "type": 255,
     * "year": 65535
     * },
     * "car_location": {
     * "RECT": {
     * "bottom": 630,
     * "left": 850,
     * "right": 1213,
     * "top": 427
     * }
     * },
     * "plates": [{
     * "binimg_path": "",
     * "binimageSize": 0,
     * "binimg_content": "",
     * "clipImgSize": 0,
     * "image_path": "0",
     * "color": 4,
     * "content": "",
     * "license": "",
     * "plate_width": 304,
     * "is_danger": 0,
     * "pos": {
     * "bottom": 422,
     * "left": 432,
     * "right": 634,
     * "top": 322
     * },
     * "type": 14
     * }],
     * "clean_time": 0,
     * "colorType": 1,
     * "colorValue": 0,
     * "confidence": 100,
     * "direction": 0,
     * "feature_code": true,
     * "gioouts": [],
     * "imageFragmentPath": "xxx",
     * "small_image_content": "",
     * "imagePath": "xxxxxxxxxxxxx",
     * "full_image_content": "",
     * "is_fake_plate": 0,
     * "isoffline": 0,
     * "license": "6LWjR1o3UzEz",
     * 臻识科技版权所有
     * 车牌识别一体机 Mqtt 对接协议
     * "license_ext_type": 0,
     * "location": {
     * "RECT": {
     * "bottom": 555,
     * "left": 954,
     * "right": 1109,
     * "top": 502
     * }
     * },
     * "plate_distance": 0,
     * "plate_true_width": 0,
     * "plateid": 594785,
     * "timeStamp": {
     * "Timeval": {
     * "decday": 11,
     * "dechour": 16,
     * "decmin": 30,
     * "decmon": 8,
     * "decsec": 19,
     * "decyear": 2021,
     * "sec": 1628670619,
     * "usec": 539956
     * }
     * },
     * "timeUsed": 3879571,
     * "triggerType": 4,
     * "type": 1
     * }
     * },
     * "serialno": "8fcd1c4e-20ca9ecb",
     * "user_data": "5Lit5Zu9"
     * }
     * }
     * }
     *
     * @param reqData
     */
    private void doResult(String taskId, JSONObject reqData) {
        Date startTime = DateUtil.getCurrentDate();
        String sn = reqData.getString("sn");
        BarrierDto machineDto = new BarrierDto();
        machineDto.setMachineCode(sn);
        List<BarrierDto> machineDtos = barrierV1InnerServiceSMOImpl.queryBarriers(machineDto);
        if (ListUtil.isNull(machineDtos)) {
            throw new IllegalArgumentException("设备不存在" + sn);
        }

        machineDto = machineDtos.get(0);

        JSONObject payload = reqData.getJSONObject("payload");
        JSONObject AlarmInfoPlate = payload.getJSONObject("AlarmInfoPlate");
        JSONObject result = AlarmInfoPlate.getJSONObject("result");
        JSONObject plateResult = result.getJSONObject("PlateResult");
        try {
            String type = plateResult.getString("colorType");
            if(StringUtil.isEmpty(type)){
                type = "1";
            }
            String license = plateResult.getString("license");
            license = Base64Convert.decoder(license);
            String imagePath = plateResult.getString("imagePath");
            if (!StringUtil.isEmpty(imagePath)) {
                imagePath = Base64Convert.decoder(imagePath);
                // 图片路径中带有 Expires 和 Signature 等查询参数，Expires 过期后无法加载，这里去掉查询参数
                int pos = imagePath.indexOf("?");
                if (pos != -1) {
                    imagePath = imagePath.substring(0, pos);
                }
                //String imagePath = ImageFactory.getBase64ByImgUrl(MappingCacheFactory.getValue("OSS_URL") + plateResult.getString("imagePath"));
                machineDto.setPhotoJpg(imagePath);
            }
            String imageFragmentPath = plateResult.getString("imageFragmentPath");

            if (!StringUtil.isEmpty(imageFragmentPath)) {
                imageFragmentPath = Base64Convert.decoder(imageFragmentPath);
                // 图片路径中带有 Expires 和 Signature 等查询参数，Expires 过期后无法加载，这里去掉查询参数
                int pos = imageFragmentPath.indexOf("?");
                if (pos != -1) {
                    imageFragmentPath = imageFragmentPath.substring(0, pos);
                }
                //String imagePath = ImageFactory.getBase64ByImgUrl(MappingCacheFactory.getValue("OSS_URL") + plateResult.getString("imagePath"));
                machineDto.setPhotoFragment(imageFragmentPath);
            }


            IInOutCarTextEngine inOutCarTextEngine = ApplicationContextFactory.getBean("zhenshiMqttInOutCarTextEngine", IInOutCarTextEngine.class);

            ResultParkingAreaTextDto resultParkingAreaTextDto = callCarServiceImpl.ivsResult(type, license, machineDto, inOutCarTextEngine);

            System.out.println("------------------------------------------------------业务处理耗时：" + (DateUtil.getCurrentDate().getTime() - startTime.getTime()));

            if (ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS == resultParkingAreaTextDto.getCode()
                    || ResultParkingAreaTextDto.CODE_MONTH_CAR_SUCCESS == resultParkingAreaTextDto.getCode()
                    || ResultParkingAreaTextDto.CODE_TEMP_CAR_SUCCESS == resultParkingAreaTextDto.getCode()
                    || ResultParkingAreaTextDto.CODE_FREE_CAR_OUT_SUCCESS == resultParkingAreaTextDto.getCode()
                    || ResultParkingAreaTextDto.CODE_MONTH_CAR_OUT_SUCCESS == resultParkingAreaTextDto.getCode()
                    || ResultParkingAreaTextDto.CODE_TEMP_CAR_OUT_SUCCESS == resultParkingAreaTextDto.getCode()
                    || ResultParkingAreaTextDto.CODE_CAR_OUT_SUCCESS == resultParkingAreaTextDto.getCode()
            ) {
                // Thread.sleep(500); //这里停一秒"
                ZhenshiWebMqttSend.sendOpenDoor(taskId, license, machineDto);
            }

            Thread.sleep(400); //这里停一秒
            ZhenshiWebMqttSend.pay(taskId, license, machineDto, resultParkingAreaTextDto.getVoice());

            if (!StringUtil.isEmpty(resultParkingAreaTextDto.getText1())) {
                Thread.sleep(400); //这里停一秒
                ZhenshiWebMqttSend.downloadTempTexts(taskId, license, machineDto, 0, resultParkingAreaTextDto.getText1());
            }
            if (!StringUtil.isEmpty(resultParkingAreaTextDto.getText2())) {
                Thread.sleep(400); //这里停一秒
                ZhenshiWebMqttSend.downloadTempTexts(taskId, license, machineDto, 1, resultParkingAreaTextDto.getText2(), (byte) 0x00, (byte) 0x03);
            }
            if (!StringUtil.isEmpty(resultParkingAreaTextDto.getText3())) {
                Thread.sleep(400); //这里停一秒
                ZhenshiWebMqttSend.downloadTempTexts(taskId, license, machineDto, 2, resultParkingAreaTextDto.getText3());
            }
            if (!StringUtil.isEmpty(resultParkingAreaTextDto.getText4())) {
                Thread.sleep(400); //这里停一秒
                ZhenshiWebMqttSend.downloadTempTexts(taskId, license, machineDto, 3, resultParkingAreaTextDto.getText4());
            }


        } catch (Exception e) {
            logger.error("开门异常", e);
        }

    }

    /**
     * 心跳协议
     * {
     * "id": "b1Y1wdVEHu1xFwdU",
     * "bv": 12378,
     * "sn": "265e1040-85e01fb7",
     * "name": "keep_alive",
     * "version": "1.0",
     * "timestamp": 1621927036,
     * "payload": {
     * "body": {
     * "timestamp": 1621927036
     * }
     * }
     * }
     *
     * @param reqData
     */
    private void doHeartBeat(String taskId, JSONObject reqData) {

        String machineCode = reqData.getString("sn");

        BarrierDto machineDto = new BarrierDto();
        machineDto.setMachineCode(machineCode);
        List<BarrierDto> machineDtos = barrierV1InnerServiceSMOImpl.queryBarriers(machineDto);
        if (ListUtil.isNull(machineDtos)) {
            throw new IllegalArgumentException("设备不存在" + machineCode);
        }

        String heartBeatTime = null;
        heartBeatTime = DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A);
        BarrierPo barrierPo = new BarrierPo();
        barrierPo.setMachineId(machineDtos.get(0).getMachineId());
        barrierPo.setHeartbeatTime(heartBeatTime);
        barrierV1InnerServiceSMOImpl.updateBarrier(barrierPo);
    }


    /**
     * 抓拍图片
     * <p>
     * {
     * "id": "b1Y1wdVEHu1xFwdU",
     * "bv": 12378,
     * "sn": "265e1040-85e01fb7",
     * "name": "snapshot",
     * "version": "1.0",
     * "timestamp": 1621927036,
     * "payload": {
     * "state_code":"",
     * "imageformat":"jpg",
     * "imgPath":"",
     * "image_content":""
     * }
     * }
     *
     * @param taskId
     * @param paramIn
     */
    private void doTriggerImage(String taskId, JSONObject paramIn) {
        String sn = paramIn.getString("sn");
        String id = CommonCache.getAndRemoveValue("triggerImage_" + sn);
        if (StringUtil.isEmpty(id)) {
            return;
        }

        JSONObject payload = paramIn.getJSONObject("payload");

        ManualOpenDoorLogPo manualOpenDoorLogPo = new ManualOpenDoorLogPo();
        manualOpenDoorLogPo.setLogId(id);
        String imagePath = payload.getString("imgPath");
        if (!StringUtil.isEmpty(imagePath)) {
            imagePath = Base64Convert.decoder(imagePath);
            // 图片路径中带有 Expires 和 Signature 等查询参数，Expires 过期后无法加载，这里去掉查询参数
            int pos = imagePath.indexOf("?");
            if (pos != -1) {
                imagePath = imagePath.substring(0, pos);
            }
            manualOpenDoorLogPo.setPhotoJpg(imagePath);
        }
        manualOpenDoorLogServiceImpl.updateManualOpenDoorLog(manualOpenDoorLogPo);
    }


    @Override
    public void restartMachine(BarrierDto machineDto) {


    }

    /**
     * 开门
     * {
     * "id": "NYGtiXpPy5ratyzU", # 消息 ID，用于关联具体消息
     * "sn": "12345678-87654321", # 设备序列号
     * "name": "gpio_out", # 消息名称
     * "version": "1.0", # 消息版本，目前都填 1.0
     * "timestamp": 1597285865, # 时间戳
     * "payload": {
     * "type": "gpio_out",
     * "body": {
     * "delay": 500,
     * "io": 0,
     * "value": 2
     * }
     * }
     * }
     *
     * @param machineDto         硬件信息
     * @param parkingAreaTextDto
     */
    @Override
    public void openDoor(BarrierDto machineDto, ParkingAreaTextDto parkingAreaTextDto) {
        try {
            String taskId = GenerateCodeFactory.getUUID();
            String carNum = "手工开门";
            if (parkingAreaTextDto != null && !StringUtil.isEmpty(parkingAreaTextDto.getCarNum())) {
                carNum = parkingAreaTextDto.getCarNum();
            }

            if (parkingAreaTextDto == null) {
                ZhenshiWebMqttSend.pay(taskId, carNum, machineDto, "欢迎光临");
                Thread.sleep(400); //这里停一秒
                ZhenshiWebMqttSend.downloadTempTexts(taskId, carNum, machineDto, 0, "欢迎光临");
                Thread.sleep(500); //这里停一秒
                ZhenshiWebMqttSend.sendOpenDoor(taskId, carNum, machineDto);
                return;
            }
            ZhenshiWebMqttSend.pay(taskId, carNum, machineDto, parkingAreaTextDto.getVoice());
            if (!StringUtil.isEmpty(parkingAreaTextDto.getText1())) {
                ZhenshiWebMqttSend.downloadTempTexts(taskId, carNum, machineDto, 0, parkingAreaTextDto.getText1());
            }
            if (!StringUtil.isEmpty(parkingAreaTextDto.getText2())) {
                Thread.sleep(400); //这里停一秒
                ZhenshiWebMqttSend.downloadTempTexts(taskId, carNum, machineDto, 1, parkingAreaTextDto.getText2(), (byte) 0x00, (byte) 0x03);

            }
            if (!StringUtil.isEmpty(parkingAreaTextDto.getText3())) {
                Thread.sleep(400); //这里停一秒
                ZhenshiWebMqttSend.downloadTempTexts(taskId, carNum, machineDto, 2, parkingAreaTextDto.getText3());

            }
            if (!StringUtil.isEmpty(parkingAreaTextDto.getText4())) {
                Thread.sleep(400); //这里停一秒
                ZhenshiWebMqttSend.downloadTempTexts(taskId, carNum, machineDto, 3, parkingAreaTextDto.getText4());
            }
            Thread.sleep(500); //这里停一秒
            ZhenshiWebMqttSend.sendOpenDoor(taskId, carNum, machineDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeDoor(BarrierDto machineDto, ParkingAreaTextDto parkingAreaTextDto) {
        String taskId = GenerateCodeFactory.getUUID();
        ZhenshiWebMqttSend.sendCloseDoor(taskId, "手动关门", machineDto);

    }



}
