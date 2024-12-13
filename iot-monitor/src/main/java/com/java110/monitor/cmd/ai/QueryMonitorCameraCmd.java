package com.java110.monitor.cmd.ai;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.AuthenticationFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.MVideoHttpUtil;
import com.java110.dto.monitorManufactor.MonitorManufactorDto;
import com.java110.intf.monitor.IMonitorManufactorV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

/**
 * 获取监控摄像头
 */
@Java110Cmd(serviceCode = "ai.queryMonitorCamera")
public class QueryMonitorCameraCmd extends Cmd {
    public static final String faceUrl = "/oapp/ext/common";
    @Autowired
    private IMonitorManufactorV1InnerServiceSMO monitorManufactorV1InnerServiceSMOImpl;
    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        super.validatePageInfo(reqJson);
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区编号");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        MonitorManufactorDto monitorManufactorDto = new MonitorManufactorDto();
        monitorManufactorDto.setCommunityId(reqJson.getString("communityId"));
        List<MonitorManufactorDto> monitorManufactorDtos = monitorManufactorV1InnerServiceSMOImpl.queryMonitorManufactors(monitorManufactorDto);

        if (ListUtil.isNull(monitorManufactorDtos)) {
            return;
        }
        JSONObject paramIn = new JSONObject();
        paramIn.put("mvApiCode","getMachineService");
        paramIn.put("page", reqJson.getIntValue("page"));
        paramIn.put("row", reqJson.getIntValue("row"));
        JSONObject paramOut = MVideoHttpUtil.post(faceUrl,paramIn,monitorManufactorDtos.get(0));

        if (paramOut.getIntValue("code") != 0) {
            throw new IllegalArgumentException(paramOut.getString("msg"));
        }

        context.setResponseEntity(ResultVo.createResponseEntity(paramOut.getJSONArray("data")));
    }
}
