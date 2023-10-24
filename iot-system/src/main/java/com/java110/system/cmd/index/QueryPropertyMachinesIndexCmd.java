package com.java110.system.cmd.index;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DateUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.chargeMachine.ChargeMachineDto;
import com.java110.dto.lamp.LampMachineDto;
import com.java110.dto.lift.LiftMachineDto;
import com.java110.dto.meter.MeterMachineDto;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.barrier.IBarrierV1InnerServiceSMO;
import com.java110.intf.charge.IChargeMachineV1InnerServiceSMO;
import com.java110.intf.lamp.ILampMachineV1InnerServiceSMO;
import com.java110.intf.lift.ILiftMachineV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Java110Cmd(serviceCode = "propertyIndex.queryPropertyMachinesIndex")
public class QueryPropertyMachinesIndexCmd extends Cmd {

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Autowired
    private IBarrierV1InnerServiceSMO barrierV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMachineV1InnerServiceSMO chargeMachineV1InnerServiceSMOImpl;

    @Autowired
    private IMeterMachineV1InnerServiceSMO meterMachineV1InnerServiceSMOImpl;

    @Autowired
    private ILiftMachineV1InnerServiceSMO liftMachineV1InnerServiceSMOImpl;

    @Autowired
    private ILampMachineV1InnerServiceSMO lampMachineV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {

        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区信息");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {

        JSONObject paramOut = new JSONObject();
        //查询门禁
        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setCommunityId(reqJson.getString("communityId"));
        int accessControlCount = accessControlV1InnerServiceSMOImpl.queryAccessControlsCount(accessControlDto);
        paramOut.put("accessControlCount", accessControlCount);
        accessControlDto.setHeartbeatTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        int accessControlOfflineCount = accessControlV1InnerServiceSMOImpl.queryAccessControlsCount(accessControlDto);
        paramOut.put("accessControlOfflineCount", accessControlOfflineCount);

        //查询道闸
        BarrierDto barrierDto = new BarrierDto();
        barrierDto.setCommunityId(reqJson.getString("communityId"));
        int barrierCount = barrierV1InnerServiceSMOImpl.queryBarriersCount(barrierDto);
        paramOut.put("barrierCount", barrierCount);
        barrierDto.setHeartbeatTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        int barrierOfflineCount = barrierV1InnerServiceSMOImpl.queryBarriersCount(barrierDto);
        paramOut.put("barrierOfflineCount", barrierOfflineCount);

        //查询充电桩
        ChargeMachineDto chargeMachineDto = new ChargeMachineDto();
        chargeMachineDto.setCommunityId(reqJson.getString("communityId"));
        int chargeCount = chargeMachineV1InnerServiceSMOImpl.queryChargeMachinesCount(chargeMachineDto);
        paramOut.put("chargeCount", chargeCount);
        chargeMachineDto.setHeartbeatTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        int chargeOfflineCount = chargeMachineV1InnerServiceSMOImpl.queryChargeMachinesCount(chargeMachineDto);
        paramOut.put("chargeOfflineCount", chargeOfflineCount);

        //查询水电表
        MeterMachineDto meterMachineDto = new MeterMachineDto();
        meterMachineDto.setCommunityId(reqJson.getString("communityId"));
        int meterCount = meterMachineV1InnerServiceSMOImpl.queryMeterMachinesCount(meterMachineDto);
        paramOut.put("meterCount", meterCount);
        meterMachineDto.setHeartbeatTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        int meterOfflineCount = meterMachineV1InnerServiceSMOImpl.queryMeterMachinesCount(meterMachineDto);
        paramOut.put("meterOfflineCount", meterOfflineCount);

        //查询电梯
        LiftMachineDto liftMachineDto = new LiftMachineDto();
        liftMachineDto.setCommunityId(reqJson.getString("communityId"));
        int liftCount = liftMachineV1InnerServiceSMOImpl.queryLiftMachinesCount(liftMachineDto);
        paramOut.put("liftCount", liftCount);
        liftMachineDto.setHeartbeatTime(new Date());
        int liftOfflineCount = liftMachineV1InnerServiceSMOImpl.queryLiftMachinesCount(liftMachineDto);
        paramOut.put("liftOfflineCount", liftOfflineCount);

        //查询路灯
        LampMachineDto lampMachineDto = new LampMachineDto();
        lampMachineDto.setCommunityId(reqJson.getString("communityId"));
        int lampCount = lampMachineV1InnerServiceSMOImpl.queryLampMachinesCount(lampMachineDto);
        paramOut.put("lampCount", lampCount);
        lampMachineDto.setHeartbeatTime(new Date());
        int lampOfflineCount = lampMachineV1InnerServiceSMOImpl.queryLampMachinesCount(lampMachineDto);
        paramOut.put("lampOfflineCount", lampOfflineCount);

        context.setResponseEntity(ResultVo.createResponseEntity(paramOut));
    }
}
