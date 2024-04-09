package com.java110.barrier.engine.adapt.hc;

import com.alibaba.fastjson.JSONObject;
import com.java110.barrier.engine.ICallCarService;
import com.java110.barrier.engine.ICarMachineProcess;
import com.java110.barrier.engine.IInOutCarTextEngine;
import com.java110.barrier.engine.adapt.BaseMachineAdapt;
import com.java110.bean.ResultVo;
import com.java110.core.cache.MappingCache;
import com.java110.core.constant.MappingConstant;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.parking.ParkingAreaTextDto;
import com.java110.dto.parking.ResultParkingAreaTextDto;
import com.java110.intf.barrier.IBarrierV1InnerServiceSMO;
import com.java110.po.barrier.BarrierPo;
import com.java110.po.manualOpenDoorLog.ManualOpenDoorLogPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * HC官方道闸协议
 */
@Service("hcCarMachineAdapt")
public class HcCarMachineAdapt extends BaseMachineAdapt implements ICarMachineProcess {
    private static Logger logger = LoggerFactory.getLogger(HcCarMachineAdapt.class);

    public static final String SN = "{sn}";
    // todo 请求topic
    public static final String TOPIC_REQ = "hc/barrier/request/{sn}";

    //todo 返回 topic
    public static final String TOPIC_RES = "hc/barrier/response";

    public static final String CMD_OPEN_BARRIER = "openBarrier";// todo 开闸接口

    public static final String CMD_CLOSE_BARRIER = "closeBarrier";// todo 关闸接口

    public static final String CMD_RECOGNITION = "recognition";// todo 触发识别

    public static final String CMD_HEARTBEAT = "heartbeat";// todo 设备心跳

    public static final String CMD_BARRIER_RESULT = "barrierResult";// todo 设备识别

    public static final String CMD_PLAY_TTS = "playTts";// todo 播放语音

    public static final String CMD_VIEW_TEXT = "viewText";// todo 显示文字

    @Autowired
    private ICallCarService callCarServiceImpl;


    @Autowired
    private IBarrierV1InnerServiceSMO barrierV1InnerServiceSMOImpl;


    @Override
    public void restartMachine(BarrierDto machineDto) {

    }

