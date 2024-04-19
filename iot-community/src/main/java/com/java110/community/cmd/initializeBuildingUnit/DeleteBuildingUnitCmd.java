package com.java110.community.cmd.initializeBuildingUnit;

import com.alibaba.fastjson.JSONObject;
import com.java110.community.bmo.initializeBuildingUnit.IinitializeBuildingUnitBmo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.CmdContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;

@Java110Cmd(serviceCode = "initializeBuildingUnit.deleteBuildingUnit")
public class DeleteBuildingUnitCmd extends Cmd {
    @Autowired
    private IinitializeBuildingUnitBmo initializeBuildingUnitBmoImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区ID");

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String userId = CmdContextUtils.getUserId(context);
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区ID");
        String communityId = reqJson.getString("communityId");
        String userPassword = reqJson.getString("devPassword");
        ResponseEntity responseEntity = initializeBuildingUnitBmoImpl.deleteBuildingUnit(communityId, userId, userPassword);
        context.setResponseEntity(responseEntity);
    }
}
