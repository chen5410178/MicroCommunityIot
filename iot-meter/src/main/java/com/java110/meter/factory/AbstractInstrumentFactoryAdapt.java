package com.java110.meter.factory;

import com.java110.dto.instrument.InstrumentDto;
import com.java110.intf.meter.*;
import com.java110.po.instrument.InstrumentPo;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractInstrumentFactoryAdapt implements IInstrumentFactoryAdapt {

    public static final String SUCCESS = "{\n" +
            "    \"code\": 0,\n" +
            "    \"msg\": \"成功\",\n" +
            "}";

    @Autowired
    private IInstrumentFactoryV1InnerServiceSMO instrumentFactoryV1InnerServiceSMOImpl;

    @Autowired
    private IInstrumentFactorySpecV1InnerServiceSMO instrumentFactorySpecV1InnerServiceSMOImpl;

    @Autowired
    private IInstrumentV1InnerServiceSMO instrumentV1InnerServiceSMOImpl;

    @Autowired
    private IInstrumentSensorV1InnerServiceSMO instrumentSensorV1InnerServiceSMOImpl;

    @Autowired
    private IInstrumentParamV1InnerServiceSMO instrumentParamV1InnerServiceSMOImpl;

    @Override
    public boolean initMachine(InstrumentDto instrumentDto) {
        return true;
    }

    @Override
    public boolean addMachine(InstrumentPo instrumentpo) {
        return true;
    }

    @Override
    public boolean updateMachine(InstrumentPo instrumentpo) {
        return true;
    }

    @Override
    public boolean deleteMachine(InstrumentPo instrumentpo) {
        return true;
    }

    @Override
    public String instrumentResult(String topic, String data) {
        return null;
    }
}
