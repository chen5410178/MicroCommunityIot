package com.java110.hal.http.yiteSmart.bmo.impl;

import com.java110.core.utils.ListUtil;
import com.java110.dto.accessControlLog.AccessControlLogDto;
import com.java110.hal.http.yiteSmart.bmo.ISyncOneUserBmo;
import com.java110.hal.http.yiteSmart.bmo.ISyncOneUserFaceBmo;
import com.java110.intf.accessControl.IAccessControlLogV1InnerServiceSMO;
import com.java110.po.accessControlLog.AccessControlLogPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SyncOneUserFaceBmoImpl implements ISyncOneUserFaceBmo {

    @Autowired
    private IAccessControlLogV1InnerServiceSMO accessControlLogV1InnerServiceSMOImpl;

    @Override
    public ResponseEntity syncUserFace(String machineCode) {


        AccessControlLogDto accessControlLogDto = new AccessControlLogDto();
        accessControlLogDto.setState(AccessControlLogDto.STATE_REQ);
        accessControlLogDto.setLogAction("/device/v2/face/sync");
        accessControlLogDto.setMachineCode(machineCode);
        List<AccessControlLogDto> accessControlLogDtos = accessControlLogV1InnerServiceSMOImpl.queryAccessControlLogs(accessControlLogDto);

        if (ListUtil.isNull(accessControlLogDtos)) {
            throw new IllegalArgumentException("没有数据");
        }

        accessControlLogDto = new AccessControlLogDto();
        accessControlLogDto.setLogId(accessControlLogDtos.get(0).getLogId());
        accessControlLogDto.setCommunityId(accessControlLogDtos.get(0).getCommunityId());

        accessControlLogDtos = accessControlLogV1InnerServiceSMOImpl.queryAccessControlLogParams(accessControlLogDto);
        if (ListUtil.isNull(accessControlLogDtos)) {
            throw new IllegalArgumentException("没有数据");
        }

        AccessControlLogPo accessControlLogPo = new AccessControlLogPo();
        accessControlLogPo.setLogId(accessControlLogDtos.get(0).getLogId());
        accessControlLogPo.setState(AccessControlLogDto.STATE_RES);
        accessControlLogPo.setResParam("已同步-" + machineCode);
        accessControlLogV1InnerServiceSMOImpl.updateAccessControlLog(accessControlLogPo);


        return new ResponseEntity(accessControlLogDtos.get(0).getReqParam(), HttpStatus.OK);
    }
}
