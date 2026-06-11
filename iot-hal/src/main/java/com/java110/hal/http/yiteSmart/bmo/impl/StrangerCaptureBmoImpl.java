package com.java110.hal.http.yiteSmart.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.file.FileDto;
import com.java110.core.cache.CommonCache;
import com.java110.core.cache.MappingCache;
import com.java110.core.constant.MappingConstant;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.hal.http.yiteSmart.bmo.IStrangerCaptureBmo;
import com.java110.intf.accessControl.IAccessControlInoutV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.system.IFileInnerServiceSMO;
import com.java110.po.accessControlInout.AccessControlInoutPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StrangerCaptureBmoImpl implements IStrangerCaptureBmo {

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlInoutV1InnerServiceSMO accessControlInoutV1InnerServiceSMOImpl;

    @Autowired
    private IFileInnerServiceSMO fileInnerServiceSMOImpl;

    @Override
    public ResponseEntity<String> capture(String machineCode, JSONObject reqJson) {
        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime() / 1000);

        AccessControlDto acDto = new AccessControlDto();
        acDto.setMachineCode(machineCode);
        List<AccessControlDto> acDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(acDto);
        if (ListUtil.isNull(acDtos)) {
            throw new IllegalArgumentException("设备不存在" + machineCode);
        }

        String openTypeCd = mapCaptureType(reqJson.getString("capture_type"));

        Integer why = reqJson.getInteger("why");
        String similar = why != null ? String.valueOf(why) : "0";

        String capturePhoto = reqJson.getString("capture_photo");

        AccessControlInoutPo po = new AccessControlInoutPo();
        po.setInoutId(GenerateCodeFactory.getGeneratorId("11"));
        po.setMachineCode(machineCode);
        po.setMachineId(acDtos.get(0).getMachineId());
        po.setCommunityId(acDtos.get(0).getCommunityId());
        po.setState("F");
        po.setName("陌生人");
        po.setIdCard("");
        po.setOpenTypeCd(openTypeCd);
        po.setFacePath(capturePhoto);
        po.setTel(reqJson.getString("ext"));
        po.setSimilar(similar);

        accessControlInoutV1InnerServiceSMOImpl.saveAccessControlInout(po);

        String pendingImage = CommonCache.getAndRemoveValue(capturePhoto + "_image");
        if (!StringUtil.isEmpty(pendingImage)) {
            FileDto fileDto = new FileDto();
            fileDto.setFileId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_file_id));
            fileDto.setFileName(fileDto.getFileId());
            fileDto.setContext(pendingImage);
            fileDto.setSuffix("jpeg");
            fileDto.setCommunityId(acDtos.get(0).getCommunityId());
            String fileName = fileInnerServiceSMOImpl.saveFile(fileDto);
            String imgUrl = MappingCache.getValue(MappingConstant.FILE_DOMAIN, "IMG_PATH");
            String fullFacePath = imgUrl + fileName;

            AccessControlInoutPo updatePo = new AccessControlInoutPo();
            updatePo.setInoutId(po.getInoutId());
            updatePo.setFacePath(fullFacePath);
            accessControlInoutV1InnerServiceSMOImpl.updateAccessControlInout(updatePo);
        } else {
            CommonCache.setValue(capturePhoto, po.getInoutId(),
                    CommonCache.defaultExpireTime);
        }

        return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
    }

    private String mapCaptureType(String type) {
        switch (type) {
            case "FACE":    return "1000";
            case "CARD":    return "2000";
            case "QR":      return "3000";
            case "PWD":     return "4000";
            case "APP":     return "5000";
            case "TMP_PWD": return "6000";
            case "TPWD":    return "7000";
            case "SWITCH":  return "9000";
            case "ID_CARD": return "10000";
            case "WEB":     return "11000";
            case "WYJ":     return "12000";
            case "SNJ":     return "13000";
            case "ANALOG":  return "14000";
            default:        return "9999";
        }
    }
}
