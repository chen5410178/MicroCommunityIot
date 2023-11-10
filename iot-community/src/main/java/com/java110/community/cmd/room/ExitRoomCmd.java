package com.java110.community.cmd.room;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.owner.OwnerRoomRelDto;
import com.java110.bean.dto.room.RoomDto;
import com.java110.bean.po.owner.OwnerRoomRelPo;
import com.java110.bean.po.room.RoomPo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.ResponseConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.doc.annotation.*;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.accessControlFloor.AccessControlFloorDto;
import com.java110.intf.accessControl.IAccessControlFaceV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlFloorV1InnerServiceSMO;
import com.java110.intf.community.ICommunityInnerServiceSMO;
import com.java110.intf.community.IRoomInnerServiceSMO;
import com.java110.intf.community.IRoomV1InnerServiceSMO;
import com.java110.intf.user.IOwnerRoomRelInnerServiceSMO;
import com.java110.intf.user.IOwnerRoomRelV1InnerServiceSMO;
import com.java110.po.accessControlFace.AccessControlFacePo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Java110CmdDoc(title = "业主房屋关系解绑",
        description = "对应后台 业主退房房屋功能",
        httpMethod = "post",
        url = "http://{ip}:{port}/iot/api/room.exitRoom",
        resource = "communityDoc",
        author = "吴学文",
        serviceCode = "room.exitRoom"
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "communityId", length = 30, remark = "小区ID"),
        @Java110ParamDoc(name = "roomId", length = 30, remark = "房屋ID"),
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
                "\t\"roomId\": \"123123\",\n" +
                "\t\"communityId\": \"2022121921870161\"\n" +
                "}",
        resBody = "{\"code\":0,\"msg\":\"成功\"}"
)
@Java110Cmd(serviceCode = "room.exitRoom")
public class ExitRoomCmd extends Cmd {


    @Autowired
    private IOwnerRoomRelInnerServiceSMO ownerRoomRelInnerServiceSMOImpl;

    @Autowired
    private IRoomInnerServiceSMO roomInnerServiceSMOImpl;


    @Autowired
    private ICommunityInnerServiceSMO communityInnerServiceSMOImpl;

    @Autowired
    private IOwnerRoomRelV1InnerServiceSMO ownerRoomRelV1InnerServiceSMOImpl;

    @Autowired
    private IRoomV1InnerServiceSMO roomV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlFloorV1InnerServiceSMO accessControlFloorV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlFaceV1InnerServiceSMO accessControlFaceV1InnerServiceSMOImpl;



    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.jsonObjectHaveKey(reqJson, "communityId", "请求报文中未包含communityId节点");
        Assert.jsonObjectHaveKey(reqJson, "ownerId", "请求报文中未包含ownerId节点");
        Assert.jsonObjectHaveKey(reqJson, "roomId", "请求报文中未包含roomId节点");
        //Assert.jsonObjectHaveKey(reqJson, "storeId", "请求报文中未包含storeId节点");


        Assert.hasLength(reqJson.getString("communityId"), "小区ID不能为空");
        Assert.hasLength(reqJson.getString("ownerId"), "ownerId不能为空");
        Assert.hasLength(reqJson.getString("roomId"), "roomId不能为空");
        //Assert.hasLength(reqJson.getString("storeId"), "storeId不能为空");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        //根据ownerId 和 roomId 查询relId 删除
        OwnerRoomRelDto ownerRoomRelDto = BeanConvertUtil.covertBean(reqJson, OwnerRoomRelDto.class);
        List<OwnerRoomRelDto> ownerRoomRelDtos = ownerRoomRelInnerServiceSMOImpl.queryOwnerRoomRels(ownerRoomRelDto);

        if (ownerRoomRelDtos == null || ownerRoomRelDtos.size() < 1) {
            throw new CmdException(ResponseConstant.RESULT_CODE_ERROR, "数据存在问题，业主和房屋对应关系不是一条");
        }

