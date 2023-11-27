package com.java110.meter.factory.instrumentCommon;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.core.factory.MqttFactory;
import com.java110.dto.instrument.InstrumentDto;
import com.java110.meter.factory.AbstractInstrumentFactoryAdapt;
import org.springframework.stereotype.Service;

@Service("commonFactoryAdaptImpl")
public class CommonFactoryAdaptImpl extends AbstractInstrumentFactoryAdapt {

    @Override
    public boolean initMachine(InstrumentDto instrumentDto) {
        MqttFactory.subscribe("" + instrumentDto.getMachineCode());
        return true;
    }



    @Override
    public String instrumentSensorReport(String topic, String param) {
        if (topic.startsWith("")) {
            doCmdResult(JSONObject.parseObject(param));
        }
        return SUCCESS;
    }

    private void doCmdResult(JSONObject parseObject) {
        JSONArray sensorDatas = parseObject.getJSONArray("sensorDatas");
        for (Object sensorData : sensorDatas) {
            String sensorDataStr = sensorData.toString();
            switch (JSONObject.parseObject(sensorDataStr).getString("flag")) {
                case "F":

                    break;
                case "T":

                    break;
                case "V":

                    break;
                case "Q":

                    break;
                case "ICCID":

                    break;
                case "ID":

                    break;
                case "ACTION":

                    break;
                case "LIMIT":

                    break;
                default:
                    break;
            }
        }
    }


}
