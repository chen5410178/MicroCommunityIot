package com.java110.hal.http.yiteSmart.bmo.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.bean.dto.owner.OwnerRoomRelDto;
import com.java110.bean.dto.room.RoomDto;
import com.java110.bean.dto.unit.UnitDto;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.cache.CommonCache;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControlInout.AccessControlInoutDto;
import com.java110.hal.http.yiteSmart.bmo.IManagerViewDataBmo;
import com.java110.intf.accessControl.IAccessControlInoutV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.user.IOwnerInnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.intf.community.IRoomV1InnerServiceSMO;
import com.java110.intf.community.IUnitV1InnerServiceSMO;
import com.java110.intf.user.IOwnerRoomRelV1InnerServiceSMO;
import com.java110.accessControl.dao.IAccessControlInoutV1ServiceDao;
import com.java110.user.dao.IOwnerV1ServiceDao;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ManagerViewDataBmoImpl implements IManagerViewDataBmo {

    Logger logger = LoggerFactory.getLogger(ManagerViewDataBmoImpl.class);

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlInoutV1InnerServiceSMO accessControlInoutV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerInnerServiceSMO ownerInnerServiceSMOImpl;

    @Autowired
    private IRoomV1InnerServiceSMO roomV1InnerServiceSMOImpl;

    @Autowired
    private IUnitV1InnerServiceSMO unitV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerRoomRelV1InnerServiceSMO ownerRoomRelV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlInoutV1ServiceDao accessControlInoutV1ServiceDaoImpl;

    @Autowired
    private IOwnerV1ServiceDao ownerV1ServiceDaoImpl;

    @Override
    public ResponseEntity<String> viewData(String uuid) {

        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineCode(uuid);
        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);

        if (ListUtil.isNull(accessControlDtos)) {
            throw new IllegalArgumentException("管理机设备不存在");
        }

        String communityId = accessControlDtos.get(0).getCommunityId();

        String viewDataBaseKey = "viewDataBase_" + communityId;
        String baseCached = CommonCache.getValue(viewDataBaseKey);
        JSONObject paramOut;
        if (baseCached != null) {
            paramOut = JSONObject.parseObject(baseCached);
        } else {
            paramOut = buildBaseData(communityId);
            CommonCache.setValue(viewDataBaseKey, paramOut.toJSONString(), 30);
        }

        JSONObject data = paramOut.getJSONObject("data");
        data.put("captures", buildCaptures(communityId));
        data.put("strangers", buildStrangers(communityId));

        return new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
    }

    private JSONObject buildBaseData(String communityId) {
        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 200);
        paramOut.put("msg", "");
        paramOut.put("timestamp", DateUtil.getCurrentDate().getTime() / 1000);

        JSONObject data = new JSONObject();
        paramOut.put("data", data);

        JSONArray room = new JSONArray();

        RoomDto rentRoomDto = new RoomDto();
        rentRoomDto.setCommunityId(communityId);
        rentRoomDto.setState(RoomDto.STATE_SHOP_FIRE);
        int rentRoomCount = roomV1InnerServiceSMOImpl.queryRoomsCount(rentRoomDto);

        RoomDto selfRoomDto = new RoomDto();
        selfRoomDto.setCommunityId(communityId);
        selfRoomDto.setState(RoomDto.STATE_SELL);
        int selfRoomCount = roomV1InnerServiceSMOImpl.queryRoomsCount(selfRoomDto);

        RoomDto freeRoomDto = new RoomDto();
        freeRoomDto.setCommunityId(communityId);
        freeRoomDto.setState(RoomDto.STATE_FREE);
        int freeRoomCount = roomV1InnerServiceSMOImpl.queryRoomsCount(freeRoomDto);

        JSONObject roomRent = new JSONObject();
        roomRent.put("name", "租户");
        roomRent.put("num", rentRoomCount);
        room.add(roomRent);
        JSONObject roomSelf = new JSONObject();
        roomSelf.put("name", "自住");
        roomSelf.put("num", selfRoomCount);
        room.add(roomSelf);
        JSONObject roomFree = new JSONObject();
        roomFree.put("name", "闲置");
        roomFree.put("num", freeRoomCount);
        room.add(roomFree);
        data.put("room", room);

        JSONArray user = new JSONArray();

        OwnerDto tenantDto = new OwnerDto();
        tenantDto.setCommunityId(communityId);
        tenantDto.setOwnerTypeCd(OwnerDto.OWNER_TYPE_CD_RENTING);
        int tenantCount = ownerV1InnerServiceSMOImpl.queryOwnersCount(tenantDto);

        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setCommunityId(communityId);
        ownerDto.setOwnerTypeCd(OwnerDto.OWNER_TYPE_CD_OWNER);
        int ownerCount = ownerV1InnerServiceSMOImpl.queryOwnersCount(ownerDto);

        OwnerDto memberDto = new OwnerDto();
        memberDto.setCommunityId(communityId);
        memberDto.setOwnerTypeCd(OwnerDto.OWNER_TYPE_CD_MEMBER);
        int memberCount = ownerV1InnerServiceSMOImpl.queryOwnersCount(memberDto);

        JSONObject userRent = new JSONObject();
        userRent.put("name", "租户");
        userRent.put("num", tenantCount);
        user.add(userRent);
        JSONObject userOwner = new JSONObject();
        userOwner.put("name", "业主");
        userOwner.put("num", ownerCount);
        user.add(userOwner);
        JSONObject userMember = new JSONObject();
        userMember.put("name", "业主成员");
        userMember.put("num", memberCount);
        user.add(userMember);
        data.put("user", user);

        JSONArray gate = new JSONArray();

        AccessControlDto allDto = new AccessControlDto();
        allDto.setCommunityId(communityId);
        List<AccessControlDto> allMachines = accessControlV1InnerServiceSMOImpl.queryAccessControls(allDto);

        int onlineCount = 0;
        int offlineCount = 0;
        if (!ListUtil.isNull(allMachines)) {
            for (AccessControlDto machine : allMachines) {
                if (isOnline(machine.getHeartbeatTime())) {
                    onlineCount++;
                } else {
                    offlineCount++;
                }
            }
        }

        JSONObject gateOnline = new JSONObject();
        gateOnline.put("name", "在线");
        gateOnline.put("num", onlineCount);
        gate.add(gateOnline);
        JSONObject gateOffline = new JSONObject();
        gateOffline.put("name", "离线");
        gateOffline.put("num", offlineCount);
        gate.add(gateOffline);
        data.put("gate", gate);

        JSONObject userLine = buildUserLine(communityId);
        data.put("user_line", userLine);

        JSONObject openDoorLine = buildOpenDoorLine(communityId);
        data.put("open_door_line", openDoorLine);

        data.put("captures", new JSONArray());
        data.put("strangers", new JSONArray());
        data.put("notices", new JSONArray());

        return paramOut;
    }

    private JSONArray buildCaptures(String communityId) {
        JSONArray captures = new JSONArray();
        AccessControlInoutDto inoutDto = new AccessControlInoutDto();
        inoutDto.setCommunityId(communityId);
        inoutDto.setPage(1);
        inoutDto.setRow(20);
        List<AccessControlInoutDto> inoutDtos = accessControlInoutV1InnerServiceSMOImpl.queryAccessControlInouts(inoutDto);

        if (ListUtil.isNull(inoutDtos)) {
            return captures;
        }

        Map<String, OwnerDto> telOwnerMap = new HashMap<>();
        List<String> tels = new ArrayList<>();
        for (AccessControlInoutDto inout : inoutDtos) {
            String tel = inout.getTel();
            if (!StringUtil.isEmpty(tel) && !telOwnerMap.containsKey(tel)) {
                tels.add(tel);
                telOwnerMap.put(tel, null);
            }
        }
        for (String tel : tels) {
            OwnerDto queryOwner = new OwnerDto();
            queryOwner.setLink(tel);
            queryOwner.setCommunityId(communityId);
            List<OwnerDto> owners = ownerInnerServiceSMOImpl.queryOwners(queryOwner);
            if (!ListUtil.isNull(owners)) {
                telOwnerMap.put(tel, owners.get(0));
            }
        }

        Set<String> ownerIds = new HashSet<>();
        for (OwnerDto o : telOwnerMap.values()) {
            if (o != null) {
                ownerIds.add(o.getOwnerId());
            }
        }
        Map<String, List<OwnerRoomRelDto>> ownerRelMap = new HashMap<>();
        if (!ownerIds.isEmpty()) {
            for (String ownerId : ownerIds) {
                OwnerRoomRelDto relDto = new OwnerRoomRelDto();
                relDto.setOwnerId(ownerId);
                List<OwnerRoomRelDto> rels = ownerRoomRelV1InnerServiceSMOImpl.queryOwnerRoomRels(relDto);
                if (!ListUtil.isNull(rels)) {
                    ownerRelMap.put(ownerId, rels);
                }
            }
        }

        Set<String> roomIds = new HashSet<>();
        for (List<OwnerRoomRelDto> rels : ownerRelMap.values()) {
            for (OwnerRoomRelDto rel : rels) {
                if (!StringUtil.isEmpty(rel.getRoomId())) {
                    roomIds.add(rel.getRoomId());
                }
            }
        }
        Map<String, RoomDto> roomMap = new HashMap<>();
        if (!roomIds.isEmpty()) {
            for (String roomId : roomIds) {
                RoomDto queryRoom = new RoomDto();
                queryRoom.setRoomId(roomId);
                List<RoomDto> rooms = roomV1InnerServiceSMOImpl.queryRooms(queryRoom);
                if (!ListUtil.isNull(rooms)) {
                    roomMap.put(roomId, rooms.get(0));
                }
            }
        }

        Set<String> unitIds = new HashSet<>();
        for (RoomDto r : roomMap.values()) {
            if (!StringUtil.isEmpty(r.getUnitId())) {
                unitIds.add(r.getUnitId());
            }
        }
        Map<String, UnitDto> unitMap = new HashMap<>();
        if (!unitIds.isEmpty()) {
            for (String unitId : unitIds) {
                UnitDto queryUnit = new UnitDto();
                queryUnit.setUnitId(unitId);
                List<UnitDto> units = unitV1InnerServiceSMOImpl.queryUnits(queryUnit);
                if (!ListUtil.isNull(units)) {
                    unitMap.put(unitId, units.get(0));
                }
            }
        }

        for (AccessControlInoutDto inout : inoutDtos) {
            JSONObject capture = new JSONObject();
            capture.put("open_type", getOpenTypeName(inout.getOpenTypeCd()));
            capture.put("image", inout.getFacePath() != null ? inout.getFacePath() : "");
            capture.put("time", String.valueOf(inout.getCreateTime() != null ? inout.getCreateTime().getTime() / 1000 : 0));
            capture.put("device_name",
                    (inout.getMachineName() == null ? "" : inout.getMachineName())
                            + " " + getSimilarName(inout.getSimilar()));

            String userType = "其他";
            String build = "";
            String roomNum = "";
            String tel = inout.getTel();
            if (!StringUtil.isEmpty(tel)) {
                OwnerDto owner = telOwnerMap.get(tel);
                if (owner != null) {
                    String typeCd = owner.getOwnerTypeCd();
                    if (OwnerDto.OWNER_TYPE_CD_OWNER.equals(typeCd)) {
                        userType = "业主";
                    } else if (OwnerDto.OWNER_TYPE_CD_MEMBER.equals(typeCd)) {
                        userType = "成员";
                    } else if (OwnerDto.OWNER_TYPE_CD_RENTING.equals(typeCd)) {
                        userType = "租户";
                    }

                    List<OwnerRoomRelDto> rels = ownerRelMap.get(owner.getOwnerId());
                    if (rels != null && !rels.isEmpty()) {
                        String roomId = rels.get(0).getRoomId();
                        RoomDto r = roomMap.get(roomId);
                        if (r != null) {
                            roomNum = r.getRoomNum() != null ? r.getRoomNum() : "";
                            String unitId = r.getUnitId();
                            if (!StringUtil.isEmpty(unitId)) {
                                UnitDto unit = unitMap.get(unitId);
                                if (unit != null) {
                                    build = r.getFloorNum() != null ? r.getFloorNum() : "";
                                    build += "栋" + unit.getUnitNum() + "单元";
                                }
                            }
                        }
                    }
                }
            }
            if ("F".equals(inout.getState())) {
                capture.put("user_type", "陌生人" + inout.getTel());
                capture.put("phone", "");
            } else {
                capture.put("user_type", userType);
                capture.put("phone", inout.getTel());
            }

            capture.put("build", build);
            capture.put("room", roomNum);
            captures.add(capture);
        }
        return captures;
    }

    private JSONArray buildStrangers(String communityId) {
        JSONArray strangers = new JSONArray();
        AccessControlInoutDto strangerDto = new AccessControlInoutDto();
        strangerDto.setCommunityId(communityId);
        strangerDto.setState("F");
        strangerDto.setPage(1);
        strangerDto.setRow(20);
        List<AccessControlInoutDto> strangerDtos = accessControlInoutV1InnerServiceSMOImpl.queryAccessControlInouts(strangerDto);

        if (!ListUtil.isNull(strangerDtos)) {
            for (AccessControlInoutDto stranger : strangerDtos) {
                JSONObject strangerObj = new JSONObject();
                strangerObj.put("image", stranger.getFacePath() != null ? stranger.getFacePath() : "");
                strangerObj.put("time", String.valueOf(stranger.getCreateTime() != null ? stranger.getCreateTime().getTime() / 1000 : 0));
                strangerObj.put("address",
                        (stranger.getMachineName() == null ? "" : stranger.getMachineName())
                                + " " + getSimilarName(stranger.getSimilar()));
                strangers.add(strangerObj);
            }
        }
        return strangers;
    }

    private String getOpenTypeName(String openTypeCd) {
        if ("2000".equals(openTypeCd)) return "刷卡开门";
        if ("4000".equals(openTypeCd)) return "密码开门";
        if ("3000".equals(openTypeCd)) return "扫码开门";
        if ("5000".equals(openTypeCd)) return "远程开门";
        if ("6000".equals(openTypeCd)) return "访客密码开门";
        if ("7000".equals(openTypeCd)) return "临时密码开门";
        return "人脸开门";
    }

    private String getSimilarName(String similarCode) {
        if ("0".equals(similarCode)) return "门禁机未上报抓拍原因";
        if ("1".equals(similarCode)) return "无网络";
        if ("2".equals(similarCode)) return "在线识别返回数据为空";
        if ("3".equals(similarCode)) return "在线识别找不到此人，判断为陌生人";
        if ("4".equals(similarCode)) return "在线识别接口失败";
        if ("5".equals(similarCode)) return "活体检测未通过";
        if ("6".equals(similarCode)) return "对比识别线程未初始化";
        if ("7".equals(similarCode)) return "离线识别找不到此人，判断为陌生人";
        if ("8".equals(similarCode)) return "在线识别返回的人，本地识别已经排除了";
        if ("9".equals(similarCode)) return "体温异常";
        if ("10".equals(similarCode)) return "过期卡";
        if ("11".equals(similarCode)) return "未授权卡";
        if ("12".equals(similarCode)) return "无效异常卡";
        if ("13".equals(similarCode)) return "业主密码错误";
        if ("14".equals(similarCode)) return "访客密码错误";
        if ("15".equals(similarCode)) return "密码错误";
        return "";
    }

    private boolean isOnline(String heartbeatTime) {
        if (StringUtil.isEmpty(heartbeatTime)) {
            return false;
        }
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(DateUtil.getDateFromString(heartbeatTime, DateUtil.DATE_FORMATE_STRING_A));
            calendar.add(Calendar.MINUTE, 2);
            return calendar.getTime().getTime() > DateUtil.getCurrentDate().getTime();
        } catch (ParseException e) {
            return false;
        }
    }

    private JSONObject buildUserLine(String communityId) {
        String cacheKey = "userLine_" + communityId;
        String cached = CommonCache.getValue(cacheKey);
        if (cached != null) {
            return JSONObject.parseObject(cached);
        }

        JSONObject userLine = new JSONObject();

        Map<String, Object> param = new HashMap<>();
        param.put("communityId", communityId);
        List<Map> statsList = ownerV1ServiceDaoImpl.queryUserLineStats(param);

        Calendar cal = Calendar.getInstance();
        JSONArray monthArr = new JSONArray();
        JSONArray memberArr = new JSONArray();
        JSONArray ownerArr = new JSONArray();
        JSONArray tenantArr = new JSONArray();

        SimpleDateFormat monthFmt = new SimpleDateFormat("M");

        for (int i = 12; i >= 0; i--) {
            cal.setTime(new Date());
            cal.add(Calendar.MONTH, -i);
            String monthKey = monthFmt.format(cal.getTime());
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);

            int memberCount = 0;
            int ownerCount = 0;
            int tenantCount = 0;

            if (!ListUtil.isNull(statsList)) {
                for (Map stat : statsList) {
                    String monthStr = (String) stat.get("month_str");
                    if (monthStr == null) continue;
                    String[] parts = monthStr.split("-");
                    if (parts.length != 2) continue;
                    int statYear = Integer.parseInt(parts[0]);
                    int statMonth = Integer.parseInt(parts[1]) - 1;
                    if (statYear == year && statMonth == month) {
                        String typeCd = (String) stat.get("owner_type_cd");
                        int cnt = ((Number) stat.get("cnt")).intValue();
                        if (OwnerDto.OWNER_TYPE_CD_OWNER.equals(typeCd)) {
                            ownerCount += cnt;
                        } else if (OwnerDto.OWNER_TYPE_CD_MEMBER.equals(typeCd)) {
                            memberCount += cnt;
                        } else if (OwnerDto.OWNER_TYPE_CD_RENTING.equals(typeCd)) {
                            tenantCount += cnt;
                        }
                    }
                }
            }

            monthArr.add(monthKey);
            memberArr.add(memberCount);
            ownerArr.add(ownerCount);
            tenantArr.add(tenantCount);
        }

        userLine.put("month", monthArr);
        userLine.put("member", memberArr);
        userLine.put("owner", ownerArr);
        userLine.put("tenant", tenantArr);
        CommonCache.setValue(cacheKey, userLine.toJSONString(), 60);
        return userLine;
    }

    private JSONObject buildOpenDoorLine(String communityId) {
        String cacheKey = "openDoorLine_" + communityId;
        String cached = CommonCache.getValue(cacheKey);
        if (cached != null) {
            return JSONObject.parseObject(cached);
        }

        JSONObject openDoorLine = new JSONObject();

        Calendar cal = Calendar.getInstance();
        String endDate = DateUtil.getFormatTimeStringA(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, -90);
        String startDate = DateUtil.getFormatTimeStringA(cal.getTime());

        Map<String, Object> param = new HashMap<>();
        param.put("communityId", communityId);
        param.put("queryStartTime", startDate);
        param.put("queryEndTime", endDate);
        List<Map> statsList = accessControlInoutV1ServiceDaoImpl.queryOpenDoorLineStats(param);

        Map<String, Map<String, Integer>> dateTypeMap = new LinkedHashMap<>();
        Calendar dayCal = Calendar.getInstance();
        for (int d = 89; d >= 0; d--) {
            dayCal.setTime(new Date());
            dayCal.add(Calendar.DAY_OF_MONTH, -d);
            String dateStr = DateUtil.getFormatTimeStringB(dayCal.getTime());
            Map<String, Integer> typeMap = new HashMap<>();
            typeMap.put("all", 0);
            typeMap.put("face", 0);
            typeMap.put("app", 0);
            typeMap.put("user_pwd", 0);
            typeMap.put("visitor_pwd", 0);
            typeMap.put("card", 0);
            dateTypeMap.put(dateStr, typeMap);
        }

        if (!ListUtil.isNull(statsList)) {
            for (Map stat : statsList) {
                String dateStr = String.valueOf(stat.get("date_str"));
                String openTypeCd = String.valueOf(stat.get("open_type_cd"));
                int cnt = ((Number) stat.get("cnt")).intValue();
                Map<String, Integer> typeMap = dateTypeMap.get(dateStr);
                if (typeMap == null) continue;
                typeMap.put("all", typeMap.get("all") + cnt);
                String type = mapOpenType(openTypeCd);
                typeMap.put(type, typeMap.get(type) + cnt);
            }
        }

        int totalDays = 90;
        List<Map<String, Integer>> allDayData = new ArrayList<>(dateTypeMap.values());
        int[] dayCounts = {15, 30, 90};
        String[] keys = {"fifteen", "thirty", "ninety"};

        for (int i = 0; i < dayCounts.length; i++) {
            int days = dayCounts[i];
            String key = keys[i];
            int startIdx = totalDays - days;

            JSONObject section = new JSONObject();
            JSONArray allArr = new JSONArray();
            JSONArray faceArr = new JSONArray();
            JSONArray appArr = new JSONArray();
            JSONArray userPwdArr = new JSONArray();
            JSONArray visitorPwdArr = new JSONArray();
            JSONArray cardArr = new JSONArray();

            for (int idx = startIdx; idx < totalDays; idx++) {
                Map<String, Integer> typeMap = allDayData.get(idx);
                allArr.add(typeMap.get("all"));
                faceArr.add(typeMap.get("face"));
                appArr.add(typeMap.get("app"));
                userPwdArr.add(typeMap.get("user_pwd"));
                visitorPwdArr.add(typeMap.get("visitor_pwd"));
                cardArr.add(typeMap.get("card"));
            }

            section.put("all", allArr);
            section.put("face", faceArr);
            section.put("app", appArr);
            section.put("user_pwd", userPwdArr);
            section.put("visitor_pwd", visitorPwdArr);
            section.put("card", cardArr);
            openDoorLine.put(key, section);
        }

        CommonCache.setValue(cacheKey, openDoorLine.toJSONString(), 60);
        return openDoorLine;
    }

    private String mapOpenType(String openTypeCd) {
        if (openTypeCd == null) return "all";
        switch (openTypeCd) {
            case "1000": return "face";
            case "2000": return "card";
            case "4000": return "user_pwd";
            case "5000": return "app";
            case "3000": return "card";
            case "6000": return "visitor_pwd";
            case "7000": return "user_pwd";
            default: return "all";
        }
    }
}
