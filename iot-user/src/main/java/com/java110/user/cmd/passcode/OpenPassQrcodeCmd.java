package com.java110.user.cmd.passcode;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.CmdContextUtils;
import com.java110.core.utils.DateUtil;
import com.java110.dto.user.UserDto;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.intf.user.IPassQrcodeV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.passQrcode.PassQrcodePo;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;


/**
 * 认证过的用户开通 通行码
 */
@Java110Cmd(serviceCode = "passcode.openPassQrcode")
public class OpenPassQrcodeCmd extends Cmd {

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;

    @Autowired
    private IPassQrcodeV1InnerServiceSMO passQrcodeV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        String userId = CmdContextUtils.getUserId(context);

        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);

        Assert.listOnlyOne(userDtos,"用户未登录");

        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setLink(userDtos.get(0).getTel());
        ownerDto.setCommunityId(reqJson.getString("communityId"));
        List<OwnerDto> ownerDtos = ownerV1InnerServiceSMOImpl.queryOwners(ownerDto);

        Assert.listOnlyOne(ownerDtos, "手机号错误");

        PassQrcodePo passQrcodePo = new PassQrcodePo();
        passQrcodePo.setPqId(GenerateCodeFactory.getGeneratorId("11"));
        passQrcodePo.setQrcode(GenerateCodeFactory.getUUID());
        passQrcodePo.setQrcodeTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        passQrcodePo.setOwnerId(ownerDtos.get(0).getMemberId());
        passQrcodePo.setPersonName(ownerDtos.get(0).getName());
        passQrcodePo.setOpenId("-1");
        passQrcodePo.setState("C");
        passQrcodePo.setCommunityId(ownerDtos.get(0).getCommunityId());
        passQrcodePo.setRemark("用户自行开通");
        int flag = passQrcodeV1InnerServiceSMOImpl.savePassQrcode(passQrcodePo);

        if (flag < 1) {
            throw new CmdException("保存数据失败");
        }

        context.setResponseEntity(ResultVo.success());

    }
}
