package com.java110.openapi.bmo.impl;


import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.core.cache.MappingCache;
import com.java110.core.constant.MappingConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.monitor.MonitorMachineDto;
import com.java110.intf.monitor.IMonitorMachineV1InnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("listOwnerMonitorBmoImpl")
public class ListOwnerMonitorBmoImpl implements IIotCommonApiBmo {
    private static Logger logger = LoggerFactory.getLogger(ListMonitorMachineBmoImpl.class);
    @Autowired
    private IMonitorMachineV1InnerServiceSMO monitorMachineV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
        Assert.hasKeyAndValue(reqJson, "page", "未包含page");
        Assert.hasKeyAndValue(reqJson, "row", "未包含row");
        Assert.hasKeyAndValue(reqJson, "link", "未包含link");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setLink(reqJson.getString("link"));
        ownerDto.setCommunityId(reqJson.getString("communityId"));
        List<OwnerDto> ownerDtos = ownerV1InnerServiceSMOImpl.queryOwners(ownerDto);

        Assert.listOnlyOne(ownerDtos, "业主不存在");

        MonitorMachineDto monitorMachineDto = BeanConvertUtil.covertBean(reqJson, MonitorMachineDto.class);
        monitorMachineDto.setPersonIds(new String[]{ownerDtos.get(0).getMemberId(),"9999"});

        int count = monitorMachineV1InnerServiceSMOImpl.queryMonitorMachinesCount(monitorMachineDto);

        List<MonitorMachineDto> monitorMachineDtos = null;

        if (count > 0) {
            monitorMachineDtos = monitorMachineV1InnerServiceSMOImpl.queryMonitorMachineList(monitorMachineDto);

            queryMachinePhotos(monitorMachineDtos);
        } else {
            monitorMachineDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, monitorMachineDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }

    private void queryMachinePhotos(List<MonitorMachineDto> monitorMachineDtos) {

        if (ListUtil.isNull(monitorMachineDtos)) {
            return;
        }

        String imgUrl = MappingCache.getValue(MappingConstant.FILE_DOMAIN, "IMG_PATH");
        String photoUrl = "";
        for (MonitorMachineDto monitorMachineDto : monitorMachineDtos) {
            if (StringUtil.isEmpty(monitorMachineDto.getPhotoUrl())) {
                continue;
            }
            if (monitorMachineDto.getPhotoUrl().startsWith("http")) {
                continue;
            }
            photoUrl = imgUrl + monitorMachineDto.getPhotoUrl();
            monitorMachineDto.setPhotoUrl(photoUrl);

        }

    }
}
