package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.intf.accessControl.IAccessControlResultV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("getAccessControlQrcodeBmoImpl")
public class GetAccessControlQrcodeBmoImpl implements IIotCommonApiBmo {

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlResultV1InnerServiceSMO accessControlResultV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "link", "未包含link");
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
        Assert.hasKeyAndValue(reqJson, "machineCode", "请求报文中未包含设备信息");

    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setLink(reqJson.getString("link"));
        ownerDto.setCommunityId(reqJson.getString("communityId"));
        List<OwnerDto> ownerDtos = ownerV1InnerServiceSMOImpl.queryOwners(ownerDto);

        Assert.listOnlyOne(ownerDtos, "业主不存在");

        AccessControlDto accessControlDto = BeanConvertUtil.covertBean(reqJson, AccessControlDto.class);
        accessControlDto.setPersonType(AccessControlFaceDto.PERSON_TYPE_OWNER);
        accessControlDto.setPersonId(ownerDtos.get(0).getMemberId());

        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryOwnerAccessControls(accessControlDto);
        if (ListUtil.isNull(accessControlDtos)) {
            throw new CmdException("没有权限开门");
        }
        accessControlDto = accessControlDtos.get(0);
        accessControlDto.setLink(ownerDtos.get(0).getLink());
        ResultVo resultVo = null;
        // wiegand
        if ("102025032420940003".equals(accessControlDtos.get(0).getImplBean())) {
            resultVo = accessControlResultV1InnerServiceSMOImpl.getQRCode(accessControlDto);
            context.setResponseEntity(ResultVo.createResponseEntity(resultVo));
            return;
        }


        context.setResponseEntity(ResultVo.createResponseEntity(resultVo));
    }
}
