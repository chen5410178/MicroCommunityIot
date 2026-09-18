package com.java110.community.cmd.room;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.room.RoomDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.doc.annotation.*;
import com.java110.intf.community.IRoomsTreeInnerServiceSMO;
import com.java110.vo.ApiRoomDataVo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Java110CmdDoc(title = "查询房屋树",
        description = "用于查询单元下房屋树信息功能",
        httpMethod = "get",
        url = "http://{ip}:{port}/iot/api/room.queryRoomsTree",
        resource = "communityDoc",
        author = "吴学文",
        serviceCode = "room.queryRoomsTree"
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "communityId", length = 30, remark = "小区ID"),
        @Java110ParamDoc(name = "unitId", length = 30, remark = "单元ID"),
        @Java110ParamDoc(name = "page", type = "int", length = 11, remark = "页数"),
        @Java110ParamDoc(name = "row", type = "int", length = 11, remark = "行数"),
})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
                @Java110ParamDoc(name = "records", type = "int", length = 11, defaultValue = "1", remark = "总页数"),
                @Java110ParamDoc(name = "total", type = "int", length = 11, defaultValue = "1", remark = "总记录数"),
                @Java110ParamDoc(name = "data", type = "Array", remark = "有效数据"),
                @Java110ParamDoc(parentNodeName = "data", name = "roomId", type = "String", length = 30, remark = "房屋ID"),
                @Java110ParamDoc(parentNodeName = "data", name = "roomNum", type = "String", length = 30, remark = "房屋编号"),
                @Java110ParamDoc(parentNodeName = "data", name = "unitId", type = "String", length = 30, remark = "单元ID"),
                @Java110ParamDoc(parentNodeName = "data", name = "unitNum", type = "String", length = 30, remark = "单元编号"),
                @Java110ParamDoc(parentNodeName = "data", name = "floorId", type = "String", length = 30, remark = "楼栋ID"),
                @Java110ParamDoc(parentNodeName = "data", name = "floorNum", type = "String", length = 30, remark = "楼栋编号"),
                @Java110ParamDoc(parentNodeName = "data", name = "layer", type = "String", length = 30, remark = "层数"),
                @Java110ParamDoc(parentNodeName = "data", name = "ownerId", type = "String", length = 30, remark = "业主ID"),
                @Java110ParamDoc(parentNodeName = "data", name = "ownerName", type = "String", length = 250, remark = "业主名称"),
                @Java110ParamDoc(parentNodeName = "data", name = "link", type = "String", length = 30, remark = "联系电话"),
        }
)

@Java110ExampleDoc(
        reqBody = "http://{ip}:{port}/iot/api/room.queryRoomsTree?communityId=123123&unitId=123123&page=1&row=10",
        resBody = "{'code':0,'msg':'成功','records':1,'total':1,'data':[{'roomId':'123123','roomNum':'101','unitId':'123123','unitNum':'1单元','floorId':'123123','floorNum':'3栋','layer':'1','ownerId':'123123','ownerName':'张三','link':'13500000000'}]}"
)

@Java110Cmd(serviceCode = "room.queryRoomsTree")
public class QueryRoomsTreeCmd extends Cmd {
    @Autowired
    private IRoomsTreeInnerServiceSMO roomsTreeInnerServiceSMOImpl;


    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.jsonObjectHaveKey(reqJson, "communityId", "请求中未包含communityId信息");
        Assert.jsonObjectHaveKey(reqJson, "unitId", "请求中未包含单元信息");
        Assert.jsonObjectHaveKey(reqJson, "page", "请求报文中未包含page节点");
        Assert.jsonObjectHaveKey(reqJson, "row", "请求报文中未包含row节点");

        Assert.isInteger(reqJson.getString("page"), "page不是数字");
        Assert.isInteger(reqJson.getString("row"), "row不是数字");
        Assert.hasLength(reqJson.getString("communityId"), "小区ID不能为空");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {
        RoomDto roomDto = BeanConvertUtil.covertBean(reqJson, RoomDto.class);

        List<RoomDto> roomDtos = roomsTreeInnerServiceSMOImpl.queryRoomsTree(roomDto);

        int total = 0;
        if (!ListUtil.isNull(roomDtos)) {
            roomDtos = deduplication(roomDtos);
            total = roomDtos.size();
        }

        List<ApiRoomDataVo> rooms = BeanConvertUtil.covertBeanList(
                ListUtil.isNull(roomDtos) ? new ArrayList<RoomDto>() : roomDtos, ApiRoomDataVo.class);

        int row = reqJson.getIntValue("row");
        if (row < 1) {
            row = total > 0 ? total : 1;
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) total / (double) row), total, rooms);
        cmdDataFlowContext.setResponseEntity(ResultVo.createResponseEntity(resultVo));
    }

    private List<RoomDto> deduplication(List<RoomDto> roomDtos) {
        List<RoomDto> newRoomDtos = new ArrayList<>();
        for(RoomDto roomDto : roomDtos){
            if(!hasInNewRoomDtos(newRoomDtos,roomDto)){
                newRoomDtos.add(roomDto);
            }
        }

        return newRoomDtos;
    }

    private boolean hasInNewRoomDtos(List<RoomDto> newRoomDtos, RoomDto roomDto) {
        if(ListUtil.isNull(newRoomDtos)){
            return false;
        }

        for(RoomDto newRoomDto : newRoomDtos){
            if(newRoomDto.getRoomId().equals(roomDto.getRoomId())){
                return true;
            }
        }

        return false;
    }


}
