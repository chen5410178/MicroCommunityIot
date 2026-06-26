package com.java110.hal.http.yiteSmart.bmo.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControlLog.AccessControlLogDto;
import com.java110.hal.http.yiteSmart.bmo.ISyncPasswordsAddBmo;
import com.java110.intf.accessControl.IAccessControlLogV1InnerServiceSMO;
import com.java110.po.accessControlLog.AccessControlLogPo;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SyncPasswordsAddBmoImpl implements ISyncPasswordsAddBmo {

    Logger logger = LoggerFactory.getLogger(SyncPasswordsAddBmoImpl.class);

    @Autowired
    private IAccessControlLogV1InnerServiceSMO accessControlLogV1InnerServiceSMOImpl;

    @Override
    public ResponseEntity syncPasswordsAdd(String machineCode, String t) {

        try {
            AccessControlLogDto accessControlLogDto = new AccessControlLogDto();
            accessControlLogDto.setLogAction("/v3_1/sync/passwords/add/sync");
            accessControlLogDto.setMachineCode(machineCode);
            accessControlLogDto.setState(AccessControlLogDto.STATE_REQ);

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

            JSONArray passwordArray = new JSONArray();
            long maxTsSec = 0;

            for (int i = 0; i < accessControlLogDtos.size(); i++) {
                AccessControlLogDto logDto = accessControlLogDtos.get(i);

                AccessControlLogDto paramQuery = new AccessControlLogDto();
                paramQuery.setLogId(logDto.getLogId());
                paramQuery.setCommunityId(logDto.getCommunityId());
                List<AccessControlLogDto> paramDtos = accessControlLogV1InnerServiceSMOImpl.queryAccessControlLogParams(paramQuery);
                if (ListUtil.isNull(paramDtos)) {
                    continue;
                }

                AccessControlLogPo accessControlLogPo = new AccessControlLogPo();
                accessControlLogPo.setLogId(paramDtos.get(0).getLogId());
                accessControlLogPo.setState(AccessControlLogDto.STATE_RES);
                accessControlLogPo.setResParam("已同步-" + machineCode);
                accessControlLogV1InnerServiceSMOImpl.updateAccessControlLog(accessControlLogPo);

                String reqParam = paramDtos.get(0).getReqParam();
                if (StringUtil.isEmpty(reqParam)) {
                    continue;
                }
                JSONObject recordData = JSONObject.parseObject(reqParam);
                if (recordData.containsKey("data") && recordData.getJSONObject("data") != null) {
                    JSONArray pwdArr = recordData.getJSONObject("data").getJSONArray("password");
                    if (pwdArr != null) {
                        passwordArray.addAll(pwdArr);
                    }
                }
                long tsSec = logDto.getCreateTime() != null ? logDto.getCreateTime().getTime() / 1000 : new Date().getTime() / 1000;
                if (tsSec > maxTsSec) {
                    maxTsSec = tsSec;
                }
            }

            if (passwordArray.isEmpty()) {
                JSONObject noData = new JSONObject();
                noData.put("code", 201);
                noData.put("msg", "no data");
                noData.put("timestamp", Long.parseLong("0".equals(t) ? "0" : t));
                noData.put("data", new JSONObject());
                return new ResponseEntity(noData.toJSONString(), HttpStatus.OK);
            }

            JSONObject result = new JSONObject();
            result.put("code", 200);
            result.put("msg", "success");
            JSONObject data = new JSONObject();
            data.put("password", passwordArray);
            result.put("data", data);
            result.put("timestamp", maxTsSec);

            return new ResponseEntity(result.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("syncPasswordsAdd 异常", e);
            JSONObject error = new JSONObject();
            error.put("code", 500);
            error.put("msg", StringUtil.isEmpty(e.getMessage()) ? e.getClass().getSimpleName() : e.getMessage());
            error.put("timestamp", System.currentTimeMillis() / 1000);
            error.put("data", new JSONObject());
            return new ResponseEntity(error.toJSONString(), HttpStatus.OK);
        }
    }
}
