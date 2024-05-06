package com.java110.hal.http.yiteSmart.bmo.impl;

import com.java110.core.utils.ListUtil;
import com.java110.dto.accessControlLog.AccessControlLogDto;
import com.java110.hal.http.yiteSmart.bmo.ISyncOneUserBmo;
import com.java110.hal.http.yiteSmart.bmo.ISyncUserBidBmo;
import com.java110.intf.accessControl.IAccessControlLogV1InnerServiceSMO;
import com.java110.po.accessControlLog.AccessControlLogPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SyncOneUserBmoImpl implements ISyncOneUserBmo {

    @Autowired
    private IAccessControlLogV1InnerServiceSMO accessControlLogV1InnerServiceSMOImpl;

    @Override
    public ResponseEntity syncUser(String machineCode) {


        AccessControlLogDto accessControlLogDto = new AccessControlLogDto();
        accessControlLogDto.setState(AccessControlLogDto.STATE_REQ);
        accessControlLogDto.setLogAction("/v3_1/sync/users/add/");
        List<AccessControlLogDto> accessControlLogDtos = accessControlLogV1InnerServiceSMOImpl.queryAccessControlLogs(accessControlLogDto);

        if (ListUtil.isNull(accessControlLogDtos)) {
            throw new IllegalArgumentException("没有数据");
        }

        accessControlLogDto = new AccessControlLogDto();
        accessControlLogDto.setLogId(accessControlLogDtos.get(0).getLogId());
        accessControlLogDto.setCommunityId(accessControlLogDtos.get(0).getLogId());
        accessControlLogDtos = accessControlLogV1InnerServiceSMOImpl.queryAccessControlLogParams(accessControlLogDto);
        if (ListUtil.isNull(accessControlLogDtos)) {
            throw new IllegalArgumentException("没有数据");
        }

        AccessControlLogPo accessControlLogPo = new AccessControlLogPo();
        accessControlLogPo.setLogId(accessControlLogDtos.get(0).getLogId());
        accessControlLogPo.setState(AccessControlLogDto.STATE_RES);
        accessControlLogPo.setResParam("已同步");
        accessControlLogV1InnerServiceSMOImpl.updateAccessControlLog(accessControlLogPo);


        return new ResponseEntity(accessControlLogDtos.get(0).getReqParam(), HttpStatus.OK);
    }
}
