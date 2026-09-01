package com.java110.user.cmd.user;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.AuthenticationFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.CmdContextUtils;
import com.java110.dto.user.UserDto;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.user.UserPo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 用户修改登录密码（需登录，校验原密码后更新）
 */
@Java110Cmd(serviceCode = "user.updateUserPwd")
public class UpdateUserPwdCmd extends Cmd {

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        reqJson.put("userId", CmdContextUtils.getLoginUserId(context));

        Assert.jsonObjectHaveKey(reqJson, "userId", "请求参数中未包含userId 节点，请确认");
        Assert.jsonObjectHaveKey(reqJson, "oldPwd", "原始密码不能为空");
        Assert.jsonObjectHaveKey(reqJson, "newPwd", "新密码不能为空");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        String oldPwd = AuthenticationFactory.passwdMd5(reqJson.getString("oldPwd"));
        String newPwd = AuthenticationFactory.passwdMd5(reqJson.getString("newPwd"));

        UserDto userDto = new UserDto();
        userDto.setStatusCd("0");
        userDto.setUserId(reqJson.getString("userId"));
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);

        Assert.listOnlyOne(userDtos, "用户信息不存在");

        if (!oldPwd.equals(userDtos.get(0).getPassword())) {
            throw new CmdException("原始密码错误");
        }

        UserPo userPo = new UserPo();
        userPo.setUserId(reqJson.getString("userId"));
        userPo.setPassword(newPwd);
        int flag = userV1InnerServiceSMOImpl.updateUser(userPo);
        if (flag < 1) {
            throw new CmdException("修改密码失败");
        }

        context.setResponseEntity(ResultVo.success());
    }
}
