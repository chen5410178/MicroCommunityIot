package com.java110.accessControl.manufactor.adapt.accessControl.yiteSmart;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.owner.OwnerRoomRelDto;
import com.java110.bean.dto.room.RoomDto;
import com.java110.bean.dto.unit.UnitDto;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.accessControlFloor.AccessControlFloorDto;
import com.java110.intf.accessControl.IAccessControlFloorV1InnerServiceSMO;
import com.java110.po.accessControlFace.AccessControlFacePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SyncUserCardImpl implements ISyncUserCard {

    @Autowired
    private IAccessControlFloorV1InnerServiceSMO accessControlFloorV1InnerServiceSMOImpl;

    @Override
    public JSONObject getUserCard(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {

        if (AccessControlFaceDto.PERSON_TYPE_STAFF.equals(accessControlFacePo.getPersonType())) {
            return syncStaff(accessControlDto, accessControlFacePo);
        } else {
            return syncOwner(accessControlDto, accessControlFacePo);
        }
    }


    private JSONObject syncStaff(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {

        long curTimeTimeStamp = new Date().getTime();
        curTimeTimeStamp = curTimeTimeStamp / 1000;


        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", curTimeTimeStamp);
        JSONObject data = new JSONObject();
        paramOut.put("data", data);


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
        card.put("b_id", "-1");
        card.put("b_name", "物业中心");

        JSONArray addCards = new JSONArray();
        card.put("add", addCards);
        JSONArray del = new JSONArray();
        card.put("del", del);

        cards.add(card);
        data.put("cards", cards);

        JSONObject addCard = null;

        addCard = new JSONObject();
        addCard.put("card", accessControlFacePo.getCardNumber());
        addCard.put("roomnum", "9999");
        long timestrap = DateUtil.getDateFromStringA(accessControlFacePo.getEndTime()).getTime();
        timestrap = timestrap / 1000;
        addCard.put("expired", timestrap);
        addCards.add(addCard);

        return paramOut;
    }

    private JSONObject syncOwner(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {

        long curTimeTimeStamp = new Date().getTime();
        curTimeTimeStamp = curTimeTimeStamp / 1000;

        AccessControlFloorDto accessControlFloorDto = new AccessControlFloorDto();
        accessControlFloorDto.setMachineId(accessControlDto.getMachineId());
        List<AccessControlFloorDto> accessControlFloorDtos = accessControlFloorV1InnerServiceSMOImpl.queryAccessControlFloors(accessControlFloorDto);


        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", curTimeTimeStamp);
        JSONObject data = new JSONObject();
        paramOut.put("data", data);

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
        card.put("b_id", accessControlFloorDtos.get(0).getUnitId());
        card.put("b_name", accessControlFloorDtos.get(0).getFloorNum() + "栋" + accessControlFloorDtos.get(0).getUnitNum() + "单元");

        JSONArray addCards = new JSONArray();
        card.put("add", addCards);
        JSONArray del = new JSONArray();
        card.put("del", del);

        cards.add(card);
        data.put("cards", cards);

        JSONObject addCard = null;


        addCard = new JSONObject();
        addCard.put("card", accessControlFacePo.getCardNumber());
        String roomName = accessControlFacePo.getRoomName();
        if (!StringUtil.isEmpty(roomName) && roomName.split("-", 3).length == 3) {
            addCard.put("roomnum", roomName.split("-", 3)[2]);
        }
        long timestrap = DateUtil.getDateFromStringA(accessControlFacePo.getEndTime()).getTime();
        timestrap = timestrap / 1000;
        addCard.put("expired", timestrap);
        addCards.add(addCard);


        return paramOut;
    }
}
