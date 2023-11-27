package com.java110.meter.factory;

import com.java110.dto.instrument.InstrumentDto;
import com.java110.po.instrument.InstrumentPo;

public interface IInstrumentFactoryAdapt {

    boolean initMachine(InstrumentDto instrumentDto);

    boolean addMachine(InstrumentPo instrumentpo);

    boolean updateMachine(InstrumentPo instrumentpo);

    boolean deleteMachine(InstrumentPo instrumentpo);

    String instrumentSensorReport(String topic, String param);
}
