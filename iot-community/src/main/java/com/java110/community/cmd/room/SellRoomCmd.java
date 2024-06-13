package com.java110.community.cmd.room;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.java110.bean.dto.file.FileRelDto;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.bean.dto.owner.OwnerRoomRelDto;
import com.java110.bean.dto.room.RoomDto;
import com.java110.bean.po.owner.OwnerRoomRelPo;
import com.java110.bean.po.room.RoomPo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cache.MappingCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.MappingConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.*;
import com.java110.doc.annotation.*;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.accessControlFloor.AccessControlFloorDto;
import com.java110.intf.accessControl.IAccessControlFaceV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlFloorV1InnerServiceSMO;
import com.java110.intf.community.ICommunityInnerServiceSMO;
import com.java110.intf.community.IRoomV1InnerServiceSMO;
import com.java110.intf.community.IUnitInnerServiceSMO;
import com.java110.intf.system.IFileRelInnerServiceSMO;
import com.java110.intf.user.IOwnerRoomRelV1InnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.po.accessControlFace.AccessControlFacePo;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Java110CmdDoc(title = "业主房屋关系绑定",
        description = "对应后台 业主入驻房屋功能",
        httpMethod = "post",
        url = "http://{ip}:{port}/iot/api/room.sellRoom",
        resource = "communityDoc",
        author = "吴学文",
        serviceCode = "room.sellRoom"
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "communityId", length = 30, remark = "小区ID"),
        @Java110ParamDoc(name = "roomId", length = 30, remark = "房屋ID"),
        @Java110ParamDoc(name = "state", length = 12, remark = "状态 2001\t已入住\t\n" +
                "2003\t已交房\t\n" +
                "2005\t已装修\t\n" +
                "2004\t未入住\t\n" +
                "2008\t空闲\n" +
                "2009\t装修中\t"),
        @Java110ParamDoc(name = "ownerId", length = 30, remark = "业主ID"),
})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
        }
)

@Java110ExampleDoc(
        reqBody = "{\n" +
                "\t\"ownerId\": 121231,\n" +
                "\t\"state\": \"2001\",\n" +
                "\t\"roomId\": \"123123\",\n" +
                "\t\"communityId\": \"2022121921870161\"\n" +
                "}",
        resBody = "{\"code\":0,\"msg\":\"成功\"}"
)
@Java110Cmd(serviceCode = "room.sellRoom")
public class SellRoomCmd extends Cmd {

    @Autowired
    private IUnitInnerServiceSMO unitInnerServiceSMOImpl;

    @Autowired
    private ICommunityInnerServiceSMO communityInnerServiceSMOImpl;

    @Autowired
    private IOwnerRoomRelV1InnerServiceSMO ownerRoomRelV1InnerServiceSMOImpl;

    @Autowired
    private IRoomV1InnerServiceSMO roomV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlFloorV1InnerServiceSMO accessControlFloorV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlFaceV1InnerServiceSMO accessControlFaceV1InnerServiceSMOImpl;

    @Autowired
    private IFileRelInnerServiceSMO fileRelInnerServiceSMOImpl;


    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.jsonObjectHaveKey(reqJson, "communityId", "请求报文中未包含communityId节点");
        Assert.jsonObjectHaveKey(reqJson, "ownerId", "请求报文中未包含ownerId节点");
        Assert.jsonObjectHaveKey(reqJson, "roomId", "请求报文中未包含roomId节点");
        Assert.jsonObjectHaveKey(reqJson, "state", "请求报文中未包含state节点");
        //  Assert.jsonObjectHaveKey(reqJson, "storeId", "请求报文中未包含storeId节点");

        Assert.hasLength(reqJson.getString("communityId"), "小区ID不能为空");
        Assert.hasLength(reqJson.getString("ownerId"), "ownerId不能为空");
        Assert.hasLength(reqJson.getString("roomId"), "roomId不能为空");
        Assert.hasLength(reqJson.getString("state"), "state不能为空");

        // todo 校验 房屋和人员之间是否存在关系
        OwnerRoomRelDto ownerRoomRelDto = new OwnerRoomRelDto();
        ownerRoomRelDto.setRoomId(reqJson.getString("roomId"));
        ownerRoomRelDto.setOwnerId(reqJson.getString("ownerId"));
        int count = ownerRoomRelV1InnerServiceSMOImpl.queryOwnerRoomRelsCount(ownerRoomRelDto);
        if (count > 0) {
            throw new CmdException("房屋和人员已经绑定，请勿重复绑定");
        }
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        if (!reqJson.containsKey("startTime")) {
            reqJson.put("startTime", DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        }
        if (!reqJson.containsKey("endTime")) {
            reqJson.put("endTime", "2050-01-01 00:00:00");
        }

        String startTime = reqJson.getString("startTime");
        String endTime = reqJson.getString("endTime");

        if(!startTime.contains(":")){
            startTime += " 00:00:00";
            reqJson.put("startTime", startTime);
        }
        if(!endTime.contains(":")){
            endTime += " 23:59:59";
            reqJson.put("endTime", endTime);
        }

        //todo 绑定人员 房屋关系
        sellRoom(reqJson);

        //todo 更新房屋信息为售出
        updateShellRoom(reqJson);

        //todo 人员同步门禁
        synchronousAccessControl(reqJson);
    }


