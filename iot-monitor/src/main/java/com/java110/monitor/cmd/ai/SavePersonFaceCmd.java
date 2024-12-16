package com.java110.monitor.cmd.ai;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.MVideoHttpUtil;
import com.java110.dto.monitorManufactor.MonitorManufactorDto;
import com.java110.intf.monitor.IMonitorManufactorV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

/**
 * 保存布控人员信息
 */
@Java110Cmd(serviceCode = "ai.savePersonFace")
public class SavePersonFaceCmd extends Cmd {

    public static final String faceUrl = "/oapp/ext/common";

    @Autowired
    private IMonitorManufactorV1InnerServiceSMO monitorManufactorV1InnerServiceSMOImpl;


    /**
     *  personName: '',
     *                 faceUrl: '',
     *                 communityId: '',
     *                 startTime: '',
     *                 endTime: '',
     *                 personType:'dispatch'
     * @param event              事件对象
     * @param context 请求报文数据
     * @param reqJson
     * @throws CmdException
     * @throws ParseException
     */
    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区编号");
        Assert.hasKeyAndValue(reqJson, "personName", "未包含人员名称");
        Assert.hasKeyAndValue(reqJson, "faceUrl", "未包含人脸");
        Assert.hasKeyAndValue(reqJson, "startTime", "未包含开始时间");
        Assert.hasKeyAndValue(reqJson, "endTime", "未包含结束时间");
        Assert.hasKeyAndValue(reqJson, "personType", "未包含人员类型");

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
        paramIn.put("mvApiCode","savePersonFaceService");
        paramIn.put("faceUrl", reqJson.getString("faceUrl"));
        paramIn.put("startTime", reqJson.getString("startTime"));
        paramIn.put("endTime", reqJson.getString("endTime"));
        paramIn.put("personName", reqJson.getString("personName"));
        paramIn.put("personType", reqJson.getString("personType"));
        JSONObject paramOut = MVideoHttpUtil.post(faceUrl,paramIn,monitorManufactorDtos.get(0));

        if (paramOut.getIntValue("code") != 0) {
            throw new IllegalArgumentException(paramOut.getString("msg"));
        }

        context.setResponseEntity(ResultVo.createResponseEntity(paramOut.getJSONArray("data")));
    }
}
