package com.java110.meter.factory.instrumentCommon;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.core.factory.MqttFactory;
import com.java110.dto.instrument.InstrumentDto;
import com.java110.meter.factory.AbstractInstrumentFactoryAdapt;
import org.springframework.stereotype.Service;

@Service("commonInstrumentFactoryAdapt")
public class CommonInstrumentFactoryAdapt extends AbstractInstrumentFactoryAdapt {
    public static final String INSTRUMENT_REPORT = "instrument/topic/report/response/SN";

    @Override
    public boolean initMachine(InstrumentDto instrumentDto) {
        MqttFactory.subscribe("instrument/topic/report/response/" + instrumentDto.getMachineCode());
        return true;
    }

    @Override
    public String instrumentResult(String topic, String data) {
        //todo: 仪表上报数据处理
        System.out.println(topic + ":" + data);
        return SUCCESS;
    }
}
