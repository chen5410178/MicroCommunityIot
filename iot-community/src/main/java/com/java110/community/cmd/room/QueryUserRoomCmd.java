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
import com.java110.core.utils.CmdContextUtils;
import com.java110.core.utils.ListUtil;
import com.java110.dto.appUser.AppUserDto;
import com.java110.intf.community.IRoomInnerServiceSMO;
import com.java110.intf.user.IAppUserV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

/**
 * 查询用户房屋接口
 */
@Java110Cmd(serviceCode = "room.queryUserRoom")
public class QueryUserRoomCmd extends Cmd {

    @Autowired
    private IAppUserV1InnerServiceSMO appUserV1InnerServiceSMOImpl;

    @Autowired
    private IRoomInnerServiceSMO roomInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson,"communityId","未包含小区信息");
        String userId = CmdContextUtils.getUserId(context);


        AppUserDto appUserDto = new AppUserDto();
        appUserDto.setUserId(userId);
        appUserDto.setCommunityId(reqJson.getString("communityId"));
        List<AppUserDto> appUserDtos = appUserV1InnerServiceSMOImpl.queryAppUsers(appUserDto);

        if (ListUtil.isNull(appUserDtos)) {
            throw new CmdException("业主未认证");
        }

        reqJson.put("memberId",appUserDtos.get(0).getMemberId());
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        RoomDto roomDto = new RoomDto();
        roomDto.setOwnerId(reqJson.getString("memberId"));
        roomDto.setCommunityId(reqJson.getString("communityId"));

        List<RoomDto> roomDtoList = roomInnerServiceSMOImpl.queryRoomsByOwner(roomDto);


        context.setResponseEntity(ResultVo.createResponseEntity(roomDtoList));
    }
}