    /**
     * 售卖房屋信息
     *
     * @param paramInJson 接口调用放传入入参
     * @return 订单服务能够接受的报文
     */
    public void sellRoom(JSONObject paramInJson) {

        JSONObject businessUnit = new JSONObject();
        businessUnit.putAll(paramInJson);
        businessUnit.put("relId", GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_relId));
        businessUnit.put("userId", "-1");
        OwnerRoomRelPo ownerRoomRelPo = BeanConvertUtil.covertBean(businessUnit, OwnerRoomRelPo.class);
        int flag = ownerRoomRelV1InnerServiceSMOImpl.saveOwnerRoomRel(ownerRoomRelPo);

        if (flag < 1) {
            throw new CmdException("添加业主房屋关系");
        }

    }

    /**
     * 修改小区楼信息
     *
     * @param paramInJson 接口调用放传入入参
     * @return 订单服务能够接受的报文
     */
    public void updateShellRoom(JSONObject paramInJson) {
        JSONObject businessUnit = new JSONObject();
        businessUnit.putAll(paramInJson);
        businessUnit.put("userId", "-1");
        RoomPo roomPo = BeanConvertUtil.covertBean(businessUnit, RoomPo.class);

        int flag = roomV1InnerServiceSMOImpl.updateRoom(roomPo);
        if (flag < 1) {
            throw new CmdException("添加业主房屋关系");
        }
    }

    /**
     * 人员同步门禁
     *
     * @param reqJson
     */
    private void synchronousAccessControl(JSONObject reqJson) {
        String roomId = reqJson.getString("roomId");

        RoomDto roomDto = new RoomDto();
        roomDto.setRoomId(roomId);
        roomDto.setCommunityId(reqJson.getString("communityId"));
        List<RoomDto> roomDtos = roomV1InnerServiceSMOImpl.queryRooms(roomDto);
        if (ListUtil.isNull(roomDtos)) {
            return;
        }

        String unitId = roomDtos.get(0).getUnitId();
        if (StringUtil.isEmpty(unitId)) {
            return;
        }


        AccessControlFloorDto accessControlFloorDto = new AccessControlFloorDto();
        accessControlFloorDto.setUnitId(unitId);
        accessControlFloorDto.setCommunityId(reqJson.getString("communityId"));
        List<AccessControlFloorDto> accessControlFloorDtos = accessControlFloorV1InnerServiceSMOImpl.queryAccessControlFloors(accessControlFloorDto);
        if (ListUtil.isNull(accessControlFloorDtos)) {
            return;
        }

        //todo 查询人员信息
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setMemberId(reqJson.getString("ownerId"));
        List<OwnerDto> ownerDtos = ownerV1InnerServiceSMOImpl.queryOwners(ownerDto);
        if (ListUtil.isNull(ownerDtos)) {
            return;
        }
        ownerDto = ownerDtos.get(0);

        //todo 查询业主照片
        String imgUrl = MappingCache.getValue(MappingConstant.FILE_DOMAIN, "IMG_PATH");
        FileRelDto fileRelDto = new FileRelDto();
        fileRelDto.setObjId(ownerDto.getMemberId());
        fileRelDto.setRelTypeCd("10000"); //人员照片
        List<FileRelDto> fileRelDtos = fileRelInnerServiceSMOImpl.queryFileRels(fileRelDto);
        if (!ListUtil.isNull(fileRelDtos)) {
            List<String> urls = new ArrayList<>();
            for (FileRelDto fileRel : fileRelDtos) {
                urls.add(imgUrl + fileRel.getFileRealName());
                ownerDto.setUrl(imgUrl + fileRel.getFileRealName());
            }
            ownerDto.setUrls(urls);
        }

        AccessControlFacePo accessControlFacePo = null;
        for (AccessControlFloorDto tmpAccessControlFloorDto : accessControlFloorDtos) {
            accessControlFacePo = new AccessControlFacePo();
            accessControlFacePo.setFacePath(ownerDto.getUrl());
            accessControlFacePo.setCommunityId(tmpAccessControlFloorDto.getCommunityId());
            accessControlFacePo.setIdNumber(ownerDto.getIdCard());
            accessControlFacePo.setComeId(tmpAccessControlFloorDto.getAcfId());
            accessControlFacePo.setComeType(AccessControlFaceDto.COME_TYPE_FLOOR);
            accessControlFacePo.setEndTime(reqJson.getString("endTime"));
            accessControlFacePo.setStartTime(reqJson.getString("endTime"));
            accessControlFacePo.setCardNumber("");
            accessControlFacePo.setMachineId(tmpAccessControlFloorDto.getMachineId());
            accessControlFacePo.setMessage("待下发到门禁");
            accessControlFacePo.setMfId(GenerateCodeFactory.getGeneratorId("11"));
            accessControlFacePo.setName(ownerDto.getName());
            accessControlFacePo.setPersonId(ownerDto.getMemberId());
            accessControlFacePo.setPersonType(AccessControlFaceDto.PERSON_TYPE_OWNER);
            accessControlFacePo.setState(AccessControlFaceDto.STATE_WAIT);
            accessControlFacePo.setRoomName(roomDtos.get(0).getFloorNum() + "-" + roomDtos.get(0).getUnitNum() + "-" + roomDtos.get(0).getRoomNum());
            accessControlFaceV1InnerServiceSMOImpl.saveAccessControlFace(accessControlFacePo);
        }


    }

}
