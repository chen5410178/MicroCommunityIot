package com.java110.hal.mqtt.subscribe;

import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.hardwareManufacturer.HardwareManufacturerDto;
import com.java110.dto.hardwareManufacturerAttr.HardwareManufacturerAttrDto;
import com.java110.core.factory.MqttFactory;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.system.IHardwareManufacturerAttrV1InnerServiceSMO;

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

        if (manufacturerAttrDtos == null || manufacturerAttrDtos.isEmpty()) {
            return;
        }

        //todo topic 批量订阅
        for (HardwareManufacturerAttrDto manufacturerAttrDto : manufacturerAttrDtos) {

            //todo 订阅门禁topic
            subscribeAccessControlHardwareTopic(manufacturerAttrDto);

            //todo 订阅道闸topic
            subscribeBarrierHardwareTopic(manufacturerAttrDto);

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

        if (accessControlDtos == null || accessControlDtos.isEmpty()) {
            return;

        }

        for (AccessControlDto tmpAccessControlDto : accessControlDtos) {
            topic = topic.replace("SN", tmpAccessControlDto.getMachineCode());
            MqttFactory.subscribe(topic);
        }

    }
}
