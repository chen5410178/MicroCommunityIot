package com.java110.charge.factory.lvcc;

import com.java110.bean.ResultVo;
import com.java110.charge.factory.IChargeFactoryAdapt;
import com.java110.core.utils.BytesUtil;
import com.java110.core.utils.DateUtil;
import com.java110.dto.chargeMachine.ChargeMachineDto;
import com.java110.dto.chargeMachine.ChargeMachinePortDto;
import com.java110.dto.chargeMachine.NotifyChargeOrderDto;
import com.java110.dto.chargeMachine.NotifyChargePortDto;
import com.java110.dto.data.NettyReplyDataDto;
import com.java110.intf.hal.INotifyNettyDataV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("lvCCChargeMachineFactory")
public class LvCCChargeMachineFactoryAdapt  implements IChargeFactoryAdapt {

    @Autowired
    private INotifyNettyDataV1InnerServiceSMO notifyNettyDataV1InnerServiceSMOImpl;

    @Override
    public ResultVo startCharge(ChargeMachineDto chargeMachineDto, ChargeMachinePortDto chargeMachinePortDto, String chargeType, double duration, String orderId) {
        return null;
    }

    @Override
    public ResultVo stopCharge(ChargeMachineDto chargeMachineDto, ChargeMachinePortDto chargeMachinePortDto) {
        return null;
    }

    @Override
    public ChargeMachinePortDto getChargePortState(ChargeMachineDto chargeMachineDto, ChargeMachinePortDto chargeMachinePortDto) {
        return null;
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

        //todo 解析指令
        String cmd = LvCCUtil.getCmd(data);

        switch (cmd){
            case LvCCUtil.CMD_REGISTER:
                machineRegister(chargeMachineDto,data,LvCCUtil.getCmdContext(data));
                break;
        }


    }

    /**
     * 设备注册
     * @param chargeMachineDto
     * @param data
     * @param cmdContext
     */
    private void machineRegister(ChargeMachineDto chargeMachineDto, byte[] data, String cmdContext) {

        long curUnit = new Date().getTime()/1000;

        String paramOut = String.format("%x", curUnit);

        paramOut = LvCCUtil.computeResultDate(data,paramOut);

        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(chargeMachineDto.getMachineCode(),BytesUtil.hexStringToByteArray(paramOut)));
    }


}
