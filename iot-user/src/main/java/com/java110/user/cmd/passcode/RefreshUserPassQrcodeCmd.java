package com.java110.user.cmd.passcode;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.CmdContextUtils;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.appUser.AppUserDto;
import com.java110.dto.passQrcode.PassQrcodeDto;
import com.java110.dto.passQrcodeSetting.PassQrcodeSettingDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.user.IAppUserV1InnerServiceSMO;
import com.java110.intf.user.IPassQrcodeSettingV1InnerServiceSMO;
import com.java110.intf.user.IPassQrcodeV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.passQrcode.PassQrcodePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Java110Cmd(serviceCode = "passcode.refreshUserPassQrcode")
public class RefreshUserPassQrcodeCmd extends Cmd {

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IPassQrcodeV1InnerServiceSMO passQrcodeV1InnerServiceSMOImpl;

    @Autowired
    private IPassQrcodeSettingV1InnerServiceSMO passQrcodeSettingV1InnerServiceSMOImpl;

    @Autowired
    private IAppUserV1InnerServiceSMO appUserV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区ID");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        String userId = CmdContextUtils.getUserId(context);

        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);

        AppUserDto appUserDto = new AppUserDto();
        appUserDto.setUserId(userId);
        List<AppUserDto> appUserDtos = appUserV1InnerServiceSMOImpl.queryAppUsers(appUserDto);

        if(ListUtil.isNull(appUserDtos)){
            throw new CmdException("房屋未认证");
        }


        Assert.listOnlyOne(userDtos, "用户未登录");
        PassQrcodeDto passQrcodeDto = new PassQrcodeDto();
        passQrcodeDto.setPersonTel(appUserDtos.get(0).getLink());
        passQrcodeDto.setCommunityId(reqJson.getString("communityId"));
        passQrcodeDto.setPage(1);
        passQrcodeDto.setRow(1);
        passQrcodeDto.setStates(new String[]{"W", "C"});

        int count = passQrcodeV1InnerServiceSMOImpl.queryPassQrcodesCount(passQrcodeDto);

        List<PassQrcodeDto> passQrcodeDtos = null;

        if (count > 0) {
            passQrcodeDtos = passQrcodeV1InnerServiceSMOImpl.queryPassQrcodes(passQrcodeDto);
            refreshQrcode(passQrcodeDtos);
        } else {
            passQrcodeDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo(1, count, passQrcodeDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }

    private void refreshQrcode(List<PassQrcodeDto> passQrcodeDtos) {

        if (ListUtil.isNull(passQrcodeDtos)) {
            return;
        }

        PassQrcodePo passQrcodePo = new PassQrcodePo();
        passQrcodePo.setPqId(passQrcodeDtos.get(0).getPqId());
        passQrcodePo.setQrcodeTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        passQrcodePo.setQrcode(GenerateCodeFactory.getUUID());
        passQrcodeV1InnerServiceSMOImpl.updatePassQrcode(passQrcodePo);

        passQrcodeDtos.get(0).setQrcode(passQrcodePo.getQrcode());
        passQrcodeDtos.get(0).setQrcodeTime(passQrcodePo.getQrcodeTime());

        PassQrcodeSettingDto passQrcodeSettingDto = new PassQrcodeSettingDto();
        passQrcodeSettingDto.setCommunityId(passQrcodeDtos.get(0).getCommunityId());
        List<PassQrcodeSettingDto> passQrcodeSettingDtos = passQrcodeSettingV1InnerServiceSMOImpl.queryPassQrcodeSettings(passQrcodeSettingDto);

        int min = 120;
        if (!ListUtil.isNull(passQrcodeSettingDtos)) {
            min = Integer.parseInt(passQrcodeSettingDtos.get(0).getExpired());
        }


        passQrcodeDtos.get(0).setExpire(DateUtil.getAddMinStringA(DateUtil.getCurrentDate(), min));


    }
}
