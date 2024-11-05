package com.java110.monitor.cmd.monitorArea;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.dto.community.CommunityDto;
import com.java110.dto.monitor.MonitorAreaDto;
import com.java110.intf.community.ICommunityV1InnerServiceSMO;
import com.java110.intf.monitor.IMonitorAreaV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

/**
 * 查询监控区域
 */
@Java110Cmd(serviceCode = "monitorArea.getMonitorArea")
public class GetMonitorAreaCmd extends Cmd {

    @Autowired
    private ICommunityV1InnerServiceSMO communityV1InnerServiceSMOImpl;
    @Autowired
    private IMonitorAreaV1InnerServiceSMO monitorAreaV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson,"communityId","未包含小区ID");

        super.validatePageInfo(reqJson);
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(reqJson.getString("communityId"));
        List<CommunityDto> communityDtos = communityV1InnerServiceSMOImpl.queryCommunitys(communityDto);
        MonitorAreaDto monitorAreaDto = new MonitorAreaDto();
        monitorAreaDto.setCommunityId(communityDtos.get(0).getCommunityId());
        List<MonitorAreaDto> monitorAreaDtos = monitorAreaV1InnerServiceSMOImpl.queryMonitorAreas(monitorAreaDto);

        JSONArray datas = new JSONArray();
        JSONObject data = null;

        for(MonitorAreaDto tmpMonitorAreaDto: monitorAreaDtos){
            data = new JSONObject();
            data.put("communityId",communityDtos.get(0).getCommunityId());
            data.put("communityName",communityDtos.get(0).getName());
            data.put("name",tmpMonitorAreaDto.getMaName());
            data.put("maId",tmpMonitorAreaDto.getMaId());
            datas.add(data);
        }

        context.setResponseEntity(ResultVo.createResponseEntity(datas));
    }
}
