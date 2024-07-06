package com.java110.hal.http;

import com.java110.bean.ResultVo;
import com.java110.core.factory.LoggerFactory;
import com.java110.dto.parkingSpaceMachine.ParkingSpaceMachineNotifyDto;
import com.java110.intf.car.IParkingSpaceMachineNotifyV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 地磁相关接口
 */
@RestController
@RequestMapping(value = "/iot/hal/parkingSpaceMachine")
public class ParkingSpaceMachineHal {

    Logger logger = LoggerFactory.getLogger(ParkingSpaceMachineHal.class);

    @Autowired
    private IParkingSpaceMachineNotifyV1InnerServiceSMO parkingSpaceMachineNotifyV1InnerServiceSMOImpl;

    /**
     * 心跳入口
     *
     * @param machineCode
     * @param postInfo
     * @param request
     * @return
     */
    @RequestMapping(path = "/heartbeat/{machineCode}", method = RequestMethod.GET)
    public ResponseEntity<String> heartbeat(@PathVariable String machineCode,
                                            @RequestBody String postInfo,
                                            HttpServletRequest request) {

        ResponseEntity<String> paramOut = null;
        String result = "";
        try {
            result = parkingSpaceMachineNotifyV1InnerServiceSMOImpl.heartbeat(new ParkingSpaceMachineNotifyDto(machineCode, postInfo));
            paramOut = new ResponseEntity<String>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("网关异常", e);
            paramOut = ResultVo.error("请求发生异常，" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return paramOut;
    }

    /**
     * 识别结果上报
     *
     * @param machineCode
     * @param postInfo
     * @param request
     * @return
     */
    @RequestMapping(path = "/plateResult/{machineCode}", method = RequestMethod.POST)
    public ResponseEntity<String> plateResult(@PathVariable String machineCode,
                                              @RequestBody String postInfo,
                                              HttpServletRequest request) {

        ResponseEntity<String> paramOut = null;
        String result = "";
        try {
            result = parkingSpaceMachineNotifyV1InnerServiceSMOImpl.plateResult(new ParkingSpaceMachineNotifyDto(machineCode, postInfo));
            paramOut = new ResponseEntity<String>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("网关异常", e);
            paramOut = ResultVo.error("请求发生异常，" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return paramOut;
    }



    /**
     * 事件信息推送
     *
     * @param machineCode
     * @param postInfo
     * @param request
     * @return
     */
    @RequestMapping(path = "/eventPush/{machineCode}", method = RequestMethod.POST)
    public ResponseEntity<String> eventPush(@PathVariable String machineCode,
                                              @RequestBody String postInfo,
                                              HttpServletRequest request) {

        ResponseEntity<String> paramOut = null;
        String result = "";
        try {
            result = parkingSpaceMachineNotifyV1InnerServiceSMOImpl.eventPush(new ParkingSpaceMachineNotifyDto(machineCode, postInfo));
            paramOut = new ResponseEntity<String>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("网关异常", e);
            paramOut = ResultVo.error("请求发生异常，" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return paramOut;
    }



    /**
     * 车位设置
     *
     * @param machineCode
     * @param postInfo
     * @param request
     * @return
     */
    @RequestMapping(path = "/getEmptyFrame/{machineCode}", method = RequestMethod.POST)
    public ResponseEntity<String> getEmptyFrame(@PathVariable String machineCode,
                                              @RequestBody String postInfo,
                                              HttpServletRequest request) {

        ResponseEntity<String> paramOut = null;
        String result = "";
        try {
            result = parkingSpaceMachineNotifyV1InnerServiceSMOImpl.getEmptyFrame(new ParkingSpaceMachineNotifyDto(machineCode, postInfo));
            paramOut = new ResponseEntity<String>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("网关异常", e);
            paramOut = ResultVo.error("请求发生异常，" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return paramOut;
    }
}
