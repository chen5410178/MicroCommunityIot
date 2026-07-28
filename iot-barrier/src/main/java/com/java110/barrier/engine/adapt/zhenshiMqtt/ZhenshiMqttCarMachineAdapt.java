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
package com.java110.barrier.engine.adapt.zhenshiMqtt;

import com.alibaba.fastjson.JSONObject;
import com.java110.barrier.engine.ICallCarService;
import com.java110.barrier.engine.ICarMachineProcess;
import com.java110.barrier.engine.IInOutCarTextEngine;
import com.java110.barrier.engine.adapt.BaseMachineAdapt;
import com.java110.barrier.engine.adapt.zhenshiMqtt.http.HttpClient;
import com.java110.barrier.engine.adapt.zhenshiMqtt.http.HttpMethod;
import com.java110.barrier.engine.adapt.zhenshiMqtt.http.HttpRequest;
import com.java110.barrier.engine.adapt.zhenshiMqtt.http.HttpResponse;
import com.java110.bean.ResultVo;
import com.java110.core.cache.MappingCache;
import com.java110.core.constant.MappingConstant;
import com.java110.core.constant.ResponseConstant;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.MachineHeartbeatDto;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.manualOpenDoorLog.ManualOpenDoorLogDto;
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
@Service("zhenshiMqttCarMachineAdapt")
public class ZhenshiMqttCarMachineAdapt extends BaseMachineAdapt implements ICarMachineProcess {
    Logger logger = LoggerFactory.getLogger(ZhenshiMqttCarMachineAdapt.class);

    private static final String CMD_GET_RTSP_URI = "get_rtsp_uri";//获取视频连接
    private static final String CMD_HEARTBEAT = "heartbeat";//获取视频连接
    private static final String CMD_RESULT = "ivs_result";// 车牌识别结果

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

        //手工开门图片抓拍
        if (paramIn.containsKey("cmd") && "TriggerImage".equals(paramIn.getString("cmd"))) {
            ManualOpenDoorLogPo manualOpenDoorLogPo = new ManualOpenDoorLogPo();
            manualOpenDoorLogPo.setLogId(paramIn.getString("msgId"));
            manualOpenDoorLogPo.setPhotoJpg(MappingCache.getValue(MappingConstant.FILE_DOMAIN, "OSS_URL") + paramIn.getString("oss_path"));
            manualOpenDoorLogServiceImpl.updateManualOpenDoorLog(manualOpenDoorLogPo);
            return;
        }

        if (!paramIn.containsKey("AlarmInfoPlate")) {
            return;
        }

        JSONObject alarmInfoPlateObj = paramIn.getJSONObject("AlarmInfoPlate");

        String serialno = alarmInfoPlateObj.getString("serialno");
        if (StringUtil.isEmpty(serialno)) {
            return;
        }

