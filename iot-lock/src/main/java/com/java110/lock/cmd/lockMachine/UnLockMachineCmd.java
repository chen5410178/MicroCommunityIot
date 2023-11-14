/*
 * Copyright 2017-2020 吴学文 and java110 team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.java110.lock.cmd.lockMachine;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.AuthenticationFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.lock.LockMachineDto;
import com.java110.dto.lock.LockPersonDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.lock.ILockMachineV1InnerServiceSMO;
import com.java110.intf.lock.ILockPersonV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.lock.factory.ILockCore;
import com.java110.po.lock.LockMachinePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 类表述：删除
 * 服务编码：lockMachine.deleteLockMachine
 * 请求路劲：/app/lockMachine.DeleteLockMachine
 * add by 吴学文 at 2023-10-30 17:23:38 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "lockMachine.unLockMachine")
public class UnLockMachineCmd extends Cmd {
    private static Logger logger = LoggerFactory.getLogger(UnLockMachineCmd.class);

    @Autowired
    private ILockMachineV1InnerServiceSMO lockMachineV1InnerServiceSMOImpl;

    @Autowired
    private ILockPersonV1InnerServiceSMO lockPersonV1InnerServiceSMOImpl;

    @Autowired
    private ILockCore lockCoreImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "machineId", "machineId不能为空");
        Assert.hasKeyAndValue(reqJson, "machineCode", "machineCode不能为空");
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");

    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        LockMachinePo lockMachinePo = BeanConvertUtil.covertBean(reqJson, LockMachinePo.class);
        lockMachinePo.setOprUserId(cmdDataFlowContext.getReqHeaders().get("user_id"));
        UserDto userDto = new UserDto();
        userDto.setUserId(cmdDataFlowContext.getReqHeaders().get("user_id"));
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        lockMachinePo.setOprUserName(userDtos.get(0).getName());
        lockMachinePo.setTel(userDtos.get(0).getTel());
        lockCoreImpl.unlock(lockMachinePo);
        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
