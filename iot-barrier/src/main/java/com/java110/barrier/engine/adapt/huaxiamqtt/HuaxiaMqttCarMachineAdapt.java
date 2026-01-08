package com.java110.barrier.engine.adapt.huaxiamqtt;

import com.alibaba.fastjson.JSONObject;
import com.java110.barrier.engine.ICallCarService;
import com.java110.barrier.engine.ICarMachineProcess;
import com.java110.barrier.engine.IInOutCarTextEngine;
import com.java110.barrier.engine.adapt.BaseMachineAdapt;
import com.java110.barrier.engine.adapt.zhenshiWebMqtt.ZhenshiWebMqttSend;
import com.java110.bean.ResultVo;
import com.java110.core.cache.CommonCache;
import com.java110.core.cache.MappingCache;
import com.java110.core.constant.MappingConstant;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.MqttFactory;
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
 * 华夏相机
 */
@Service("huaxiaMqttCarMachineAdapt")
public class HuaxiaMqttCarMachineAdapt extends BaseMachineAdapt implements ICarMachineProcess {

    Logger logger = LoggerFactory.getLogger(HuaxiaMqttCarMachineAdapt.class);


    // todo 识别结果
    public static final String CMD_PLATE_RESULT = "plateResult";

    //todo 心跳
    public static final String CMD_HEARTBEAT = "heartbeat";

    // todo 抓拍上报
    public static final String CMD_CAP_REPORT = "capReport";


    @Autowired
    private IBarrierV1InnerServiceSMO barrierV1InnerServiceSMOImpl;

    @Autowired
    private ICallCarService callCarServiceImpl;

    @Autowired
    private IManualOpenDoorLogV1InnerServiceSMO manualOpenDoorLogServiceImpl;


    @Override
    public void restartMachine(BarrierDto machineDto) {

    }


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

        String cmd = paramIn.getString("cmd");