    @Override
    public void openDoor(BarrierDto machineDto, ParkingAreaTextDto parkingAreaTextDto) {

        try {
            String taskId = GenerateCodeFactory.getGeneratorId("11");
            JSONObject param = new JSONObject();
            param.put("cmd", CMD_OPEN_BARRIER);
            param.put("taskId", taskId);
            String carNum = "手工开门";
            if (parkingAreaTextDto != null && !StringUtil.isEmpty(parkingAreaTextDto.getCarNum())) {
                carNum = parkingAreaTextDto.getCarNum();
            }

            if (parkingAreaTextDto == null) {
                TTsViewFactory.pay(carNum, machineDto, "欢迎光临");
                Thread.sleep(400); //这里停一秒
                TTsViewFactory.downloadTempTexts(carNum, machineDto, 0, "欢迎光临");
                Thread.sleep(500); //这里停一秒
                MqttFactory.publish(TOPIC_REQ.replace(SN, machineDto.getMachineCode()), param.toJSONString());
                return;
            }
            TTsViewFactory.pay(carNum, machineDto, parkingAreaTextDto.getVoice());
            if (!StringUtil.isEmpty(parkingAreaTextDto.getText1())) {
                TTsViewFactory.downloadTempTexts(carNum, machineDto, 0, parkingAreaTextDto.getText1());
            }
            if (!StringUtil.isEmpty(parkingAreaTextDto.getText2())) {
                Thread.sleep(400); //这里停一秒
                TTsViewFactory.downloadTempTexts(carNum, machineDto, 1, parkingAreaTextDto.getText2());

            }
            if (!StringUtil.isEmpty(parkingAreaTextDto.getText3())) {
                Thread.sleep(400); //这里停一秒
                TTsViewFactory.downloadTempTexts(carNum, machineDto, 2, parkingAreaTextDto.getText3());

            }
            if (!StringUtil.isEmpty(parkingAreaTextDto.getText4())) {
                Thread.sleep(400); //这里停一秒
                TTsViewFactory.downloadTempTexts(carNum, machineDto, 3, parkingAreaTextDto.getText4());
            }
            Thread.sleep(500); //这里停一秒
            MqttFactory.publish(TOPIC_REQ.replace(SN, machineDto.getMachineCode()), param.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void closeDoor(BarrierDto machineDto, ParkingAreaTextDto parkingAreaTextDto) {
        String taskId = GenerateCodeFactory.getGeneratorId("11");
        JSONObject param = new JSONObject();
        param.put("cmd", CMD_CLOSE_BARRIER);
        param.put("taskId", taskId);
        MqttFactory.publish(TOPIC_REQ.replace(SN, machineDto.getMachineCode()), param.toJSONString());

    }

    @Override
    public void sendKeepAlive(BarrierDto machineDto) {

    }

    @Override
    public void mqttMessageArrived(String tId, String topic, String s) {
        JSONObject paramIn = JSONObject.parseObject(s);
        String cmd = paramIn.getString("cmd");
        String taskId = paramIn.getString("taskId");
        String machineCode = paramIn.getString("machineCode");

        Assert.hasLength(cmd, "未包含cmd");
        Assert.hasLength(taskId, "未包含taskId");
        Assert.hasLength(machineCode, "未包含machineCode");

        BarrierDto machineDto = new BarrierDto();
        machineDto.setMachineCode(machineCode);
        List<BarrierDto> machineDtos = barrierV1InnerServiceSMOImpl.queryBarriers(machineDto);
        if (machineDtos == null || machineDtos.size() < 1) {
            throw new IllegalArgumentException("设备不存在" + machineCode);
        }

        JSONObject paramOut = new JSONObject();
        paramOut.put("cmd", cmd);
        paramOut.put("taskId", taskId);
        paramOut.put("code", 0);
        paramOut.put("msg", "处理成功");
        paramOut.put("machineCode", machineCode);

        switch (cmd) {
            case CMD_HEARTBEAT: // todo 心跳
                heartbeatHc(cmd, taskId, machineCode, paramIn);
                MqttFactory.publish(TOPIC_REQ.replace(SN, machineCode), paramOut.toJSONString());
                break;
            case CMD_OPEN_BARRIER: //todo 重启返回
                break;
            case CMD_CLOSE_BARRIER: //todo 开门返回
                break;
            case CMD_RECOGNITION: // todo 触发识别
                break;
            case CMD_PLAY_TTS://todo 播放语音
                break;
            case CMD_BARRIER_RESULT:
                doResult(cmd, taskId, machineDtos.get(0), paramIn);
                MqttFactory.publish(TOPIC_REQ.replace(SN, machineCode), paramOut.toJSONString());
                break;

        }
    }

    private void doResult(String cmd, String tId, BarrierDto machineDto, JSONObject reqData) {

        try {
            String type = reqData.getString("carType");
            String license = reqData.getString("carNum");
            if (reqData.containsKey("imagePath")) {
                machineDto.setPhotoJpg(MappingCache.getValue(MappingConstant.FILE_DOMAIN, "OSS_URL") + reqData.getString("imagePath"));
            }

            IInOutCarTextEngine inOutCarTextEngine = ApplicationContextFactory.getBean("zhenshiMqttInOutCarTextEngine", IInOutCarTextEngine.class);

            ResultParkingAreaTextDto resultParkingAreaTextDto = callCarServiceImpl.ivsResult(type, license, machineDto, inOutCarTextEngine);

            if (ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS == resultParkingAreaTextDto.getCode()
                    || ResultParkingAreaTextDto.CODE_MONTH_CAR_SUCCESS == resultParkingAreaTextDto.getCode()
                    || ResultParkingAreaTextDto.CODE_TEMP_CAR_SUCCESS == resultParkingAreaTextDto.getCode()
                    || ResultParkingAreaTextDto.CODE_FREE_CAR_OUT_SUCCESS == resultParkingAreaTextDto.getCode()
                    || ResultParkingAreaTextDto.CODE_MONTH_CAR_OUT_SUCCESS == resultParkingAreaTextDto.getCode()
                    || ResultParkingAreaTextDto.CODE_TEMP_CAR_OUT_SUCCESS == resultParkingAreaTextDto.getCode()
                    || ResultParkingAreaTextDto.CODE_CAR_OUT_SUCCESS == resultParkingAreaTextDto.getCode()
            ) {
                // Thread.sleep(500); //这里停一秒
                String taskId = GenerateCodeFactory.getGeneratorId("11");
                JSONObject param = new JSONObject();
                param.put("cmd", CMD_OPEN_BARRIER);
                param.put("taskId", taskId);
                MqttFactory.publish(TOPIC_REQ.replace(SN, machineDto.getMachineCode()), param.toJSONString());
            }

            Thread.sleep(400); //这里停一秒
            TTsViewFactory.pay(license, machineDto, resultParkingAreaTextDto.getVoice());
            if (!StringUtil.isEmpty(resultParkingAreaTextDto.getText1())) {
                Thread.sleep(400); //这里停一秒
                TTsViewFactory.downloadTempTexts(license, machineDto, 0, resultParkingAreaTextDto.getText1());
            }
            if (!StringUtil.isEmpty(resultParkingAreaTextDto.getText2())) {
                Thread.sleep(400); //这里停一秒
                TTsViewFactory.downloadTempTexts(license, machineDto, 1, resultParkingAreaTextDto.getText2());
            }
            if (!StringUtil.isEmpty(resultParkingAreaTextDto.getText3())) {
                Thread.sleep(400); //这里停一秒
                TTsViewFactory.downloadTempTexts(license, machineDto, 2, resultParkingAreaTextDto.getText3());
            }
            if (!StringUtil.isEmpty(resultParkingAreaTextDto.getText4())) {
                Thread.sleep(400); //这里停一秒
                TTsViewFactory.downloadTempTexts(license, machineDto, 3, resultParkingAreaTextDto.getText4());
            }
        } catch (Exception e) {
            logger.error("开门异常", e);
        }

    }

    /**
     * 设备心跳
     *
     * @param cmd
     * @param taskId
     * @param machineCode
     * @param paramIn
     */
    private void heartbeatHc(String cmd, String taskId, String machineCode, JSONObject paramIn) {
        String heartBeatTime = null;
        heartBeatTime = DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A);
        BarrierPo barrierPo = new BarrierPo();
        barrierPo.setMachineCode(machineCode);
        barrierPo.setHeartbeatTime(heartBeatTime);
        barrierV1InnerServiceSMOImpl.updateBarrier(barrierPo);


    }

    @Override
    public void manualTrigger(BarrierDto machineDto) {
        String taskId = GenerateCodeFactory.getGeneratorId("11");
        JSONObject param = new JSONObject();
        param.put("cmd", CMD_RECOGNITION);
        param.put("taskId", taskId);
        MqttFactory.publish(TOPIC_REQ.replace(SN, machineDto.getMachineCode()), param.toJSONString());
    }

    @Override
    public void triggerImage(BarrierDto machineDto, ManualOpenDoorLogPo manualOpenDoorLogPo) {
        String taskId = GenerateCodeFactory.getGeneratorId("11");
        JSONObject param = new JSONObject();
        param.put("cmd", CMD_RECOGNITION);
        param.put("taskId", taskId);
        MqttFactory.publish(TOPIC_REQ.replace(SN, machineDto.getMachineCode()), param.toJSONString());
    }

    @Override
    public ResultVo getCloudFlvVideo(BarrierDto barrierDto) {
        return new ResultVo(ResultVo.CODE_OK, ResultVo.MSG_OK);
    }
}
