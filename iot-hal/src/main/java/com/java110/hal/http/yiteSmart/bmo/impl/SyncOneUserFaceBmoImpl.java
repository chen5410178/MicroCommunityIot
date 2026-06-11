package com.java110.hal.http.yiteSmart.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.utils.DateUtil;
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

import java.util.Date;
import java.util.List;

@Service
public class SyncOneUserFaceBmoImpl implements ISyncOneUserFaceBmo {

    @Autowired
    private IAccessControlLogV1InnerServiceSMO accessControlLogV1InnerServiceSMOImpl;

    @Override
    public ResponseEntity syncUserFace(String machineCode,int page,int row,String t) {



        AccessControlLogDto accessControlLogDto = new AccessControlLogDto();
//        accessControlLogDto.setState(AccessControlLogDto.STATE_REQ);
        accessControlLogDto.setLogAction("/device/v2/face/sync");
        accessControlLogDto.setMachineCode(machineCode);
        accessControlLogDto.setPage(page);
        accessControlLogDto.setRow(row);
        if (!"0".equals(t)) {
            long ts = Long.parseLong(t);
            if (ts > 9999999999L) {
                ts = ts / 1000;
            }
            Date tsDate = new Date(ts * 1000);
            tsDate.setTime(tsDate.getTime() + 1000);
            accessControlLogDto.setQueryStartTime(DateUtil.getFormatTimeStringA(tsDate));
        }
        List<AccessControlLogDto> accessControlLogDtos = accessControlLogV1InnerServiceSMOImpl.queryAccessControlLogs(accessControlLogDto);

        if (ListUtil.isNull(accessControlLogDtos)) {
            JSONObject noData = new JSONObject();
            noData.put("code", 201);
            noData.put("msg", "no data");
            noData.put("timestamp", Long.parseLong("0".equals(t) ? "0" : t));
            noData.put("data", new JSONObject());
            return new ResponseEntity(noData.toJSONString(), HttpStatus.OK);
        }

        AccessControlLogDto selectedLog = accessControlLogDtos.get(accessControlLogDtos.size() - 1);

        if (AccessControlLogDto.CMD_ADD_FACE.equals(selectedLog.getLogCmd())) {
            AccessControlLogDto checkDto = new AccessControlLogDto();
            checkDto.setState(AccessControlLogDto.STATE_REQ);
            checkDto.setLogAction("/v3_1/sync/users/add/");
            checkDto.setMachineCode(machineCode);
            checkDto.setUserId(selectedLog.getUserId());
            checkDto.setLogCmd(AccessControlLogDto.CMD_ADD_USER);
            List<AccessControlLogDto> checkList = accessControlLogV1InnerServiceSMOImpl.queryAccessControlLogs(checkDto);
            if (!ListUtil.isNull(checkList)) {
                JSONObject noData = new JSONObject();
                noData.put("code", 201);
                noData.put("msg", "no data");
                noData.put("timestamp", Long.parseLong("0".equals(t) ? "0" : t));
                noData.put("data", new JSONObject());
                return new ResponseEntity(noData.toJSONString(), HttpStatus.OK);
            }
        }

        Date createTime = selectedLog.getCreateTime();

        accessControlLogDto = new AccessControlLogDto();
        accessControlLogDto.setLogId(selectedLog.getLogId());
        accessControlLogDto.setCommunityId(selectedLog.getCommunityId());

        accessControlLogDtos = accessControlLogV1InnerServiceSMOImpl.queryAccessControlLogParams(accessControlLogDto);
        if (ListUtil.isNull(accessControlLogDtos)) {
            throw new IllegalArgumentException("没有数据");
        }

        AccessControlLogPo accessControlLogPo = new AccessControlLogPo();
        accessControlLogPo.setLogId(accessControlLogDtos.get(0).getLogId());
        accessControlLogPo.setState(AccessControlLogDto.STATE_RES);
        accessControlLogPo.setResParam("已同步-" + machineCode);
        accessControlLogV1InnerServiceSMOImpl.updateAccessControlLog(accessControlLogPo);

        long tsSec = createTime != null ? createTime.getTime() / 1000 : new Date().getTime() / 1000;
        JSONObject result = JSONObject.parseObject(accessControlLogDtos.get(0).getReqParam());
        result.put("timestamp", tsSec);

        return new ResponseEntity(result.toJSONString(), HttpStatus.OK);
    }
}
