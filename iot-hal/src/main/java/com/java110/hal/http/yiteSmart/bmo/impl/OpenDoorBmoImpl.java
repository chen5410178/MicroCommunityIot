package com.java110.hal.http.yiteSmart.bmo.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.file.FileDto;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.core.cache.CommonCache;
import com.java110.core.cache.MappingCache;
import com.java110.core.constant.MappingConstant;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.PropertyHttpFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControl.AccessControlOweFeeDto;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.accessControlInout.AccessControlInoutDto;
import com.java110.dto.user.UserDto;
import com.java110.hal.http.yiteSmart.bmo.IOpenDoorBmo;
import com.java110.intf.accessControl.IAccessControlFaceV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlInoutV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.system.IFileInnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.accessControlInout.AccessControlInoutPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenDoorBmoImpl implements IOpenDoorBmo {

    @Autowired
    private IAccessControlFaceV1InnerServiceSMO accessControlFaceV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;

    @Autowired
    private IFileInnerServiceSMO fileInnerServiceSMOImpl;

    @Autowired
    private IAccessControlInoutV1InnerServiceSMO accessControlInoutV1InnerServiceSMOImpl;


    @Override
    public ResponseEntity<String> openDoor(String machineCode, JSONObject reqJson) {

        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime() / 1000);

        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineCode(machineCode);
        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);
        if (ListUtil.isNull(accessControlDtos)) {
            throw new IllegalArgumentException("设备不存在" + machineCode);
        }

        String type = reqJson.getString("type");
        UserDto userDto = null;
        String openTypeCd = "";
        // 类型映射：FACE=1000,CARD=2000,PWD=4000,QR=3000,APP=5000,GUEST=6000,TPWD=7000
        switch (type) {
            case "FACE": openTypeCd = "1000"; break;
            case "CARD": openTypeCd = "2000"; break;
            case "PWD": openTypeCd = "4000"; break;
            case "QR": openTypeCd = "3000"; break;
            case "APP": openTypeCd = "5000"; break;
            case "GUEST": openTypeCd = "6000"; break;
            case "TPWD": openTypeCd = "7000"; break;
            default: return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
        }

