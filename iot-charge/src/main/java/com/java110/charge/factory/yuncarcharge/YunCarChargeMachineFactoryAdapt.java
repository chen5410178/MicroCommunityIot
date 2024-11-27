package com.java110.charge.factory.yuncarcharge;

import com.java110.bean.ResultVo;
import com.java110.charge.factory.IChargeFactoryAdapt;
import com.java110.core.cache.UrlCache;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.*;
import com.java110.dto.chargeMachine.*;
import com.java110.dto.chargePeakAlleyPrice.ChargePeakAlleyPriceDto;
import com.java110.dto.chargeRulePrice.ChargeRulePriceDto;
import com.java110.dto.data.NettyReplyDataDto;
import com.java110.intf.charge.*;
import com.java110.intf.hal.INotifyNettyDataV1InnerServiceSMO;
import com.java110.po.chargeMachine.ChargeMachinePo;
import com.java110.po.chargeMachineOrder.ChargeMachineOrderPo;
import com.java110.po.chargeMachinePort.ChargeMachinePortPo;
import com.java110.po.chargeProcessInfo.ChargeProcessInfoPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service("yunCarChargeMachineFactory")
public class YunCarChargeMachineFactoryAdapt implements IChargeFactoryAdapt {

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

    }

    @Override
    public ResultVo restartMachine(ChargeMachineDto chargeMachineDto) {
        return null;
    }
}
