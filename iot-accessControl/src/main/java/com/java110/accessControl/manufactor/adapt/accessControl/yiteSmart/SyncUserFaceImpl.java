package com.java110.accessControl.manufactor.adapt.accessControl.yiteSmart;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.accessControlFloor.AccessControlFloorDto;
import com.java110.intf.accessControl.IAccessControlFaceV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlFloorV1InnerServiceSMO;
import com.java110.po.accessControlFace.AccessControlFacePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class SyncUserFaceImpl implements ISyncUserFace {

    @Autowired
    private IAccessControlFloorV1InnerServiceSMO accessControlFloorV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlFaceV1InnerServiceSMO accessControlFaceV1InnerServiceSMOImpl;


    @Override
    public JSONObject getUserFace(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {


        AccessControlFaceDto accessControlFaceDto = new AccessControlFaceDto();
        accessControlFaceDto.setMfId(accessControlFacePo.getMfId());
        List<AccessControlFaceDto> accessControlFaceDtos = accessControlFaceV1InnerServiceSMOImpl.queryYiteAccessControlFaces(accessControlFaceDto);

        if (ListUtil.isNull(accessControlFaceDtos)) {
            throw new IllegalArgumentException("设备未授权人脸");
        }

        long curTimeTimeStamp = new Date().getTime();
        curTimeTimeStamp = curTimeTimeStamp / 1000;
        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", curTimeTimeStamp);
        JSONObject data = new JSONObject();
        data.put("issync", 1);
        data.put("timestamp", curTimeTimeStamp);
        data.put("total", accessControlFaceDtos.size());
        data.put("del_data", new JSONArray());

        JSONArray ds = new JSONArray();
        data.put("data", ds);

        paramOut.put("data", data);

        JSONObject d = null;
        for (AccessControlFaceDto tmpAccessControlFaceDto : accessControlFaceDtos) {
            d = new JSONObject();
            long expired = DateUtil.getDateFromStringA(tmpAccessControlFaceDto.getEndTime()).getTime() / 1000;
            d.put("expired", expired);
            d.put("user_uid", tmpAccessControlFaceDto.getPersonId());
            if (AccessControlFaceDto.PERSON_TYPE_OWNER.equals(tmpAccessControlFaceDto.getPersonType())) {
                d.put("phone", tmpAccessControlFaceDto.getOwnerLink());
            } else {
                d.put("phone", tmpAccessControlFaceDto.getStaffLink());
            }
            d.put("url", tmpAccessControlFaceDto.getFacePath());
            d.put("feature", "");
            d.put("open", "0");
            String user_uid = d.getString("user_uid");
            Random random = new Random();
            int faceId = random.nextInt(100);
            //d.put("face_id", phone.substring(6) + faceId);
            d.put("face_id", user_uid.substring(user_uid.length() - 9));
            d.put("source", "1");
            ds.add(d);
        }
        return paramOut;
    }
}