        BarrierDto machineDto = new BarrierDto();
        machineDto.setMachineCode(serialno);
        List<BarrierDto> machineDtos = barrierV1InnerServiceSMOImpl.queryBarriers(machineDto);
        if (machineDtos == null || machineDtos.size() < 1) {
            throw new IllegalArgumentException("设备不存在" + serialno);
        }
        if (alarmInfoPlateObj.containsKey("heartbeat")) {
            alarmInfoPlateObj.put("cmd", CMD_HEARTBEAT);
        } else {
            alarmInfoPlateObj.put("cmd", CMD_RESULT);
        }
        dealCmd(taskId, alarmInfoPlateObj, machineDtos.get(0));
    }

    /**
     * 触发识别
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
        ZhenshiMqttSend.sendCmd(taskId, "无", machineDto, data.toJSONString());
    }

    @Override
    public void triggerImage(BarrierDto machineDto, ManualOpenDoorLogPo manualOpenDoorLogDto) {
        JSONObject data = JSONObject.parseObject("{\n" +
                "\"Response_AlarmInfoPlate\": \n" +
                "    {\n" +
                "\"TriggerImage\":\"ok\",\n" +
                "\"msgId\":\"" + manualOpenDoorLogDto.getLogId() + "\"\n" +
                "    }\n" +
                "}");
        String taskId = GenerateCodeFactory.getUUID();
        ZhenshiMqttSend.sendCmd(taskId, "手工开闸", machineDto, data.toJSONString());
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
            return new ResultVo(ResultVo.CODE_ERROR,"调用臻识云异常:"+response.getBodyStr());
        }
        String result = response.getBodyStr();
        return new ResultVo(ResultVo.CODE_OK,ResultVo.MSG_OK,JSONObject.parseObject(result));
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

    private void dealCmd(String taskId, JSONObject reqData, BarrierDto machineDto) {
        String cmd = reqData.getString("cmd");
        switch (cmd) {
            case CMD_HEARTBEAT:
                doHeartBeat(taskId, reqData, machineDto);
                break;
            case CMD_RESULT:
                doResult(taskId, reqData, machineDto);
                break;
        }
    }

    /**
     * 车辆识别结果
     * <p>
     * {
     * <p>
     * }
     *
     * @param reqData
     * @param machineDto
     */
    private void doResult(String taskId, JSONObject reqData, BarrierDto machineDto) {

        try {
            Date startTime = DateUtil.getCurrentDate();
            JSONObject plateResult = reqData.getJSONObject("result").getJSONObject("PlateResult");
            String type = plateResult.getString("colorType");
            if(StringUtil.isEmpty(type)){
                type = "1";
            }
            String license = plateResult.getString("license");
            if (plateResult.containsKey("imagePath")) {
                //String imagePath = ImageFactory.getBase64ByImgUrl(MappingCacheFactory.getValue("OSS_URL") + plateResult.getString("imagePath"));
                machineDto.setPhotoJpg(MappingCache.getValue(MappingConstant.FILE_DOMAIN, "OSS_URL") + plateResult.getString("imagePath"));
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
                // Thread.sleep(500); //这里停一秒
                String triggerCmd = "{\n" +
                        "    \"Response_AlarmInfoPlate\": {\n" +
                        "        \"channelNum\": 0,\n" +
                        "        \"delay\": 1000,\n" +
                        "        \"info\": \"ok\",\n" +
                        "        \"msgId\": \"" + GenerateCodeFactory.getUUID() + "\"\n" +
                        "    }\n" +
                        "}";
                ZhenshiMqttSend.sendCmd(taskId, license, machineDto, triggerCmd);
            }

            Thread.sleep(400); //这里停一秒
            JinjieScreenMqttFactory.pay(taskId, license, machineDto, resultParkingAreaTextDto.getVoice());
            if (!StringUtil.isEmpty(resultParkingAreaTextDto.getText1())) {
                Thread.sleep(400); //这里停一秒
                JinjieScreenMqttFactory.downloadTempTexts(taskId, license, machineDto, 0, resultParkingAreaTextDto.getText1());
            }
            if (!StringUtil.isEmpty(resultParkingAreaTextDto.getText2())) {
                Thread.sleep(400); //这里停一秒
                JinjieScreenMqttFactory.downloadTempTexts(taskId, license, machineDto, 1, resultParkingAreaTextDto.getText2(), (byte) 0x00, (byte) 0x03);
            }
            if (!StringUtil.isEmpty(resultParkingAreaTextDto.getText3())) {
                Thread.sleep(400); //这里停一秒
                JinjieScreenMqttFactory.downloadTempTexts(taskId, license, machineDto, 2, resultParkingAreaTextDto.getText3());
            }
            if (!StringUtil.isEmpty(resultParkingAreaTextDto.getText4())) {
                Thread.sleep(400); //这里停一秒
                JinjieScreenMqttFactory.downloadTempTexts(taskId, license, machineDto, 3, resultParkingAreaTextDto.getText4());
            }


        } catch (Exception e) {
            logger.error("开门异常", e);
        }

    }

    /**
     * 处理视频播放地址
     *
     * @param reqData
     * @param machineDto
     */
    private void doHeartBeat(String taskId, JSONObject reqData, BarrierDto machineDto) {

        String heartBeatTime = null;
        heartBeatTime = DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A);
        BarrierPo barrierPo = new BarrierPo();
        barrierPo.setMachineId(machineDto.getMachineId());
        barrierPo.setHeartbeatTime(heartBeatTime);
        barrierV1InnerServiceSMOImpl.updateBarrier(barrierPo);

    }

    @Override
    public void restartMachine(BarrierDto machineDto) {


    }

    @Override
    public void openDoor(BarrierDto machineDto, ParkingAreaTextDto parkingAreaTextDto) {
        try {
            // 发送开闸命令
            String triggerCmd = "{\n" +
                    "    \"Response_AlarmInfoPlate\": {\n" +
                    "        \"channelNum\": 0,\n" +
                    "        \"delay\": 1000,\n" +
                    "        \"info\": \"ok\",\n" +
                    "        \"msgId\": \"" + GenerateCodeFactory.getUUID() + "\"\n" +
                    "    }\n" +
                    "}";
            String taskId = GenerateCodeFactory.getUUID();
            String carNum = "手工开门";
            if (parkingAreaTextDto != null && !StringUtil.isEmpty(parkingAreaTextDto.getCarNum())) {
                carNum = parkingAreaTextDto.getCarNum();
            }

            if (parkingAreaTextDto == null) {
                JinjieScreenMqttFactory.pay(taskId, carNum, machineDto, "欢迎光临");
                Thread.sleep(400); //这里停一秒
                JinjieScreenMqttFactory.downloadTempTexts(taskId, carNum, machineDto, 0, "欢迎光临");
                Thread.sleep(500); //这里停一秒
                ZhenshiMqttSend.sendCmd(taskId, carNum, machineDto, triggerCmd);
                return;
            }
            JinjieScreenMqttFactory.pay(taskId, carNum, machineDto, parkingAreaTextDto.getVoice());
            if (!StringUtil.isEmpty(parkingAreaTextDto.getText1())) {
                JinjieScreenMqttFactory.downloadTempTexts(taskId, carNum, machineDto, 0, parkingAreaTextDto.getText1());
            }
            if (!StringUtil.isEmpty(parkingAreaTextDto.getText2())) {
                Thread.sleep(400); //这里停一秒
                JinjieScreenMqttFactory.downloadTempTexts(taskId, carNum, machineDto, 1, parkingAreaTextDto.getText2(), (byte) 0x00, (byte) 0x03);

            }
            if (!StringUtil.isEmpty(parkingAreaTextDto.getText3())) {
                Thread.sleep(400); //这里停一秒
                JinjieScreenMqttFactory.downloadTempTexts(taskId, carNum, machineDto, 2, parkingAreaTextDto.getText3());

            }
            if (!StringUtil.isEmpty(parkingAreaTextDto.getText4())) {
                Thread.sleep(400); //这里停一秒
                JinjieScreenMqttFactory.downloadTempTexts(taskId, carNum, machineDto, 3, parkingAreaTextDto.getText4());
            }
            Thread.sleep(500); //这里停一秒
            ZhenshiMqttSend.sendCmd(taskId, carNum, machineDto, triggerCmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeDoor(BarrierDto machineDto, ParkingAreaTextDto parkingAreaTextDto) {
        // 发送开闸命令
        String triggerCmd = "{\n" +
                "    \"Response_AlarmInfoPlate\": {\n" +
                "        \"channelNum\": 1,\n" +
                "        \"delay\": 1000,\n" +
                "        \"info\": \"ok\",\n" +
                "        \"msgId\": \"" + GenerateCodeFactory.getUUID() + "\"\n" +
                "    }\n" +
                "}";
        String taskId = GenerateCodeFactory.getUUID();
        ZhenshiMqttSend.sendCmd(taskId, "手动关门", machineDto, triggerCmd);

    }




}
