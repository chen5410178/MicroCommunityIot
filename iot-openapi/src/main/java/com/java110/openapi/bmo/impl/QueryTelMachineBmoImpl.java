package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.tel.TelMachineDto;
import com.java110.dto.tel.TelMachineUserDto;
import com.java110.intf.lamp.ITelMachineUserV1InnerServiceSMO;
import com.java110.intf.lamp.ITelMachineV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("queryTelMachineBmoImpl")
public class QueryTelMachineBmoImpl  implements IIotCommonApiBmo {

    @Autowired
    private ITelMachineUserV1InnerServiceSMO telMachineUserV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
        Assert.hasKeyAndValue(reqJson, "page", "page不能为空");
        Assert.hasKeyAndValue(reqJson, "row", "row不能为空");
        Assert.hasKeyAndValue(reqJson, "propertyUserTel", "propertyUserTel不能为空");

    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        TelMachineUserDto telMachineUserDto = BeanConvertUtil.covertBean(reqJson, TelMachineUserDto.class);
        telMachineUserDto.setStaffTel(reqJson.getString("propertyUserTel"));
        int count = telMachineUserV1InnerServiceSMOImpl.queryTelMachineUsersCount(telMachineUserDto);

        List<TelMachineUserDto> telMachineUserDtos = null;

        if (count > 0) {
            telMachineUserDtos = telMachineUserV1InnerServiceSMOImpl.queryTelMachineUsers(telMachineUserDto);
        } else {
            telMachineUserDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, telMachineUserDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }
}
