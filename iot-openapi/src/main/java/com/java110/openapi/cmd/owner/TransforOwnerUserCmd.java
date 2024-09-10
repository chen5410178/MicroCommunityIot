package com.java110.openapi.cmd.owner;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.AuthenticationFactory;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.appUser.AppUserDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.user.IAppUserV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.appUser.AppUserPo;
import com.java110.po.user.UserPo;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

@Java110Cmd(serviceCode = "owner.transforOwnerUser")
public class TransforOwnerUserCmd extends Cmd {

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IAppUserV1InnerServiceSMO appUserV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson,"tel","未包含手机号");
        Assert.hasKeyAndValue(reqJson,"userName","未包含用户名称");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        UserDto userDto = new UserDto();
        userDto.setTel(reqJson.getString("tel"));
        userDto.setLevelCd(UserDto.LEVEL_CD_PHONE);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);

        if (ListUtil.isNull(userDtos)) {
            userDtos = registerUser(reqJson);
        }
        context.setResponseEntity(ResultVo.success());
        if(!reqJson.containsKey("appUsers")){
            return;
        }


        JSONArray appUsers = reqJson.getJSONArray("appUsers");
        if(ListUtil.isNull(appUsers)){
            return ;
        }

        /**
         * appUser.put("ownerId",tmpOwnerAppUserDto.getMemberId());
         * appUser.put("ownerName",tmpOwnerAppUserDto.getAppUserName());
         * appUser.put("roomId",tmpOwnerAppUserDto.getRoomId());
         * appUser.put("roomName",tmpOwnerAppUserDto.getRoomName());
         * appUser.put("communityId",tmpOwnerAppUserDto.getCommunityId());
         * appUser.put("communityName",tmpOwnerAppUserDto.getCommunityName());
         */
        JSONObject appUser = null;
        List<AppUserDto> appUserDtos = null;
        for(int userIndex = 0; userIndex < appUsers.size();userIndex++){
            appUser = appUsers.getJSONObject(userIndex);
            AppUserDto appUserDto = new AppUserDto();
            appUserDto.setCommunityId(appUser.getString("communityId"));
            appUserDto.setRoomId(appUser.getString("roomId"));
            appUserDto.setUserId(userDtos.get(0).getUserId());
            appUserDtos = appUserV1InnerServiceSMOImpl.queryAppUsers(appUserDto);

            if(!ListUtil.isNull(appUserDtos)){
                continue;
            }
            AppUserPo appUserPo = new AppUserPo();
            appUserPo.setStateMsg("成功");
            appUserPo.setAuId(GenerateCodeFactory.getGeneratorId("11"));
            appUserPo.setOpenId("");
            appUserPo.setLink(userDtos.get(0).getTel());
            appUserPo.setRemark("物业系统同步");
            appUserPo.setUserId(userDtos.get(0).getUserId());
            appUserPo.setRoomId(appUser.getString("roomId"));
            appUserPo.setRoomName(appUser.getString("roomName"));
            appUserPo.setOwnerName(appUser.getString("ownerName"));
            appUserPo.setCommunityName(appUser.getString("communityName"));
            appUserPo.setState(AppUserDto.STATE_S);
            appUserPo.setCommunityId(appUser.getString("communityId"));
            appUserPo.setMemberId(appUser.getString("ownerId"));
            appUserV1InnerServiceSMOImpl.saveAppUser(appUserPo);
        }

    }

    private List<UserDto> registerUser(JSONObject reqJson) {

        String userPassword = reqJson.getString("passwd");
        if(StringUtil.isEmpty(userPassword)){
            userPassword = AuthenticationFactory.passwdMd5(reqJson.getString("tel"));
        }
        String address = "无";
        if(reqJson.containsKey("address")){
            address = reqJson.getString("address");
        }

        //todo 注册用户
        UserPo userPo = new UserPo();
        userPo.setAddress(address);
        userPo.setUserId(GenerateCodeFactory.getUserId());
        userPo.setLevelCd(UserDto.LEVEL_CD_PHONE);
        userPo.setName(reqJson.getString("userName"));
        userPo.setTel(reqJson.getString("tel"));
        userPo.setPassword(userPassword);

        int flag = userV1InnerServiceSMOImpl.saveUser(userPo);
        if (flag < 1) {
            throw new CmdException("注册失败");
        }

        UserDto userDto = new UserDto();
        userDto.setTel(reqJson.getString("tel"));
        userDto.setLevelCd(UserDto.LEVEL_CD_PHONE);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        return userDtos;
    }
}
