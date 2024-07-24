package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.monitor.MonitorMachineDto;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.monitor.IMonitorMachineV1InnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("queryOwnerAcPlayVideoUrlBmoImpl")
public class QueryOwnerAcPlayVideoUrlBmoImpl implements IIotCommonApiBmo {

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Autowired
    private IMonitorMachineV1InnerServiceSMO monitorMachineV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
        Assert.hasKeyAndValue(reqJson, "machineId", "machineId不能为空");
        Assert.hasKeyAndValue(reqJson, "link", "未包含link");

        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setLink(reqJson.getString("link"));
        ownerDto.setCommunityId(reqJson.getString("communityId"));
        List<OwnerDto> ownerDtos = ownerV1InnerServiceSMOImpl.queryOwners(ownerDto);

        Assert.listOnlyOne(ownerDtos, "业主不存在");


        AccessControlDto accessControlDto = BeanConvertUtil.covertBean(reqJson, AccessControlDto.class);
        accessControlDto.setPersonType(AccessControlFaceDto.PERSON_TYPE_OWNER);
        accessControlDto.setPersonId(ownerDtos.get(0).getMemberId());
        accessControlDto.setMachineId(reqJson.getString("machineId"));

        int count = accessControlV1InnerServiceSMOImpl.queryOwnerAccessControlsCount(accessControlDto);
        if (count < 1) {
            throw new CmdException("业主没有权限");
        }
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {

        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineId(reqJson.getString("machineId"));
        accessControlDto.setCommunityId(reqJson.getString("communityId"));
        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);
        if (ListUtil.isNull(accessControlDtos)) {
            throw new CmdException("门禁不存在");
        }

        if (StringUtil.isEmpty(accessControlDtos.get(0).getMonitorId())) {
            throw new CmdException("门禁不支持观看视频");
        }

        MonitorMachineDto monitorMachineDto = new MonitorMachineDto();
        monitorMachineDto.setMachineId(accessControlDtos.get(0).getMonitorId());
        monitorMachineDto.setCommunityId(reqJson.getString("communityId"));
        String url = monitorMachineV1InnerServiceSMOImpl.getPlayVideoUrl(monitorMachineDto);


        context.setResponseEntity(ResultVo.createResponseEntity(url));
    }
}
