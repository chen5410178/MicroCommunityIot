package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.visit.VisitTypeDto;
import com.java110.intf.accessControl.IVisitTypeV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("listVisitTypeBmoImpl")
public class ListVisitTypeBmoImpl implements IIotCommonApiBmo {

    private static Logger logger = LoggerFactory.getLogger(ListVisitTypeBmoImpl.class);
    @Autowired
    private IVisitTypeV1InnerServiceSMO visitTypeV1InnerServiceSMOImpl;
    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
        Assert.hasKeyAndValue(reqJson, "page", "page不能为空");
        Assert.hasKeyAndValue(reqJson, "row", "row不能为空");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        VisitTypeDto visitTypeDto = BeanConvertUtil.covertBean(reqJson, VisitTypeDto.class);

        int count = visitTypeV1InnerServiceSMOImpl.queryVisitTypesCount(visitTypeDto);

        List<VisitTypeDto> visitTypeDtos = null;

        if (count > 0) {
            visitTypeDtos = visitTypeV1InnerServiceSMOImpl.queryVisitTypes(visitTypeDto);
        } else {
            visitTypeDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, visitTypeDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }
}
