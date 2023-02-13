package com.java110.user.cmd.login;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cache.MappingCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.MappingConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.StringUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;

@Java110Cmd(serviceCode = "login.getSysInfo")
public class GetSysInfoCmd extends Cmd {
    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String logo = MappingCache.getValue("SYS_LOGO");
        if (StringUtil.isEmpty(logo)) {
            logo = "HC";
        }
        String apiUrl = MappingCache.getValue(MappingConstant.URL_DOMAIN,"SYS_API_URL");

        JSONObject sysInfo = new JSONObject();
        sysInfo.put("logo", logo);
        sysInfo.put("apiUrl", apiUrl);

        context.setResponseEntity(new ResponseEntity<String>(sysInfo.toJSONString(), HttpStatus.OK));
    }
}
