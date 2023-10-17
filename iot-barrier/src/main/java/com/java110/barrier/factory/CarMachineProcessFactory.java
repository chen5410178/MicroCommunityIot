package com.java110.barrier.factory;

import com.java110.barrier.engine.ICarMachineProcess;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.Assert;
import com.java110.dto.hardwareManufacturer.HardwareManufacturerDto;
import com.java110.intf.system.IHardwareManufacturerV1InnerServiceSMO;

import java.util.List;

/**
 * @ClassName AssessControlProcessFactory
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/20 21:00
 * @Version 1.0
 * add by wuxw 2020/5/20
 **/
public class CarMachineProcessFactory {


    /**
     * 获取硬件接口对象
     *
     * @return
     */
    public static ICarMachineProcess getCarImpl(String hmId)  {
        /**
         * 访问硬件接口
         */
        ICarMachineProcess carMachineProcessImpl = null;

        IHardwareManufacturerV1InnerServiceSMO manufacturerServiceImpl = ApplicationContextFactory.getBean("hardwareManufacturerV1InnerServiceSMOImpl", IHardwareManufacturerV1InnerServiceSMO.class);

        if (manufacturerServiceImpl == null) {
            manufacturerServiceImpl = ApplicationContextFactory.getBean(IHardwareManufacturerV1InnerServiceSMO.class.getName(), IHardwareManufacturerV1InnerServiceSMO.class);
        }

        HardwareManufacturerDto tmpManufacturerDto = new HardwareManufacturerDto();
        tmpManufacturerDto.setHmType(HardwareManufacturerDto.HM_TYPE_BARRIER);
        tmpManufacturerDto.setHmId(hmId);


        List<HardwareManufacturerDto> manufacturerDtos = manufacturerServiceImpl.queryHardwareManufacturers(tmpManufacturerDto);

        Assert.listOnlyOne(manufacturerDtos, "当前有多个默认协议或者一个都没有");
        carMachineProcessImpl = ApplicationContextFactory.getBean(manufacturerDtos.get(0).getProtocolImpl(), ICarMachineProcess.class);
        return carMachineProcessImpl;
    }

}
