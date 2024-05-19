package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.visit.VisitDto;
import com.java110.intf.accessControl.IVisitV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service("listVisitBmoImpl")
public class ListVisitBmoImpl implements IIotCommonApiBmo {

    private static Logger logger = LoggerFactory.getLogger(ListVisitBmoImpl.class);
    @Autowired
    private IVisitV1InnerServiceSMO visitV1InnerServiceSMOImpl;
    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
        Assert.hasKeyAndValue(reqJson, "page", "page不能为空");
        Assert.hasKeyAndValue(reqJson, "row", "row不能为空");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        VisitDto visitDto = BeanConvertUtil.covertBean(reqJson, VisitDto.class);

        int count = visitV1InnerServiceSMOImpl.queryVisitsCount(visitDto);

        List<VisitDto> visitDtos = null;

        if (count > 0) {
            visitDtos = visitV1InnerServiceSMOImpl.queryVisits(visitDto);
        } else {
            visitDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, visitDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }
}
