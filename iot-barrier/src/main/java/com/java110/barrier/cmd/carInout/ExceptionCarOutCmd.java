package com.java110.barrier.cmd.carInout;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ListUtil;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.intf.barrier.ICarInoutV1InnerServiceSMO;
import com.java110.po.carInout.CarInoutPo;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

/**
 * 异常出场 / 异常入场
 * INSERT INTO `p_privilege` VALUES ('502024072439940003', '异常入场', '', '2024-07-24 19:39:38', '0', '800900000003', '/iot/api/carInout.exceptionCarOut', '702023092199790001');
 * INSERT INTO `p_privilege_rel` VALUES ('102024072471860004', '502024072439940003', '600201904002', '1001', '2024-07-24 19:39:38', '0');
 */
@Java110Cmd(serviceCode = "carInout.exceptionCarOut")
public class ExceptionCarOutCmd extends Cmd {

    @Autowired
    private ICarInoutV1InnerServiceSMO carInoutV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.hasKeyAndValue(reqJson, "ciId", "未包含ciId");
        // operateType 可选，默认 OUT（异常出场），允许 IN（异常入场）
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        String ciId = reqJson.getString("ciId");
        String communityId = reqJson.getString("communityId");
        String operateType = reqJson.getString("operateType");
        if (operateType == null || operateType.isEmpty()) {
            operateType = "OUT"; // 默认为异常出场
        }

        // 查询进场记录
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCiId(ciId);
        carInoutDto.setCommunityId(communityId);
        List<CarInoutDto> carInoutDtos = carInoutV1InnerServiceSMOImpl.queryCarInouts(carInoutDto);
        Assert.listOnlyOne(carInoutDtos, "未包含进出场记录");

        // 根据操作类型设置目标状态
        String targetState;
        if ("IN".equals(operateType)) {
            targetState = CarInoutDto.STATE_IN; // 进场状态
        } else {
            targetState = CarInoutDto.STATE_OUT; // 离场状态
        }

        CarInoutPo carInoutPo = new CarInoutPo();
        carInoutPo.setCiId(ciId);
        carInoutPo.setCommunityId(communityId);
        carInoutPo.setState(targetState);
        carInoutV1InnerServiceSMOImpl.updateCarInout(carInoutPo);
    }
}