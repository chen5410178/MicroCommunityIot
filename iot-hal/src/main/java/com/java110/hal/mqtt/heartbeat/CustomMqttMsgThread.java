package com.java110.hal.mqtt.heartbeat;

import com.java110.core.factory.ApplicationContextFactory;
import com.java110.dto.accessControl.AccessControlResultDto;
import com.java110.dto.barrier.BarrierResultDto;
import com.java110.dto.hardwareManufacturer.HardwareManufacturerDto;
import com.java110.dto.hardwareManufacturerAttr.HardwareManufacturerAttrDto;
import com.java110.dto.mqtt.MqttMsgDto;
import com.java110.dto.mqtt.MqttResultDto;
import com.java110.hal.mqtt.queue.MqttQueue;
import com.java110.intf.accessControl.IAccessControlResultV1InnerServiceSMO;
import com.java110.intf.accessControl.IAttendanceCheckinV1InnerServiceSMO;
import com.java110.intf.barrier.IMqttNotifyBarrierV1InnerServiceSMO;
import com.java110.intf.meter.IInstrumentV1InnerServiceSMO;
import com.java110.intf.system.IHardwareManufacturerAttrV1InnerServiceSMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 消耗消息队列
 */
public class CustomMqttMsgThread implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(CustomMqttMsgThread.class);


    @Override
    public void run() {
        while (true) {
            try {
                doDealMqttMsg();
            } catch (Exception e) {
                log.error("处理消息异常", e);
                e.printStackTrace();
            }
        }
    }

    private void doDealMqttMsg() throws Exception {

        MqttMsgDto mqttMsgDto = MqttQueue.getMqttMsg();

        log.debug("----- mqtt 消息处理------");

        if (mqttMsgDto == null) {
            return;
        }


        IHardwareManufacturerAttrV1InnerServiceSMO hardwareManufacturerAttrV1InnerServiceSMOImpl
                = ApplicationContextFactory.getBean("hardwareManufacturerAttrV1InnerServiceSMOImpl",
                IHardwareManufacturerAttrV1InnerServiceSMO.class);
        if (hardwareManufacturerAttrV1InnerServiceSMOImpl == null) {
            hardwareManufacturerAttrV1InnerServiceSMOImpl = ApplicationContextFactory.getBean(IHardwareManufacturerAttrV1InnerServiceSMO.class.getName(),
                    IHardwareManufacturerAttrV1InnerServiceSMO.class);
        }
        // todo 根据topic 查询
        HardwareManufacturerAttrDto tmpManufacturerDto = new HardwareManufacturerAttrDto();
        tmpManufacturerDto.setSpecCd(HardwareManufacturerAttrDto.SPEC_TOPIC);
        //tmpManufacturerDto.setValue(mqttMsgDto.getTopic());
        List<HardwareManufacturerAttrDto> manufacturerAttrDtos = hardwareManufacturerAttrV1InnerServiceSMOImpl.queryHardwareManufacturerAttrs(tmpManufacturerDto);

        if (manufacturerAttrDtos == null || manufacturerAttrDtos.isEmpty()) {
            return;
        }

        for (HardwareManufacturerAttrDto manufacturerAttrDto : manufacturerAttrDtos) {

            //todo 通知门禁
            notifyAccessControlMsg(manufacturerAttrDto, mqttMsgDto);

            //todo 通知道闸
            notifyBarrierMsg(manufacturerAttrDto, mqttMsgDto);

            //todo 通知仪表
            notifyInstrumentMsg(manufacturerAttrDto, mqttMsgDto);

            //todo 通知考勤
            notifyAttendanceMsg(manufacturerAttrDto, mqttMsgDto);


//            if (ManufacturerDto.HM_TYPE_ACCESS_CONTROL.equals(manufacturerAttrDto.getHmType())) {
//                AccessControlProcessFactory.getAssessControlProcessImpl(manufacturerAttrDto.getHmId()).mqttMessageArrived(taskId, mqttMsgDto.getTopic(), mqttMsgDto.getMsg());
//            } else if (ManufacturerDto.HM_TYPE_CAR.equals(manufacturerAttrDto.getHmType())) {
//                CarMachineProcessFactory.getCarImpl(manufacturerAttrDto.getHmId()).mqttMessageArrived(taskId, mqttMsgDto.getTopic(), mqttMsgDto.getMsg());
//            } else if (ManufacturerDto.HM_TYPE_ATTENDANCE.equals(manufacturerAttrDto.getHmType())) {
//                AttendanceProcessFactory.getAttendanceProcessImpl(manufacturerAttrDto.getHmId()).mqttMessageArrived(taskId, mqttMsgDto.getTopic(), mqttMsgDto.getMsg());
//            }
        }
    }

    private void notifyAttendanceMsg(HardwareManufacturerAttrDto manufacturerAttrDto, MqttMsgDto mqttMsgDto) {

        if (!HardwareManufacturerDto.HM_TYPE_ATTENDANCE.equals(manufacturerAttrDto.getHmType())) {
            return;
        }

        IAttendanceCheckinV1InnerServiceSMO attendanceCheckinV1InnerServiceSMO = ApplicationContextFactory.getBean("attendanceCheckinV1InnerServiceSMOImpl", IAttendanceCheckinV1InnerServiceSMO.class);
        if (attendanceCheckinV1InnerServiceSMO == null) {
            attendanceCheckinV1InnerServiceSMO = ApplicationContextFactory.getBean(IAttendanceCheckinV1InnerServiceSMO.class.getName(), IAttendanceCheckinV1InnerServiceSMO.class);
        }

        if (!manufacturerAttrDto.getValue().contains("SN") && manufacturerAttrDto.getValue().equals(mqttMsgDto.getTopic())) {
            attendanceCheckinV1InnerServiceSMO.attendanceResult(new MqttResultDto(manufacturerAttrDto.getHmId(), mqttMsgDto.getTopic(), mqttMsgDto.getMsg()));
            return;
        }

        String[] values = manufacturerAttrDto.getValue().split("SN");

        if (values.length == 1 && mqttMsgDto.getTopic().contains(values[0])) {
            attendanceCheckinV1InnerServiceSMO.attendanceResult(new MqttResultDto(manufacturerAttrDto.getHmId(), mqttMsgDto.getTopic(), mqttMsgDto.getMsg()));
        }

    }

    private void notifyInstrumentMsg(HardwareManufacturerAttrDto manufacturerAttrDto, MqttMsgDto mqttMsgDto) {

        if (!HardwareManufacturerDto.HM_TYPE_INSTRUMENT.equals(manufacturerAttrDto.getHmType())) {
            return;
        }

        IInstrumentV1InnerServiceSMO instrumentV1InnerServiceSMO = ApplicationContextFactory.getBean("instrumentV1InnerServiceSMOImpl", IInstrumentV1InnerServiceSMO.class);
        if (instrumentV1InnerServiceSMO == null) {
            instrumentV1InnerServiceSMO = ApplicationContextFactory.getBean(IInstrumentV1InnerServiceSMO.class.getName(), IInstrumentV1InnerServiceSMO.class);
        }

        if (!manufacturerAttrDto.getValue().contains("SN") && manufacturerAttrDto.getValue().equals(mqttMsgDto.getTopic())) {
            instrumentV1InnerServiceSMO.instrumentResult(new MqttResultDto(manufacturerAttrDto.getHmId(), mqttMsgDto.getTopic(), mqttMsgDto.getMsg()));
            return;
        }

        String[] values = manufacturerAttrDto.getValue().split("SN");

        if (values.length == 1 && mqttMsgDto.getTopic().contains(values[0])) {
            instrumentV1InnerServiceSMO.instrumentResult(new MqttResultDto(manufacturerAttrDto.getHmId(), mqttMsgDto.getTopic(), mqttMsgDto.getMsg()));
            return;
        }
    }


    /**
     * 通知门禁信息
     *
     * @param manufacturerAttrDto
     * @param mqttMsgDto
     */
    private void notifyAccessControlMsg(HardwareManufacturerAttrDto manufacturerAttrDto, MqttMsgDto mqttMsgDto) {

        if (!HardwareManufacturerDto.HM_TYPE_ACCESS_CONTROL.equals(manufacturerAttrDto.getHmType())) {
            return;
        }

        IAccessControlResultV1InnerServiceSMO accessControlResultV1InnerServiceSMO =
                ApplicationContextFactory.getBean("accessControlResultV1InnerServiceSMOImpl",
                        IAccessControlResultV1InnerServiceSMO.class);
        if (accessControlResultV1InnerServiceSMO == null) {
            accessControlResultV1InnerServiceSMO = ApplicationContextFactory.getBean(IAccessControlResultV1InnerServiceSMO.class.getName(),
                    IAccessControlResultV1InnerServiceSMO.class);
        }
        //todo 检查 配置的topic 中是否存在sn
        if (!manufacturerAttrDto.getValue().contains("SN") && manufacturerAttrDto.getValue().equals(mqttMsgDto.getTopic())) {
            accessControlResultV1InnerServiceSMO.accessControlResult(new AccessControlResultDto(manufacturerAttrDto.getHmId(), mqttMsgDto.getTopic(), mqttMsgDto.getMsg()));
            return;
        }

        String[] values = manufacturerAttrDto.getValue().split("SN");

        //todo 设备编码在结尾的情况
        if (values.length == 1 && mqttMsgDto.getTopic().contains(values[0])) {
            accessControlResultV1InnerServiceSMO.accessControlResult(new AccessControlResultDto(manufacturerAttrDto.getHmId(), mqttMsgDto.getTopic(), mqttMsgDto.getMsg()));
            return;
        }

        //todo 设备编码在中间的情况
        if (values.length == 2 && mqttMsgDto.getTopic().contains(values[0]) && mqttMsgDto.getTopic().contains(values[1])) {
            accessControlResultV1InnerServiceSMO.accessControlResult(new AccessControlResultDto(manufacturerAttrDto.getHmId(), mqttMsgDto.getTopic(), mqttMsgDto.getMsg()));
            return;
        }

        //todo 不处理

    }


    /**
     * 通知道闸消息
     *
     * @param manufacturerAttrDto
     * @param mqttMsgDto
     */
    private void notifyBarrierMsg(HardwareManufacturerAttrDto manufacturerAttrDto, MqttMsgDto mqttMsgDto) {
        if (!HardwareManufacturerDto.HM_TYPE_BARRIER.equals(manufacturerAttrDto.getHmType())) {
            return;
        }


        //todo 检查 配置的topic 中是否存在sn
        if (!manufacturerAttrDto.getValue().contains("SN") && manufacturerAttrDto.getValue().equals(mqttMsgDto.getTopic())) {
            IMqttNotifyBarrierV1InnerServiceSMO mqttNotifyBarrierV1InnerServiceSMOImpl =
                    ApplicationContextFactory.getBean("mqttNotifyBarrierV1InnerServiceSMOImpl",
                            IMqttNotifyBarrierV1InnerServiceSMO.class);
            if (mqttNotifyBarrierV1InnerServiceSMOImpl == null) {
                mqttNotifyBarrierV1InnerServiceSMOImpl = ApplicationContextFactory.getBean(IMqttNotifyBarrierV1InnerServiceSMO.class.getName(),
                        IMqttNotifyBarrierV1InnerServiceSMO.class);
            }

            mqttNotifyBarrierV1InnerServiceSMOImpl.mqttMessageArrived(new BarrierResultDto(manufacturerAttrDto.getHmId(), mqttMsgDto.getTopic(), mqttMsgDto.getMsg(), mqttMsgDto.getTaskId()));
            return;
        }



    }


}
