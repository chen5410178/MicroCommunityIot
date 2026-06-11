package com.java110.hal.http.yiteSmart.bmo.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.unit.UnitDto;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.hal.http.yiteSmart.bmo.ICheckCardBmo;
import com.java110.intf.accessControl.IAccessControlFaceV1InnerServiceSMO;
import com.java110.intf.community.IUnitV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckCardBmoImpl implements ICheckCardBmo {

    Logger logger = LoggerFactory.getLogger(CheckCardBmoImpl.class);

    @Autowired
    private IAccessControlFaceV1InnerServiceSMO accessControlFaceV1InnerServiceSMOImpl;

    @Autowired
    private IUnitV1InnerServiceSMO unitV1InnerServiceSMOImpl;

    @Override
    public ResponseEntity<String> checkCard(String uuid, String machineId, String cardCode) {
        long timestamp = DateUtil.getCurrentDate().getTime() / 1000;

        AccessControlFaceDto faceDto = new AccessControlFaceDto();
        faceDto.setCardNumber(cardCode.toUpperCase());
        faceDto.setMachineId(machineId);
        faceDto.setStatusCd("0");
        List<AccessControlFaceDto> faces = accessControlFaceV1InnerServiceSMOImpl.queryAccessControlFaces(faceDto);

        if (ListUtil.isNull(faces)) {
            return buildCardNotFoundResponse(cardCode, timestamp);
        }

        AccessControlFaceDto face = faces.get(0);
        String personType = face.getPersonType();
        String roomName = face.getRoomName();
        String endTime = face.getEndTime();

        JSONObject result = new JSONObject();
        result.put("code", 200);
        result.put("msg", "ok");
        result.put("timestamp", timestamp);

        JSONObject data = new JSONObject();
        JSONArray cardsArray = new JSONArray();
        JSONObject cardObj = new JSONObject();

        JSONArray addArray = new JSONArray();
        JSONObject addCard = new JSONObject();
        addCard.put("card", cardCode.toUpperCase());
        addCard.put("expired", parseEndTimeToEpoch(endTime));
        cardObj.put("add", addArray);
        cardObj.put("del", new JSONArray());

        if (AccessControlFaceDto.PERSON_TYPE_STAFF.equals(personType)) {
            addCard.put("roomnum", "9909-909");
            addArray.add(addCard);
            cardObj.put("b_id", "-1");
            JSONArray bNameArray = new JSONArray();
            bNameArray.add("物业中心");
            cardObj.put("b_name", bNameArray);
        } else {
            String unitId = "-1";
            String bName = "";
            String roomnum = "";
            if (!StringUtil.isEmpty(roomName)) {
                String[] parts = roomName.split("-", 3);
                if (parts.length >= 3) {
                    String floorNum = parts[0];
                    String unitNum = parts[1];
                    String roomNum = parts[2];
                    String mappedRoomNum = convertRoomNum(roomNum);
                    roomnum = String.format("%02d%02d-%04d",
                            Integer.parseInt(floorNum),
                            Integer.parseInt(unitNum),
                            Integer.parseInt(mappedRoomNum));

                    UnitDto unitDto = new UnitDto();
                    unitDto.setFloorNum(floorNum);
                    unitDto.setUnitNum(unitNum);
                    List<UnitDto> units = unitV1InnerServiceSMOImpl.queryUnits(unitDto);

                    if (!ListUtil.isNull(units)) {
                        unitId = units.get(0).getUnitId();
                        bName = floorNum + "栋" + unitNum + "单元";
                    }
                }
            }

            addCard.put("roomnum", roomnum);
            addArray.add(addCard);

            cardObj.put("b_id", unitId);
            JSONArray bNameArray = new JSONArray();
            bNameArray.add(bName);
            cardObj.put("b_name", bNameArray);
        }

        cardsArray.add(cardObj);
        data.put("cards", cardsArray);
        result.put("data", data);

        logger.debug("/v3_1/check/card, uuid={}, cardCode={}, response={}", uuid, cardCode, result.toJSONString());
        return new ResponseEntity<>(result.toJSONString(), HttpStatus.OK);
    }

    private ResponseEntity<String> buildCardNotFoundResponse(String cardCode, long timestamp) {
        JSONObject result = new JSONObject();
        result.put("code", 204);
        result.put("msg", "[" + cardCode + "] 卡号查询失败，请检查您的卡是否录入到物业系统，门卡是否能正常识别，设备是否支持门卡类型");
        result.put("timestamp", timestamp);
        return new ResponseEntity<>(result.toJSONString(), HttpStatus.OK);
    }

    private long parseEndTimeToEpoch(String endTime) {
        if (StringUtil.isEmpty(endTime)) {
            return DateUtil.getCurrentDate().getTime() / 1000;
        }
        try {
            return DateUtil.getDateFromStringA(endTime).getTime() / 1000;
        } catch (Exception e) {
            logger.warn("解析endTime失败: {}", endTime, e);
            return DateUtil.getCurrentDate().getTime() / 1000;
        }
    }

    private String convertRoomNum(String roomNum) {
        switch (roomNum) {
            case "1B01":
                return "2201";
            case "1B02":
                return "2202";
            case "1A01":
                return "2301";
            case "1A02":
                return "2302";
            case "17A1":
                return "2401";
            case "17A2":
                return "2402";
            default:
                return roomNum;
        }
    }
}