// 只有 CARD 走卡号查询，其余全部走手机号查询
        if ("CARD".equals(type)) {
            userDto = queryUserDtoByCardNumber(reqJson.getString("data"), accessControlDtos.get(0).getCommunityId());
        } else {
            userDto = queryUserDtoByTel(reqJson.getString("data"), accessControlDtos.get(0).getCommunityId());
        }

        String facePath = reqJson.getString("photo");


        AccessControlInoutPo accessControlInoutPo = new AccessControlInoutPo();
        accessControlInoutPo.setMachineCode(machineCode);
        accessControlInoutPo.setCommunityId(accessControlDtos.get(0).getCommunityId());
        accessControlInoutPo.setInoutId(GenerateCodeFactory.getGeneratorId("11"));
        accessControlInoutPo.setMachineId(accessControlDtos.get(0).getMachineId());
        accessControlInoutPo.setState("C");
        accessControlInoutPo.setFacePath(facePath);
        accessControlInoutPo.setName(userDto.getName());
        accessControlInoutPo.setIdCard("-1");
        accessControlInoutPo.setOpenTypeCd(openTypeCd);
        accessControlInoutPo.setSimilar(100 + "");
        accessControlInoutPo.setTel(userDto.getTel());
        accessControlInoutV1InnerServiceSMOImpl.saveAccessControlInout(accessControlInoutPo);

        CommonCache.setValue(facePath,accessControlInoutPo.getInoutId(),CommonCache.defaultExpireTime);

        return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> createPhoto(String facePath, String image) {
        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime() / 1000);

        ResponseEntity<String> responseEntity = new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);

        String inoutId = CommonCache.getAndRemoveValue(facePath);

        if(StringUtil.isEmpty(inoutId)){
            return responseEntity;
        }


        AccessControlInoutDto accessControlInoutDto = new AccessControlInoutDto();
        accessControlInoutDto.setInoutId(inoutId);
        List<AccessControlInoutDto> accessControlInoutDtos = accessControlInoutV1InnerServiceSMOImpl.queryAccessControlInouts(accessControlInoutDto);
        if(ListUtil.isNull(accessControlInoutDtos)){
            return responseEntity;
        }



        FileDto fileDto = new FileDto();
        fileDto.setFileId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_file_id));
        fileDto.setFileName(fileDto.getFileId());
        fileDto.setContext(image);
        fileDto.setSuffix("jpeg");
        fileDto.setCommunityId(accessControlInoutDtos.get(0).getCommunityId());
        String fileName = fileInnerServiceSMOImpl.saveFile(fileDto);
        String imgUrl = MappingCache.getValue(MappingConstant.FILE_DOMAIN, "IMG_PATH");

        facePath = imgUrl + fileName;

        AccessControlInoutPo accessControlInoutPo = new AccessControlInoutPo();
        accessControlInoutPo.setInoutId(inoutId);
        accessControlInoutPo.setFacePath(facePath);
        accessControlInoutV1InnerServiceSMOImpl.updateAccessControlInout(accessControlInoutPo);
        return responseEntity;
    }


    private UserDto queryUserDtoByCardNumber(String data, String communityId) {

        AccessControlFaceDto accessControlFaceDto = new AccessControlFaceDto();
        accessControlFaceDto.setCommunityId(communityId);
        accessControlFaceDto.setCardNumber(data);
        List<AccessControlFaceDto> accessControlFaceDtos = accessControlFaceV1InnerServiceSMOImpl.queryAccessControlFaces(accessControlFaceDto);
        UserDto userDto = new UserDto();
        if (ListUtil.isNull(accessControlFaceDtos)) {
            userDto.setUserId("-1");
            userDto.setName("未知");
            userDto.setTel("11111111111");
            return userDto;
        }

        userDto.setUserId(accessControlFaceDtos.get(0).getPersonId());
        userDto.setName(accessControlFaceDtos.get(0).getName());

        String personId = accessControlFaceDtos.get(0).getPersonId();
        String personType = accessControlFaceDtos.get(0).getPersonType();
        if ("OWNER".equals(personType)) {
            OwnerDto ownerDto = new OwnerDto();
            ownerDto.setMemberId(personId);
            List<OwnerDto> ownerDtos = ownerV1InnerServiceSMOImpl.queryOwners(ownerDto);
            userDto.setTel(!ListUtil.isNull(ownerDtos) ? ownerDtos.get(0).getLink() : null);
        } else if ("STAFF".equals(personType)) {
            UserDto staffDto = new UserDto();
            staffDto.setUserId(personId);
            List<UserDto> staffDtos = userV1InnerServiceSMOImpl.queryUsers(staffDto);
            userDto.setTel(!ListUtil.isNull(staffDtos) ? staffDtos.get(0).getTel() : null);
        }
        if (StringUtil.isEmpty(userDto.getTel())) {
            userDto.setTel("11111111111");
        }
        return userDto;
    }

    private UserDto queryUserDtoByTel(String data, String communityId) {

        UserDto userDto = new UserDto();
        userDto.setTel(data);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        if (!ListUtil.isNull(userDtos)) {
            return userDtos.get(0);
        }
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setLink(data);
        ownerDto.setCommunityId(communityId);
        List<OwnerDto> ownerDtos = ownerV1InnerServiceSMOImpl.queryOwners(ownerDto);
        if (ListUtil.isNull(ownerDtos)) {
            userDto.setUserId("-1");
            userDto.setName("未知");
            userDto.setTel(data);
            return userDto;
        }

        userDto.setUserId(ownerDtos.get(0).getMemberId());
        userDto.setName(ownerDtos.get(0).getName());
        userDto.setTel(ownerDtos.get(0).getLink());
        return userDto;
    }
}
