package com.java110.meter.factory.instrumentCommon;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DateUtil;
import com.java110.dto.instrument.InstrumentDto;
import com.java110.dto.instrument.InstrumentLogDto;
import com.java110.dto.instrument.InstrumentSensorDto;
import com.java110.intf.meter.IInstrumentLogSensorV1InnerServiceSMO;
import com.java110.intf.meter.IInstrumentLogV1InnerServiceSMO;
import com.java110.intf.meter.IInstrumentSensorV1InnerServiceSMO;
import com.java110.intf.meter.IInstrumentV1InnerServiceSMO;
import com.java110.meter.factory.AbstractInstrumentFactoryAdapt;
import com.java110.po.instrument.InstrumentLogPo;
import com.java110.po.instrument.InstrumentLogSensorPo;
import com.java110.po.instrument.InstrumentPo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("commonInstrumentFactoryAdapt")
public class CommonInstrumentFactoryAdapt extends AbstractInstrumentFactoryAdapt {
    public static final String INSTRUMENT_REPORT = "instrument/topic/report/response/SN";

    public static final String SEND_PARAM_SIGNAL = "getParam";

    public static final String SN = "SN";

    @Autowired
    private IInstrumentSensorV1InnerServiceSMO instrumentSensorV1InnerServiceSMOImpl;

    @Autowired
    private IInstrumentV1InnerServiceSMO instrumentV1InnerServiceSMOImpl;

    @Autowired
    private IInstrumentLogV1InnerServiceSMO instrumentLogV1InnerServiceSMOImpl;

    @Autowired
    private IInstrumentLogSensorV1InnerServiceSMO instrumentLogSensorV1InnerServiceSMOImpl;

    @Override
    public boolean initMachine(InstrumentDto instrumentDto) {
        MqttFactory.subscribe("instrument/topic/report/response/" + instrumentDto.getMachineCode());
        return true;
    }

    @Override
   // @Transactional
    public String instrumentResult(String topic, String data) {
        String sensorId = topic.substring(topic.lastIndexOf("/") + 1, topic.length() - 1);

        InstrumentSensorDto instrumentSensorDto = new InstrumentSensorDto();
        instrumentSensorDto.setSensorId(sensorId);
        List<InstrumentSensorDto> instrumentSensorDtos = instrumentSensorV1InnerServiceSMOImpl.queryInstrumentSensors(instrumentSensorDto);
        Assert.listOnlyOne(instrumentSensorDtos, "传感器不存在");

        InstrumentDto instrumentDto = new InstrumentDto();
        instrumentDto.setMachineId(instrumentSensorDtos.get(0).getMachineId());
        List<InstrumentDto> instrumentDtos = instrumentV1InnerServiceSMOImpl.queryInstruments(instrumentDto);
        Assert.listOnlyOne(instrumentDtos, "仪表不存在");

        JSONObject jsonObject = JSONObject.parseObject(data);
        JSONArray sensorDatas = jsonObject.getJSONArray("sensorDatas");
        if (data.contains(SEND_PARAM_SIGNAL)) {
            sendParam(instrumentSensorDtos.get(0), instrumentDtos.get(0));
            return SUCCESS;
        }
        if (data.contains("ID") && data.contains("ACTION")) {
            eventReport(instrumentSensorDtos.get(0), instrumentDtos.get(0), sensorDatas);
        } else {
            String times = jsonObject.getString("times");
            if (!ObjectUtils.isEmpty(sensorDatas) && !StringUtils.isEmpty(times)) {
                Date upTime = DateUtil.getDateFromStringA(times);
                if (DateUtil.getCurrentDate().before(new Date(upTime.getTime() + 5 * 60 * 1000))) {// 实时上报(上报时间不超过五分钟)
                    realTimeReport(instrumentSensorDtos.get(0), instrumentDtos.get(0), sensorDatas);
                }
            } else {
                //补发数据(看需要)
            }
        }

        return SUCCESS;
    }

