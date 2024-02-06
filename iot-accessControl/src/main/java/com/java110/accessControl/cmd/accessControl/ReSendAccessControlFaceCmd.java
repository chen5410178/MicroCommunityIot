package com.java110.accessControl.cmd.accessControl;

import com.alibaba.fastjson.JSONObject;
import com.java110.accessControl.bmo.ISaveAccessControlFaceBMO;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;

/**
 * 重新下发设备人脸
 */
@Java110Cmd(serviceCode = "accessControl.reSendAccessControlFace")
public class ReSendAccessControlFaceCmd extends Cmd {

    @Autowired
    private ISaveAccessControlFaceBMO saveAccessControlFaceBMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "machineId", "未包含设备信息");
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区信息");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        saveAccessControlFaceBMOImpl.reSendFace(reqJson.getString("machineId"),
                reqJson.getString("communityId"),
                reqJson.getString("mfId"));

    }
}
