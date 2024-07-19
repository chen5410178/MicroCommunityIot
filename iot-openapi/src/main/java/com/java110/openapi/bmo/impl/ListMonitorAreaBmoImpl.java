package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.monitor.MonitorAreaDto;
import com.java110.intf.monitor.IMonitorAreaV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("listMonitorAreaBmoImpl")
public class ListMonitorAreaBmoImpl implements IIotCommonApiBmo{

    @Autowired
    private IMonitorAreaV1InnerServiceSMO monitorAreaV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区");
        Assert.hasKeyAndValue(reqJson, "page", "未包含page");
        Assert.hasKeyAndValue(reqJson, "row", "未包含row");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        MonitorAreaDto monitorAreaDto = BeanConvertUtil.covertBean(reqJson, MonitorAreaDto.class);

        int count = monitorAreaV1InnerServiceSMOImpl.queryMonitorAreasCount(monitorAreaDto);

        List<MonitorAreaDto> monitorAreaDtos = null;

        if (count > 0) {
            monitorAreaDtos = monitorAreaV1InnerServiceSMOImpl.queryMonitorAreas(monitorAreaDto);
        } else {
            monitorAreaDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, monitorAreaDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }
}
