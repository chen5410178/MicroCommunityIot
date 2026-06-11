package com.java110.hal.http.yiteSmart.bmo.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.community.CommunityDto;
import com.java110.hal.http.yiteSmart.bmo.IManagerGateListBmo;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.hal.http.yiteSmart.util.Gcj02ToBd09Util;
import com.java110.intf.community.ICommunityInnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ManagerGateListBmoImpl implements IManagerGateListBmo {

    Logger logger = LoggerFactory.getLogger(ManagerGateListBmoImpl.class);

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Autowired
    private ICommunityInnerServiceSMO communityInnerServiceSMOImpl;

    @Override
    public ResponseEntity<String> gateList(String uuid) {

        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineCode(uuid);
        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);

        if (ListUtil.isNull(accessControlDtos)) {
            throw new IllegalArgumentException("管理机设备不存在");
        }

        String communityId = accessControlDtos.get(0).getCommunityId();

        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(communityId);
        List<CommunityDto> communityDtos = communityInnerServiceSMOImpl.queryCommunitys(communityDto);
        String communityLat = "39.907881821019075";
        String communityLng = "116.39803241981451";
        if (!ListUtil.isNull(communityDtos)) {
            communityLat = communityDtos.get(0).getLat();
            communityLng = communityDtos.get(0).getLng();
        }

        AccessControlDto queryDto = new AccessControlDto();
        queryDto.setCommunityId(communityId);
        List<AccessControlDto> allMachines = accessControlV1InnerServiceSMOImpl.queryAccessControls(queryDto);

        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime() / 1000);

        JSONObject data = new JSONObject();
        data.put("timestamp", DateUtil.getCurrentDate().getTime() / 1000);
        data.put("project_id", communityId);
        paramOut.put("data", data);

        JSONArray add = new JSONArray();
        data.put("add", add);

        if (!ListUtil.isNull(allMachines)) {
            for (AccessControlDto machine : allMachines) {
                if ("3".equals(machine.getMachineMac())) {
                    continue;
                }
                JSONObject gate = new JSONObject();
                gate.put("uid", machine.getMachineId());
                String mLat = machine.getLat();
                String mLng = machine.getLng();
                if (StringUtil.isEmpty(mLat)) mLat = communityLat;
                if (StringUtil.isEmpty(mLng)) mLng = communityLng;
                double[] bd = Gcj02ToBd09Util.convert(mLng, mLat);
                String bdLng = String.valueOf(bd[0]);
                String bdLat = String.valueOf(bd[1]);
                gate.put("gate_gps", bdLng + "," + bdLat);
                gate.put("gate_guid", machine.getMachineCode());
                gate.put("gate_name", machine.getMachineName());
                gate.put("project_id", communityId);
                gate.put("gate_latitude", bdLat);
                gate.put("gate_longitude", bdLng);
                gate.put("gate_online", isOnline(machine.getHeartbeatTime()) ? "Y" : "N");
                add.add(gate);
            }
        }

        logger.debug("管理机设备列表返回：{}", paramOut.toString());
        return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
    }

    private boolean isOnline(String heartbeatTime) {
        if (StringUtil.isEmpty(heartbeatTime)) {
            return false;
        }
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(DateUtil.getDateFromString(heartbeatTime, DateUtil.DATE_FORMATE_STRING_A));
            calendar.add(Calendar.MINUTE, 2);
            return calendar.getTime().getTime() > DateUtil.getCurrentDate().getTime();
        } catch (ParseException e) {
            return false;
        }
    }
}