    private void sendParam(InstrumentSensorDto instrumentSensorDto, InstrumentDto instrumentDto) {
        Map<String, String> paramMap = new HashMap();
        switch (instrumentSensorDto.getSensorType()) {
            case InstrumentSensorDto.PRESSURE_SENSOR:
                paramMap.put("FH", instrumentSensorDto.getMaxValue());
                paramMap.put("FL", instrumentSensorDto.getMinValue());
                paramMap.put("AT", instrumentDto.getUpMin());
                paramMap.put("CT", instrumentDto.getCheckSec());
                break;
            case InstrumentSensorDto.LIQUID_LEVEL_SENSOR:
                paramMap.put("FH", instrumentSensorDto.getMaxValue());
                paramMap.put("FL", instrumentSensorDto.getMinValue());
                paramMap.put("AT", instrumentDto.getUpMin());
                paramMap.put("CT", instrumentDto.getCheckSec());
                break;
            case InstrumentSensorDto.TEMPERATURE_AND_HUMIDITY_SENSOR:
                paramMap.put("FH", instrumentSensorDto.getMaxValue());
                paramMap.put("FL", instrumentSensorDto.getMinValue());
                paramMap.put("AT", instrumentDto.getUpMin());
                paramMap.put("CT", instrumentDto.getCheckSec());
                break;
            case InstrumentSensorDto.TEMPERATURE_SENSOR:
                paramMap.put("TH", instrumentSensorDto.getMaxValue());
                paramMap.put("TL", instrumentSensorDto.getMinValue());
                paramMap.put("AT", instrumentDto.getUpMin());
                paramMap.put("CT", instrumentDto.getCheckSec());
                break;
            case InstrumentSensorDto.WATER_QUALITY_SENSOR:
                paramMap.put("FH", instrumentSensorDto.getMaxValue());
                paramMap.put("FL", instrumentSensorDto.getMinValue());
                paramMap.put("AT", instrumentDto.getUpMin());
                paramMap.put("CT", instrumentDto.getCheckSec());
                break;
            case InstrumentSensorDto.COMBUSTIBLE_GAS_SENSOR:
                paramMap.put("TH", instrumentSensorDto.getMaxValue());
                paramMap.put("TL", instrumentSensorDto.getMinValue());
                paramMap.put("AT", instrumentDto.getUpMin());
                paramMap.put("CT", instrumentDto.getCheckSec());
                break;
            case InstrumentSensorDto.WATERLOGGING_SENSOR:
                paramMap.put("FH", instrumentSensorDto.getMaxValue());
                paramMap.put("FL", instrumentSensorDto.getMinValue());
                paramMap.put("AT", instrumentDto.getUpMin());
                paramMap.put("CT", instrumentDto.getCheckSec());
                break;
            default:
                break;
        }
        String sendParam = JSON.toJSONString(paramMap);
        MqttFactory.publish(INSTRUMENT_REPORT.replace(SN, instrumentSensorDto.getSensorId()), sendParam);
    }

    private void realTimeReport(InstrumentSensorDto instrumentSensorDto, InstrumentDto instrumentDto, JSONArray sensorDatas) {
        Map sensorDataMap = new HashMap();
        for (int i = 0; i < sensorDatas.size(); i++) {
            JSONObject sensorData = sensorDatas.getJSONObject(i); //{"flag":"F","value":0.000}
            switch (sensorData.getString("flag")) {
                case "F":
                    sensorDataMap.put("F", sensorData.getFloat("value"));
                    break;
                case "T":
                    sensorDataMap.put("T", sensorData.getFloat("value"));
                    break;
                case "V":
                    sensorDataMap.put("V", sensorData.getFloat("value"));
                    break;
                case "Q":
                    sensorDataMap.put("Q", sensorData.getInteger("value"));
                    break;
                case "ICCID":
                    sensorDataMap.put("ICCID", sensorData.getString("str"));
                    break;
                case "GPS":
                    sensorDataMap.put("GPS_lat", sensorData.getFloat("lat"));
                    sensorDataMap.put("GPS_lng", sensorData.getFloat("lng"));
                    break;
                default:
                    break;
            }
        }
        updateInstrument(instrumentDto, sensorDataMap);
        saveLog(instrumentSensorDto, instrumentDto, sensorDataMap, InstrumentLogDto.REAL_TIME_REPORT);
    }

