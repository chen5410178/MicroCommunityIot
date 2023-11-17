package com.java110.charge.factory.common;

import com.java110.bean.ResultVo;
import com.java110.charge.factory.IChargeFactoryAdapt;
import com.java110.dto.chargeMachine.ChargeMachineDto;
import com.java110.dto.chargeMachine.ChargeMachinePortDto;
import com.java110.dto.chargeMachine.NotifyChargeOrderDto;
import com.java110.dto.chargeMachine.NotifyChargePortDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service("commonChargeMachineFactory")
public class CommonChargeMachineFactoryAdapt implements IChargeFactoryAdapt {
    @Override
    public ResultVo startCharge(ChargeMachineDto chargeMachineDto, ChargeMachinePortDto chargeMachinePortDto, String chargeType, double duration, String orderId) {
        return new ResultVo(ResultVo.CODE_OK, "启动成功");
    }

    @Override
    public ResultVo stopCharge(ChargeMachineDto chargeMachineDto, ChargeMachinePortDto chargeMachinePortDto) {
        return new ResultVo(ResultVo.CODE_OK, "停车成功");
    }

    @Override
    public ChargeMachinePortDto getChargePortState(ChargeMachineDto chargeMachineDto, ChargeMachinePortDto chargeMachinePortDto) {
        return chargeMachinePortDto;
    }

    @Override
    public List<NotifyChargePortDto> getChargeHeartBeatParam(NotifyChargeOrderDto notifyChargeOrderDto) {
        return null;
    }

    @Override
    public void queryChargeMachineState(ChargeMachineDto chargeMachineDto) {

    }

    @Override
    public void workHeartbeat(ChargeMachineDto chargeMachineDto, String bodyParam) {

    }

    @Override
    public void chargeResult(ChargeMachineDto chargeMachineDto, byte[] data) {

    }

    @Override
    public ResultVo restartMachine(ChargeMachineDto chargeMachineDto) {
        return new ResultVo(ResultVo.CODE_OK, "重启");
    }
}
