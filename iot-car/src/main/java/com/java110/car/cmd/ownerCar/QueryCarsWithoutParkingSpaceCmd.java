package com.java110.car.cmd.ownerCar;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.car.OwnerCarDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.intf.car.IOwnerCarInnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@Java110Cmd(serviceCode = "ownerCar.queryCarsWithoutParkingSpace")
public class QueryCarsWithoutParkingSpaceCmd extends Cmd {

    @Autowired
    private IOwnerCarInnerServiceSMO ownerCarInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {
        super.validatePageInfo(reqJson);
        Assert.hasKeyAndValue(reqJson, "communityId", "请求中未包含communityId信息");
        Assert.isInteger(reqJson.getString("page"), "不是有效数字");
        Assert.isInteger(reqJson.getString("row"), "不是有效数字");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {
        int row = reqJson.getInteger("row");
        OwnerCarDto ownerCarDto = BeanConvertUtil.covertBean(reqJson, OwnerCarDto.class);
        //查询总记录数
        int total = ownerCarInnerServiceSMOImpl.queryCarsWithoutParkingSpaceCount(ownerCarDto);
        List<OwnerCarDto> ownerCarDtoList = null;
        if (total > 0) {
            ownerCarDtoList = ownerCarInnerServiceSMOImpl.queryCarsWithoutParkingSpace(ownerCarDto);
        } else {
            ownerCarDtoList = new ArrayList<>();
        }
        ResponseEntity<String> responseEntity = ResultVo.createResponseEntity((int) Math.ceil((double) total / (double) row), total, ownerCarDtoList);
        cmdDataFlowContext.setResponseEntity(responseEntity);
    }
}
