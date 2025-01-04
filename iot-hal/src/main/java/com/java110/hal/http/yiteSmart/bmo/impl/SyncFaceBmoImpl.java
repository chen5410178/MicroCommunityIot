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
        accessControlDto.setPage(page);
        accessControlDto.setRow(row);
        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);

        if (ListUtil.isNull(accessControlDtos)) {
            throw new IllegalArgumentException("设备不存在");
        }

        AccessControlFaceDto accessControlFaceDto = new AccessControlFaceDto();
        accessControlFaceDto.setMachineId(accessControlDtos.get(0).getMachineId());

        List<AccessControlFaceDto> accessControlFaceDtos = accessControlFaceV1InnerServiceSMOImpl.queryYiteAccessControlFaces(accessControlFaceDto);

        if (ListUtil.isNull(accessControlFaceDtos)) {
            throw new IllegalArgumentException("设备未授权人脸");
        }

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
        data.put("total", accessControlFaceDtos.size());
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
            d.put("feature","8da496105de1101541a69b980e38631c61c81581dda4a50535264b083aa5ff0cee7cca3082e27b3575efa13865a38c3d4f819ca017dbc9a404572c288a479dad04b1d0d0026f6754f13c2cda22d359dc87156a40b5f06444cd8ed9481282364dc095f6f0cee2b3743c9b62f9ebccde03458eaae28d1a766405f07ee812e06c6d3e0cbe907ee0249565fdb1980082989d510b38803a44268475c2688890a8fe0cdbcd98b024860bb4a81c28b8677208c3a04bc1a0d367d5a4fff3a629613b6a2cc77a5a50bb7ae5549c916b58c6f5c45d7b4016405107b3c5d4a122c8b857d44d7869668fa00dedf48962f7f8991891fc3cadf7e07b9ecc657ecf74e80369ccec422ac81068bb699458c03799ac94329dcfd9440009f0c904bfce2388f119bef38342a8300961f4b5035f15b96ed96bbdd756a520fb486f25cfb3fca8b72b38ace37c7b508ed77d54fd376e588ac382dcc4e521c07dc0de4532ba7c49a57a704d6a8f7ef0cc57daf46ce9bbf97bdac67e8cd5c1e04e6e726486aad86884aa7bec6311ff90569a019430ce0719825eac1c6b239682c5e18884ed4f0b89197bb78d4c63b03124d295b5f15c01b84efaa6bcf31cd121df59c325b3d807d74393262c38060b510b88dfd4a08bcb598277ba5d861d034304863ec461e09349cc7cf8cc8d65cef1975dc875d54650789a49e7fdbc5e1562366b47e4bc6c9f68d04167ec");
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