    private void eventReport(InstrumentSensorDto instrumentSensorDto, InstrumentDto instrumentDto, JSONArray sensorDatas) {
        Map sensorDataMap = new HashMap();
        for (int i = 0; i < sensorDatas.size(); i++) {
            JSONObject sensorData = sensorDatas.getJSONObject(i); //{"flag":"F","value":0.000}
            switch (sensorData.getString("flag")) {
                case "F":
                    sensorDataMap.put("F", sensorData.getFloat("value"));
                    break;
                case "T":
                    sensorDataMap.put("T", sensorData.getFloat("value"));
                    break;
                case "V":
                    sensorDataMap.put("V", sensorData.getFloat("value"));
                    break;
                case "Q":
                    sensorDataMap.put("Q", sensorData.getInteger("value"));
                    break;
                case "ICCID":
                    sensorDataMap.put("ICCID", sensorData.getString("str"));
                    break;
                case "ID":
                    sensorDataMap.put("ID", sensorData.getInteger("value"));
                    break;
                case "ACTION":
                    sensorDataMap.put("ACTION", sensorData.getInteger("value"));
                    break;
                case "LIMIT":
                    sensorDataMap.put("LIMIT", sensorData.getFloat("value"));
                    break;
                default:
                    break;
            }
        }
        updateInstrument(instrumentDto, sensorDataMap);
        saveLog(instrumentSensorDto, instrumentDto, sensorDataMap, InstrumentLogDto.EVENT_REPORT);
    }

    private void updateInstrument(InstrumentDto instrumentDto, Map sensorDataMap) {
        InstrumentPo instrumentPo = new InstrumentPo();
        BeanUtils.copyProperties(instrumentDto, instrumentPo);
        instrumentPo.setSign(sensorDataMap.get("Q").toString());
        instrumentPo.setCharge(sensorDataMap.get("V").toString());
        instrumentV1InnerServiceSMOImpl.updateInstrument(instrumentPo);
    }

    private void saveLog(InstrumentSensorDto instrumentSensorDto, InstrumentDto instrumentDto, Map sensorDataMap, String action) {
        InstrumentLogPo instrumentLogPo = new InstrumentLogPo();
        instrumentLogPo.setLogId(GenerateCodeFactory.getGeneratorId("10"));
        instrumentLogPo.setAction(action);
        instrumentLogPo.setMachineId(instrumentDto.getMachineId());
        instrumentLogPo.setMachineCode(instrumentDto.getMachineCode());
        instrumentLogPo.setMachineName(instrumentDto.getMachineName());
        instrumentLogPo.setCommunityId(instrumentDto.getCommunityId());
        instrumentLogV1InnerServiceSMOImpl.saveInstrumentLog(instrumentLogPo);

        InstrumentLogSensorPo instrumentLogSensorPo = new InstrumentLogSensorPo();
        instrumentLogSensorPo.setLsId(GenerateCodeFactory.getGeneratorId("10"));
        instrumentLogSensorPo.setLogId(instrumentLogPo.getLogId());
        instrumentLogSensorPo.setMachineId(instrumentDto.getMachineId());
        instrumentLogSensorPo.setSensorId(instrumentSensorDto.getSensorId());
        switch (instrumentSensorDto.getSensorType()) {
            case InstrumentSensorDto.PRESSURE_SENSOR:
                instrumentLogSensorPo.setSensorValue(sensorDataMap.get("F").toString() + "MPa/" + sensorDataMap.get("T").toString() + "℃");
                break;
            case InstrumentSensorDto.LIQUID_LEVEL_SENSOR:
                instrumentLogSensorPo.setSensorValue(sensorDataMap.get("F").toString() + "M/" + sensorDataMap.get("T").toString() + "℃");
                break;
            case InstrumentSensorDto.TEMPERATURE_AND_HUMIDITY_SENSOR:
                instrumentLogSensorPo.setSensorValue(sensorDataMap.get("F").toString() + "%/" + sensorDataMap.get("T").toString() + "℃");
                break;
            case InstrumentSensorDto.TEMPERATURE_SENSOR:
                instrumentLogSensorPo.setSensorValue(sensorDataMap.get("T").toString() + "℃");
                break;
            case InstrumentSensorDto.WATER_QUALITY_SENSOR:
                instrumentLogSensorPo.setSensorValue(sensorDataMap.get("F").toString() + "ppm/" + sensorDataMap.get("T").toString() + "℃");
                break;
            case InstrumentSensorDto.COMBUSTIBLE_GAS_SENSOR:
                instrumentLogSensorPo.setSensorValue(sensorDataMap.get("T").toString() + "ppm");
                break;
            case InstrumentSensorDto.WATERLOGGING_SENSOR:
                instrumentLogSensorPo.setSensorValue(sensorDataMap.get("F").toString() + sensorDataMap.get("T").toString() + "V");
                break;
            default:
                break;
        }
        instrumentLogSensorPo.setCommunityId(instrumentDto.getCommunityId());
        instrumentLogSensorV1InnerServiceSMOImpl.saveInstrumentLogSensor(instrumentLogSensorPo);
    }
}
