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
import com.java110.doc.annotation.*;
import com.java110.dto.chargeMachine.ChargeMachineDto;
import com.java110.dto.chargeMachine.ChargeMachineOrderDto;
import com.java110.dto.chargeMachine.ChargeMachinePortDto;
import com.java110.intf.acct.IAccountInnerServiceSMO;
import com.java110.intf.charge.IChargeMachineOrderV1InnerServiceSMO;
import com.java110.intf.charge.IChargeMachinePortV1InnerServiceSMO;
import com.java110.intf.charge.IChargeMachineV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.chargeMachineOrder.ChargeMachineOrderPo;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

/**
 * 汽车充电桩手动结束充电 处理类
 * <p>
 * add by wuxw 2023-03-09
 */

@Java110CmdDoc(title = "停止充电",
        description = "用于外系统停止充电",
        httpMethod = "post",
        url = "http://{ip}:{port}/iot/api/chargeMachine.stopCarCharge",
        resource = "chargeDoc",
        author = "吴学文",
        serviceCode = "chargeMachine.stopCharge",
        seq = 25
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "communityId", length = 30, remark = "小区ID"),
        @Java110ParamDoc(name = "machineId", length = 30, remark = "充电桩ID"),
        @Java110ParamDoc(name = "machineCode", length = 11, remark = "充电桩编号"),
        @Java110ParamDoc(name = "portId", length = 30, remark = "插槽ID"),
        @Java110ParamDoc(name = "portCode", length = 30, remark = "插槽编号"),
        @Java110ParamDoc(name = "orderId", length = 30, remark = "订单ID"),
        @Java110ParamDoc(name = "state", length = 12, remark = "插槽状态"),
        @Java110ParamDoc(name = "amount", length = 30, remark = "扣款金额"),
        @Java110ParamDoc(name = "personId", length = 30, remark = "用户ID"),
})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
        }
)

@Java110ExampleDoc(
        reqBody = "{\"communityId\":\"2022081539020475\",\"machineId\":\"123123\",\"machineCode\":\"123123\",\"portId\":\"123123\",\"portCode\":\"123123\",\"orderId\":\"123123\",\"state\":\"1001\",\"amount\":\"2.70\",\"personId\":\"123123\"}",
        resBody = "{'code':0,'msg':'成功'}"
)
@Java110Cmd(serviceCode = "chargeMachine.stopCarCharge")
public class StopCarChargeCmd extends Cmd {

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

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区信息");
        Assert.hasKeyAndValue(reqJson, "machineId", "未包含设备号");
        Assert.hasKeyAndValue(reqJson, "portId", "未包含充电桩");

        String userId = context.getReqHeaders().get("user-id");
        Assert.hasLength(userId, "用户不存在");

        //查询充电桩设备信息

        ChargeMachineDto chargeMachineDto = new ChargeMachineDto();
        chargeMachineDto.setCommunityId(reqJson.getString("communityId"));
        chargeMachineDto.setMachineId(reqJson.getString("machineId"));
        List<ChargeMachineDto> chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(chargeMachineDto);

        Assert.listOnlyOne(chargeMachineDtos, "充电桩不存在");

        // todo 充电枪是否存在

        ChargeMachinePortDto chargeMachinePortDto = new ChargeMachinePortDto();
        chargeMachinePortDto.setMachineId(reqJson.getString("machineId"));
        chargeMachinePortDto.setPortId(reqJson.getString("portId"));
        List<ChargeMachinePortDto> chargeMachinePortDtos = chargeMachinePortV1InnerServiceSMOImpl.queryChargeMachinePorts(chargeMachinePortDto);
        Assert.listOnlyOne(chargeMachinePortDtos, "充电枪不存在");

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

        //调用充电桩停电
        ResultVo resultVo = chargeCoreImpl.stopCharge(chargeMachineDtos.get(0), chargeMachinePortDtos.get(0));

        if (resultVo.getCode() != ResultVo.CODE_OK) {
            context.setResponseEntity(ResultVo.createResponseEntity(resultVo));
            return;
        }



        context.setResponseEntity(ResultVo.createResponseEntity(resultVo));
    }
}
