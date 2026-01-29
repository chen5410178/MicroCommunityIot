package com.java110.hal.http.yiteSmart.bmo.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.hal.http.yiteSmart.bmo.ISyncFaceBmo;
import com.java110.intf.accessControl.IAccessControlFaceV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SyncFaceBmoImpl implements ISyncFaceBmo {

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlFaceV1InnerServiceSMO accessControlFaceV1InnerServiceSMOImpl;

    @Override
    public ResponseEntity<String> sync(String machineCode,int page,int row) {


        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineCode(machineCode);

        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);

        if (ListUtil.isNull(accessControlDtos)) {
            throw new IllegalArgumentException("设备不存在");
        }



        AccessControlFaceDto accessControlFaceDto = new AccessControlFaceDto();
        accessControlFaceDto.setMachineId(accessControlDtos.get(0).getMachineId());
        accessControlFaceDto.setPage(page);
        accessControlFaceDto.setRow(row);
        List<AccessControlFaceDto> accessControlFaceDtos = accessControlFaceV1InnerServiceSMOImpl.queryYiteAccessControlFaces(accessControlFaceDto);

        if (ListUtil.isNull(accessControlFaceDtos)) {
            throw new IllegalArgumentException("设备未授权人脸");
        }

       long count = accessControlFaceV1InnerServiceSMOImpl.queryYiteAccessControlFaceCount(accessControlFaceDto);


        long curTimeTimeStamp = new Date().getTime();
        curTimeTimeStamp = curTimeTimeStamp / 1000;
        ResponseEntity<String> responseEntity = null;
        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", curTimeTimeStamp);
        JSONObject data = new JSONObject();
        data.put("issync", 1);
        data.put("timestamp", curTimeTimeStamp);
        data.put("total", count);
        data.put("del_data", new JSONArray());

        JSONArray ds = new JSONArray();
        data.put("data",ds);

        paramOut.put("data", data);

        JSONObject d = null;
        for(AccessControlFaceDto tmpAccessControlFaceDto: accessControlFaceDtos){
            d = new JSONObject();
           long expired =  DateUtil.getDateFromStringA(tmpAccessControlFaceDto.getEndTime()).getTime()/1000;
            d.put("expired",expired);
            d.put("user_uid",tmpAccessControlFaceDto.getPersonId());
            if(AccessControlFaceDto.PERSON_TYPE_OWNER.equals(tmpAccessControlFaceDto.getPersonType())) {
                d.put("phone", tmpAccessControlFaceDto.getOwnerLink());
            }else{
                d.put("phone", tmpAccessControlFaceDto.getStaffLink());
            }
            d.put("url",tmpAccessControlFaceDto.getFacePath());
            d.put("feature","");
            d.put("open","0");
            String user_uid = d.getString("user_uid");
            d.put("face_id",user_uid.substring(user_uid.length() - 9));
            d.put("source","1");
            ds.add(d);
        }



        responseEntity = new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
        return responseEntity;
    }
}
