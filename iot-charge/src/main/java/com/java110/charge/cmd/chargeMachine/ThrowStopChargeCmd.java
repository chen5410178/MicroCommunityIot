package com.java110.charge.cmd.chargeMachine;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.charge.factory.IChargeCore;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ListUtil;
import com.java110.dto.chargeMachine.ChargeMachineDto;
import com.java110.dto.chargeMachine.ChargeMachineOrderDto;
import com.java110.dto.chargeMachine.ChargeMachinePortDto;
import com.java110.dto.chargeMachine.NotifyChargeOrderDto;
import com.java110.intf.acct.IAccountInnerServiceSMO;
import com.java110.intf.charge.IChargeMachineOrderV1InnerServiceSMO;
import com.java110.intf.charge.IChargeMachinePortV1InnerServiceSMO;
import com.java110.intf.charge.IChargeMachineV1InnerServiceSMO;
import com.java110.intf.charge.INotifyChargeV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

@Java110Cmd(serviceCode = "chargeMachine.throwStopCharge")
public class ThrowStopChargeCmd extends Cmd {

    @Autowired
    private IChargeMachineV1InnerServiceSMO chargeMachineV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMachinePortV1InnerServiceSMO chargeMachinePortV1InnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IAccountInnerServiceSMO accountInnerServiceSMOImpl;

    @Autowired
    private IChargeCore chargeCoreImpl;

    @Autowired
    private IChargeMachineOrderV1InnerServiceSMO chargeMachineOrderV1InnerServiceSMOImpl;

    @Autowired
    private INotifyChargeV1InnerServiceSMO notifyChargeV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区信息");
        Assert.hasKeyAndValue(reqJson, "machineId", "未包含充电桩");
        Assert.hasKeyAndValue(reqJson, "portId", "未包含插槽");

        String userId = context.getReqHeaders().get("user-id");
        Assert.hasLength(userId, "用户不存在");

        //查询充电桩设备信息

        ChargeMachineDto chargeMachineDto = new ChargeMachineDto();
        chargeMachineDto.setCommunityId(reqJson.getString("communityId"));
        chargeMachineDto.setMachineId(reqJson.getString("machineId"));
        List<ChargeMachineDto> chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(chargeMachineDto);

        Assert.listOnlyOne(chargeMachineDtos, "充电桩 不存在");

        // todo 插槽是否存在

        ChargeMachinePortDto chargeMachinePortDto = new ChargeMachinePortDto();
        chargeMachinePortDto.setMachineId(reqJson.getString("machineId"));
        chargeMachinePortDto.setPortId(reqJson.getString("portId"));
        List<ChargeMachinePortDto> chargeMachinePortDtos = chargeMachinePortV1InnerServiceSMOImpl.queryChargeMachinePorts(chargeMachinePortDto);
        Assert.listOnlyOne(chargeMachinePortDtos, "插槽不存在");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        ChargeMachineDto chargeMachineDto = new ChargeMachineDto();
        chargeMachineDto.setCommunityId(reqJson.getString("communityId"));
        chargeMachineDto.setMachineId(reqJson.getString("machineId"));
        List<ChargeMachineDto> chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(chargeMachineDto);

        ChargeMachinePortDto chargeMachinePortDto = new ChargeMachinePortDto();
        chargeMachinePortDto.setMachineId(reqJson.getString("machineId"));
        chargeMachinePortDto.setPortId(reqJson.getString("portId"));
        List<ChargeMachinePortDto> chargeMachinePortDtos = chargeMachinePortV1InnerServiceSMOImpl.queryChargeMachinePorts(chargeMachinePortDto);

        ChargeMachineOrderDto chargeMachineOrderDto = new ChargeMachineOrderDto();
        chargeMachineOrderDto.setMachineId(chargeMachineDtos.get(0).getMachineId());
        chargeMachineOrderDto.setPortId(chargeMachinePortDtos.get(0).getPortId());
        List<ChargeMachineOrderDto> chargeMachineOrderDtos = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrders(chargeMachineOrderDto);
        if (ListUtil.isNull(chargeMachineOrderDtos)) {
            throw new CmdException("订单不存在");
        }

        NotifyChargeOrderDto notifyChargeOrderDto = new NotifyChargeOrderDto();
        notifyChargeOrderDto.setOrderId(chargeMachineOrderDtos.get(0).getOrderId());
        notifyChargeOrderDto.setMachineCode(chargeMachineDtos.get(0).getMachineCode());
        notifyChargeOrderDto.setPortCode(chargeMachinePortDtos.get(0).getPortCode());
        notifyChargeOrderDto.setBodyParam("");
        notifyChargeOrderDto.setReason("结束充电-> 手工异常结束");
        notifyChargeOrderDto.setEnergy(chargeMachineOrderDtos.get(0).getEnergy());
        notifyChargeOrderDto.setAmount("0");
        notifyChargeOrderDto.setFeeAmount("0");
        notifyChargeOrderDto.setServiceAmount("0");
        notifyChargeV1InnerServiceSMOImpl.finishCharge(notifyChargeOrderDto);

    }
}
