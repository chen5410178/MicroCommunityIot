package com.java110.user.cmd.login;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cache.CommonCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.CommonConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.factory.ValidateCodeFactory;
import com.java110.core.utils.CmdContextUtils;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;

/**
 * 生成验证图片
 */
@Java110Cmd(serviceCode = "login.generatorValidateCode")
public class GeneratorValidateCodeCmd extends Cmd {
    Logger logger = LoggerFactory.getLogger(GeneratorValidateCodeCmd.class);

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        int w = 200, h = 80;
        String verifyCode = ValidateCodeFactory.generateVerifyCode(4);
        ResponseEntity<String> verifyCodeImage = null;
        try {
            verifyCodeImage = new ResponseEntity<>(ValidateCodeFactory.outputImage(200, 80, verifyCode), HttpStatus.OK);

            //将验证码存入Redis中
            CommonCache.setValue(CmdContextUtils.getSessionId(context) + "_validateCode", verifyCode.toLowerCase(), CommonCache.defaultExpireTime);

        } catch (Exception e) {
            logger.error("生成验证码失败，", e);
            verifyCodeImage = new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        context.setResponseEntity(verifyCodeImage);
    }
}
