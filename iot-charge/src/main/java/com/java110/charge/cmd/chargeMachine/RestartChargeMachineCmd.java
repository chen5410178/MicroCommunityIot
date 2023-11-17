package com.java110.charge.cmd.chargeMachine;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.charge.factory.IChargeFactoryAdapt;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.Assert;
import com.java110.dto.chargeMachine.ChargeMachineDto;
import com.java110.dto.chargeMachine.ChargeMachineFactoryDto;
import com.java110.intf.charge.IChargeMachineFactoryV1InnerServiceSMO;
import com.java110.intf.charge.IChargeMachineV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

/**
 * 重启充电桩
 */
@Java110Cmd(serviceCode = "chargeMachine.restartChargeMachine")
public class RestartChargeMachineCmd extends Cmd {

    @Autowired
    private IChargeMachineV1InnerServiceSMO chargeMachineV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMachineFactoryV1InnerServiceSMO chargeMachineFactoryV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "machineId", "machineId不能为空");
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        ChargeMachineDto chargeMachineDto = new ChargeMachineDto();
        chargeMachineDto.setCommunityId(reqJson.getString("communityId"));
        chargeMachineDto.setMachineId(reqJson.getString("machineId"));
        List<ChargeMachineDto> chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(chargeMachineDto);

        Assert.listOnlyOne(chargeMachineDtos, "充电桩 不存在");

        ChargeMachineFactoryDto chargeMachineFactoryDto = new ChargeMachineFactoryDto();
        chargeMachineFactoryDto.setFactoryId(chargeMachineDtos.get(0).getImplBean());
        List<ChargeMachineFactoryDto> chargeMachineFactoryDtos = chargeMachineFactoryV1InnerServiceSMOImpl.queryChargeMachineFactorys(chargeMachineFactoryDto);

        Assert.listOnlyOne(chargeMachineFactoryDtos, "充电桩厂家不存在");

        IChargeFactoryAdapt chargeFactoryAdapt = ApplicationContextFactory.getBean(chargeMachineFactoryDtos.get(0).getBeanImpl(), IChargeFactoryAdapt.class);
        if (chargeFactoryAdapt == null) {
            throw new CmdException("厂家接口未实现");
        }

        ResultVo resultVo = chargeFactoryAdapt.restartMachine(chargeMachineDtos.get(0));
        context.setResponseEntity(ResultVo.createResponseEntity(resultVo));

    }
}
