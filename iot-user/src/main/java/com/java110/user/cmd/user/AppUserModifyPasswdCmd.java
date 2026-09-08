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
import com.java110.core.utils.StringUtil;
import com.java110.doc.annotation.*;
import com.java110.dto.user.UserDto;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.user.UserPo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 类表述：用户端修改登录密码（登录态免验证码，免原密码）
 * 服务编码：user.appUserModifyPasswd
 * 请求路劲：/iot/api/user.appUserModifyPasswd
 * add by 吴学文 at 2026-09-08 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110CmdDoc(title = "用户端修改密码",
        description = "用于用户端登录状态下修改登录密码（免原密码、免验证码，从登录态取用户）",
        httpMethod = "post",
        url = "http://{ip}:{port}/iot/api/user.appUserModifyPasswd",
        resource = "userDoc",
        author = "吴学文",
        serviceCode = "user.appUserModifyPasswd",
        seq = 5
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "newPwd", length = 20, remark = "新密码（6-20位）"),
})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
                @Java110ParamDoc(name = "data", type = "Object", remark = "有效数据"),
        }
)

@Java110ExampleDoc(
        reqBody = "http://{ip}:{port}/iot/api/user.appUserModifyPasswd",
        resBody = "{'code':0,'msg':'成功','data':{}}"
)
@Java110Cmd(serviceCode = "user.appUserModifyPasswd")
public class AppUserModifyPasswdCmd extends Cmd {

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        reqJson.put("userId", CmdContextUtils.getLoginUserId(context));

        Assert.jsonObjectHaveKey(reqJson, "userId", "用户未登录");
        Assert.jsonObjectHaveKey(reqJson, "newPwd", "新密码不能为空");

        String newPwd = reqJson.getString("newPwd");
        if (StringUtil.isEmpty(newPwd)) {
            throw new CmdException("新密码不能为空");
        }
        if (newPwd.length() < 6 || newPwd.length() > 20) {
            throw new CmdException("新密码长度需 6-20 位");
        }
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {

        UserDto userDto = new UserDto();
        userDto.setStatusCd("0");
        userDto.setUserId(reqJson.getString("userId"));
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);

        Assert.listOnlyOne(userDtos, "用户信息不存在");

        UserPo userPo = new UserPo();
        userPo.setUserId(reqJson.getString("userId"));
        userPo.setPassword(AuthenticationFactory.passwdMd5(reqJson.getString("newPwd")));
        int flag = userV1InnerServiceSMOImpl.updateUser(userPo);
        if (flag < 1) {
            throw new CmdException("修改密码失败");
        }

        context.setResponseEntity(ResultVo.success());
    }
}
