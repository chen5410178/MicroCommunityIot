package com.java110.hal.mqtt.subscribe;

import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.attendanceMachine.AttendanceMachineDto;
import com.java110.dto.hardwareManufacturer.HardwareManufacturerDto;
import com.java110.dto.hardwareManufacturerAttr.HardwareManufacturerAttrDto;
import com.java110.core.factory.MqttFactory;
import com.java110.dto.instrument.InstrumentDto;
import com.java110.dto.instrument.InstrumentSensorDto;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.accessControl.IAttendanceMachineV1InnerServiceSMO;
import com.java110.intf.meter.IInstrumentV1InnerServiceSMO;
import com.java110.intf.system.IHardwareManufacturerAttrV1InnerServiceSMO;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class MqttClientSubscribeFactory {

    public static void subscribe() {

        IHardwareManufacturerAttrV1InnerServiceSMO hardwareManufacturerAttrV1InnerServiceSMOImpl = ApplicationContextFactory.getBean("hardwareManufacturerAttrV1InnerServiceSMOImpl", IHardwareManufacturerAttrV1InnerServiceSMO.class);

        if (hardwareManufacturerAttrV1InnerServiceSMOImpl == null) {
            hardwareManufacturerAttrV1InnerServiceSMOImpl = ApplicationContextFactory.getBean(IHardwareManufacturerAttrV1InnerServiceSMO.class.getName(), IHardwareManufacturerAttrV1InnerServiceSMO.class);
        }

        HardwareManufacturerAttrDto hardwareManufacturerAttrDto = new HardwareManufacturerAttrDto();
        hardwareManufacturerAttrDto.setSpecCd(HardwareManufacturerAttrDto.SPEC_TOPIC);
        List<HardwareManufacturerAttrDto> manufacturerAttrDtos = hardwareManufacturerAttrV1InnerServiceSMOImpl.queryHardwareManufacturerAttrs(hardwareManufacturerAttrDto);

        if (ListUtil.isNull(manufacturerAttrDtos)) {
            return;
        }

        //todo topic 批量订阅
        for (HardwareManufacturerAttrDto manufacturerAttrDto : manufacturerAttrDtos) {

            //todo 订阅门禁topic
            subscribeAccessControlHardwareTopic(manufacturerAttrDto);

            //todo 订阅道闸topic
            subscribeBarrierHardwareTopic(manufacturerAttrDto);

            //todo 订阅仪表topic
            subscribeInstrumentHardwareTopic(manufacturerAttrDto);

            //todo 订阅考勤topic
            subscribeAttendanceHardwareTopic(manufacturerAttrDto);

        }


    }

    private static void subscribeAttendanceHardwareTopic(HardwareManufacturerAttrDto manufacturerAttrDto) {
        if (!HardwareManufacturerDto.HM_TYPE_ATTENDANCE.equals(manufacturerAttrDto.getHmType())) {
            return;
        }

        String topic = manufacturerAttrDto.getValue();

        if (!topic.contains("SN")) {
            MqttFactory.subscribe(topic.trim());
            return;
        }

        IAttendanceMachineV1InnerServiceSMO attendanceMachineV1InnerServiceSMOImpl = ApplicationContextFactory.getBean("attendanceMachineV1InnerServiceSMOImpl", IAttendanceMachineV1InnerServiceSMO.class);
        if (attendanceMachineV1InnerServiceSMOImpl == null) {
            attendanceMachineV1InnerServiceSMOImpl = ApplicationContextFactory.getBean(IAttendanceMachineV1InnerServiceSMO.class.getName(), IAttendanceMachineV1InnerServiceSMO.class);
        }

        AttendanceMachineDto attendanceMachineDto = new AttendanceMachineDto();
        attendanceMachineDto.setImplBean(manufacturerAttrDto.getHmId());
        List<AttendanceMachineDto> attendanceMachineDtos = attendanceMachineV1InnerServiceSMOImpl.queryAttendanceMachines(attendanceMachineDto);
        if (CollectionUtils.isEmpty(attendanceMachineDtos)) {
            return;
        }

        for (AttendanceMachineDto tmpAttendanceMachineDto : attendanceMachineDtos) {
            String newTopic = topic.replace("SN", tmpAttendanceMachineDto.getMachineCode());
            MqttFactory.subscribe(newTopic);
        }
    }

    private static void subscribeInstrumentHardwareTopic(HardwareManufacturerAttrDto manufacturerAttrDto) {
        if (!HardwareManufacturerDto.HM_TYPE_INSTRUMENT.equals(manufacturerAttrDto.getHmType())) {
            return;
        }

        String topic = manufacturerAttrDto.getValue();

        if (!topic.contains("SN")) {
            MqttFactory.subscribe(topic.trim());
            return;
        }

        IInstrumentV1InnerServiceSMO instrumentV1InnerServiceSMOImpl = ApplicationContextFactory.getBean("instrumentV1InnerServiceSMOImpl", IInstrumentV1InnerServiceSMO.class);
        if (instrumentV1InnerServiceSMOImpl == null) {
            instrumentV1InnerServiceSMOImpl = ApplicationContextFactory.getBean(IInstrumentV1InnerServiceSMO.class.getName(), IInstrumentV1InnerServiceSMO.class);
        }

        InstrumentDto instrumentDto = new InstrumentDto();
        instrumentDto.setImplBean(manufacturerAttrDto.getHmId());
        List<InstrumentDto> instrumentDtos = instrumentV1InnerServiceSMOImpl.queryInstruments(instrumentDto);
        if (CollectionUtils.isEmpty(instrumentDtos)) {
            return;
        }

        for (InstrumentDto dto : instrumentDtos) {
            List<InstrumentSensorDto> sensorList = dto.getInstrumentSensorList();
            for (InstrumentSensorDto sensorDto : sensorList) {
                String newTopic = topic.replace("SN", sensorDto.getSensorId() + "*");
                MqttFactory.subscribe(newTopic);
            }
        }
    }

    private static void subscribeBarrierHardwareTopic(HardwareManufacturerAttrDto manufacturerAttrDto) {

        if (!HardwareManufacturerDto.HM_TYPE_BARRIER.equals(manufacturerAttrDto.getHmType())) {
            return;
        }

        String topic = manufacturerAttrDto.getValue();
        if (StringUtil.isEmpty(topic)) {
            return;
        }

        MqttFactory.subscribe(topic);
    }

    /**
     * 门禁topic订阅
     *
     * @param manufacturerAttrDto
     * @return
     */
    private static void subscribeAccessControlHardwareTopic(HardwareManufacturerAttrDto manufacturerAttrDto) {

        if (!HardwareManufacturerDto.HM_TYPE_ACCESS_CONTROL.equals(manufacturerAttrDto.getHmType())) {
            return;
        }

        String topic = manufacturerAttrDto.getValue();

        if (!topic.contains("SN")) {
            MqttFactory.subscribe(topic.trim());
            return;
        }

        IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl = ApplicationContextFactory.getBean("accessControlV1InnerServiceSMOImpl", IAccessControlV1InnerServiceSMO.class);

        if (accessControlV1InnerServiceSMOImpl == null) {
            accessControlV1InnerServiceSMOImpl = ApplicationContextFactory.getBean(IAccessControlV1InnerServiceSMO.class.getName(), IAccessControlV1InnerServiceSMO.class);
        }

        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setImplBean(manufacturerAttrDto.getHmId());
        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);

        if (ListUtil.isNull(accessControlDtos)) {
            return;

        }

        String nTopic = "";
        for (AccessControlDto tmpAccessControlDto : accessControlDtos) {
            nTopic = topic.replace("SN", tmpAccessControlDto.getMachineCode());
            MqttFactory.subscribe(nTopic);
        }

    }
}
