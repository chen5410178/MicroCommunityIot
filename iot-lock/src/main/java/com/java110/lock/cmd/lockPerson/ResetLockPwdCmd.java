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
package com.java110.lock.cmd.lockPerson;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.AuthenticationFactory;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.DateUtil;
import com.java110.dto.lock.LockPersonDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.lock.ILockPersonV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.lock.factory.ILockCore;
import com.java110.po.lock.LockPersonPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;


/**
 * 类表述：更新
 * 服务编码：lockPerson.updateLockPerson
 * 请求路劲：/app/lockPerson.UpdateLockPerson
 * add by 吴学文 at 2023-10-31 14:11:56 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "lockPerson.resetLockPwd")
public class ResetLockPwdCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ResetLockPwdCmd.class);


    @Autowired
    private ILockPersonV1InnerServiceSMO lockPersonV1InnerServiceSMOImpl;

    @Autowired
    private ILockCore lockCoreImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "lpId", "lpId不能为空");
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
        Assert.hasKeyAndValue(reqJson, "machineId", "machineId不能为空");
        Assert.hasKeyAndValue(reqJson, "personId", "personId不能为空");
        Assert.hasKeyAndValue(reqJson, "openModel", "openModel不能为空");
        Assert.hasKeyAndValue(reqJson, "cardNumber", "cardNumber不能为空");

        if (!LockPersonDto.OPEN_MODEL_PW.equals(reqJson.getString("openModel"))) {
            throw new CmdException("非密码开锁方式授权,不能重置密码");
        }

        LockPersonDto lockPersonDto = new LockPersonDto();
        lockPersonDto.setMachineId(reqJson.getString("machineId"));
        lockPersonDto.setPersonId(reqJson.getString("personId"));
        lockPersonDto.setOpenModel(reqJson.getString("openModel"));
        List<LockPersonDto> lockPersonDtos = lockPersonV1InnerServiceSMOImpl.queryLockPersons(lockPersonDto);
        Assert.listOnlyOne(lockPersonDtos, "该业主对应门锁未授权密码开锁方式");

        if (LockPersonDto.STATE_FREEZE.equals(lockPersonDtos.get(0).getState())) {
            throw new CmdException("授权已冻结,不能重置密码");
        }

        if (new Date().before(DateUtil.getDateFromStringA(lockPersonDtos.get(0).getStartTime())) || new Date().after(DateUtil.getDateFromStringA(lockPersonDtos.get(0).getEndTime()))) {
            throw new CmdException("不在授权有效期内,不能重置密码");
        }
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        LockPersonPo lockPersonPo = BeanConvertUtil.covertBean(reqJson, LockPersonPo.class);
        int flag = lockPersonV1InnerServiceSMOImpl.updateLockPerson(lockPersonPo);

        lockPersonPo.setOprUserId(cmdDataFlowContext.getReqHeaders().get("user_id"));
        UserDto userDto = new UserDto();
        userDto.setUserId(cmdDataFlowContext.getReqHeaders().get("user_id"));
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        lockPersonPo.setOprUserName(userDtos.get(0).getName());
        lockCoreImpl.updatePasswordToCloud(lockPersonPo);

        if (flag < 1) {
            throw new CmdException("更新数据失败");
        }

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
