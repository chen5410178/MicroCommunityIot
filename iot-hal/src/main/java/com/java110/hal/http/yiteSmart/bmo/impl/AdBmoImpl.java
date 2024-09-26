package com.java110.hal.http.yiteSmart.bmo.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.accessControlAd.AccessControlAdDto;
import com.java110.dto.accessControlAdRel.AccessControlAdRelDto;
import com.java110.hal.http.yiteSmart.bmo.IAdBmo;
import com.java110.intf.accessControl.IAccessControlAdRelV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlAdV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class AdBmoImpl implements IAdBmo {

    @Autowired
    private IAccessControlAdV1InnerServiceSMO accessControlAdV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlAdRelV1InnerServiceSMO accessControlAdRelV1InnerServiceSMOImpl;

    @Override
    public ResponseEntity<String> getAd(String machineCode, JSONObject reqJson) {


        AccessControlAdRelDto accessControlAdRelDto = new AccessControlAdRelDto();
        accessControlAdRelDto.setMachineCode(machineCode);
        List<AccessControlAdRelDto> accessControlAdRelDtos = accessControlAdRelV1InnerServiceSMOImpl.queryAccessControlAdRels(accessControlAdRelDto);
        int local = 0;
        if (!ListUtil.isNull(accessControlAdRelDtos)) {
            local = accessControlAdRelDtos.size();
        }

        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "ok");
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.MINUTE,-5);
//        paramOut.put("timetamp", calendar.getTime().getTime());

        paramOut.put("timetamp", (DateUtil.getCurrentDate().getTime())/1000);
        paramOut.put("type", "local");

        JSONObject data = new JSONObject();
        paramOut.put("data", data);
        JSONObject num = new JSONObject();
        data.put("num", num);
        num.put("local", local);
        num.put("baidu", 0);
        num.put("local_flag", 1);
        num.put("baidu_flag", 1);
        num.put("gate_ad_report", 0);
        num.put("gate_baidu_ad_report", 0);

        JSONArray datas = new JSONArray();
        data.put("data", datas);
        ResponseEntity<String> responseEntity = null;
        if (ListUtil.isNull(accessControlAdRelDtos)) {
            responseEntity = new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
            return responseEntity;
        }
        JSONObject ad = null;
        for (AccessControlAdRelDto tmpAccessControlAdRelDto : accessControlAdRelDtos) {
            ad = new JSONObject();
            /**
             * "ad_id": "5a29e637c95c895ab2b9da1319f01cb4",
             * 				"uid": "5a29e637c95c895ab2b9da1319f01cb4",
             * 				"ad_url": "",
             * 				"ad_type": "img",
             * 				"ad_md5": "",
             * 				"s_time": 1621180800,
             * 				"e_time": 1627660800,
             * 				"name": "s78uglnvyp8447bpcv.png",
             * 				"url": "https://ad-picture.***.com/s78uglnvyp8447bpcv.png?attname=s78uglnvyp8447bpcv.png",
             * 				"type": 1,
             * 				"count": "99999999999",
             * 				"secord": 15
             */
            ad.put("ad_id", tmpAccessControlAdRelDto.getAdId());
            ad.put("uid", tmpAccessControlAdRelDto.getAdId());
            ad.put("ad_url", "");
            if (AccessControlAdRelDto.AD_TYPE_IMG.equals(tmpAccessControlAdRelDto.getAdType())) {
                ad.put("ad_type", "img");
            } else {
                ad.put("ad_type", "video");
            }
            ad.put("ad_md5", "");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            ad.put("s_time", (calendar.getTime().getTime())/1000);
            try {
                ad.put("e_time", (DateUtil.getLastDate().getTime())/1000);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            ad.put("name", tmpAccessControlAdRelDto.getAdName());
            ad.put("url", tmpAccessControlAdRelDto.getUrl());
            ad.put("type", 1);
            ad.put("count", "99999999999");
            ad.put("secord", 60);
            datas.add(ad);
        }

        responseEntity = new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
        return responseEntity;
    }
}
