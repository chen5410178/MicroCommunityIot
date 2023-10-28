package com.java110.barrier.cmd.carInout;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.intf.barrier.ICarInoutV1InnerServiceSMO;
import com.java110.po.carInout.CarInoutPo;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;

@Java110Cmd(serviceCode = "carInout.updateCarInoutNum")
public class UpdateCarInoutNumCmd extends Cmd {

    @Autowired
    private ICarInoutV1InnerServiceSMO carInoutV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        Assert.hasKeyAndValue(reqJson,"ciId","未包含进出记录");
        Assert.hasKeyAndValue(reqJson,"carNum","未包含车牌号");
        Assert.hasKeyAndValue(reqJson,"communityId","未包含小区信息");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {


        CarInoutPo carInoutPo = new CarInoutPo();
        carInoutPo.setCiId(reqJson.getString("ciId"));
        carInoutPo.setCommunityId(reqJson.getString("communityId"));
        carInoutPo.setCarNum(reqJson.getString("carNum"));
       int flag = carInoutV1InnerServiceSMOImpl.updateCarInout(carInoutPo);
        if (flag < 1) {
            throw new CmdException("更新数据失败");
        }

        context.setResponseEntity(ResultVo.success());
    }
}
