package com.java110.system.smo.impl;


import com.java110.core.cache.CommonCache;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.factory.SendSmsFactory;
import com.java110.core.utils.StringUtil;
import com.java110.dto.msg.SmsDto;
import com.java110.intf.system.ISmsInnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @ClassName FloorInnerServiceSMOImpl
 * @Description 消息内部服务实现类
 * @Author wuxw
 * @Date 2019/4/24 9:20
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
@RestController
public class SmsInnerServiceSMOImpl  implements ISmsInnerServiceSMO {

    private static final Logger logger = LoggerFactory.getLogger(SmsInnerServiceSMOImpl.class);



    @Override
    public SmsDto send(@RequestBody SmsDto smsDto) {

        try {

            SendSmsFactory.sendSms(smsDto.getTel(), smsDto.getCode());

            //将验证码存入Redis中
            CommonCache.setValue(smsDto.getTel() + SendSmsFactory.VALIDATE_CODE, smsDto.getCode().toLowerCase() + "-" + new Date().getTime(), CommonCache.defaultExpireTime);

            smsDto.setSuccess(true);
            smsDto.setMsg("成功");
        } catch (Exception e) {
            logger.error("发送短信验证码失败", e);
            smsDto.setSuccess(false);
            smsDto.setMsg(e.getMessage());
        }
        return smsDto;

    }


    @Override
    public SmsDto validateCode(@RequestBody SmsDto smsDto) {

        //校验是否有有效的验证码
        String smsCode = CommonCache.getValue(smsDto.getTel() + SendSmsFactory.VALIDATE_CODE);

        if (!StringUtil.isEmpty(smsCode) && smsCode.contains("-")) {
            smsCode = smsCode.substring(0, smsCode.indexOf("-"));
        }

        if (smsDto.getCode().equals(smsCode)) {
            smsDto.setSuccess(true);
            smsDto.setMsg("校验成功");
            return smsDto;
        }
        smsDto.setSuccess(false);
        smsDto.setMsg("校验码不存在或不正确");
        return smsDto;
    }

}
