package com.java110.accessControl.cmd.visit;

import com.alibaba.fastjson.JSONObject;
import com.java110.accessControl.bmo.IAuthVisitBMO;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ListUtil;
import com.java110.dto.visit.VisitDto;
import com.java110.intf.accessControl.IVisitV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.ListenerUtils;

import java.text.ParseException;
import java.util.List;

/**
 * 物业审核访客
 */
@Java110Cmd(serviceCode = "visit.propertyAuthVisit")
public class PropertyAuthVisitCmd extends Cmd {

    @Autowired
    private IVisitV1InnerServiceSMO visitV1InnerServiceSMOImpl;

    @Autowired
    private IAuthVisitBMO authVisitBMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "visitId", "visitId不能为空");
        Assert.hasKeyAndValue(reqJson, "state", "state不能为空");


    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        VisitDto visitDto = new VisitDto();
        visitDto.setVisitId(reqJson.getString("visitId"));
        visitDto.setState(VisitDto.STATE_W);
        List<VisitDto> visitDtos = visitV1InnerServiceSMOImpl.queryVisits(visitDto);

        if(ListUtil.isNull(visitDtos)){
            throw new CmdException("不存在待审核记录");
        }

        authVisitBMOImpl.auth(visitDtos.get(0),reqJson.getString("state"),reqJson.getString("msg"));
    }
}
