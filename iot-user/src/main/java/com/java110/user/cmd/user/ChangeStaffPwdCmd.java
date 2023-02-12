package com.java110.user.cmd.user;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.Environment;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.AuthenticationFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.CmdContextUtils;
import com.java110.core.utils.StringUtil;
import com.java110.dto.user.UserDto;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.user.UserPo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Java110Cmd(serviceCode = "user.changeStaffPwd")
public class ChangeStaffPwdCmd extends Cmd {
    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Environment.isDevEnv();

        if (reqJson.containsKey("userId") || StringUtil.isEmpty(reqJson.getString("userId"))) {
            reqJson.put("userId", CmdContextUtils.getLoginUserId(context));
        }

        Assert.jsonObjectHaveKey(reqJson, "userId", "请求参数中未包含userId 节点，请确认");
        Assert.jsonObjectHaveKey(reqJson, "oldPwd", "请求参数中未包含oldPwd 节点，请确认");
        Assert.jsonObjectHaveKey(reqJson, "newPwd", "请求参数中未包含newPwd 节点，请确认");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {


        reqJson.put("oldPwd", AuthenticationFactory.passwdMd5(reqJson.getString("oldPwd")));
        reqJson.put("newPwd", AuthenticationFactory.passwdMd5(reqJson.getString("newPwd")));

        modifyStaff(reqJson);
    }

    public void modifyStaff(JSONObject paramObj) {
        //校验json 格式中是否包含 name,email,levelCd,tel
        UserPo userPo = BeanConvertUtil.covertBean(builderStaffInfo(paramObj), UserPo.class);
        int flag = userV1InnerServiceSMOImpl.updateUser(userPo);
        if (flag < 1) {
            throw new CmdException("修改密码失败");
        }
    }

    /**
     * 构建员工信息
     *
     * @param paramObj
     * @return
     */
    private JSONObject builderStaffInfo(JSONObject paramObj) {

        UserDto userDto = new UserDto();
        userDto.setStatusCd("0");
        userDto.setUserId(paramObj.getString("userId"));
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);

        Assert.listOnlyOne(userDtos, "数据错误查询到多条用户信息或单条");

        JSONObject userInfo = JSONObject.parseObject(JSONObject.toJSONString(userDtos.get(0)));

        if (!paramObj.getString("oldPwd").equals(userDtos.get(0).getPassword())) {
            throw new IllegalArgumentException("原始密码错误");
        }
        userInfo.putAll(paramObj);
        userInfo.put("password", paramObj.getString("newPwd"));

        return userInfo;
    }
}
