package com.java110.hal.http;

import com.java110.bean.ResultVo;
import com.java110.core.factory.LoggerFactory;
import com.java110.dto.accessControl.AccessControlNotifyDto;
import com.java110.dto.lift.LiftWarnNotifyDto;
import com.java110.intf.accessControl.IAccessControlResultV1InnerServiceSMO;
import com.java110.intf.lift.ILiftWarnResultV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 电梯告警相机
 */
@RestController
@RequestMapping(value = "/iot/hal/liftCamera")
public class LiftCameraHal {
    Logger logger = LoggerFactory.getLogger(LiftCameraHal.class);


    @Autowired
    private ILiftWarnResultV1InnerServiceSMO liftWarnResultV1InnerServiceSMOImpl;

    /**
     * 心跳入口
     *
     * @param machineCode
     * @param postInfo
     * @param request
     * @return
     */
    @RequestMapping(path = "/liftWarnResult/{machineCode}", method = RequestMethod.POST)
    public ResponseEntity<String> liftWarnResult(@PathVariable String machineCode,
                                            @RequestBody String postInfo,
                                            HttpServletRequest request) {

        ResponseEntity<String> paramOut = null;
        String result = "";
        try {
            result = liftWarnResultV1InnerServiceSMOImpl.liftWarnResult(new LiftWarnNotifyDto(machineCode, postInfo));
            paramOut = new ResponseEntity<String>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("网关异常", e);
            paramOut = ResultVo.error("请求发生异常，" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return paramOut;
    }

    /**
     * 心跳入口
     *
     * @param machineCode
     * @param postInfo
     * @param request
     * @return
     */
    @RequestMapping(path = "/heartbeat/{machineCode}", method = RequestMethod.POST)
    public ResponseEntity<String> heartbeat(@PathVariable String machineCode,
                                            @RequestBody String postInfo,
                                            HttpServletRequest request) {

        ResponseEntity<String> paramOut = null;
        String result = "";
        try {
            result = liftWarnResultV1InnerServiceSMOImpl.heartbeat(new LiftWarnNotifyDto(machineCode, postInfo));
            paramOut = new ResponseEntity<String>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("网关异常", e);
            paramOut = ResultVo.error("请求发生异常，" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return paramOut;
    }
}
