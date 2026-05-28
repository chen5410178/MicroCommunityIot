package com.java110.hal.http.yiteSmart.bmo.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.unit.UnitDto;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.hal.http.yiteSmart.bmo.IManagerBuildingsBmo;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.community.IUnitV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManagerBuildingsBmoImpl implements IManagerBuildingsBmo {

    Logger logger = LoggerFactory.getLogger(ManagerBuildingsBmoImpl.class);

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Autowired
    private IUnitV1InnerServiceSMO unitV1InnerServiceSMOImpl;

    @Override
    public ResponseEntity<String> buildings(String uuid) {

        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineCode(uuid);
        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);

        if (ListUtil.isNull(accessControlDtos)) {
            throw new IllegalArgumentException("管理机设备不存在");
        }

        String communityId = accessControlDtos.get(0).getCommunityId();

        UnitDto unitDto = new UnitDto();
        unitDto.setCommunityId(communityId);
        List<UnitDto> unitDtos = unitV1InnerServiceSMOImpl.queryUnits(unitDto);

        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime() / 1000);

        JSONArray data = new JSONArray();
        paramOut.put("data", data);

        if (!ListUtil.isNull(unitDtos)) {
            for (UnitDto unit : unitDtos) {
                JSONObject building = new JSONObject();
                building.put("uid", unit.getUnitId());
                building.put("building_name", unit.getFloorNum() + "栋" + unit.getUnitNum() + "单元");
                building.put("building_number", String.format("%02d%02d",
                        Integer.parseInt(unit.getFloorNum()), Integer.parseInt(unit.getUnitNum())));
                data.add(building);
            }
        }

        logger.debug("管理机楼栋列表返回：{}", paramOut.toString());
        return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
    }
}
