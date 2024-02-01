package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.accessControlInout.AccessControlInoutDto;
import com.java110.dto.chargeMachine.ChargeMachineOrderDto;
import com.java110.intf.charge.IChargeMachineOrderV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("listChargeMachineOrderBmoImpl")
public class ListChargeMachineOrderBmoImpl implements IIotCommonApiBmo {


    private static Logger logger = LoggerFactory.getLogger(ListChargeMachineOrderBmoImpl.class);
    @Autowired
    private IChargeMachineOrderV1InnerServiceSMO chargeMachineOrderV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
        Assert.hasKeyAndValue(reqJson, "page", "未包含page");
        Assert.hasKeyAndValue(reqJson, "row", "未包含row");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        ChargeMachineOrderDto chargeMachineOrderDto = BeanConvertUtil.covertBean(reqJson, ChargeMachineOrderDto.class);

        int count = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrdersCount(chargeMachineOrderDto);

        List<ChargeMachineOrderDto> chargeMachineOrderDtos = null;

        if (count > 0) {
            chargeMachineOrderDtos = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrders(chargeMachineOrderDto);
        } else {
            chargeMachineOrderDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, chargeMachineOrderDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }
}
