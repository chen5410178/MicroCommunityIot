package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.telTts.TelTtsDto;
import com.java110.intf.lamp.ITelTtsV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("queryTelTtsBmoImpl")
public class QueryTelTtsBmoImpl implements IIotCommonApiBmo {

    @Autowired
    private ITelTtsV1InnerServiceSMO telTtsV1InnerServiceSMOImpl;
    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
        Assert.hasKeyAndValue(reqJson, "page", "page不能为空");
        Assert.hasKeyAndValue(reqJson, "row", "row不能为空");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        TelTtsDto telTtsDto = BeanConvertUtil.covertBean(reqJson, TelTtsDto.class);

        int count = telTtsV1InnerServiceSMOImpl.queryTelTtssCount(telTtsDto);

        List<TelTtsDto> telTtsDtos = null;

        if (count > 0) {
            telTtsDtos = telTtsV1InnerServiceSMOImpl.queryTelTtss(telTtsDto);
        } else {
            telTtsDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, telTtsDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }
}
