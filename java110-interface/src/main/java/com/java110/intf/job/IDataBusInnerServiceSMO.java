package com.java110.intf.job;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.businessDatabus.CustomBusinessDatabusDto;
import com.java110.bean.dto.order.BusinessDto;
import com.java110.intf.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @ClassName ITaskInnerServiceSMO
 * @Description dataBus统一处理类
 * @Author wuxw
 * @Date 2019/4/24 9:04
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
@FeignClient(name = "iot-job", configuration = {FeignConfiguration.class})
@RequestMapping("/dataBusApi")
public interface IDataBusInnerServiceSMO {

    /**
     * <p>查询小区楼信息</p>
     *
     * @param businesses 业务
     * @return TaskDto 对象数据
     */
    @RequestMapping(value = "/exchange", method = RequestMethod.POST)
    boolean exchange(@RequestBody List<BusinessDto> businesses);


    /**
     * <p>开门</p>
     *
     * @param reqJson 请求信息
     * @return TaskDto 对象数据
     */
    @RequestMapping(value = "/openDoor", method = RequestMethod.POST)
    ResultVo openDoor(@RequestBody JSONObject reqJson);
    /**
     * <p>开门</p>
     *
     * @param reqJson 请求信息
     * @return TaskDto 对象数据
     */
    @RequestMapping(value = "/closeDoor", method = RequestMethod.POST)
    ResultVo closeDoor(@RequestBody JSONObject reqJson);

    /**
     * <p>重启设备</p>
     *
     * @param reqJson 请求信息
     * @return TaskDto 对象数据
     */
    @RequestMapping(value = "/restartMachine", method = RequestMethod.POST)
    ResultVo restartMachine(@RequestBody JSONObject reqJson);

    /**
     * <p>重启设备</p>
     *
     * @param reqJson 请求信息
     * @return TaskDto 对象数据
     */
    @RequestMapping(value = "/resendIot", method = RequestMethod.POST)
    ResultVo resendIot(@RequestBody JSONObject reqJson);


    /**
     * 自定义databus 数据 传输
     * @param customBusinessDatabusDto
     * @return
     */
    @RequestMapping(value = "/customExchange", method = RequestMethod.POST)
    void customExchange(@RequestBody CustomBusinessDatabusDto customBusinessDatabusDto);

    @RequestMapping(value = "/getQRcode", method = RequestMethod.POST)
    ResultVo getQRcode(@RequestBody JSONObject reqJson);

    @RequestMapping(value = "/customCarInOut", method = RequestMethod.POST)
    ResultVo customCarInOut(@RequestBody JSONObject reqJson);

}
