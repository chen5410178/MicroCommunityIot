package com.java110.user.cmd.owner;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.CmdContextUtils;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.appUser.AppUserDto;
import com.java110.dto.userAttr.UserAttrDto;
import com.java110.intf.user.IAppUserV1InnerServiceSMO;
import com.java110.intf.user.IUserAttrV1InnerServiceSMO;
import com.java110.po.appUser.AppUserPo;
import com.java110.po.userAttr.UserAttrPo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Java110Cmd(serviceCode = "owner.refreshAppUserBindingOwnerOpenId")
public class RefreshAppUserBindingOwnerOpenIdCmd extends Cmd {

    @Autowired
    private IAppUserV1InnerServiceSMO appUserV1InnerServiceSMOImpl;

    @Autowired
    private IUserAttrV1InnerServiceSMO userAttrV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "openId", "必填，请填写状态");
        Assert.hasKeyAndValue(reqJson, "communityId", "必填，请填写小区ID");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        String userId = CmdContextUtils.getUserId(cmdDataFlowContext);

        if (StringUtil.isEmpty(userId)) {
            throw new CmdException("用户未登录");
        }

        //todo 刷user_attr 中的openId
        freshUserToken(reqJson, userId);

        AppUserDto ownerAppUserDto = new AppUserDto();
        ownerAppUserDto.setUserId(userId);
        ownerAppUserDto.setCommunityId(reqJson.getString("communityId"));
        List<AppUserDto> ownerAppUserDtos = appUserV1InnerServiceSMOImpl.queryAppUsers(ownerAppUserDto);

        if (ListUtil.isNull(ownerAppUserDtos)) {
            return;
        }
        AppUserPo appUserPo = new AppUserPo();
        appUserPo.setAuId(ownerAppUserDtos.get(0).getAuId());
        appUserPo.setCommunityId(reqJson.getString("communityId"));
        appUserPo.setOpenId(reqJson.getString("openId"));
        appUserV1InnerServiceSMOImpl.updateAppUser(appUserPo);
    }

    private void freshUserToken(JSONObject reqJson, String userId) {
        //判断 用户下是否有openId 属性有修改没有添加
        if (!reqJson.containsKey("openId")) {
            return;
        }

        UserAttrDto userAttrDto = new UserAttrDto();
        userAttrDto.setUserId(userId);
        userAttrDto.setSpecCd(UserAttrDto.SPEC_OPEN_ID);
        List<UserAttrDto> userAttrDtos = userAttrV1InnerServiceSMOImpl.queryUserAttrs(userAttrDto);
        if (ListUtil.isNull(userAttrDtos)) {
            UserAttrPo userAttrPo = new UserAttrPo();
            userAttrPo.setUserId(userId);
            userAttrPo.setAttrId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_attrId));
            userAttrPo.setSpecCd(UserAttrDto.SPEC_OPEN_ID);
            userAttrPo.setValue(reqJson.getString("openId"));
            userAttrV1InnerServiceSMOImpl.saveUserAttr(userAttrPo);
            return;
        }
        UserAttrPo userAttrPo = new UserAttrPo();
        userAttrPo.setUserId(userId);
        userAttrPo.setAttrId(userAttrDtos.get(0).getAttrId());
        userAttrPo.setSpecCd(UserAttrDto.SPEC_OPEN_ID);
        userAttrPo.setValue(reqJson.getString("openId"));
        userAttrV1InnerServiceSMOImpl.updateUserAttr(userAttrPo);

        if (!reqJson.containsKey("unionId")) {
            return;
        }

        userAttrDto = new UserAttrDto();
        userAttrDto.setUserId(userId);
        userAttrDto.setSpecCd(UserAttrDto.SPEC_UNION_ID);
        userAttrDtos = userAttrV1InnerServiceSMOImpl.queryUserAttrs(userAttrDto);
        if (ListUtil.isNull(userAttrDtos)) {
            userAttrPo = new UserAttrPo();
            userAttrPo.setUserId(userId);
            userAttrPo.setAttrId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_attrId));
            userAttrPo.setSpecCd(UserAttrDto.SPEC_UNION_ID);
            userAttrPo.setValue(reqJson.getString("unionId"));
            userAttrV1InnerServiceSMOImpl.saveUserAttr(userAttrPo);
            return;
        }
        userAttrPo = new UserAttrPo();
        userAttrPo.setUserId(userId);
        userAttrPo.setAttrId(userAttrDtos.get(0).getAttrId());
        userAttrPo.setSpecCd(UserAttrDto.SPEC_UNION_ID);
        userAttrPo.setValue(reqJson.getString("unionId"));
        userAttrV1InnerServiceSMOImpl.updateUserAttr(userAttrPo);
    }
}
