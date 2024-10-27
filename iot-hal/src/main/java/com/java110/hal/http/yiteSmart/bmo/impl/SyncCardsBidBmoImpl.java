package com.java110.hal.http.yiteSmart.bmo.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.owner.OwnerRoomRelDto;
import com.java110.bean.dto.room.RoomDto;
import com.java110.bean.dto.unit.UnitDto;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.hal.http.yiteSmart.YiteSmartHal;
import com.java110.hal.http.yiteSmart.bmo.ISyncCardsBidBmo;
import com.java110.intf.accessControl.IAccessControlFaceV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.community.IRoomV1InnerServiceSMO;
import com.java110.intf.community.IUnitInnerServiceSMO;
import com.java110.intf.user.IOwnerRoomRelV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SyncCardsBidBmoImpl implements ISyncCardsBidBmo {

    Logger logger = LoggerFactory.getLogger(YiteSmartHal.class);

    @Autowired
    private IRoomV1InnerServiceSMO roomV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerRoomRelV1InnerServiceSMO ownerRoomRelV1InnerServiceSMOImpl;

    @Autowired
    private IUnitInnerServiceSMO unitInnerServiceSMOImpl;

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlFaceV1InnerServiceSMO accessControlFaceV1InnerServiceSMOImpl;

    /**
     * {
     * "code": 200,
     * "msg": "success",
     * "data": {
     * "cards": [
     * {
     * "add": [
     * {
     * "card": "a1b2c3d4",
     * "expired": 1744646400,
     * "roomnum": "0204-1201" //绑定房间的
     * }
     * ],
     * "del": [],
     * "b_id": 287,
     * "b_name": [
     * "2栋4单元"
     * ]
     * }
     * ]
     * },
     * "timestamp": 1713194440
     * }
     *
     * @param unitId
     * @return
     */
    @Override
    public ResponseEntity<String> sync(String unitId, String machineCode) {

        if ("-1".equals(unitId)) {
            return syncStaff(unitId, machineCode);
        } else {
            return syncOwner(unitId);
        }

    }

    private ResponseEntity<String> syncStaff(String unitId, String machineCode) {

        long curTimeTimeStamp = new Date().getTime();
        curTimeTimeStamp = curTimeTimeStamp / 1000;


        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", curTimeTimeStamp);
        JSONObject data = new JSONObject();
        paramOut.put("data", data);


        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineCode(machineCode);
        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);

        if (ListUtil.isNull(accessControlDtos)) {
            throw new IllegalArgumentException("设备不存在");
        }


        AccessControlFaceDto accessControlFaceDto = new AccessControlFaceDto();
        accessControlFaceDto.setMachineId(accessControlDtos.get(0).getMachineId());
        accessControlFaceDto.setPersonType(AccessControlFaceDto.PERSON_TYPE_STAFF);
        List<AccessControlFaceDto> accessControlFaceDtos = accessControlFaceV1InnerServiceSMOImpl.queryYiteAccessControlFaces(accessControlFaceDto);
        if (ListUtil.isNull(accessControlFaceDtos)) {
            return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
        }

        /**
         * "cards": [
         *                            {
         *     				"add": [
         *                        {
         *     						"card": "a1b2c3d4",
         *     						"expired": 1744646400,
         *     						"roomnum": "0204-1201" //绑定房间的
         *                        }
         *     				],
         *     				"del": [],
         *     				"b_id": 287,
         *     				"b_name": [
         *     					"2栋4单元"
         *     				]
         *                }
         *     		]
         */
        JSONArray cards = new JSONArray();
        JSONObject card = new JSONObject();
        card.put("b_id", unitId);
        card.put("b_name", "物业中心");

        JSONArray addCards = new JSONArray();
        card.put("add", addCards);
        JSONArray del = new JSONArray();
        card.put("del", del);

        cards.add(card);
        data.put("cards", cards);

        JSONObject addCard = null;

        for (AccessControlFaceDto tmpAccessControlFaceDto : accessControlFaceDtos) {
            addCard = new JSONObject();
            addCard.put("card", tmpAccessControlFaceDto.getCardNumber());
            addCard.put("roomnum", "9909-909");//添加楼栋单元号
            long timestrap = DateUtil.getDateFromStringA(tmpAccessControlFaceDto.getEndTime()).getTime();
            timestrap = timestrap / 1000;
            addCard.put("expired", timestrap);
            addCards.add(addCard);
        }

        logger.debug("/v3_1/sync/cards/bid/：{}", paramOut.toString());
        logger.debug("card返回报文：{}", paramOut.toString());
        return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
    }

    private ResponseEntity<String> syncOwner(String unitId) {

        long curTimeTimeStamp = new Date().getTime();
        curTimeTimeStamp = curTimeTimeStamp / 1000;

        UnitDto unitDto = new UnitDto();
        unitDto.setUnitId(unitId);
        List<UnitDto> unitDtos = unitInnerServiceSMOImpl.queryUnits(unitDto);

        if (ListUtil.isNull(unitDtos)) {
            throw new IllegalArgumentException("单元错误");
        }

        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", curTimeTimeStamp);
        JSONObject data = new JSONObject();
        paramOut.put("data", data);


        RoomDto roomDto = new RoomDto();
        roomDto.setUnitId(unitId);
        List<RoomDto> roomDtos = roomV1InnerServiceSMOImpl.queryRooms(roomDto);
        if (ListUtil.isNull(roomDtos)) {
            return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
        }

        List<String> roomIds = new ArrayList<>();
        for (RoomDto tmpRoomDto : roomDtos) {
            roomIds.add(tmpRoomDto.getRoomId());
        }

        OwnerRoomRelDto ownerRoomRelDto = new OwnerRoomRelDto();
        ownerRoomRelDto.setRoomIds(roomIds.toArray(new String[roomIds.size()]));
        List<OwnerRoomRelDto> ownerRoomRelDtos = ownerRoomRelV1InnerServiceSMOImpl.queryOwnerRoomRels(ownerRoomRelDto);


        /**
         * "cards": [
         *                            {
         *     				"add": [
         *                        {
         *     						"card": "a1b2c3d4",
         *     						"expired": 1744646400,
         *     						"roomnum": "0204-1201" //绑定房间的
         *                        }
         *     				],
         *     				"del": [],
         *     				"b_id": 287,
         *     				"b_name": [
         *     					"2栋4单元"
         *     				]
         *                }
         *     		]
         */
        JSONArray cards = new JSONArray();
        JSONObject card = new JSONObject();
        card.put("b_id", unitId);
        card.put("b_name", unitDtos.get(0).getFloorNum() + "栋" + unitDtos.get(0).getUnitNum() + "单元");

        JSONArray addCards = new JSONArray();
        card.put("add", addCards);
        JSONArray del = new JSONArray();
        card.put("del", del);

        cards.add(card);
        data.put("cards", cards);

        JSONObject addCard = null;
        for (RoomDto tmpRoomDto : roomDtos) {
            if (ListUtil.isNull(ownerRoomRelDtos)) {
                continue;
            }
            for (OwnerRoomRelDto tmpOwnerRoomRelDto : ownerRoomRelDtos) {
                if (StringUtil.isEmpty(tmpOwnerRoomRelDto.getCardNumber())) {
                    continue;
                }
                if (tmpRoomDto.getRoomId().equals(tmpOwnerRoomRelDto.getRoomId())) {
                    addCard = new JSONObject();
                    addCard.put("card", tmpOwnerRoomRelDto.getCardNumber());
                    //addCard.put("roomnum", unitDtos.get(0).getFloorNum() + unitDtos.get(0).getUnitNum() + "-" + tmpRoomDto.getRoomNum());//添加楼栋单元号
					addCard.put("roomnum", String.format("%02d%02d-%s",
					    Integer.parseInt(unitDtos.get(0).getFloorNum()),
					    Integer.parseInt(unitDtos.get(0).getUnitNum()),
					   Integer.parseInt( tmpRoomDto.getRoomNum())));
					
                    long timestrap = tmpOwnerRoomRelDto.getEndTime().getTime();
                    timestrap = timestrap / 1000;
                    addCard.put("expired", timestrap);
                    addCards.add(addCard);
                }
            }
        }
        logger.debug("/v3_1/sync/cards/bid/：{}", paramOut.toString());
        logger.debug("card返回报文：{}", paramOut.toString());
        return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
    }
}
