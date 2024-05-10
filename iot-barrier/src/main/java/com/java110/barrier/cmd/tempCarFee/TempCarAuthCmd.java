package com.java110.barrier.cmd.tempCarFee;

import com.alibaba.fastjson.JSONObject;
import com.java110.barrier.dao.ICarInoutTempAuthV1ServiceDao;
import com.java110.barrier.engine.ICallCarService;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.carInoutTempAuth.CarInoutTempAuthDto;
import com.java110.intf.barrier.ICarInoutTempAuthV1InnerServiceSMO;
import com.java110.po.carInoutTempAuth.CarInoutTempAuthPo;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

@Java110Cmd(serviceCode = "tempCarFee.tempCarAuth")
public class TempCarAuthCmd extends Cmd {

    Logger logger = LoggerFactory.getLogger(TempCarAuthCmd.class);

    @Autowired
    private ICarInoutTempAuthV1InnerServiceSMO carInoutTempAuthV1InnerServiceSMOImpl;

    @Autowired
    private ICallCarService callCarServiceImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "authId", "未包含外部Id");
        Assert.hasKeyAndValue(reqJson, "state", "未包含状态");
        Assert.hasKeyAndValue(reqJson, "msg", "未包含审核说明");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        CarInoutTempAuthDto carInoutTempAuthDto = new CarInoutTempAuthDto();
        carInoutTempAuthDto.setAuthId(reqJson.getString("authId"));
        carInoutTempAuthDto.setState(CarInoutTempAuthDto.STATE_W);
        List<CarInoutTempAuthDto> carInoutTempAuthDtos = carInoutTempAuthV1InnerServiceSMOImpl.queryCarInoutTempAuths(carInoutTempAuthDto);

        Assert.listOnlyOne(carInoutTempAuthDtos, "临时车审核不存在或者已审核");

        CarInoutTempAuthPo carInoutTempAuthPo = BeanConvertUtil.covertBean(reqJson, CarInoutTempAuthPo.class);
        int flag = carInoutTempAuthV1InnerServiceSMOImpl.updateCarInoutTempAuth(carInoutTempAuthPo);

        if (flag < 1) {
            throw new CmdException("审核失败");
        }

        if (!CarInoutTempAuthDto.STATE_C.equals(reqJson.getString("state"))) {
            return;
        }


        try {
            callCarServiceImpl.tempCarAuthOpen(carInoutTempAuthDtos.get(0));
        } catch (Exception e) {
            logger.error("通知失败", e);
            throw new CmdException(e.getMessage());
        }
    }
}