        switch (cmd) {
            case CMD_HEARTBEAT: // todo 心跳
                doHeartBeat(taskId, paramIn);
                break;
            case CMD_PLATE_RESULT:
                doResult(taskId, paramIn);
                break;
            case CMD_CAP_REPORT:
                doTriggerImage(taskId, paramIn);
                break;

        }

    }

    /**
     * 心跳
     * {
     * "cmd": "heartbeat",
     * "msgId": "1562566753001402b681",
     * "devId": "0001aa00000d",
     * "parkId": "123456",
     * "devIp": "192.168.55.100",
     * "utcTs": 1562566751,
     * }
     *
     * @param taskId
     * @param reqData
     */
    private void doHeartBeat(String taskId, JSONObject reqData) {

        String machineCode = reqData.getString("devId");
        String msgId = reqData.getString("msgId");

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

        // 回复
        HuaxiaMqttSend.heartbeatAck(machineDtos.get(0), msgId);

    }

    /**
     * 识别
     * {
     * "cmd":"plateResult",
     * "msgId":"1562566751001402b681",
     * "devId": "0001aa00000d",
     * "parkId": "123456",
     * "devIp": "192.168.55.100",
     * "utcTs": 1562566751,
     * "content": {
     * "plateNum": "津 A12345",
     * "plateColor": "蓝色",
     * "plateVal": true,
     * "confidence": 28,
     * "carLogo": "丰田",
     * "carColor": "白色",
     * "vehicleType": "轿车",
     * "triggerTime": 1562566751,
     * "inOut": "in",
     * "isWhitelist": true,
     * "triggerType": "video",
     * "encrypt_verify": true,
     * "fullPicPath": "/picture/xxx/xxxx/xxx.jpg",
     * "platePicPath": "/picture/xxx/xxxx/xxx_plate.jpg"
     * }
     * }
     *
     * @param taskId
     * @param reqData
     */
    private void doResult(String taskId, JSONObject reqData) {

        Date startTime = DateUtil.getCurrentDate();
        String sn = reqData.getString("devId");
        BarrierDto machineDto = new BarrierDto();
        machineDto.setMachineCode(sn);
        List<BarrierDto> machineDtos = barrierV1InnerServiceSMOImpl.queryBarriers(machineDto);
        if (ListUtil.isNull(machineDtos)) {
            throw new IllegalArgumentException("设备不存在" + sn);
        }

        machineDto = machineDtos.get(0);

        JSONObject plateResult = reqData.getJSONObject("content");
        try {
            String type = plateResult.getString("plateColor");
            if ("蓝色".equals(type)) {
                type = "1";
            } else if ("黄色".equals(type)) {
                type = "2";
            } else if ("白色".equals(type)) {
                type = "3";
            } else if ("黑色".equals(type)) {
                type = "4";
            } else if ("绿色".equals(type)) {
                type = "5";
            } else if ("黄绿色".equals(type)) {
                type = "6";
            } else {
                type = "0";
            }
            String license = plateResult.getString("plateNum");
            if (plateResult.containsKey("fullPicPath")) {
                String imagePath = MappingCache.getValue(MappingConstant.FILE_DOMAIN, "OSS_URL") + plateResult.getString("fullPicPath");
                //String imagePath = ImageFactory.getBase64ByImgUrl(MappingCacheFactory.getValue("OSS_URL") + plateResult.getString("imagePath"));
                machineDto.setPhotoJpg(imagePath);
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
                HuaxiaMqttSend.sendOpenDoor(taskId, license, machineDto);
            }

            Thread.sleep(400); //这里停一秒
            HuaxiaMqttSend.pay(taskId, license, machineDto, resultParkingAreaTextDto.getVoice());

            if (!StringUtil.isEmpty(resultParkingAreaTextDto.getText1())) {
                Thread.sleep(400); //这里停一秒
                HuaxiaMqttSend.downloadTempTexts(taskId, license, machineDto, 0, resultParkingAreaTextDto.getText1());
            }
            if (!StringUtil.isEmpty(resultParkingAreaTextDto.getText2())) {
                Thread.sleep(400); //这里停一秒
                HuaxiaMqttSend.downloadTempTexts(taskId, license, machineDto, 1, resultParkingAreaTextDto.getText2(), (byte) 0x00, (byte) 0x03);
            }
            if (!StringUtil.isEmpty(resultParkingAreaTextDto.getText3())) {
                Thread.sleep(400); //这里停一秒
                HuaxiaMqttSend.downloadTempTexts(taskId, license, machineDto, 2, resultParkingAreaTextDto.getText3());
            }
            if (!StringUtil.isEmpty(resultParkingAreaTextDto.getText4())) {
                Thread.sleep(400); //这里停一秒
                HuaxiaMqttSend.downloadTempTexts(taskId, license, machineDto, 3, resultParkingAreaTextDto.getText4());
            }


        } catch (Exception e) {
            logger.error("开门异常", e);
        }
    }


    /**
     * 抓拍
     * {
     * "cmd":"capReport",
     * "msgId":"1562566751001402b681",
     * "devId": "0001aa00000d",
     * "parkId": "123456",
     * "devIp": "192.168.55.100",
     * "utcTs": 1562566751,
     * "whichMsg":"1562566753001402b999",
     * "picPath":"/picture/xxx/xxxx/20190329/10/20190329_101800_922_capture.jpg"
     * }
     *
     * @param taskId
     * @param paramIn
     */
    private void doTriggerImage(String taskId, JSONObject paramIn) {
        String sn = paramIn.getString("devId");
        String id = CommonCache.getAndRemoveValue("triggerImage_" + sn);
        if (StringUtil.isEmpty(id)) {
            return;
        }

        JSONObject payload = paramIn.getJSONObject("payload");

        ManualOpenDoorLogPo manualOpenDoorLogPo = new ManualOpenDoorLogPo();
        manualOpenDoorLogPo.setLogId(id);
        String imagePath = MappingCache.getValue(MappingConstant.FILE_DOMAIN, "OSS_URL") + payload.getString("picPath");
        imagePath = Base64Convert.decoder(imagePath);

        manualOpenDoorLogPo.setPhotoJpg(imagePath);
        manualOpenDoorLogServiceImpl.updateManualOpenDoorLog(manualOpenDoorLogPo);
    }


    @Override
    public void manualTrigger(BarrierDto machineDto) {
        String taskId = GenerateCodeFactory.getUUID();
        String carNum = "手工开门";
        HuaxiaMqttSend.manualTrigger(taskId, carNum, machineDto);

    }

    @Override
    public void triggerImage(BarrierDto machineDto, ManualOpenDoorLogPo manualOpenDoorLogPo) {
        CommonCache.setValue("triggerImage_" + machineDto.getMachineCode(), manualOpenDoorLogPo.getLogId());
        HuaxiaMqttSend.triggerImage(manualOpenDoorLogPo.getLogId(), "手工开闸", machineDto);
    }

    @Override
    public void openDoor(BarrierDto machineDto, ParkingAreaTextDto parkingAreaTextDto) {
        try {
            String taskId = GenerateCodeFactory.getUUID();
            String carNum = "手工开门";
            if (parkingAreaTextDto != null && !StringUtil.isEmpty(parkingAreaTextDto.getCarNum())) {
                carNum = parkingAreaTextDto.getCarNum();
            }

            if (parkingAreaTextDto == null) {
                HuaxiaMqttSend.pay(taskId, carNum, machineDto, "欢迎光临");
                Thread.sleep(400); //这里停一秒
                HuaxiaMqttSend.downloadTempTexts(taskId, carNum, machineDto, 0, "欢迎光临");
                Thread.sleep(500); //这里停一秒
                HuaxiaMqttSend.sendOpenDoor(taskId, carNum, machineDto);
                return;
            }
            HuaxiaMqttSend.pay(taskId, carNum, machineDto, parkingAreaTextDto.getVoice());
            if (!StringUtil.isEmpty(parkingAreaTextDto.getText1())) {
                HuaxiaMqttSend.downloadTempTexts(taskId, carNum, machineDto, 0, parkingAreaTextDto.getText1());
            }
            if (!StringUtil.isEmpty(parkingAreaTextDto.getText2())) {
                Thread.sleep(400); //这里停一秒
                HuaxiaMqttSend.downloadTempTexts(taskId, carNum, machineDto, 1, parkingAreaTextDto.getText2(), (byte) 0x00, (byte) 0x03);

            }
            if (!StringUtil.isEmpty(parkingAreaTextDto.getText3())) {
                Thread.sleep(400); //这里停一秒
                HuaxiaMqttSend.downloadTempTexts(taskId, carNum, machineDto, 2, parkingAreaTextDto.getText3());

            }
            if (!StringUtil.isEmpty(parkingAreaTextDto.getText4())) {
                Thread.sleep(400); //这里停一秒
                HuaxiaMqttSend.downloadTempTexts(taskId, carNum, machineDto, 3, parkingAreaTextDto.getText4());
            }
            Thread.sleep(500); //这里停一秒
            HuaxiaMqttSend.sendOpenDoor(taskId, carNum, machineDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeDoor(BarrierDto machineDto, ParkingAreaTextDto parkingAreaTextDto) {
        String taskId = GenerateCodeFactory.getUUID();
        HuaxiaMqttSend.sendCloseDoor(taskId, "手动关门", machineDto);
    }

    @Override
    public ResultVo getCloudFlvVideo(BarrierDto barrierDto) {
        return null;
    }


}
