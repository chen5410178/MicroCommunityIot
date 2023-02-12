package com.java110.dev.cmd.app;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.ResponseConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.app.AppDto;
import com.java110.intf.dev.IAppV1InnerServiceSMO;
import com.java110.po.app.AppPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Java110Cmd(serviceCode = "app.updateApp")
public class UpdateAppCmd extends Cmd {

    @Autowired
    private IAppV1InnerServiceSMO appInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {
        Assert.hasKeyAndValue(reqJson, "appId", "应用Id不能为空");
        Assert.hasKeyAndValue(reqJson, "name", "必填，请填写应用名称");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {
        AppPo appDto = BeanConvertUtil.covertBean(reqJson, AppPo.class);
        int count = appInnerServiceSMOImpl.updateApp(appDto);
        if (count < 1) {
            throw new CmdException(ResponseConstant.RESULT_CODE_ERROR, "修改数据失败");
        }
        ResponseEntity<String> responseEntity = new ResponseEntity<String>("", HttpStatus.OK);
        cmdDataFlowContext.setResponseEntity(responseEntity);
    }
}
