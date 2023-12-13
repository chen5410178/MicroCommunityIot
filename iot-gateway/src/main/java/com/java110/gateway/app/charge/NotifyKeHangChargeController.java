package com.java110.gateway.app.charge;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.factory.LoggerFactory;
import com.java110.dto.chargeMachine.NotifyChargeOrderDto;
import com.java110.intf.charge.INotifyChargeV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 科航充电桩 充电完成回调
 *
 */
@RestController
@RequestMapping(path = "/app/charge/kehang")
public class NotifyKeHangChargeController {

    private final static Logger logger = LoggerFactory.getLogger(NotifyKeHangChargeController.class);


    private static final String FINISH_CHARGE = "net.equip.charge.slow.async.notice.finish";

    @Autowired
    private INotifyChargeV1InnerServiceSMO notifyChargeV1InnerServiceSMOImpl;

    /**
     * <p>支付回调Api</p>
     *
     * @param request
     * @throws Exception
     */
    @RequestMapping(path = "/notice", method = RequestMethod.POST)
    public ResponseEntity<String> notice(
            @RequestBody String postInfo,
            HttpServletRequest request) {

        String api = request.getHeader("api");

        JSONObject reqJson = JSONObject.parseObject(postInfo);
        if(FINISH_CHARGE.equals(api)){
            NotifyChargeOrderDto notifyChargeOrderDto = new NotifyChargeOrderDto();
            notifyChargeOrderDto.setMachineCode(reqJson.getString("equipCd"));
            notifyChargeOrderDto.setPortCode(reqJson.getString("port"));
            notifyChargeOrderDto.setBodyParam(postInfo);
            notifyChargeOrderDto.setReason(reqJson.getString("reason"));
            ResultVo resultVo = notifyChargeV1InnerServiceSMOImpl.finishCharge(notifyChargeOrderDto);

            if (resultVo.getCode() != ResultVo.CODE_OK) {
               return new ResponseEntity<>("FAIL",HttpStatus.OK);
            }

        }

        return new ResponseEntity<>("SUCCESS",HttpStatus.OK);
    }



}
