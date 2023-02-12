package com.java110.dev.cmd.service;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.ResponseConstant;
import com.java110.core.constant.StatusConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.service.ServiceDto;
import com.java110.intf.dev.IServiceInnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Java110Cmd(serviceCode = "service.deleteService")
public class DeleteServiceCmd extends Cmd {

    @Autowired
    private IServiceInnerServiceSMO serviceInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        //Assert.hasKeyAndValue(reqJson, "xxx", "xxx");

        Assert.hasKeyAndValue(reqJson, "serviceId", "服务ID不能为空");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        ServiceDto serviceDto = BeanConvertUtil.covertBean(reqJson, ServiceDto.class);

        serviceDto.setStatusCd(StatusConstant.STATUS_CD_INVALID);

        int count = serviceInnerServiceSMOImpl.deleteService(serviceDto);



        if (count < 1) {
            throw new CmdException(ResponseConstant.RESULT_CODE_ERROR, "删除数据失败");
        }

        ResponseEntity<String> responseEntity = new ResponseEntity<String>("", HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }
}
