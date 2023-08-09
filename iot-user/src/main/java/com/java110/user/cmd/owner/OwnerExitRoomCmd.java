package com.java110.user.cmd.owner;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.owner.OwnerRoomRelDto;
import com.java110.bean.dto.room.RoomDto;
import com.java110.bean.po.owner.OwnerRoomRelPo;
import com.java110.bean.po.room.RoomPo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.Environment;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.intf.community.IRoomV1InnerServiceSMO;
import com.java110.intf.user.IOwnerRoomRelV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Java110Cmd(serviceCode = "owner.ownerExitRoom")
public class OwnerExitRoomCmd extends Cmd {



    @Autowired
    private IRoomV1InnerServiceSMO roomV1InnerServiceSMOImpl;


    @Autowired
    private IOwnerRoomRelV1InnerServiceSMO ownerRoomRelV1InnerServiceSMOImpl;


    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {
        Environment.isDevEnv();
        Assert.jsonObjectHaveKey(reqJson, "ownerId", "请求报文中未包含业主");

        JSONArray selectRooms = reqJson.getJSONArray("selectRooms");

        if(selectRooms == null || selectRooms.size() < 1){
            throw new CmdException("未选择房屋");
        }


    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        JSONArray rooms = reqJson.getJSONArray("selectRooms");
        OwnerRoomRelPo ownerRoomRelPo = null;
        int flag = 0;
        OwnerRoomRelDto ownerRoomRelDto = null;
        List<OwnerRoomRelDto> ownerRoomRelDtos = null;
        for (int roomIndex = 0; roomIndex < rooms.size(); roomIndex++) {
            ownerRoomRelDto = new OwnerRoomRelDto();
            ownerRoomRelDto.setOwnerId(reqJson.getString("ownerId"));
            ownerRoomRelDto.setRoomId(rooms.getString(roomIndex));
            ownerRoomRelDtos = ownerRoomRelV1InnerServiceSMOImpl.queryOwnerRoomRels(ownerRoomRelDto);
            if(ownerRoomRelDtos != null && ownerRoomRelDtos.size()>0){
                for(OwnerRoomRelDto tmpOwnerRoomRelDto : ownerRoomRelDtos) {
                    ownerRoomRelPo = new OwnerRoomRelPo();
                    ownerRoomRelPo.setRelId(tmpOwnerRoomRelDto.getRelId());
                    flag = ownerRoomRelV1InnerServiceSMOImpl.deleteOwnerRoomRel(ownerRoomRelPo);
                    if (flag < 1) {
                        throw new CmdException("删除业主房屋失败");
                    }
                }
            }

            RoomPo roomPo = new RoomPo();
            roomPo.setRoomId(rooms.getString(roomIndex));
            roomPo.setCommunityId(reqJson.getString("communityId"));
            roomPo.setState(RoomDto.STATE_FREE);
            flag = roomV1InnerServiceSMOImpl.updateRoom(roomPo);
            if (flag < 1) {
                throw new CmdException("操作业主失败");
            }
        }
    }
}
