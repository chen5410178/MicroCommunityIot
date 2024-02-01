package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.*;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service("listAccessControlBmoImpl")
public class ListAccessControlBmoImpl implements IIotCommonApiBmo {

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
        Assert.hasKeyAndValue(reqJson, "page", "未包含page");
        Assert.hasKeyAndValue(reqJson, "row", "未包含row");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        AccessControlDto accessControlDto = BeanConvertUtil.covertBean(reqJson, AccessControlDto.class);

        int count = accessControlV1InnerServiceSMOImpl.queryAccessControlsCount(accessControlDto);

        List<AccessControlDto> accessControlDtos = null;

        if (count > 0) {
            accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);
            freshMachineStateName(accessControlDtos);
        } else {
            accessControlDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, accessControlDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }

    private void freshMachineStateName(List<AccessControlDto> accessControlDtos) {

        if (ListUtil.isNull(accessControlDtos)) {
            return;
        }
        for (AccessControlDto accessControlDto : accessControlDtos) {
            String heartbeatTime = accessControlDto.getHeartbeatTime();
            try {
                if (StringUtil.isEmpty(heartbeatTime)) {
                    accessControlDto.setStateName("设备离线");
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(DateUtil.getDateFromString(heartbeatTime, DateUtil.DATE_FORMATE_STRING_A));
                    calendar.add(Calendar.MINUTE, 2);
                    if (calendar.getTime().getTime() <= DateUtil.getCurrentDate().getTime()) {
                        accessControlDto.setStateName("设备离线");
                    } else {
                        accessControlDto.setStateName("设备在线");
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
                accessControlDto.setStateName("设备离线");
            }
        }
    }
}
