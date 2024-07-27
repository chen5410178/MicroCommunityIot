package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.chargeMachine.ChargeMachinePortDto;
import com.java110.intf.charge.IChargeMachinePortV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("listChargeMachinePortBmoImpl")
public class ListChargeMachinePortBmoImpl implements IIotCommonApiBmo {

    private static Logger logger = LoggerFactory.getLogger(ListChargeMachinePortBmoImpl.class);
    @Autowired
    private IChargeMachinePortV1InnerServiceSMO chargeMachinePortV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "page", "未包含page");
        Assert.hasKeyAndValue(reqJson, "row", "未包含row");
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
        Assert.hasKeyAndValue(reqJson, "machineId", "communityId不能为空");

    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        ChargeMachinePortDto chargeMachinePortDto = BeanConvertUtil.covertBean(reqJson, ChargeMachinePortDto.class);

        int count = chargeMachinePortV1InnerServiceSMOImpl.queryChargeMachinePortsCount(chargeMachinePortDto);

        List<ChargeMachinePortDto> chargeMachinePortDtos = null;

        if (count > 0) {
            chargeMachinePortDtos = chargeMachinePortV1InnerServiceSMOImpl.queryChargeMachinePorts(chargeMachinePortDto);

            //调用 第三方查询 插槽状态
            // queryPortState(chargeMachinePortDtos);
        } else {
            chargeMachinePortDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, chargeMachinePortDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }
}