        if (StringUtil.isEmpty(ownerRoomRelDtos.get(0).getRelId())) {
            throw new CmdException("未包含关系");
        }


        JSONObject businessUnit = new JSONObject();
        //businessUnit.putAll(paramInJson);
        businessUnit.put("relId", ownerRoomRelDtos.get(0).getRelId());
        //businessUnit.put("userId", dataFlowContext.getRequestCurrentHeaders().get(CommonConstant.HTTP_USER_ID));
        OwnerRoomRelPo roomPo = BeanConvertUtil.covertBean(businessUnit, OwnerRoomRelPo.class);

        int flag = ownerRoomRelV1InnerServiceSMOImpl.deleteOwnerRoomRel(roomPo);

        if (flag < 1) {
            throw new IllegalArgumentException("删除业主房屋关系失败");
        }

        //todo 查询房屋是否存在人员，如果存在返回 如果不存在 则修改为空闲
        OwnerRoomRelDto tmpOwnerRoomRelDto = new OwnerRoomRelDto();
        tmpOwnerRoomRelDto.setRoomId(ownerRoomRelDtos.get(0).getRoomId());
        long count = ownerRoomRelInnerServiceSMOImpl.queryOwnerRoomRelsCount(ownerRoomRelDto);

        if (count > 0) {
            return;
        }

        RoomDto roomDto = new RoomDto();
        roomDto.setRoomId(reqJson.getString("roomId"));
        roomDto.setCommunityId(reqJson.getString("communityId"));
        List<RoomDto> roomDtos = roomInnerServiceSMOImpl.queryRooms(roomDto);
        Assert.listOnlyOne(roomDtos, "房屋或商铺不存在");


        if (RoomDto.ROOM_TYPE_SHOPS.equals(roomDtos.get(0).getRoomType())) {
            reqJson.put("state", RoomDto.STATE_SHOP_FREE);
        } else {
            reqJson.put("state", "2002");
        }


        Assert.listOnlyOne(roomDtos, "存在" + roomDtos.size() + "条房屋信息");

        businessUnit = new JSONObject();
        businessUnit.putAll(BeanConvertUtil.beanCovertMap(roomDtos.get(0)));
        businessUnit.putAll(reqJson);
        RoomPo tmpRoomPo = BeanConvertUtil.covertBean(businessUnit, RoomPo.class);
        flag = roomV1InnerServiceSMOImpl.updateRoom(tmpRoomPo);

        if (flag < 1) {
            throw new IllegalArgumentException("更新房屋状态");
        }

        //todo 人员同步门禁
        synchronousAccessControl(reqJson);

    }

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

        AccessControlFaceDto accessControlFaceDto = new AccessControlFaceDto();
        accessControlFaceDto.setComeType(AccessControlFaceDto.COME_TYPE_FLOOR);
        accessControlFaceDto.setComeId(accessControlFloorDtos.get(0).getAcfId());
        accessControlFaceDto.setPersonId(reqJson.getString("ownerId"));
        accessControlFaceDto.setCommunityId(accessControlFloorDtos.get(0).getCommunityId());
        List<AccessControlFaceDto> accessControlFaceDtos = accessControlFaceV1InnerServiceSMOImpl.queryAccessControlFaces(accessControlFaceDto);
        if(ListUtil.isNull(accessControlFaceDtos)){
            return;
        }

        AccessControlFacePo accessControlFacePo = new AccessControlFacePo();
        accessControlFacePo.setMfId(accessControlFaceDtos.get(0).getMfId());
        accessControlFacePo.setMachineId(accessControlFaceDtos.get(0).getMachineId());
        accessControlFacePo.setCommunityId(accessControlFaceDtos.get(0).getCommunityId());
       // accessControlFacePo.
        accessControlFaceV1InnerServiceSMOImpl.deleteAccessControlFace(accessControlFacePo);




    }
}
