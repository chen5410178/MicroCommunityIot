package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.cache.MappingCache;
import com.java110.core.constant.MappingConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.monitor.MonitorMachineAttrsDto;
import com.java110.dto.monitor.MonitorMachineDto;
import com.java110.dto.storeStaff.StoreStaffDto;
import com.java110.intf.monitor.IMonitorMachineAttrsV1InnerServiceSMO;
import com.java110.intf.monitor.IMonitorMachineV1InnerServiceSMO;
import com.java110.intf.user.IStoreStaffV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service("listStaffMonitorMachineBmoImpl")
public class ListStaffMonitorMachineBmoImpl implements IIotCommonApiBmo {

    private static Logger logger = LoggerFactory.getLogger(ListStaffMonitorMachineBmoImpl.class);
    @Autowired
    private IMonitorMachineV1InnerServiceSMO monitorMachineV1InnerServiceSMOImpl;

    @Autowired
    private IMonitorMachineAttrsV1InnerServiceSMO monitorMachineAttrsV1InnerServiceSMOImpl;

    @Autowired
    private IStoreStaffV1InnerServiceSMO storeStaffV1InnerServiceSMOImpl;

    //    @Autowired
//    private IMonitorCore monitorCoreImpl;
    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区");
        Assert.hasKeyAndValue(reqJson, "page", "未包含page");
        Assert.hasKeyAndValue(reqJson, "row", "未包含row");
        Assert.hasKeyAndValue(reqJson, "propertyUserTel", "未包含propertyUserTel");
        Assert.hasKeyAndValue(reqJson, "propertyStoreId", "未包含propertyUserTel");

    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {


        StoreStaffDto storeStaffDto = new StoreStaffDto();
        storeStaffDto.setTel(reqJson.getString("propertyUserTel"));
        storeStaffDto.setStoreId(reqJson.getString("propertyStoreId"));
        List<StoreStaffDto> staffDtos = storeStaffV1InnerServiceSMOImpl.queryStoreStaffs(storeStaffDto);

        if (ListUtil.isNull(staffDtos)) {
            throw new IllegalArgumentException("员工不存在");
        }


        MonitorMachineDto monitorMachineDto = BeanConvertUtil.covertBean(reqJson, MonitorMachineDto.class);
        if(!"Y".equals(staffDtos.get(0).getAdminFlag())) {
            monitorMachineDto.setPersonId(staffDtos.get(0).getStaffId());
        }

        int count = monitorMachineV1InnerServiceSMOImpl.queryMonitorMachinesCount(monitorMachineDto);

        List<MonitorMachineDto> monitorMachineDtos = null;

        if (count > 0) {
            monitorMachineDtos = monitorMachineV1InnerServiceSMOImpl.queryMonitorMachineList(monitorMachineDto);
            refreshAttr(monitorMachineDtos);
            // todo  查询设备是否在线
            // queryMachineState(monitorMachineDtos);

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

    private void refreshAttr(List<MonitorMachineDto> monitorMachineDtos) {
        if (CollectionUtils.isEmpty(monitorMachineDtos)) {
            return;
        }
        for (MonitorMachineDto monitorMachineDto : monitorMachineDtos) {
            MonitorMachineAttrsDto monitorMachineAttrsDto = new MonitorMachineAttrsDto();
            monitorMachineAttrsDto.setMachineId(monitorMachineDto.getMachineId());
            monitorMachineAttrsDto.setCommunityId(monitorMachineDto.getCommunityId());
            List<MonitorMachineAttrsDto> monitorMachineAttrsDtos = monitorMachineAttrsV1InnerServiceSMOImpl.queryMonitorMachineAttrss(monitorMachineAttrsDto);
            monitorMachineDto.setMonitorMachineAttrsList(monitorMachineAttrsDtos);
        }
    }

}
