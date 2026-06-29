package com.java110.user.cmd.login;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cache.CommonCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.factory.SliderCodeFactory;
import com.java110.core.factory.SliderCodeFactory.SliderCodeData;
import com.java110.core.utils.CmdContextUtils;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;

@Java110Cmd(serviceCode = "login.generatorSliderCode")
public class GeneratorSliderCodeCmd extends Cmd {
    Logger logger = LoggerFactory.getLogger(GeneratorSliderCodeCmd.class);

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        int bgWidth = 300;
        int bgHeight = 150;
        int sliderWidth = 50;
        int sliderHeight = 50;

        try {
            SliderCodeData data = SliderCodeFactory.generateSliderCode(bgWidth, bgHeight, sliderWidth, sliderHeight);

            CommonCache.setValue(CmdContextUtils.getSessionId(context) + "_sliderCode",
                    String.valueOf(data.getX()), CommonCache.defaultExpireTime);

            JSONObject result = new JSONObject();
            result.put("bgImage", data.getBgImage());
            result.put("sliderImage", data.getSliderImage());
            result.put("y", data.getY());
            result.put("sliderWidth", data.getSliderWidth());
            result.put("sliderHeight", data.getSliderHeight());

            context.setResponseEntity(new ResponseEntity<>(result.toJSONString(), HttpStatus.OK));
        } catch (Exception e) {
            logger.error("生成滑块验证码失败", e);
            context.setResponseEntity(new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
